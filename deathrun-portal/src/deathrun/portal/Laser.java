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

/**
 *
 * @author pdemiche
 */
public class Laser extends PObject {
    private Game game ;
    Box collision_box;
    static Image img;
    float angle ;
    int typePlateforme;
    int step;
    Vec2 normal ;
    Vec2 vectir; 
    
    
    public Laser(Game game, Vec2 position, float angle) throws IOException, SQLException {
        super(game);
        this.collision_box = new Box(0, 0, 1, 1).translate(position);
        this.angle = angle;

        //this.vectir = new Vec2(Math.cos(angle),Math.sin(angle));
        this.vectir = new Vec2(1,0);
        //this.normal = new Vec2(-Math.sin(angle),Math.cos(angle));
        this.normal = new Vec2(0,1);
        setPosition(position);
        
        
        if (img == null) {         
            img = ImageIO.read(new File("./images/Barrel (1).png")); //Laser
        }
    
    }
    
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
    
    public int sign(double nombre){
        if(nombre < 0){
            return -1;
        }else{
            return +1;
        }
    }
    
    //--------------- interface de gestion des collisions -----------------
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?1:0;
    }
    @Override
    public Box getCollisionBox()       { return collision_box; }
    
    @Override
    public void onGameStep(Game g, float dt) {
        System.out.println("Player Killed");
        for (Player p: game.players){
            Vec2 p1 = p.getCollisionBox().p1 ; //point inférieur gauche
            Vec2 p2 = p.getCollisionBox().p2 ; //point supérieur droit
            Vec2 p3 = new Vec2(p1.x, p2.y) ; // point supérieur gauche
            Vec2 p4 = new Vec2(p2.x , p1.y) ; // point supérieur gauche
            double proj1 = p1.sub(collision_box.center()).vect(this.normal) ;
            double proj2 = p2.sub(collision_box.center()).vect(this.normal) ;
            double proj3 = p3.sub(collision_box.center()).vect(this.normal) ;
            double proj4 = p4.sub(collision_box.center()).vect(this.normal) ;
            System.out.println(proj1);
            if (this.sign(proj1) != this.sign(proj2)){
                System.out.println("Player Killed");
                // Player Killed
            }else if(this.sign(proj3) != this.sign(proj4)){
                System.out.println("Player Killed");
                // Player Killed
            }
        }
    }
    
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D canvas, float scale) {
        /*
        canvas.drawImage(img[typePlateforme], 
                (int) (position.x*scale), 
                (int) (position.y*scale), 
                null);    */
        int scale1 = Math.round(scale); 
        canvas.drawImage(img, 
                (int) (collision_box.p1.x*scale), 
                (int) (collision_box.p1.y*scale), 
                (int) (collision_box.p2.x*scale), 
                (int) (collision_box.p2.y*scale), 
                0, 0,
                img.getWidth(null), img.getHeight(null),
                null);
        canvas.drawLine((int) collision_box.center().x*scale1,(int) collision_box.center().y*scale1,(int) (collision_box.center().x*scale1+vectir.mul(1000).x*scale1),(int) (collision_box.center().y*scale1+vectir.mul(1000).y*scale1));
        //System.out.println(collision_box.center().y);
        super.render(canvas, scale);
        
    }
    
}
