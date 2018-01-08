package com.mygdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.game.Agent.actions;
import com.mygdx.game.Constants;

public class Player extends Entity{
	
	private boolean alive = true;
	private boolean touchingFloor = false;
	private double deltaX;
	private double deltaY;


	public Player() {
		height = 76;
		width = 48;
		x = 50.0f;
		y = 400.0f;
		vertV = 0.0f;
		horiV = 350.0f;
	}
	
	public boolean onFloor(){
		return touchingFloor;
	}
	
	public void move(float delta, float gravity, float floorY, float roofY, float leftWall, float rightWall){
		//System.out.println("X Value" + x);
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
		//System.out.println("X Value" + x);
		deltaY = 0;
		deltaX = 0;
		if((action == actions.JUMP || action == actions.JUMP_LEFT || action == actions.JUMP_RIGHT) && y <= floorY){
			vertV = 1250.0f;
		}
		
		if(action == actions.LEFT || action == actions.JUMP_LEFT){
			if(x - delta*horiV > leftWall){
				deltaX =- delta*horiV;
				x += deltaX;
			}
			else{
				x = leftWall +  0.001f;
			} 
		}

		if(action == actions.RIGHT|| action == actions.JUMP_RIGHT){
			if(x + delta*horiV < rightWall){
				deltaX = delta*horiV;
				x += deltaX;
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
			deltaY = vertV*delta;
		}
		else{
			y = floorY;
			vertV = 0;
			if(floorY == 0){
				this.died();
			}
		}
		if(y + vertV*delta + height > roofY){
			vertV = -100;
			y = roofY - height;
		}
		if(x > 200*Constants.BLOCK_HEIGHT){
			x = 50;
		}
		
		 if(y == floorY && y > 0){
			 touchingFloor = true;
		 }
		 else{
			 touchingFloor = false;
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
		x = 0;
		y = 400;
	}
	
	public void revive(){
		x = 0;
		y = 400;
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
}
