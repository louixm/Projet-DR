/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;

/**
 *
 * @author ydejongh
 */
public class Platform extends PObject {
    Box collision_box;
    static Image img[];
    int typePlateforme;
    public static Box[] standardBoxes = {new Box(0,0,1,1), new Box(0,0,3,1), new Box(0,0,1,0.5), new Box(0,0,3,0.5), new Box(0,0,1.5,6), new Box(0,0,4,3), new Box(0,0,1,3), new Box(0,0,2,1), new Box(0,0,1,2), new Box(0,0,1,2)};
    
    public Platform(Game game, Vec2 position, Box box, int typePlateforme) throws IOException, SQLException {this(game, position, box, typePlateforme, -1);}
    public Platform(Game game, Vec2 position, Box box, int typePlateforme, int db_id) throws IOException, SQLException {
        super(game, String.valueOf(typePlateforme), db_id);
        this.collision_box = box;
        setPosition(position);
        this.typePlateforme = typePlateforme;
        
        if (img == null) {
            img = new Image[10];
            img[0] = ImageIO.read(new File("./images/patterns/Tile (5).png")); //plateforme 1x1
            img[1] = ImageIO.read(new File("./images/patterns/Tile (16).png")); //plateforme 1x3
            img[2] = ImageIO.read(new File("./images/patterns/Tile (13).png")); //plateforme 1/2x1
            img[3] = ImageIO.read(new File("./images/patterns/Tile (17).png")); //plateforme 1/2x3
            img[4] = ImageIO.read(new File("./images/Barrel_1_3.png"));
            img[5] = ImageIO.read(new File("./images/Barrel_2_4.png"));
            img[6] = ImageIO.read(new File("./images/patterns/Tile (18).png")); //mur hauteur 3
            img[7] = ImageIO.read(new File("./images/Tile_10.png"));
            img[8] = ImageIO.read(new File("./images/Tile_11.png"));
            img[9] = ImageIO.read(new File("./images/Tile_12.png"));
        }
    }
    public Platform rotation(Platform p0, double angle){  //la rotation de this autours de p0 d'un angle "angle"; [p0,this] est vertical par rapport à leur centre
        Vec2 centre0 = p0.position.add(new Vec2(p0.collision_box.getWidth()/2,p0.collision_box.getHeight()/2)) ;
        Vec2 centre1 = this.position.add(new Vec2(this.collision_box.getWidth()/2,this.collision_box.getHeight()/2)) ;
        double rayon = centre0.sub(centre1).norm();
        double pasX = rayon*Math.sin(Math.PI*angle/180);
        double pasY = rayon*Math.cos(Math.PI*angle/180);
        centre1 = centre0.add(new Vec2(pasX,pasY));
        this.setPosition(centre1.add(new Vec2(-this.collision_box.getWidth()/2,-this.collision_box.getHeight()/2)));
        return this;
    }
    
    public Platform(Game game, Vec2 position, double width, double height, int typePlateforme) throws IOException, SQLException {
        this(game, position, new Box(0, 0, width, height),typePlateforme);
    }
    
    @Override
    public void setPosition(Vec2 pos) {
        super.setPosition(pos);
        collision_box = collision_box.translateToPosition(pos);
    }
     
    //--------------- interface de gestion des collisions -----------------
    @Override
    public int collisionable(PObject other)  { 
        if (other instanceof Player)    return 1;
        else                            return 0;
    }
    @Override
    public Box getCollisionBox()       { return collision_box; }
    
    //--------------- interface d'affichage -----------------
    @Override
    public void render(Graphics2D canvas, float scale) {
        /*
        canvas.drawImage(img[typePlateforme], 
                (int) (position.x*scale), 
                (int) (position.y*scale), 
                null);    */
        canvas.drawImage(img[typePlateforme], 
                (int) (collision_box.p1.x*scale), 
                (int) (collision_box.p1.y*scale), 
                (int) (collision_box.p2.x*scale), 
                (int) (collision_box.p2.y*scale), 
                0, 0,
                img[typePlateforme].getWidth(null), img[typePlateforme].getHeight(null),
                null);
        super.render(canvas, scale);
        // TODO
    }
    
    
    
}
