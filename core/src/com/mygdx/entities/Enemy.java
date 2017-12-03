package com.mygdx.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Enemy extends Entity{

	protected boolean hadFirstMove = false;
	
	public Enemy(int x, int y) {		
		height = 64;
		width = 48;
		this.x = x;
		this.y = y;
		vertV = 0.0f;
		horiV = 100.0f;
	}
	
	public void reverseDirection(){
		horiV = - horiV;
	}
	
	public void move(float delta, float leftWall, float rightWall, float floorY, float roofY){
		moveHori(delta, leftWall, rightWall);
		moveVert(delta, floorY, roofY);
	}

	public void moveHori(float delta, float leftWall, float rightWall) {
		if(horiV > 0) {
			if((x += delta*horiV) < rightWall) {
				x += delta*horiV;
			}
			else {
				x = rightWall;
				horiV = -horiV;
			}
		}
		else {
			if((x += delta*horiV) > leftWall) {
				x += delta*horiV;
			}
			else {
				x = leftWall;
				horiV = -horiV;
			}
		}
	}
	public void moveVert(float delta, float floorY, float roofY){
		
		if(vertV > 0) {
			if((y += delta*vertV) < roofY) {
				y += delta*vertV;
			}
			else {
				y = roofY - height;
				vertV = -vertV;
			}
		}
		else {
			if((y += delta*vertV) > floorY) {
				y += delta*vertV;
			}
			else {
				y = floorY;
				vertV = -vertV;
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