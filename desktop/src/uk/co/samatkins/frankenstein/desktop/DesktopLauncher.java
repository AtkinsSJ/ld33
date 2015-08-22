package uk.co.samatkins.frankenstein.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import uk.co.samatkins.frankenstein.FrankGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 780;
		config.height = 600;
		config.title = "Frankenstein's Monsters, Inc.";
		new LwjglApplication(new FrankGame(), config);
	}
}
