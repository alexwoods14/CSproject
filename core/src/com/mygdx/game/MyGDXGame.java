package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MyGDXGame extends Game{

	public SpriteBatch batch;
	public ShapeRenderer sr;

	@Override
	public void create () {
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		this.setScreen(new World(sr));
	}

	@Override
	public void render () {
		super.render();

	}

	@Override
	public void dispose () {
		super.dispose();

	}
}