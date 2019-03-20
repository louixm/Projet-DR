/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

/**
 *
 * @author ydejongh
 */
public class Vec2 {
    public double x;
    public double y;
    
    public Vec2(double x, double y) { this.x = x; this.y = y; }
    public Vec2 add(Vec2 other) 	{ return new Vec2(this.x+other.x, this.y+other.y); }
    public Vec2 sub(Vec2 other)		{ return new Vec2(this.x-other.x, this.y-other.y); }
    public Vec2 neg() 				{ return new Vec2(-this.x, -this.y); }
    public Vec2 mul(double scalar)	{ return new Vec2(scalar*this.x, scalar*this.y); }
    public double dot(Vec2 other)	{ return other.x*this.x + other.y*this.y; }
    
    public String toString() {
        return "Vec2("+String.valueOf(x)+", "+String.valueOf(y)+")";
    }
}
