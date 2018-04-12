package com.mygdx.entities;

import com.mygdx.map.Map;

public class GroundEnemy extends Enemy{
	
	private boolean hasJumped = false;
	
	public GroundEnemy(int x, Map map, boolean startsRight) {
		super(x, map, startsRight);
		verticalVelocity = 0.0f;
	}
	
	public GroundEnemy(int x, int y, boolean startsRight) {
		super(x, y, startsRight);
		verticalVelocity = 0.0f;
	}
	

	public void move(float delta, float leftWall, float rightWall, float floorY, float roofY, float gravity){
		super.moveHori(delta, leftWall, rightWall);
		
		if(y > floorY){
			if(hasJumped == false) {
				verticalVelocity = 400;
				hasJumped = true;
			}
			verticalVelocity -= gravity * delta;
		}
		
		if(y + verticalVelocity*delta > floorY){
			
			y += verticalVelocity * delta;
		}
		else{
			y = floorY;
			verticalVelocity = 0;
			hasJumped = false;
		}
		
	}
	
}
