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
    
    public ExitDoor exit;
    public EnterDoor enter;
    
    Map(Box size) { 
        this.size = size; 
        this.objects = new ArrayList<PObject>();
    }
    
    
    public static Map MapInitialization(Game g, int mapNumber) throws IOException, SQLException{
        Box b = new Box(0,0,30,20);
        Map m = new Map(b);
        
        m.enter = new EnterDoor(g,new Vec2(0,0));
        m.exit = new ExitDoor(g,new Vec2(0,0));
        
        double ratioY = b.getHeight()/20;      //Les ratios permettent juste de mettre les plateformes à la bonne échelle
        double ratioX = b.getWidth()/40;
        double hauteurDeSaut = 2;
        double hauteurPlatform = 1;
        
        if (mapNumber == 1) {
            m.objects.add(new Platform(g, new Vec2(0,0),1,2,6));
            m.objects.add(new Platform(g, new Vec2(15,15),1,2,1));
            m.objects.add(new Platform(g, new Vec2(20,20),1,2,2));
            m.objects.add(new Platform(g, new Vec2(10,10),1,2,3));
            m.enter.setPosition(new Vec2(0, 15-m.enter.size));
            m.objects.add(m.enter);
            m.exit.setPosition(new Vec2(20, 5-m.enter.size));
            m.objects.add(m.exit);
        }
        else if (mapNumber == 4){
            //portails
            Portal port = new Portal(g,new Vec2(13, 1), new boolean[] {false,true});
            Portal port2 = new Portal(g,new Vec2(13,16), new boolean[] {false,true});
            port.otherPortal = port2;
            port2.otherPortal = port;
            m.objects.add(port);
            m.objects.add(port2);
            
            //pieges
            m.objects.add(new Saw(g, g.map.size.center().sub(new Vec2(1, 1))));
            //m.objects.add(new Laser(g,new Vec2(0, 11),0));
            //m.objects.add(new Punch(g,2,new Vec2(21,16)));
            m.objects.add(new Acid(g, new Vec2(6,13)));
            
            //Croix centrale
            m.objects.add(new Platform(g, new Vec2(-1+b.getWidth()/2,b.getHeight()/4),1,b.getHeight()/2,6));
            m.objects.add(new Platform(g, new Vec2((b.getWidth()/2)-5,b.getHeight()/2),10,1,1));
            
            //entree
            m.enter.setPosition(new Vec2(0, 0));
            m.objects.add(m.enter);
            m.objects.add(new Platform(g, new Vec2(0,m.enter.size),10,1,1));
            
            //sortie
            m.exit.setPosition(new Vec2(b.getWidth()-3, 10));
            m.objects.add(m.exit);
            m.objects.add(new Platform(g, new Vec2(b.getWidth()-5,10-m.exit.size/2),5,1,1));
            m.objects.add(new Platform(g, new Vec2(b.getWidth()-3,10+m.exit.size),3,1,1));
            m.objects.add(new Platform(g, new Vec2(b.getWidth()-5,10-m.exit.size/2),1,m.exit.size,6));
            
            //cote gauche
            m.objects.add(new Platform(g, new Vec2(0,b.getHeight()/2),5,1,1));
            m.objects.add(new Platform(g, new Vec2(2,b.getHeight()-1),10,1,1));
            m.objects.add(new Platform(g, new Vec2(1,b.getHeight()-1),10,b.getWidth(),1));
            
            //cote droit
            m.objects.add(new Platform(g, new Vec2(5+b.getWidth()/2,5),5,1,1));
            m.objects.add(new Platform(g, new Vec2(b.getWidth()-13,b.getHeight()-1),10,1,1));
                                     
        }
        
        else if (mapNumber == 2 || mapNumber == 3){
            int nbPlatform = 1;
            if (mapNumber == 2){
                nbPlatform=5;
            }
            if (mapNumber == 3){
                nbPlatform=8;
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
            m.enter.setPosition(new Vec2(l, ((b.getHeight()+decalage-2*h)/2)-m.enter.size));
            m.objects.add(m.enter);
            //m.objects.add(new Platform(1, new Vec2(0,((b.getHeight()+3*decalage-0.375*h)/4)),l,1.5*h));
           // m.objects.add(new Platform(1, new Vec2(0,((3*b.getHeight()+decalage-0.375*h)/4)),l,1.5*h));
            
            m.objects.add(new Platform(g, new Vec2(b.getWidth()-(3*l),((b.getHeight()+decalage-h)/2)),2.5*l,2*h,1));
            m.exit.setPosition(new Vec2(b.getWidth()-(0.5*m.exit.size)-1.75*l, ((b.getHeight()+decalage-h)/2)-m.exit.size));
            m.objects.add(m.exit);
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
//else if (mapNumber == 4){
//            
//            
//            int nbPlatform=6;   //doit être un diviseur de 360;
//            int pas = 360/nbPlatform;  // en dégré
//            double rayon = 3*b.getHeight()/8;
//            
//            double l = 3;
//            double h = 1;
//            
//            Vec2 v0 = new Vec2((b.getWidth()-l)/2,(b.getHeight()-h)/2);
//            Vec2 v1 = v0.add(new Vec2(0,-rayon));
//            
//            Platform p0 = new Platform(g,v0,l,h,3);
//            
//            int i=0;
//            while (i<nbPlatform) {
//                Platform p1 = new Platform(g,v1,l,h,3);
//                m.objects.add(p1.rotation(p0, i*pas));
//                i++;
//            }
//            m.objects.add(new Platform(g, new Vec2(0,(b.getHeight()-h)/2),l,h,2));
//            m.objects.add(new Platform(g, new Vec2(0,b.getHeight()-6*h),0.5*l,6*h,4));
//            m.objects.add(new Platform(g, new Vec2(0.5*l,b.getHeight()-4*h),l,4*h,5));
//            m.enter.setPosition(new Vec2(0, ((b.getHeight()-h)/2)-m.enter.size));
//            m.objects.add(m.enter);
//            
//            m.objects.add(new Platform(g, new Vec2(b.getWidth()-l,((b.getHeight()-h)/2)),l,h,2));
//            m.objects.add(new Platform(g, new Vec2(b.getWidth()-0.5*l,b.getHeight()-6*h),0.5*l,6*h,4));
//            m.objects.add(new Platform(g, new Vec2(b.getWidth()-l-0.5*l,b.getHeight()-4*h),l,4*h,5));
//            m.exit.setPosition(new Vec2(b.getWidth()-m.exit.size, ((b.getHeight()-h)/2)-m.exit.size));
//            m.objects.add(m.exit);
//        }
//        else if (mapNumber == 2 || mapNumber == 3){
//            int nbPlatform = 1;
//            if (mapNumber == 2){
//                nbPlatform=5;
//            }
//            if (mapNumber == 3){
//                nbPlatform=8;
//            }
//            double l = b.getWidth()/(2*nbPlatform);
//            double h = b.getHeight()/(4*nbPlatform);
//            double decalage = hauteurDeSaut;
//            Vec2 v1 = new Vec2(0,decalage);
//            Vec2 v2 = new Vec2(b.getWidth()-l,decalage);
//            
//            double ecartX1 = (b.getWidth()-(nbPlatform*l))/(nbPlatform-1);
//            double ecartY1 = ((b.getHeight()-decalage)-(nbPlatform*h))/(nbPlatform-1);
//            double pasX1 = ecartX1 + l;
//            double pasY1 = ecartY1 + h;
//            
//            boolean verif = false;
//            int a1 = 0;
//            int a2 = 0;
//            while (!verif){
//                if ((int)v1.x <= (int)b.getWidth()-l){
//                    m.objects.add(new Platform(g, v1, l, h,3));
//                    v1=v1.add(new Vec2(pasX1,pasY1));
//                    a1 = 1;
//                } else{
//                    a1=0;
//                }
//                
//                if ((int)v2.x >= 0){
//                    m.objects.add(new Platform(g, v2, l, h,3));
//                    v2=v2.add(new Vec2(-pasX1,pasY1));
//                    a2 = 1;
//                } else{
//                    a2=0;
//                }
//                if (a1==0 & a2==0){
//                    verif = true;
//                }
//            }
//            m.objects.add(new Platform(g, new Vec2(0.5*l,((b.getHeight()+decalage-2*h)/2)),2.5*l,2*h,1));
//            m.enter.setPosition(new Vec2(l, ((b.getHeight()+decalage-2*h)/2)-m.enter.size));
//            m.objects.add(m.enter);
//            //m.objects.add(new Platform(1, new Vec2(0,((b.getHeight()+3*decalage-0.375*h)/4)),l,1.5*h));
//           // m.objects.add(new Platform(1, new Vec2(0,((3*b.getHeight()+decalage-0.375*h)/4)),l,1.5*h));
//            
//            m.objects.add(new Platform(g, new Vec2(b.getWidth()-(3*l),((b.getHeight()+decalage-h)/2)),2.5*l,2*h,1));
//            m.exit.setPosition(new Vec2(b.getWidth()-(0.5*m.exit.size)-1.75*l, ((b.getHeight()+decalage-h)/2)-m.exit.size));
//            m.objects.add(m.exit);
//            //m.objects.add(new Platform(1, new Vec2(b.getWidth()-l,((b.getHeight()+3*decalage-0.375*h)/4)),l,1.5*h));
//            //m.objects.add(new Platform(1, new Vec2(b.getWidth()-l,((3*b.getHeight()+decalage-0.375*h)/4)),l,1.5*h));
//            
//            //m.objects.add(new Platform(1, new Vec2(0.5*(b.getWidth()-h),decalage/2),h,(b.getHeight()/2)-h-2*decalage));
//            m.objects.add(new Platform(g, new Vec2(0.5*b.getWidth()-1.5*l, decalage+pasY1), 3*l, h,0)); 
//            
//            m.objects.add(new Platform(g, new Vec2(0.5*b.getWidth()-3*l, b.getHeight()-2*h), 2*l, 2*h,2));
//            m.objects.add(new Platform(g, new Vec2(0.5*b.getWidth()+l, b.getHeight()-2*h), 2*l, 2*h,2));
//        }
//        return m;
//    }