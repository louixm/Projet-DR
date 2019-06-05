/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import static deathrun.portal.Acid.db_type;
import static deathrun.portal.Punch.img;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;

/**
 *
 * @author kbenie
 */
public class Spikes extends PObject {
    Box collision_box;
    static Image img;
    double div=1;
    double sens;
    static final String db_type = "spikes";
    
    public Spikes(Game game,double sens, Vec2 position) throws IOException, SQLException {
        super(game, db_type);
        this.position = position;
        this.sens = sens;
        if(sens==0){
            img = ImageIO.read(new File("./images/Spikes_0.png")); //spikes vers le bas
        }
        if(sens==1){
            img = ImageIO.read(new File("./images/Spikes_1.png")); //spikes vers la gauche
        }
        if(sens == 2){
            img = ImageIO.read(new File("./images/Spikes_2.png")); //spikes vers le haut
        }
        if(sens == 3){
            img = ImageIO.read(new File("./images/Spikes_3.png")); //spikes vers la droite
        }
        collision_box = new Box(0,0 , img.getWidth(null)/36f,img.getHeight(null)/36f); //la dimension de la box est celle de l'image
        collision_box = collision_box.translateToPosition(position);
        
        
    }
    
    //--------------- interface de gestion des collisions -----------------
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?1:0;
    }
    //--------------- interface de gestion des collisions -----------------*/
    
    @Override
    public Box getCollisionBox()  { return collision_box; }
    
    
    
    
    @Override
    public void onGameStep(Game game, float dt) {
        for (Player p: game.players){
            Vec2 p1 = p.getCollisionBox().p1 ; //point supérieur gauche
            Vec2 p2 = p.getCollisionBox().p2 ; //point inférieur droit
            Vec2 p3 = new Vec2(p1.x, p2.y) ; // point inférieur gauche
            Vec2 p4 = new Vec2(p2.x , p1.y) ; // point supérieur droit
            if (sens == 0){ // bas
                if ((collision_box.p1.x <= p4.x && p1.x<=collision_box.p2.x) && p1.y == collision_box.p2.y){  
                    p.setDead(true);
                    // Player Killed
                }
            }
            if (sens == 1){ // gauche
                if ((collision_box.p1.y <= p2.y && p4.y<=collision_box.p2.y) && p2.x == collision_box.p1.x){  
                    p.setDead(true);
                    // Player Killed
                }
            }
            if (sens == 2){ // haut
                if ((collision_box.p1.x <= p4.x && p1.x<=collision_box.p2.x) && p2.y == collision_box.p1.y){  
                    p.setDead(true);
                    // Player Killed
                }
            }
            if (sens == 3){ // droit
                if ((collision_box.p1.y <= p2.y && p4.y<=collision_box.p2.y) && p1.x == collision_box.p2.x){  
                    p.setDead(true);
                    // Player Killed
                }
            }
        }
    }
    
    
    
    //-------------------Interface d'affichage--------------------------------
    
    @Override
    public void render(Graphics2D canvas, float scale) {
         
        // ---------------affichage de PObject ----------------------      
        canvas.drawImage(img, 
                    (int) (collision_box.p1.x*scale), 
                    (int) (collision_box.p1.y*scale), 
                    (int) (collision_box.p2.x*scale),   
                    (int) (collision_box.p2.y*scale),  
                    0, 0,
                    img.getWidth(null), img.getHeight(null),
                    null);
        super.render(canvas, scale);
    }
}
