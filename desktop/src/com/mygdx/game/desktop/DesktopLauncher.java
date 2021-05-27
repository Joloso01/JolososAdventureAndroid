package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.JadventureMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Joloso's Adventure The Game";
		config.addIcon("sprites/ataqueIzquierda_2.png", Files.FileType.Internal);
//		config.setTitle("Joloso's Adventure The Game");
//		config.setWindowIcon("sprites/ataqueIzquierda_2.png");
		new LwjglApplication(new JadventureMain(), config);
	}
}
