package mg.centre.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fournit des connexions a la base PostgreSQL.
 */
public final class Connexion {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver PostgreSQL absent : verifie lib/postgresql-*.jar");
        }
    }

    private Connexion() {
    }

    public static Connection ouvrir() throws SQLException {
        return DriverManager.getConnection(
                Config.get("db.url"),
                Config.get("db.user"),
                Config.get("db.password"));
    }
}