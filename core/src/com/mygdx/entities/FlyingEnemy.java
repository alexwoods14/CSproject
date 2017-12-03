package com.mygdx.entities;

public class FlyingEnemy extends Enemy{
	private int yUpperLimit;
	private int yLowerLimit;
	private int xUpperLimit;
	private int xLowerLimit;
	private float zeroDisplacement;
	private float timePeriod;
	
	public FlyingEnemy(int x, int y, int yUpperLimit, int yLowerLimit, float horiDisplacement, float timePeriod) {
		super(x, y);
		height = width;
		horiV = horiDisplacement/(timePeriod*4);
		this.yUpperLimit = yUpperLimit;
		this.yLowerLimit = yLowerLimit;
		this.xLowerLimit = x;
		this.xUpperLimit = (int) (x + horiDisplacement);
		this.timePeriod = timePeriod;
		zeroDisplacement = (yUpperLimit + yLowerLimit)/2;
		y = (yUpperLimit + yLowerLimit)/2;
		vertV = (float) ((2*Math.PI/timePeriod) * (yUpperLimit-(yUpperLimit + yLowerLimit)/2));
	}
	
	public FlyingEnemy(int x, int y, int yUpperLimit, int yLowerLimit, float timePeriod) {
		super(x, y);
		height = width;
		horiV = 0;
		this.yUpperLimit = yUpperLimit;
		this.yLowerLimit = yLowerLimit;
		this.timePeriod = timePeriod;
		zeroDisplacement = (yUpperLimit + yLowerLimit)/2;
		y = (yUpperLimit + yLowerLimit)/2;
		vertV = (float) ((2*Math.PI/timePeriod) * (yUpperLimit-(yUpperLimit + yLowerLimit)/2));
	}
	
	public void move(float delta, float leftWall, float rightWall, float floorY, float roofY) {
		//super.moveHori(delta, leftWall, rightWall);
		moveVert(delta);
		if(horiV != 0) {
			super.moveHori(delta, xLowerLimit, xUpperLimit);
		}
	}

	private void moveVert(float delta) {
		double omega = (2*Math.PI)/timePeriod;
		float displacement = y - (yUpperLimit + yLowerLimit)/2;
		float acceleration = (float) -Math.pow(omega, 2)*displacement;
		
		vertV += acceleration*delta;
		
		if(x == xLowerLimit || x == xUpperLimit && horiV != 0){
			y = zeroDisplacement;			
		}
		
		y += vertV*delta;
		
		//System.out.println("Y: " + y + "  maxAmplitude: " + maxAmplitude + "  midPoint: " + zeroDisplacement + "   displacement: " + displacement + "   acceleration: " + acceleration);
		
	}

}
