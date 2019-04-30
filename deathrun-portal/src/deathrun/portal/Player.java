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
    private Vec2 previousPosition;
    private ArrayList<String> collisionDirection;
    private boolean controled = false;
    
    Box collision_box;
    
    public static BufferedImage avatars[];
    
    
    public Player(Game game, String name, int avatar) throws SQLException {
        super(game, -game.players.size()-1);  // creer en ajoutant a la fin
        game.players.add(this);
        this.name = name; 
        this.avatar = avatar;
        collision_box = new Box(-0.5, 0, 0.5, 1.8);
        
        if (avatars == null) {
            this.avatars = new BufferedImage[3];
            try {
                this.avatars[0] = ImageIO.read(new File("./images/sentrybot.png"));
                this.avatars[1] = ImageIO.read(new File("./images/robotBleu.png"));
                this.avatars[2] = ImageIO.read(new File("./images/robotOrange.png"));
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (game.sync != null) {
            PreparedStatement req = game.sync.srv.prepareStatement("SELECT EXISTS(SELECT id FROM players WHERE id = ?)");
            req.setInt(1, db_id);
            ResultSet r = req.executeQuery();
            r.next();
            if (!r.getBoolean(1)) {
                req = game.sync.srv.prepareStatement("INSERT INTO players VALUES (?, 0, 0, 0, 0)"); //TODO
                req.setInt(1, db_id);
                req.executeUpdate();
                req.close();
            }
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
        // TODO
    }
//    
//    public void setLeft(boolean left) { 
//        if (left)   this.velocity.x = -6;
//        else        this.velocity.x = 0;
//    }
//    public void setRight(boolean right) { 
//        if (right)  this.velocity.x = 6;
//        else        this.velocity.x = 0;
//    }
//    public void setJump(boolean jump) { 
//        if (jump && acceleration.y == 0)
//            this.velocity.y = -4;
//    }
    
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
    
    public void applyMovementChanges(){
        if (this.left && (!this.right || !this.leftAndRightWithPriorityOnRight)){
            if (this.velocity.x > 0) this.acceleration.x = -40;
            else if (this.velocity.x > -7) this.acceleration.x = -20;
            else this.acceleration.x = 0;
//            if (this.velocity.x > -6) this.velocity.x += this.acceleration.x;
        }
        else if (this.right && (!this.left || this.leftAndRightWithPriorityOnRight)){
            if (this.velocity.x < 0) this.acceleration.x = 40;
            else if (this.velocity.x < 7) this.acceleration.x = 20;
            else this.acceleration.x = 0;
//            if (this.velocity.x < 6) this.velocity.x += this.acceleration.x;
        }       
        else{
            if (this.velocity.x > 1) this.acceleration.x = -40;
            else if (this.velocity.x < -1) this.acceleration.x = 40;
            else {this.acceleration.x = 0; this.velocity.x = 0;}
//            this.velocity.x = 0;
        }
        
//        if (!this.jump && acceleration.y == 0) this.hasJumped = false;
//        if (!this.hasJumped && this.jump && acceleration.y == 0) {
        if (this.jump) {
//            System.out.println("Prev pos: " + previousPosition.x + ", pos: " + position.x + ", == ?: " + (previousPosition.x == position.x));
            if (acceleration.y == 0) {
                this.velocity.y = -6;
//            this.hasJumped = true;
            }
            else if (velocity.y > 0 && ((this.right && this.collisionDirection.contains("right")) || (this.left && this.collisionDirection.contains("left")))){ //au lieu de previouisPosition, ajouter un attribut qui dit de quelle direction vient la collsision, et l'update dans la boucle dans Game lors d'une collision
                this.velocity.y = -5;
                if (this.right) {
//                    this.leftAndRightWithPriorityOnRight = false;
                    this.velocity.x = -12;                    
                }      
                else {
//                    this.leftAndRightWithPriorityOnRight = true;
                    this.velocity.x = 12;
                }
            }
        }
        previousPosition = position;
    }
    
    public void setCollisionDirection(ArrayList<String> array){ this.collisionDirection = array; }
    public void addCollisionDirection(String direction){ this.collisionDirection.add(direction); }
    public ArrayList<String> getCollisionDirection(){ return this.collisionDirection; }
    
    public void setControled(boolean controled){ this.controled = controled; }
    public boolean isControled(){ return this.controled; }
    
}
