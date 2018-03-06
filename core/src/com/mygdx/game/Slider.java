package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

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
	private double decimal = 0.50;
	private Texture text;
	private BitmapFont font;
	private boolean asPercent;
	
	public Slider(float x, float y, String text, boolean asPercentage) {
		this.x = x;
		this.y = y;
		this.text = new Texture(Constants.ASSETS_FOLDER_LOCATION + text + ".png");
		draggerX = x + lineLength/2;
		draggerY = y+lineHeight/2 - draggerHeight/2;
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		font.getData().scale(0.7f);
		asPercent = asPercentage;
	}
	
	public void draw(ShapeRenderer sr, OrthographicCamera cam, boolean isClicked){
		float mouseX = Gdx.input.getX();
		float mouseY = (Constants.WINDOW_HEIGHT - Gdx.input.getY());
		float xOffset = 0;
		float yOffset = 0;
		if(cam != null) {
			xOffset = cam.position.x - cam.viewportWidth/2;
			yOffset = cam.position.y - cam.viewportHeight/2;
		}
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
		sr.set(ShapeType.Filled);
		sr.rect(x + xOffset, y + yOffset, lineLength, lineHeight);
		sr.setColor(Color.GRAY);
		sr.rect(draggerX + xOffset, draggerY + yOffset, draggerWidth, draggerHeight);
	}
	
	public void drawLabel(SpriteBatch batch){
		batch.draw(text, x - 10, y + 30);

		if(asPercent == true) {
			font.draw(batch, (int) (decimal*100) + "%", x + text.getWidth() , y + 30 + text.getHeight()/2 + font.getCapHeight()/2);
		}
		else {
			font.draw(batch, "" + decimal, x + text.getWidth() , y + 30 + text.getHeight()/2 + font.getCapHeight()/2);
		}
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
		calculateDecimal();
	}

	private void calculateDecimal() {
		decimal = Math.floor(((draggerX - x)/lineLength)*100)/100;
		System.out.println(decimal);
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
		return (int) (decimal*100);
	}
	public double getDecimal() {
		return decimal;
	}
}
