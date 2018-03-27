package com.mygdx.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.entities.Enemy;
import com.mygdx.entities.GroundEnemy;
import com.mygdx.entities.Player;
import com.mygdx.entities.SineFlyingEnemy;
import com.mygdx.entities.VerticalFlyingEnemy;
import com.mygdx.map.Map;

public class Agent extends Player{
	
	private Map map;
	private ArrayList<Enemy> enemies;
	private Slider randomness;
	private Texture direction;
	
	private double deathReward;
	private double rightReward;
	private double leftReward;
	private double upReward;
	private double downReward;
	private double stationaryReward;
	
	private int count;
	private Date startOfRunTime;
	private Date currentTime;
	private boolean aliveSinceLastState;
	
	private int side = Constants.BLOCK_HEIGHT;
	private int straightRange = Constants.SIGHT_DISTANCE;
	private int diagonalRange = (int) (Math.sqrt(0.5)*straightRange);
	private int straightStep = 5;
	private int diagonalStep = (int) (Math.sqrt(0.5)*straightStep);
	
	private double deltaX;
	private double deltaY;
	private int[] lastState = new int[9];
	private int[] currentState = new int[9];
	
	private	objs top = null;
	private	objs topRight = null;
	private	objs right = null;
	private	objs bottomRight = null;
	private	objs bottom = null;
	private	objs bottomLeft = null;
	private	objs left = null;
	private	objs topLeft = null;
	private objs onFloor = objs.SOLID_BLOCK;
	
	private actions nextMove = actions.NONE;
	private actions lastMove = actions.NONE;
	
	private final double alpha;
	private final double gamma;
	
	private double[][][][][][][][][][] Q = new double[6][6][6][6][6][6][6][6][2][6];
	
	public Agent(Map map, ArrayList<Enemy> enemies, double eagerness, double learningRate, double deathReward,
		     double rightReward, double leftReward, double upReward, double downReward, double stationaryReward) {
		super();
		this.map = map;
		this.enemies = enemies;
		
		this.randomness = new Slider(140, Constants.WINDOW_HEIGHT - 80, "randomness", true, 0, 1);
		alpha = learningRate;
		gamma = eagerness;
		this.deathReward = deathReward;
		this.rightReward = rightReward;
		this.leftReward = leftReward;
		this.upReward = upReward;
		this.downReward = downReward;
		this.stationaryReward = stationaryReward;
		
		startOfRunTime = new Date();
		direction = new Texture("arrow.png");
		
		//random value for each state-action Q
		//Random rand = new Random();
		for(int a = 0; a < 6; a++){
			for(int b = 0; b < 6; b++){
				for(int c = 0; c < 6; c++){
					for(int d = 0; d < 6; d++){
						for(int e = 0; e < 6; e++){
							for(int f = 0; f < 6; f++){
								for(int g = 0; g < 6; g++){
									for(int h = 0; h < 6; h++){
										//action
										for(int action = 0; action < 6; action++){
											Q[a][b][c][d][e][f][g][h][0][action] = 0.5;
											Q[a][b][c][d][e][f][g][h][1][action] = 0.5;
										}	

									}	
								}	
							}	
						}	
					}	
				}	
			}
		}
	}

	private enum objs{
		SOLID_BLOCK, ROOFLESS_BLOCK, GROUND_ENEMY, VERTICAL_ENEMY, SINE_ENEMY, NONE
		//    0    ,       1       ,       2     ,       3       ,      4    ,    5
	}
	
	public enum actions{
		LEFT, RIGHT, NONE, JUMP, JUMP_LEFT, JUMP_RIGHT;
		//0 ,  1  ,   2  ,  3  ,    4     ,      5
	}
	
	@Override
	public void move(float delta, float gravity, float floorY, float roofY, float leftWall, float rightWall) {
		if(touchingFloor == false){
					
			if(y == floorY && y > 0){
				touchingFloor = true;
			}
			
		}

		
		if((nextMove == actions.JUMP || nextMove == actions.JUMP_LEFT || nextMove == actions.JUMP_RIGHT) && y == floorY){
			vertV = 1250.0f;
		}
		
		if(y > floorY){
			vertV -= gravity * delta;
		}

		if(y + vertV*delta > floorY){		
			y += vertV * delta;
		}
		else{
			y = floorY;
			vertV = 0;
			
		}
		
		if(y + vertV*delta + height > roofY){
			vertV = -100;
			y = roofY - height;
		}
		if(nextMove == actions.LEFT || nextMove == actions.JUMP_LEFT){
			if(x - delta*horiV > leftWall){
				deltaX -= delta*horiV;
				x -= delta*horiV;
			}
			else{
				x = leftWall +  0.001f;
			} 
		}

		if(nextMove == actions.RIGHT|| nextMove == actions.JUMP_RIGHT){
			if(x + delta*horiV < rightWall){
				deltaX += delta*horiV;
				x += delta*horiV;
			}
			else{
				x = rightWall - 0.001f;
			}
		}

		
		if(x > 200*Constants.BLOCK_HEIGHT){
			aliveSinceLastState = false;
		}
		
		deltaY += vertV*delta;
		
		if(y <= 0){
			died();
		}
		
	}
	
	public void draw(ShapeRenderer sr, OrthographicCamera cam) {
		super.draw(sr, cam);
		int[][] toDraw = new int[3][3];
		toDraw[0][0] = currentState[7];
		toDraw[1][0] = currentState[0];
		toDraw[2][0] = currentState[1];
		toDraw[0][1] = currentState[6];
		toDraw[2][1] = currentState[2];
		toDraw[0][2] = currentState[5];
		toDraw[1][2] = currentState[4];
		toDraw[2][2] = currentState[3];
		
		//SOLID_BLOCK, ROOFLESS_BLOCK, GROUND_ENEMY, VERTICAL_ENEMY, SINE_ENEMY,  (null)
		//      0    ,       1       ,       2     ,       3       ,      4    ,    5		
		
		float xDueToCam = cam.position.x - cam.viewportWidth/2;
		float yDueToCam = cam.position.y - cam.viewportHeight/2;
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(i != 1 || j != 1){
					sr.set(ShapeType.Filled);
					if(toDraw[i][2-j] == 0){
						sr.setColor(Color.OLIVE);					
					}
					if(toDraw[i][2-j] == 2 || toDraw[i][2-j] == 3 || toDraw[i][2-j] == 4){
						sr.setColor(Color.FIREBRICK);
					}
					if(toDraw[i][2-j] == 1){
						sr.setColor(Color.TEAL);
					}
					if(toDraw[i][2-j] == 5){

					}
					else{
						sr.rect(xDueToCam + 70+i*45, yDueToCam + 320+j*45, 40, 40);
					}
					sr.set(ShapeType.Line);
					sr.setColor(Color.BLACK);
					sr.rect(xDueToCam + 70+i*45, yDueToCam + 320+j*45, 40, 40);
				}
				else {
//					if(currentState[8] == 1) {
//						sr.setColor(Color.PINK);
//					}
//					else {
//						sr.setColor(Color.YELLOW);
//					}
//					sr.set(ShapeType.Filled);
//					sr.rect(xDueToCam + 70+45, yDueToCam + 320+45, 40, 40);
				}
			}
		}
		
		randomness.draw(sr, cam, Gdx.input.isTouched());
		
	
	}
	
	public void draw(SpriteBatch batch) {
		int angle = 0;
		if(nextMove == actions.RIGHT){angle = 0;}
		if(nextMove == actions.LEFT){angle = 180;}
		if(nextMove == actions.JUMP){angle = 90;}
		if(nextMove == actions.JUMP_LEFT){angle = 135;}
		if(nextMove == actions.JUMP_RIGHT){angle = 45;}	
		
		if(nextMove != actions.NONE) {
			batch.draw(direction, 60, Constants.WINDOW_HEIGHT - 220, direction.getWidth()/2, direction.getHeight()/2, direction.getWidth(), direction.getHeight(), 1, 1, angle, 0, 0, direction.getWidth(), direction.getHeight(), false, false);
		}	
		randomness.drawLabel(batch);
		

		if(count > 3) {
			this.calculateQ();
			count = 0;
			if(alive == false) {
				aliveSinceLastState = false;
			}
			else {
				aliveSinceLastState = true;
			}
		}
		count++;

		currentTime = new Date();

		if((currentTime.getTime() - startOfRunTime.getTime()) >= 90000){
			died();
		}
		
	}
	
	private void findCurrentState(){
		
		Thread thread1 = new Thread(new Runnable() {
		@Override
			public void run() {
				findTop();
				findTopRight();
			}
		});
		Thread thread2 = new Thread(new Runnable() {
			@Override
				public void run() {
				findRight();
				findBottomRight();
			}
		});
		Thread thread3 = new Thread(new Runnable() {
			@Override
			public void run() {
				findBottom();
				findBottomLeft();
			}
		});
		Thread thread4 = new Thread(new Runnable() {
			@Override
			public void run() {
				findLeft();
				findTopLeft();
			}
		}); 
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		
		while(thread1.isAlive() == true || thread2.isAlive() == true || thread3.isAlive() == true || thread4.isAlive() == true){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(touchingFloor == true){
			onFloor = objs.ROOFLESS_BLOCK;
		}
		else {
			onFloor = objs.SOLID_BLOCK;
		}
		
		objs[] state = new objs[9];
		state[0] = top;
		state[1] = topRight;
		state[2] = right;
		state[3] = bottomRight;
		state[4] = bottom;
		state[5] = bottomLeft;
		state[6] = left;
		state[7] = topLeft;
		state[8] = onFloor;
		
		
		//lastState = currentState.clone();


		//SOLID_BLOCK, ROOFLESS_BLOCK, GROUND_ENEMY, VERTICAL_ENEMY, SINE_ENEMY,  (null)
		//      0    ,       1       ,       2     ,       3       ,      4    ,    5
				
		
		int i = 0;
		for(objs obj: state){
			if(obj == null){
				currentState[i] = 5;
			}
			else{
				currentState[i] = (obj.ordinal());
			}
			i++;
		}
		
		//System.out.printf("[%d, %d, %d, %d, %d, %d, %d, %d] %n", lastStateOnFloor[0], lastStateOnFloor[1], lastStateOnFloor[2], lastStateOnFloor[3], lastStateOnFloor[4], lastStateOnFloor[5], lastStateOnFloor[6], lastStateOnFloor[7]);
		
		//System.out.printf("top: %-14s  topRight: %-14s  right: %-14s  bottomRight: %-14s  bottom: %-14s  bottomLeft: %-14s  left: %-14s  topLeft: %-14s onFloor: %-4s %n", top, topRight, right, bottomRight, bottom, bottomLeft, left, topLeft, touchingFloor == objs.SOLID_BLOCK ? "Yes" : "No");
		
	}
	
	private void calculateQ() {
		findCurrentState();
		Random rand = new Random();
		double reward = 0;
		if(deltaX > 0){
			reward += rightReward;
		}
		if(deltaX < 0){
			reward += leftReward;
		}
		if(deltaX == 0) {
			reward = stationaryReward;
		}
		if(deltaY > 0) {
			reward += upReward;
		}
		if(deltaY < 0) {
			reward += downReward;
		}
		if(alive == false){
			reward = deathReward;
		}
		
		if(rand.nextDouble() >= randomness.getDecimal()){
			nextMove = findBestAction(currentState);
		}
		else{
			int action;
			if(currentState[8] == 1){
				action = rand.nextInt(6);
			}
			else{
				action = rand.nextInt(3);
			}
			nextMove = actions.values()[action];
		}

		// Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
		double q = Q[lastState[0]][lastState[1]][lastState[2]][lastState[3]][lastState[4]][lastState[5]][lastState[6]][lastState[7]][lastState[8]][lastMove.ordinal()];
		Q[lastState[0]][lastState[1]][lastState[2]][lastState[3]][lastState[4]][lastState[5]][lastState[6]][lastState[7]][lastState[8]][lastMove.ordinal()] = q + alpha * (reward + gamma*findMaxQ(currentState) - q);

		//System.out.printf(" %-7s :  %.4f  :  %.1f  :     deltaX: %.2f %n", lastMove, Q[lastState[0]][lastState[1]][lastState[2]][lastState[3]][lastState[4]][lastState[5]][lastState[6]][lastState[7]][lastState[8]][lastMove.ordinal()], reward, deltaX);
		
		lastState = currentState.clone();
		lastMove = nextMove;
		deltaX = 0;
		deltaY = 0;
		touchingFloor = false;
	}
		
	private actions findBestAction(int[] state) {
		double maxQ = -1000000;
		int maxQindex = 2;
		int limit;
		if(state[8] == 1) {
			limit = 6;
		}
		else {
			limit = 3;
		}
		for(int i = 0; i < limit; i ++){
			if(maxQ < Q[state[0]][state[1]][state[2]][state[3]][state[4]][state[5]][state[6]][state[7]][state[8]][i]){
				maxQindex = i;
				maxQ = Q[state[0]][state[1]][state[2]][state[3]][state[4]][state[5]][state[6]][state[7]][state[8]][i];
			}
		}
		actions next = actions.values()[maxQindex];		
		return next;
	}

	private double findMaxQ(int[] state){
		double maxQ = -10000000;
		int limit;
		if(state[8] == 1) {
			limit = 6;
		}
		else {
			limit = 3;
		}
		for(int i = 0; i < limit; i ++){
			double max2 = Math.max(maxQ, Q[state[0]][state[1]][state[2]][state[3]][state[4]][state[5]][state[6]][state[7]][state[8]][i]);
			maxQ = max2;
		}
		return maxQ;
		
	}

	public void revive(Map map) {
		super.revive(map);
		aliveSinceLastState = true;
		startOfRunTime = new Date();
	}
		
	public boolean isAlive() {
		return aliveSinceLastState;
	}
	
	private void findTop() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
		float midX = x + width/2;
		float midY = y + height/2;
		while(found == false && distanceFromCentre <= straightRange){
			int blockX = (int) (midX/side);
			int blockY = (int) ((midY + distanceFromCentre)/side);
			char block = map.get(blockX, blockY); 
			if(block == 'S'){
				toReturn = objs.SOLID_BLOCK;
				found = true;				
			}
			if(block == 'R') {
				toReturn = objs.ROOFLESS_BLOCK;
				found = true;
			}
			for(Enemy enemy: enemies) {
				if(enemy.crossesPoint(midX, midY+distanceFromCentre) == true) {
					found = true;
					if(enemy.getClass() == GroundEnemy.class){
						toReturn = objs.GROUND_ENEMY;
					}
					if(enemy.getClass() == SineFlyingEnemy.class){
						toReturn = objs.SINE_ENEMY;
					}
					if(enemy.getClass() == VerticalFlyingEnemy.class){
						toReturn = objs.VERTICAL_ENEMY;
					}
					break;
				}
			}
			
			distanceFromCentre += straightStep;
		}
		
		top = toReturn;
	}

	private void findTopRight() {
		boolean found = false;   // whether an object has been found in this direction
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
		float midX = x + width/2;				// centre x of agent
		float midY = y + height/2;  			// centre y of agent
		while(found == false && distanceFromCentre <= diagonalRange){  //while nothing is found and still in range
			int blockX = (int) ((midX + distanceFromCentre)/side);
			int blockY = (int) ((midY + distanceFromCentre)/side);  //block x and y used by map
			char block = map.get(blockX, blockY); 
			if(block == 'S'){
				toReturn = objs.SOLID_BLOCK;
				found = true;				
			}					   // if returned value for map.get() is not 'N', found = true
			if(block == 'R') {			   // and return is updated to corresponding 'obj' enum.
				toReturn = objs.ROOFLESS_BLOCK;
				found = true;
			}
			for(Enemy enemy: enemies) {			// Loops through all enemies.
				if(enemy.crossesPoint(midX+distanceFromCentre, midY+distanceFromCentre) == true) {
					found = true;
					if(enemy.getClass() == GroundEnemy.class){	// if enemy found
						toReturn = objs.GROUND_ENEMY;			 
					}
					if(enemy.getClass() == SineFlyingEnemy.class){
						toReturn = objs.SINE_ENEMY;
					}
					if(enemy.getClass() == VerticalFlyingEnemy.class){
						toReturn = objs.VERTICAL_ENEMY;
					}
					break;
				}
			}
						// if nothing is found, the distance away from the agent is increased
			distanceFromCentre += diagonalStep;	
		}

		topRight = toReturn;
	}

	private void findRight() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
		float midX = x + width/2;
		float midY = y + height/2;
		while(found == false && distanceFromCentre <= straightRange){
			int blockX = (int) ((midX + distanceFromCentre)/side);
			int blockY = (int) (midY/side);
			char block = map.get(blockX, blockY); 
			if(block == 'S'){
				toReturn = objs.SOLID_BLOCK;
				found = true;				
			}
			if(block == 'R') {
				toReturn = objs.ROOFLESS_BLOCK;
				found = true;
			}
			for(Enemy enemy: enemies) {
				if(enemy.crossesPoint(midX+distanceFromCentre, midY) == true) {
					found = true;
					if(enemy.getClass() == GroundEnemy.class){
						toReturn = objs.GROUND_ENEMY;
					}
					if(enemy.getClass() == SineFlyingEnemy.class){
						toReturn = objs.SINE_ENEMY;
					}
					if(enemy.getClass() == VerticalFlyingEnemy.class){
						toReturn = objs.VERTICAL_ENEMY;
					}
					break;
				}
			}
			
			distanceFromCentre += straightStep;
		}
		
		right = toReturn;
	}

	private void findBottomRight() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
		float midX = x + width/2;
		float midY = y + height/2;
		while(found == false && distanceFromCentre <= diagonalRange){
			int blockX = (int) ((midX + distanceFromCentre)/side);
			int blockY = (int) ((midY - distanceFromCentre)/side);
			char block = map.get(blockX, blockY); 
			if(block == 'S'){
				toReturn = objs.SOLID_BLOCK;
				found = true;				
			}
			if(block == 'R') {
				toReturn = objs.ROOFLESS_BLOCK;
				found = true;
			}
			for(Enemy enemy: enemies) {
				if(enemy.crossesPoint(midX+distanceFromCentre, midY-distanceFromCentre) == true) {
					found = true;
					if(enemy.getClass() == GroundEnemy.class){
						toReturn = objs.GROUND_ENEMY;
					}
					if(enemy.getClass() == SineFlyingEnemy.class){
						toReturn = objs.SINE_ENEMY;
					}
					if(enemy.getClass() == VerticalFlyingEnemy.class){
						toReturn = objs.VERTICAL_ENEMY;
					}
					break;
				}
			}

			distanceFromCentre += diagonalStep;
		}

		bottomRight = toReturn;
	}

	private void findBottom() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
		float midX = x + width/2;
		float midY = y + height/2;
		while(found == false && distanceFromCentre <= straightRange){
			int blockX = (int) (midX/side);
			int blockY = (int) ((midY - distanceFromCentre)/side);
			char block = map.get(blockX, blockY); 
			if(block == 'S'){
				toReturn = objs.SOLID_BLOCK;
				found = true;				
			}
			if(block == 'R') {
				toReturn = objs.ROOFLESS_BLOCK;
				found = true;
			}
			for(Enemy enemy: enemies) {
				if(enemy.crossesPoint(midX, midY-distanceFromCentre) == true) {
					found = true;
					if(enemy.getClass() == GroundEnemy.class){
						toReturn = objs.GROUND_ENEMY;
					}
					if(enemy.getClass() == SineFlyingEnemy.class){
						toReturn = objs.SINE_ENEMY;
					}
					if(enemy.getClass() == VerticalFlyingEnemy.class){
						toReturn = objs.VERTICAL_ENEMY;
					}
					break;
				}
			}
			
			distanceFromCentre += straightStep;
		}
		
		bottom = toReturn;
	}

	private void findBottomLeft() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
		float midX = x + width/2;
		float midY = y + height/2;
		while(found == false && distanceFromCentre <= diagonalRange){
			int blockX = (int) ((midX - distanceFromCentre)/side);
			int blockY = (int) ((midY - distanceFromCentre)/side);
			char block = map.get(blockX, blockY); 
			if(block == 'S'){
				toReturn = objs.SOLID_BLOCK;
				found = true;				
			}
			if(block == 'R') {
				toReturn = objs.ROOFLESS_BLOCK;
				found = true;
			}
			for(Enemy enemy: enemies) {
				if(enemy.crossesPoint(midX-distanceFromCentre, midY-distanceFromCentre) == true) {
					found = true;
					if(enemy.getClass() == GroundEnemy.class){
						toReturn = objs.GROUND_ENEMY;
					}
					if(enemy.getClass() == SineFlyingEnemy.class){
						toReturn = objs.SINE_ENEMY;
					}
					if(enemy.getClass() == VerticalFlyingEnemy.class){
						toReturn = objs.VERTICAL_ENEMY;
					}
					break;
				}
			}

			distanceFromCentre += diagonalStep;
		}
		
		bottomLeft = toReturn;
	}

	private void findLeft() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
		float midX = x + width/2;
		float midY = y + height/2;
		while(found == false && distanceFromCentre <= straightRange){
			int blockX = (int) ((midX - distanceFromCentre)/side);
			int blockY = (int) (midY/side);
			char block = map.get(blockX, blockY); 
			if(block == 'S'){
				toReturn = objs.SOLID_BLOCK;
				found = true;				
			}
			if(block == 'R') {
				toReturn = objs.ROOFLESS_BLOCK;
				found = true;
			}
			for(Enemy enemy: enemies) {
				if(enemy.crossesPoint(midX-distanceFromCentre, midY) == true) {
					found = true;
					if(enemy.getClass() == GroundEnemy.class){
						toReturn = objs.GROUND_ENEMY;
					}
					if(enemy.getClass() == SineFlyingEnemy.class){
						toReturn = objs.SINE_ENEMY;
					}
					if(enemy.getClass() == VerticalFlyingEnemy.class){
						toReturn = objs.VERTICAL_ENEMY;
					}
					break;
				}
			}
			
			distanceFromCentre += straightStep;
		}
		
		left = toReturn;
	}

	private void findTopLeft() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
		float midX = x + width/2;
		float midY = y + height/2;
		while(found == false && distanceFromCentre <= diagonalRange){
			int blockX = (int) ((midX - distanceFromCentre)/side);
			int blockY = (int) ((midY + distanceFromCentre)/side);
			char block = map.get(blockX, blockY); 
			if(block == 'S'){
				toReturn = objs.SOLID_BLOCK;
				found = true;				
			}
			if(block == 'R') {
				toReturn = objs.ROOFLESS_BLOCK;
				found = true;
			}
			for(Enemy enemy: enemies) {
				if(enemy.crossesPoint(midX-distanceFromCentre, midY+distanceFromCentre) == true) {
					found = true;
					if(enemy.getClass() == GroundEnemy.class){
						toReturn = objs.GROUND_ENEMY;
					}
					if(enemy.getClass() == SineFlyingEnemy.class){
						toReturn = objs.SINE_ENEMY;
					}
					if(enemy.getClass() == VerticalFlyingEnemy.class){
						toReturn = objs.VERTICAL_ENEMY;
					}
					break;
				}
			}

			distanceFromCentre += diagonalStep;
		}

		topLeft = toReturn;
	}
}
