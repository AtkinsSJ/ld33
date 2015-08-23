package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FrankGame extends ApplicationAdapter {
	SpriteBatch batch;
	Viewport viewport;

	private Scene scene;
	public Skin skin;
	private Music music;

	@Override
	public void create () {
		batch = new SpriteBatch();
		viewport = new FitViewport(780, 600);

		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("packed.atlas"));
		skin = new Skin(Gdx.files.internal("skin.json"), atlas);

		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		music.setVolume(0.5f);
		music.setLooping(true);
		music.play();

		showMenu();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		scene.act();
		scene.draw();
	}

	private void setScene(Scene newScene) {
		if (this.scene != null) {
			this.scene.dispose();
		}
		this.scene = newScene;
		Gdx.input.setInputProcessor(this.scene);
	}

	public void playGame() {
		setScene(new PlayScene(this));
	}

	public void showMenu() {
		setScene(new MenuScene(this));
	}
}
