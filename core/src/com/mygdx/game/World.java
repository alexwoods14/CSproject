package com.mygdx.game;

import java.util.ArrayList;
import java.util.Date;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
	private SpriteBatch batch;
	

	private float gravity = 3600.0f;
	private int camX = 720;
	private int camY = 410; 

	private Player player;
	
	private Map map;
	private ArrayList<Enemy> enemies;
	private Agent learner;
	
	private MyButton exploring;
	private Slider randomness;

	private String fileName;
	private actions action = actions.NONE;
	
	private int count;
	private Date startTime;
	private Date startOfRunTime;
	private Date currentTime;
	private boolean changed = false;
	private int deathCount = 0;


	public World(ShapeRenderer sr, String fileName, SpriteBatch batch) {
		this.sr = sr;
		this.batch = batch;
		this.fileName = fileName;
	}
	
    
	
	@Override
	public void show() {
		cam = new OrthographicCamera(1280, 720);
		player = new Player();
		map = new Map(fileName);
		enemies = new ArrayList<Enemy>();
		exploring = new MyButton("finished", 20, Constants.WINDOW_HEIGHT - 100);
		randomness = new Slider();
		currentTime = new Date();
		startTime = new Date();
		reset();
		//enemies.add(new VerticalFlyingEnemy(300, 500, 400, 2));
		//enemies.add(new SineFlyingEnemy(400, 500, 400, 1, 400));
		learner = new Agent(player, map, enemies, true);
//		int side = Constants.BLOCK_HEIGHT;
//		enemies.add(new GroundEnemy((int) (30.5*side ), 4*side, false));  
//		enemies.add(new GroundEnemy((int) (34.5*side), 7*side, false));
//		enemies.add(new GroundEnemy((int) (40.5*side), 9*side, false));
		
	}

	private void spawnEnemies() {
		int side = Constants.BLOCK_HEIGHT;
		
		//ground enemies
		enemies.add(new GroundEnemy(14*side, 5*side + 5, false));
		enemies.add(new GroundEnemy(25*side, 2*side + 5, false));
		enemies.add(new GroundEnemy(40*side, 8*side + 5, true));
		enemies.add(new GroundEnemy(33*side, 5*side + 5, false));
		enemies.add(new GroundEnemy(59*side, 2*side + 5, true));
		enemies.add(new GroundEnemy(62*side, 2*side + 5, false));
		enemies.add(new GroundEnemy(80*side, 2*side + 5, true));
		enemies.add(new GroundEnemy(81*side, 4*side + 5, false));
		enemies.add(new GroundEnemy(84*side, 6*side + 5, true));
		enemies.add(new GroundEnemy(94*side, 6*side + 5, false));
		enemies.add(new GroundEnemy(105*side, 8*side + 5, false));
		enemies.add(new GroundEnemy(125*side, 4*side + 5, true));
		enemies.add(new GroundEnemy(122*side, 5*side + 5, false));
		enemies.add(new GroundEnemy(145*side, 2*side + 5, false));
		enemies.add(new GroundEnemy(143*side, 4*side + 5, false));
		enemies.add(new GroundEnemy(141*side, 6*side + 5, false));
		enemies.add(new GroundEnemy(139*side, 9*side + 5, false));
		
		//vertical flying enemies
		enemies.add(new VerticalFlyingEnemy(24*side, 9*side, 6*side, 1.5f));
		enemies.add(new VerticalFlyingEnemy(42*side, 10*side, 5*side, 2.0f));
		enemies.add(new VerticalFlyingEnemy(73*side, 7*side, 5*side, 1.0f));
		enemies.add(new VerticalFlyingEnemy(93*side, 10*side, 7*side, 1.5f));
		enemies.add(new VerticalFlyingEnemy(154*side, 12*side, 8*side, 1.5f));
		
		//sinusoidal flying enemies
		enemies.add(new SineFlyingEnemy(12*side, 12*side, 9*side, 1.0f, 5*side));
		enemies.add(new SineFlyingEnemy(54*side, 10*side, 6*side, 1.0f, 7*side));
		enemies.add(new SineFlyingEnemy(116*side, 8*side, 12*side, 1.0f, 8*side));
	}

	@Override
	public void render(float delta) {
		if(Gdx.input.justTouched() == true){
			if(exploring.isHovering() == true){
				learner.changeExploring();
			}
		}

		Gdx.gl.glClearColor(173/255f, 218/255f, 248/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(player.isAlive() == true){
			map.findAllBoundaries(player.getX(), player.getY(), player.getWidth(), player.getHeight());
			//player.move(delta, gravity, map.getFloor(), map.getRoofY(), map.getLeftWall(), map.getRightWall());
			player.AImove(delta, gravity, map.getFloor(), map.getRoofY(), map.getLeftWall(), map.getRightWall(), action);
			if(player.getX() >= 720){
				camX = (int) player.getX();
			}
			else{
				camX = 720;
			}

			if(player.getY() >= cam.viewportHeight/2){
				camY = (int) player.getY();
			}
			else{
				camY = (int) (cam.viewportHeight/2);
			}
		}
		
		cam.position.set(camX, camY, 0);

		cam.update();
		//System.out.println("camX: " + camX + "  camX + viewportWidth: " + (camX + cam.viewportWidth));

		sr.begin(ShapeType.Line);

		sr.setProjectionMatrix(cam.combined);
		//System.out.println("X: " + Gdx.input.getX() + "   Y: " + (1080 - Gdx.input.getY()));
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
				if(enemy.getY() == 0){
					toRemove.add(enemy);
				}
			}
			
			if(enemy.getX() < camX + cam.viewportWidth/2){
				enemy.firstMove();
				enemy.draw(sr);
			}
		}
		
		
//		sr.setColor(Color.BLACK);
//		int range = Constants.SHORT_SIGHT_DISTANCE;
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
		
		randomness.draw(sr, cam, Gdx.input.isTouched());
		
		sr.end();
		batch.begin();
		//exploring.draw(batch, Gdx.input.getX(), Gdx.input.getY());
		batch.end();
		
		for(Enemy deadEnemies: toRemove){
			enemies.remove(deadEnemies);
		}
		learner.onFloor(player.onFloor());
		if(count > 3) {
			action = learner.calculateQ(player.getDeltaX(), player.getDeltaY(), player.isAlive());
			if(player.isAlive() == false){
				reset();
			} 
			count = 0;
		}
		count++;
		
		currentTime = new Date();
		
		if((currentTime.getTime() - startOfRunTime.getTime()) >= 120000){
			reset();
		}
		
		
	}
	
	private void reset(){
		if(deathCount > 5){
			deathCount = 0;
		}
		player.revive(deathCount);
		deathCount ++;
		enemies.clear();
		spawnEnemies();
		startOfRunTime = new Date();
		if((currentTime.getTime() - startTime.getTime()) > 7200000 && changed == false){
			learner.changeExploring();
			changed  = true;
		}
		
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
