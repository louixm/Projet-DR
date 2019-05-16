/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exempledejeu;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.event.ActionListener;
/**
 *
 * @author Bruno
 */
public class Perso {
    
    private BufferedImage robot, robotdroite1, robotBase, robotdroite2, robotdroite3, robotSaut, robotBasegauche, robotgauche1, robotgauche2, robotgauche3, robotsautgauche;
    private int x, y;
    private boolean gauche, droite, onFloor;
    private double vx = 0, vy = 0;
    private double ax = 0, ay = 0;
    private Timer timer1;
    private int EtatdAnimation;
    private int LineOfSight=1;
    
    public Perso(){
        try {
                this.robotBase = ImageIO.read(new File("tourellebase.png"));
                this.robotdroite1 = ImageIO.read(new File("tourelledroite1.png"));
                this.robotdroite2 = ImageIO.read(new File("tourelledroite2.png"));
                this.robotdroite3 = ImageIO.read(new File("tourelledroite3.png"));
                this.robotSaut = ImageIO.read(new File("tourellesautdroite.png"));
                this.robotBasegauche = ImageIO.read(new File("tourellebasegauche.png"));
                this.robotgauche1 = ImageIO.read(new File("tourellegauche1.png"));
                this.robotgauche2 = ImageIO.read(new File("tourellegauche2.png"));
                this.robotgauche3 = ImageIO.read(new File("tourellegauche3.png"));
                this.robotsautgauche = ImageIO.read(new File("tourellesautgauche.png"));
            } catch (IOException ex) {
                Logger.getLogger(Jeu.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        try {
//            Connection connexion = DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/20182019_s2_vs2_tp1_deathrun?serverTimezone=UTC", "deathrun", "111666");
//
//            PreparedStatement requete = connexion.prepareStatement("INSERT INTO players VALUES (0,?,0,0,?)");
//            requete.setString(1, "Jean-Bark");
//            requete.setInt(2, 2);
//            requete.executeUpdate();
//
//            requete.close();
//            connexion.close();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
        
        this.robot = this.robotBase;
        this.x = 100;
        this.y = 150;
        
        this.gauche = false;
        this.droite = false;
        this.onFloor = false;
        this.timer1 = new Timer(100, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (EtatdAnimation < 3){
                        EtatdAnimation++;
                     }
                if (EtatdAnimation >= 3){
                        EtatdAnimation =0;
                }
        }}
        );
                
    }
    
    public void MettreAJour(int width, int height) {
        //horizontal
        x += vx;
        if (this.gauche) {
            this.timer1.start();
            
            if (EtatdAnimation == 0){
                this.robot = this.robotgauche1;
            }
            if (EtatdAnimation == 1){
                this.robot = this.robotgauche2;
            }
            if (EtatdAnimation == 2){
                this.robot = this.robotgauche3;
            }
            LineOfSight = 2;
            ax = -1;
            if (vx > 0) ax += -2;
            if (vx > -14) vx += ax;
            
        }
        if (this.droite) {
            this.timer1.start();
            if (EtatdAnimation == 0){
            this.robot = this.robotdroite1;
            }
            if (EtatdAnimation == 1){
            this.robot = this.robotdroite2;
            }
            if (EtatdAnimation == 2){
            this.robot = this.robotdroite3;
            }
            LineOfSight = 1;
            ax = 1;
            if (vx < 0) ax += 2;
            if (vx < 14) vx += ax;
        }
            //stop le mouvement lorsque pas de déplacement
        if (!(this.droite || this.gauche)){
            if (LineOfSight == 1){
                 this.robot = this.robotBase;
            }
            if (LineOfSight == 2) {
                this.robot = this.robotBasegauche;
            }
            if (vx > 1) ax = -2;
            else if(vx < -1) ax = 2;
            else {ax = 0; vx = 0;}
            vx += ax;
        }
        if (!this.onFloor){
            if (LineOfSight == 1) {
                this.robot = this.robotSaut;
            }
            if (LineOfSight == 2) {
                this.robot = this.robotsautgauche;
            } 
        }    
            //bords
        if (x > width - this.robot.getWidth()){
            x = width - this.robot.getWidth();
            ax = 0; vx = 0;
        }
        if (x < 0){
            x = 0;
            ax = 0; vx = 0;
        }
        
        //vertical
        y += vy;
        if (y > height - this.robot.getHeight()){
            ay = 0; vy = 0;
            y = height - this.robot.getHeight();
            this.onFloor = true;
        }
        else if (y < height - this.robot.getHeight()) {
            ay = 2; vy += ay;
            this.onFloor = false;
        }
        
    }
        
    public void Afficher(Graphics2D contexte){
        contexte.drawImage(this.robot, x, y, null);
    }
    
    public void setGauche(boolean gauche) {
        this.gauche = gauche;
    }

    public void setDroite(boolean droite) {
        this.droite = droite;
    }

    public boolean isGauche() {
        return gauche;
    }

    public boolean isDroite() {
        return droite;
    }
    
    public void saute(int width){
        if (this.onFloor) vy = -26;
        else if (this.isOnWall(width)){
            if (this.isOnWall(width)){
                if (x == 0) vx = 16;
                else vx = -16;
            }
            vy = -22;  
        }
    }
    
    public boolean isOnWall(int width){
        return vy > 0 && (x == width - this.robot.getWidth() || x == 0);
    }
}
