package com.mygdx.map;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.game.Constants;
import com.mygdx.game.MyButton;

public class MapMaker implements Screen, TextInputListener{
	
	private OrthographicCamera cam;
	
	private ShapeRenderer sr;
	private SpriteBatch batch;
	private int camX = 0;
	private int camY = 0;
	private int lastX = 0;
	private int lastY = 0;
	private int deltaX = 0;
	private int deltaY = 0;
	private int mapWidth = Constants.MAP_WIDTH;
	private int mapHeight = Constants.MAP_HEIGHT;
	private boolean isSolid = true;
	private boolean[][] solidGrid;
	private boolean[][] rooflessGrid;
	private ArrayList<ArrayList<Character>> layers;
	private int side = 48;
	private boolean input = false;
	private ArrayList<MyButton> buttons;
	private MyButton finished;
	private MyButton changeState;
	
	public MapMaker(ShapeRenderer sr, SpriteBatch batch) {
		this.sr = sr;
		this.batch = batch;
	}
	
	@Override
	public void show() {
			
		cam = new OrthographicCamera(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		solidGrid = new boolean[mapHeight][mapWidth];
		rooflessGrid = new boolean[mapHeight][mapWidth];
		for(int j = 0; j < mapHeight; j ++){
			for(int i = 0; i < mapWidth; i++){
				solidGrid[j][i] = false;
				rooflessGrid[j][i] = false;
			}
		}
		layers = new ArrayList<ArrayList<Character>>();
		finished = new MyButton("finished", 20, 20);
		changeState = new MyButton("tile_state", 20, Constants.WINDOW_HEIGHT - 100);
		buttons = new ArrayList<MyButton>();
		buttons.add(finished);
		buttons.add(changeState);	
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
				
		
		if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) == true || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT) == true){
			if(Gdx.input.isTouched() == true){
				deltaX = (lastX - Gdx.input.getX())/2;
				deltaY = (Gdx.input.getY() - lastY)/2;
				//System.out.println("deltaX: " + deltaX + "   deltaY: " + deltaY);
			}
			else{
				deltaX = 0;
				deltaY = 0;
			}
		}
		else{
			if(Gdx.input.justTouched() == true){
				float mouseX = Gdx.input.getX() + camX - cam.viewportWidth/2;
				float mouseY = (Constants.WINDOW_HEIGHT - Gdx.input.getY()) + camY - cam.viewportHeight/2;
				int blockX = (int) Math.floor(mouseX/side);
				int blockY = (int) Math.floor(mouseY/side);
				boolean touchingButton = false;

				for(MyButton button: buttons){
					if(button.isHovering() == true){
						touchingButton = true;
						if(button.equals(finished) == true && input == false){
							System.out.println("HERE");
							Gdx.input.getTextInput(this, "Enter a map name:", "", "mapname.txt");
							input = true;
						}
						if(button.equals(changeState) == true){
							if(isSolid == true){
								isSolid = false;
							}
							else{
								isSolid = true;
							}
						}
					}
					else{
						touchingButton = false;
					}
				}


				//sr.triangle(-side, j*side + side/10, -side, (j+1)*side - side/10, 0-side/10, j*side + side/2);
				if(touchingButton == false){
					for(int j = 0; j < mapHeight; j++){
						if(mouseX > -side && mouseX < -side/10 && mouseY > j*side + side/10 && mouseY < (j+1)*side - side/10){
							for(int i = 0; i < mapWidth; i ++){
								if(solidGrid[j][i] == true){
									solidGrid[j][i] = false;
								}
								else{
									solidGrid[j][i] = true;
								}
							}
						}
					}
				}
				//System.out.println("x: " + mouseX + "   y: " + mouseY);
				if(touchingButton == false && blockX >=0 && blockX < mapWidth && blockY >=0 && blockY < mapHeight){
					if(isSolid == true){
						if(solidGrid[blockY][blockX] == false){
							solidGrid[blockY][blockX] = true;
						}
						else{
							solidGrid[blockY][blockX] = false;
						}
					}
					else{
						if(rooflessGrid[blockY][blockX] == false){
							rooflessGrid[blockY][blockX] = true;
						}
						else{
							rooflessGrid[blockY][blockX] = false;
						}
					}
				}
			}
		}

		lastX = Gdx.input.getX();
		lastY = Gdx.input.getY();

		if((camX += deltaX) > 0 ){
			camX += deltaX;
		}
		if((camY += deltaY) > 0 ){
			camY += deltaY;
		}

		cam.position.set(camX, camY, 0);
		cam.update();

		
		sr.begin(ShapeType.Line);
		sr.setProjectionMatrix(cam.combined);
		sr.setAutoShapeType(true);
		for(int j = 0; j < mapHeight; j++){
			for(int i = 0; i < mapWidth; i++){
				if(solidGrid[j][i] == true){
					sr.setColor(Color.RED);
					sr.set(ShapeType.Filled);
					sr.rect(i*side, j*side, side, side);
				}
				if(rooflessGrid[j][i] == true){
					sr.setColor(Color.ORANGE);
					sr.set(ShapeType.Filled);
					sr.rect(i*side, (j+1)*side - 24, side, 24);
				}
				sr.set(ShapeType.Line);
				sr.setColor(Color.BLACK);
				sr.rect(i*side, j*side, side, side);
			}
			sr.triangle(-side, j*side + side/10, -side, (j+1)*side - side/10, 0-side/10, j*side + side/2);
		}
		sr.end();
		batch.begin();
		for(MyButton button: buttons){
			button.draw(batch, lastX, lastY);
		}
		batch.end();
		
	}
	

	private void saveMap(String fileName){
		// R = roof-less
		// S = solid
		// N = none/null
		for(int j = 0; j < mapHeight; j++){
			ArrayList<Character> row = new ArrayList<Character>(); 
			for(int i = 0; i < mapWidth; i++){
					if(rooflessGrid[j][i] == true && solidGrid[j][i] == true){
						row.add('S');
					}
					if(solidGrid[j][i] == true && rooflessGrid[j][i] == false){
						row.add('S');
					}
					if(rooflessGrid[j][i] == true && solidGrid[j][i] == false){
						row.add('R');
					}
					if(rooflessGrid[j][i] == false && solidGrid[j][i] == false){
						row.add('N');
					}
			}
			layers.add(j, row);
		}
		
				System.out.println(Constants.ASSETS_FOLDER_LOCATION  + fileName);
		
				PrintWriter writer;
		try {
			writer = new PrintWriter( Constants.ASSETS_FOLDER_LOCATION + fileName);
			for(int j = 0; j < mapHeight; j++){
				for(int i = 0; i < mapWidth; i++){
					writer.print(layers.get(j).get(i));
				}
				writer.println();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		sr.dispose();
		batch.dispose();
	}

	@Override
	public void input(String text) {
		saveMap(text);
	}

	@Override
	public void canceled() {
		input = false;
	}

}
