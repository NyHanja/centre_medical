package mg.centre.qrcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import mg.centre.dao.PatientDAO;
import mg.centre.model.Patient;
import mg.centre.util.Config;

/**
 * Generation des QR codes des patients.
 * Utilisable de DEUX facons :
 *   - programme autonome : main() (terminal, script, cron)
 *   - depuis le site web  : bouton du medecin (PatientController)
 *
 * Resultat : <patients.dossier>/<id>/<id>_qrcode.jpeg
 * Contenu du QR code : <app.urlBase>patient/<id>
 */
public class GenerateurQrCode {

    private static final int TAILLE = 300;   // pixels

    private final PatientDAO dao = new PatientDAO();

    /** Programme autonome. */
    public static void main(String[] args) throws Exception {
        System.out.println(new GenerateurQrCode().genererManquants());
    }

    /** Parcourt tous les patients et cree les QR codes manquants. @return un resume */
    public String genererManquants() throws SQLException {
        int crees = 0;
        int dejaPresents = 0;
        int erreurs = 0;

        for (Patient p : dao.findAll()) {
            if (existe(p)) {
                dejaPresents++;
                continue;
            }
            try {
                generer(p);
                crees++;
            } catch (Exception e) {
                System.err.println("Erreur pour le patient " + p.getId() + " : " + e.getMessage());
                erreurs++;
            }
        }
        return "QR codes crees : " + crees + " | deja presents : " + dejaPresents + " | erreurs : " + erreurs;
    }

    /** Le QR code existe-t-il deja (en base ET sur le disque) ? */
    public boolean existe(Patient p) {
        return p.getQrcode() != null && Files.exists(fichier(p.getId()));
    }

    /** Cree (ou recree) le QR code d'UN patient. */
    public void generer(Patient p) throws WriterException, IOException, SQLException {
        int id = p.getId();
        Path fichier = fichier(id);
        Files.createDirectories(fichier.getParent());

        String contenu = Config.get("app.urlBase") + "patient/" + id;
        creerImage(contenu, fichier);
        dao.updateQrCode(id, id + "/" + fichier.getFileName());
        System.out.println("QR code cree : " + fichier + "  ->  " + contenu);
    }

    /** Chemin du fichier : <patients.dossier>/<id>/<id>_qrcode.jpeg */
    public Path fichier(int id) {
        return Paths.get(Config.get("patients.dossier"))
                .resolve(String.valueOf(id))
                .resolve(id + "_qrcode.jpeg");
    }

    /** Dessine le QR code de "contenu" et l'enregistre en JPEG. */
    private static void creerImage(String contenu, Path fichier) throws WriterException, IOException {
        Map<EncodeHintType, Object> options = Map.of(
                EncodeHintType.CHARACTER_SET, "UTF-8",
                EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                EncodeHintType.MARGIN, 2);

        // Calcul du QR code : une grille de cases noires (true) et blanches (false)
        BitMatrix grille = new QRCodeWriter().encode(contenu, BarcodeFormat.QR_CODE, TAILLE, TAILLE, options);

        // Conversion de la grille en image
        BufferedImage image = new BufferedImage(grille.getWidth(), grille.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < grille.getWidth(); x++) {
            for (int y = 0; y < grille.getHeight(); y++) {
                image.setRGB(x, y, grille.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        if (!ImageIO.write(image, "jpeg", fichier.toFile())) {
            throw new IOException("Format JPEG non supporte");
        }
    }
}