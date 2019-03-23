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
class Map {
    public ArrayList<PObject> objects;
    public Box size;
    
    Map(Box size) { 
		this.size = size; 
		this.objects = new ArrayList<PObject>();
	}
}
