package mg.centre.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import mg.centre.model.Patient;
import mg.centre.util.Connexion;

public class PatientDAO {

    private static final String COLONNES =
            "id, nom, prenom, date_naissance, sexe, telephone, adresse, "
          + "groupe_sanguin, allergies, qrcode, date_creation";

    // ---------- LECTURE ----------

    public List<Patient> findAll() throws SQLException {
        String sql = "SELECT " + COLONNES + " FROM patient ORDER BY nom, prenom";
        List<Patient> liste = new ArrayList<>();
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(lire(rs));
            }
        }
        return liste;
    }

    public List<Patient> rechercher(String motCle) throws SQLException {
        String sql = "SELECT " + COLONNES + " FROM patient "
                   + "WHERE nom ILIKE ? OR prenom ILIKE ? ORDER BY nom, prenom";
        String motif = "%" + echapperLike(motCle) + "%";
        List<Patient> liste = new ArrayList<>();
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, motif);
            ps.setString(2, motif);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(lire(rs));
                }
            }
        }
        return liste;
    }

    /** @return le patient, ou null s'il n'existe pas */
    public Patient findById(int id) throws SQLException {
        String sql = "SELECT " + COLONNES + " FROM patient WHERE id = ?";
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? lire(rs) : null;
            }
        }
    }

    // ---------- ECRITURE ----------

    /** @return l'id genere par la base */
    public int insert(Patient p) throws SQLException {
        String sql = "INSERT INTO patient (nom, prenom, date_naissance, sexe, telephone, "
                   + "adresse, groupe_sanguin, allergies) VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
                   + "RETURNING id";
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql)) {
            remplir(ps, p);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** @return true si une ligne a ete modifiee */
    public boolean update(Patient p) throws SQLException {
        String sql = "UPDATE patient SET nom = ?, prenom = ?, date_naissance = ?, sexe = ?, "
                   + "telephone = ?, adresse = ?, groupe_sanguin = ?, allergies = ? "
                   + "WHERE id = ?";
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql)) {
            remplir(ps, p);
            ps.setInt(9, p.getId());
            return ps.executeUpdate() == 1;
        }
    }

    /** @return true si une ligne a ete supprimee */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM patient WHERE id = ?";
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // ---------- OUTILS PRIVES ----------

    /** Remplit les 8 premiers "?" communs a INSERT et UPDATE. */
    private void remplir(PreparedStatement ps, Patient p) throws SQLException {
        ps.setString(1, p.getNom());
        ps.setString(2, p.getPrenom());
        ps.setObject(3, p.getDateNaissance());
        ps.setString(4, p.getSexe());
        ps.setString(5, p.getTelephone());
        ps.setString(6, p.getAdresse());
        ps.setString(7, p.getGroupeSanguin());
        ps.setString(8, p.getAllergies());
    }

    /** Transforme la ligne courante du ResultSet en objet Patient. */
    private Patient lire(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("id"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setDateNaissance(rs.getObject("date_naissance", LocalDate.class));
        p.setSexe(rs.getString("sexe"));
        p.setTelephone(rs.getString("telephone"));
        p.setAdresse(rs.getString("adresse"));
        p.setGroupeSanguin(rs.getString("groupe_sanguin"));
        p.setAllergies(rs.getString("allergies"));
        p.setQrcode(rs.getString("qrcode"));
        p.setDateCreation(rs.getObject("date_creation", LocalDateTime.class));
        return p;
    }

    /** Neutralise les jokers % et _ du LIKE pour qu'ils soient cherches tels quels. */
    private String echapperLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

        /** Enregistre le chemin du QR code d'un patient (utilise par le programme QR code). */
    public void updateQrCode(int id, String chemin) throws SQLException {
        String sql = "UPDATE patient SET qrcode = ? WHERE id = ?";
        try (Connection c = Connexion.ouvrir();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, chemin);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}