package deathrun.portal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ydejongh
 */
public class Gui extends JFrame implements ActionListener, KeyListener {
	const float scale = 30;	// pixel/m
        private Game game;

    Gui(Game game) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        this.game = game;
    }
    
        // Methode appelee par le timer et qui contient la boucle de game
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        this.game.MettreAJour();
//        this.game.Afficher(contexteBuffer);
//        this.jLabel1.repaint();
//    }

    @Override
    public void keyTyped(KeyEvent e) {
        // NOP
    }
    
    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_D) {
            this.game.setDroite(true);
        }
        if (evt.getKeyCode() == evt.VK_Q) {
            this.game.setGauche(true);
        }
        if (evt.getKeyCode() == evt.VK_SPACE){
            this.game.saute();
        }
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_D) {
            this.game.setDroite(false);
        }
        if (evt.getKeyCode() == evt.VK_Q) {
            this.game.setGauche(false);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
		// TODO: l'affichage du game
	}
}
