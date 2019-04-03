/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.util.ArrayList;

/**
 *
 * @author ydejongh
 */
public class Map {
    public ArrayList<PObject> objects;
    public Box size;
    
    Map(Box size) { 
        this.size = size; 
        this.objects = new ArrayList<PObject>();
    }
    
    
    public static Map MapInitialization(){
        Box b = new Box(0,0,40,20);
        Map m = new Map(b);
        m.objects.add(new Platform(1, new Vec2(0,5)));
        for (int i=0; i<40; i++){
            
        } 
        return m;
    }
}
