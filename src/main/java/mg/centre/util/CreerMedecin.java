package mg.centre.util;

import java.io.Console;

import mg.centre.dao.UtilisateurDAO;
import mg.centre.model.Utilisateur;

/**
 * Programme en ligne de commande pour creer un compte MEDECIN.
 * Usage : java -cp "build/classes:lib/*" mg.centre.util.CreerMedecin
 */
public class CreerMedecin {

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        if (console == null) {
            System.err.println("Lance ce programme dans un vrai terminal.");
            System.exit(1);
        }

        String login = console.readLine("Login du medecin : ").trim().toLowerCase();
        if (!login.matches("[a-z0-9._-]{3,50}")) {
            System.err.println("Login invalide (3 a 50 caracteres : lettres, chiffres, . _ -).");
            System.exit(1);
        }

        UtilisateurDAO dao = new UtilisateurDAO();
        if (dao.findByLogin(login) != null) {
            System.err.println("Ce login existe deja.");
            System.exit(1);
        }

        String mdp1 = new String(console.readPassword("Mot de passe : "));
        String mdp2 = new String(console.readPassword("Confirmation : "));
        if (!mdp1.equals(mdp2)) {
            System.err.println("Les deux mots de passe sont differents.");
            System.exit(1);
        }
        String probleme = MotDePasse.verifierRobustesse(mdp1);
        if (probleme != null) {
            System.err.println(probleme);
            System.exit(1);
        }

        Utilisateur u = new Utilisateur();
        u.setLogin(login);
        u.setMotDePasse(MotDePasse.hacher(mdp1));
        u.setRole(Utilisateur.MEDECIN);
        int id = dao.insert(u);
        System.out.println("Compte medecin cree (id = " + id + ").");
    }
}