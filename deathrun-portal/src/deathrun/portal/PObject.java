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
    public String db_type;
    public int orientation = 0;
    
    long prev_time; // (ns) instant de dernier pas physique
    long next_sync; // (ns) instant de prochaine synchronisation prévue de l'etat du jeu avec la BDD
    long last_sync; // numero de version (pour situer son etat de mise a jour par rapport aux données serveur)
    
    boolean local_changed;
    
    static boolean drawHitBox = false;
    
    PObject(Game game) throws SQLException { this(game, ""); }
    PObject(Game game, String db_type) throws SQLException 	{ this(game, db_type, -1); }
    PObject(Game game, String db_type, int db_id) throws SQLException 	{ 
        this.db_type = db_type;
        this.position = new Vec2();
        this.velocity = new Vec2();
        this.acceleration = new Vec2();
        
        // ajout dans la table des objets de Game
        /*
        if (db_id < 0) {
            int max_id = 0;
            for (int key: game.objects.keySet()) {
                if (key > max_id)   max_id = key;
            }
            this.db_id = max_id + 1;
        }
        else
            this.db_id = db_id;
        */
        if (db_id < 0)      this.db_id = db_id = game.next_id ++;
        else {
            this.db_id = db_id;
            game.next_id = Math.max(db_id, game.next_id)+1;
        }
        
        game.objects.put(this.db_id, this);
        
        if (game.sync != null) {
            this.last_sync = 0;      // initialisation du jeu: pas de derniere sync en date
            
            PreparedStatement req = game.sync.srv.prepareStatement("SELECT EXISTS(SELECT id FROM pobjects WHERE id = ?)");
            req.setInt(1, this.db_id);
            ResultSet r = req.executeQuery();
            r.next();
            if (!r.getBoolean(1)) {
                System.out.println("add pobject "+this.db_id);
                req = game.sync.srv.prepareStatement("INSERT INTO pobjects VALUES (?,0,0,0,0,0,?,0)");
                req.setInt(1, this.db_id);
                req.setString(2, this.db_type);
                req.executeUpdate();
                req.close();
            }
        }
    }

    //--------------- interface de gestion des collisions -----------------
    /** retourne 
        0 si les deux objets se superposent
        1 si les deux objets peuvent entrer en collision
        2 si les deux objets se superposent mais qu'on veut que onCollision() soit appellée
    */    
    public int collisionable(PObject other) { return 0; }
    /// renvoie la boite de collision de l'objet dans l'espace courant (doit prendre en compte la position)
    public Box getCollisionBox() { return null; }
    
    //--------------- interface d'affichage -----------------
    /// methode d'affichage de l'objet
    public void render(Graphics2D g, float scale)  // methode interface
    {
        // affichage de la boite de collision (pour l'instant)
        if (drawHitBox){
            Box collision_box = getCollisionBox();
            if (collision_box != null){
                g.setColor(new Color(255, 255, 0));
                g.drawRect( //drawRect(x, y, width, height)
                    (int) (collision_box.p1.x*scale),       (int) (collision_box.p1.y*scale),
                    (int) (collision_box.getWidth()*scale), (int) (collision_box.getHeight()*scale)
                    );
            }
    //        System.out.println("p1: " + (int) (collision_box.p1.x*scale) + ", " + (int) (collision_box.p1.y*scale) + ", p2: " + (int) (collision_box.p2.x*scale) + ", " + (int) (collision_box.p2.y*scale));
        }
    }
    
    /// renvoie true si l'objet doit etre affiché apres avoir rendu les joueurs (avant-plan)
    /// si non surchargée, la valeur par défaut est false
    boolean foreground()    { return false; }
    
    
    public void setPosition(Vec2 p)       { this.position = p; local_changed = true; }
    public void setVelocity(Vec2 v)       { this.velocity = v; local_changed = true; }
    public void setAcceleration(Vec2 a)   { this.acceleration = a; local_changed = true; }
    
    /// methode d'envoi des données locales a la base de donnée
    public void syncSet(Sync sync) { syncSet(sync, false); }
    public void syncSet(Sync sync, boolean force)	{
        // recuperer la date
        long ac_time = System.nanoTime();
        if (force || (ac_time > next_sync && local_changed)) {
            next_sync = ac_time + sync.sync_interval;
            local_changed = false;
            
            // numeros de version
            sync.latest ++;
            last_sync = sync.latest;
            
            //System.out.println("sync set "+db_id);
            
            try {
                PreparedStatement req = sync.srv.prepareStatement("UPDATE pobjects SET x=?, y=?, vx=?, vy=?, version=?, type=?, orientation=? WHERE id = ?");
                req.setInt(1, (int) (position.x*1000));
                req.setInt(2, (int) (position.y*1000));
                req.setDouble(3, velocity.x);
                req.setDouble(4, velocity.y);
                // le numero de version
                req.setLong(5, last_sync);
                req.setString(6, db_type);
                req.setInt(7, orientation);
                // id de l'objet a modifier
                req.setInt(8, db_id);
                
                // execution de la requete
                req.executeUpdate();
                req.close();
            }
            catch (SQLException err) {
                System.out.println("PObject.syncSet():\n"+err);
            }
        }
        
    }
    
    
    /// appellé a chaque iteration de la boucle principale du jeu
    public void onGameStep(Game g, float dt) {}
    /// appellé en cas de collision detectée
    public void onCollision(Game g, PObject other) {}

    void setOrientation(int orientationBloc) {}
}
