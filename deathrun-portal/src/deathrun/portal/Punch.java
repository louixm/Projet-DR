/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import static deathrun.portal.Platform.img;
import static deathrun.portal.Saw.img;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author kbenie
 */
public class Punch extends PObject {
    Box collision_box;
    static Image img;
    int typePlateforme;
    int step;
    Player player; 
    
    public Punch(Game game, Vec2 position) throws IOException, SQLException {
        super(game);
        this.collision_box = new Box(-1, -1, 1, 1).translate(position);
        setPosition(position);
        
        if (img == null) {
            img = ImageIO.read(new File("./images/punch_1.png")); //poinçon denté
        }
    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
    
    //--------------- interface de gestion des collisions -----------------
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?2:0;
    }
    @Override
    public Box getCollisionBox()       { return collision_box; }
    
    
    @Override
    public void render(Graphics2D canvas, float scale) {
        // incrementer le compteur de frame
        step ++;
        //int i = (int) Math.cos(step*2*Math.PI/6); //step=6*k
        int fiveMulticator = (int)step/5;
        /*if (step%6 == 0){
            sixMulticator = step/6; 
        }*/
        int numImage;
        if (fiveMulticator % 2 == 0){
            numImage = step-(5*fiveMulticator)+1;
        }else{
            numImage = 5-(step-(5*fiveMulticator));
        }
        try {
            img = ImageIO.read(new File("./images/punch_"+numImage+".png"));
        } catch (IOException ex) {
            Logger.getLogger(Punch.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*if (numImage==1){
            canvas.drawImage(img, 
                    (int) (collision_box.p1.x*scale), 
                    (int) (collision_box.p1.y*scale), 
                    (int) (collision_box.p2.x*scale), 
                    (int) ((collision_box.p2.y)*scale), 
                    0, 0,
                    img.getWidth(null), img.getHeight(null),
                    null);
        }else{*/
            canvas.drawImage(img, 
                    (int) (collision_box.p1.x*scale), 
                    (int) (collision_box.p1.y*scale), 
                    (int) (collision_box.p2.x*scale), 
                    (int) ((collision_box.p2.y+(numImage*0.25))*scale), 
                    0, 0,
                    img.getWidth(null), img.getHeight(null),
                    null);
        //}
        // affichage de PObject
        super.render(canvas, scale);
    }
    
    @Override
    public void onCollision(Game g, PObject other) {
        ((Player)other).setDead(true);
    }
    
}
