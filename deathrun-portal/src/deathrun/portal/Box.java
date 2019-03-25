/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 *
 * @author ydejongh
 */
public class Box {
    public Vec2 p1;
    public Vec2 p2;
    
    public Box(Vec2 min, Vec2 max) {
        p1 = min; p2 = max;
    }
    public Box(double x1, double y1, double x2, double y2) {
        this(new Vec2(x1, y1), new Vec2(x2, y2));
    }
 

    public Vec2 center() { return p1.add(p2).mul(0.5); }
    public double getWidth() { return p2.x - p1.x; }
    public double getHeight() { return p2.y - p1.y; }
    
    public Box translate(Vec2 vector)  { return new Box(p1.add(vector), p2.add(vector)); }
    
    public Box translateToPosition(Vec2 pos)  {
        Vec2 translated_p1 = new Vec2(pos.x, pos.y);
        Vec2 translated_p2 = new Vec2(pos.x + this.getWidth(), pos.y + this.getHeight());
//        System.out.println(translated_p1 + ", " + translated_p2);
        return new Box(translated_p1, translated_p2);
    }

    /// retourne vrai si le point est strictement a l'interieur du rectangle
    public boolean contains(Vec2 position) {
        return (
                p1.x < position.x && position.x < p2.x
        &&	p1.y < position.y && position.y < p2.y);
    }

    /// retourne vrai si le point est a l'interieur our sur le bord du rectangle
    public boolean inborder(Vec2 position) {
        return (
                p1.x <= position.x && position.x <= p2.x
        &&	p1.y <= position.y && position.y <= p2.y);
    }

    /// retourne un rectangle elargi par les dimensions d'un autre
    public Box outline(Box margin) {
        return new Box(p1.add(margin.p1), p2.add(margin.p2));
    }

    /// retourne le point d'intersection du segment [start end] avec les bords si end est a l'intérieur, sinon retourne end
    /// note: se fiche de si start est dedans ou pas
    public Vec2 intersectionBorder(Vec2 start, Vec2 end) {
        if (contains(end)) {
                Vec2 v = end.sub(start);
                double x;
                double y;
                y = v.y/v.x * p1.x;
                if (p1.y <= y && y <= p2.y)		return new Vec2(p1.x, y);
                y = v.y/v.x * p2.x;
                if (p1.y <= y && y <= p2.y)		return new Vec2(p2.x, y);

                x = v.x/v.y * p1.y;
                if (p1.x <= x && x <= p2.x)		return new Vec2(x, p1.y);
                x = v.x/v.y * p2.y;
                if (p1.x <= x && y <= p2.x)		return new Vec2(x, p2.y);

                return null;	// ne peut pas arriver normalement mais servira a detecter les bugs
        }
        else
                return end;
    }

    /// retourne le point de la bordure le plus proche de position
    public Vec2 outer(Vec2 position) {
        double x = position.x;
        double y = position.y;
        if 		(position.x < p1.x)	x = p1.x;
        else if (position.x > p2.x)	x = p2.x;
        if		(position.y < p1.y)	y = p1.y;
        else if (position.y > p2.y)	y = p2.y;
        return new Vec2(x, y);
    }
    
    /// retourne true si this et other presentent une intersection
    public boolean intersect(Box other) {
        return (
                p1.x <= other.p2.x && p2.x >= other.p1.x
			&&	p1.y <= other.p2.y && p2.y >= other.p1.y
            );
    }
    
    /// retourne la boite intersection des deux boites
    public Box intersection(Box other) {
        return new Box(
            new Vec2 (max(p1.x, other.p1.x), max(p1.x, other.p1.x)), 
            new Vec2 (min(p2.x, other.p2.x), min(p2.y, other.p2.y))
            );
    }
    
    /// retourne le coin de other qui est dans this
    public Vec2 contact(Box other) {
        if (intersect(other)) {
            double x = other.p1.x;
            double y = other.p1.y;
            if (x < p1.x)   x = other.p2.x;
            if (y < p1.y)   x = other.p2.y;
            return new Vec2(x,y);
		}
		else
            return null;
    }


    public String toString() {
        return "Box("+String.valueOf(p1.x)+", "+String.valueOf(p1.y)+", "+String.valueOf(p2.x)+", "+String.valueOf(p2.y)+")";
    }
    
}
