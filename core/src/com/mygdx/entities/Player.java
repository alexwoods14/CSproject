package com.mygdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.game.Constants;
import com.mygdx.map.Map;

public class Player extends Entity{
	
	protected boolean alive = true;
	protected boolean touchingFloor = false;

	public Player() {
		height = 76;
		width = 48;
		x = 0.0f;
		y = 0.0f;
		verticalVelocity = 0.0f;
		horizontalVelocity = 300.0f;
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
			if(x - delta*horizontalVelocity > leftWall){
				x -= delta*horizontalVelocity;
			}
			else{
				x = leftWall +  0.001f;
			} 
		}

		if(Gdx.input.isKeyPressed(Keys.D) == true){
			if(x + delta*horizontalVelocity < rightWall){
				x += delta*horizontalVelocity;
			}
			else{
				x = rightWall - 0.001f;
			}
		}

		if(Gdx.input.isKeyPressed(Keys.SPACE) == true && y <= floorY){
			verticalVelocity = 1250.0f;
		}

		if(y > floorY){
			verticalVelocity -= gravity * delta;
		}

		if(y + verticalVelocity*delta > floorY){		
			y += verticalVelocity * delta;
		}
		else{
			y = floorY;
			verticalVelocity = 0;
		}
		 if(y + verticalVelocity*delta + height > roofY){
			verticalVelocity = -100;
			y = roofY - height;
		 }
	 
		if(y <= 0){
			died();
		}	
	}
	
	public void bounce(){
		verticalVelocity = 1000.0f;
	}

	public void draw(ShapeRenderer sr, OrthographicCamera cam) {
		sr.setAutoShapeType(true);
		sr.set(ShapeType.Filled);
		sr.setColor(Color.ORANGE);
		sr.rect(x, y, width, height);
		sr.setColor(Color.BLACK);
	}

	public void died() {
		alive = false;
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void revive(Map map){
		x = Constants.BLOCK_HEIGHT;
		y = map.findStartingFloor(x, width);
		alive = true;
	}

	public void draw(SpriteBatch batch) {
	}

}
