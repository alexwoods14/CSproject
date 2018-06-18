package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class Constants {
	public static final String ASSETS_FOLDER_LOCATION = Gdx.files.getLocalStoragePath().replace("desktop","core") + "assets/";
	public static final int WINDOW_HEIGHT = 720;
	public static final int WINDOW_WIDTH = 1280;
	public static final int MAP_WIDTH = 300;
	public static final int MAP_HEIGHT = 25;
	public static final int BLOCK_HEIGHT = 48;
	public static final int SIGHT_DISTANCE = (int) (BLOCK_HEIGHT*3.5);
	public static final int FPS = 60;
}
