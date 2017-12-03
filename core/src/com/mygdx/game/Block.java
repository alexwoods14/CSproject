package com.mygdx.game;

public class Block {
	private final int side = 48;
	private float x;
	private float y;
	private boolean exists = true;

	public enum Sides{
		LEFT,RIGHT,TOP,BOTTOM;
	}

	public Block(int x, int y) {
		this.x = x*side;
		this.y = y*side;
	}

	public int getSide(){
		return side;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	public boolean exists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}
//
//	public int maxCollisionArea(float x2, float y2, int width, int height){
//		int area;
//		float xLength = 0;
//		float yLength = 0;
//
//		if(y <= (y2 + height) && (y+side) >= y2 && x <= (x2+width) && (x+side) >= x2){
//			//top
//			if((y+side) >= y2 && y < y2){
//				yLength = (y + side) - y2;
//				//System.out.println("FROM TOP");
//			}
//
//			//middle
//			if(y <= y2 && (y + side) >= (y2 + height)){
//				yLength = side;
//			}
//
//			//bottom			
//			if(y <= (y2 + height) && y > y2){
//				yLength = (y2 + height) - y;
//
//				//System.out.println("FROM BOTTOM");
//			}
//
//			//left
//			if(x <= (x2+width) && x > x2){
//				xLength = (x2 + width) - x;
//			}
//
//			//middle
//			if(x <= x2 && (x + side) >= (x2 + width)){
//				xLength = side;
//				//System.out.println("*****************");
//			}
//
//			//right
//			if((x+side) >= x2 && x < x2){
//				xLength = (x + side) - x2;
//			}
//
//		}
//		
//		
//		area = (int) (xLength*yLength);
//		
//		if(area == 0){
//			//System.out.println("NOTHING");
//		}
//
//		return area;
//
//	}
//
//	public Sides collisionSide(float x2, float y2, int width, int height) {
//		//overlaps
//		Sides toReturn = null;
//		float fromRight = 0;
//		float fromLeft = 0;
//		float fromBottom = 0;
//		float fromTop = 0;
//		float maxHori;
//		float maxVert;
//		float minOfMaxes;
//		//top
//		if((y+side) > y2 && y < y2){
//			fromTop = (y + side) - y2;
//		}
//
//		//middle
//		if(y <= y2 && (y + side) >= (y2 + height)){
//			fromBottom = 0;
//			fromTop = 0;
//		}
//
//		//bottom			
//		if(y < (y2 + height) && y > y2){
//			fromBottom = (y2 + height) - y;
//		}
//
//		//left
//		if(x < (x2+width) && x > x2){
//			fromLeft = (x2 + width) - x;
//		}
//
//		//middle
//		if(x <= x2 && (x + side) >= (x2 + width)){
//			fromLeft = 0;
//			fromRight = 0;
//		}
//
//		//right
//		if((x+side) > x2 && x < x2){
//			fromRight = (x + side) - x2;
//		}
//
//		maxHori = Math.max(fromLeft, fromRight);
//		maxVert = Math.max(fromTop, fromBottom);
//		
//		minOfMaxes = Math.min(maxVert, maxHori);
//		
//		if(maxHori == 0 && minOfMaxes == 0){
//			minOfMaxes = maxVert;
//			
//		}
//		if(maxVert == 0 && minOfMaxes == 0){
//			minOfMaxes = maxHori;
//		}
//		
//
//		if(minOfMaxes == fromLeft){toReturn = Sides.LEFT;}
//		if(minOfMaxes == fromRight){toReturn = Sides.RIGHT;}
//		if(minOfMaxes == fromTop){toReturn = Sides.TOP;}
//		if(minOfMaxes == fromBottom){toReturn = Sides.BOTTOM;}
//		//if((y+side) == y2){toReturn = Sides.TOP;}
//
//		return toReturn;
//	}
}
