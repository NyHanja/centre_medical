package mg.centre.util;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import mg.itu.view.ModelAndView;

/**
 * Petites fabriques de ModelAndView utilisees par tous les controleurs.
 */
public final class Vue {

    private static final Logger LOG = Logger.getLogger(Vue.class.getName());

    private Vue() {
    }

    /** Redirige le navigateur vers un chemin INTERNE de l'application (ex : "/patients"). */
    public static ModelAndView redirection(String chemin) {
        if (chemin == null || !chemin.startsWith("/") || chemin.startsWith("//")) {
            chemin = "/";
        }
        ModelAndView mv = new ModelAndView("redirect");
        mv.addAttribut("cible", chemin);
        return mv;
    }

    /** Page d'erreur avec un message destine a l'utilisateur. */
    public static ModelAndView erreur(String message) {
        ModelAndView mv = new ModelAndView("erreur");
        mv.addAttribut("message", message);
        return mv;
    }

    /** Erreur technique : le detail va dans les logs, l'utilisateur ne voit qu'une reference. */
    public static ModelAndView erreurTechnique(Exception e) {
        String reference = UUID.randomUUID().toString().substring(0, 8);
        LOG.log(Level.SEVERE, "Erreur technique [ref " + reference + "]", e);
        return erreur("Une erreur technique est survenue (reference : " + reference + ").");
    }
}