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

/**
 *
 * @author ydejongh
 */
public class Platform extends PObject {
    Box collision_box;
    static Image img[];
    int typePlateforme;
    
    public Platform(int db_id, Vec2 position, Box box, int typePlateforme) throws IOException {
        super(db_id);
        this.collision_box = box;
        setPosition(position);
        this.typePlateforme = typePlateforme;
        
        if (img == null) {
            img = new Image[6];
            img[0] = ImageIO.read(new File("./images/patterns/Tile (5).png")); //plateforme 1x1
            img[1] = ImageIO.read(new File("./images/patterns/Tile (16).png")); //plateforme 1x3
            img[2] = ImageIO.read(new File("./images/patterns/Tile (13).png")); //plateforme 1/2x1
            img[3] = ImageIO.read(new File("./images/patterns/Tile (17).png")); //plateforme 1/2x3
            img[4] = ImageIO.read(new File("./images/Barrel_1_3.png"));
            img[5] = ImageIO.read(new File("./images/Barrel_2_4.png"));
        }
    }
    
    public Platform(int db_id, Vec2 position, double width, double height, int typePlateforme) throws IOException {
        this(db_id, position, new Box(0, 0, width, height),typePlateforme);
    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
     
    //--------------- interface de gestion des collisions -----------------
    public boolean collisionable(PObject other)  { 
        return (other instanceof Player);
    }
    @Override
    public Box getCollisionBox()       { return collision_box; }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D canvas, float scale) {
        /*
        canvas.drawImage(img[typePlateforme], 
                (int) (position.x*scale), 
                (int) (position.y*scale), 
                null);    */
        canvas.drawImage(img[typePlateforme], 
                (int) (collision_box.p1.x*scale), 
                (int) (collision_box.p1.y*scale), 
                (int) (collision_box.p2.x*scale), 
                (int) (collision_box.p2.y*scale), 
                0, 0,
                img[typePlateforme].getWidth(null), img[typePlateforme].getHeight(null),
                null);
        super.render(canvas, scale);
        // TODO
    }
    
}
