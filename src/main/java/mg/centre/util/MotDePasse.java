package mg.centre.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hachage des mots de passe avec PBKDF2-HMAC-SHA256 (inclus dans Java, aucun jar a ajouter).
 * Format stocke en base : pbkdf2$iterations$sel$hash
 */
public final class MotDePasse {

    private static final String ALGORITHME = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int TAILLE_SEL = 16;      // octets
    private static final int TAILLE_HASH = 256;    // bits
    private static final SecureRandom ALEA = new SecureRandom();

    /** Hash d'un mot de passe que personne ne connait : sert quand le login n'existe pas. */
    public static final String HASH_LEURRE = hacher("leurre-" + ALEA.nextLong());

    private MotDePasse() {
    }

    public static String hacher(String motDePasse) {
        byte[] sel = new byte[TAILLE_SEL];
        ALEA.nextBytes(sel);
        byte[] hash = calculer(motDePasse, sel, ITERATIONS);
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2$" + ITERATIONS + "$" + b64.encodeToString(sel) + "$" + b64.encodeToString(hash);
    }

    public static boolean verifier(String motDePasse, String stocke) {
        if (motDePasse == null || stocke == null) {
            return false;
        }
        String[] parties = stocke.split("\\$");
        if (parties.length != 4 || !parties[0].equals("pbkdf2")) {
            return false;
        }
        try {
            int iterations = Integer.parseInt(parties[1]);
            byte[] sel = Base64.getDecoder().decode(parties[2]);
            byte[] attendu = Base64.getDecoder().decode(parties[3]);
            byte[] calcule = calculer(motDePasse, sel, iterations);
            return MessageDigest.isEqual(attendu, calcule);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** @return un message d'erreur, ou null si le mot de passe est acceptable */
    public static String verifierRobustesse(String motDePasse) {
        if (motDePasse == null || motDePasse.length() < 8) {
            return "Le mot de passe doit contenir au moins 8 caracteres.";
        }
        if (motDePasse.length() > 128) {
            return "Le mot de passe ne doit pas depasser 128 caracteres.";
        }
        return null;
    }

    private static byte[] calculer(String motDePasse, byte[] sel, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(motDePasse.toCharArray(), sel, iterations, TAILLE_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITHME).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 indisponible", e);
        } finally {
            spec.clearPassword();
        }
    }
}