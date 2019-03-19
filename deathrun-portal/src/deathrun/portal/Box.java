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
public class Box {
    public Vec2 p1;
    public Vec2 p2;
    
    Box(double x1, double y1, double x2, double y2) {
        p1 = new Vec2(x1, y1);
        p2 = new Vec2(x2, y2);
    }
}
