/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Graphics;

/**
 *
 * @author ydejongh
 */
public class Platform extends PObject {
	Box collision_box;

    public Platform(int db_id) {
        super(db_id);
        this.collision_box = new Box(-1, -0.2, 1, 0);
    }
    
    public void setPosition(Vec2 pos) {
		collision_box = collision_box.translate(pos);
    }
     
    //--------------- interface de gestion des collisions -----------------
    public boolean collisionable()  { return true; }
    public Box getCollisionBox()       { return collision_box; }
    
    //--------------- interface d'affichage -----------------
    public void render(Graphics g, float scale) {
		super.render(g, scale);
        // TODO
    }
    
}
