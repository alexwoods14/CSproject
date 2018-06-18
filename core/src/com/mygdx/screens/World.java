package com.mygdx.screens;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.entities.Agent;
import com.mygdx.entities.Enemy;
import com.mygdx.entities.Entity.Sides;
import com.mygdx.entities.GroundEnemy;
import com.mygdx.entities.Player;
import com.mygdx.entities.SineFlyingEnemy;
import com.mygdx.entities.VerticalFlyingEnemy;
import com.mygdx.game.Constants;
import com.mygdx.map.Map;

public class World implements Screen{


	private OrthographicCamera cam;
	private ShapeRenderer sr;
	private SpriteBatch batch;
	

	private float gravity = 3600.0f;
	private int camX = Constants.WINDOW_WIDTH/2;
	private int camY = Constants.WINDOW_HEIGHT/2; 
	
	private Map map;
	private ArrayList<Enemy> enemies;
	private Player player;
	
	public World(ShapeRenderer sr, String fileName, SpriteBatch batch) {	
		startUp(sr, fileName, batch);
		player = new Player();
		reset();
	}
	
//	private Slider learningRate;
//	private Slider eagerness;
//	private Slider deathReward;
//	private Slider moveRightReward;
//	private Slider moveLeftReward;
//	private Slider moveUpReward;
//	private Slider moveDownReward;
//	private Slider initialRandomness; 
	
	public World(ShapeRenderer sr, String fileName, SpriteBatch batch, double eagerness, double learningRate, double deathReward, double rightReward, double leftReward, double upReward, double downReward, double stationaryReward) {
		startUp(sr, fileName, batch);
		player = new Agent(map, enemies, eagerness, learningRate, deathReward, rightReward, leftReward, upReward, downReward, stationaryReward);
		reset();
	}
	
	public void startUp(ShapeRenderer sr, String fileName, SpriteBatch batch) {
		this.sr = sr;
		this.batch = batch;
		
		cam = new OrthographicCamera(1280, 720);

		map = new Map(fileName);
		enemies = new ArrayList<Enemy>();
	}
		
	@Override
	public void show() {
		
	}

	private void spawnEnemies() {
		int side = Constants.BLOCK_HEIGHT;
		
		//ground enemies
		enemies.add(new GroundEnemy(14*side, map, false));
		enemies.add(new GroundEnemy(25*side, map, false));
		enemies.add(new GroundEnemy(40*side, map, true));
		enemies.add(new GroundEnemy(33*side, map, false));
//		enemies.add(new GroundEnemy(59*side, map, true));
		enemies.add(new GroundEnemy(62*side, map, false));
		enemies.add(new GroundEnemy(80*side, map, true));
		enemies.add(new GroundEnemy(81*side, map, false));
//		enemies.add(new GroundEnemy(84*side, map, true));
		enemies.add(new GroundEnemy(94*side, map, false));
		enemies.add(new GroundEnemy(105*side, map, false));
		enemies.add(new GroundEnemy(125*side, map, true));
//		enemies.add(new GroundEnemy(122*side, map, false));
//		enemies.add(new GroundEnemy(145*side, map, false));
		enemies.add(new GroundEnemy(143*side, map, false));
		enemies.add(new GroundEnemy(140*side, 5*side, false));
		enemies.add(new GroundEnemy(143*side, 7*side, false));
		
		//vertical flying enemies
		enemies.add(new VerticalFlyingEnemy(23*side, 9*side, 6*side, 1.5f));
		enemies.add(new VerticalFlyingEnemy(45*side, 10*side, 6*side, 2.0f));
//		enemies.add(new VerticalFlyingEnemy(73*side, 7*side, 5*side, 1.0f));
		enemies.add(new VerticalFlyingEnemy(91*side, 10*side, 7*side, 1.5f));
		enemies.add(new VerticalFlyingEnemy(154*side, 12*side, 8*side, 1.5f));
		
		//sinusoidal flying enemies
		enemies.add(new SineFlyingEnemy(12*side, 12*side, 9*side, 1.0f, 5*side));
		enemies.add(new SineFlyingEnemy(56*side, 10*side, 7*side, 1.0f, 5*side));
		enemies.add(new SineFlyingEnemy(116*side, 9*side, 11*side, 1.0f, 5*side));
	}

	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(173/255f, 218/255f, 248/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	
		
		if(player.isAlive() == true && player.getY() > 0){
			map.findAllBoundaries(player.getX(), player.getY(), player.getWidth(), player.getHeight());
			player.move(delta, gravity, map.getFloor(), map.getRoofY(), map.getLeftWall(), map.getRightWall());
			if(player.getX() >= Constants.WINDOW_WIDTH/2){
				camX = (int) player.getX();
			}
			else{
				camX = Constants.WINDOW_WIDTH/2;
			}

			if(player.getY() >= Constants.WINDOW_HEIGHT/2){
				camY = (int) player.getY();
			}
			else{
				camY = Constants.WINDOW_HEIGHT/2;
			}
		}
		
		cam.position.set(camX, camY, 0);
		cam.update();
				
		ArrayList<Enemy> toRemove = new  ArrayList<Enemy>();

		for(Enemy enemy: enemies) {
			if(player.isAlive() == true){
				if(player.hasCollided(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()) == true){			
					if(enemy.collisionSide(player.getX(), player.getY(), player.getWidth(), player.getHeight()) == Sides.TOP){
						toRemove.add(enemy);
						player.bounce();
					}
					else{
						player.died();
					}
				}
			}
			for(Enemy otherEnemy: enemies) {
				if(otherEnemy.equals(enemy)){

				}
				else{
					if(Math.abs((enemy.getX()/map.getSide()) - (otherEnemy.getX()/map.getSide())) < 2){
						if(enemy.hasCollided(otherEnemy.getX(), otherEnemy.getY(), otherEnemy.getWidth(), otherEnemy.getHeight()) == true){
							enemy.reverseDirection();
						}
					}
				}
			}


			if(enemy.hadFirstMove() == true && enemy.getX() + cam.viewportWidth > camX){
				if(enemy.getX() >= 0 && enemy.getY() > 0){
					map.findAllBoundaries(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
					enemy.move(delta, map.getLeftWall(), map.getRightWall(), map.getFloor(), map.getRoofY(), gravity);
				}
				else{
					toRemove.add(enemy);
				}
				if(enemy.getY() == 0){
					toRemove.add(enemy);
				}
			}
			
			if(enemy.getX() < camX + cam.viewportWidth/2){
				enemy.firstMove();
			}
		}
		
		
//		sr.setColor(Color.BLACK);
//		int range = Constants.SIGHT_DISTANCE;
//		//horizontal
//		float leftX = player.getX() + player.getWidth()/2 - range;
//		float rightX = player.getX() + player.getWidth()/2 + range;
//		float bottomY = player.getY() + player.getHeight()/2;
//		float topY = player.getY() + player.getHeight()/2;
//		sr.line(leftX, bottomY, rightX, topY);
//		
//		//Vertical
//		leftX = player.getX() + player.getWidth()/2;
//		rightX = player.getX() + player.getWidth()/2;
//		bottomY = player.getY() + player.getHeight()/2 - range;
//		topY = player.getY() + player.getHeight()/2 + range;
//		sr.line(leftX, bottomY, rightX, topY);
//		
//		//diagonal bottom left to top right
//		leftX = player.getX() + player.getWidth()/2  - (float)(Math.sqrt(0.5)*range);
//		rightX = player.getX() + player.getWidth()/2 + (float)(Math.sqrt(0.5)*range);
//		bottomY = player.getY() + player.getHeight()/2  - (float)(Math.sqrt(0.5)*range);
//		topY = player.getY() + player.getHeight()/2 + (float)(Math.sqrt(0.5)*range);
//		sr.line(leftX, bottomY, rightX, topY);
//		
//		//diagonal top left to bottom right
//		leftX = player.getX() + player.getWidth()/2 - (float)(Math.sqrt(0.5)*range);
//		rightX = player.getX() + player.getWidth()/2 + (float)(Math.sqrt(0.5)*range);
//		bottomY = player.getY() + player.getHeight()/2 + (float)(Math.sqrt(0.5)*range);
//		topY = player.getY() + player.getHeight()/2  - (float)(Math.sqrt(0.5)*range);
//		sr.line(leftX, bottomY, rightX, topY);

		for(Enemy deadEnemies: toRemove){
			enemies.remove(deadEnemies);
		}
		
		
		sr.begin(ShapeType.Line);
		sr.setAutoShapeType(true);
		sr.setProjectionMatrix(cam.combined);
		
		map.draw(sr);
		for(Enemy enemy: enemies) {
			enemy.draw(sr);
		}
		
		player.draw(sr, cam);			
		
		sr.end();
		
		batch.begin();
		player.draw(batch);
		batch.end();
		
		
		if(player.isAlive() == false) {
			reset();
		}
	}
	
	private void reset(){
		player.revive(map);
		enemies.clear();
		spawnEnemies();
	}

	@Override	
	public void resize(int width, int height) {		
	}

	@Override
	public void pause() {		
	}

	@Override
	public void resume() {		
	}

	@Override
	public void hide() {		
	}

	@Override
	public void dispose() {	
		sr.dispose();
		batch.dispose();
	}


}
