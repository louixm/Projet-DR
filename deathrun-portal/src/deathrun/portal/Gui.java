package deathrun.portal;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
    public final float scale = 30;	// pixel/m
    public final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 1080;
    
    private JLabel jLabel1;
    public Game game;
    private BufferedImage buffer;
    private Graphics2D bufferContext;
    private Timer timer;

    
    Gui(Game game) {
        //initilalisation de la fenêtre graphique
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
        this.jLabel1 = new JLabel();
        this.jLabel1.setPreferredSize(new java.awt.Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setContentPane(this.jLabel1);
        this.pack();
        
        // Creation du jeu
        this.game = new Game();

        // Creation du buffer pour l'affichage du jeu et recuperation du contexte graphique
        this.buffer = new BufferedImage(this.jLabel1.getWidth(), this.jLabel1.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.jLabel1.setIcon(new ImageIcon(buffer));
        this.bufferContext = this.buffer.createGraphics();

        // Creation du Timer qui appelle this.actionPerformed() tous les 20 ms
        this.timer = new Timer(20, this);
        this.timer.start();
    }
    
    
    @Override
    public void keyTyped(KeyEvent e) {
        // NOP
    }
    
    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_D) this.game.getFirstPlayer().setRight(true);
        if (evt.getKeyCode() == evt.VK_Q) this.game.getFirstPlayer().setLeft(true);
        if (evt.getKeyCode() == evt.VK_SPACE) this.game.getFirstPlayer().setJump(true);
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_D) this.game.getFirstPlayer().setRight(false);
        if (evt.getKeyCode() == evt.VK_Q) this.game.getFirstPlayer().setLeft(false);
//        if (evt.getKeyCode() == evt.VK_SPACE) this.game.getFirstPlayer().setJump(false); //peut etre pas besoin si on remet jump à false direct après le saut
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        game.physicStep();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics2D g) {
        for (PObject object : game.map.objects) {
            if (! object.foreground())	object.render(g, scale); }
        for (Player player : game.players)		
            player.render(g, scale);
        for (PObject object : game.map.objects) {
            if (object.foreground())	object.render(g, scale); }
        }
}
