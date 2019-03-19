package exempledejeu;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Exemple de fenetre de jeu en utilisant uniquement des commandes
 *
 * @author guillaume.laurent
 */
public class FenetreDeJeu extends JFrame implements ActionListener, KeyListener {

    private BufferedImage buffer;
    private Graphics2D contexteBuffer;
    private Jeu jeu;
    private Timer timer;
    private JLabel jLabel1;

    public FenetreDeJeu() {
        // initialisation de la fenetre
        this.setSize(607, 380);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
        this.jLabel1 = new JLabel();
        this.jLabel1.setPreferredSize(new java.awt.Dimension(607, 380));
        this.setContentPane(this.jLabel1);
        this.pack();

        // Creation du jeu
        this.jeu = new Jeu();

        // Creation du buffer pour l'affichage du jeu et recuperation du contexte graphique
        this.buffer = new BufferedImage(this.jLabel1.getWidth(), this.jLabel1.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.jLabel1.setIcon(new ImageIcon(buffer));
        this.contexteBuffer = this.buffer.createGraphics();

        // Creation du Timer qui appelle this.actionPerformed() tous les 40 ms
        this.timer = new Timer(40, this);
        this.timer.start();
    }

    // Methode appelee par le timer et qui contient la boucle de jeu
    @Override
    public void actionPerformed(ActionEvent e) {
        this.jeu.MettreAJour();
        this.jeu.Afficher(contexteBuffer);
        this.jLabel1.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // NOP
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_RIGHT) {
            this.jeu.setDroite(true);
        }
        if (evt.getKeyCode() == evt.VK_LEFT) {
            this.jeu.setGauche(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_RIGHT) {
            this.jeu.setDroite(false);
        }
        if (evt.getKeyCode() == evt.VK_LEFT) {
            this.jeu.setGauche(false);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FenetreDeJeu fenetre = new FenetreDeJeu();
        fenetre.setVisible(true);
    }

}
