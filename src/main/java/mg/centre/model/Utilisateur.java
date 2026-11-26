package mg.centre.model;

/**
 * Represente un compte de connexion (table utilisateur).
 */
public class Utilisateur {

    public static final String MEDECIN = "MEDECIN";
    public static final String PATIENT = "PATIENT";

    private Integer id;
    private String login;
    private String motDePasse;   // HASH, jamais le mot de passe en clair
    private String role;
    private Integer idPatient;   // null pour un medecin
    private boolean actif = true;

    public Utilisateur() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getIdPatient() { return idPatient; }
    public void setIdPatient(Integer idPatient) { this.idPatient = idPatient; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}