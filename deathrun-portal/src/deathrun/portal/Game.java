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
            // pas de mise a jour si pas de mise a jour de vitesse
            if (player.velocity.isnull())       continue;
            Vec2 newpos = player.position.add(player.velocity.mul(dt));
            
            // collisions
            for (PObject object: map.objects) {
                if (player.collisionable(object)) {
                    Box bplayer = player.collisionBox();
                    Box bobject = object.collisionBox();
                    if (bplayer.intersect(bobject)) {
                        // corriger la position pour que player ne soit plus dans object
                        newpos = bobject.outer(newpos);
                        // supprimer l'acceleration dans la direction du contact 
                        // TODO: a voir avec le product owner si cela satisfait la dynamique de jeu
                        Box intersection = bplayer.intersection(bobject);
                        if      (player.acceleration.y < 0 && intersection.p2.y == bplayer.p1.y)        player.acceleration.y = 0;
                        else if (player.acceleration.y > 0 && intersection.p1.y == bplayer.p2.y)        player.acceleration.y = 0;
                        else if (player.acceleration.x < 0 && intersection.p2.x == bplayer.p1.x)        player.acceleration.x = 0;
                        else if (player.acceleration.x > 0 && intersection.p1.x == bplayer.p2.x)        player.acceleration.x = 0;
                    }
                }
            }
            // position maintenant corrigée
            player.setPosition(newpos);
            
            // si pas d'acceleration pas de mise a jour de vitesse
            if (player.acceleration.isnull())       continue;
            player.setVelocity(player.speed.add(player.acceleration.mul(dt)));
        }
        
        prev_time = ac_time;
    }
    
    
    /// se connecte au serveur et construit toutes les instances d'objet correspondant aux objets de la map et aux joueurs
    void init() {
        // TODO
    }
    
    /// crée un nouveau joueur, l'ajoute dans la partie, sur le serveur et le retourne
    Player newPlayer() {
        // TODO
    }
    
    /// met a jour l'etat local du jeu avec les dernieres modifications du serveur
    void syncUpdate() {
        // TODO
    }
}
