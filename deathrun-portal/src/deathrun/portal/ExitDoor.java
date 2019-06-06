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
public class ExitDoor extends PObject{
    Box box;
    Box collision_box;
    static Image img;
    final double size = 3;  // diametre de la porte
    final double csize = 1; // largeur hauteur de la boite de collisions
    static final String db_type = "exit";
    
    public ExitDoor(Game game, Vec2 position) throws IOException, SQLException {
        super(game, db_type);
        this.position = position;
        this.box = new Box(position.x, position.y, position.x+size, position.y+size);
        this.collision_box = new Box(position.x+(size-csize)/2, position.y+(size-csize)/2, position.x+(size+csize)/2, position.y+(size+csize)/2);
        
        if (img == null) {
            img = ImageIO.read(new File("./images/porteSortie.png"));
        }
    }
    
    @Override public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        box = box.translateToPosition(pos);
        collision_box = collision_box.translateToPosition(pos.add(new Vec2((size-csize)/2, (size-csize)/2)));
    }
     
    //--------------- interface de gestion des collisions -----------------
    @Override public int collisionable(PObject other)  { return 2; }
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
        if (other instanceof Player) {
            Player player = (Player) other;
            if (!player.disconnected){
                System.out.println("player "+player.name+" reached the door");
                try {
                        PreparedStatement req = g.sync.srv.prepareStatement("UPDATE players SET state=? WHERE id = ?");
                        req.setInt(1, 2); //state = 0 (en vie), 1 (dead), 2 (exit door)
                        // id de l'objet a modifier
                        req.setInt(2, player.db_id);

                        // execution de la requete
                        req.executeUpdate();
                        req.close();
                    }
                    catch (SQLException err) {
                        System.out.println("sql exception:\n"+err);
                    }
                player.hasReachedExitDoor = true;
                g.tryEndRound();
            }
        }
    }
}
