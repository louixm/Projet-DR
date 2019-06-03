/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.abs;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author ydejongh
 */
public class Player extends PObject {
    public String name; // pseudo du joueur
    public int avatar;  // numéro d'avatar choisi
    public boolean controled = false;   // vrai si le joueur est le joueur actuellement controlé par ce client
    ArrayList<Trap> traps;  // pieges actuellement controlés par le joueur
    
    // status du joueur dans la partie
    public boolean dead = false, hasReachedExitDoor = false, disconnected = false;
    
    // variables internes de déplacement
    boolean left, right, jump, leftAndRightWithPriorityOnRight; //, hasJumped;
    ArrayList<String> collisionDirection;

    // variable interne de collisions
    Box collision_box;
    Box visual_box;
        
    Game game;  // pour la synchronisation a la table des joueurs
    public static BufferedImage avatars[][];  // avatars disponibles pour chaque joueur
    
    int compteurdanimation = 1;    //compteur pour les animations de personnage
    long time_next_image;
    long image_duration = 100000000;
    int lignedevue = 1;
    
    int compteurdanimation_death = 1;    //compteur pour les animations de personnage
    long time_next_image_death;
    long image_duration_death = 200000000;
    //int lignedevue_death = 1;
    BufferedImage current_image;
    
    public static int availableId(Game game) {
        int id = -1;
        boolean id_found = true;
        while (id_found) {
            id_found = false;
            for (int i=0; i<game.players.size(); i++) {
                if (game.players.get(i).db_id == id)    {
                    id_found = true;
                    --id;
                    break;
                }
            }
        }
        System.out.println("id = " + id);
        return id;
    }
    
    
    public Player(Game game, String name, int avatar) throws SQLException {
        super(game, availableId(game), name);  // creer en ajoutant a la fin
        this.game = game;
        this.name = name; 
        this.avatar = avatar;
        
        collision_box = new Box(-0.2, 0, 0.2, 1.7);
        visual_box = new Box(-0.5, 0, 0.5, 1.8);
        traps = new ArrayList<Trap>();
        if (avatars == null) {
            this.avatars = new BufferedImage[4][];
            try {
                this.avatars[0] = new BufferedImage[] { 
                    ImageIO.read(new File("./images/robotbase.png")),
                    ImageIO.read(new File("./images/robotdroite.png")),
                    ImageIO.read(new File("./images/robotdroitee.png")),
                    ImageIO.read(new File("./images/robotsaut.png")),
                    ImageIO.read(new File("./images/robotbasegauche.png")),
                    ImageIO.read(new File("./images/robotgauche.png")),
                    ImageIO.read(new File("./images/robotdgaucheex.png")),
                    ImageIO.read(new File("./images/robotsautgauche.png")),
                    ImageIO.read(new File("./images/robotDead.png")),
                    
                };
                this.avatars[1] = new BufferedImage[]{
                    ImageIO.read(new File("./images/tourellebase.png")),
                    ImageIO.read(new File("./images/tourelledroite2.png")),
                    ImageIO.read(new File("./images/tourelledroite3.png")),
                    ImageIO.read(new File("./images/tourellesautdroite.png")),
                    ImageIO.read(new File("./images/tourellebasegauche.png")),
                    ImageIO.read(new File("./images/tourellegauche2.png")),
                    ImageIO.read(new File("./images/tourellegauche3.png")),
                    ImageIO.read(new File("./images/tourellesautgauche.png")),
                    ImageIO.read(new File("./images/robotDead.png")),
            
                 };
                this.avatars[2] = new BufferedImage[]{
                    ImageIO.read(new File("./images/sentrybotbasedroite.png")),
                    ImageIO.read(new File("./images/sentrybotdroite1.png")),
                    ImageIO.read(new File("./images/sentrybotdroite2.png")),
                    ImageIO.read(new File("./images/sentrybotsaut1.png")),
                    ImageIO.read(new File("./images/sentrybotbasegauche.png")),
                    ImageIO.read(new File("./images/sentrybotgauche1.png")),
                    ImageIO.read(new File("./images/sentrybotgauche2.png")),
                    ImageIO.read(new File("./images/sentrybotsaut2.png")),
                    ImageIO.read(new File("./images/robotDead.png")),
                 };
                this.avatars[3] = new BufferedImage[]{
                    ImageIO.read(new File("./images/robotdeath1v2.png")),
                    ImageIO.read(new File("./images/robotdeath2v2.png")),
                    ImageIO.read(new File("./images/robotdeath3v2.png")),
                    ImageIO.read(new File("./images/robotdeath4v2.png")),
                    ImageIO.read(new File("./images/robotdeath5v2.png")),
                };
                //this.avatars[3] = new BufferedImageImageIO.read(new File("./images/robotDead.png"));
                
                //this.avatars[2] = ImageIO.read(new File("./images/robotOrange.png"));
                //this.robotBase = ImageIO.read(new File("robotbase.png"));    // rajouté par louis animation
                //this.robotDr = ImageIO.read(new File("robotdroite.png"));    // rajouté par louis animation
                //this.robotDrEx = ImageIO.read(new File("robotdroitee.png"));    // rajouté par louis animation
                //this.robotSaut = ImageIO.read(new File("robotsaut.png"));    // rajouté par louis animation
                //this.robotBasegauche = ImageIO.read(new File("robotbasegauche.png"));    // rajouté par louis animation
                //this.robotgauche = ImageIO.read(new File("robotgauche.png"));    // rajouté par louis animation
                //this.robotgaucheex = ImageIO.read(new File("robotdgaucheex.png"));    // rajouté par louis animation
                //this.robotsautgauche = ImageIO.read(new File("robotsautgauche.png"));    // rajouté par louis animation
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // horloge pour l'affichage des animations
        time_next_image = System.nanoTime() + image_duration;
        time_next_image_death = System.nanoTime() + image_duration_death;
        System.out.println("avatar = "+avatar);
        System.out.println("avatars[avatar] = "+avatars[avatar]);
        current_image = this.avatars[avatar][0];
        
        //double j = game.map.enter.position.y + game.map.enter.size - this.avatars.getHight();
        this.setPosition(game.map.enter.position.add(new Vec2(-0.25,0)));
        
        
        if (game.sync != null) {
            PreparedStatement req = game.sync.srv.prepareStatement("SELECT EXISTS(SELECT id FROM players WHERE id = ?)");
            req.setInt(1, db_id);
            ResultSet r = req.executeQuery();
            r.next();
            if (!r.getBoolean(1)) {
                req.close();
                req = game.sync.srv.prepareStatement("INSERT INTO players VALUES (?, ?, 0, 0, ?, 0, 0, 0)"); //TODO
                req.setInt(1, db_id);
                req.setString(2, name);
                req.setInt(3, avatar);
                req.executeUpdate();
                req.close();
                System.out.println("ok");
            }
        }
        
        // tout est pret, declaration du nouveau joueur
        game.players.add(this);
    }
    
    public void disconnect() {
        disconnected = true;
        try {
            // effacement de la table des objets
            boolean allDisconnected = true;
            for (Player p: game.players){
                if (!p.disconnected)  {allDisconnected = false; break;}
            }
            if (allDisconnected) game.purge();
            else {
                PreparedStatement req;
                req = game.sync.srv.prepareStatement("UPDATE players SET state=? WHERE id = ?");
                if (this.dead) req.setInt(1, 4);
                else req.setInt(1, 3);
                req.setInt(2, db_id);
                req.executeUpdate();
                req.close();
                System.out.println("Player " + name + " disconnected.");
            }
//            req = game.sync.srv.prepareStatement("DELETE FROM pobjects WHERE id = ?");
//            req.setInt(1, db_id);
//            req.executeUpdate();
//            // effacement de la table de joueurs
//            req = game.sync.srv.prepareStatement("DELETE FROM players WHERE id = ?");
//            req.setInt(1, db_id);
//            req.executeUpdate();
//            
//            System.out.println("Deleted player with id " + db_id);
            
        }
        catch (SQLException err) {
            System.out.println("Player.disconnect(): "+err);
        }
    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
        visual_box = visual_box.translateToPosition(pos.add(new Vec2(-0.3,0)));
    }
    
    //--------------- interface de gestion des collisions -----------------
    public int collisionable(PObject other) { 
        if (other instanceof Player)    return 0;
        else                            return 1;
    }
    @Override
    public Box getCollisionBox() 	{ return collision_box; }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D g, float scale) {
        if (avatar!=3){
            long ac_time = System.nanoTime();
            if (ac_time > time_next_image)  {
        
                if (left) { 
                    lignedevue =2;
                
                    if (compteurdanimation == 1){
                        current_image = avatars[avatar][4];
                        compteurdanimation = compteurdanimation + 1;
                    }
                    else if (compteurdanimation == 2){
                        current_image = avatars[avatar][5];
                        compteurdanimation = compteurdanimation + 1;
                    }
                    else if (compteurdanimation == 3){
                        current_image = avatars[avatar][6];
                        compteurdanimation = 1;
                    }
            
                }
                if (right) { 
                    lignedevue =1;
                    if (compteurdanimation == 1){
                        current_image = avatars[avatar][0];
                        compteurdanimation++;
                    }
                    else if (compteurdanimation == 2){
                        current_image = avatars[avatar][1];
                        compteurdanimation++;
                    }
                    else if (compteurdanimation == 3){
                        current_image = avatars[avatar][2];
                        compteurdanimation = 1;
                    }
                
            
                }
                if (jump){
                    if (lignedevue == 1){
                        current_image = avatars[avatar][3];
                    }
                    if (lignedevue == 2) {
                        current_image = avatars[avatar][7];
                    }
                }
                if (!left && !right && !jump){
                    if (lignedevue ==1){
                        current_image = avatars[avatar][0];
                    }
                    if (lignedevue ==2){
                        current_image = avatars[avatar][4];
                    }
                }
                time_next_image = ac_time + image_duration;
            }
            
           
            
            
        }
        if (avatar==3){
            long ac_time = System.nanoTime();
            if (ac_time > time_next_image_death)  {
                    if (compteurdanimation_death == 1){
                        current_image = avatars[3][0];
                        compteurdanimation_death++;
                    }
                    else if (compteurdanimation_death == 2){
                        current_image = avatars[3][1];
                        compteurdanimation_death++;
                    }
                    else if (compteurdanimation_death == 3){
                        current_image = avatars[3][2];
                        compteurdanimation_death++;
                    }
                    else if (compteurdanimation_death == 4){
                        current_image = avatars[3][3];
                        compteurdanimation_death++;
                    }
                    else if (compteurdanimation_death == 5){
                        current_image = avatars[3][4];
                        compteurdanimation_death = 1;
                    }
                    time_next_image_death = ac_time + image_duration_death;
                }
            
            
            }
    
        
        g.drawImage(current_image, 
            (int) (visual_box.p1.x*scale), 
            (int) (visual_box.p1.y*scale), 
            (int) (visual_box.p2.x*scale), 
            (int) (visual_box.p2.y*scale), 
            0, 0,
            current_image.getWidth(null), current_image.getHeight(null),
            null);
        
        g.setColor(getPlayerColor());
        drawCenteredString(g, name, collision_box, 3, new Font("Trebuchet MS", Font.BOLD, 13), scale);
        if (disconnected)
            g.setColor(Color.DARK_GRAY); drawCenteredString(g, "Disconnected", collision_box, 15, new Font("Trebuchet MS", Font.ITALIC, 10), scale);
        super.render(g, scale);
    }

    public Color getPlayerColor(){
        switch(this.db_id){
            case(-1): return Color.BLUE;
            case(-2): return Color.ORANGE;
            case(-3): return Color.GREEN;
            case(-4): return Color.PINK;
            case(-5): return Color.CYAN;
            case(-6): return Color.RED;
            case(-7): return Color.YELLOW;
            case(-8): return Color.MAGENTA; 
            default: return Color.WHITE;
        }
    }
    
    public void setLeft(boolean left) {
        if (dead)   return;
        this.left = left;
        if (this.right) this.leftAndRightWithPriorityOnRight = false;
    }
    public void setRight(boolean right) { 
        if (dead)   return;
        this.right = right;
        if (this.left) this.leftAndRightWithPriorityOnRight = true;
    }
    public void setJump(boolean jump) { 
        if (dead)   return;
        this.jump = jump;  
    }
    
    public void setDead(boolean dead) { setDead(dead, controled); }
    public void setDead(boolean dead, boolean syncAndEndRound) {
        if (!this.dead) {
            this.dead = dead;
            if (dead) {
                avatar = 3;
                if(syncAndEndRound){
                    System.out.println("player "+name+" is dead");
                    try {
                        PreparedStatement req = game.sync.srv.prepareStatement("UPDATE players SET state=? WHERE id = ?");
                        req.setInt(1, 1); //state = 0 (en vie), 1 (dead), 2 (exit door)
                        // id de l'objet a modifier
                        req.setInt(2, db_id);

                        // execution de la requete
                        req.executeUpdate();
                        req.close();
                    }
                    catch (SQLException err) {
                        System.out.println("sql exception:\n"+err);
                    }
                    game.tryEndRound();
                }
            }
        }
    }
    
    @Override
    public void onGameStep(Game game, float dt) {
        applyMovementChanges(dt);
    }
    
    public void applyMovementChanges(float dt){
//        if (dead || hasReachedExitDoor)   return;
        
        if (!(dead || hasReachedExitDoor) && this.left && (!this.right || !this.leftAndRightWithPriorityOnRight)){
            if (this.velocity.x > 0)        this.acceleration.x = -40;
            else if (this.velocity.x > -7)  this.acceleration.x = -20;
            else                            this.acceleration.x = 0;
            syncMovement(1);
        }
        else if (!(dead || hasReachedExitDoor) && this.right && (!this.left || this.leftAndRightWithPriorityOnRight)){
            if (this.velocity.x < 0)        this.acceleration.x = 40;
            else if (this.velocity.x < 7)   this.acceleration.x = 20;
            else                            this.acceleration.x = 0;
            syncMovement(2);
        }       
        else{
            if (abs(this.velocity.x) > 1)        this.acceleration.x = -40 * abs(this.velocity.x)/this.velocity.x;
            else                                {this.acceleration.x = 0;      this.velocity.x = 0;}
            if ((this.velocity.x + this.acceleration.x*dt) * this.velocity.x < 0) 
                                                {this.acceleration.x = 0;      this.velocity.x = 0;};
            syncMovement(0);
        }
        
        if (!(dead || hasReachedExitDoor) && this.jump) {
            if (acceleration.y == 0)    this.velocity.y = -6;
            else if (velocity.y > 0 && (
                    (this.right && this.collisionDirection.contains("right")) 
                 || (this.left && this.collisionDirection.contains("left"))))
            {
                this.velocity.y = -5;
                if (this.right) this.velocity.x = -12;               
                else            this.velocity.x = 12;
            }
            syncMovement(3);
        }
    }
    
    public void setCollisionDirection(ArrayList<String> array){ this.collisionDirection = array; }
    public void addCollisionDirection(String direction){ this.collisionDirection.add(direction); }
    public ArrayList<String> getCollisionDirection(){ return this.collisionDirection; }
    
    public void setControled(boolean controled){ this.controled = controled; }
    public boolean isControled(){ return this.controled; }
    
    public void syncMovement(int move){
        if (game.sync != null){
            try {
                PreparedStatement req = game.sync.srv.prepareStatement("UPDATE players SET movement=? WHERE id = ?");
                req.setInt(1, move); 
                // id de l'objet a modifier
                req.setInt(2, db_id);

                // execution de la requete
                req.executeUpdate();
                req.close();
            }
            catch (SQLException err) {
                System.out.println("sql exception:\n"+err);
            }
        }
    }
    
    public void setState(int state){
        switch(state){
            default: {this.setDead(false, false); this.hasReachedExitDoor = false; this.disconnected = false; break;}
            case 1: {this.setDead(true, false); this.hasReachedExitDoor = false; this.disconnected = false; break;}
            case 2: {this.setDead(false, false); this.hasReachedExitDoor = true; this.disconnected = false; break;}
            case 3: {this.setDead(false, false); this.hasReachedExitDoor = false; this.disconnected = true; break;}
            case 4: {this.setDead(true, false); this.hasReachedExitDoor = false; this.disconnected = true; break;}
        }
    }
    
    public void setMovement(int movement){
        switch(movement){
            default: {this.setLeft(false); this.setRight(false); this.setJump(false); break;}
            case 1: {this.setLeft(true); this.setRight(false); this.setJump(false); break;}
            case 2: {this.setLeft(false); this.setRight(true); this.setJump(false); break;}
            case 3: {this.setLeft(false); this.setRight(false); this.setJump(true); break;}    
        }
    }
    
    public void drawCenteredString(Graphics g, String text, Box box, int up, Font font, float scale) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = (int) (box.p1.x*scale) + ((int) (box.getWidth()*scale) - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y;
        y = (int) (box.p1.y*scale) - up; //+ (((int) (box.getHeight()*scale) - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }
    
}
