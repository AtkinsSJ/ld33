package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class Scene extends Stage {
	public final FrankGame game;

	Scene(FrankGame game) {
		super(game.viewport, game.batch);
		this.game = game;
	}
}
