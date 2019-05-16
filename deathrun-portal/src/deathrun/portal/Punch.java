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
    
    public Punch(Game game, Vec2 position) throws IOException, SQLException {
        super(game);
        this.collision_box = new Box(-1, -1, 1, 1).translate(position);
        setPosition(position);
        
        if (img == null) {
            img = ImageIO.read(new File("./images/punch_0.png")); //poinçon denté
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
    
    //--------------- interface d'affichage -----------------
    /*public void newImage() throws IOException{
        int i=1;
        boolean plus = true;
        while (true){
            if (plus){
                while (i<6){
                    img = ImageIO.read(new File("./images/punch_"+i+".png"));
                    i++;
                }
                plus = false;
            } else{
                while(i>-1){
                    img = ImageIO.read(new File("./images/punch_"+i+".png"));
                    i--;
                }
                plus = true; 
            }
            
        }
    }*/
    @Override
    public void render(Graphics2D canvas, float scale) {
        
        //int i = (int) Math.cos(step*2*Math.PI/6); //step=6*k
        int sixMulticator = 0;
        if (step%6 == 0){
            sixMulticator = step/6; 
        }
        int numImage;
        if (sixMulticator % 2 == 0){
            numImage = step-(6*sixMulticator);
        }else{
            numImage = 5-(step-(6*sixMulticator));
        }
        try {
            img = ImageIO.read(new File("./images/punch_"+numImage+".png"));
        } catch (IOException ex) {
            Logger.getLogger(Punch.class.getName()).log(Level.SEVERE, null, ex);
        }
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
