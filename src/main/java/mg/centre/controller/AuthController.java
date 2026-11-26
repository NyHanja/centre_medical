package mg.centre.controller;

import java.sql.SQLException;

import mg.centre.dao.UtilisateurDAO;
import mg.centre.model.Utilisateur;
import mg.centre.util.AntiBruteForce;
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
 * Connexion / deconnexion.
 */
@Controller
public class AuthController {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @UrlMapping(value = "/login", method = "GET")
    public ModelAndView formulaire(Session session) {
        if (session.get("utilisateur") != null) {
            return Vue.redirection("/accueil");
        }
        return pageLogin(session, null, "");
    }

    @UrlMapping(value = "/login", method = "POST")
    public ModelAndView connexion(@Param("login") String login,
                                  @Param("motDePasse") String motDePasse,
                                  @Param("csrf") String csrf,
                                  Session session) {
        login = (login == null) ? "" : login.trim().toLowerCase();

        if (!Csrf.valide(session, csrf)) {
            return pageLogin(session, "Votre session a expire. Veuillez reessayer.", login);
        }

        long minutes = AntiBruteForce.minutesRestantes(login);
        if (minutes > 0) {
            return pageLogin(session, "Trop de tentatives. Reessayez dans " + minutes + " minute(s).", login);
        }

        try {
            Utilisateur u = utilisateurDAO.findByLogin(login);
            String hash = (u != null) ? u.getMotDePasse() : MotDePasse.HASH_LEURRE;
            boolean motDePasseOk = MotDePasse.verifier(motDePasse, hash);

            if (u == null || !motDePasseOk || !u.isActif()) {
                AntiBruteForce.echec(login);
                return pageLogin(session, "Login ou mot de passe incorrect.", login);
            }

            AntiBruteForce.reussite(login);
            session.set("utilisateur", u.getLogin());
            session.set("role", u.getRole());
            session.set("idUtilisateur", u.getId());
            if (u.getIdPatient() != null) {
                session.set("idPatient", u.getIdPatient());
            }
            Csrf.renouveler(session);
            return Vue.redirection("/accueil");

        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }

    @Auth
    @UrlMapping(value = "/logout", method = "POST")
    public ModelAndView deconnexion(@Param("csrf") String csrf, Session session) {
        if (Csrf.valide(session, csrf)) {
            session.invalider();
        }
        return Vue.redirection("/login");
    }

    private ModelAndView pageLogin(Session session, String erreur, String login) {
        ModelAndView mv = new ModelAndView("login");
        mv.addAttribut("csrf", Csrf.jeton(session));
        mv.addAttribut("erreur", erreur);
        mv.addAttribut("login", login);
        return mv;
    }
}