/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import static deathrun.portal.Saw.img;
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

/**
 *
 * @author pdemiche
 */
public class Laser extends Trap {
    static Image img;
    float angle ;
    int typePlateforme;
    int step;
    Vec2 normal ;
    Vec2 vectir; 
    boolean dejaJoue = false;
    
    public Laser(Game game, Vec2 position, int orientation) throws IOException, SQLException {
        super(game, "laser");
        this.collision_box = new Box(0, 0, 1, 1).translate(position);
        this.orientation = orientation;
        this.angle = (float) ((Math.PI/4)*(orientation));

        this.vectir = new Vec2(Math.cos(angle),Math.sin(angle));
        setPosition(position);        
        
        if (img == null) {         
            img = ImageIO.read(new File("./images/laser2.png")); //Laser
        }
    
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
            for (Player p: game.players){
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
                    p.setDead(true);
                    // Player Killed
                }else if(sign(proj3) != sign(proj4) && scal3>0 && scal4>0){
                    p.setDead(true);
                    // Player Killed
                }
            }
        }
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
        }
        if (enabled) {
            final float length = 1000;
            canvas.drawLine(
                    (int) (c.x*scale),
                    (int) (c.y*scale),
                    (int) ((c.x+vectir.x*length)*scale),
                    (int) ((c.y+vectir.y*length)*scale)
            );
        }
        
        AffineTransform transform = new AffineTransform();
        transform.translate(c.x*scale, c.y*scale);
        transform.scale(
                scale*collision_box.getWidth()/img.getWidth(null),
                scale*collision_box.getHeight()/img.getHeight(null)
        );
        transform.rotate(angle, img.getWidth(null)/2,img.getHeight(null)/2);
        // affichage de l'image tournée
        canvas.drawImage(img, 
            transform,
            null);
        //System.out.println(collision_box.center().y);
        super.render(canvas, scale);
        
    }
    
}
