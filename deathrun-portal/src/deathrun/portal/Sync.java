/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author ydejongh
 */
public class Sync {
    /*
    interface Syncable {
        /// synchronise l'objet avec le serveur SQL
        void SyncSet();
    }
    */
    
    public Connection srv;
    public Sync(Connection srv) {
        this.srv = srv;
    }
}