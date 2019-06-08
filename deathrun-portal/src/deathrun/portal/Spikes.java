/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import static deathrun.portal.Acid.db_type;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;

/**
 *
 * @author kbenie
 */
public class Spikes extends PObject {
    Box collision_box;
    private Image img;
    static Image im[];
    double taille=3f;
    double ratio=1f/3f;
//    double orientation;
    static final String db_type = "spikes";
    static SoundPlayer spiked;
    
    public Spikes(Game game, int orientation, Vec2 position) throws IOException, SQLException {this(game,orientation,position,-1);}
    public Spikes(Game game, int orientation, Vec2 position, int db_id) throws IOException, SQLException {
        super(game, db_type, db_id);
        this.position = position;
        spiked = new SoundPlayer("Pics.mp3", false);
        
        im = new Image[] {
            ImageIO.read(new File("./images/Spikes_0.png")),
            ImageIO.read(new File("./images/Spikes_1.png")),
            ImageIO.read(new File("./images/Spikes_2.png")),
            ImageIO.read(new File("./images/Spikes_3.png")),
        };
        
        setOrientation(orientation);
        
    }
    
    //--------------- interface de gestion des collisions -----------------
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?1:0;
    }
    //--------------- interface de gestion des collisions -----------------*/
    
    @Override
    public Box getCollisionBox()  { return collision_box; }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
    
    @Override
    public void setOrientation(int orientation){
        this.orientation = orientation;
        if(this.orientation >= 4) this.orientation -= 4;
        switch(this.orientation){
            case 0:
                img = im[0]; //spikes vers le bas
                collision_box = new Box(0,0 , this.taille,this.taille*this.ratio); 
                collision_box = collision_box.translateToPosition(position);
                break;
            case 1:
                img = im[1]; //spikes vers la gauche
                collision_box = new Box(0,0 , this.taille*this.ratio,this.taille); 
                collision_box = collision_box.translateToPosition(position);
                break;
            case 2:
                img = im[2]; //spikes vers le haut
                collision_box = new Box(0,0 , this.taille,this.taille*this.ratio); 
                collision_box = collision_box.translateToPosition(position);
                break;
            case 3:
                img = im[3]; //spikes vers la droite
                collision_box = new Box(0,0 , this.taille*this.ratio,this.taille); 
                collision_box = collision_box.translateToPosition(position);
                break;
        }
    }
    
    
    @Override
    public void onGameStep(Game game, float dt) {
        for (Player p: game.players){
            Vec2 p1 = p.getCollisionBox().p1 ; //point supérieur gauche
            Vec2 p2 = p.getCollisionBox().p2 ; //point inférieur droit
            Vec2 p3 = new Vec2(p1.x, p2.y) ; // point inférieur gauche
            Vec2 p4 = new Vec2(p2.x , p1.y) ; // point supérieur droit
            if (orientation == 0){ // bas
                if ((collision_box.p1.x <= p4.x && p1.x<=collision_box.p2.x) && p1.y == collision_box.p2.y){  
                     if (!p.dead){
                        System.out.println("Player " + p.name + " wasnt dead "+p.dead+"    "+orientation);
                         spiked.play();
                        p.setDead(true);
                     }
                    
                    // Player Killed
                }
            }
            if (orientation == 1){ // gauche
                if ((collision_box.p1.y <= p2.y && p4.y<=collision_box.p2.y) && p2.x == collision_box.p1.x){  
                    if (!p.dead){
                        System.out.println("Player " + p.name + " wasnt dead "+p.dead+"    "+orientation);
                        spiked.play();
                        p.setDead(true);
                    }
                    // Player Killed
                }
            }
            if (orientation == 2){ // haut
                if ((collision_box.p1.x <= p4.x && p1.x<=collision_box.p2.x) && p2.y == collision_box.p1.y){  
                       if (!p.dead){
                        System.out.println("Player " + p.name + " wasnt dead "+p.dead+"    "+orientation);
                        spiked.play();
                        p.setDead(true);
                    }                    
                    // Player Killed
                }
            }
            if (orientation == 3){ // droit
                if ((collision_box.p1.y <= p2.y && p4.y<=collision_box.p2.y) && p1.x == collision_box.p2.x){  
                       if (!p.dead){
                        System.out.println("Player " + p.name + " wasnt dead "+p.dead+"    "+orientation);
                        spiked.play();
                        p.setDead(true);
                    }
                }// Player Killed
            }
        }
    }
    
    
    
    //-------------------Interface d'affichage--------------------------------
    
    @Override
    public void render(Graphics2D canvas, float scale) {
         
        // ---------------affichage de PObject ----------------------      
        canvas.drawImage(img, 
                    (int) (collision_box.p1.x*scale), 
                    (int) (collision_box.p1.y*scale), 
                    (int) (collision_box.p2.x*scale),   
                    (int) (collision_box.p2.y*scale),  
                    0, 0,
                    img.getWidth(null), img.getHeight(null),
                    null);
        super.render(canvas, scale);
    }
}
