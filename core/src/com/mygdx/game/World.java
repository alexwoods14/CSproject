package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.mygdx.entities.Enemy;
import com.mygdx.entities.GroundEnemy;
import com.mygdx.entities.Player;
import com.mygdx.entities.SineFlyingEnemy;
import com.mygdx.entities.VerticalFlyingEnemy;
import com.mygdx.game.Agent.actions;
import com.mygdx.map.Map;
import com.mygdx.map.Block.Sides;

public class World implements Screen{


	private OrthographicCamera cam;
	private ShapeRenderer sr;

	private float gravity = 3500.0f;
	private int camX = 720;
	private int camY = 452; 

	private Player player;
	
	private Map map;
	private ArrayList<Enemy> enemies;
	private Agent learner;
	private Random rand;

	private String fileName;
	private actions action = actions.NONE;
	
	private int count;
	private int deathCount;

	public World(ShapeRenderer sr, String fileName) {
		this.sr = sr;
		this.fileName = fileName;
	}
	
    
	
	@Override
	public void show() {
		cam = new OrthographicCamera(1440, 810);
		player = new Player();
		map = new Map(fileName);
		enemies = new ArrayList<Enemy>();
		rand = new Random();
//		spawnRandomEnemies(30, 5, 5);
		//enemies.add(new VerticalFlyingEnemy(300, 500, 400, 2));
		//enemies.add(new SineFlyingEnemy(400, 500, 400, 1, 400));
		learner = new Agent(player, map, enemies, true);
//		int side = Constants.BLOCK_HEIGHT;
//		enemies.add(new GroundEnemy((int) (30.5*side ), 4*side, false));  
//		enemies.add(new GroundEnemy((int) (34.5*side), 7*side, false));
//		enemies.add(new GroundEnemy((int) (40.5*side), 9*side, false));
		
	}

	private void spawnRandomEnemies(int ground, int sine, int vert) {
		for(int i = 0; i < ground; i++){
			enemies.add(new GroundEnemy(rand.nextInt(150)*Constants.BLOCK_HEIGHT, rand.nextBoolean(), map));
		}		
	}

	@Override
	public void render(float delta) {	
		System.out.print("  " + player.isAlive());
		
		Gdx.gl.glClearColor(173/255f, 218/255f, 248/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		double deltaX = 0;
		if(player.isAlive() == true){
			map.findAllBoundaries(player.getX(), player.getY(), player.getWidth(), player.getHeight());
			//player.move(delta, gravity, map.getFloor(), map.getRoofY(), map.getLeftWall(), map.getRightWall());
			deltaX = player.AImove(delta, gravity, map.getFloor(), map.getRoofY(), map.getLeftWall(), map.getRightWall(), action);

			if(player.getX() >= 720){
				camX = (int) player.getX();
			}

			if(player.getY() >= 452){
				camY = (int) player.getY();
			}
		}
		
		camY = 0;

		cam.position.set(camX, camY, 0);

		cam.update();
		//System.out.println("camX: " + camX + "  camX + viewportWidth: " + (camX + cam.viewportWidth));

		sr.begin(ShapeType.Line);

		sr.setProjectionMatrix(cam.combined);
		//		System.out.println("X: " + Gdx.input.getX() + "   Y: " + (1080 - Gdx.input.getY()));
		player.draw(sr);
		map.draw(sr);
		
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
							//enemy.moveOutOfWay();
						}
					}
				}
			}


			if(enemy.hadFirstMove() == true && enemy.getX() + cam.viewportWidth > camX){
				map.findAllBoundaries(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
				enemy.move(delta, map.getLeftWall(), map.getRightWall(), map.getFloor(), map.getRoofY(), gravity);
			}
			
			if(enemy.getX() < camX + cam.viewportWidth/2){
				enemy.firstMove();
				enemy.draw(sr);
			}
		}
		
		//sr.rect(camX + cam.viewportWidth/2 - 10, 0, 10, 2000);
		
		sr.setColor(Color.BLACK);
		int range = Constants.SIGHT_DISTANCE;
		//horizontal
		float leftX = player.getX() + player.getWidth()/2 - range;
		float rightX = player.getX() + player.getWidth()/2 + range;
		float bottomY = player.getY() + player.getHeight()/2;
		float topY = player.getY() + player.getHeight()/2;
		sr.line(leftX, bottomY, rightX, topY);
		
		//Vertical
		leftX = player.getX() + player.getWidth()/2;
		rightX = player.getX() + player.getWidth()/2;
		bottomY = player.getY() + player.getHeight()/2 - range;
		topY = player.getY() + player.getHeight()/2 + range;
		sr.line(leftX, bottomY, rightX, topY);
		
		//diagonal bottom left to top right
		leftX = player.getX() + player.getWidth()/2  - (float)(Math.sqrt(0.5)*range);
		rightX = player.getX() + player.getWidth()/2 + (float)(Math.sqrt(0.5)*range);
		bottomY = player.getY() + player.getHeight()/2  - (float)(Math.sqrt(0.5)*range);
		topY = player.getY() + player.getHeight()/2 + (float)(Math.sqrt(0.5)*range);
		sr.line(leftX, bottomY, rightX, topY);
		
		//diagonal top left to bottom right
		leftX = player.getX() + player.getWidth()/2 - (float)(Math.sqrt(0.5)*range);
		rightX = player.getX() + player.getWidth()/2 + (float)(Math.sqrt(0.5)*range);
		bottomY = player.getY() + player.getHeight()/2 + (float)(Math.sqrt(0.5)*range);
		topY = player.getY() + player.getHeight()/2  - (float)(Math.sqrt(0.5)*range);
		sr.line(leftX, bottomY, rightX, topY);
		
		sr.end();

		for(Enemy deadEnemies: toRemove){
			enemies.remove(deadEnemies);
		}
		
		if(count > 3) {
			action = learner.calculateQ(deltaX, player.isAlive());
			if(player.isAlive() == false){
				player.revive();
			}
			count = 0;
		}
		count++;
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
	}


}
