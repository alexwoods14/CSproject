package com.mygdx.game;

import java.util.ArrayList;

import com.mygdx.entities.Enemy;
import com.mygdx.entities.Player;
import com.mygdx.map.Block;
import com.mygdx.map.Map;
import com.mygdx.map.SolidBlock;

public class Agent {
	
	private Map map;
	private ArrayList<Enemy> enemies;
	private Player player;
	private int side = Constants.BLOCK_HEIGHT;
	private int range = Constants.SIGHT_DISTANCE;
	private int step = Constants.BLOCK_HEIGHT/8;

	private	objs top = null;
	private	objs topRight = null;
	private	objs right = null;
	private	objs bottomRight = null;
	private	objs bottom = null;
	private	objs bottomLeft = null;
	private	objs left = null;
	private	objs topLeft = null;

		
	public Agent(Player player, Map map, ArrayList<Enemy> enemies) {
		this.map = map;
		this.enemies = enemies;
		this.player = player;
	}
	
	private enum objs{
		SOLID_BLOCK, ROOFLESS_BLOCK, GROUND_ENEMY, VERTICAL_ENEMY, SINE_ENEMY;
	}
	
	public void run(){
		
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
		
		
		//System.out.printf("top: %-14s  topRight: %-14s  right: %-14s  bottomRight: %-14s  bottom: %-14s  bottomLeft: %-14s  left: %-14s  topLeft: %-14s %n", top, topRight, right, bottomRight, bottom, bottomLeft, left, topLeft);
		System.out.println("bottom:" + bottom);
		
	}

	private objs findTop() {
		boolean found = true;
		int distanceFromCentre = 0;
		objs toReturn = objs.GROUND_ENEMY;
		
		while(found == false && distanceFromCentre <= range){
			
		}
		
		return toReturn;
	}

	private objs findTopRight() {
		boolean found = true;
		int distanceFromCentre = 0;
		objs toReturn = null;
		
		while(found == false && distanceFromCentre <= range){
			
		}
		
		return toReturn;
	}

	private objs findRight() {
		boolean found = true;
		int distanceFromCentre = 0;
		objs toReturn = null;
		
		while(found == false && distanceFromCentre <= range){
			
		}
		
		return toReturn;
	}

	private objs findBottomRight() {
		boolean found = true;
		int distanceFromCentre = 0;
		objs toReturn = null;
		
		while(found == false && distanceFromCentre <= range){
			
		}
		
		return toReturn;
	}

	private objs findBottom() {
		boolean found = false;
		int distanceFromCentre = 0;
		objs toReturn = null;
		float x = player.getX() + player.getWidth()/2;
		float y = player.getY() + player.getHeight()/2;
		while(found == false && distanceFromCentre <= range){
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
					toReturn = objs.GROUND_ENEMY;
					break;
				}
			}
			
			distanceFromCentre += step;
		}
		
		return toReturn;
	}

	private objs findBottomLeft() {
		boolean found = true;
		int distanceFromCentre = 0;
		objs toReturn = null;
		
		while(found == false && distanceFromCentre <= range){
			
		}
		
		return toReturn;
	}

	private objs findLeft() {
		boolean found = true;
		int distanceFromCentre = 0;
		objs toReturn = null;
		
		while(found == false && distanceFromCentre <= range){
			
		}
		
		return toReturn;
	}

	private objs findTopLeft() {
		boolean found = true;
		int distanceFromCentre = 0;
		objs toReturn = null;
		
		while(found == false && distanceFromCentre <= range){
			
		}
		
		return toReturn;
	}
	
}
