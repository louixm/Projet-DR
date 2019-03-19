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

    private BufferedImage fond;
    private Perso perso = new Perso();
    
    public Jeu() {
        try {
            this.fond = ImageIO.read(new File("fond.png"));
        } catch (IOException ex) {
            Logger.getLogger(Jeu.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void MettreAJour() {
        perso.MettreAJour(this.fond.getWidth(), this.fond.getHeight());
    }

    public void Afficher(Graphics2D contexte) {
        contexte.drawImage(this.fond, 0, 0, null);
        perso.Afficher(contexte);
    }

    public void setGauche(boolean gauche) {
        perso.setGauche(gauche);
    }

    public void setDroite(boolean droite) {
        perso.setDroite(droite);
    }
    
    public boolean isGauche() {
        return perso.isGauche();
    }

    public boolean isDroite() {
        return perso.isDroite();
    }
    
    public void saute(){
        perso.saute();
    }

}
