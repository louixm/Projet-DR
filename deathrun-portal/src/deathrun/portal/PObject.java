/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author ydejongh
 */
abstract public class PObject {
    public int db_id;		// id dans la base de donnée
    public Vec2 position;
    public Vec2 velocity;
    public Vec2 acceleration;
    Timestamp last_sync;
    
    PObject(Game game) throws SQLException { this(game, game.map.objects.size()); }
    PObject(Game game, int db_id) throws SQLException 	{ 
        this.db_id = db_id; 
        this.position = new Vec2();
        this.velocity = new Vec2();
        this.acceleration = new Vec2();
        
        if (game.sync != null) {
            PreparedStatement req = game.sync.srv.prepareStatement("SELECT EXISTS(SELECT id FROM pobjects WHERE id = ?)");
            req.setInt(1, db_id);
            ResultSet r = req.executeQuery();
            r.next();
            if (!r.getBoolean(1)) {
                req = game.sync.srv.prepareStatement("INSERT INTO pobjects VALUES (?,0,0,0,0,0)");
                req.setInt(1, db_id);
                req.executeUpdate();
                req.close();
            }
        }
    }

    //--------------- interface de gestion des collisions -----------------
    /// retourne vrai si les deux objets peuvent entrer en collision si ils se superposent
    public boolean collisionable(PObject other) { return false; }
    /// renvoie la boite de collision de l'objet dans l'espace courant (doit prendre en compte la position)
    public Box getCollisionBox() { return null; }
    
    //--------------- interface d'affichage -----------------
    /// methode d'affichage de l'objet
    public void render(Graphics2D g, float scale, boolean drawHitBox)  // methode interface
    {
        // affichage de la boite de collision (pour l'instant)
        if (drawHitBox){
            Box collision_box = getCollisionBox();
            if (collision_box != null){
                g.setColor(new Color(255, 0, 0));
                g.drawRect( //drawRect(x, y, width, height)
                    (int) (collision_box.p1.x*scale),       (int) (collision_box.p1.y*scale),
                    (int) (collision_box.getWidth()*scale), (int) (collision_box.getHeight()*scale)
                    );
            }
    //        System.out.println("p1: " + (int) (collision_box.p1.x*scale) + ", " + (int) (collision_box.p1.y*scale) + ", p2: " + (int) (collision_box.p2.x*scale) + ", " + (int) (collision_box.p2.y*scale));
        }
    }
    
    public void render(Graphics2D g, float scale){}
    
    /// renvoie true si l'objet doit etre affiché apres avoir rendu les joueurs (avant-plan)
    /// si non surchargée, la valeur par défaut est false
    boolean foreground()    { return false; }
    
    
    public void setPosition(Vec2 p)       { this.position = p; }
    public void setVelocity(Vec2 v)       { this.velocity = v; }
    public void setAcceleration(Vec2 a)   { this.acceleration = a; }
    
    /// methode d'envoi des données locales a la base de donnée
    public void syncSet(Sync sync)	{
        try {
            PreparedStatement req = sync.srv.prepareStatement("UPDATE pobjects SET x=?, y=?, vx=?, vy=?, date_sync=NOW() WHERE id = ?");
            req.setInt(1, (int) (position.x*1000));
            req.setInt(2, (int) (position.y*1000));
            req.setDouble(3, velocity.x);
            req.setDouble(4, velocity.y);
            // id de l'objet a modifier
            req.setInt(5, db_id);
            // execution de la requete
            req.executeUpdate();
            req.close();
            
            req = sync.srv.prepareStatement("SELECT now();");
            ResultSet r = req.executeQuery();
            r.next();
            last_sync = r.getTimestamp(1);
        }
        catch (SQLException err) {
            System.out.println("sql exception:\n"+err);
        }
    }
}
