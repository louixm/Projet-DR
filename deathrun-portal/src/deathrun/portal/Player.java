/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author ydejongh
 */
public class Player extends PObject {
    public String name;
    public int avatar;
    public static BufferedImage avatars[];
    
    Box collision_box;
    
    public Player(String name, int avatar, int db_id) {
		super(db_id);
        this.name = name; this.avatar = avatar;
        collision_box = new Collision_box(-0.5, 0, 0.5, 1.8);
        
        if (avatars.length == 0) {
            // charger les images des avatars si pas deja fait
        }
    }
    
    @Override
    public setPosition(Vec2D pos) {
		collision_box = collision_box.add(pos);
    }
    
    //--------------- interface de gestion des collisions -----------------
    public boolean collisionable() 	{ return true; }
    @Override
    public Box getCollisionBox() 	{ return collision_box; }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics g, float scale) {
        // TODO
        
        // affichage de la boite de collision (pour l'instant)
        g.drawRect(
            collision_box.p1.x*scale, collision_box.p1.y*scale, 
            collision_box.p2.x*scale, collision_box.p2.y*scale
            );
    }
    
}
