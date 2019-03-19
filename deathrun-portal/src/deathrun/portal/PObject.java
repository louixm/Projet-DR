/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import javax.swing.JPanel;

/**
 *
 * @author ydejongh
 */
abstract public class PObject implements Syncable {
    Vec2 position;
    Vec2 velocity;
    Vec2 acceleration;
    
    /// retourne vrai si les deux objets peuvent entrer en collision si ils se superposent
    public boolean collisionable(PObject other) { return false; }
    /// renvoie la boite de collision de l'objet
    public Box collisionBox() { return null; }
    
    /// methode d'affichage de l'objet
    abstract void render(JPanel canvas);  // methode interface
    /// renvoie true si l'objet doit etre affiché apres avoir rendu les joueurs (avant-plan)
    boolean foreground()    { return false; }
}
