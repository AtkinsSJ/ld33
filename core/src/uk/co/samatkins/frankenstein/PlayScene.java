package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class PlayScene extends Scene {
	PlayScene(FrankGame game) {
		super(game);

		addActor(new Label("This is super fun!", game.skin));
	}
}
