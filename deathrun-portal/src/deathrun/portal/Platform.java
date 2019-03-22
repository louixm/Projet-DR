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

    public Platform(int db_id) {
        super(db_id);
    }
    
     
    //--------------- interface de gestion des collisions -----------------
    public boolean collisionable() { return true; }
    @Override
    public Box collisionBox() {
        return new Box(-0.5, 0, 0.5, 1.8);
    }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(JPanel canvas) {
        // TODO
    }
    
}
