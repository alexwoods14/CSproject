package com.mygdx.entities;

import com.mygdx.map.Block.Sides;

public class Entity {
	protected int height;
	protected int width;
	protected float x;
	protected float y;
	protected float verticalVelocity;
	protected float horizontalVelocity;

	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	
	public boolean hasCollided(float x2, float y2, int width2, int height2){
		
		if(y <= (y2 + height2) && (y + height) >= y2 && x <= (x2 + width2) && (x + width) >= x2){
			return true;
		}
		else {
			return false;
		}

	}
	
	public boolean crossesPoint(float x2, float y2) {
		if(x2 > x && x2 < x + width && y2 > y && y2 < y + height){   
			return true;		// if the point lays within the enemy, true is returned
		}
		else{
			return false;
		}
		
	}

	public Sides collisionSide(float x2, float y2, int width2, int height2) {
		//overlaps
		Sides toReturn = null;
		float fromRight = 0;
		float fromLeft = 0;
		float fromBottom = 0;
		float fromTop = 0;
		float maxHori;
		float maxVert;
		float minOfMaxes;
		//top
		if((y + height) > y2 && y < y2){
			fromTop = (y + height) - y2;
		}

		//middle
		if(y <= y2 && (y + height) >= (y2 + height2)){
			fromBottom = 0;
			fromTop = 0;
		}

		//bottom			
		if(y < (y2 + height2) && y > y2){
			fromBottom = (y2 + height2) - y;
		}

		//left
		if(x < (x2+width2) && x > x2){
			fromLeft = (x2 + width2) - x;
		}

		//middle
		if(x <= x2 && (x + width) >= (x2 + width2)){
			fromLeft = 0;
			fromRight = 0;
		}

		//right
		if((x + width) > x2 && x < x2){
			fromRight = (x + width) - x2;
		}

		maxHori = Math.max(fromLeft, fromRight);
		maxVert = Math.max(fromTop, fromBottom);
		
		minOfMaxes = Math.min(maxVert, maxHori);
		
		if(maxHori == 0 && minOfMaxes == 0){
			minOfMaxes = maxVert;
			
		}
		if(maxVert == 0 && minOfMaxes == 0){
			minOfMaxes = maxHori;
		}
		

		if(minOfMaxes == fromLeft){toReturn = Sides.LEFT;}
		if(minOfMaxes == fromRight){toReturn = Sides.RIGHT;}
		if(minOfMaxes == fromTop){toReturn = Sides.TOP;}
		if(minOfMaxes == fromBottom){toReturn = Sides.BOTTOM;}

		return toReturn;
	}
}
