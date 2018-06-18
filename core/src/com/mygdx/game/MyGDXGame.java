package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.screens.MenuScreen;
import com.mygdx.screens.World;

public class MyGDXGame extends Game{

	public SpriteBatch batch;
	public ShapeRenderer sr;
	public OrthographicCamera cam;

	@Override
	public void create () {
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		cam = new OrthographicCamera(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		
		cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
		
		//this.setScreen(new MenuScreen(this));
		
		this.setScreen(new World(sr, "map2.txt", batch, 0.95, 0.025, -25, 0.5, -0.5, 0, 0, -3));

		this.dispose();
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
