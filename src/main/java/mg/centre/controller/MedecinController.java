package mg.centre.controller;

import java.sql.SQLException;

import mg.centre.dao.UtilisateurDAO;
import mg.centre.model.Utilisateur;
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
 * Gestion des comptes medecins depuis le site : un medecin connecte peut en creer d'autres.
 */
@Controller
public class MedecinController {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/medecins", method = "GET")
    public ModelAndView liste(Session session) {
        return page(session, null, null);
    }

    @Auth(role = "MEDECIN")
    @UrlMapping(value = "/medecins/creer", method = "POST")
    public ModelAndView creer(@Param("login") String login,
                              @Param("motDePasse") String motDePasse,
                              @Param("confirmation") String confirmation,
                              Session session) {
        login = (login == null) ? "" : login.trim().toLowerCase();

        if (login.isEmpty()) {
            return page(session, null, "Le login est obligatoire.");
        }
        if (motDePasse == null || motDePasse.length() < 8) {
            return page(session, null, "Le mot de passe doit contenir au moins 8 caracteres.");
        }
        if (!motDePasse.equals(confirmation)) {
            return page(session, null, "Les deux mots de passe sont differents.");
        }
        try {
            if (utilisateurDAO.findByLogin(login) != null) {
                return page(session, null, "Ce login est deja utilise.");
            }
            Utilisateur u = new Utilisateur();
            u.setLogin(login);
            u.setMotDePasse(MotDePasse.hacher(motDePasse));
            u.setRole(Utilisateur.MEDECIN);
            utilisateurDAO.insert(u);
            return page(session, "Compte medecin cree : " + login, null);
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    private ModelAndView page(Session session, String message, String erreur) {
        try {
            ModelAndView mv = new ModelAndView("medecins");
            mv.addAttribut("login", session.get("utilisateur"));
            mv.addAttribut("role", session.get("role"));
            mv.addAttribut("csrf", Csrf.jeton(session));
            mv.addAttribut("medecins", utilisateurDAO.findMedecins());
            mv.addAttribut("message", message);
            mv.addAttribut("erreur", erreur);
            return mv;
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }
}