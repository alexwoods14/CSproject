package com.mygdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.game.Agent.actions;
import com.mygdx.game.Constants;
import com.mygdx.map.Map;

public class Player extends Entity{
	
	private boolean alive = true;
	private boolean touchingFloor = false;
	private double deltaX;
	private double deltaY;
	private boolean newState;


	public Player() {
		height = 76;
		width = 48;
		x = 1000.0f;
		y = 1.0f;
		vertV = 0.0f;
		horiV = 300.0f;
		newState = true;
	}
	
	public boolean onFloor(){
		return touchingFloor;
	}
	
	public void move(float delta, float gravity, float floorY, float roofY, float leftWall, float rightWall){
		
		//sets the touchingFloor variable depending on whether it is currently on floor or not
		if(y == floorY && y > 0){
			touchingFloor = true;
		}
		else{
			touchingFloor = false;
		}

		if(Gdx.input.isKeyPressed(Keys.A) == true){
			if(x - delta*horiV > leftWall){
				x -= delta*horiV;
			}
			else{
				x = leftWall +  0.001f;
			} 
		}

		if(Gdx.input.isKeyPressed(Keys.D) == true){
			if(x + delta*horiV < rightWall){
				x += delta*horiV;
			}
			else{
				x = rightWall - 0.001f;
			}
		}

		if(Gdx.input.isKeyPressed(Keys.SPACE) == true && y <= floorY){
			vertV = 1250.0f;
		}

		if(y > floorY){
			vertV -= gravity * delta;
		}

		if(y + vertV*delta > floorY){		
			y += vertV * delta;
		}
		else{
			y = floorY;
			vertV = 0;
		}
		 if(y + vertV*delta + height > roofY){
			vertV = -100;
			y = roofY - height;
		 }
	 
		if(y == 0){
			died();
		}	
	}
	
	public void AImove(float delta, float gravity, float floorY, float roofY, float leftWall, float rightWall, actions action){
		
		if(newState == true){
			deltaY = 0;
			deltaX = 0;
			newState = false;
		}
		
		if(y == floorY && y > 0){
			touchingFloor = true;
		}
		else{
			touchingFloor = false;
		}

		
		if((action == actions.JUMP || action == actions.JUMP_LEFT || action == actions.JUMP_RIGHT) && y <= floorY){
			vertV = 1250.0f;
		}
		if(action == actions.LEFT || action == actions.JUMP_LEFT){
			if(x - delta*horiV > leftWall){
				deltaX =- delta*horiV;
				x -= delta*horiV;
			}
			else{
				x = leftWall +  0.001f;
			} 
		}

		if(action == actions.RIGHT|| action == actions.JUMP_RIGHT){
			if(x + delta*horiV < rightWall){
				deltaX += delta*horiV;
				x += delta*horiV;
			}
			else{
				x = rightWall - 0.001f;
			}
		}

		if(y > floorY){
			vertV -= gravity * delta;
		}

		if(y + vertV*delta > floorY){		
			y += vertV * delta;
			deltaY += vertV*delta;
		}
		else{
			y = floorY;
			vertV = 0;
			
		}
		if(y + vertV*delta + height > roofY){
			vertV = -100;
			y = roofY - height;
		}
		if(x > 200*Constants.BLOCK_HEIGHT){
			x = 50;
			y = 100.0f;
		}
		
		if(y <= 0){
			this.died();
		}
	}

	public void bounce(){
		vertV = 1000.0f;
	}

	public void draw(ShapeRenderer sr) {
		sr.setAutoShapeType(true);
		sr.set(ShapeType.Filled);
		sr.setColor(Color.ORANGE);
		sr.rect(x, y, width, height);
		sr.setColor(Color.BLACK);
	}


	public void died() {
		alive = false;
	}
	
	public void revive(int nextX, Map map){
		if(x >= 0 && x < 200*Constants.BLOCK_HEIGHT) {
			x = 1.0f;
			y = 300;
		}
		alive = true;
	}


	public boolean isAlive() {
		return alive;
	}

	public double getDeltaX() {
		return deltaX;
	}
	public double getDeltaY() {
		return deltaY;
	}
	public void newState(){
		newState = true;
	}
}
