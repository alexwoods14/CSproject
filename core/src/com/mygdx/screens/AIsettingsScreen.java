package com.mygdx.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.game.MyButton;
import com.mygdx.game.MyGDXGame;
import com.mygdx.game.Slider;

public class AIsettingsScreen implements Screen{
	
	private MyGDXGame game;
	private ShapeRenderer sr;
	private SpriteBatch batch;
	private Slider learningRate;
	private Slider eagerness;
	private Slider dieReward;
	private Slider moveRightReward;
	private Slider moveLeftReward;
	private Slider moveUpReward;
	private Slider moveDownReward;
	
	public AIsettingsScreen(MyGDXGame game) {
		this.batch = game.batch;
		this.sr = game.sr;
		this.game = game;
	}
	
	@Override
	public void show() {
		learningRate = new Slider(500, 400, "learning_rate", false, 0, 1);
		eagerness = new Slider(500, 600, "eagerness", false, -4, 4);
//		dieReward = new Slider(500, 400, "learning_rate", false, 5);
//		moveRightReward = new Slider(500, 600, "eagerness", false, 5);
//		moveLeftReward = new Slider(500, 600, "eagerness", false, 5);
//		moveUpReward = new Slider(500, 600, "eagerness", false, 5);
//		moveDownReward = new Slider(500, 600, "eagerness", false, 5);
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(90/255f, 1.0f, 112/255f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		sr.begin(ShapeType.Filled);
		learningRate.draw(sr, null, Gdx.input.isTouched());
		eagerness.draw(sr, null, Gdx.input.isTouched());
		sr.end();
		batch.begin();
		learningRate.drawLabel(batch);
		eagerness.drawLabel(batch);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
