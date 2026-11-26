package mg.centre.controller;

import java.sql.SQLException;

import mg.centre.dao.PatientDAO;
import mg.centre.model.Patient;
import mg.centre.util.Csrf;
import mg.centre.util.Vue;
import mg.itu.annotation.Auth;
import mg.itu.annotation.Controller;
import mg.itu.annotation.UrlMapping;
import mg.itu.session.Session;
import mg.itu.view.ModelAndView;

/**
 * Espace du patient connecte : il ne voit que SA fiche.
 */
@Controller
public class DossierController {

    private final PatientDAO patientDAO = new PatientDAO();

    @Auth(role = "PATIENT")
    @UrlMapping(value = "/mon-dossier", method = "GET")
    public ModelAndView monDossier(Session session) {
        Integer idPatient = (Integer) session.get("idPatient");
        if (idPatient == null) {
            return Vue.erreur("Aucun dossier associe a ce compte.");
        }
        try {
            Patient p = patientDAO.findById(idPatient);
            if (p == null) {
                return Vue.erreur("Aucun dossier associe a ce compte.");
            }
            ModelAndView mv = new ModelAndView("mon-dossier");
            mv.addAttribut("login", session.get("utilisateur"));
            mv.addAttribut("role", session.get("role"));
            mv.addAttribut("csrf", Csrf.jeton(session));
            mv.addAttribut("patient", p);
            return mv;
        } catch (SQLException e) {
            return Vue.erreurTechnique(e);
        }
    }
}