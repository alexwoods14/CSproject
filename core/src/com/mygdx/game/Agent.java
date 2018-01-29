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
	private boolean onFloor = false;
	private boolean newState = true;
	
	private int side = Constants.BLOCK_HEIGHT;
	private int straightRange = Constants.SIGHT_DISTANCE;
	private int diagonalRange = (int) (Math.sqrt(0.5)*straightRange);
	private int straightStep = Constants.BLOCK_HEIGHT/8;
	private int diagonalStep = (int) (Math.sqrt(0.5)*straightStep);
	
	private int[] lastState = new int[9];
	private int[] lastStateOnGround = new int[9];
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
	private actions lastMoveOnGround = actions.NONE;
	
	private final double alpha = 0.1;
	private final double gamma = 0.9;
	
	private double[][][][][][][][][][] Q = new double[7][7][7][7][7][7][7][7][2][6];
	
	public Agent(Player player, Map map, ArrayList<Enemy> enemies, boolean exploring) {
		this.map = map;
		this.enemies = enemies;
		this.player = player;
		this.exploring = exploring;
		//random value for each state-action Q
		//Random rand = new Random();
		for(int a = 0; a < 7; a++){
			for(int b = 0; b < 7; b++){
				for(int c = 0; c < 7; c++){
					for(int d = 0; d < 7; d++){
						for(int e = 0; e < 7; e++){
							for(int f = 0; f < 7; f++){
								for(int g = 0; g < 7; g++){
									for(int h = 0; h < 7; h++){
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
		SOLID_BLOCK, ROOFLESS_BLOCK, GROUND_ENEMY, VERTICAL_ENEMY, SINE_ENEMY, MAP_LIMIT, NONE
		//    0    ,       1       ,       2     ,       3       ,      4    ,    5    ,    6
		// array places in the learning arrays^
	}

	public enum actions{
		JUMP, LEFT, RIGHT, NONE, JUMP_LEFT, JUMP_RIGHT;
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
				currentState[i] = 6;
			}
			else{
				currentState[i] = (obj.ordinal());
			}
			i++;
		}
		
		//System.out.printf("[%d, %d, %d, %d, %d, %d, %d, %d] %n", lastStateOnFloor[0], lastStateOnFloor[1], lastStateOnFloor[2], lastStateOnFloor[3], lastStateOnFloor[4], lastStateOnFloor[5], lastStateOnFloor[6], lastStateOnFloor[7]);
		
		//System.out.printf("top: %-14s  topRight: %-14s  right: %-14s  bottomRight: %-14s  bottom: %-14s  bottomLeft: %-14s  left: %-14s  topLeft: %-14s onFloor: %-4s %n", top, topRight, right, bottomRight, bottom, bottomLeft, left, topLeft, touchingFloor == objs.SOLID_BLOCK ? "Yes" : "No");
		
	}

	public actions calculateQ(double deltaX, double deltaY, boolean alive, int randomness) {
		findCurrentState();
		Random rand = new Random();
		double reward = 0;
		if(deltaX > 0){
			reward = 0.1;
		}
		if(deltaX < 0){
			reward = -3;
		}
		if(deltaX == 0){
			reward = -5;
		}
		if(alive == false){
			//died();
			reward = -25;
		}
//		else{
//			if(player.getX() > 170*Constants.BLOCK_HEIGHT){
//				reward = 50;
//			}
//		}
		
		if(exploring == true){
			if(rand.nextInt(101) >= randomness){
				nextMove = findBestAction(currentState);
			}
			else{
				int action = rand.nextInt(6);
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
		}
		else{
			nextMove = findBestAction(currentState);
		}
		
		if(currentState[8] == 0){
			//System.out.println("****");
			lastStateOnGround = currentState.clone();
			lastMoveOnGround = nextMove;
		}

		// Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
		double q = Q[lastState[0]][lastState[1]][lastState[2]][lastState[3]][lastState[4]][lastState[5]][lastState[6]][lastState[7]][lastState[8]][lastMove.ordinal()];
		Q[lastState[0]][lastState[1]][lastState[2]][lastState[3]][lastState[4]][lastState[5]][lastState[6]][lastState[7]][lastState[8]][lastMove.ordinal()] = q + alpha * (reward + gamma*findMaxQ(currentState) - q);

		lastMove = nextMove;
		newState = true;
		return nextMove;
				
	}

	private void died() {
		if(exploring == false){
			//System.out.printf("top: %-14s| topRight: %-14s| right: %-14s| bottomRight: %-14s| bottom: %-14s| bottomLeft: %-14s| Left: %-14s| topLeft: %-14s| touching floor: %-4s --- %s %n", objs.values()[lastStateOnGround[0]], objs.values()[lastStateOnGround[1]], objs.values()[lastStateOnGround[2]], objs.values()[lastStateOnGround[3]], objs.values()[lastStateOnGround[4]], objs.values()[lastStateOnGround[5]], objs.values()[lastStateOnGround[6]], objs.values()[lastStateOnGround[7]], lastStateOnGround[8] == 0 ? "yes" : "no", lastMoveOnGround);
			double q = Q[lastStateOnGround[0]][lastStateOnGround[1]][lastStateOnGround[2]][lastStateOnGround[3]][lastStateOnGround[4]][lastStateOnGround[5]][lastStateOnGround[6]][lastStateOnGround[7]][lastStateOnGround[8]][lastMoveOnGround.ordinal()];
			Q[lastStateOnGround[0]][lastStateOnGround[1]][lastStateOnGround[2]][lastStateOnGround[3]][lastStateOnGround[4]][lastStateOnGround[5]][lastStateOnGround[6]][lastStateOnGround[7]][lastStateOnGround[8]][lastMoveOnGround.ordinal()] = q + alpha * (-5 - q);
		}
		
	}
	
	public void onFloor(boolean onFloor) {
		if(onFloor != this.onFloor && newState == true) {
			this.onFloor = onFloor;
			newState = false;
		}
	}
	
	public void changeExploring(){
		if(exploring == true){
			exploring = false;
			System.out.println("OPTIMAL ROUTING");
		}
		else{
			exploring = true;
			System.out.println("EXPLORATIVE ROUTING");
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
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = objs.NONE;
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
		
		if(y-distanceFromCentre <= 0 && toReturn.equals(objs.NONE)){
			toReturn = objs.MAP_LIMIT;
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
		
		if(y-distanceFromCentre <= 0 && toReturn.equals(objs.NONE)){
			toReturn = objs.MAP_LIMIT;
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

		if((y-diagonalRange <= 0) && toReturn.equals(objs.NONE)){
			toReturn = objs.MAP_LIMIT;
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
		
		if(x-distanceFromCentre <= 0 && toReturn.equals(objs.NONE)){
			toReturn = objs.MAP_LIMIT;
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
		
		if(x-distanceFromCentre <= 0 && toReturn.equals(objs.NONE)){
			toReturn = objs.MAP_LIMIT;
		}

		topLeft = toReturn;
	}
}
