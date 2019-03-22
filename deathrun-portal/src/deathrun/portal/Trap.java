/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

/**
 *
 * @author jimy
 */
public class Trap extends PObject {
	boolean enabled;
	Box collision_box;
	
    @Override
    public setPosition(Vec2D pos) {
		collision_box = collision_box.add(pos);
    }
	
	public boolean collisionable() { return enabled; }
	@Override
    public Box collisionBox()       { return collision_box; }
    
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
