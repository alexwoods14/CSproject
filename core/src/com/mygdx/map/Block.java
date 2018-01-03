package com.mygdx.map;

import com.mygdx.game.Constants;

public class Block {
	protected final int side = Constants.BLOCK_HEIGHT;
	protected float x;
	protected float y;

	public enum Sides{
		LEFT,RIGHT,TOP,BOTTOM;
	}

	public Block(int x, int y) {
		this.x = x*side;
		this.y = y*side;
	}

	public int getSide(){
		return side;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

//	public boolean exists() {
//		return exists;
//	}
//
//	public void setExists(boolean exists) {
//		this.exists = exists;
//	}
}