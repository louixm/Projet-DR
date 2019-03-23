/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.sql.SQLException;
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
        game.init();	// connecte au serveur et construit tous les objets tels que dans la base de donnnées
        game.map.objects.add(new Platform(1));
        Player controled = new Player("myname", 0, 0);	// TODO: choisir le nom et l'avatar du joueur
        game.players.add(controled);
        
        Gui gui = new Gui(game, controled);
		gui.setVisible(true);
    }
    
}
