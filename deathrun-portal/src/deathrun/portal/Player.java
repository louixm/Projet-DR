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
    String name;
    int avatar;
    static BufferedImage avatars[];
    
    public Player(String name, int avatar, int db_id) {
		super(db_id);
        this.name = name; this.avatar = avatar;
        
        if (avatars.length == 0) {
            // charger les images des avatars si pas deja fait
        }
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
    
    //--------------- interface de synchronisation a la DB -----------------
    @Override
    public void syncGet() {
        // TODO
    }
    @Override
    public void syncSet() {
        // TODO
    }
}
