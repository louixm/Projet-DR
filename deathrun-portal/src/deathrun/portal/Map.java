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
        Box b = new Box(0,0,30,15);
        Map m = new Map(b); 
        double ratioY = b.getHeight()/15;
        double ratioX = b.getWidth()/30;
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
            
            double l = b.getWidth()/15;
            double h = b.getHeight()/40;
            double decalage = hauteurDeSaut;  //revoir le dimensionnement de sorte la modification de ça comprime la map au lieu de la translater
            int nbPlatform=10;
            Vec2 v1 = new Vec2(0*ratioX,decalage*ratioY);
            Vec2 v2 = new Vec2(b.getWidth()-l,decalage*ratioY);
            
            double ecartX1 = (b.getWidth()-(nbPlatform*l))/(nbPlatform-1);
            double ecartY1 = ((b.getHeight()/*-2*decalage*/)-(nbPlatform*h))/(nbPlatform-1);
            double pasX1 = ecartX1 + l;
            double pasY1 = ecartY1 + h;
            
            boolean verif = false;
            int a1 = 0;
            int a2 = 0;
            while (!verif){
                if ((int)v1.x <= (int)b.getWidth()-l){
                    m.objects.add(new Platform(1, v1, l, h));
                    v1=v1.add(new Vec2(pasX1,pasY1));
                    a1 = 1;
                } else{
                    a1=0;
                }
                
                if ((int)v2.x >= 0){
                    m.objects.add(new Platform(1, v2, l, h));
                    v2=v2.add(new Vec2(-pasX1,pasY1));
                    a2 = 1;
                } else{
                    a2=0;
                }
                if (a1==0 & a2==0){
                    verif = true;
                }
            }
            m.objects.add(new Platform(1, new Vec2(0.5*pasX1,((b.getHeight()-2*h)/2)+decalage),2.5*l,1.5*h));
            m.objects.add(new Platform(1, new Vec2(0,((b.getHeight()-2*h)/4)+decalage),l,1.5*h));
            m.objects.add(new Platform(1, new Vec2(0,((b.getHeight()-2*h)*3/4)+decalage),l,1.5*h));
            
            m.objects.add(new Platform(1, new Vec2(b.getWidth()-(2.5*l),((b.getHeight()-2*h)/2)+decalage),2.5*l,1.5*h));
            m.objects.add(new Platform(1, new Vec2(b.getWidth()-l,((b.getHeight()-2*h)/4)+decalage),l,1.5*h));
            m.objects.add(new Platform(1, new Vec2(b.getWidth()-l,((b.getHeight()-2*h)*3/4)+decalage),l,1.5*h));
            
            m.objects.add(new Platform(1, new Vec2(0.5*(b.getWidth()-h),pasY1),h,2*l));
            m.objects.add(new Platform(1, new Vec2((0.5*(b.getWidth()-h))-2*l, decalage+pasY1), l, h)); //revoir la position de son x
        }
        return m;
    }
}
