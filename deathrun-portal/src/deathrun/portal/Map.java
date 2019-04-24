/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.io.IOException;
import java.sql.SQLException;
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
    
    
    public static Map MapInitialization(Game g, int mapNumber) throws IOException, SQLException{
        Box b = new Box(0,0,30,20);
        Map m = new Map(b); 
        double ratioY = b.getHeight()/20;      //Les ratios permettent juste de mettre les plateformes à la bonne échelle
        double ratioX = b.getWidth()/40;
        double hauteurDeSaut = 2;
        double hauteurPlatform = 1;
        
        if (mapNumber == 1) {
            m.objects.add(new Platform(g, new Vec2(0*ratioX,5*ratioY), 3*ratioX, hauteurPlatform*ratioY,0));
            m.objects.add(new Platform(g, new Vec2(0*ratioX, 12*ratioY),   6*ratioX, hauteurPlatform*ratioY,1));
            m.objects.add(new Platform(g, new Vec2(0*ratioX, 18*ratioY), 25*ratioX, hauteurPlatform*ratioY,1));
        
            m.objects.add(new Platform(g, new Vec2(26.5*ratioX, 3*ratioY), 2*ratioX, 5*ratioY,4));
            m.objects.add(new Platform(g, new Vec2(25.5*ratioX, 10*ratioY), 4*ratioX, 4*ratioY,5));
        
            m.objects.add(new Platform(g, new Vec2(30*ratioX, 18*ratioY), 10*ratioX, hauteurPlatform*ratioY,1));
            m.objects.add(new Platform(g, new Vec2(33*ratioX, 10*ratioY), 7*ratioX, hauteurPlatform*ratioY,1));
        
            m.objects.add(new Platform(g, new Vec2(12*ratioX, 14*ratioY), 3.5*ratioX, hauteurPlatform*ratioY,1));
        
            m.objects.add(new Platform(g, new Vec2(6*ratioX, 8.5*ratioY), 12*ratioX, hauteurPlatform*ratioY,1));
            m.objects.add(new Platform(g, new Vec2(10*ratioX, 5*ratioY), 10*ratioX, hauteurPlatform*ratioY,1));
            m.objects.add(new Platform(g, new Vec2(12*ratioX, 2*ratioY), 7*ratioX, hauteurPlatform*ratioY,1));
        }
        else if (mapNumber == 0){
            int nbPlatform=10;   //doit être un diviseur de 360;
            int pas = 360/nbPlatform;
            double rayon = b.getHeight()/4;
            double pasX = rayon*Math.cos(pas);
            double pasY = rayon*Math.sin(pas);
            double translationX = 0;
            double translationY = 0;
            
            double l = 4;
            double h = 1;
            
            Vec2 v0 = new Vec2((b.getWidth()-l)/2,(b.getHeight()-h)/2);
            Vec2 v1 = v0.add(new Vec2(0,-rayon));
            Vec2 v = new Vec2(0,0);
            Vec2 trans = new Vec2(translationX,translationY);
            
            int arret = 0;
            while (arret!=360){
                if (translationX < rayon & translationX > -rayon){
                    translationX = translationX + pasX;
                }else{
                    translationX = translationX - pasX;
                }
                if (translationY < rayon & translationY > -rayon){
                    translationY = translationY + pasY;
                }else{
                    translationY = translationY - pasY;
                }
                v = v1.add(trans);
                m.objects.add(new Platform(g, v, l, h,3));
                arret = arret + pas;
            }
            //for int i=0; i<360
            m.objects.add(new Platform(g, new Vec2(0*ratioX,7*ratioY), 3*ratioX, (hauteurDeSaut/4)*ratioY,1));
            m.objects.add(new Platform(g, new Vec2(0*ratioX, 12*ratioY),   6*ratioX, 0.5*ratioY,1));
            m.objects.add(new Platform(g, new Vec2(0*ratioX, 18*ratioY), 25*ratioX, 0.5*ratioY,1));
        
            m.objects.add(new Platform(g, new Vec2(27*ratioX, 4*ratioY), 1*ratioX, 4*ratioY,4));
            m.objects.add(new Platform(g, new Vec2(26*ratioX, 10*ratioY), 3*ratioX, 3*ratioY,5));
        
            m.objects.add(new Platform(g, new Vec2(30*ratioX, 18*ratioY), 10*ratioX, 0.5*ratioY,1));
            m.objects.add(new Platform(g, new Vec2(33*ratioX, 10*ratioY), 7*ratioX, 0.5*ratioY,1));
        
            m.objects.add(new Platform(g, new Vec2(5*ratioX, 16*ratioY), 3.5*ratioX, 0.5*ratioY,1));
        
            m.objects.add(new Platform(g, new Vec2(4*ratioX, 10*ratioY), 12*ratioX, 0.5*ratioY,1));
            m.objects.add(new Platform(g, new Vec2(9*ratioX, 6*ratioY), 10*ratioX, 0.5*ratioY,1));
            m.objects.add(new Platform(g, new Vec2(11*ratioX, 3*ratioY), 7*ratioX, 0.5*ratioY,1));
        }
        else if (mapNumber == 2 || mapNumber == 3 || mapNumber == 4 || mapNumber == 5){
            int nbPlatform = 1;
            if (mapNumber == 2){
                nbPlatform=5;
            }
            if (mapNumber == 3){
                nbPlatform=10;
            } 
            if (mapNumber == 4){
                nbPlatform=8;
            }
            if (mapNumber == 5){
                nbPlatform=11;
            }
            double l = b.getWidth()/(2*nbPlatform);
            double h = b.getHeight()/(4*nbPlatform);
            double decalage = hauteurDeSaut;
            Vec2 v1 = new Vec2(0,decalage);
            Vec2 v2 = new Vec2(b.getWidth()-l,decalage);
            
            double ecartX1 = (b.getWidth()-(nbPlatform*l))/(nbPlatform-1);
            double ecartY1 = ((b.getHeight()-decalage)-(nbPlatform*h))/(nbPlatform-1);
            double pasX1 = ecartX1 + l;
            double pasY1 = ecartY1 + h;
            
            boolean verif = false;
            int a1 = 0;
            int a2 = 0;
            while (!verif){
                if ((int)v1.x <= (int)b.getWidth()-l){
                    m.objects.add(new Platform(g, v1, l, h,3));
                    v1=v1.add(new Vec2(pasX1,pasY1));
                    a1 = 1;
                } else{
                    a1=0;
                }
                
                if ((int)v2.x >= 0){
                    m.objects.add(new Platform(g, v2, l, h,3));
                    v2=v2.add(new Vec2(-pasX1,pasY1));
                    a2 = 1;
                } else{
                    a2=0;
                }
                if (a1==0 & a2==0){
                    verif = true;
                }
            }
            m.objects.add(new Platform(g, new Vec2(0.5*l,((b.getHeight()+decalage-2*h)/2)),2.5*l,2*h,1));
            //m.objects.add(new Platform(1, new Vec2(0,((b.getHeight()+3*decalage-0.375*h)/4)),l,1.5*h));
           // m.objects.add(new Platform(1, new Vec2(0,((3*b.getHeight()+decalage-0.375*h)/4)),l,1.5*h));
            
            m.objects.add(new Platform(g, new Vec2(b.getWidth()-(3*l),((b.getHeight()+decalage-h)/2)),2.5*l,2*h,1));
            //m.objects.add(new Platform(1, new Vec2(b.getWidth()-l,((b.getHeight()+3*decalage-0.375*h)/4)),l,1.5*h));
            //m.objects.add(new Platform(1, new Vec2(b.getWidth()-l,((3*b.getHeight()+decalage-0.375*h)/4)),l,1.5*h));
            
            //m.objects.add(new Platform(1, new Vec2(0.5*(b.getWidth()-h),decalage/2),h,(b.getHeight()/2)-h-2*decalage));
            m.objects.add(new Platform(g, new Vec2(0.5*b.getWidth()-1.5*l, decalage+pasY1), 3*l, h,0)); 
            
            m.objects.add(new Platform(g, new Vec2(0.5*b.getWidth()-3*l, b.getHeight()-2*h), 2*l, 2*h,2));
            m.objects.add(new Platform(g, new Vec2(0.5*b.getWidth()+l, b.getHeight()-2*h), 2*l, 2*h,2));
        }
        return m;
    }
}
