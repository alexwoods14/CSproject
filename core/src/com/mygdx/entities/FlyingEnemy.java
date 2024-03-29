package com.mygdx.entities;

public class FlyingEnemy extends Enemy{
	protected int yUpperLimit;
	protected int yLowerLimit;
	protected int xUpperLimit;
	protected int xLowerLimit;
	protected float zeroDisplacement;
	protected float timePeriod;
	protected double omega;
	
	
	public FlyingEnemy(int x, int yUpperLimit, int yLowerLimit, float timePeriod, boolean startsRight) {
		super(x, 0, false);
		height = width;
		horizontalVelocity = 0;
		this.yUpperLimit = yUpperLimit;
		this.yLowerLimit = yLowerLimit;
		this.timePeriod = timePeriod;
		zeroDisplacement = (yUpperLimit + yLowerLimit)/2;
		this.y = zeroDisplacement;
		verticalVelocity = (float) ((2*Math.PI/timePeriod) * (yUpperLimit-(yUpperLimit + yLowerLimit)/2));
		omega = (2*Math.PI)/timePeriod;
	}
	 
	public void move(float delta, float leftWall, float rightWall, float floorY, float roofY, float gravity) {
		//super.moveHori(delta, leftWall, rightWall);
		moveVert(delta);
		if(horizontalVelocity != 0) {
			super.moveHori(delta, xLowerLimit, xUpperLimit);
		}
	}

	private void moveVert(float delta){ 
		float displacement = y - zeroDisplacement;
		float acceleration = (float) -Math.pow(omega, 2)*displacement;
		
		verticalVelocity += acceleration*delta;
		
		if(x == xLowerLimit || x == xUpperLimit && horizontalVelocity != 0){
			y = zeroDisplacement;			
		}
		
		y += verticalVelocity*delta;
		
		//System.out.println("Y: " + y + "  maxAmplitude: " + maxAmplitude + "  midPoint: " + zeroDisplacement + "   displacement: " + displacement + "   acceleration: " + acceleration);
		
	}

}
