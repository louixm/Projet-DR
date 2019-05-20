/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import static com.mysql.cj.core.io.ExportControlled.enabled;
import static deathrun.portal.Platform.img;
import static deathrun.portal.Saw.img;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
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
    static Image img;
    int typePlateforme;
    int step;
    Vec2 initPosition;   //posiion initiale
    Vec2 activPosition;   //position quand le piège est sous controle
    
    public Punch(Game game, Vec2 position) throws IOException, SQLException {
        super(game);
        
        this.initPosition = position;
        /*if (img == null) {
            img = ImageIO.read(new File("./images/punch0.png")); //poinçon denté
        }*/
        Image im = ImageIO.read(new File("./images/punch.png"));
        collision_box = new Box(0,0,im.getWidth(null)/36f,im.getHeight(null)/36f); //36 = scale & a dimension de la box est celle de l'image
        //Vec2 realPos = new Vec2(position.x-(collision_box.getWidth()/2),position.y-(collision_box.getHeight()/2));   //pour centrer la box 
        /*collision_box = collision_box.translateToPosition(initPosition);
        setPosition(initPosition);*/
        Image im0 = ImageIO.read(new File("./images/punch0.png"));
        this.activPosition = new Vec2(initPosition.x-((im0.getWidth(null)/(2*36f))-(im.getWidth(null)/(2*36f))),initPosition.y);
        
        
    }
    
    /*@Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }*/
    
    //--------------- interface de gestion des collisions -----------------
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?1:0;
    }
    /*@Override
    public Box getCollisionBox()       { return collision_box; }
    //--------------- interface de gestion des collisions -----------------*/
    
    
    
    
    @Override
    public void onGameStep(Game game, float dt) {
        if (enabled) {
            
            for (Player p: game.players){
                Vec2 p1 = p.getCollisionBox().p1 ; //point supérieur gauche
                Vec2 p2 = p.getCollisionBox().p2 ; //point inférieur droit
                Vec2 p3 = new Vec2(p1.x, p2.y) ; // point inférieur gauche
                Vec2 p4 = new Vec2(p2.x , p1.y) ; // point supérieur droit
                if ((collision_box.p1.x <= p4.x && p1.x<=collision_box.p2.x) && p1.y == collision_box.p2.y){  
                    p.setDead(true);
                    // Player Killed
                }else{
                    p.setDead(false);
                }
            }
        }
    }
    
    
    
    //-------------------Interface d'affichage--------------------------------
    
    @Override
    public void render(Graphics2D canvas, float scale) {
        
        // (comment savoir) si le pège n'est en pocession de personne, on prend l'img "punch"
        if (false){  
            try {
                img = ImageIO.read(new File("./images/punch.png"));
            } catch (IOException ex) {
                Logger.getLogger(Punch.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // Redimensionnement de la box de collision
            collision_box = new Box(0,0 , img.getWidth(null)/scale,img.getHeight(null)/scale); //la dimension de la box est celle de l'image
            //Vec2 realPos = new Vec2(position.x-(collision_box.getWidth()/2),position.y-(collision_box.getHeight()/2));   //pour centrer la box 
            collision_box = collision_box.translateToPosition(initPosition);
            setPosition(initPosition);
        } 
        
        // (comment savoir) si le piège appartient à un joueur
        else{   
            
            // -----------------MAJ de l'image---------------------------
            
            // si le piège n'est pas activé on met l'image "punch0"
            if (!enabled){     
                try {
                    img = ImageIO.read(new File("./images/punch0.png"));
                } catch (IOException ex) {
                    Logger.getLogger(Punch.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        
                
                try {
                    img = ImageIO.read(new File("./images/punch"+numImage+".png"));
                } catch (IOException ex) {
                    Logger.getLogger(Punch.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                // (comment avoir un seul cycle pas appui)condition d'arrêt du cycle
                if (step == 9){
                    step=0;
                }
                
                
        
            }
            // Redimensionnement de la box de collision
            collision_box = new Box(0,0 , img.getWidth(null)/scale,img.getHeight(null)/scale); //la dimension de la box est celle de l'image
            //Vec2 realPos = new Vec2(position.x-(collision_box.getWidth()/2),position.y-(collision_box.getHeight()/2));   //pour centrer la box 
            collision_box = collision_box.translateToPosition(activPosition);
            setPosition(activPosition);
        }
        
        // ---------------affichage de PObject ----------------------      
        canvas.drawImage(img, 
                    (int) (collision_box.p1.x*scale), 
                    (int) (collision_box.p1.y*scale), 
                    (int) ((img.getWidth(null))+collision_box.p1.x*scale),   //+collision_box.p2.x*scale        REVOIR!!!!!!!!!!!!!
                    (int) ((img.getHeight(null))+collision_box.p1.y*scale),  //+collision_box.p2.y*scale
                    0, 0,
                    img.getWidth(null), img.getHeight(null),
                    null);
        super.render(canvas, scale);
    }
    
    
}
