/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jimy
 */
public class Trap extends PObject {
    // a utiliser dans les classes dérivées
    public boolean enabled;    /// etat d'activation du piege
    
    Box collision_box;
    
    Player takingcontrol = null;
    Player currentcontrol = null;
    long taken_time = 0;
    final long take_time = 2000;
    Game game;
    
    long last_trap_sync;    /// numero de version (pour situer son etat de mise a jour par rapport aux données serveur)
    
    
    
    
    public Trap(Game game, String db_type) throws SQLException {this(game,db_type,-1);}
    public Trap(Game game, String db_type, int db_id) throws SQLException {
        super(game, db_type, db_id);
        this.game = game;
        this.enabled = false;
        
        if (game.sync != null) {      
            last_trap_sync = 0;
                
            // inserer dans la base de donnée
            PreparedStatement req = game.sync.srv.prepareStatement("SELECT EXISTS(SELECT id FROM traps WHERE id = ?)");
            req.setInt(1, db_id);
            ResultSet r = req.executeQuery();
            r.next();
            if (!r.getBoolean(1)) {
                req.close();
                System.out.println("trap "+db_id+" is added");
                req = game.sync.srv.prepareStatement("INSERT INTO traps VALUE (?, ?, false, 0)");
                req.setInt(1, db_id);
                req.setInt(2, -1);
                req.executeUpdate();
                req.close();
            }
        }
    }
    
    void enable(boolean enable, boolean withsync) {
        System.out.println("trap "+db_id+" is set "+enable);
        
        if (withsync && game.sync != null) {
            last_trap_sync = game.sync.latest ++;
            
            try {
                System.out.println("trap "+db_id+" has been sent");
                // noter le joueur comme pilote du piege dans la base de donnée
                PreparedStatement req = game.sync.srv.prepareStatement("UPDATE traps SET enabled=?, version=? WHERE id = ?");
                req.setBoolean(1, enable);
                req.setLong(2, last_trap_sync);
                req.setInt(3, db_id);
                req.executeUpdate();
                req.close();
                
            } catch (SQLException err) {
                System.out.println("Trap.enable: "+err);
            }
        }
        
        enabled = enable;
    }
    
    void takeControl(Player user) {
        this.takingcontrol = user;
        taken_time = System.currentTimeMillis() + take_time;
    }
    
    void setControl(Player user, boolean withsync) {
        System.out.println("trap "+db_id+" control taken");
        
        if (withsync && game.sync != null) {
            last_trap_sync = game.sync.latest ++;
            try {
                // noter le joueur comme pilote du piege dans la base de donnée
                PreparedStatement req = game.sync.srv.prepareStatement("UPDATE traps SET owner=?, version=? WHERE id = ?");
                int userid = (user != null)? user.db_id:0;
                req.setInt(1, userid);
                req.setLong(2, last_trap_sync);
                req.setInt(3, db_id);
                req.executeUpdate();
                req.close();
                
            } catch (SQLException err) {
                System.out.println("Trap.setControl: "+err);
            }
        }
        
        // ajouter le piege aux controles de l'utilisateur local
        if (currentcontrol != null) currentcontrol.traps.remove(this);
        if (user != null)           user.traps.add(0, this);
        currentcontrol = user;
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
            g.drawRoundRect(px, py, (int)(max_bar * (remains)/take_time), (int)(0.2*scale), (int)(0.2*scale), (int)(0.2*scale));

            // prise de controle lorsque le temps est écoulé
            if (remains <= 0) {
                taken_time = 0;
                setControl(takingcontrol, true);
                takingcontrol = null;
            }
        }
    }
}
