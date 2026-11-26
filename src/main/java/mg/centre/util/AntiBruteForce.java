package mg.centre.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Bloque un login pendant 5 minutes apres 5 mots de passe faux.
 */
public final class AntiBruteForce {

    private static final int MAX_ECHECS = 5;
    private static final long DUREE_BLOCAGE_MS = 5 * 60 * 1000L;

    private static final ConcurrentHashMap<String, Compteur> COMPTEURS = new ConcurrentHashMap<>();

    private static final class Compteur {
        int echecs;
        long bloqueJusqua;
    }

    private AntiBruteForce() {
    }

    /** @return le nombre de minutes de blocage restantes (0 = pas bloque) */
    public static long minutesRestantes(String login) {
        Compteur c = COMPTEURS.get(login);
        if (c == null) {
            return 0;
        }
        long restant = c.bloqueJusqua - System.currentTimeMillis();
        return restant > 0 ? (restant + 59_999) / 60_000 : 0;
    }

    public static void echec(String login) {
        COMPTEURS.compute(login, (cle, c) -> {
            if (c == null) {
                c = new Compteur();
            }
            c.echecs++;
            if (c.echecs >= MAX_ECHECS) {
                c.bloqueJusqua = System.currentTimeMillis() + DUREE_BLOCAGE_MS;
                c.echecs = 0;
            }
            return c;
        });
    }

    public static void reussite(String login) {
        COMPTEURS.remove(login);
    }
}