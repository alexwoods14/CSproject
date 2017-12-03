package com.mygdx.entities;

public class GroundEnemy extends Enemy{
	
	public GroundEnemy(int x, int y) {
		super(x, y);
		vertV = 0.0f;
	}
	
	public void move(float delta, float leftWall, float rightWall, float floorY, float roofY){
		super.moveHori(delta, leftWall, rightWall);
		
	}


}
