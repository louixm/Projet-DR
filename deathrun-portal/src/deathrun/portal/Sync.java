/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
    public long sync_interval = 100000000; // (ns) temps minimum entre chaque synchronisation avec la BDD
    
    public Sync(Connection srv) {
        this.srv = srv;
    }
    
    public Timestamp now() {
        try {
            PreparedStatement req = srv.prepareStatement("SELECT now();");
            ResultSet r = req.executeQuery();
            r.next();
            return r.getTimestamp(1);
        }
        catch (SQLException err) {
            System.out.println("PObject.update_date_sync():\n"+err);
            return null;
        }
    }
}
