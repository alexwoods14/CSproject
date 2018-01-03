package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyButton {
	
	private Texture active;
	private Texture inactive;
	private boolean isHovering = false;
	private int x = 0;
	private int y = 0;
	private float height;
	private float width;
	
	public MyButton(String fileName, int x, int y) {
		this.active = new Texture(fileName + "_active.png");
		this.inactive = new Texture(fileName  + "_inactive.png");
		width = this.active.getWidth();
		height = this.active.getHeight();
		this.x = x;
		this.y = y;
		
	}

	public void draw(SpriteBatch batch, int mouseX, int mouseY) {
		if(isMouseOver(mouseX, mouseY) == true){
			isHovering = true;
		}
		else{
			isHovering = false;
		}
		
		if(isHovering == true) {
			batch.draw(active, x, y, width, height);
		}
		else {
			batch.draw(inactive, x, y);
		}
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public float getWidth(){
		return width;
	}
	public float getHeight(){
		return height;
	}

	private boolean isMouseOver(int mouseX, int mouseY) {
		if(mouseX > x && mouseX < x + width && (Constants.WINDOW_HEIGHT-mouseY) > y && (Constants.WINDOW_HEIGHT-mouseY) < y + height){
			return true;		
		}
		else{
			return false;
		}
	}

	public boolean isHovering(){
		return isHovering;
	}

}
