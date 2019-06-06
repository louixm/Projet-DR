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
public class Acid extends PObject {
    Box collision_box;
    Platform platform_g;
    Platform platform_m1;
    Platform platform_m2;
    Platform platform_d;
    static Image img;
    static Image im;
    static Image im0;
    static Image im1;
    static Image im2;
    static Image im3;
    int typePlateforme;
    int step;
    Vec2 position;
    double div=4;
    double decalage = 50; // décalage en pixel entre la box de collision et l'image avec la vapeur
    //Vec2 position;
    static final String db_type = "acid";
    long time_next_image;
    long image_duration = 35000000;
    
    
    public Acid(Game game, Vec2 position) throws IOException, SQLException {
        super(game, db_type);
        if (img == null) {
            img = ImageIO.read(new File("./images/Acid_i.png")); //Acid
        }
        this.position = position;
        im = ImageIO.read(new File("./images/Tile_10.png"));
        im0 = ImageIO.read(new File("./images/Acid_0.png"));
        im1 = ImageIO.read(new File("./images/Acid_1.png"));
        im2 = ImageIO.read(new File("./images/Acid_2.png"));
        im3 = ImageIO.read(new File("./images/Acid_3.png"));
        
        Vec2 pos = new Vec2 (position.x+im.getHeight(null)/(div*36f),position.y);
        this.collision_box = new Box(0, 0, im.getWidth(null)/(div*36f), im.getWidth(null)/(div*36f)).translateToPosition(pos);
        
        Vec2 positionG = position;
        Vec2 positionM1 = new Vec2(position.x,position.y+im.getWidth(null)/(div*36f));
        Vec2 positionM2 = new Vec2(position.x+im.getWidth(null),position.y+im.getWidth(null)/(div*36f));
        Vec2 positionD = new Vec2(position.x+(im.getWidth(null)+im.getHeight(null))/(div*36f),position.y);
        
        this.platform_g = new Platform(game,positionG, im.getHeight(null)/(div*36f), im.getWidth(null)/(div*36f),9); // 36 == scale
        this.platform_m1 = new Platform(game,positionM1, im.getWidth(null)/(div*36f), im.getHeight(null)/(div*36f),7);
        this.platform_m2 = new Platform(game,positionM2, im.getWidth(null)/(div*36f), im.getHeight(null)/(div*36f),7);
        this.platform_d = new Platform(game,positionD, im.getHeight(null)/(div*36f), im.getWidth(null)/(div*36f),8);
        
        long ac_time = System.nanoTime();
        time_next_image = ac_time + image_duration;
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
            if ((p3.x<(platform_d.position.x) && p2.x>(platform_g.position.x + im.getHeight(null)/(div*36f))) && p2.y == platform_g.position.y){
                p.setDead(true);
                    // Player Killed
            }
        }
    }
    
    
    
    //-------------------Interface d'affichage--------------------------------
    
    @Override
    public void render(Graphics2D canvas, float scale) {
        
          // -----------------MAJ de l'image---------------------------
            
        long ac_time = System.nanoTime();
        if (ac_time > time_next_image)  {   
                
                // incrementer le compteur de frame
            step ++;
                
                // évolution du numéro de l'immage
            int fourMulticator = (int)step/4;
            int numImage;
            if (fourMulticator % 2 == 0){
                numImage = step-(4*fourMulticator);
            }else{
                numImage = 3-(step-(4*fourMulticator));
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
                
        } 
            
        time_next_image = ac_time + image_duration;
        // ---------------affichage de PObject ----------------------      
        canvas.drawImage(img, 
                    (int) (position.x*scale), 
                    (int) ((position.y*scale)-(decalage/div)), 
                    (int) (position.x*scale + im0.getWidth(null)/div),   
                    (int) (position.y*scale + im0.getHeight(null)/div),  
                    0, 0,
                    img.getWidth(null), img.getHeight(null),
                    null);
        super.render(canvas, scale);
    }
}
