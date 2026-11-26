package mg.centre.controller;

import mg.centre.util.Csrf;
import mg.centre.util.Vue;
import mg.itu.annotation.Auth;
import mg.itu.annotation.Controller;
import mg.itu.annotation.UrlMapping;
import mg.itu.session.Session;
import mg.itu.view.ModelAndView;

/**
 * Page racine et page d'accueil apres connexion.
 */
@Controller
public class AccueilController {

    @UrlMapping(value = "/", method = "GET")
    public ModelAndView racine(Session session) {
        if (session.get("utilisateur") == null) {
            return Vue.redirection("/login");
        }
        return Vue.redirection("/accueil");
    }

    @Auth
    @UrlMapping(value = "/accueil", method = "GET")
    public ModelAndView accueil(Session session) {
        ModelAndView mv = new ModelAndView("accueil");
        mv.addAttribut("login", session.get("utilisateur"));
        mv.addAttribut("role", session.get("role"));
        mv.addAttribut("csrf", Csrf.jeton(session));
        return mv;
    }
}