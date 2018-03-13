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
	private Slider deathReward;
	private Slider moveRightReward;
	private Slider moveLeftReward;
//	private Slider moveUpReward;
//	private Slider moveDownReward;
	private ArrayList<Slider> sliders;
	
	public AIsettingsScreen(MyGDXGame game) {
		this.batch = game.batch;
		this.sr = game.sr;
		this.game = game;
	}
	
	@Override
	public void show() {
		learningRate = new Slider(100, 400, "learning_rate", false, 0, 1);
		eagerness = new Slider(100, 600, "eagerness", false, -4, 4);
		deathReward = new Slider(100, 200, "death_reward", false, -5, 0);
		moveRightReward = new Slider(500, 600, "move_right_reward", false, 0, 5);
		moveLeftReward = new Slider(500, 400, "move_left_reward", false, 0, 5);

//		sliders = new ArrayList<Slider>();
		
//		sliders.add(deathReward);
//		sliders.add(eagerness);
//		sliders.add(learningRate);
//		sliders.add(moveRightReward);
//		sliders.add(moveLeftReward);

//		sliders.add(new Slider(100, 400, "learning_rate", false, 0, 1));
//		sliders.add(new Slider(100, 600, "eagerness", false, -4, 4));
//		sliders.add(new Slider(100, 200, "death_reward", false, -5, 0));
//		sliders.add(new Slider(500, 600, "move_right_reward", false, 0, 5));
//		sliders.add(new Slider(500, 400, "move_left_reward", false, 0, 5));

	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(90/255f, 1.0f, 112/255f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		sr.begin(ShapeType.Filled);
		batch.begin();
//		for(Slider slider: sliders) {
//			if(slider != null){
//				slider.draw(sr, null, Gdx.input.isTouched());
//				slider.drawLabel(batch);
//			}
//		}
		learningRate.draw(sr, null, Gdx.input.isTouched());
		eagerness.draw(sr, null, Gdx.input.isTouched());
		moveLeftReward.draw(sr, null, Gdx.input.isTouched());
		moveRightReward.draw(sr, null, Gdx.input.isTouched());
		deathReward.draw(sr, null, Gdx.input.isTouched());
		//learningRate.drawLabel(batch, deathReward);
		//eagerness.drawLabel(batch, deathReward);
		//moveLeftReward.drawLabel(batch, deathReward);
		//moveRightReward.drawLabel(batch, deathReward);
		//deathReward.drawLabel(batch, deathReward);
		

		sr.end();
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
