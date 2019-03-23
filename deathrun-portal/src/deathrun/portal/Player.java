/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

/**
 *
 * @author ydejongh
 */
public class Player extends PObject {
    public String name;
    public int avatar;
    
    Box collision_box;
    boolean right, left, jump;
    
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
		collision_box = collision_box.translate(pos);
    }
    
    //--------------- interface de gestion des collisions -----------------
    public boolean collisionable() 	{ return true; }
    @Override
    public Box getCollisionBox() 	{ return collision_box; }
    
    //--------------- interface d'affichage -----------------
    public void render(Graphics g, float scale) {
		super.render(g, scale);
        // TODO
    }
    
    public void setLeft(boolean left) { 
        if (left) this.velocity.x = -10;
        else this.velocity.x = 0;
    }
    public void setRight(boolean right) { 
        if (right) this.velocity.x = 10;
        else this.velocity.x = 0;
    }
    public void setJump(boolean jump) { this.jump = jump; }
    
}
