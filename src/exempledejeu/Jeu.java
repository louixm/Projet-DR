package exempledejeu;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Exemple de jeu
 *
 * @author guillaume.laurent
 */
public class Jeu {

    private BufferedImage nyancat, fond;
    private int x;
    private boolean gauche, droite;

    public Jeu() {
        try {
            this.fond = ImageIO.read(new File("fond.jpg"));
            this.nyancat = ImageIO.read(new File("nyancat.png"));
        } catch (IOException ex) {
            Logger.getLogger(Jeu.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.x = 100;
        this.gauche = false;
        this.droite = false;
    }

    public void MettreAJour() {
        if (this.gauche) {
            x -= 5;
        }
        if (this.droite) {
            x += 5;
        }
        if (x > this.fond.getWidth() - this.nyancat.getWidth()) {
            x = this.fond.getWidth() - this.nyancat.getWidth();
        }
        if (x < 0) {
            x = 0;
        }
    }

    public void Afficher(Graphics2D contexte) {
        contexte.drawImage(this.fond, 0, 0, null);
        contexte.drawImage(this.nyancat, x, 150, null);
    }

    public void setGauche(boolean gauche) {
        this.gauche = gauche;
    }

    public void setDroite(boolean droite) {
        this.droite = droite;
    }

}
