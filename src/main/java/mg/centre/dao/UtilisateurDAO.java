package mg.centre.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import mg.centre.model.Utilisateur;
import mg.centre.util.Connexion;

/**
 * Requetes SQL sur la table utilisateur.
 */
public class UtilisateurDAO {

    private static final String COLONNES = "id, login, mot_de_passe, role, id_patient, actif";

    /** @return l'utilisateur, ou null si le login n'existe pas */
    public Utilisateur findByLogin(String login) throws SQLException {
        String sql = "SELECT " + COLONNES + " FROM utilisateur WHERE login = ?";
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? lire(rs) : null;
            }
        }
    }

    /** @return le compte lie a ce patient, ou null s'il n'en a pas */
    public Utilisateur findByIdPatient(int idPatient) throws SQLException {
        String sql = "SELECT " + COLONNES + " FROM utilisateur WHERE id_patient = ?";
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPatient);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? lire(rs) : null;
            }
        }
    }

    /** @return l'id genere. Le mot de passe de l'objet doit DEJA etre hache. */
    public int insert(Utilisateur u) throws SQLException {
        String sql = "INSERT INTO utilisateur (login, mot_de_passe, role, id_patient, actif) "
                   + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getLogin());
            ps.setString(2, u.getMotDePasse());
            ps.setString(3, u.getRole());
            if (u.getIdPatient() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, u.getIdPatient());
            }
            ps.setBoolean(5, u.isActif());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Utilisateur lire(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setLogin(rs.getString("login"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setRole(rs.getString("role"));
        int idPatient = rs.getInt("id_patient");
        u.setIdPatient(rs.wasNull() ? null : idPatient);
        u.setActif(rs.getBoolean("actif"));
        return u;
    }
}