/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author kbenie
 */
public class Punch extends Trap {
    private Image img;
    private Image im0;
    private Image im1;
    private Image im2;
    private Image im3;
    private Image im4;
    int typePlateforme;
    int step;
//    int orientation;  // 0 = tue en descente; 1 = tue à gauche, 2 tue en haut, 3 tue à droite
    Vec2 initPosition;   //posiion initiale
    Vec2 activPosition;   //position quand le piège est sous controle
    long time_next_image;
    long image_duration = 20000000;
    
    boolean dejaJoue = false;
    
    public Punch(Game game, int orientation, Vec2 position) throws IOException, SQLException {
        super(game, "punch");
        this.orientation = orientation;
        this.position = position;
        this.initPosition = position; //redondant ?
        collision_box = new Box(0,0 , 1,1);
        
        
        im0 = ImageIO.read(new File("./images/punch"+orientation+"_0.png"));
        im1 = ImageIO.read(new File("./images/punch"+orientation+"_1.png"));
        im2 = ImageIO.read(new File("./images/punch"+orientation+"_2.png"));
        im3 = ImageIO.read(new File("./images/punch"+orientation+"_3.png"));
        im4 = ImageIO.read(new File("./images/punch"+orientation+"_4.png"));
        
    }
    
    //--------------- interface de gestion des collisions -----------------
    @Override
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?1:0;
    }
    //--------------- interface de gestion des collisions -----------------*/
    
    
    @Override
    public void onGameStep(Game game, float dt) {
        for (Player p: game.players){
            Vec2 p1 = p.getCollisionBox().p1 ; //point supérieur gauche
            Vec2 p2 = p.getCollisionBox().p2 ; //point inférieur droit
            Vec2 p3 = new Vec2(p1.x, p2.y) ; // point inférieur gauche
            Vec2 p4 = new Vec2(p2.x , p1.y) ; // point supérieur droit
            
            if (orientation == 0){ // bas
                if ((collision_box.p1.x <= p4.x && p1.x<=collision_box.p2.x) && p1.y == collision_box.p2.y){  
                    game.addScoreUponTrapKill(this, p);
                    p.setDead(true);                 
                    // Player Killed
                }
            }
            if (orientation == 1){ // gauche
                if ((collision_box.p1.y <= p2.y && p4.y<=collision_box.p2.y) && p2.x == collision_box.p1.x){  
                    game.addScoreUponTrapKill(this, p);
                    p.setDead(true);
                    // Player Killed
                }
            }
            if (orientation == 2){ // haut
                if ((collision_box.p1.x <= p4.x && p1.x<=collision_box.p2.x) && p2.y == collision_box.p1.y){  
                    game.addScoreUponTrapKill(this, p);
                    p.setDead(true);
                    // Player Killed
                }
            }
            if (orientation == 3){ // droit
                if ((collision_box.p1.y <= p2.y && p4.y<=collision_box.p2.y) && p1.x == collision_box.p2.x){  
                    game.addScoreUponTrapKill(this, p);
                    p.setDead(true);
                    // Player Killed
                }
            }
        }
    }
    
    
    /*void enable(boolean enable, boolean withsync) {
        if (step == 0)  {
            if (enable) super.enable(enable, withsync);
            else        super.enable(enable, false);
        }
    }*/
    
    //-------------------Interface d'affichage--------------------------------
    
    @Override
    public void render(Graphics2D canvas, float scale) {
        
        // -----------------Audio---------------------------
        SoundPlayer crush = new SoundPlayer("crush.mp3", false);
        if (enabled && dejaJoue == false) {
            dejaJoue = true;
            crush.play();
        }
        if (!enabled){
            dejaJoue = false;
        }
            // -----------------MAJ de l'image---------------------------
            
        long ac_time = System.nanoTime();
        if (ac_time > time_next_image)  {    
            
            // si le piège n'est pas activé on met l'image "punch0"
            if (!enabled){   
                img = im0;
                step=0;
            } 
            
            // si le piège est activé
            else {  
                
                // incrementer le compteur de frame
                step ++;
                
                // évolution du numéro de l'immage
                int fiveMulticator = (int)step/5;
                int numImage;
                if (fiveMulticator % 2 == 0){
                    numImage = step-(5*fiveMulticator);
                }else{
                    numImage = 4-(step-(5*fiveMulticator));
                }
                if (numImage==0){
                    img = im0;
                }
                if (numImage==1){
                    img = im1;
                }
                if (numImage==2){
                    img = im2;
                }
                if (numImage==3){
                    img = im3;
                }
                if (numImage==4){
                    img = im4;
                }
                    // condition d'arrêt du cycle
                if (step == 9){
                    step=0;
                    this.enable(false, false);
                }
            }
            
            // positionnement de l'image en fonction de sa rotation
            if (orientation == 1){
                activPosition = new Vec2(initPosition.x-((img.getWidth(null)-im0.getWidth(null))/scale),initPosition.y); 
            }
            if(orientation == 2){
                activPosition = new Vec2(initPosition.x,initPosition.y-(img.getHeight(null)-im0.getHeight(null))/scale); 
            }
            if (orientation == 3 || orientation == 0){
                activPosition = initPosition; 
            }
            
            
            // Redimensionnement de la box de collision
            collision_box = new Box(0,0 , img.getWidth(null)/scale,img.getHeight(null)/scale); //la dimension de la box est celle de l'image
            collision_box = collision_box.translateToPosition(activPosition);
            }
            time_next_image = ac_time + image_duration;
        
        
        
        // ---------------affichage de PObject ----------------------      
        canvas.drawImage(img, 
                    (int) (collision_box.p1.x*scale), 
                    (int) (collision_box.p1.y*scale), 
                    (int) ((img.getWidth(null))+collision_box.p1.x*scale),   
                    (int) ((img.getHeight(null))+collision_box.p1.y*scale),  
                    0, 0,
                    img.getWidth(null), img.getHeight(null),
                    null);
        super.render(canvas, scale);
    }
    
    
}
