/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.abs;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author ydejongh
 */
public class Player extends PObject {
    public String name;
    public int avatar;
    
    private boolean left, right, jump, leftAndRightWithPriorityOnRight; //, hasJumped;
    private ArrayList<String> collisionDirection;
    private boolean controled = false;
//    private BufferedImage robot, robotBase, robotDr, robotDrEx, robotSaut, robotBasegauche, robotgauche, robotgaucheex, robotsautgauche;     // rajouté par louis animation
    Box collision_box;
    Game game;
    
    public static BufferedImage avatars[];
    
    public static int availableId(Game game) {
        int id = -1;
        boolean id_found = true;
        while (id_found) {
            id_found = false;
            for (int i=0; i<game.players.size(); i++) {
                if (game.players.get(i).db_id == id)    {
                    id_found = true;
                    --id;
                    break;
                }
            }
        }
        return id;
    }
    
    public Player(Game game, String name, int avatar) throws SQLException {
        super(game, availableId(game));  // creer en ajoutant a la fin
        this.game = game;
        this.name = name; 
        this.avatar = avatar;
        collision_box = new Box(-0.5, 0, 0.5, 1.8);
        if (avatars == null) {
            this.avatars = new BufferedImage[3];
            try {
                this.avatars[0] = ImageIO.read(new File("./images/sentrybot.png"));
                this.avatars[1] = ImageIO.read(new File("./images/robotBleu.png"));
                this.avatars[2] = ImageIO.read(new File("./images/robotOrange.png"));
//                this.robotBase = ImageIO.read(new File("robotbase.png"));    // rajouté par louis animation
//                this.robotDr = ImageIO.read(new File("robotdroite.png"));    // rajouté par louis animation
//                this.robotDrEx = ImageIO.read(new File("robotdroitee.png"));    // rajouté par louis animation
//                this.robotSaut = ImageIO.read(new File("robotsaut.png"));    // rajouté par louis animation
//                this.robotBasegauche = ImageIO.read(new File("robotbasegauche.png"));    // rajouté par louis animation
//                this.robotgauche = ImageIO.read(new File("robotgauche.png"));    // rajouté par louis animation
//                this.robotgaucheex = ImageIO.read(new File("robotdgaucheex.png"));    // rajouté par louis animation
//                this.robotsautgauche = ImageIO.read(new File("robotsautgauche.png"));    // rajouté par louis animation
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        this.setPosition(game.map.enter.position.add(new Vec2(-0.25,0)));
        
        
        if (game.sync != null) {
            PreparedStatement req = game.sync.srv.prepareStatement("SELECT EXISTS(SELECT id FROM players WHERE id = ?)");
            req.setInt(1, db_id);
            ResultSet r = req.executeQuery();
            r.next();
            if (!r.getBoolean(1)) {
                req = game.sync.srv.prepareStatement("INSERT INTO players VALUES (?, ?, 0, 0, ?)"); //TODO
                req.setInt(1, db_id);
                req.setString(2, name);
                req.setInt(3, avatar);
                req.executeUpdate();
                req.close();
            }
        }
        
        // tout est pret, declaration du nouveau joueur
        game.players.add(this);
    }
    
    public void disconnect() {
        try {
            PreparedStatement req;
            // effacement de la table des objets
            req = game.sync.srv.prepareStatement("DELETE FROM pobjects WHERE id = ?");
            req.setInt(1, db_id);
            req.executeUpdate();
            // effacement de la table de joueurs
            req = game.sync.srv.prepareStatement("DELETE FROM players WHERE id = ?");
            req.setInt(1, db_id);
            req.executeUpdate();
            
            System.out.println("Deleted player with id " + db_id);
            req.close();
        }
        catch (SQLException err) {
            System.out.println("Player.disconnect(): "+err);
        }
    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
    
    //--------------- interface de gestion des collisions -----------------
    public int collisionable(PObject other) { 
        if (other instanceof Player)    return 0;
        else                            return 1;
    }
    @Override
    public Box getCollisionBox() 	{ return collision_box; }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D g, float scale) {
        g.drawImage(avatars[avatar], 
            (int) (collision_box.p1.x*scale), 
            (int) (collision_box.p1.y*scale), 
            (int) (collision_box.p2.x*scale), 
            (int) (collision_box.p2.y*scale), 
            0, 0,
            avatars[avatar].getWidth(null), avatars[avatar].getHeight(null),
            null);
        
        super.render(g, scale);
    }

    public void setLeft(boolean left) { 
        this.left = left;
        if (this.right) this.leftAndRightWithPriorityOnRight = false;
    }
    public void setRight(boolean right) { 
        this.right = right;
        if (this.left) this.leftAndRightWithPriorityOnRight = true;
    }
    public void setJump(boolean jump) { 
        this.jump = jump;  
    }
    
    @Override
    public void onGameStep(Game game, float dt) {
        applyMovementChanges(dt);
    }
    
    public void applyMovementChanges(float dt){
        if (this.left && (!this.right || !this.leftAndRightWithPriorityOnRight)){
            if (this.velocity.x > 0)        this.acceleration.x = -40;
            else if (this.velocity.x > -7)  this.acceleration.x = -20;
            else                            this.acceleration.x = 0;
        }
        else if (this.right && (!this.left || this.leftAndRightWithPriorityOnRight)){
            if (this.velocity.x < 0)        this.acceleration.x = 40;
            else if (this.velocity.x < 7)   this.acceleration.x = 20;
            else                            this.acceleration.x = 0;
        }       
        else{            
//            if (this.velocity.x > 1)            this.acceleration.x = -40;
//            else if (this.velocity.x < -1)      this.acceleration.x = 40;
            if (abs(this.velocity.x) > 1)        this.acceleration.x = -40 * abs(this.velocity.x)/this.velocity.x;
            else                                {this.acceleration.x = 0;      this.velocity.x = 0;}
            if ((this.velocity.x + this.acceleration.x*dt) * this.velocity.x < 0) 
                                                {this.acceleration.x = 0;      this.velocity.x = 0;};
        }
        
        if (this.jump) {
            if (acceleration.y == 0)    this.velocity.y = -6;
            else if (velocity.y > 0 && (
                    (this.right && this.collisionDirection.contains("right")) 
                 || (this.left && this.collisionDirection.contains("left"))))
            {
                this.velocity.y = -5;
                if (this.right) this.velocity.x = -12;               
                else            this.velocity.x = 12;
            }
        }
    }
    
    public void setCollisionDirection(ArrayList<String> array){ this.collisionDirection = array; }
    public void addCollisionDirection(String direction){ this.collisionDirection.add(direction); }
    public ArrayList<String> getCollisionDirection(){ return this.collisionDirection; }
    
    public void setControled(boolean controled){ this.controled = controled; }
    public boolean isControled(){ return this.controled; }
    
}
