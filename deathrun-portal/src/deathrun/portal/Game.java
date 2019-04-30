/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ydejongh
 */
public class Game {
    public Map map;
    public ArrayList<Player> players;
    public  Sync sync;
    
    long prev_time; // (ns) instant de dernier pas physique
    long next_sync; // (ns) instant de prochaine synchronisation prévue de l'etat du jeu avec la BDD
    final long sync_interval = 100000000; // (ns) temps minimum entre chaque synchronisation avec la BDD
    Timestamp db_last_sync;
    
    public final double gravity = 9.81;
    
    
    Game() {
        
        try {
            sync = new Sync(DriverManager.getConnection(
                    "jdbc:mysql://nemrod.ens2m.fr:3306/20182019_s2_vs2_tp1_deathrun?serverTimezone=UTC", 
                    "deathrun2", 
                    "5V8HVbDZMtkOHwaX"
                ));
            db_last_sync = new Timestamp(0);
        }
        catch (SQLException err) {
            System.out.println("sql connection error, fail to init game:\n\t"+err);
        }
        
        prev_time = System.nanoTime();
	players = new ArrayList<>();
    }
    
    void physicStep() {
        // TODO:  voir si il faut tout passer en getters et setters
        
        long ac_time = System.nanoTime();
        float dt = ((float)(ac_time - prev_time))/1e9f;
        
        // simulation de mecanique des objets (rectangles) avec Euler directe
        // suppose que les données sont synchronisées et que l'etat précédent est ok
        
        for (Player player: players) { 
            if (player.isControled()) player.applyMovementChanges();
            
            // pas de mise a jour de vitesse si pas d'acceleration
            if (! player.acceleration.isnull()) {
                player.setVelocity(player.velocity.add(player.acceleration.mul(dt)));
            }
        
            // pas de mise a jour de position si pas de vitesse
            if (player.velocity.isnull())       continue;
            //System.out.println("new position: "+player.position);
            Vec2 newpos = player.position.add(player.velocity.mul(dt));
            player.setPosition(newpos);
            
            // collisions avec les bords de l'ecran
            Box bplayer = player.getCollisionBox();
            
            if (bplayer.p1.x < map.size.p1.x) {
                double newx = map.size.p1.x - bplayer.p1.x + player.position.x;
                player.setPosition(new Vec2(newx, player.position.y));
            }
            else if (bplayer.p2.x > map.size.p2.x) {
                double newx = map.size.p2.x - bplayer.p2.x + player.position.x;
                player.setPosition(new Vec2(newx, player.position.y));
            }
            
            // acceleration gagnée automatiquement si pas de contact en dessous (corrigé par la boucle de collision)
            player.acceleration.y = gravity; 
            player.setCollisionDirection(new ArrayList<String>());
            
            // collisions avec les objets
            for (PObject object: map.objects) {
                int obj_collisionable = object.collisionable(player);
                int pl_collisionable = player.collisionable(object);
                if (pl_collisionable != 0 && obj_collisionable != 0) {
                    bplayer = player.getCollisionBox();
                    Box bobject = object.getCollisionBox();
                    
                    if (pl_collisionable == 1 && obj_collisionable == 1) {
                        if (bplayer.intersect(bobject)) {
                            // corriger la position pour que player ne soit plus dans object
                            Vec2 correction = bobject.outline(bplayer).outer(bplayer.center()).sub(bplayer.center());
                            // supprimer l'acceleration dans la direction du contact 
                            if (correction.y < 0) {
                                player.addCollisionDirection("down");
                                if (player.acceleration.y > 0)        player.acceleration.y = 0;
                                if (player.velocity.y > 0)            player.velocity.y = 0;
                            }
                            else if (correction.y > 0) {
                                player.addCollisionDirection("up");
                                if (player.acceleration.y < 0)        player.acceleration.y = 0;
                                if (player.velocity.y < 0)            player.velocity.y = 0;
                            }
                            if (correction.x < 0) {
                                player.addCollisionDirection("right");
                                if (player.acceleration.x > 0)        player.acceleration.x = 0;
                                if (player.velocity.x > 0)            player.velocity.x = 0;
                            }
                            else if (correction.x > 0) {
                                player.addCollisionDirection("left");
                                if (player.acceleration.x < 0)        player.acceleration.x = 0;
                                if (player.velocity.x < 0)            player.velocity.x = 0;
                            }

                            //player.setPosition(newpos2);
                            player.setPosition(player.position.add(correction));
                        }
                    }
                    
                    player.onCollision(this, object);
                    object.onCollision(this, player);
                }
            }
            
            // position maintenant corrigée
            // si sync n'est pas instancié, fonctionnement hors ligne
            if (sync != null && player.isControled())   {
                System.out.println("send sync for "+player.db_id);
                player.syncSet(sync);
            }
        }
        
        prev_time = ac_time;
    }
    
    
    /// se connecte au serveur et construit toutes les instances d'objet correspondant aux objets de la map et aux joueurs
    void init() {
        map = new Map(new Box(0, 0, 40, 20));
        
        if (this.sync != null) {
            //Players initialization
            try {                
                PreparedStatement req = sync.srv.prepareStatement("SELECT * FROM players");
                ResultSet r = req.executeQuery();
                while (r.next()) {
    //                    Vec2 pos = new Vec2(r.getInt("x")/1000, r.getInt("y")/1000);
    //                    Vec2 vel = new Vec2(r.getDouble("vx"), r.getDouble("vy"));
                    String name = r.getString("name");
                    int avatar = r.getInt("avatar");
                    Player player = new Player(this, name, avatar);
                    //syncUpdate(true);
                    System.out.println("initialized player " + name + " with skin #" + avatar);   
                    //id, name, score, id_object, avatar
                }
            }
            catch (SQLException err) {
                System.out.println("init players: "+err);
            }
        }
    }
    
    
    /// met a jour l'etat local du jeu avec les dernieres modifications du serveur
    void syncUpdate()       { syncUpdate(false); }
    void syncUpdate(boolean force) {
        // recuperer la date
        long ac_time = System.nanoTime();
        if (force || ac_time > next_sync) {
            next_sync = ac_time + sync_interval;
            
            // si sync n'est pas instancié, fonctionnement hors ligne
            if (sync == null)   return;
            // sinon essai de connexion
            try {                
                PreparedStatement req = sync.srv.prepareStatement("SELECT * FROM pobjects WHERE date_sync > ?;");
                req.setTimestamp(1, db_last_sync);
                // TODO: ne demander que les objets dont la date de mise a jour est plus recente que la derniere reception
                ResultSet r = req.executeQuery();
                while (r.next()) {
                    int id = r.getInt("id");
                    PObject obj; 
                    //boolean isControled = false;
                    if (id < 0) {
                        try {
                            obj = players.get(-id-1);
                            //isControled = players.get(-id-1).isControled();
                        }
                        catch (IndexOutOfBoundsException e){
                            obj = syncNewPlayer(id);                      
                        }
                    }
                    else        obj = map.objects.get(id);
                    //if (!isControled){
                    Timestamp server_sync = r.getTimestamp("date_sync");
                    if (server_sync.compareTo(obj.last_sync) > 0) {
                        obj.setPosition(new Vec2(r.getInt("x")/1000, r.getInt("y")/1000));
                        obj.velocity.x = r.getDouble("vx");
                        obj.velocity.y = r.getDouble("vy");
                        System.out.println("updated object "+id);
                    }
                    //}
                }
                r.close();
                
                req = sync.srv.prepareStatement("SELECT now();");
                r = req.executeQuery();
                r.next();
                db_last_sync = r.getTimestamp(1);
                r.close();
            }
            catch (SQLException err) {
                System.out.println("syncUpdate: "+err);
            }
        }
    }
    
    public Sync getSync() {return sync;}
    
    public PObject syncNewPlayer(int db_id){
        PObject obj;
        if (this.sync != null) {
            try {
                System.out.println("prep id = " + db_id);
                PreparedStatement req = this.sync.srv.prepareStatement("SELECT * FROM players WHERE id = ?");
                req.setInt(1, db_id);
                ResultSet r = req.executeQuery();
                r.next();
                String name = r.getString("name");
                int avatar = r.getInt("avatar");
                obj = new Player(this, name, avatar);
                System.out.println("added player " + name + " with skin #" + avatar);
                r.close();
                return obj;
            } catch (SQLException err) {
                System.out.println("syncNewPlayer: "+err);
            }
        }
        return null;
    }
  
            
}
