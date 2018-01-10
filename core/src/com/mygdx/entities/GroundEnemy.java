package com.mygdx.entities;

import com.mygdx.game.Constants;
import com.mygdx.map.Map;

public class GroundEnemy extends Enemy{
	
	private boolean hasJumped = false;
	
	public GroundEnemy(int x, int y, boolean startsRight) {
		super(x, y, startsRight);
		vertV = 0.0f;
	}
	
	public GroundEnemy(int x1, boolean nextBoolean, Map map) {
		super(x1>10*Constants.BLOCK_HEIGHT? x1 : (x1+400), 500, nextBoolean);		
		
	}

	public void move(float delta, float leftWall, float rightWall, float floorY, float roofY, float gravity){
		super.moveHori(delta, leftWall, rightWall);
		
		if(y > floorY){
			if(hasJumped == false) {
				vertV = 400;
				hasJumped = true;
			}
			vertV -= gravity * delta;
		}
		
		if(y + vertV*delta > floorY){
			
			y += vertV * delta;
		}
		else{
			y = floorY;
			vertV = 0;
			hasJumped = false;
		}
		
	}
	
}
