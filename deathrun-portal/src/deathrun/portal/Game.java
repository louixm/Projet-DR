/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author ydejongh
 */
public class Game {
    public Map map;
    public ArrayList<Player> players;
    
    Sync sync;
    Instant prev_time; // instant de dernier pas physique
    
    
    Game() {
        try {
            sync = new Sync(DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/tp_jdbc?serverTimezone=UTC", "etudiant", "YTDTvj9TR3CDYCmP"));
        }
        catch (SQLException e) {
            System.out.println("sql connection error, fail to init game");
        }
        
        prev_time = Instant.now();
    }
    
    void physicStep() {
        // TODO:  voir si il faut tout passer en getters et setters
        
        Instant ac_time = Instant.now();
        float dt = (ac_time.getNano() - prev_time.getNano())/1000000000;
        
        // simulation de mecanique des objets (rectangles) avec Euler directe
        // suppose que les données sont synchronisées et que l'etat précédent est ok
        
        for (Player player: players) {
            // pas de mise a jour de vitesse si pas d'acceleration
            if (! player.acceleration.isnull()) {
                player.setVelocity(player.speed.add(player.acceleration.mul(dt)));
            }
        
            // pas de mise a jour de position si pas de vitesse
            if (player.velocity.isnull())       continue;
            Vec2 newpos = player.position.add(player.velocity.mul(dt));
            player.setPosition(newpos);
            
            // collisions avec les bords de l'ecran
            Box bplayer = player.collisionBox();
            if (bplayer.p1.x < map.size.p1.x) {
				float newx = map.size.p1.x - bplayer.p1.x + player.position.x;
				player.setPosition(new Vec2(newx, player.position.y));
            }
            else if (bplayer.p2.y > map.size.p2.y) {
				float newy = map.size.p2.x - bplayer.p2.x + player.position.y;
				player.setPosition(new Vec2(player.position.x, newy));
            }
            
            // acceleration gagnée automatiquement si pas de contact en dessous (corrigé par la boucle de collision)
            player.acceleration.y = -9.81; 
            
            // collisions avec les objets
            for (PObject object: map.objects) {
                if (player.collisionable(object)) {
                    Box bplayer = player.collisionBox();
                    Box bobject = object.collisionBox();
                    if (bplayer.intersect(bobject)) {
                        // corriger la position pour que player ne soit plus dans object
                        Vec2 contact = bobject.contact(bplayer.contact);
                        Vec2 correction = bobject.outer(contact).sub(contact);
                        // supprimer l'acceleration dans la direction du contact 
                        // TODO: a voir avec le product owner si cela satisfait la dynamique de jeu
                        if (correction.y < 0) {
                            if (player.acceleration.y < 0)        player.acceleration.y = 0;
                            if (player.velocity.y < 0)            player.velocity.y = 0;
                        }
                        else if (correction.y > 0) {
                            if (player.acceleration.y > 0)        player.acceleration.y = 0;
                            if (player.velocity.y > 0)            player.velocity.y = 0;
                        }
                        if (correction.x < 0) {
                            if (player.acceleration.x < 0)        player.acceleration.x = 0;
                            if (player.velocity.x < 0)            player.velocity.x = 0;
                        }
                        else if (correction.x > 0) {
                            if (player.acceleration.x > 0)        player.acceleration.x = 0;
                            if (player.velocity.x > 0)            player.velocity.x = 0;
                        }
                        
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
        // TODO
    }
    
    /// met a jour l'etat local du jeu avec les dernieres modifications du serveur
    void syncUpdate() {
        // TODO
    }
}
