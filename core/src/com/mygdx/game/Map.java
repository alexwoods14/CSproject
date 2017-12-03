package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Map {
	private ArrayList<ArrayList<Block>> layers =  new ArrayList<ArrayList<Block>>(5);

	private float floorY = 0;
	private float roofY = 10000;
	private float rightWall = 0;
	private float leftWall = 0;
	private int side = 48;

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

	public Map() {
		int mapWidth = 150;
		for(int j = 0; j < 20; j++){
			ArrayList<Block> blocks =  new ArrayList<Block>();
			for(int i = 0; i < mapWidth ;i++){
				blocks.add(new Block(i, j, true));
			}
			//add layer to list of layers
			layers.add(blocks);
		}
		for(int j = 4; j < 20; j++){
			for(int i = 0; i < mapWidth; i++){
				layers.get(j).get(i).setExists(false);
			}
		}
		
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
			if(yindex >= layers.size()){
				foundFloor = true;
			}
			else{
				if(yindex - 1 > 0){
					if(layers.get(yindex - i).get(xindex).exists() == true){
						floor = layers.get(yindex - i).get(xindex).getY() + side;
						foundFloor = true;
					}
				}      
				i++;
			}
		}

		return floor;
	}
	
	public float findRoof(int xindex, int yindex){
		float roof = 10000;
		boolean foundRoof = false;
		int i = 0;
		//System.out.println(yindex);
		while(foundRoof == false && i < 3){
			if(yindex >= layers.size()){
				foundRoof = true;
			}
			else{
				if(layers.get(yindex + i).get(xindex).exists() == true && layers.get(yindex + i).get(xindex).isSolid() == true){
					roof = layers.get(yindex + i).get(xindex).getY();
					foundRoof = true;
				}

				i++;
			}
		}

		return roof;
	}

	public float findRightWall(int xindex, int yindex, int limit){
		float wallX = 10000;
		boolean foundWall = false;
		int i = 0;
		while(foundWall == false && i < limit){
			if(yindex >= layers.size()){
				foundWall = true;
			}
			else{
				if(layers.get(yindex).get(xindex + i).exists() == true && layers.get(yindex).get(xindex + i).isSolid() == true){
					wallX = layers.get(yindex).get(xindex + i).getX() - side;
					foundWall = true;
					//System.out.println("HERE");
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
			if(yindex >= layers.size()){
				foundWall = true;
			}
			else{
				if(xindex - 1 > 0){
					if(layers.get(yindex).get(xindex - i).exists() == true && layers.get(yindex).get(xindex - i).isSolid() == true){
						wallX = layers.get(yindex).get(xindex - i).getX() + side;
						foundWall = true;
						//System.out.println("leftWall: " + (wallX/32));
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
		for(ArrayList<Block> layer : layers){
			for(Block block: layer){
				if(block.exists() == true){
					if(block.isSolid() == true){
						sr.rect(block.getX(), block.getY(), side, side);
					}
					else{
						sr.rect(block.getX(), (float) (block.getY() + side*0.75), side, (float) (side*0.25));
						
					}
					
//					sr.setColor(Color.GREEN);
//					boolean[] sides = edge(block, layer);
//					if(sides[0] = true){
//						sr.rect(block.getX(), block.getY(), 10, block.getSide());
//					}
//					if(sides[1] = true){
//						sr.rect(block.getX()+block.getSide() - 10, block.getY(), 10, block.getSide());
//					}
//					if(sides[2] = true){
//						sr.rect(block.getX(), block.getY() + block.getSide() - 10, block.getSide(), 10);
//					}
//					if(sides[3] = true){
//						sr.rect(block.getX(), block.getY(), block.getSide(), 10);
//					}
				}
			}
		}
	}
//	public boolean[] edge(Block block, ArrayList<Block> layer){
//		boolean side[] = new boolean[4];
//		//0left, 1right, 2top, 3bottom
//		side[0] = false;side[1] = false;side[2] = false;side[3] = false;
//		if(layers.indexOf(layer) > 0 && layers.indexOf(layer) < 15){
//			if(layer.get(layers.indexOf(block) - 1).exists() == false){
//				side[0] = true;
//			}
//			if(layer.get(layers.indexOf(block) + 1).exists() == false){
//				side[1] = true;
//			}
//			if(layers.get(layers.indexOf(layer) + 1).get(layers.indexOf(block) + 1).exists() == false){
//				side[2] = true;
//			}
//			if(layers.get(layers.indexOf(layer) - 1).get(layers.indexOf(block) + 1).exists() == false){
//				side[3] = true;
//			}
//		}
//		return side;
//	}
}


//						if(sideOfContact == Sides.LEFT){
//							drawX = block.getX();
//							drawY = block.getY();
//							drawWidth = 10;
//							drawHeight = block.getSide();
//						}
//						if(sideOfContact == Sides.RIGHT){
//							drawX = block.getX()+block.getSide() - 10;
//							drawY = block.getY();
//							drawWidth = 10;
//							drawHeight = block.getSide();
//						}
//						if(sideOfContact == Sides.TOP){
//							drawX = block.getX();
//							drawY = block.getY() + block.getSide() - 10;
//							drawWidth = block.getSide();
//							drawHeight = 10;
//						}
//						if(sideOfContact == Sides.BOTTOM){
//							drawX = block.getX();
//							drawY = block.getY();
//							drawWidth = block.getSide();
//							drawHeight = 10;
//						}
//						sr.rect(block.getX(), block.getY(), block.getSide(), block.getSide());
//						sr.setColor(Color.GOLD);
//						sr.set(ShapeType.Filled);
//						sr.rect(drawX, drawY, drawWidth, drawHeight);