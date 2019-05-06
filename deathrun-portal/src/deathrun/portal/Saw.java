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

/**
 *
 * @author pdemiche
 */
public class Saw extends PObject {
    Box collision_box;
    static Image img;
    int typePlateforme;
    int step;
    
    public Saw(Game game, Vec2 position) throws IOException, SQLException {
        super(game);
        this.collision_box = new Box(-1, -1, 1, 1).translate(position);
        setPosition(position);
        
        if (img == null) {
            img = ImageIO.read(new File("./images/saw.png")); //scie circulaire
        }
    
    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
    
    //--------------- interface de gestion des collisions -----------------
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?1:0;
    }
    @Override
    public Box getCollisionBox()       { return collision_box; }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D canvas, float scale) {
        // incrementer le compteur de frame
        step ++;
        // assembler la matrice de transformation (scale,rotation,translation)
        Vec2 c = collision_box.p1;
        AffineTransform transform = new AffineTransform();
        transform.translate(c.x*scale, c.y*scale);
        transform.scale(
                scale*collision_box.getWidth()/img.getWidth(null),
                scale*collision_box.getHeight()/img.getHeight(null)
        );
        transform.rotate(Math.PI / 40 * step,img.getWidth(null)/2,img.getHeight(null)/2);
        // affichage de l'image tournée
        canvas.drawImage(img, 
            transform,
            null);
        // affichage de PObject
        super.render(canvas, scale);
    }
    
    @Override
    public void onCollision(Game g, PObject other) {
        System.out.println("player "+((Player)other).name+" is dead");
    }
}
