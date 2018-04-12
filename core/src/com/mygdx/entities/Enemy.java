package com.mygdx.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.map.Map;

public class Enemy extends Entity{

	protected boolean hadFirstMove = false;
	
	public Enemy(float x, Map map, boolean startsRight) {		
		height = 64;
		width = 48;
		this.x = x;
		this.y = map.findStartingFloor(x, width);
		verticalVelocity = 0.0f;
		if(startsRight == true) {
			horizontalVelocity = 100.0f;
		}
		else {
			horizontalVelocity = -100.0f;
		}
	}	
	
	public Enemy(float x, float y, boolean startsRight) {		
		height = 64;
		width = 48;
		this.x = x;
		this.y = y;
		verticalVelocity = 0.0f;
		if(startsRight == true) {
			horizontalVelocity = 100.0f;
		}
		else {
			horizontalVelocity = -100.0f;
		}
	}
	
	public void reverseDirection(){
		horizontalVelocity = - horizontalVelocity;
	}
	
	public void move(float delta, float leftWall, float rightWall, float floorY, float roofY, float gravity){
		moveHori(delta, leftWall, rightWall);
		moveVert(delta, floorY, roofY);
	}

	public void moveHori(float delta, float leftWall, float rightWall) {
		if(horizontalVelocity > 0) {
			if((x += delta*horizontalVelocity) < rightWall) {
				x += delta*horizontalVelocity;
			}
			else {
				x = rightWall;
				horizontalVelocity = -horizontalVelocity;
			}
		}
		else {
			if((x += delta*horizontalVelocity) > leftWall) {
				x += delta*horizontalVelocity;
			}
			else {
				x = leftWall;
				horizontalVelocity = -horizontalVelocity;
			}
		}
	}
	public void moveVert(float delta, float floorY, float roofY){
		
		if(verticalVelocity > 0) {
			if((y += delta*verticalVelocity) < roofY) {
				y += delta*verticalVelocity;
			}
			else {
				y = roofY - height;
				verticalVelocity = -verticalVelocity;
			}
		}
		else {
			if((y += delta*verticalVelocity) > floorY) {
				y += delta*verticalVelocity;
			}
			else {
				y = floorY;
				verticalVelocity = -verticalVelocity;
			}
		}
	}
	
	public boolean hadFirstMove() {
		return hadFirstMove;
	}
	
	public void firstMove() {
		hadFirstMove = true;
	}
	
	public void draw(ShapeRenderer sr) {
		sr.setAutoShapeType(true);
		sr.set(ShapeType.Filled);
		sr.setColor(Color.RED);
		sr.rect(x, y, width, height);
	}
}