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
 * @author trazafit
 */
public class ExitDoor extends PObject{
    Box box;
    static Image img[];
    int typePlateforme;
    
    public ExitDoor(int db_id, Vec2 position, Box box) throws IOException {
        super(db_id);
        this.box = box;
        setPosition(position);
        this.typePlateforme = typePlateforme;
        
        if (img == null) {
            img = new Image[1];
//            img[0] = ImageIO.read(new File("./images/porteSortie.png"));
        }
    }
    
    public ExitDoor(int db_id, Vec2 position, double width, double height) throws IOException {
        this(db_id, position, new Box(0, 0, width, height));
    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        box = box.translateToPosition(pos);
    }
     
    //--------------- interface de gestion des collisions -----------------
    public boolean collisionable(PObject other)  { 
        return false;
    }
    @Override
    public Box getCollisionBox()       { return null; }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D canvas, float scale) {
//        canvas.drawImage(img[typePlateforme], 
//                (int) (box.p1.x*scale), 
//                (int) (box.p1.y*scale), 
//                (int) (box.p2.x*scale), 
//                (int) (box.p2.y*scale), 
//                0, 0,
//                img[typePlateforme].getWidth(null), img[typePlateforme].getHeight(null),
//                null);
//        super.render(canvas, scale);
        // TODO
    }
    
}
