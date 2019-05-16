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
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        StartMenu menu = new StartMenu();
        menu.show();
        while (menu.alive)  Thread.sleep(100);
        
        if (menu.start) {
            Game game = new Game(true);
            Player controled;
            //try {
            game.init(4);// choix de la map 1, 2, 3, 4	// connecte au serveur et construit tous les objets tels que dans la base de donnnées
            //TODO: check dans la db players et ajouter au jeu tous ceux deja existants
            controled = new Player(game, menu.pseudo, menu.avatar);
            controled.setControled(true);

            controled.setPosition(game.map.enter.position.add(new Vec2((game.map.enter.box.getWidth() - controled.collision_box.getWidth())/2, game.map.enter.box.getHeight() ))); //- controled.collision_box.getHeight()

            game.map.objects.add(new Saw(game, game.map.size.center().sub(new Vec2(1, 1))));
            game.map.objects.add(new Laser(game,new Vec2(4, 8),(float)Math.PI/6));
            //game.map.objects.add(new Punch(game,new Vec2(2,4)));
        
            controled.acceleration.y = -1; // valeur différente de 0 pour forcer l'update de physicstep initiale
            //TODO: faire plus prore que ca
            
            Gui gui = new Gui(game, controled);
            gui.setVisible(true);
        }
        /*
        }
        catch (Exception err) {
            System.out.println("error initializing the game: "+err);
            return;
        }
        */
        
        
    }
    
}
