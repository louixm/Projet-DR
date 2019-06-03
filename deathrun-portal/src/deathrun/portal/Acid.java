/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import static deathrun.portal.Punch.img;
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
public class Acid extends Trap {
    //Box collision_box;
    static Image img;
    static Image im;
    int typePlateforme;
    int step;
    double div=4;
    static final String db_type = "acid";
    
    
    public Acid(Game game, Vec2 position) throws IOException, SQLException {
        super(game, db_type);
        if (img == null) {
            img = ImageIO.read(new File("./images/Acid_1.png")); //Acid
        }
        
        //div = 3;
        this.collision_box = new Box(0, 0, img.getWidth(null)/(div*36f), img.getHeight(null)/(div*36f)).translate(position);
        setPosition(position);
        
        im = ImageIO.read(new File("./images/Tile_10.png"));
    
    }
    
    //--------------- interface de gestion des collisions -----------------
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
            if ((p2.x<(collision_box.p2.x - im.getHeight(null)/(div*36f)) && p3.x>(collision_box.p1.x + im.getHeight(null)/(div*36f))) && p2.y == collision_box.p1.y){
                p.setDead(true);
                    // Player Killed
            }else{
                p.setDead(false);
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
