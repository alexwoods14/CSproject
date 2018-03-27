package com.mygdx.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.game.Constants;
import com.mygdx.game.MyButton;
import com.mygdx.game.MyGDXGame;
import com.mygdx.game.Slider;
import com.mygdx.game.World;

public class AIsettingsScreen implements Screen{
	
	private MyGDXGame game;
	private ShapeRenderer sr;
	private SpriteBatch batch;
	private Slider learningRate;
	private Slider eagerness;
	private Slider deathReward;
	private Slider moveRightReward;
	private Slider moveLeftReward;
	private Slider moveUpReward;
	private Slider moveDownReward;
	private Slider stationaryReward;
	
	private MyButton done;
	
	private ArrayList<Slider> sliders;
	private float width = Constants.WINDOW_WIDTH;
	private float height = Constants.WINDOW_HEIGHT;
	private String fileName;
	
	
	public AIsettingsScreen(MyGDXGame game, String fileName) {
		this.batch = game.batch;
		this.sr = game.sr;
		this.game = game;
		this.fileName = fileName;
		
		learningRate = new Slider(width/3 - width/6 , height - height/6, "learning_rate", false, 0, 1);
		eagerness = new Slider(width/3 - width/6, 2*height/3 - height/6, "eagerness", false, 0, 1);
		stationaryReward = new Slider(width/3 - width/6, height/3 - height/6, "stationary_reward", false, -5, 0);
		
		moveLeftReward = new Slider(2*width/3 - width/6, height - height/6, "move_left_reward", false, -5, 0);
		moveRightReward = new Slider(2*width/3 - width/6, height/3 - height/6, "move_right_reward", false, 0, 2);
		
		deathReward = new Slider(width - width/6, height - height/6, "death_reward", false, -200, 0);
		moveDownReward = new Slider(width - width/6, 2*height/3 - height/6, "move_down_reward", false, -5, 5);
		moveUpReward = new Slider(width - width/6, height/3 - height/6, "move_up_reward", false, -5, 5);

		sliders = new ArrayList<Slider>();
		
		sliders.add(deathReward);
		sliders.add(eagerness);
		sliders.add(learningRate);
		sliders.add(moveRightReward);
		sliders.add(moveLeftReward);
		sliders.add(moveUpReward);
		sliders.add(moveDownReward);
		sliders.add(stationaryReward);
		
		done = new MyButton("done", 2*width/3 - width/6, 2*height/3 - height/6);
	}
	
	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(90/255f, 1.0f, 112/255f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		sr.begin(ShapeType.Filled);
		batch.begin();
		
		done.draw(batch, Gdx.input.getX(), Gdx.input.getY());
		
		for(Slider slider: sliders) {
			if(slider != null){
				slider.draw(sr, game.cam, Gdx.input.isTouched());
				slider.drawLabel(batch);
			}
		}
		batch.end();
		sr.end();
		
		if(Gdx.input.isTouched() == true) {
			if(done.isHovering() == true) {
				game.setScreen(new World(sr, fileName, batch, eagerness.getDecimal(), learningRate.getDecimal(), deathReward.getDecimal(), moveRightReward.getDecimal(), moveLeftReward.getDecimal(), moveUpReward.getDecimal(), moveDownReward.getDecimal(), stationaryReward.getDecimal()));
			}
		}
		
		
//		learningRate.draw(sr, null, Gdx.input.isTouched());
//		eagerness.draw(sr, null, Gdx.input.isTouched());
//		moveLeftReward.draw(sr, null, Gdx.input.isTouched());
//		moveRightReward.draw(sr, null, Gdx.input.isTouched());
//		deathReward.draw(sr, null, Gdx.input.isTouched());
//		learningRate.drawLabel(batch, deathReward);
//		eagerness.drawLabel(batch, deathReward);
//		moveLeftReward.drawLabel(batch, deathReward);
//		moveRightReward.drawLabel(batch, deathReward);
//		deathReward.drawLabel(batch, deathReward);

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
