package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.entities.Enemy;
import com.mygdx.entities.GroundEnemy;
import com.mygdx.entities.Player;
import com.mygdx.game.Block.Sides;

public class World implements Screen{


	OrthographicCamera cam;

	float gravity = 3500.0f;
	int camX = 720;
	int camY = 452; 
	int side;

	Player player;
	Map map;
	ArrayList<Enemy> enemies;

	float WIDTH = Gdx.graphics.getWidth();
	float HEIGHT = Gdx.graphics.getHeight();
	private ShapeRenderer sr;

	public World(ShapeRenderer sr) {
		this.sr = sr;
	}

    

	@Override
	public void show() {
		cam = new OrthographicCamera(1440, 810);
		player = new Player();
		map = new Map();
		side = map.getSide();
		enemies = new ArrayList<Enemy>();
		enemies.add(new GroundEnemy((int) (30.5*side), 4*side, false));
		enemies.add(new GroundEnemy((int) (34.5*side), 7*side, false));
		enemies.add(new GroundEnemy((int) (40.5*side), 9*side, false));
		
		
	}

	@Override
	public void render(float delta) {	


		Gdx.gl.glClearColor(173/255f, 218/255f, 248/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		map.findAllBoundaries(player.getX(), player.getY(), player.getWidth(), player.getHeight());
		player.move(delta, gravity, map.getFloor(), map.getRoofY(), map.getLeftWall(), map.getRightWall());

		if(player.getX() >= 720){
			camX = (int) player.getX();
		}

		if(player.getY() >= 452){
			camY = (int) player.getY();
		}

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
			if(player.hasCollided(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()) == true){			
				if(enemy.collisionSide(player.getX(), player.getY(), player.getWidth(), player.getHeight()) == Sides.TOP){
					toRemove.add(enemy);
					player.bounce();
				}
				else{
					System.exit(0);
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
  
		sr.end();

		for(Enemy deadEnemies: toRemove){
			enemies.remove(deadEnemies);
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
	}


}
