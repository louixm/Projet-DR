/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exempledejeu;

import java.awt.Graphics2D;
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

/**
 *
 * @author Bruno
 */
public class Perso {
    
    private BufferedImage robot, robotL, robotR;
    private int x, y;
    private boolean gauche, droite, onFloor;
    private double vx = 0, vy = 0;
    private double ax = 0, ay = 0;
    
    public Perso(){
        try {
                this.robotR = ImageIO.read(new File("robotR.png"));
                this.robotL = ImageIO.read(new File("robotL.png"));
            } catch (IOException ex) {
                Logger.getLogger(Jeu.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            Connection connexion = DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/20182019_s2_vs2_tp1_deathrun?serverTimezone=UTC", "deathrun", "111666");

            PreparedStatement requete = connexion.prepareStatement("INSERT INTO players VALUES (0,?,0,0,0)");
            requete.setString(1, "Jean-Bark");
            requete.executeUpdate();

            requete.close();
            connexion.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        this.robot = this.robotR;
        this.x = 100;
        this.y = 150;
        
        this.gauche = false;
        this.droite = false;
        this.onFloor = false;
    }
    
    public void MettreAJour(int width, int height) {
        //horizontal
        x += vx;
        if (this.gauche) {
            this.robot = this.robotL;
            ax = -1;
            if (vx > 0) ax += -2;
            if (vx > -14) vx += ax;
        }
        if (this.droite) {
            this.robot = this.robotR;
            ax = 1;
            if (vx < 0) ax += 2;
            if (vx < 14) vx += ax;
        }
            //stop le mouvement lorsque pas de déplacement
        if (!(this.droite || this.gauche)){
            if (vx > 1) ax = -2;
            else if(vx < -1) ax = 2;
            else {ax = 0; vx = 0;}
            vx += ax;
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
    
    public void saute(){
//        if (this.onFloor)
            vy = -26;
    }
    
}
