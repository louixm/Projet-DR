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
    static Image img;
    
    public Platform(int db_id, Vec2 position, Box box) throws IOException {
        super(db_id);
        this.collision_box = box;
        setPosition(position);
        
        if (img == null)
            img = ImageIO.read(new File("./images/patterns/Tile (5).png"));
    }
    
    public Platform(int db_id, Vec2 position, double width, double height) throws IOException {
        this(db_id, position, new Box(0, 0, width, height));
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
    public void render(Graphics2D g, float scale) {
        super.render(g, scale);
        Box collision_box = getCollisionBox();
        //g.drawRect(img,); // A finir
        
        // TODO
    }
    
}
