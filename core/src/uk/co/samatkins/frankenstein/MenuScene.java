package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class MenuScene extends Scene {
	MenuScene(FrankGame game) {
		super(game);
		Label title = new Label("Frankenstein's Monsters, Inc.", game.skin, "title");
		title.setPosition(400, 350, Align.center);
		addActor(title);

		Label established = new Label("Established 1818", game.skin, "titleItalic");
		established.setPosition(400, 300, Align.center);
		addActor(established);

		TextButton playButton = new TextButton("Begin", game.skin);
		playButton.setPosition(400, 200, Align.center);
		playButton.addListener(new ClickListener(Input.Buttons.LEFT) {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MenuScene.this.game.playGame();
			}
		});
		addActor(playButton);
	}
}
