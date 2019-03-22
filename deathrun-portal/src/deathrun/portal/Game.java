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
    Map map;
    ArrayList<Player> players;
    
    Sync sync;
    
    float prev_time; // instant de dernier pas physique
    
    Game() {
        try {
            sync = new Sync(DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/tp_jdbc?serverTimezone=UTC", "etudiant", "YTDTvj9TR3CDYCmP"));
        }
        catch (SQLException e) {
            System.out.println("sql connection error, fail to init game");
        }
    }
    
    void physicStep() {
        float ac_time;
        float dt = ac_time - prev_time;
        // suppose que les données sont synchronisées et que l'etat précédent est ok
        for (Player player: players) {
            Vec2 newpos = player.position.add(player.velocity.mul(dt));
            // collisions
            for (PObject object: map.objects) {
                if (player.collisionable(object)) {
                    // ramener la boite du joueur dans le repere 
                    Box bplayer = player.collisionBox();
                    Box bobject = object.collisionBox();
                    Vec2 d = bplayer.distance(bobject);
                }
            }
        }
        
        prev_time = ac_time;
    }
    
    void syncGet() {
        // TODO mettre a jour les positions des joueurs et les pieges
    }
}
