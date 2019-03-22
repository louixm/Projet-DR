/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

/**
 *
 * @author ydejongh
 */
interface Syncable {
    /// synchronise l'objet avec le serveur SQL
    boolean sync_send = false;
    
    /// envoie le dernier état
    void syncGet();	// TODO: rajouter un argument donnant la connexion a la DB
    /// récupere le dernier état
    void syncSet(); // TODO: rajouter un argument donnant la connexion a la DB
}
