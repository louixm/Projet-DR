/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.imageio.ImageIO;

/**
 *
 * @author kbenie
 */
public class Explosive extends Trap {
    private Image img;
    static private Image im[];
    int step;
    double grandissement = 200; //en pixel
    double largeurBox = 1;
    double largeurImage=280;  //en pixel
    
    double ratioLargeurHauteur = 19f/28f;
    static final String db_type = "explosive";
    Vec2 activPosition;   //position quand le piège est sous controle
    long time_next_image;
    long image_duration = 20000000;
    
    //boolean dejaJoue = false;
    
    public Explosive(Game game, Vec2 position) throws IOException, SQLException {this(game,position,-1);}
    public Explosive(Game game, Vec2 position, int db_id) throws IOException, SQLException {
        super(game, "explosive", db_id);
        this.position = position;
        collision_box = new Box(0,0,1,1);
        
        step = 0;
        
        im = new Image[] {
            ImageIO.read(new File("./images/Explosive_0.png")),
            ImageIO.read(new File("./images/Explosive_1.png")),
            ImageIO.read(new File("./images/Explosive_2.png")),
            ImageIO.read(new File("./images/Explosive_3.png")),
            ImageIO.read(new File("./images/Explosive_4.png")),
            ImageIO.read(new File("./images/Explosive_5.png")),
            ImageIO.read(new File("./images/Explosive_6.png")),
            ImageIO.read(new File("./images/Explosive_7.png")),
            ImageIO.read(new File("./images/Explosive_8.png")),
            ImageIO.read(new File("./images/Explosive_9.png")),
            ImageIO.read(new File("./images/Explosive_10.png")),
            ImageIO.read(new File("./images/Explosive_11.png")),
            ImageIO.read(new File("./images/Explosive_12.png")),
            ImageIO.read(new File("./images/Explosive_13.png"))
        };
    }
    
    //--------------- interface de gestion des collisions -----------------
    @Override
    public int collisionable(PObject other)  { 
        return (other instanceof Player)?2:0;
    }
    
    public void onCollision(Game g, PObject other) { // si le piège est activé, il tue, si non, il ne le fait pas
        if(enabled){
            if (other instanceof Player){
                Player p = (Player) other;
                p.setDead(true, p.isControled(), this);
            }
        }
    }
    //-------------------Interface d'affichage--------------------------------
    
    @Override
    public void render(Graphics2D canvas, float scale) {
        
        // -----------------Audio---------------------------
        /*SoundPlayer crush = new SoundPlayer("crush.mp3", false);
        if (enabled && dejaJoue == false) {
            dejaJoue = true;
            crush.play();
        }
        if (!enabled){
            dejaJoue = false;
        }*/
            // -----------------MAJ de l'image---------------------------
            
        long ac_time = System.nanoTime();
        if (ac_time > time_next_image)  {    
            
            // si le piège n'est pas activé on met l'image "punch0"
            if (!enabled){   
                img = im[0];
                collision_box = new Box(0,0,largeurBox,largeurBox*ratioLargeurHauteur);
                collision_box = collision_box.translateToPosition(position);
            } 
            
            // si le piège est activé
            else {  
                if (step<13){
                    // incrementer le compteur de frame
                    step ++;
                    // évolution du numéro de l'immage
                    img = im[step];
                    //dimensionnement et positionnement de la box de collision
                    double ratio = largeurBox*scale/largeurImage;
                    if (step<=4){
                        double NouvelleLargeur = largeurBox+((step*ratio*grandissement))/scale;
                        collision_box = new Box(0,0 ,NouvelleLargeur,NouvelleLargeur*ratioLargeurHauteur);
                        activPosition = new Vec2(position.x-(step*ratio*grandissement/(2*scale)),position.y-(step*ratio*grandissement/(2*scale))); 
                        collision_box = collision_box.translateToPosition(activPosition);
                    }
                    else{
                        double NouvelleLargeur = largeurBox+((5*ratio*grandissement))/scale;
                        collision_box = new Box(0,0 ,NouvelleLargeur,NouvelleLargeur*ratioLargeurHauteur);
                        activPosition = new Vec2(position.x-(5*ratio*grandissement/(2*scale)),position.y-(5*ratio*grandissement/(2*scale))); 
                        collision_box = collision_box.translateToPosition(activPosition);
                    }
                }
                // Suppression de l'explosif de la base de données
                else{
                    last_sync++;
                    step=0;
                    this.enable(false, false);
                    //self-destruction de la bombe
                    try {
                        PreparedStatement req = game.sync.srv.prepareStatement("UPDATE pobjects SET version=?, type=? WHERE id = ?");
                        // le numero de version
                        req.setLong(1, last_sync);
                        req.setString(2, "null");
                        // id de l'objet a modifier
                        req.setInt(3, this.db_id);

                        // execution de la requete
                        req.executeUpdate();
                        req.close();
                    }
                    catch (SQLException err) {
                        System.out.println("explosive onGameStep:\n"+err);
                    }
                }
            }
            }
            time_next_image = ac_time + image_duration;
        
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
