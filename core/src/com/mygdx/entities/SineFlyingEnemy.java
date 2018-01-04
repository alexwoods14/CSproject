package com.mygdx.entities;

public class SineFlyingEnemy extends FlyingEnemy{

	public SineFlyingEnemy(int x, int yUpperLimit, int yLowerLimit, float timePeriod, float horiDisplacement) {
		super(x, yUpperLimit, yLowerLimit, timePeriod, false);
		horiV = horiDisplacement/(timePeriod*4);
		this.xUpperLimit = (int) (x + horiDisplacement);
		this.xLowerLimit = x;
	}

}
