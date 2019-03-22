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
    ArrayList players;
    
    Sync sync;
    
    Game() {
        try {
            sync = new Sync(DriverManager.getConnection("jdbc:mysql://nemrod.ens2m.fr:3306/tp_jdbc?serverTimezone=UTC", "etudiant", "YTDTvj9TR3CDYCmP"));
        }
        catch (SQLException e) {
            System.out.println("sql connection error, fail to init game");
        }
    }
    
    void physicStep() { 
        // TODO:
        // - mettre a jour les vitesses en fonction des collisions
        // - mettre a jour les positions en fonction des vitesses
    }
    
    void syncGet() {
        // TODO mettre a jour les positions des joueurs et les pieges
    }
}
