package mg.centre.controller;

import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;

import mg.centre.dao.PatientDAO;
import mg.centre.dao.UtilisateurDAO;
import mg.centre.model.Patient;
import mg.centre.model.Utilisateur;
import mg.centre.qrcode.GenerateurQrCode;
import mg.centre.util.Csrf;
import mg.centre.util.MotDePasse;
import mg.centre.util.Vue;
import mg.itu.annotation.Auth;
import mg.itu.annotation.Controller;
import mg.itu.annotation.Param;
import mg.itu.annotation.UrlMapping;
import mg.itu.session.Session;
import mg.itu.view.ModelAndView;

/**
 * CRUD des patients, reserve aux medecins.
 */
@Controller
public class PatientController {

    private final PatientDAO patientDAO = new PatientDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final GenerateurQrCode generateur = new GenerateurQrCode();

    // ---------- LISTE ----------

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients", method = "GET")
    public ModelAndView liste(Session session) {
        return pageListe(session, null);
    }

    // ---------- CREATION ----------

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients/nouveau", method = "GET")
    public ModelAndView nouveau(Session session) {
        ModelAndView mv = page("patient-form", session);
        mv.addAttribut("patient", new Patient());
        return mv;
    }

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients/creer", method = "POST")
    public ModelAndView creer(@Param("nom") String nom,
                              @Param("prenom") String prenom,
                              @Param("dateNaissance") String dateNaissance,
                              @Param("sexe") String sexe,
                              @Param("telephone") String telephone,
                              @Param("adresse") String adresse,
                              @Param("groupeSanguin") String groupeSanguin,
                              @Param("allergies") String allergies,
                              Session session) {
        Patient p = construire(nom, prenom, dateNaissance, sexe, telephone, adresse, groupeSanguin, allergies);
        String erreur = verifier(p);
        if (erreur != null) {
            return formulaireAvecErreur(session, p, erreur);
        }
        try {
            int id = patientDAO.insert(p);
            return Vue.redirection("/patients/fiche?id=" + id);
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    // ---------- FICHE ----------

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients/fiche", method = "GET")
    public ModelAndView fiche(@Param("id") String id, Session session) {
        try {
            Patient p = trouver(id);
            if (p == null) {
                return Vue.erreur("Patient introuvable.");
            }
            Utilisateur compte = utilisateurDAO.findByIdPatient(p.getId());
            ModelAndView mv = page("patient-fiche", session);
            mv.addAttribut("patient", p);
            mv.addAttribut("loginCompte", (compte == null) ? null : compte.getLogin());
            mv.addAttribut("qrcodeImage", lireImageQrCode(p));
            return mv;
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    // ---------- MODIFICATION ----------

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients/modifier", method = "GET")
    public ModelAndView modifier(@Param("id") String id, Session session) {
        try {
            Patient p = trouver(id);
            if (p == null) {
                return Vue.erreur("Patient introuvable.");
            }
            ModelAndView mv = page("patient-form", session);
            mv.addAttribut("patient", p);
            return mv;
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients/maj", method = "POST")
    public ModelAndView mettreAJour(@Param("id") String id,
                                    @Param("nom") String nom,
                                    @Param("prenom") String prenom,
                                    @Param("dateNaissance") String dateNaissance,
                                    @Param("sexe") String sexe,
                                    @Param("telephone") String telephone,
                                    @Param("adresse") String adresse,
                                    @Param("groupeSanguin") String groupeSanguin,
                                    @Param("allergies") String allergies,
                                    Session session) {
        Integer idPatient = lireId(id);
        if (idPatient == null) {
            return Vue.erreur("Patient introuvable.");
        }
        Patient p = construire(nom, prenom, dateNaissance, sexe, telephone, adresse, groupeSanguin, allergies);
        p.setId(idPatient);
        String erreur = verifier(p);
        if (erreur != null) {
            return formulaireAvecErreur(session, p, erreur);
        }
        try {
            patientDAO.update(p);
            return Vue.redirection("/patients/fiche?id=" + idPatient);
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    // ---------- SUPPRESSION ----------

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients/supprimer", method = "POST")
    public ModelAndView supprimer(@Param("id") String id) {
        Integer idPatient = lireId(id);
        if (idPatient == null) {
            return Vue.erreur("Patient introuvable.");
        }
        try {
            patientDAO.delete(idPatient);
            return Vue.redirection("/patients");
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    // ---------- QR CODES ----------

    /** Bouton de la fiche : genere le QR code d'UN patient. */
    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients/qrcode", method = "POST")
    public ModelAndView genererQrCode(@Param("id") String id) {
        try {
            Patient p = trouver(id);
            if (p == null) {
                return Vue.erreur("Patient introuvable.");
            }
            generateur.generer(p);
            return Vue.redirection("/patients/fiche?id=" + p.getId());
        } catch (Exception e) {
            return Vue.erreurTechnique(e);
        }
    }

    /** Bouton de la liste : genere TOUS les QR codes manquants. */
    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients/qrcodes", method = "POST")
    public ModelAndView genererTousLesQrCodes(Session session) {
        try {
            String resume = generateur.genererManquants();
            return pageListe(session, resume);
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    // ---------- COMPTE DU PATIENT ----------

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/patients/compte", method = "POST")
    public ModelAndView creerCompte(@Param("id") String id,
                                    @Param("login") String login,
                                    @Param("motDePasse") String motDePasse) {
        Integer idPatient = lireId(id);
        if (idPatient == null) {
            return Vue.erreur("Patient introuvable.");
        }
        login = (login == null) ? "" : login.trim().toLowerCase();
        if (login.isEmpty()) {
            return Vue.erreur("Le login est obligatoire.");
        }
        if (motDePasse == null || motDePasse.length() < 8) {
            return Vue.erreur("Le mot de passe doit contenir au moins 8 caracteres.");
        }
        try {
            if (utilisateurDAO.findByLogin(login) != null) {
                return Vue.erreur("Ce login est deja utilise.");
            }
            Utilisateur u = new Utilisateur();
            u.setLogin(login);
            u.setMotDePasse(MotDePasse.hacher(motDePasse));
            u.setRole(Utilisateur.PATIENT);
            u.setIdPatient(idPatient);
            utilisateurDAO.insert(u);
            return Vue.redirection("/patients/fiche?id=" + idPatient);
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    // ---------- OUTILS ----------

    /** Ce dont toutes les pages medecin ont besoin (barre du haut). */
    private ModelAndView page(String vue, Session session) {
        ModelAndView mv = new ModelAndView(vue);
        mv.addAttribut("login", session.get("utilisateur"));
        mv.addAttribut("role", session.get("role"));
        mv.addAttribut("csrf", Csrf.jeton(session));
        return mv;
    }

    /** Page liste, avec un message facultatif (ex : resume des QR codes). */
    private ModelAndView pageListe(Session session, String message) {
        try {
            ModelAndView mv = page("patients", session);
            mv.addAttribut("patients", patientDAO.findAll());
            mv.addAttribut("message", message);
            return mv;
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    /** Lit l'image du QR code et la convertit en texte Base64 (pour l'afficher dans la page). */
    private String lireImageQrCode(Patient p) {
        if (!generateur.existe(p)) {
            return null;
        }
        try {
            byte[] octets = Files.readAllBytes(generateur.fichier(p.getId()));
            return Base64.getEncoder().encodeToString(octets);
        } catch (Exception e) {
            return null;
        }
    }

    private ModelAndView formulaireAvecErreur(Session session, Patient p, String erreur) {
        ModelAndView mv = page("patient-form", session);
        mv.addAttribut("patient", p);
        mv.addAttribut("erreur", erreur);
        return mv;
    }

    /** Fabrique un Patient a partir des champs du formulaire. */
    private Patient construire(String nom, String prenom, String dateNaissance, String sexe,
                               String telephone, String adresse, String groupeSanguin, String allergies) {
        Patient p = new Patient();
        p.setNom(vide(nom));
        p.setPrenom(vide(prenom));
        p.setSexe(vide(sexe));
        p.setTelephone(vide(telephone));
        p.setAdresse(vide(adresse));
        p.setGroupeSanguin(vide(groupeSanguin));
        p.setAllergies(vide(allergies));
        try {
            p.setDateNaissance(LocalDate.parse(dateNaissance));
        } catch (Exception e) {
            p.setDateNaissance(null);
        }
        return p;
    }

    /** @return un message d'erreur, ou null si tout est correct */
    private String verifier(Patient p) {
        if (p.getNom() == null || p.getPrenom() == null) {
            return "Le nom et le prenom sont obligatoires.";
        }
        if (p.getDateNaissance() == null) {
            return "La date de naissance est obligatoire.";
        }
        if (p.getDateNaissance().isAfter(LocalDate.now())) {
            return "La date de naissance ne peut pas etre dans le futur.";
        }
        if (!"M".equals(p.getSexe()) && !"F".equals(p.getSexe())) {
            return "Veuillez choisir le sexe.";
        }
        return null;
    }

    /** "  " ou "" devient null, sinon on retire les espaces autour. */
    private String vide(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return null;
        }
        return valeur.trim();
    }

    /** "7" devient 7 ; "abc" ou rien devient null. */
    private Integer lireId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {
            return null;
        }
    }

    private Patient trouver(String id) throws SQLException {
        Integer idPatient = lireId(id);
        return (idPatient == null) ? null : patientDAO.findById(idPatient);
    }
}