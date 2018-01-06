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


	public Player() {
		height = 76;
		width = 48;
		x = 40*Constants.BLOCK_HEIGHT;
		y = 400.0f;
		vertV = 0.0f;
		horiV = 350.0f;
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
	
	public float AImove(float delta, float gravity, float floorY, float roofY, float leftWall, float rightWall, actions action){
		//System.out.println("X Value" + x);
		float deltaX = 0;
		if(action == actions.LEFT){
			if(x - delta*horiV > leftWall){
				deltaX =- delta*horiV;
				x += deltaX;
			}
			else{
				x = leftWall +  0.001f;
			} 
		}

		if(action == actions.RIGHT){
			if(x + delta*horiV < rightWall){
				deltaX = delta*horiV;
				x += deltaX;
			}
			else{
				x = rightWall - 0.001f;
			}
		}

		if(action == actions.JUMP && y <= floorY){
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
			if(floorY == 0){
				this.died();
			}
		}
		if(y + vertV*delta + height > roofY){
			vertV = -100;
			y = roofY - height;
		}

		return deltaX;
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
		x = 20;
		y = 400;
	}
	
	public void revive(){
		x = 20;
		y = 400;
		alive = true;
	}


	public boolean isAlive() {
		return alive;
	}
}
