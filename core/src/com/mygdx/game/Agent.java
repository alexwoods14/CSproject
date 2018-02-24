package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.mygdx.entities.Enemy;
import com.mygdx.entities.GroundEnemy;
import com.mygdx.entities.Player;
import com.mygdx.entities.SineFlyingEnemy;
import com.mygdx.entities.VerticalFlyingEnemy;
import com.mygdx.map.Map;

public class Agent {
	
	private Map map;
	private ArrayList<Enemy> enemies;
	private Player player;
	private boolean onFloor = false;
	private boolean newState = true;
	
	private int side = Constants.BLOCK_HEIGHT;
	private int straightRange = Constants.SIGHT_DISTANCE;
	private int diagonalRange = (int) (Math.sqrt(0.5)*straightRange);
	private int straightStep = Constants.BLOCK_HEIGHT/8;
	private int diagonalStep = (int) (Math.sqrt(0.5)*straightStep);
	
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
	private objs touchingFloor = objs.SOLID_BLOCK;
	
	
	private actions nextMove = actions.NONE;
	private actions lastMove = actions.NONE;
	
	private final double alpha = 0.1;
	private final double gamma = 0.9;
	
	private double[][][][][][][][][][] Q = new double[6][6][6][6][6][6][6][6][2][6];
	
	public Agent(Player player, Map map, ArrayList<Enemy> enemies) {
		this.map = map;
		this.enemies = enemies;
		this.player = player;
		
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
											//Q[a][b][c][d][e][f][g][h][action] = rand.nextDouble();
											Q[a][b][c][d][e][f][g][h][0][action] = 0.7;
											Q[a][b][c][d][e][f][g][h][1][action] = 0.7;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(onFloor == true){
			touchingFloor = objs.SOLID_BLOCK;
		}
		else {
			touchingFloor = objs.ROOFLESS_BLOCK;
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
		state[8] = touchingFloor;
		
		
		lastState = currentState.clone();


		//SOLID_BLOCK, ROOFLESS_BLOCK, GROUND_ENEMY, VERTICAL_ENEMY, SINE_ENEMY,  (null)
		//    0    ,       1       ,       2     ,       3       ,      4    ,    5
				
		
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
	

	public void calculateQ(double deltaX, double deltaY, boolean alive, int randomness) {
		findCurrentState();
		Random rand = new Random();
		double reward = 0;
		if(deltaX > 0){
			reward = 3;
		}
		if(deltaX < 0){
			reward = -3;
		}
		if(deltaX == 0){
			reward = -3;
		}
		if(alive == false){
			//died();
			reward = -10;
		}
		
//		else{
//			if(player.getX() > 170*Constants.BLOCK_HEIGHT){
//				reward = 50;
//			}
//		}
		


		if(rand.nextInt(101) >= randomness){
			nextMove = findBestAction(currentState);
		}
		else{
			int action;
			if(currentState[8] == 0){
				action = rand.nextInt(6);
			}
			else{
				action = rand.nextInt(4);
			}
			if(action == 0){
				nextMove = actions.JUMP;
			}
			if(action == 1){
				nextMove = actions.LEFT;
			}
			if(action == 2){
				nextMove = actions.RIGHT;
			}
			if(action == 3){
				nextMove = actions.NONE;
			}
			if(action == 4){
				nextMove = actions.JUMP_LEFT;
			}
			if(action == 5){
				nextMove = actions.JUMP_RIGHT;
			}
		}



		// Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
		double q = Q[lastState[0]][lastState[1]][lastState[2]][lastState[3]][lastState[4]][lastState[5]][lastState[6]][lastState[7]][lastState[8]][lastMove.ordinal()];
		Q[lastState[0]][lastState[1]][lastState[2]][lastState[3]][lastState[4]][lastState[5]][lastState[6]][lastState[7]][lastState[8]][lastMove.ordinal()] = q + alpha * (reward + gamma*findMaxQ(currentState) - q);

		lastMove = nextMove;
		newState = true;				
	}

	public actions getAction(){
		return nextMove;
	}
	
	public void onFloor(boolean onFloor) {
		if(onFloor != this.onFloor && newState == true) {
			this.onFloor = onFloor;
			newState = false;
		}
	}
	

	
	
	private actions findBestAction(int[] state) {
		double maxQ = 0;
		int maxQindex = 0;
		for(int i = 0; i < 6; i ++){
			double max2 = Math.max(maxQ, Q[state[0]][state[1]][state[2]][state[3]][state[4]][state[5]][state[6]][state[7]][state[8]][i]);
			if(maxQ != max2){
				maxQindex = i;
			}
			maxQ = max2;
		}
		actions next = actions.values()[maxQindex];		
		return next;
	}

	private double findMaxQ(int[] state){
		double maxQ = 0;
		for(int i = 0; i < 6; i ++){
			double max2 = Math.max(maxQ, Q[state[0]][state[1]][state[2]][state[3]][state[4]][state[5]][state[6]][state[7]][state[8]][i]);
			maxQ = max2;
		}
		return maxQ;
		
	}

	private void findTop() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
		float x = player.getX() + player.getWidth()/2;
		float y = player.getY() + player.getHeight()/2;
		while(found == false && distanceFromCentre <= straightRange){
			int blockX = (int) (x/side);
			int blockY = (int) ((y + distanceFromCentre)/side);
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
				if(enemy.crossesPoint(x, y+distanceFromCentre) == true) {
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
		float x = player.getX() + player.getWidth()/2;   // centre x of agent
		float y = player.getY() + player.getHeight()/2;  // centre y of agent
		while(found == false && distanceFromCentre <= diagonalRange){  //while nothing is found and still in range
			int blockX = (int) ((x + distanceFromCentre)/side);
			int blockY = (int) ((y + distanceFromCentre)/side);  //block x and y used by map
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
				if(enemy.crossesPoint(x+distanceFromCentre, y+distanceFromCentre) == true) {
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
		float x = player.getX() + player.getWidth()/2;
		float y = player.getY() + player.getHeight()/2;
		while(found == false && distanceFromCentre <= straightRange){
			int blockX = (int) ((x + distanceFromCentre)/side);
			int blockY = (int) (y/side);
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
				if(enemy.crossesPoint(x+distanceFromCentre, y) == true) {
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
		float x = player.getX() + player.getWidth()/2;
		float y = player.getY() + player.getHeight()/2;
		while(found == false && distanceFromCentre <= diagonalRange){
			int blockX = (int) ((x + distanceFromCentre)/side);
			int blockY = (int) ((y - distanceFromCentre)/side);
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
				if(enemy.crossesPoint(x+distanceFromCentre, y-distanceFromCentre) == true) {
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
		float x = player.getX() + player.getWidth()/2;
		float y = player.getY() + player.getHeight()/2;
		while(found == false && distanceFromCentre <= straightRange){
			int blockX = (int) (x/side);
			int blockY = (int) ((y - distanceFromCentre)/side);
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
				if(enemy.crossesPoint(x, y-distanceFromCentre) == true) {
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
		float x = player.getX() + player.getWidth()/2;
		float y = player.getY() + player.getHeight()/2;
		while(found == false && distanceFromCentre <= diagonalRange){
			int blockX = (int) ((x - distanceFromCentre)/side);
			int blockY = (int) ((y - distanceFromCentre)/side);
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
				if(enemy.crossesPoint(x-distanceFromCentre, y-distanceFromCentre) == true) {
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
		float x = player.getX() + player.getWidth()/2;
		float y = player.getY() + player.getHeight()/2;
		while(found == false && distanceFromCentre <= straightRange){
			int blockX = (int) ((x - distanceFromCentre)/side);
			int blockY = (int) (y/side);
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
				if(enemy.crossesPoint(x-distanceFromCentre, y) == true) {
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
		float x = player.getX() + player.getWidth()/2;
		float y = player.getY() + player.getHeight()/2;
		while(found == false && distanceFromCentre <= diagonalRange){
			int blockX = (int) ((x - distanceFromCentre)/side);
			int blockY = (int) ((y + distanceFromCentre)/side);
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
				if(enemy.crossesPoint(x-distanceFromCentre, y+distanceFromCentre) == true) {
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
