package mg.centre.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import mg.itu.session.Session;

/**
 * Jeton anti-CSRF : un secret aleatoire stocke en session et recopie dans chaque formulaire POST.
 */
public final class Csrf {

    private static final String CLE = "csrf";
    private static final SecureRandom ALEA = new SecureRandom();

    private Csrf() {
    }

    /** Renvoie le jeton de la session (et le cree s'il n'existe pas encore). */
    public static String jeton(Session session) {
        Object jeton = session.get(CLE);
        if (jeton == null) {
            jeton = nouveau();
            session.set(CLE, jeton);
        }
        return (String) jeton;
    }

    /** A appeler apres le login : l'ancien jeton ne vaut plus rien. */
    public static void renouveler(Session session) {
        session.set(CLE, nouveau());
    }

    public static boolean valide(Session session, String recu) {
        Object attendu = session.get(CLE);
        if (attendu == null || recu == null) {
            return false;
        }
        return MessageDigest.isEqual(
                attendu.toString().getBytes(StandardCharsets.UTF_8),
                recu.getBytes(StandardCharsets.UTF_8));
    }

    private static String nouveau() {
        byte[] octets = new byte[32];
        ALEA.nextBytes(octets);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
    }
}