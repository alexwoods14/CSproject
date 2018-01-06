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
	private boolean exploring;
	
	private int side = Constants.BLOCK_HEIGHT;
	private int straightRange = Constants.SIGHT_DISTANCE;
	private int diagonalRange = (int) (Math.sqrt(0.5)*straightRange);
	private int straightStep = Constants.BLOCK_HEIGHT/8;
	private int diagonalStep = (int) (Math.sqrt(0.5)*straightStep);
	
	private int[] lastState = new int[8];
	private int[] currentState = new int[8];
	
	private	objs top = null;
	private	objs topRight = null;
	private	objs right = null;
	private	objs bottomRight = null;
	private	objs bottom = null;
	private	objs bottomLeft = null;
	private	objs left = null;
	private	objs topLeft = null;
	
	private actions nextMove = actions.NONE;
	private actions lastMove = actions.NONE;
	
	private final double alpha = 0.1; // Learning rate
	private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future
		
	private double[][][][][][][][][] Q = new double[6][6][6][6][6][6][6][6][4]; 
	// first 8 dimensions are for the different locations: top, top right, right etc. etc.
	// 9th and last dimension is the possible actions it can take in ant given state: jump, left, right, nothing.
	private int count = 0;
	
	
	public Agent(Player player, Map map, ArrayList<Enemy> enemies, boolean exploring) {
		this.map = map;
		this.enemies = enemies;
		this.player = player;
		this.exploring = exploring;
		//random value for each state-action Q
		Random rand = new Random();
		for(int a = 0; a < 6; a++){
			for(int b = 0; b < 6; b++){
				for(int c = 0; c < 6; c++){
					for(int d = 0; d < 6; d++){
						for(int e = 0; e < 6; e++){
							for(int f = 0; f < 6; f++){
								for(int g = 0; g < 6; g++){
									for(int h = 0; h < 6; h++){
										for(int i = 0; i < 4; i++){
											Q[a][b][c][d][e][f][g][h][i] = rand.nextDouble(); 											
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
		SOLID_BLOCK, ROOFLESS_BLOCK, GROUND_ENEMY, VERTICAL_ENEMY, SINE_ENEMY;// (null)
		//    0    ,       1       ,       2     ,       3       ,      4    ,    5
		// array places in the learning arrays^
	}
	
	public enum actions{
		JUMP, LEFT, RIGHT, NONE;
		//0 ,  1  ,   2  ,  3		
	}
	
	private void findCurrentState(){
		
		Thread thread1 = new Thread(new Runnable() {
		@Override
			public void run() {
				top = findTop();
				topRight = findTopRight();
			}
		});
		Thread thread2 = new Thread(new Runnable() {
			@Override
				public void run() {
				right = findRight();
				bottomRight = findBottomRight();
			}
		});
		Thread thread3 = new Thread(new Runnable() {
			@Override
			public void run() {
				bottom = findBottom();
				bottomLeft = findBottomLeft();
			}
		});
		Thread thread4 = new Thread(new Runnable() {
			@Override
			public void run() {
				left = findLeft();
				topLeft = findTopLeft();
			}
		}); 
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		
		objs[] state = new objs[8];
		state[0] = top;
		state[1] = topRight;
		state[2] = right;
		state[3] = bottomRight;
		state[4] = bottom;
		state[5] = bottomLeft;
		state[6] = left;
		state[7] = topLeft;
		
		
		lastState = currentState;
			
		
		//SOLID_BLOCK, ROOFLESS_BLOCK, GROUND_ENEMY, VERTICAL_ENEMY, SINE_ENEMY,  (null)
		//    0    ,       1       ,       2     ,       3       ,      4    ,    5
		
		
		if(count < 2000){
			count++;
			System.out.println(count);
		}
		else{
			System.out.println("HERE");
			exploring = false;
		}
		
		
		
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
		
		//System.out.printf("[%d, %d, %d, %d, %d, %d, %d, %d] %n", currentState[0], currentState[1], currentState[2], currentState[3], currentState[4], currentState[5], currentState[6], currentState[7]);
		
		//System.out.printf("top: %-14s  topRight: %-14s  right: %-14s  bottomRight: %-14s  bottom: %-14s  bottomLeft: %-14s  left: %-14s  topLeft: %-14s %n", top, topRight, right, bottomRight, bottom, bottomLeft, left, topLeft);
		
		//System.out.println("bottom:" + bottom);
		//System.out.printf("top: %-14s  right: %-14s  bottom: %-14s  left: %-14s%n", top, right, bottom, left);
		
		//calculateQ(top, topRight, right, bottomRight, bottom, bottomLeft, left, topLeft);	
	}

	public actions calculateQ(double deltaX, boolean alive) {
		findCurrentState();
		Random rand = new Random();
		double reward = 0;
		if(deltaX > 0){
			reward = 0.5;
		}
		if(deltaX < 0){
			reward = - 0.5;
		}
		if(deltaX == 0){
			reward = -0.1;
		}
		if(alive == false){
			reward = -2;
		}
		if(exploring == true){
			if(rand.nextInt(2) == 0){
				nextMove = findBestAction(currentState);
			}
			else{
				int action = rand.nextInt(4);
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
			}
		}
		else{
			nextMove = findBestAction(currentState);
		}
		
		
		// Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
		double q = Q[lastState[0]][lastState[1]][lastState[2]][lastState[3]][lastState[4]][lastState[5]][lastState[6]][lastState[7]][lastMove.ordinal()];
		Q[lastState[0]][lastState[1]][lastState[2]][lastState[3]][lastState[4]][lastState[5]][lastState[6]][lastState[7]][lastMove.ordinal()] = q + alpha * (reward + gamma*findMaxQ(currentState) - q); 
		
		
		lastMove = nextMove;
		return nextMove;
			
		
	}
	
	
	private actions findBestAction(int[] state) {
		double maxQ = 0;
		int maxQindex = 5;
		for(int i = 0; i < 4; i ++){
			double max2 = Math.max(maxQ, Q[state[0]][state[1]][state[2]][state[3]][state[4]][state[5]][state[6]][state[7]][i]);
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
		for(int i = 0; i < 4; i ++){
			double max2 = Math.max(maxQ, Q[state[0]][state[1]][state[2]][state[3]][state[4]][state[5]][state[6]][state[7]][i]);
			maxQ = max2;
		}
		return maxQ;
		
	}

	private objs findTop() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = null;
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
		
		return toReturn;
	}

	private objs findTopRight() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = null;
		float x = player.getX() + player.getWidth()/2;
		float y = player.getY() + player.getHeight()/2;
		while(found == false && distanceFromCentre <= diagonalRange){
			int blockX = (int) ((x + distanceFromCentre)/side);
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
				if(enemy.crossesPoint(x+distanceFromCentre, y+distanceFromCentre) == true) {
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

		return toReturn;
	}

	private objs findRight() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = null;
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
		
		return toReturn;
	}

	private objs findBottomRight() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = null;
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

		return toReturn;
	}

	private objs findBottom() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = null;
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
		
		return toReturn;
	}

	private objs findBottomLeft() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = null;
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

		return toReturn;
	}

	private objs findLeft() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = null;
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
		
		return toReturn;
	}

	private objs findTopLeft() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = null;
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

		return toReturn;
	}
	
}
