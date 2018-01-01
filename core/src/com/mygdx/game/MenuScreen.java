package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.map.MapMaker;

public class MenuScreen implements Screen{
	
	MyButton mapButton;
	MyGDXGame game;
	SpriteBatch batch;
	ArrayList<MyButton> buttons;
	
	public MenuScreen(MyGDXGame game) {
		this.batch = game.batch;
		this.game = game;
	}
	
	@Override
	public void show() {
		buttons = new ArrayList<MyButton>();
		mapButton = new MyButton("map_button", 300, 300);
		buttons.add(mapButton);
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
					if(button.equals(mapButton)){
						game.setScreen(new MapMaker(game.sr, game.batch));
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

}
