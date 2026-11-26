package mg.centre.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lit le fichier db.properties UNE SEULE FOIS et donne acces a ses valeurs.
 */
public final class Config {

    private static final Properties PROPRIETES = charger();

    private Config() {
        // classe utilitaire : on ne l'instancie pas
    }

    private static Properties charger() {
        Properties p = new Properties();
        try (InputStream in = Config.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties introuvable dans le classpath");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire db.properties", e);
        }
        return p;
    }

    public static String get(String cle) {
        String valeur = PROPRIETES.getProperty(cle);
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalStateException("Parametre manquant dans db.properties : " + cle);
        }
        return valeur.trim();
    }
}