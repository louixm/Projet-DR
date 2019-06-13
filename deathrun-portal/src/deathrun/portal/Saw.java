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
    Box collision_box, box;
    static Image img;
    int typePlateforme;
    int step;
    static final String db_type = "saw";
    static float size = 2.1f, csize = 1.8f;
    
    public Saw(Game game, Vec2 position) throws IOException, SQLException {this(game,position,-1);}
    public Saw(Game game, Vec2 position, int db_id) throws IOException, SQLException {
        super(game, db_type, db_id);
        this.box = new Box(position.x, position.y, position.x+size, position.y+size);
        this.collision_box = new Box(position.x+(size-csize)/2, position.y+(size-csize)/2, position.x+(size+csize)/2, position.y+(size+csize)/2);
        setPosition(position);
        
        if (img == null) {
            img = ImageIO.read(new File("./images/saw.png")); //scie circulaire
        }

    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        box = box.translateToPosition(pos);
        Vec2 cpos = new Vec2(position.x+(size-csize)/2, position.y+(size-csize)/2);
        collision_box = collision_box.translateToPosition(cpos);
    }
    
    //--------------- interface de gestion des collisions -----------------
    @Override
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?2:0;
    }
    @Override
    public Box getCollisionBox()       { return collision_box; }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D canvas, float scale) {
        // incrementer le compteur de frame
        step ++;
        // assembler la matrice de transformation (scale,rotation,translation)
        Vec2 c = box.p1;
        AffineTransform transform = new AffineTransform();
        transform.translate(c.x*scale, c.y*scale);
        transform.scale(
                scale*box.getWidth()/img.getWidth(null),
                scale*box.getHeight()/img.getHeight(null)
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
        ((Player)other).setDead(true);
    }
}
