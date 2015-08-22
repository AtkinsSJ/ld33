package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PlayScene extends Scene {

	private final TextureRegion diggingBackground;
	private int diggers, diggerMonsters;
	private float diggingCounter;
	private float diggingDelay;

	private final TextureRegion surgeryBackground;
	private int surgeons, surgeonMonsters;
	private float stitchingCounter;
	private float stitchingDelay;

	private final TextureRegion zappingBackground;
	private int zappers, zapperMonsters;
	private float zappingCounter;
	private float zappingDelay;

	private int bodyPartCount;
	private int bodyCount;
	private int monsterCount;

	private final Label diggingLabel;
	private final Label stitchingLabel;
	private final Label zappingLabel;
	private final Label monstersLabel;
	private final Label salesLabel;
	private final Label outrageLabel;
	private final Label fundsLabel;
	private final Label incomeLabel;
	private final Label expensesLabel;

	private final TextureRegion tempMan;
	private boolean draggingMonster;
	private float dragX, dragY;

	enum Room {
		Digging,
		Surgery,
		Zapping
	}
	private Room getRoomAtPosition(float x, float y) {
		if (x < 0 || x >= 780 || y < 0 || y >= 600) {
			return null;
		}

		if (x < 260) {
			if (y >= 400) {
				return Room.Digging;
			}
		} else if (x < 520) {
			if (y >= 400) {
				return Room.Surgery;
			}
		} else {
			if (y >= 400) {
				return Room.Zapping;
			}
		}

		return null;
	}

	PlayScene(FrankGame game) {
		super(game);

		tempMan = game.skin.getRegion("temp-man");

		bodyPartCount = 0;
		bodyCount = 0;
		monsterCount = 11;

		diggingBackground = game.skin.getRegion("graves");
		diggers = 0;
		diggerMonsters = 0;
		diggingCounter = 0;
		diggingDelay = 1f;
		diggingLabel = new Label("Body Parts: 0 / minute", game.skin);
		addDigger(false);

		surgeryBackground = game.skin.getRegion("operating-theatre");
		surgeons = 0;
		surgeonMonsters = 0;
		stitchingCounter = 0;
		stitchingDelay = 5f;
		stitchingLabel = new Label("Bodies: 0 / minute", game.skin);
		addSurgeon(false);

		zappingBackground = game.skin.getRegion("zap-room");
		zappers = 0;
		zapperMonsters = 0;
		zappingCounter = 0;
		zappingDelay = 5f;
		zappingLabel = new Label("Monsters: 0 / minute", game.skin);
		addZapper(false);

		Table table = new Table(game.skin);
		table.setFillParent(true);
		addActor(table);
		table.defaults().fill().width(260).height(200).top();

		table.add(diggingLabel);
		table.add(stitchingLabel);
		table.add(zappingLabel).row();

		monstersLabel = new Label("Monsters", game.skin);
		salesLabel = new Label("Job Applicants: 0", game.skin);
		outrageLabel = new Label("Public Outrage: 15%", game.skin);

		table.add(monstersLabel);
		table.add(salesLabel);
		table.add(outrageLabel).row();

		Table statsTable = new Table(game.skin);
		fundsLabel = new Label("Funds: �22.37", game.skin);
		incomeLabel = new Label("Income: �0.35 / minute", game.skin);
		expensesLabel = new Label("Expenses: �0.20 / minute", game.skin);

		statsTable.add("All lies:").row();
		statsTable.add(fundsLabel).row();
		statsTable.add(incomeLabel).row();
		statsTable.add(expensesLabel).row();
		table.add(statsTable).colspan(3).width(780);

		updateLabels();

		getRoot().addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				if (pointer == Input.Buttons.LEFT
					&& draggingMonster) {

					Room room = getRoomAtPosition(x, y);
					if (room != null) {
						switch (room) {
							case Digging: {
								addDigger(true);
								monsterCount--;
							} break;
							case Surgery: {
								addSurgeon(true);
								monsterCount--;
							} break;
							case Zapping: {
								addZapper(true);
								monsterCount--;
							} break;
						}
					}
					draggingMonster = false;
				}
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				super.touchDragged(event, x, y, pointer);
				if (draggingMonster && pointer == Input.Buttons.LEFT) {
					dragX = x;
					dragY = y;
				}
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				// Drag a monster if we have monsters and the mouse is in the monster closet
				if (pointer == Input.Buttons.LEFT
					&& monsterCount >= 1
					&& x >= 0 && x <= 260
					&& y >= 200 && y <= 400) {

					draggingMonster = true;
					dragX = x;
					dragY = y;

					return true;
				} else {
					return super.touchDown(event, x, y, pointer, button);
				}
			}
		});

//		table.debug(Table.Debug.all);
	}

	void updateLabels() {
		float bodyPartsPerMinute = 60f / diggingDelay;
		float bodiesPerMinute = 60f / stitchingDelay;
		float monstersPerMinute = 60f / zappingDelay;

		diggingLabel.setText(String.format("Body Parts: %1$d\n(%2$.2f / minute)", bodyPartCount, bodyPartsPerMinute));
		stitchingLabel.setText(String.format("Bodies: %1$d\n(%2$.2f / minute)", bodyCount, bodiesPerMinute));
		zappingLabel.setText(String.format("Monsters: %1$d\n(%2$.2f / minute)", monsterCount, monstersPerMinute));

		monstersLabel.setText(String.format("Monsters: %d", monsterCount));
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// Digging
		diggingCounter += delta;
		while (diggingCounter >= diggingDelay) {
			diggingCounter -= diggingDelay;
			bodyPartCount++;
			// TODO: Animate a +1 body part
		}

		// Stitching
		if (bodyPartCount >= 5) {
			stitchingCounter += delta;
			while (stitchingCounter >= stitchingDelay && bodyPartCount >= 5) {
				stitchingCounter -= stitchingDelay;
				bodyPartCount -= 5;
				bodyCount++;
				// TODO: Animate a +1 body
			}
		}

		// Zapping
		if (bodyCount >= 1) {
			zappingCounter += delta;
			while (zappingCounter >= zappingDelay && bodyCount >= 1) {
				zappingCounter -= zappingDelay;
				bodyCount -= 1;
				monsterCount++;
				// TODO: Animate a +1 monster
			}
		}

		updateLabels();
	}

	@Override
	public void draw() {
		// Draw the departments!
		Batch batch = getBatch();
		batch.begin();

		batch.draw(diggingBackground, 0, 400);

		batch.draw(surgeryBackground, 260, 400);

		batch.draw(zappingBackground, 520, 400);

		// Monster storage
//		batch.draw(zappingBackground, 0, 200);
		int drawMonsters = monsterCount - (draggingMonster ? 1 : 0);
		for (int i=0; i<drawMonsters; i++) {
			batch.draw(tempMan, (i % 8) * 30, 250 - (i / 8) * 10);
		}

		if (draggingMonster) {
			batch.draw(tempMan, dragX, dragY);
		}

		batch.end();

		super.draw();
	}

	void addDigger(boolean isMonster) {
		if (isMonster) {
			diggerMonsters++;
		} else {
			diggers++;
		}
		float digPerMinute = (diggers * 18f) + (diggerMonsters * 4f);
		diggingDelay = 60f / digPerMinute;
	}

	void addSurgeon(boolean isMonster) {
		if (isMonster) {
			surgeonMonsters++;
		} else {
			surgeons++;
		}
		float stitchPerMinute = (surgeons * 4f) + (surgeonMonsters * 1f);
		stitchingDelay = 60f / stitchPerMinute;
	}

	void addZapper(boolean isMonster) {
		if (isMonster) {
			zapperMonsters++;
		} else {
			zappers++;
		}
		float zapPerMinute = (zappers * 3f) + (zapperMonsters * 0.8f);
		zappingDelay = 60f / zapPerMinute;
	}
}
