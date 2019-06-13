/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
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
            //game.purgeTraps();
            Player controled;
            //try {
            Random rand = new Random();
            game.init();// choix de la map 1, 2, 3, 4	// connecte au serveur et construit tous les objets tels que dans la base de donnnées
            //TODO: check dans la db players et ajouter au jeu tous ceux deja existants
//            game.map.objects.add(new Punch(game,1,new Vec2(21,16)));
                                    //audio continue
            
            SoundPlayer[] backgroundSound = { 
                new SoundPlayer("sabaton-primo-victoria-8-bit.mp3", true), 
                new SoundPlayer("sabaton-the-art-of-war-8-bit.mp3", true),
                new SoundPlayer("sabaton-coat-of-arms-8-bit.mp3", true),
                new SoundPlayer("square-hammer-8-bit-tribute-to-ghost-8-bit-universe.mp3", true)};
            
            int track = rand.nextInt(4);
            backgroundSound[track].play();
            
            controled = new Player(game, menu.pseudo, menu.avatar);
            controled.setControled(true);

            controled.setPosition(game.map.enter.position.add(new Vec2((game.map.enter.box.getWidth() - controled.collision_box.getWidth())/2, 0 ))); //- controled.collision_box.getHeight()
//            System.out.println(game.map.enter.position.add(new Vec2((game.map.enter.box.getWidth() - controled.collision_box.getWidth())/2, game.map.enter.box.getHeight() )));
//            //portails
//            Portal port = new Portal(game,new Vec2(13, 1), new boolean[] {false,true});
//            Portal port2 = new Portal(game,new Vec2(13,16), new boolean[] {false,true});
//            port.otherPortal = port2;
//            port2.otherPortal = port;
//            game.map.objects.add(port);
//            game.map.objects.add(port2);
            
//            //pieges
//            game.map.objects.add(new Saw(game, game.map.size.center().sub(new Vec2(1, 1))));
//            game.map.objects.add(new Laser(game,new Vec2(0, 11),0));

//            

//            controled.acceleration.y = 0; // valeur différente de 0 pour forcer l'update de physicstep initiale
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
