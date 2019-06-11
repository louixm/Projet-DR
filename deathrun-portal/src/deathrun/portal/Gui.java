package deathrun.portal;

import static deathrun.portal.Platform.img;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
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
public class Gui extends JFrame implements KeyListener, MouseListener, MouseMotionListener {
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
    public Vec2 positionSouris = new Vec2(0/(float)scale, (0-window_header_size)/(float)scale);
    private int orientationBloc = 0; //Entier allant de 0 à 7 permettant de changer l'orientation d'un bloc
    
    public Game game;
    public Player controled;
    
    
    Gui(Game game, Player controled) {
        //initilalisation de la fenêtre graphique
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.jLabel1 = new JLabel();
        this.jLabel1.setPreferredSize(new java.awt.Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setContentPane(this.jLabel1);
        this.pack();
        
        // Creation du jeu
        this.controled = controled;
        this.game = game;
        
        this.selectionBloc = new SelectionBloc(game, new javax.swing.JFrame(), false);
        
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
                try {
                    if (!game.editionMode) game.step();
                    else if (game.sync != null) game.syncUpdate();
                } catch (IOException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
                render(bufferContext);
                jLabel1.repaint();
                if (game.editionMode) {
                    if (controled.readyToGo) tryToGo();
                    else enterEditionMode();
                }
                if (game.editionMode){
                    try {              
                        previsualisationBloc(positionSouris, orientationBloc);
                    } catch (Exception ex) {
                        
                    }
                }
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
        //System.out.println(evt.getKeyCode());
        if (evt.getKeyCode() == evt.VK_D)       this.controled.setRight(false);
        if (evt.getKeyCode() == evt.VK_Q)       this.controled.setLeft(false);
        if (evt.getKeyCode() == evt.VK_SPACE)   this.controled.setJump(false); //peut etre pas besoin si on remet jump à false direct après le saut
        if (evt.getKeyCode() == evt.VK_SEMICOLON)   {this.scale++; System.out.println("Scale = " + this.scale);}
        if (evt.getKeyCode() == evt.VK_COMMA)       {this.scale--; System.out.println("Scale = " + this.scale);}
        if (evt.getKeyCode() == evt.VK_H)           PObject.drawHitBox = !PObject.drawHitBox;
        //if (evt.getKeyCode() == evt.VK_E)           {this.editMode = !this.editMode; System.out.println("Mode édition actif : " + this.editMode);}
        if (evt.getKeyCode() == evt.VK_P)           game.purge(); 
        if (evt.getKeyCode() == evt.VK_O)           game.purgeTraps(); 
        if (evt.getKeyCode() == evt.VK_F1)          switch_trap(0);
        if (evt.getKeyCode() == evt.VK_F2)          switch_trap(1);
        if (evt.getKeyCode() == evt.VK_F3)          switch_trap(2);
        if (evt.getKeyCode() == evt.VK_F4)          switch_trap(3);
        if (evt.getKeyCode() == evt.VK_F5)          switch_trap(4);
        if (evt.getKeyCode() == evt.VK_F6)          switch_trap(5);
        if (evt.getKeyCode() == evt.VK_F7)          switch_trap(6);
        if (evt.getKeyCode() == evt.VK_F8)          switch_trap(7);
        
        if (editMode || game.editionMode){
            if (evt.getKeyCode() == evt.VK_LEFT)    {if (orientationBloc == 0) orientationBloc = 7; else orientationBloc--;}
            if (evt.getKeyCode() == evt.VK_RIGHT)   {if (orientationBloc == 7) orientationBloc = 0; else orientationBloc++;}
        }
                  
    } 
    
    public void switch_trap(int i) {
        if (i < controled.traps.size()) {
            Trap trap = controled.traps.get(i);
            trap.enable(!trap.enabled, true);
        }
    }

    public void render(Graphics2D g) {
        g.drawImage(this.background, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
        for (PObject p: game.objects.values()) if (!p.foreground() && !(p instanceof Player)) p.render(g, scale);
        for (PObject p: game.objects.values()) if (p instanceof Player) p.render(g, scale);
        for (PObject p : game.objects.values()) if (p.foreground() && !(p instanceof Player)) p.render(g, scale);
    }
  
    @Override
    public void mouseClicked(MouseEvent e) { //Moment où le bouton de la souris a été pressé et relaché
        Vec2 pos_clicked = new Vec2(e.getX()/(float)scale, (e.getY()-window_header_size)/(float)scale);
        // prise de controle des pieges
        if (e.getButton() == 1) {   // clic gauche
            for (HashMap.Entry<Integer,PObject> p : game.objects.entrySet()) {
                PObject object = p.getValue();
                if (object instanceof Trap && ((Trap)object).collision_box.contains(pos_clicked))
                    ((Trap)object).takeControl(controled);
            }
        }
        
        if (editMode || game.editionMode) {
            /*if (editMode && (e.getButton()==1 && this.selectionBloc.blocAPoser == -1)) { //Si un clic gauche a été effectué et qu'on a pas encore choisi de bloc, alors la fenetre de selection de bloc s'ouvre
            //    selectionBloc.setVisible(true);
            //    selectionBloc.addWindowListener(new java.awt.event.WindowAdapter() { //Attente de la fermeture de la fenetre de selection de bloc
            //        @Override
            //        public void windowClosing(java.awt.event.WindowEvent e) {
            //            selectionBloc.dispose();
            //        }
            //    });
            //}
            else*/ if(e.getButton()==1 && this.selectionBloc.blocAPoser != -1){
                try { //Si un clic gauche a été effectué et qu'on a déjà choisi un bloc, alors le bloc est posé
                    poserObjet(pos_clicked, orientationBloc);
//                    controled.readyToGo = true;
                    controled.setState(0);
                    controled.syncReady(true);
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
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
    
    public void poserObjet(Vec2 pos_clicked, int orientationBloc) throws SQLException, IOException{
        PObject obj = null;
//        int orientation = orientationBloc;
//        switch (this.selectionBloc.blocAPoser) {
//                    //Tests pour savoir quel bloc a été choisi dans la fenetre SelectBloc
//                    case 0:case 1:case 2:case 3:case 4:case 5:case 6:case 7:case 8:case 9: //Plateforme
//                        obj = new Platform(this.game, pos_clicked, Platform.standardBoxes[this.selectionBloc.blocAPoser], this.selectionBloc.blocAPoser);
//                        break;
//                    case 10: //Bombe
//                        obj = new Bomb(this.game, pos_clicked);
//                        break;    
//                    case 11: //Scie circulaire
//                        obj = new Saw(this.game, pos_clicked);
//                        break;
//                    case 12: //Laser                       
//                        obj = new Laser(this.game, pos_clicked, orientationBloc);
//                        break;
//                    case 13: //Punch 
//                        if(orientation >= 4) orientation -= 4;
//                        obj = new Punch(this.game, orientation, pos_clicked);
//                        break;
//                    case 14: //Spikes
//                        if(orientation >= 4) orientation -= 4;
//                        obj = new Spikes(this.game, orientation, pos_clicked);
//                        break;
////                    case 15: //Acid
////                        Acid acid = new Acid(this.game, pos_clicked);
////                        obj = (PObject) acid;
////                        break;
//                    }
        obj = selectionBloc.objectToPlace;
        obj.setPosition(pos_clicked);
        obj.setOrientation(orientationBloc);

        obj.last_sync = game.sync.latest++;
        obj.syncSet(game.sync, true);
        
        this.selectionBloc.blocAPoser = -1;
    }

    @Override
    public void mouseMoved(MouseEvent e) { //Permet d'obtenir la position de la souris après qu'elle ait bougée
        if (editMode || game.editionMode) {
            if (this.selectionBloc.blocAPoser != -1){
                Vec2 pos_clicked = new Vec2(e.getX()/(float)scale, (e.getY()-window_header_size)/(float)scale);
                this.positionSouris = pos_clicked;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void previsualisationBloc(Vec2 pos_clicked, int orientationBloc) throws IOException, SQLException{
//        Platform platform;
//        int orientation = orientationBloc;
//        switch (this.selectionBloc.blocAPoser) {
//            case 0:case 1:case 2:case 3:case 4:case 5:case 6:case 7:case 8:case 9: //Plateforme
//                platform = new Platform(this.game, pos_clicked, Platform.standardBoxes[this.selectionBloc.blocAPoser], this.selectionBloc.blocAPoser); //peut etre faire differentes tailles de box (dans un tableau)
//                platform.render(this.bufferContext, scale);
//                game.objects.remove(platform.db_id);
//                break;
//            case 10: //Bombe
//                Bomb bomb = new Bomb(this.game, pos_clicked);
//                bomb.render(this.bufferContext, scale);
//                game.objects.remove(bomb.db_id);
//                break;    
//            case 11: //Scie circulaire
//                Saw saw = new Saw(this.game, pos_clicked);
//                saw.render(this.bufferContext, scale);
//                game.objects.remove(saw.db_id);
//                break;
//            case 12: //Laser
//                Laser laser = new Laser(this.game, pos_clicked, orientationBloc);
//                laser.render(this.bufferContext, scale);
//                game.objects.remove(laser.db_id);
//                break;
//            case 13: //Punch
//                if(orientation >= 4) orientation -= 4;
//                Punch punch = new Punch(this.game, orientation, pos_clicked);
//                punch.render(this.bufferContext, scale);//portal.render(this.bufferContext, scale);
//                game.objects.remove(punch.db_id);
//                break;
//            case 14: //Spikes
//                if(orientation >= 4) orientation -= 4;
//                Spikes spikes = new Spikes(this.game, orientation, pos_clicked);
//                spikes.render(this.bufferContext, scale);
//                game.objects.remove(spikes.db_id);
//                break;
////            case 15: //Acid
////                Acid acid = new Acid(this.game, pos_clicked);
////                acid.render(this.bufferContext, scale);
////                game.objects.remove(acid.platform_d.db_id);
////                game.objects.remove(acid.platform_g.db_id);
////                game.objects.remove(acid.platform_m1.db_id);
////                game.objects.remove(acid.platform_m2.db_id);
////                game.objects.remove(acid.db_id);
////                break;
////            default: System.out.println("nothing to prev");
//        }
        selectionBloc.objectToPlace.setPosition(pos_clicked);
        selectionBloc.objectToPlace.setOrientation(orientationBloc);
//        selectionBloc.objectToPlace.render(this.bufferContext, scale);
    }
    
    public void enterEditionMode(){
        controled.syncReady(false);
        int nbPlayersWhoReachedDoor = 0, nbPlayersWhoDied = 0;
        for (Player p: game.players){
            if (!p.disconnected && p.hasReachedExitDoor) nbPlayersWhoReachedDoor++;
            if (!p.disconnected && p.dead) nbPlayersWhoDied++;
        }
        float ratio; //ratio réussite/total
        try{
            ratio = (1f+nbPlayersWhoReachedDoor)/(2f+nbPlayersWhoReachedDoor+nbPlayersWhoDied);
        } catch (Exception e){
            ratio = 0.5f; //ne devrait jamais arriver
        }
        System.out.println("ratio = " + ratio);
        
        int[] objectsToPlace = {0,0,0};
        /*
        0 = plateforme type 0
        1 = plateforme type 1
        2 = plateforme type 2
        3 = plateforme type 3
        4 = plateforme type 4
        5 = plateforme type 5
        6 = plateforme type 6
        7 = plateforme type 7
        8 = plateforme type 8
        9 = plateforme type 9
        10 = bomb
        11 = saw
        12 = laser
        13 = punch
        14 = spikes
        15 = explosive
        */
        Random ran = new Random();
        for (int i = 0; i < 3; i++){
            if (ran.nextFloat() < ratio) objectsToPlace[i] = 11 + ran.nextInt(5); //place a trap
            else {
                int r = ran.nextInt(12);
                if (r > 10) r = 10; //for bombs
                objectsToPlace[i] = r;
            } //place a platform (or bomb)
        }
        selectionBloc.objectsToPlace = objectsToPlace;
        selectionBloc.setIcons();
        selectionBloc.setVisible(true);
        selectionBloc.addWindowListener(new java.awt.event.WindowAdapter() { //Attente de la fermeture de la fenetre de selection de bloc
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                selectionBloc.dispose();
            }
        });
        if (selectionBloc.blocAPoser != 1) controled.readyToGo = true;
    }

    private void tryToGo() {
        boolean go = true;
        PreparedStatement reqplayers;
        try {
            reqplayers = game.sync.srv.prepareStatement("SELECT id,state FROM players");

            ResultSet rplayers = reqplayers.executeQuery();
            while (rplayers.next() && go){
                int id = rplayers.getInt("id");
                int state = rplayers.getInt("state");

                Player p;
                if (game.objects.containsKey(id))    p = game.getPlayerWithId(id);
                else                            { p = (Player) game.syncNewPlayer(id); System.out.println("spawn a player "+id); }
                try {
                        if (!p.isControled()){
                            p.setState(state);
                        }
                    } catch (NullPointerException e){
                        System.out.println("NullPointer for player with id " + id);
                    }
                if (state == -1) go = false;
            }
            rplayers.close();
        } catch (SQLException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (go) {
            game.switchToEditionMode(false);
            controled.readyToGo = false;
            controled.setLeft(false);
            controled.setRight(false);
            controled.setJump(false);
        }
    }
    
}
