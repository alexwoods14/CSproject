package com.mygdx.entities;

public class stationaryEnemy extends Enemy{
	public stationaryEnemy(int x, int y) {
		super(x + (48-32)/2, y - 128);
		width = 32;
		height = 128;
	}

	private final double interval = 5;
	private double timeToLive = interval;
	
	public void move(float delta){
		if(timeToLive == 0){
			
		}
		if(timeToLive - delta <= 0){
			timeToLive = 0;
			vertV = 200;			
		}
	}
}
