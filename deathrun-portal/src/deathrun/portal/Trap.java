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
    boolean enabled;    // etat d'activation du piege
    
    Box collision_box;
    
    Player takingcontrol = null;
    Gui controler = null;
    long taken_time = 0;
    final long take_time = 2000;

    public Trap(Game game) throws SQLException {
        super(game);
    }
    
    void takeControl(Player user, Gui controler) {
        this.takingcontrol = user;
        this.controler = controler;
        taken_time = System.currentTimeMillis() + take_time;
    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
	
    public int collisionable() { return 2; }
    
    //@Override
    public Box getCollisionBox()  { return collision_box; }
    
    //--------------- interface d'affichage -----------------
        @Override
    public void render(Graphics2D g, float scale) {
        super.render(g, scale);
        
        // affichage de la bare du temps restant
        if (taken_time > 0) {
            int px = (int) (collision_box.p1.x*scale);
            int py = (int) (collision_box.p2.y*scale);
            double max_bar = 2*scale;
            long remains = taken_time-System.currentTimeMillis();
            g.drawRoundRect(px, py, (int)(max_bar * (remains)/take_time), (int)(0.2*scale), (int)(0.1*scale), (int)(0.1*scale));

            // prise de controle lorsque le temps est écoulé
            if (remains <= 0) {
                taken_time = 0;
                // noter le joueur comme pilote du piege dans la base de donnée
                
                
                // ajouter le piege aux controles de l'utilisateur local
                controler.controled = takingcontrol;
                System.out.println("local player can now control the trap !");
            }
        }
    }
}
