/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pdemiche
 */
public class Laser extends Trap {
    static Image img_base;
    static Image img_center;
    float angle ;
    int typePlateforme;
    int step;
    Vec2 normal ;
    Vec2 vectir; 
    Vec2 ray_stop;
    
    boolean dejaJoue = false;
    int counter =0;
    Game game;
    
    
    public Laser(Game game, Vec2 position, int orientation) throws IOException, SQLException {
        super(game, "laser");
        this.game = game;
        this.collision_box = new Box(0, 0, 1, 1).translate(position);
        this.orientation = orientation;
        this.angle = (float) ((Math.PI/4)*(orientation));
        System.out.println("orientation "+orientation+"   angle "+angle);

        setPosition(position);  
        
        if (img_center == null || img_base == null) {
            img_base = ImageIO.read(new File("./images/laser_base.png"));
            img_center = ImageIO.read(new File("./images/laser_centre.png"));
        }
    
    }
    
    @Override
    public void setOrientation(int orientation){
        this.orientation = orientation;
        this.angle = (float) ((Math.PI/4)*(orientation));
    }
    
    //--------------- interface de gestion des collisions -----------------
    @Override
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?1:0;
    }
    
    
    public static int sign(double nombre){
        if(nombre < 0)  return -1;
        else            return +1;
    }
    
    @Override
    public void onGameStep(Game game, float dt) {
        if (enabled) {
            Vec2 start = collision_box.center();
            double length = ray_stop.sub(start).dot(vectir);
            for (Player p: game.players){
                /*
                Vec2 p1 = p.getCollisionBox().p1 ; //point inférieur gauche
                Vec2 p2 = p.getCollisionBox().p2 ; //point supérieur droit
                Vec2 p3 = new Vec2(p1.x, p2.y) ; // point inférieur gauche
                Vec2 p4 = new Vec2(p2.x , p1.y) ; // point supérieur gauche
                double proj1 = p1.sub(collision_box.center()).cross(this.vectir) ;
                double proj2 = p2.sub(collision_box.center()).cross(this.vectir) ;
                double proj3 = p3.sub(collision_box.center()).cross(this.vectir) ;
                double proj4 = p4.sub(collision_box.center()).cross(this.vectir) ;
                double scal1 = p1.sub(collision_box.center()).dot(this.vectir) ;
                double scal2 = p2.sub(collision_box.center()).dot(this.vectir) ;
                double scal3 = p3.sub(collision_box.center()).dot(this.vectir) ;
                double scal4 = p4.sub(collision_box.center()).dot(this.vectir) ;

                if (sign(proj1) != sign(proj2) && scal1>0 && scal2>0){
                    game.addScoreUponTrapKill(this, p);
                    p.setDead(true);
                    
                    // Player Killed
                }else if(sign(proj3) != sign(proj4) && scal3>0 && scal4>0){
                    p.setDead(true);
                    game.addScoreUponTrapKill(this, p);
                    // Player Killed
                }
                */
                Vec2 intersection = p.getCollisionBox().intersectionFirstBorder(start, vectir);
                if (intersection != null) {
                    double distance = intersection.sub(start).dot(vectir);
                    if (distance > 0 && distance <= length) {
                        game.addScoreUponTrapKill(this, p);
                        p.setDead(true);
                    }
                }
            }
        }
    }
    
    @Override
    void enable(boolean enable, boolean withsync) {
        super.enable(enable, withsync);
        if (enable)     update_internals();
    }
    
    void update_internals() {
        vectir = new Vec2(Math.cos(angle),Math.sin(angle));
        
        Vec2 start = collision_box.center();
        double nearest = 1e20;
        Vec2 stop = vectir.mul(1000);
        for (PObject obj : game.objects.values()) {
            if (obj instanceof Platform) {
                Vec2 intersection = obj.getCollisionBox().intersectionFirstBorder(start, vectir);
                if (intersection != null) {
                    double distance = vectir.dot(intersection.sub(start));
                    if (distance >= 0 && distance < nearest) {
                        nearest = distance;
                        stop = intersection;
                    }
                }
            }
        }
        
        ray_stop = stop;
    }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D canvas, float scale) {
        canvas.setColor(Color.red);
        Vec2 c = collision_box.center();    // centre le l'objet
        SoundPlayer starwars = new SoundPlayer("laser.mp3", false);
        
        
        if (enabled && dejaJoue == false) {
            dejaJoue = true;
            starwars.play();
        }
        if (!enabled){
            dejaJoue = false;
            counter=0;
        }
        if (counter>100) {
            enabled=false;
        }
        
        // affichage de la base
        final double base_w = 1;
        final double base_h = 1;
        
        canvas.drawImage(img_base, 
                (int) ((c.x-base_w)*scale), 
                (int) ((c.y-base_h)*scale), 
                (int) ((c.x+base_w)*scale), 
                (int) ((c.y+base_h)*scale),
                0, 0,
                img_base.getWidth(null), img_base.getHeight(null),
                null);
        
        // affichage du rayon
        if (enabled) {
            counter++;
            canvas.drawLine(
                    (int) (c.x*scale),
                    (int) (c.y*scale),
                    (int) (ray_stop.x*scale),
                    (int) (ray_stop.y*scale)
            );
            /*
            final float length = 1000;
            canvas.drawLine(
                    (int) (c.x*scale),
                    (int) (c.y*scale),
                    (int) ((c.x+vectir.x*length)*scale),
                    (int) ((c.y+vectir.y*length)*scale)
            );
            */
        }
        
        // affichage de la tete
        double box_w = scale*collision_box.getWidth();
        double box_h = scale*collision_box.getHeight();
        AffineTransform transform = new AffineTransform();
        transform.translate(c.x*scale - box_w/2, c.y*scale - box_h/2);
        transform.scale(
                box_w/img_center.getWidth(null),
                box_h/img_center.getHeight(null));
        transform.rotate(angle, 
                img_center.getWidth(null)/2, 
                img_center.getHeight(null)/2);
        
        canvas.drawImage(img_center, transform, null);
        
        //System.out.println(collision_box.center().y);
        super.render(canvas, scale);
        
    }
    
}
