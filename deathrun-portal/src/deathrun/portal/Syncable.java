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
    void Sync();
}
