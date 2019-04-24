/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.io.IOException;
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
    public static void main(String[] args) throws SQLException, IOException {
        Game game = new Game();
        Player controled, otherone;
        //try {
            game.init();	// connecte au serveur et construit tous les objets tels que dans la base de donnnées
            controled = new Player(game, "blue", 1);	// TODO: choisir le nom et l'avatar du joueur
            otherone = new Player(game, "orange", 2);	// TODO: choisir le nom et l'avatar du joueur
            controled.setControled(true);

            /*game.map.objects.add(new Platform(1, new Vec2(10, 5),   2, 0.2));
            game.map.objects.add(new Platform(2, new Vec2(17, 6.5), 10, 3));
            game.map.objects.add(new Platform(3, new Vec2(3, 6.5), 1, 10));
            game.map.objects.add(new Platform(4, new Vec2(3, 16.5), 5, 1));*/
            game.map = game.map.MapInitialization(2);  // choix de la map 1, 2, 3, 4, 5
            controled.setPosition(new Vec2(10, 2));

            otherone.setPosition(new Vec2(12, 2));
            game.map.objects.add(new ExitDoor(1,new Vec2(20, 15), 3, 3));
            game.map.objects.add(new Saw(1,new Vec2(2, 2)));
        
//            game.map.objects.add(new Platform(1, new Vec2(12, 5),   2, 0.2, 3));
//            game.map.objects.add(new Platform(2, new Vec2(19, 6.5), 10, 3, 1));
//            game.map.objects.add(new Platform(3, new Vec2(5, 6.5), 1, 10, 0));
//            game.map.objects.add(new Platform(4, new Vec2(5, 16.5), 5, 1, 3));
//            game.map.objects.add(new Platform(5, new Vec2(0, 6.5), 1, 10, 0));
//            controled.setPosition(new Vec2(12, 1));
            
            controled.acceleration.y = -1; // valeur différente de 0 pour forcer l'update de physicstep initiale
            otherone.acceleration.y = -1; // valeur différente de 0 pour forcer l'update de physicstep initiale
        
        /*
        }
        catch (Exception err) {
            System.out.println("error initializing the game: "+err);
            return;
        }
        */
        
        Gui gui = new Gui(game, controled);
        gui.setVisible(true);
        
    }
    
}
