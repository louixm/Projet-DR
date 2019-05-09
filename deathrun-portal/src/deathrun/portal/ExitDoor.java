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
    Box collision_box;
    static Image img;
    final double size = 3;  // diametre de la porte
    final double csize = 1; // largeur hauteur de la boite de collisions
    
    public ExitDoor(Game game, Vec2 position) throws IOException, SQLException {
        super(game);
        this.box = new Box(position.x-size/2, position.y-size/2, position.x+size/2, position.y+size/2);
        this.collision_box = new Box(position.x-csize/2, position.y-csize/2, position.x+csize/2, position.y+csize/2);
        this.position = position;
        
        if (img == null) {
            img = ImageIO.read(new File("./images/porteSortie.png"));
        }
    }
    
    @Override public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        box = box.translateToPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
     
    //--------------- interface de gestion des collisions -----------------
    @Override public int collisionable(PObject other)  { return 2; }
    @Override public Box getCollisionBox()       { return collision_box; }
    
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
            Player player = (Player) other;
            System.out.println("player "+player.name+" reached the door");
            player.hasReachedExitDoor = true;
            g.tryEndRound();
        }
    }
}
