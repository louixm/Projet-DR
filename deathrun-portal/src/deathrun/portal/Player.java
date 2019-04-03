/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author ydejongh
 */
public class Player extends PObject {
    public String name;
    public int avatar;
    
    Box collision_box;
    
    public static BufferedImage avatars[];
    
    
    public Player(String name, int avatar, int db_id) {
        super(db_id);
        this.name = name; 
        this.avatar = avatar;
        collision_box = new Box(-0.5, 0, 0.5, 1.8);
        
        if (avatars == null) {
            this.avatars = new BufferedImage[0];
            // charger les images des avatars si pas deja fait
        }
    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
    
    //--------------- interface de gestion des collisions -----------------
    public boolean collisionable(PObject other) { 
        return ! (other instanceof Player);
    }
    @Override
    public Box getCollisionBox() 	{ return collision_box; }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D g, float scale) {
        super.render(g, scale);
        // TODO
    }
    
    public void setLeft(boolean left) { 
        if (left)   this.velocity.x = -5;
        else        this.velocity.x = 0;
    }
    public void setRight(boolean right) { 
        if (right)  this.velocity.x = 5;
        else        this.velocity.x = 0;
    }
    public void setJump(boolean jump) { 
        if (jump && acceleration.y == 0)
            this.velocity.y = -6;
    }
    
}
