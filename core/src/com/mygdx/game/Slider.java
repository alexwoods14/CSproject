package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Slider {
	private float x;
	private float y;
	private final float lineHeight = 10;
	private final float lineLength = 200;
	private final float draggerWidth = 10;
	private final float draggerHeight = 35;
	private float draggerX;
	private float draggerY;
	private boolean hovering = false;
	private boolean currentlyDragging = false;
	private int percentage = 0;
	private Texture text;
	
	public Slider(float x, float y, String texture) {
		this.x = x;
		this.y = y;
		text = new Texture(texture + ".png");
		draggerX = x;
		draggerY = y+lineHeight/2 - draggerHeight/2;
	}
	
	
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
	
	public void drawLabel(SpriteBatch batch){
		batch.draw(text, x, y + 30);
	}

	private void moveDragger(float mouseX) {
		if(mouseX - draggerWidth/2 > x && mouseX - draggerWidth/2 < x + lineLength){
			draggerX = mouseX - draggerWidth/2;
		}
		else{
			if(mouseX - draggerWidth/2 <= x){
				draggerX = x;
			}
			else{
				draggerX = x + lineLength;
			}
		}
		currentlyDragging = true;
		calculatePercentage();
	}

	private void calculatePercentage() {
		percentage = (int) +(((draggerX - x)/lineLength)*100);
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
	
	public int getPercentage(){
		return percentage ;
	}
}
