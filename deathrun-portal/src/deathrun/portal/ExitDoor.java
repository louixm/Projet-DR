package deathrun.portal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;
/**
 *
 * @author trazafit
 */
public class ExitDoor extends PObject{
    Box box;
    static Image img;
    final double size = 3;
    
    public ExitDoor(Game game, Vec2 position) throws IOException, SQLException {
        super(game);
        this.box = new Box(position.x-size/2, position.y-size/2, position.x+size/2, position.y+size/2);
        this.position = position;
        
        if (img == null) {
            img = ImageIO.read(new File("./images/porteSortie.png"));
        }
    }
    
    @Override public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        box = box.translateToPosition(pos);
    }
     
    //--------------- interface de gestion des collisions -----------------
    @Override public int collisionable(PObject other)  { return 2; }
    @Override public Box getCollisionBox()       { return box; }
    
    //--------------- interface d'affichage -----------------
    @Override public void render(Graphics2D canvas, float scale) {
        super.render(canvas, scale);
        canvas.drawImage(img, 
                (int) (box.p1.x*scale), 
                (int) (box.p1.y*scale), 
                (int) (box.p2.x*scale), 
                (int) (box.p2.y*scale), 
                0, 0,
                img.getWidth(null), img.getHeight(null),
                null);
    }
    
    @Override public void onCollision(Game g, PObject other) {
        if (other instanceof Player) {
            System.out.println("player "+((Player)other).name+" reached the door");
        }
    }
}
