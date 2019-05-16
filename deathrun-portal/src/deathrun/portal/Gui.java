package deathrun.portal;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
public class Gui extends JFrame implements KeyListener, MouseListener {
    public int scale = 36;	// pixel/m
    public boolean drawHitBox = false;
    public final int WINDOW_WIDTH = 1080, WINDOW_HEIGHT = 720;
    private final int window_header_size = 28;  // taille de la barre de titre de la fenetre (TODO: trouver ca dynamiquement ou trouver comment recuperer les bonnes coordonnées de la souris)
    
    private JLabel jLabel1;
    private BufferedImage buffer, background;
    private Graphics2D bufferContext;
    private Timer timer;
    private JFrame window;
    
    boolean editMode = false;
    private SelectionBloc selectionBloc;
    
    public Game game;
    public Player controled;
    
    
    Gui(Game game, Player controled) {
        //initilalisation de la fenêtre graphique
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.jLabel1 = new JLabel();
        this.jLabel1.setPreferredSize(new java.awt.Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setContentPane(this.jLabel1);
        this.pack();
        
        // Creation du jeu
        this.controled = controled;
        this.game = game;
        
        this.selectionBloc = new SelectionBloc(new javax.swing.JFrame(), true);
        
        try {
            this.background = ImageIO.read(new File("images/fond_1.png"));
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Creation du buffer pour l'affichage du jeu et recuperation du contexte graphique
        this.buffer = new BufferedImage(this.jLabel1.getWidth(), this.jLabel1.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.jLabel1.setIcon(new ImageIcon(buffer));
        this.bufferContext = this.buffer.createGraphics();

        // Creation du Timer qui appelle this.actionPerformed() tous les 20 ms
        this.timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.step();
                render(bufferContext);
                jLabel1.repaint();
            }
        });
        this.timer.start();
        
        this.addWindowListener(new WindowAdapter() {
            @Override      
            public void windowClosing(WindowEvent e) {
                if (game.sync != null) {
                    controled.disconnect();
                    game.disconnect();
                }
            }
        });
    }
    
    
    @Override
    public void keyTyped(KeyEvent e) {
        // NOP
    }
    
    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_D)	this.controled.setRight(true);
        if (evt.getKeyCode() == evt.VK_Q)	this.controled.setLeft(true);
        if (evt.getKeyCode() == evt.VK_SPACE)	this.controled.setJump(true);
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_D)       this.controled.setRight(false);
        if (evt.getKeyCode() == evt.VK_Q)       this.controled.setLeft(false);
        if (evt.getKeyCode() == evt.VK_SPACE)   this.controled.setJump(false); //peut etre pas besoin si on remet jump à false direct après le saut
        if (evt.getKeyCode() == evt.VK_SEMICOLON)   {this.scale++; System.out.println("Scale = " + this.scale);}
        if (evt.getKeyCode() == evt.VK_COMMA)       {this.scale--; System.out.println("Scale = " + this.scale);}
        if (evt.getKeyCode() == evt.VK_H)           PObject.drawHitBox = !PObject.drawHitBox;
        if (evt.getKeyCode() == evt.VK_E)           ;//TODO: trigger l'item du joueur
        if (evt.getKeyCode() == evt.VK_P)           game.purge();
    } 

    public void render(Graphics2D g) {
        g.drawImage(this.background, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
        for (PObject object : game.map.objects) {
            if (! object.foreground())	object.render(g, scale);
        }
        for (Player player : game.players)		
            player.render(g, scale);
        for (PObject object : game.map.objects) {
            if (object.foreground())	object.render(g, scale);
        }
    }
  
    @Override
    public void mouseClicked(MouseEvent e) { //Moment où le bouton de la souris a été pressé et relaché
        Vec2 pos_clicked = new Vec2(e.getX()/(float)scale, (e.getY()-window_header_size)/(float)scale);
        
        // prise de controle des pieges
        if (e.getButton() == 1) {   // clic gauche
            for (PObject object : game.map.objects) {
                if (object instanceof Trap && ((Trap)object).collision_box.contains(pos_clicked))
                    ((Trap)object).takeControl(controled, this);
            }
        }
        
        if (editMode) {
            try {
                if (e.getButton()==1 && this.selectionBloc.blocAPoser == 0) { //Si un clic gauche a été effectué et qu'on a pas encore choisi de bloc, alors la fenetre de selection de bloc s'ouvre
                    selectionBloc.setVisible(true);
                    selectionBloc.addWindowListener(new java.awt.event.WindowAdapter() { //Attente de la fermeture de la fenetre de selection de bloc
                            @Override
                            public void windowClosing(java.awt.event.WindowEvent e) {
                                System.exit(0);
                            }
                        });
                }
                else if(e.getButton()==1 && this.selectionBloc.blocAPoser != 0){ //Si un clic gauche a été effectué et qu'on a déjà choisi un bloc, alors le bloc est posé

                    //Tests pour savoir quel bloc a été choisi dans la fenetre SelectBloc
                    switch (this.selectionBloc.blocAPoser) {
                    case 1: //Plateforme
                        this.game.map.objects.add(new Platform(this.game, pos_clicked, new Box (0,0,2,1.5), 0)); //Ajout du bloc aux coordonnées du clic
                        System.out.println("Plateforme posée en (x = " + e.getX() + "; y = " + e.getY()+ ").");
                        break;
                    case 2: //Scie circulaire
                        this.game.map.objects.add(new Saw(this.game, pos_clicked)); //Ajout du bloc aux coordonnées du clic
                        System.out.println("Scie posée en (x = " + e.getX() + "; y = " + e.getY()+ ").");
                        break;
                    case 3: //Laser
                        //this.game.map.objects.add(new Laser(this.game, pos_clicked,0)); //Ajout du bloc aux coordonnées du clic
                        System.out.println("Laser posé en (x = " + e.getX() + "; y = " + e.getY()+ ").");
                        break;
                    case 4: //Acide
                        //this.game.map.objects.add(new Platform(this.game, new Vec2(e.getX()/scale, e.getY()/scale), new Box (0,0,2,1.5), 0)); //Ajout du bloc aux coordonnées du clic
                        System.out.println("Acide posé en (x = " + e.getX() + "; y = " + e.getY()+ ").");
                        break;
                    case 5: //Portail
                        this.game.map.objects.add(new Platform(this.game, pos_clicked, new Box (0,0,2,1.5), 0)); //Ajout du bloc aux coordonnées du clic
                        System.out.println("Portail posé en (x = " + e.getX() + "; y = " + e.getY()+ ").");
                        break;
                    case 6: //
                        this.game.map.objects.add(new Platform(this.game, pos_clicked, new Box (0,0,2,1.5), 0)); //Ajout du bloc aux coordonnées du clic
                        System.out.println("... posé en (x = " + e.getX() + "; y = " + e.getY()+ ").");
                        break;
                    case 7: //
                        this.game.map.objects.add(new Platform(this.game, pos_clicked, new Box (0,0,2,1.5), 0)); //Ajout du bloc aux coordonnées du clic
                        System.out.println("... posé en (x = " + e.getX() + "; y = " + e.getY()+ ").");
                        break;
                    case 8: //
                        this.game.map.objects.add(new Platform(this.game, pos_clicked, new Box (0,0,2,1.5), 0)); //Ajout du bloc aux coordonnées du clic
                        System.out.println("... posé en (x = " + e.getX() + "; y = " + e.getY()+ ").");
                        break;
                    }
                    this.selectionBloc.blocAPoser = 0;
                }
            } catch (IOException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
         //
    }

    @Override
    public void mouseReleased(MouseEvent e) {
         //
    }

    @Override
    public void mouseEntered(MouseEvent e) {
         //
    }

    @Override
    public void mouseExited(MouseEvent e) {
         //
    }
    
    
}
