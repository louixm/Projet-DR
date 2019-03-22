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
abstract public class PObject {
    int db_id;		// id dans la base de donnée
    Vec2 position;
    Vec2 velocity;
    Vec2 acceleration;
    
    PObject(int db_id) 	{ this.db_id = db_id; }

    //--------------- interface de gestion des collisions -----------------
    /// retourne vrai si les deux objets peuvent entrer en collision si ils se superposent
    public boolean collisionable(PObject other) { return false; }
    /// renvoie la boite de collision de l'objet
    public Box collisionBox() { return null; }
    
    //--------------- interface d'affichage -----------------
    /// methode d'affichage de l'objet
    abstract void render(JPanel canvas);  // methode interface
    /// renvoie true si l'objet doit etre affiché apres avoir rendu les joueurs (avant-plan)
    /// si non surchargée, la valeur par défaut est false
    boolean foreground()    { return false; }
}
