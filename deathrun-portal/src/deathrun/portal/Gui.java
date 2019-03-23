package deathrun.portal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import javax.swing.Timer;

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
    public float scale = 30;	// pixel/m
    public Game game;
    
    Timer timer;

    
    Gui(Game game) {
        this.game = game;
        this.timer = new Timer(20, this);
        this.timer.start();
    }
    
    
    @Override
    public void keyTyped(KeyEvent e) {
        // NOP
    }
    
    @Override
    public void keyPressed(KeyEvent evt) {
//        if (evt.getKeyCode() == evt.VK_D) {
//            this.game.setDroite(true);
//        }
//        if (evt.getKeyCode() == evt.VK_Q) {
//            this.game.setGauche(true);
//        }
//        if (evt.getKeyCode() == evt.VK_SPACE){
//            this.game.saute();
//        }
    }

    @Override
    public void keyReleased(KeyEvent evt) {
//        if (evt.getKeyCode() == evt.VK_D) {
//            this.game.setDroite(false);
//        }
//        if (evt.getKeyCode() == evt.VK_Q) {
//            this.game.setGauche(false);
//        }
    }
    
    public void actionPerformed(ActionEvent e) {
        game.physicStep();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        for (PObject object : game.map.objects) {
            if (! object.foreground())	object.render(g, scale);
        for (Player player : game.players)		
            player.render(g, scale);
        for (PObject object : game.map.objects) {
            if (object.foreground())	object.render(g, scale);
    }
}
