package com.mygdx.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Constants;
import com.mygdx.game.MyButton;
import com.mygdx.game.MyGDXGame;
import com.mygdx.map.MapMaker;

public class MenuScreen implements Screen, TextInputListener{
	
	private MyButton mapMakerButton;
	private MyButton playButton;
	private MyButton AIbutton;
	private MyGDXGame game;
	private SpriteBatch batch;
	private ArrayList<MyButton> buttons;
	private boolean input = false;
	private String fileName;
	private boolean AI = false;
	
	public MenuScreen(MyGDXGame game) {
		this.batch = game.batch;
		this.game = game;
		input = false;
		buttons = new ArrayList<MyButton>();
		mapMakerButton = new MyButton("map_button", Constants.WINDOW_WIDTH/4, Constants.WINDOW_HEIGHT/2);
		playButton = new MyButton("play_button", Constants.WINDOW_WIDTH/2, Constants.WINDOW_HEIGHT/2);
		AIbutton = new MyButton("ai_button", 3*Constants.WINDOW_WIDTH/4, Constants.WINDOW_HEIGHT/2);
		buttons.add(mapMakerButton);
		buttons.add(playButton);
		buttons.add(AIbutton);
	}
	
	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(90/255f, 1.0f, 112/255f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(Gdx.input.isTouched() == true){
			for(MyButton button: buttons){
				if(button.isHovering() == true){
					this.pause();
					if(button.equals(mapMakerButton) && input == false){
						input = true;
						game.setScreen(new MapMaker(game));
					}
					if(button.equals(playButton) && input == false){
						input = true;
						AI = false;
						Gdx.input.getTextInput(this, "Enter a map name:", "", "mapname.txt");						
					}
					if(button.equals(AIbutton) && input == false) {
						input = true;
						AI = true;
						Gdx.input.getTextInput(this, "Enter a map name:", "", "mapname.txt");
					}
				}
			}
		}
		batch.begin();
		for(MyButton button: buttons){
			button.draw(batch, Gdx.input.getX(), Gdx.input.getY());
		}
		batch.end();
		
		if(fileName!= null) {
			if(AI == true) {
				game.setScreen(new AIsettingsScreen(game, fileName));
			}
			else {
				game.setScreen(new World(game.sr, fileName, batch));
			}
		}
						
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
		if(text.endsWith(".txt")) {
			if(Gdx.files.internal(text).exists() == true){
				fileName = text;
			}
		}
		else {
			input = false;
			canceled();
		}
		
	}

	@Override
	public void canceled() {
		input = false;
	}

}
