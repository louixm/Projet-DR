/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

/**
 *
 * @author ydejongh
 */
public class Game {
    public Map map;
    public ArrayList<Player> players;
    
    Sync sync;
    long prev_time; // instant de dernier pas physique
    
    public final double gravity = 9.81;
    
    
    Game() throws SQLException {
        //try {
            //sync = new Sync(
          //  DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/tp_jdbc?serverTimezone=UTC", "etudiant", "YTDTvj9TR3CDYCmP")
            //);
        //}
        //catch (SQLException e) {
         //   System.out.println("sql connection error, fail to init game");
        //}
        
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
            // pas de mise a jour de vitesse si pas d'acceleration
            if (! player.acceleration.isnull()) {
                player.setVelocity(player.velocity.add(player.acceleration.mul(dt)));
            }
        
            // pas de mise a jour de position si pas de vitesse
            if (player.velocity.isnull())       continue;
            //System.out.println("new position: "+player.position);
            Vec2 newpos = player.position.add(player.velocity.mul(dt));
            Vec2 oldpos = player.position;
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
            
            // collisions avec les objets
            for (PObject object: map.objects) {
                if (player.collisionable(object)) {
                    bplayer = player.getCollisionBox();
                    Box bobject = object.getCollisionBox();
                    if (bplayer.intersect(bobject)) {
                        // corriger la position pour que player ne soit plus dans object
                        Vec2 correction = bobject.outline(bplayer).outer(bplayer.center()).sub(bplayer.center());
                        // supprimer l'acceleration dans la direction du contact 
                        if (correction.y < 0) {
                            if (player.acceleration.y > 0)        player.acceleration.y = 0;
                            if (player.velocity.y > 0)            player.velocity.y = 0;
                        }
                        else if (correction.y > 0) {
                            if (player.acceleration.y < 0)        player.acceleration.y = 0;
                            if (player.velocity.y < 0)            player.velocity.y = 0;
                        }
                        if (correction.x < 0) {
                            if (player.acceleration.x > 0)        player.acceleration.x = 0;
                            if (player.velocity.x > 0)            player.velocity.x = 0;
                        }
                        else if (correction.x > 0) {
                            if (player.acceleration.x < 0)        player.acceleration.x = 0;
                            if (player.velocity.x < 0)            player.velocity.x = 0;
                        }
                        
                        //player.setPosition(newpos2);
                        player.setPosition(player.position.add(correction));
                    }
                }
            }
            
            // position maintenant corrigée
            player.syncSet(sync);
        }
        
        prev_time = ac_time;
    }
    
    
    /// se connecte au serveur et construit toutes les instances d'objet correspondant aux objets de la map et aux joueurs
    void init() {
        map = new Map(new Box(0, 0, 40, 20));
    }
    
    /// met a jour l'etat local du jeu avec les dernieres modifications du serveur
    void syncUpdate() {
        // TODO
    }
    
    public Player getFirstPlayer() { return this.players.get(0); }

  
            
}
