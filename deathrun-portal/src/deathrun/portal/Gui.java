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

    Gui(Game game) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }
    
    @Override
    protected void paintComponent(Graphics g) {
		// TODO: l'affichage du game
	}
}
