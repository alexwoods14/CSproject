package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Slider {
	private final float x = 20;
	private final float y = Constants.WINDOW_HEIGHT - 40;
	private final float lineHeight = 10;
	private final float lineLength = 200;
	private float draggerWidth = 10;
	private float draggerHeight = 35;
	private float draggerX = x + 30;
	private final float draggerY = y+lineHeight/2 - draggerHeight/2;
	private boolean hovering = false;
	private boolean currentlyDragging = false;
	
	public void draw(ShapeRenderer sr, OrthographicCamera cam, boolean isClicked){
		float mouseX = Gdx.input.getX();
		float mouseY = (Constants.WINDOW_HEIGHT - Gdx.input.getY());
		if(isClicked == true && currentlyDragging == false){
			isHovering(mouseX, mouseY);
		}
		if(hovering == true && isClicked == true){
			moveDragger(mouseX);
		}
		else{
			currentlyDragging = false;
		}
		sr.setColor(Color.LIGHT_GRAY);
		sr.rect(x + cam.position.x - cam.viewportWidth/2, y + cam.position.y - cam.viewportHeight/2, lineLength, lineHeight);
		sr.setColor(Color.GRAY);
		sr.rect(draggerX + cam.position.x - cam.viewportWidth/2, draggerY + cam.position.y - cam.viewportHeight/2, draggerWidth, draggerHeight);
	}

	private void moveDragger(float mouseX) {
		if(mouseX > x && mouseX < x + lineLength){
			draggerX = mouseX - draggerWidth/2;
		}
		else{
			if(mouseX < x){
				draggerX = x;
			}
			else{
				draggerX = x + lineLength;
			}
		}
		currentlyDragging = true;
	}

	private void isHovering(float mouseX, float mouseY) {
		//System.out.printf("mouseX: %f   mouseY: %f  draggerX: %f  draggerY: %f  %n", mouseX, mouseY, draggerX, draggerY);
		if(mouseX > draggerX && mouseX < draggerX + draggerWidth && mouseY > draggerY && mouseY < draggerY + draggerHeight){	
			hovering  = true;
		}
		else{
			hovering = false;
		}
	}
}
