/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.io.IOException;
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
    
    /*public ArrayList<PObject> positionnementColonne(int nbDePlatform){
        int H = (int) this.size.getHeight();
        int L = (int) this.size.getWidth();
        int hauteurDeSaut = 2;
        Vec2 v = new Vec2(0,0);
        Platform p = new Platform(1,v,3);
        int pas = H/nbDePlatform;
    }*/
    
    
    public static Map MapInitialization(int mapNumber) throws IOException{
        Box b = new Box(0,0,35,20);
        Map m = new Map(b); 
        double ratioY = b.getHeight()/20;
        double ratioX = b.getWidth()/40;
        double hauteurDeSaut = 2;
        
        if (mapNumber == 1){
            m.objects.add(new Platform(1, new Vec2(0*ratioX,7*ratioY), 3*ratioX, (hauteurDeSaut/4)*ratioY));
            m.objects.add(new Platform(1, new Vec2(0*ratioX, 12*ratioY),   6*ratioX, 0.5*ratioY));
            m.objects.add(new Platform(1, new Vec2(0*ratioX, 18*ratioY), 25*ratioX, 0.5*ratioY));
        
            m.objects.add(new Platform(2, new Vec2(27*ratioX, 4*ratioY), 1*ratioX, 4*ratioY));
            m.objects.add(new Platform(2, new Vec2(26*ratioX, 10*ratioY), 3*ratioX, 3*ratioY));
        
            m.objects.add(new Platform(2, new Vec2(30*ratioX, 18*ratioY), 10*ratioX, 0.5*ratioY));
            m.objects.add(new Platform(2, new Vec2(33*ratioX, 10*ratioY), 7*ratioX, 0.5*ratioY));
        
            m.objects.add(new Platform(3, new Vec2(5*ratioX, 16*ratioY), 3.5*ratioX, 0.5*ratioY));
        
            m.objects.add(new Platform(4, new Vec2(4*ratioX, 10*ratioY), 12*ratioX, 0.5*ratioY));
            m.objects.add(new Platform(4, new Vec2(9*ratioX, 6*ratioY), 10*ratioX, 0.5*ratioY));
            m.objects.add(new Platform(4, new Vec2(11*ratioX, 3*ratioY), 7*ratioX, 0.5*ratioY));
        }
        if (mapNumber == 2){
            
            Vec2 v1 = new Vec2(0*ratioX,2*ratioY);
            Vec2 v2 = new Vec2(b.getWidth()*ratioX,2*ratioY);
            double diag = Math.sqrt(b.getHeight()*b.getHeight() + b.getWidth()*b.getWidth());
            int pasDiag = (int)diag/6;
            
            // La box de la Map n'est pas carrée, donc le pas suivant un axe est fonction de la diagonale et son inclinaison par rapport à l'axe 
            
            double cos = b.getWidth()/diag;
            double sin = b.getHeight()/diag;
            double pasX = pasDiag*cos;
            double pasY = pasDiag*sin;
            
            int i = 0;
            while (v1.x+2*ratioX < b.getWidth() & v1.y+0.5*ratioY <b.getHeight() ){
                
                m.objects.add(new Platform(1, v1, 2*ratioX, 0.5*ratioY));
                v1=v1.add(new Vec2(pasX*ratioX,pasY*ratioY));
                
                m.objects.add(new Platform(1, v2, 2*ratioX, 0.5*ratioY));
                v2=v2.add(new Vec2(-pasX*ratioX,pasY*ratioY));
                
                i=i+(int)pasDiag;
            }
        }
        return m;
    }
}
