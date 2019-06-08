/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ydejongh
 */
public class Game {
    public Map map;
    public ArrayList<Player> players;
    public  Sync sync;
    
    public HashMap<Integer,PObject> objects;
    
    long prev_time; // (ns) instant de dernier pas physique
    long next_sync; // (ns) instant de prochaine synchronisation prévue de l'etat du jeu avec la BDD
    long db_last_sync;
    
    public final double gravity = 9.81;
    
    public boolean roundEnded = false;
    public boolean editionMode = true;
    
    public int next_id;
    
    
    Game() { this(false); }
    Game(boolean sync_enable) {
        if (sync_enable) {
            try {
                sync = new Sync(DriverManager.getConnection(
                        "jdbc:mysql://nemrod.ens2m.fr:3306/20182019_s2_vs2_tp1_deathrun?serverTimezone=UTC", 
                        "deathrun2", 
                        "5V8HVbDZMtkOHwaX"
                    ));
                db_last_sync = 0;
                next_sync = 0;
                System.out.println("connected to database");
            }
            catch (SQLException err) {
                System.out.println("sql connection error, fail to init game:\n\t"+err);
            }
        }
        
        prev_time = System.nanoTime();
	players = new ArrayList<>();
        objects = new HashMap<>();
        next_id = 0;
    }
    
    public void disconnect() {
        try {
            sync.srv.close();
        }
        catch (SQLException err) {
            System.out.println("Game.disconnect(): "+err);
        }
    }
    
    
    
    
    void step() throws IOException {
        long ac_time = System.nanoTime();
        float dt = ((float)(ac_time - prev_time))/1e9f;
        
        //for (PObject p: map.objects)    p.onGameStep(this, dt);
        for (HashMap.Entry<Integer,PObject> p : objects.entrySet()) p.getValue().onGameStep(this, dt);
//        for (PObject p: players)        p.onGameStep(this, dt);
        if (sync != null) syncUpdate();
        physicStep(dt);
        
        prev_time = ac_time;
    }
    
    
    void physicStep(float dt) {
        // simulation de mecanique des objets (rectangles) avec Euler directe
        // suppose que les données sont synchronisées et que l'etat précédent est ok
        
        for (Player player: players) { 
            if (player.disconnected) continue;
//            if (player.isControled()) 
                player.applyMovementChanges(dt);
            
            // pas de mise a jour de vitesse si pas d'acceleration
            if (! player.acceleration.isnull()) {
                player.setVelocity(player.velocity.add(player.acceleration.mul(dt)));
            }
        
            // pas de mise a jour de position si pas de vitesse
            //if (!player.velocity.isnull())       continue;
            //System.out.println("new position: "+player.position);
            Vec2 newpos = player.position.add(player.velocity.mul(dt));
            player.setPosition(newpos);
            
            // collisions avec les bords de l'ecran
            Box bplayer = player.getCollisionBox();
            //player.setPosition(new Vec2(10,10));
            if (bplayer.p1.x < map.size.p1.x) {
                double newx = map.size.p1.x - bplayer.p1.x + player.position.x;
                player.setPosition(new Vec2(newx, player.position.y));
            }
            else if (bplayer.p2.x > map.size.p2.x) {
                double newx = map.size.p2.x - bplayer.p2.x + player.position.x;
                player.setPosition(new Vec2(newx, player.position.y));
            }
            
            // acceleration gagnée automatiquement si pas de contact en dessous (corrigé par la boucle de collision)
            player.acceleration.y = gravity; 
            player.setCollisionDirection(new ArrayList<String>());
            
            // collisions avec les objets
            for (PObject object: objects.values()) {
                if (object instanceof Player) continue;
                int obj_collisionable = object.collisionable(player);
                int pl_collisionable = player.collisionable(object);
                if (pl_collisionable != 0 && obj_collisionable != 0) {
                    bplayer = player.getCollisionBox();
                    Box bobject = object.getCollisionBox();
                    
                    if (bplayer.intersect(bobject)) {
                        if (pl_collisionable == 1 && obj_collisionable == 1) {
                            // corriger la position pour que player ne soit plus dans object
                            Vec2 correction = bobject.outline(bplayer).outer(bplayer.center()).sub(bplayer.center());
                            // supprimer l'acceleration dans la direction du contact 
                            if (correction.y < 0) {
                                player.addCollisionDirection("down");
                                if (player.acceleration.y > 0)        player.acceleration.y = 0;
                                if (player.velocity.y > 0)            player.velocity.y = 0;
                            }
                            else if (correction.y > 0) {
                                player.addCollisionDirection("up");
                                if (player.acceleration.y < 0)        player.acceleration.y = 0;
                                if (player.velocity.y < 0)            player.velocity.y = 0;
                            }
                            if (correction.x < 0) {
                                player.addCollisionDirection("right");
                                if (player.acceleration.x > 0)        player.acceleration.x = 0;
                                if (player.velocity.x > 0)            player.velocity.x = 0;
                            }
                            else if (correction.x > 0) {
                                player.addCollisionDirection("left");
                                if (player.acceleration.x < 0)        player.acceleration.x = 0;
                                if (player.velocity.x < 0)            player.velocity.x = 0;
                            }

                            player.setPosition(player.position.add(correction));
                        }
                        
                        player.onCollision(this, object);
                        object.onCollision(this, player);
                    }
                    
                }
            }
            
            // position maintenant corrigée
            
            if (player.position.y > map.size.getHeight()*1.5) player.setDead(true);
            // si sync n'est pas instancié, fonctionnement hors ligne
            if (sync != null && player.isControled())   {
                player.syncSet(sync);
            }
        }
    }
    
    
    /// se connecte au serveur et construit toutes les instances d'objet correspondant aux objets de la map et aux joueurs
    void init(int i) throws IOException, SQLException {
        map = new Map(new Box(0, 0, 40, 20));
        this.map = this.map.MapInitialization(this, i);
        
        syncUpdate();
        /*
        if (this.sync != null) {
            //Players initialization
            try {                
                PreparedStatement req = sync.srv.prepareStatement("SELECT * FROM players");
                ResultSet r = req.executeQuery();
                while (r.next()) {
                    String name = r.getString("name");
                    int avatar = r.getInt("avatar");
                    int state = r.getInt("state");
                    int id = r.getInt("id");
                    Player player = new Player(this, name, avatar, id);
                    player.setState(state);
                    System.out.println("initialized player " + name + " with skin #" + avatar);
                }
            }
            catch (SQLException err) {
                System.out.println("init players: "+err);
            }
        }
        */
        if (this.sync != null) {
            
            // recupérer l'id max
            PreparedStatement req = this.sync.srv.prepareStatement("SELECT MAX(id) FROM pobjects");
            ResultSet r = req.executeQuery();
            r.next();
            next_id = r.getInt(1) + 1;
            r.close();
                    
            if (this.players.isEmpty()){
                switchToEditionMode(true);
            } 
            else {
                try {          
                    req = this.sync.srv.prepareStatement("SELECT * FROM server");
                    r = req.executeQuery();
                    r.next();
                    int mode = r.getInt("mode");
                    this.editionMode = (mode == 0);
                    System.out.println("edition mode = " + this.editionMode);
                    r.close();         
                    PreparedStatement reqScores = this.sync.srv.prepareStatement("DELETE FROM scores");
                    reqScores.executeUpdate();
                    reqScores.close();
                } catch (SQLException err) {
                    System.out.println("init: "+err);
                }
            }
        }
    }
    
    
    /// met a jour l'etat local du jeu avec les dernieres modifications du serveur
    void syncUpdate() throws IOException       { syncUpdate(false); }
    void syncUpdate(boolean force) throws IOException {
        // recuperer la date
        long ac_time = System.nanoTime();
        if (force || ac_time > next_sync) {
            next_sync = ac_time + sync.sync_interval;
            
            //System.out.println("sync get");
            
            // si sync n'est pas instancié, fonctionnement hors ligne
            if (sync == null)   return;
            //System.out.println("objects: "+objects);
            // sinon essai de connexion
            try {    
                // numero de derniere mise a jour
                long last_update = db_last_sync;
                        
                // synchronisation de la table des joueurs
                PreparedStatement reqplayers = sync.srv.prepareStatement("SELECT id,state,movement FROM players");
                
                ResultSet rplayers = reqplayers.executeQuery();
                while (rplayers.next()){
                    int id = rplayers.getInt("id");
                    int state = rplayers.getInt("state");
                    int movement = rplayers.getInt("movement");
                    
                    Player p;
                    if (objects.containsKey(id))    p = getPlayerWithId(id);
                    else                            { p = (Player) syncNewPlayer(id); System.out.println("spawn a player "+id); }
                    if (!p.isControled()){
                        p.setState(state);
                        p.setMovement(movement);
                    }
                }
                rplayers.close();
                
                // synchronisation de la table des pieges
                PreparedStatement reqtraps = sync.srv.prepareStatement("SELECT id,owner,enabled,version FROM traps WHERE version > ?");
                reqtraps.setLong(1, db_last_sync);
                ResultSet rtraps = reqtraps.executeQuery();
                while (rtraps.next()) {
                    int trapid = rtraps.getInt("id");
                    int ownerid = rtraps.getInt("owner");
                    boolean enabled = rtraps.getBoolean("enabled");
                    
                    if (!objects.containsKey(trapid))    {
//                        System.out.println("there is no local instance for trap "+trapid);
                        if (syncNewObject(trapid) == null) continue;
                    }
                    
                    Trap trap = (Trap) objects.get(trapid);
                    long update = rtraps.getLong("version");
//                    System.out.println("get trap "+trapid+" version "+update+"  here is "+trap.last_trap_sync);
                    if (update > trap.last_trap_sync) {
                        
                        Player owner;
                        if (ownerid >= 0)
                            owner = (Player) objects.get(ownerid);
                        else
                            owner = null;
                        
                        // assignation
                        trap.setControl(owner, false);
                        trap.enable(enabled, false);
                        trap.last_trap_sync = update;
                        if (update < last_update)  last_update = update;
                    }
                }
                
                // synchronisation des objets physiques
                // recupérer les infos du serveur plus récentes que la derniere reception
                PreparedStatement reqobjects = sync.srv.prepareStatement("SELECT id,x,y,vx,vy,version,type FROM pobjects WHERE version > ? ");
                reqobjects.setLong(1, db_last_sync);
                
                ResultSet robjects = reqobjects.executeQuery();
                while (robjects.next()) {
                    int id = robjects.getInt("id");  
                    String type = robjects.getString("type");
                    PObject obj;
                    if (objects.containsKey(id)){
                        obj = objects.get(id);
                        if (type.equals("null")){
                            objects.remove(id);
                            System.out.println("Removed object " + obj + " still here : " + objects.containsKey(id));
                            continue;
                        }
                    } else obj = syncNewObject(id);
                    
                    if (obj == null) {
                        System.out.println("object "+id+" not found");
                        continue;
                    }
                    
                    long update = robjects.getLong("version");
                    if (update > obj.last_sync) {
                        obj.setPosition(new Vec2(robjects.getInt("x")/1000f, robjects.getInt("y")/1000f));
                        obj.velocity.x = robjects.getDouble("vx");
                        obj.velocity.y = robjects.getDouble("vy");
                        //System.out.println("updated object "+id);
                        
                        obj.last_sync = update;
                        if (update < last_update)  last_update = update;
                    }
                }
                robjects.close();
                
                
                // set last sync time
                db_last_sync = last_update;
            }
            catch (SQLException err) {
                System.out.println("syncUpdate: "+err);
            }
        }
    }
    
    public Sync getSync() {return sync;}
    
    public PObject syncNewPlayer(int db_id) {
        PObject obj;
        if (this.sync != null) {
            try {
                System.out.println("prep player id = " + db_id);
                PreparedStatement req = this.sync.srv.prepareStatement("SELECT * FROM players WHERE id = ?");
                req.setInt(1, db_id);
                ResultSet r = req.executeQuery();
                r.next();
                String name = r.getString("name");
                int avatar = r.getInt("avatar");
                int state = r.getInt("state");
                Player p = new Player(this, name, avatar, db_id);
                p.setState(state);
                obj = (PObject) p;
                System.out.println("added player " + name + " with skin #" + avatar);
                r.close();
                return obj;
                
            } catch (SQLException err) {
                System.out.println("syncNewPlayer: "+err);
            }
        }
        return null;
    }
    
    public PObject syncNewObject(int db_id) throws IOException {
        PObject obj;
        if (this.sync != null) {
            try {
                System.out.println("prep obj id = " + db_id);
                PreparedStatement req = this.sync.srv.prepareStatement("SELECT * FROM pobjects WHERE id = ?");
                req.setInt(1, db_id);
                ResultSet r = req.executeQuery();
                r.next();
                String type = r.getString("type");
                Vec2 position = new Vec2(r.getInt("x")/1000f, r.getInt("y")/1000f);
                double vx = r.getDouble("vx");
                double vy = r.getDouble("vy");
                int orientation = r.getInt("orientation");
                switch(type){
                    case("saw"): obj = new Saw(this, position); break;
                    case("spikes"): obj = new Spikes(this, orientation, position); break;
                    case("bomb"): obj = new Bomb(this, position); break;
                    case("laser"): obj = new Laser(this, position, orientation); break;
                    case("punch"): obj = new Punch(this, orientation, position); break;
                    default: try{
                        int platformType = Integer.valueOf(type);
                        obj = new Platform(this, position, Platform.standardBoxes[platformType], platformType, db_id);
                    } catch(NumberFormatException e) {
                        System.out.println("Unknown object tried to be added");
                        obj = null;
                    }
                }
                System.out.println("added object " + db_id);
                r.close();
                return obj;
                
            } catch (SQLException err) {
                System.out.println("syncNewObject: "+err);
            }
        }
        return null;
    }
    
  
    public void tryEndRound(){
        if (roundEnded) return;
        for (Player player : this.players){
            if (!(player.dead || player.hasReachedExitDoor || player.disconnected)) return;
        }
        setEndRoundScores();
        roundEnded = true;
//        switchToEditionMode(true);
        ScoreFrame scoreFrame = new ScoreFrame(this, new javax.swing.JFrame(), false);
        scoreFrame.show();
    }
          
    public void purge(){
        try {
            PreparedStatement req;
                // effacement de la table des objets
//                for (Player p: players){
//                    req = this.sync.srv.prepareStatement("DELETE FROM pobjects WHERE id = ?");
//                    req.setInt(1, p.db_id);
//                    req.executeUpdate();
//                }
            req = this.sync.srv.prepareStatement("DELETE FROM pobjects");
            req.executeUpdate();
            // effacement de la table de joueurs
            req = this.sync.srv.prepareStatement("DELETE FROM players");
            req.executeUpdate();

            System.out.println("Purged everything from db");
            req.close();
            
            PreparedStatement reqScores = this.sync.srv.prepareStatement("DELETE FROM scores");
            reqScores.executeUpdate();
            reqScores.close();
        }
        catch (SQLException err) {
            System.out.println("purge(): "+err);
        }
    }
    
    public void purgeTraps(){
        try {
            PreparedStatement req;
                // effacement de la table des traps
                req = this.sync.srv.prepareStatement("DELETE FROM traps");
                req.executeUpdate();

                System.out.println("Purged traps from db");
            req.close();
        }
        catch (SQLException err) {
            System.out.println("purgeTraps(): "+err);
        }
    }
    
    public Player getPlayerWithId(int id){
        for (Player p : players) if (p.db_id == id) return p;
        return null;
    }

    public void switchToEditionMode(boolean edition) {
        try {
            PreparedStatement req = this.sync.srv.prepareStatement("UPDATE server SET mode=?");
            if (edition) req.setInt(1, 0);
            else req.setInt(1, 1);
            req.executeUpdate();
            req.close();
            for (Player p: this.players) {
                if (!edition) {
                    p.setPosition(this.map.enter.position.add(new Vec2((this.map.enter.box.getWidth() - p.collision_box.getWidth())/2, 0 )));
                    p.acceleration.y = 0; p.acceleration.x = 0;
                    p.velocity.y = 0; p.velocity.x = 0;
                    roundEnded = false;
                }
//                p.syncReady(!edition);
//                    System.out.println("Player " + p.name + " now at pos " + p.position);
            }      
        }
        catch (SQLException err) {
            System.out.println("game init:\n"+err);
        }
        this.editionMode = edition;
    }

    private void setEndRoundScores() {
        ArrayList<Player> hasReached = new ArrayList<Player>();
        ArrayList<Player> hasDied = new ArrayList<Player>();
        for (Player player : this.players){
            if (!player.disconnected){
                if (player.dead) hasDied.add(player);
                if (player.hasReachedExitDoor) hasReached.add(player);
            }
        }
        if (!hasDied.isEmpty()){ // on ajoute les scores que pour le joueur contrôlé
            for (Player p : hasReached){
                if (p.isControled()) addScore(p, 0, 10);
            }
            if (hasReached.size() == 1 && hasReached.get(0).isControled()) addScore(hasReached.get(0), 2, 5);
        }
//        System.out.println(hasReached.get(0).name + hasReached.get(0).isControled());
    }

    public void addScore(Player player, int type, int amount) {
        if (this.sync != null) {
            try {
                PreparedStatement req = this.sync.srv.prepareStatement("INSERT INTO scores VALUES (?,?,?)");
                req.setInt(1, player.db_id);
                req.setInt(2, type);
                req.setInt(3, amount);
                req.executeUpdate();
                req.close();
            } catch (SQLException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void addScoreUponTrapKill(Trap trap, Player player){
        if (trap.currentcontrol != null && trap.currentcontrol.isControled() && !player.dead && !player.equals(trap.currentcontrol)) this.addScore(trap.currentcontrol, 1, 5);
    }
}
