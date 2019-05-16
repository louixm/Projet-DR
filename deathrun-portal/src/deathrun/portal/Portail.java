package deathrun.portal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.imageio.ImageIO;
/**
 *
 * @author trazafit
 */
public class Portail extends PObject{
    Box box;
    Box collision_box;
    static Image img;
    final double size = 3;  // diametre de la porte
    final double csize = 1; // largeur hauteur de la boite de collisions
    Portail otherPortail;
    
    public Portail(Game game, Vec2 position) throws IOException, SQLException {
        super(game);
        this.position = position;
        this.box = new Box(position.x, position.y, position.x+size, position.y+size);
        this.collision_box = new Box(position.x+(size-csize)/2, position.y+(size-csize)/2, position.x+(size+csize)/2, position.y+(size+csize)/2);
        
        if (img == null) {
            img = ImageIO.read(new File("./images/portail.png"));
        }
    }
    
    @Override public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        box = box.translateToPosition(pos);
        collision_box = collision_box.translateToPosition(pos.add(new Vec2((size-csize)/2, (size-csize)/2)));
    }
     
    //--------------- interface de gestion des collisions -----------------
    @Override public int collisionable(PObject other)  { 
        if (other instanceof Player) return 2; 
        else return 0; 
    }
    @Override public Box getCollisionBox()       { return collision_box; }
    
    //--------------- interface d'affichage -----------------
    @Override public void render(Graphics2D canvas, float scale) {
        canvas.drawImage(img, 
                (int) (box.p1.x*scale), 
                (int) (box.p1.y*scale), 
                (int) (box.p2.x*scale), 
                (int) (box.p2.y*scale), 
                0, 0,
                img.getWidth(null), img.getHeight(null),
                null);
        super.render(canvas, scale);
    }
    
    @Override public void onCollision(Game g, PObject other) {
            other.setPosition(otherPortail.position.add(new Vec2(2,2)));
                }            
    }
