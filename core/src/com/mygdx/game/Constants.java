package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class Constants {
	public static final String ASSETS_FOLDER_LOCATION = Gdx.files.getLocalStoragePath().replace("desktop","core") + "assets/";
	public static final int WINDOW_HEIGHT = 720;
	public static final int WINDOW_WIDTH = 1280;
	public static final int MAP_WIDTH = 300;
	public static final int MAP_HEIGHT = 25;
	public static final int BLOCK_HEIGHT = 48;
	public static final int SHORT_SIGHT_DISTANCE = (int) (BLOCK_HEIGHT*5);
	public static final int FPS = 45;
}
