package com.mygdx.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.game.Constants;

public class Map {

	private float floorY = 0;
	private float roofY = 10000;
	private float rightWall = 0;
	private float leftWall = 0;
	private int side = Constants.BLOCK_HEIGHT;
	private Block grid[][];

	public float getFloor(){
		return floorY;		
	}

	public float getRoofY() {
		return roofY;
	}

	public float getRightWall(){
		return rightWall;
	}

	public float getLeftWall(){
		return leftWall;
	}
	
	public int getSide(){
		return side;
	}
	
	public char get(int i, int j){
		char toReturn = 'N';
		if(i > 0 && i < Constants.MAP_WIDTH && j > 0 && j < Constants.MAP_HEIGHT){
			if(grid[i][j] != null) {
				if(grid[i][j].getClass() == SolidBlock.class){
					toReturn = 'S';
				}

				if(grid[i][j].getClass() == RooflessBlock.class){
					toReturn = 'R';
				}
			}
		}
		return toReturn;
	}

	public Map(String fileName) {
		grid = new Block[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
		Scanner reader = null;
		try {
			reader = new Scanner(new File(Constants.ASSETS_FOLDER_LOCATION + fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
			String line = null;
			if(reader.hasNextLine() == true){
				line = reader.nextLine();
			}
			else{
				break;
			}
			for(int i = 0; i < Constants.MAP_WIDTH; i++){
				char next = line.charAt(i);
				//System.out.print(next);
				if(next == 'N'){
					grid[i][j] = null;
				}
				if(next == 'S'){
					grid[i][j] = new SolidBlock(i, j);
				}
				if(next == 'R'){
					grid[i][j] = new RooflessBlock(i, j);
				}
			}
			//System.out.println();
		}
		reader.close();	
		
		
	}
		
	public void findAllBoundaries(float x2, float y2 , float width, float height){
		float leftFloor = findFloor((int) (x2/side), (int)(y2/side));
		float rightFloor = findFloor((int) ((x2 + width)/side), (int)(y2/side));

		if(leftFloor >= rightFloor){
			floorY = leftFloor;
		}
		else{
			floorY = rightFloor;
		}
		
		float leftRoof = findRoof((int) (x2/side), (int)(y2/side));
		float rightRoof = findRoof((int) ((x2 + width)/side), (int)(y2/side));
		
		if(leftRoof <= rightRoof){
			roofY = leftRoof;
		}
		else{
			roofY = rightRoof;
		}

		float topRightWall = findRightWall((int) (x2/side), (int)((y2 + side)/side), 3);
		float bottomRightWall = findRightWall((int) (x2/side), (int)((y2)/side), 3);
		
		if(topRightWall <= bottomRightWall){
			rightWall = topRightWall;
		}
		else{
			rightWall = bottomRightWall;
		}
		
		float topLeftWall = findLeftWall((int) (x2/side), (int)((y2 + side)/side), 3);
		float bottomLeftWall = findLeftWall((int) (x2/side), (int)((y2)/side), 3);
		
		if(topLeftWall >= bottomLeftWall){
			leftWall = topLeftWall;
		}
		else{
			leftWall = bottomLeftWall;
		}
		
	}
	
	public void findWalls(float x2, float y2 , float width, float height){
		rightWall = findRightWall((int) (x2/side), (int)(y2/side), 1);
		leftWall = findLeftWall((int) (x2/side), (int)(y2/side), 1);		
	}

	public float findFloor(int xindex, int yindex){
		float floor = 0;
		boolean foundFloor = false;
		int i = 1;
		//System.out.println(yindex);
		while(foundFloor == false && i < 3){
			if(yindex >= Constants.MAP_HEIGHT){
				foundFloor = true;
			}
			else{
				if(yindex - i > 0){
					if(grid[xindex][yindex - i] != null){
						floor = grid[xindex][yindex - i].getY() + side;
						foundFloor = true;
					}
				}      
				i++;
			}
		}

		return floor;
	}
	
	public float findStartingFloor(float x, int width){
		float floor = 0;
		boolean leftFound = false;
		boolean rightFound = false;
		int i = 0;
		int leftX = (int) (x/side);
		int rightX = (int) ((x+width)/side);
		while(leftFound == false && rightFound == false){
			if(leftFound  == false){
				if(grid[leftX][i] == null){
					leftFound = true;
					floor = (i+1)*side;
				}
			}
			if(rightFound == false) {
				if(grid[rightX][i] == null){
					rightFound = true;
					floor = (i+1)*side;
				}
			}
			i++;
		}
		return floor;
	}

	public float findRoof(int xindex, int yindex){
		float roof = 10000;
		boolean foundRoof = false;
		int i = 0;
		//System.out.println(yindex);
		while(foundRoof == false && i < 3){
			if(yindex >= Constants.MAP_HEIGHT){
				foundRoof = true;
			}
			else{
				if(yindex + i > 0 && xindex > 0){
					if(grid[xindex][yindex + i] != null){
						if(grid[xindex][yindex + i].getClass() == SolidBlock.class){
							roof = grid[xindex][yindex + i].getY();
							foundRoof = true;
						}
					}
				}
				i++;
			}
		}

		return roof;
	}

	public float findRightWall(int xindex, int yindex, int limit){
		float wallX = 100000;
		boolean foundWall = false;
		int i = 0;
		while(foundWall == false && i < limit){
			if(yindex >= Constants.MAP_HEIGHT){
				foundWall = true;
			}
			else{
				if(grid[xindex + i][yindex] != null ){
					if(grid[xindex + i][yindex].getClass() == SolidBlock.class){

						wallX = grid[xindex + i][yindex].getX() - side;
						foundWall = true;
						//System.out.println("HERE");
					}
				}
				i++;
			}
		}

		return wallX;
	}

	public float findLeftWall(int xindex, int yindex, int limit){
		float wallX = 0;
		boolean foundWall = false;
		int i = 0;
		//System.out.println("yindex: " + yindex + "  xindex: " + xindex);
		while(foundWall == false && i < limit){
			if(yindex >= Constants.MAP_HEIGHT){
				foundWall = true;
			}
			else{
				if(xindex - 1 > 0){
					if(grid[xindex - i][yindex] != null){
						if(grid[xindex - i][yindex].getClass() == SolidBlock.class){
							wallX = grid[xindex - i][yindex].getX() + side;
							foundWall = true;
							//System.out.println("leftWall: " + (wallX/32));
						}
					}
				}
				i++;
			}
		}
		return wallX;
	}

	public void draw(ShapeRenderer sr){
		sr.setAutoShapeType(true);
		sr.set(ShapeType.Line);
		sr.setColor(Color.RED);
		for(int j = 0; j < Constants.MAP_HEIGHT; j++){
			for(int i = 0; i< Constants.MAP_WIDTH; i++){
				if(grid[i][j] != null){
					if(grid[i][j].getClass() == SolidBlock.class){
						sr.rect(grid[i][j].getX(), grid[i][j].getY(), side, side);
					}
					else{
						sr.rect(grid[i][j].getX(), (float) (grid[i][j].getY() + side*0.75), side, (float) (side*0.25));
					}
				}
			}
		}
	}
}