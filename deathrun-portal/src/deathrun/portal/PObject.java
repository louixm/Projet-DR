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
    public int db_id;		// id dans la base de donnée
    public Vec2 position;
    public Vec2 velocity;
    public Vec2 acceleration;
    
    PObject(int db_id) 	{ this.db_id = db_id; }

    //--------------- interface de gestion des collisions -----------------
    /// retourne vrai si les deux objets peuvent entrer en collision si ils se superposent
    public boolean collisionable(PObject other) { return false; }
    /// renvoie la boite de collision de l'objet dans l'espace courant (doit prendre en compte la position)
    public Box getCollisionBox() { return null; }
    
    //--------------- interface d'affichage -----------------
    /// methode d'affichage de l'objet
    public void render(Graphics g, float scale)  // methode interface
    {
        // affichage de la boite de collision (pour l'instant)
        Box collision_box = getCollisionBox();
        g.drawRect(
            collision_box.p1.x*scale, collision_box.p1.y*scale, 
            collision_box.p2.x*scale, collision_box.p2.y*scale
            );
    }
    /// renvoie true si l'objet doit etre affiché apres avoir rendu les joueurs (avant-plan)
    /// si non surchargée, la valeur par défaut est false
    boolean foreground()    { return false; }
    
    
    public void setPosition()       { return position; }
    public void setVelocity()       { return velocity; }
    public void setAcceleration()   { return acceleration; }
    
    /// methode d'envoi des données locales a la base de donnée
    public void syncSet(Sync sync)	{
		// TODO
    }
}
