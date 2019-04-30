/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.SQLException;

/**
 *
 * @author jimy
 */
public class Trap extends PObject {
	boolean enabled;
	Box collision_box;

	public Trap(Game game) throws SQLException {
            super(game);
	}
	
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
	
	public boolean collisionable() { return enabled; }
        @Override
    public Box getCollisionBox()       { return collision_box; }
    
    //--------------- interface d'affichage -----------------
        @Override
    public void render(Graphics2D g, float scale) {
		super.render(g, scale);
        // TODO
    }
}
