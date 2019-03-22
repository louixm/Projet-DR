/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import javax.swing.JPanel;

/**
 *
 * @author ydejongh
 */
public class Platform extends PObject {
	Box collision_box;

    public Platform(int db_id) {
        super(db_id);
    }
    
    @Override
    public setPosition(Vec2D pos) {
		collision_box = collision_box.add(pos);
    }
     
    //--------------- interface de gestion des collisions -----------------
    public boolean collisionable()  { return true; }
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
