package com.mygdx.entities;

import com.mygdx.game.Constants;
import com.mygdx.map.Block.Sides;

public class Entity {
	protected int height;
	protected int width;
	protected float x;
	protected float y;
	protected float vertV;
	protected float horiV;

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
		boolean hasHit = false;
		int area;
		float xLength = 0;
		float yLength = 0;

		if(y <= (y2 + height2) && (y + height) >= y2 && x <= (x2 + width2) && (x + width) >= x2){
			//top
			if((y + height) >= y2 && y < y2){
				yLength = (y + height) - y2;
			}

			//middle
			if(y <= y2 && (y + height) >= (y2 + height2)){
				yLength = height;
			}

			//bottom			
			if(y <= (y2 + height2) && y > y2){
				yLength = (y2 + height2) - y;

			}

			//left
			if(x <= (x2 + width2) && x > x2){
				xLength = (x2 + width2) - x;
			}

			//middle
			if(x <= x2 && (x + width) >= (x2 + width2)){
				xLength = width;
			}

			//right
			if((x + width) >= x2 && x < x2){
				xLength = (x + width) - x2;
			}

		}
		
		
		area = (int) (xLength*yLength);
		
		if(area != 0){
			hasHit = true;
		}

		return hasHit;

	}
	
	public boolean crossesPoint(float x2, float y2) {
		if(x2 > x && x2 < x + width && y2 > y && y2 < y + height){
			return true;		
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
