package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class MenuScene extends Scene {
	private static final String INSTRUCTIONS =
		"You are Dr. Frankenstein. Like any self-respecting entrepreneur, you've turned your monster-building " +
		"experiments into a business! And why get your hands dirty when you can delegate?\n\n" +
		"Body parts are unearthed at the graveyard, are sewn together in the operating theatre, " +
		"then zapped to life with a healthy dose of lightning.\n\n" +
		"The resulting monsters can then either be assigned to work for you, or sold for a healthy profit. " +
		"Human beings will also apply for jobs, and are about 4 times more effective, but cost 4 times as much " +
		"in wages.\n\n" +
		"Your actions understandably generate public outrage - the higher it is, the fewer people will want " +
		"to work for you, and if it ever reaches 100% an angry mob will burn down your premises. Try to avoid that, " +
		"by spending money on charitable works.";

	MenuScene(FrankGame game) {
		super(game);
		Label title = new Label("Frankenstein's Monsters, Inc.", game.skin, "title");
		title.setPosition(390, 560, Align.center);
		addActor(title);

		Label established = new Label("Established 1818", game.skin, "titleItalic");
		established.setPosition(390, 520, Align.center);
		addActor(established);

		Label instructions = new Label(INSTRUCTIONS, game.skin);
		instructions.setWrap(true);
		instructions.setWidth(700);
		instructions.setPosition(390, 300, Align.center);
		addActor(instructions);

		TextButton playButton = new TextButton("Begin", game.skin);
		playButton.setPosition(390, 100, Align.center);
		playButton.addListener(new ClickListener(Input.Buttons.LEFT) {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MenuScene.this.game.playGame();
			}
		});
		addActor(playButton);
	}
}
