/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

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
    static Image im[];
    int typePlateforme;
    
    int step;
    int increment;
    
    Vec2 position;
    double largeurImage=512;
    double decalage = 50; // décalage en pixel entre la box de collision et l'image avec la vapeur
    static final String db_type = "acid";
    long time_next_image;
    long image_duration = 400000000;
    
    
    public Acid(Game game, double taille, Vec2 position) throws IOException, SQLException {
        super(game, db_type);
        if (img == null) {
            img = ImageIO.read(new File("./images/Acid_i.png")); //Acid
        }
        
        im = new Image[] {
            ImageIO.read(new File("./images/Tile_10.png")),
            ImageIO.read(new File("./images/Acid_0.png")),
            ImageIO.read(new File("./images/Acid_1.png")),
            ImageIO.read(new File("./images/Acid_2.png")),
            ImageIO.read(new File("./images/Acid_3.png"))
        };
        double largeur = taille;
        double hauteur = largeur*3/4;
        
        
        this.collision_box = new Box(0, 0, largeur, largeur).translateToPosition(position);
        Vec2 pos = new Vec2 (position.x-(collision_box.getWidth()/2),position.y);
        
        this.position = pos;
        
        Vec2 positionG = pos;
        Vec2 positionM1 = new Vec2(pos.x,pos.y+(collision_box.getHeight()));
        Vec2 positionM2 = new Vec2(positionM1.x+(collision_box.getWidth()),positionM1.y);
        Vec2 positionD = new Vec2(positionM2.x+(collision_box.getWidth()/2),positionG.y);
        
        this.platform_g = new Platform(game,positionG, collision_box.getWidth()/2, collision_box.getHeight(),9); 
        this.platform_m1 = new Platform(game,positionM1, collision_box.getWidth(), collision_box.getHeight()/2,7);
        this.platform_m2 = new Platform(game,positionM2, collision_box.getWidth(), collision_box.getHeight()/2,7);
        this.platform_d = new Platform(game,positionD, collision_box.getWidth()/2, collision_box.getHeight(),8);
        
        long ac_time = System.nanoTime();
        time_next_image = ac_time + image_duration;
        
        step = 1;
        increment = 1;
    }
    
    //--------------- interface de gestion des collisions -----------------
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?2:0;
    }
    //--------------- interface de gestion des collisions -----------------*/
    
    @Override
    public Box getCollisionBox()  { return collision_box; }
    
    public void onCollision(Game g, PObject other) {
        ((Player)other).setDead(true);
    }
    
    /*@Override
    public void onGameStep(Game game, float dt) {
        for (Player p: game.players){
            Vec2 p1 = p.getCollisionBox().p1 ; //point supérieur gauche
            Vec2 p2 = p.getCollisionBox().p2 ; //point inférieur droit
            Vec2 p3 = new Vec2(p1.x, p2.y) ; // point inférieur gauche
            Vec2 p4 = new Vec2(p2.x , p1.y) ; // point supérieur droit
            if ((p3.x<(platform_d.position.x) && p2.x>(platform_g.position.x + collision_box.getWidth()/2)) && p2.y == platform_g.position.y){
                p.setDead(true);
                    // Player Killed
            }
        }
    }*/
    
    
    
    //-------------------Interface d'affichage--------------------------------
    
    @Override
    public void render(Graphics2D canvas, float scale) {
        
          // -----------------MAJ de l'image---------------------------
            
        long ac_time = System.nanoTime();
        if (ac_time > time_next_image)  {   
                
                // incrementer le compteur de frame
            step += increment;
                
                // évolution du numéro de l'immage
           
            if (step == 4) { increment = -1; }
            else if (step == 1) { increment = 1; }
            
            img = im[step];
            time_next_image = ac_time + image_duration;
        } 
            
        double ratio = (collision_box.getWidth()*scale)/largeurImage;
        // ---------------affichage de PObject ----------------------      
        canvas.drawImage(img, 
                    (int) (platform_g.position.x*scale), 
                    (int) ((platform_g.position.y*scale)-(decalage*ratio)), 
                    (int) ((platform_d.position.x+collision_box.getWidth()/2)*scale),   
                    (int) ((platform_m1.position.y+collision_box.getHeight()/2)*scale),  
                    0, 0,
                    img.getWidth(null), img.getHeight(null),
                    null);
        super.render(canvas, scale);
    }
}
