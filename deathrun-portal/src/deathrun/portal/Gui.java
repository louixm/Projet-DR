package deathrun.portal;

import javax.swing.JPanel;
import java.awt.event.ActionListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 

/**
 *
 * @author ydejongh
 */
public class Gui extends JPanel  implements ActionListener {
    public const float scale = 30;	// pixel/m
    public Game game;
    
    Timer timer;

    
    Gui(Game game) {
        this.game = game;
        this.timer = new Timer(20, this);
        this.timer.start();
    }
    
    public void actionPerformed(ActionEvent e) {
        game.physicStep();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        for (PObject object : game.map.objects) {
            if (! object.foreground())	object.render(g, scale);
        for (Player player : game.players)		
            player.render(g, scale);
        for (PObject object : game.map.objects) {
            if (object.foreground())	object.render(g, scale);
    }
}
