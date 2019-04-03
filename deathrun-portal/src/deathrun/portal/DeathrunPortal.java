/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;

/**
 *
 * @author ydejongh
 */
public class DeathrunPortal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        Game game = new Game();
        Player controled;
        try {
            game.init();	// connecte au serveur et construit tous les objets tels que dans la base de donnnées
            controled = new Player("myname", 0, 0);	// TODO: choisir le nom et l'avatar du joueur

            game.players.add(controled);

            game.map.objects.add(new Platform(1, new Vec2(10, 5),   2, 0.2));
            game.map.objects.add(new Platform(2, new Vec2(17, 6.5), 10, 3));
            game.map.objects.add(new Platform(3, new Vec2(3, 6.5), 1, 10));
            game.map.objects.add(new Platform(4, new Vec2(3, 16.5), 5, 1));
            controled.setPosition(new Vec2(10, 2));
        }
        catch (Exception err) {
            System.out.println("error initializing the game: "+err);
            return;
        }
        
        Gui gui = new Gui(game, controled);
        gui.setVisible(true);
    }
    
}
