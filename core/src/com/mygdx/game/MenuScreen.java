package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.map.MapMaker;

public class MenuScreen implements Screen, TextInputListener{
	
	private MyButton mapMakerButton;
	private MyButton playButton;
	private MyGDXGame game;
	private SpriteBatch batch;
	private ArrayList<MyButton> buttons;
	private boolean input = false;
	
	public MenuScreen(MyGDXGame game) {
		this.batch = game.batch;
		this.game = game;
		input = false;
	}
	
	@Override
	public void show() {
		buttons = new ArrayList<MyButton>();
		mapMakerButton = new MyButton("map_button", Constants.WINDOW_WIDTH/2 + 200, 300);
		playButton = new MyButton("play_button", Constants.WINDOW_WIDTH/2 - 200, 300);
		buttons.add(mapMakerButton);
		buttons.add(playButton);
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(90/255f, 1.0f, 112/255f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(Gdx.input.isTouched() == true){
			for(MyButton button: buttons){
				if(button.isHovering() == true){
					//this.dispose();
					this.pause();
					if(button.equals(mapMakerButton)){
						game.setScreen(new MapMaker(game.sr, game.batch));
					}
					if(button.equals(playButton) && input == false){
						Gdx.input.getTextInput(this, "Enter a map name:", "", "mapname.txt");
						input = true;
					}
				}
			}
		}
		batch.begin();
		for(MyButton button: buttons){
			button.draw(batch, Gdx.input.getX(), Gdx.input.getY());
		}
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

	@Override
	public void input(String text) {
		if(text.endsWith(".txt") == true){
			game.setScreen(new World(game.sr, text, batch));
		}
	}

	@Override
	public void canceled() {
		input = false;
	}

}
