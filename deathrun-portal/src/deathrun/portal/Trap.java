/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author jimy
 */
public class Trap extends PObject {
	boolean enabled;
	Box collision_box;

	public Trap(int db_id) {
		super(db_id);
	}
	
    @Override
    public void setPosition(Vec2 pos) {
		collision_box = collision_box.translate(pos);
    }
	
	public boolean collisionable() { return enabled; }
    public Box getCollisionBox()       { return collision_box; }
    
    //--------------- interface d'affichage -----------------
    public void render(Graphics2D g, float scale) {
		super.render(g, scale);
        // TODO
    }
}
