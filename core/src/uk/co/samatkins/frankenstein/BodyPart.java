package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

public class BodyPart extends Image {
	private float vX;
	private float vY;
	private final float gravity;
	private final float totalLife;
	private final float deltaRotation;
	private float life;

	public BodyPart(TextureRegion region, float x, float y, float vX, float vY, float gravity, float lifetime, float rotation, float deltaRotation) {
		super(region);
		setPosition(x, y, Align.center);
		setRotation(rotation);
		setOrigin(getImageWidth()/2f, getImageHeight()/2f);
		this.vX = vX;
		this.vY = vY;
		this.gravity = gravity;
		this.totalLife = lifetime;
		this.life = 0;
		this.deltaRotation = 0;//deltaRotation;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		vY += gravity * delta;
		setPosition(getX() + vX * delta, getY() + vY * delta);
		rotateBy(deltaRotation * delta);

		life += delta;
		if (life > totalLife) {
			remove();
		}
	}
}
