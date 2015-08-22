package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class PlayScene extends Scene {

	private final TextureRegion diggingBackground;
	private int diggers, diggerMonsters;
	private float diggingCounter;
	private float diggingDelay;

	private final TextureRegion surgeryBackground;
	private int surgeons, surgeonMonsters;
	private float stitchingCounter;
	private float stitchingDelay;
	private final Label stitchingWaitingLabel;

	private final TextureRegion zappingBackground;
	private int zappers, zapperMonsters;
	private float zappingCounter;
	private float zappingDelay;
	private final Label zappingWaitingLabel;

	private int bodyPartCount;
	private int bodyCount;
	private int monsterCount;

	private final Money money;
	private final Money income;
	private final Money expenses, expensesPerSecond, expensesRemainder;
	private float moneyChangeCounter;
	private int secondsCounter;
	private final Money sellMonsterAmount = new Money(0, 5, 0);

	private final Label diggingLabel;
	private final Label stitchingLabel;
	private final Label zappingLabel;
	private final Label monstersLabel;
	private final Label applicantsLabel;
	private final Label outrageLabel;
	private final Label fundsLabel;
	private final Label incomeLabel;
	private final Label expensesLabel;

	private final TextureRegion applicantBackground,
								monstersBackground,
								outrageBackground;
	private final TextureRegion applicantImage;
	private int applicantCount;
	private float applicantCounter;
	private final TextureRegion tempMan;
	private final Group sellOverlay;

	private boolean draggingApplicant;
	private boolean draggingMonster;
	private float dragX, dragY;

	private float outragePercent;
	private final TextureRegion sadFace;

	enum GameState {
		Playing,
		Lost
	}
	private GameState gameState;
	private final Group gameOverOverlay;

	enum Room {
		Digging,
		Surgery,
		Zapping,
		MonsterCloset,
		Applicants,
		PublicOutrage,
		Sell
	}
	private Room getRoomAtPosition(float x, float y) {
		Room result;

		if (x < 0 || x >= 780 || y < 0 || y >= 600) {
			result = null;
		} else {

			if (y < 200) {
				result = Room.Sell;
			} else if (y < 400) {
				if (x < 260) {
					result = Room.MonsterCloset;
				} else if (x < 520) {
					result = Room.Applicants;
				} else {
					result = Room.PublicOutrage;
				}
			} else {
				if (x < 260) {
					result = Room.Digging;
				} else if (x < 520) {
					result = Room.Surgery;
				} else {
					result = Room.Zapping;
				}
			}
		}

		return result;
	}

	PlayScene(FrankGame game) {
		super(game);

		gameState = GameState.Playing;
		money = new Money(0, 5, 6);
		income = new Money(0, 0, 0);
		expenses = new Money(0, 0, 0);
		expensesPerSecond = new Money(0, 0, 0);
		expensesRemainder = new Money(0, 0, 0);
		moneyChangeCounter = 0;
		secondsCounter = 0;
		outragePercent = 45f;

		tempMan = game.skin.getRegion("temp-man");
		applicantImage = game.skin.getRegion("applicant");
		sadFace = game.skin.getRegion("sadface");

		monstersBackground = game.skin.getRegion("monster-closet");
		applicantBackground = game.skin.getRegion("waiting-room");
		outrageBackground = game.skin.getRegion("outrage-room");

		bodyPartCount = 0;
		bodyCount = 0;
		monsterCount = 1;
		applicantCount = 3 + 3;
		applicantCounter = 0;

		diggingBackground = game.skin.getRegion("graves");
		diggers = 0;
		diggerMonsters = 0;
		diggingCounter = 0;
		diggingDelay = 1f;
		diggingLabel = new Label("Body Parts: 0 / minute", game.skin);
		diggingLabel.setAlignment(Align.topLeft);
		addDigger(false);

		surgeryBackground = game.skin.getRegion("operating-theatre");
		surgeons = 0;
		surgeonMonsters = 0;
		stitchingCounter = 0;
		stitchingDelay = 5f;
		stitchingLabel = new Label("Bodies: 0 / minute", game.skin);
		stitchingLabel.setAlignment(Align.topLeft);
		stitchingWaitingLabel = new Label("Waiting!", game.skin, "warning");
		stitchingWaitingLabel.setPosition(260 + 130, 420, Align.center);
		addSurgeon(false);
		addActor(stitchingWaitingLabel);

		zappingBackground = game.skin.getRegion("zap-room");
		zappers = 0;
		zapperMonsters = 0;
		zappingCounter = 0;
		zappingDelay = 5f;
		zappingLabel = new Label("Monsters: 0 / minute", game.skin);
		zappingLabel.setAlignment(Align.topLeft);
		zappingWaitingLabel = new Label("Waiting!", game.skin, "warning");
		zappingWaitingLabel.setPosition(520 + 130, 420, Align.center);
		addZapper(false);
		addActor(zappingWaitingLabel);

		Table table = new Table(game.skin);
		table.setFillParent(true);
		addActor(table);
		table.defaults().fill().width(260).height(200).top();

		table.add(diggingLabel);
		table.add(stitchingLabel);
		table.add(zappingLabel).row();

		monstersLabel = new Label("Monsters", game.skin);
		monstersLabel.setAlignment(Align.topLeft);
		applicantsLabel = new Label("Job Applicants: 0", game.skin);
		applicantsLabel.setAlignment(Align.topLeft);
		outrageLabel = new Label("Public Outrage: 15%", game.skin);
		outrageLabel.setAlignment(Align.topLeft);

		table.add(monstersLabel);
		table.add(applicantsLabel);
		table.add(outrageLabel).row();

		// Stats
		{
			Table statsTable = new Table(game.skin);
			statsTable.defaults().expandX().fillX().left();
			fundsLabel = new Label("", game.skin);
			incomeLabel = new Label("", game.skin);
			expensesLabel = new Label("", game.skin);

			statsTable.add("Financial Circumstances:").row();
			statsTable.add(fundsLabel).row();
			statsTable.add(incomeLabel).row();
			statsTable.add(expensesLabel).row();
			table.add(statsTable).colspan(2).width(520);
		}

		// Buttons
		{
			Table buttonsTable = new Table(game.skin);

			buttonsTable.add(new OutrageButton("Orphanage", game.skin, new Money(3, 10, 0), 30f)).row();

			table.add(buttonsTable).row();
		}

		updateLabels();

		getRoot().addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				if (pointer == Input.Buttons.LEFT
					&& (draggingMonster || draggingApplicant)) {

					Room room = getRoomAtPosition(x, y);
					if (gameState == GameState.Playing
						&& room != null) {
						switch (room) {
							case Digging: {
								addDigger(draggingMonster);
							} break;

							case Surgery: {
								addSurgeon(draggingMonster);
							} break;

							case Zapping: {
								addZapper(draggingMonster);
							} break;

							case Sell: {
								if (draggingMonster) {
									sellMonster();
								}
							} break;
						}
					}
					draggingMonster = false;
					draggingApplicant = false;
				}
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				super.touchDragged(event, x, y, pointer);
				if (pointer == Input.Buttons.LEFT
					&& (draggingMonster || draggingApplicant)) {
					dragX = x;
					dragY = y;
				}
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				// Drag a monster if we have monsters and the mouse is in the monster closet
				if (gameState == GameState.Playing
					&& pointer == Input.Buttons.LEFT) {

					Room room = getRoomAtPosition(x, y);
					if (room == null) {
						return false;
					}

					switch (room) {
						case MonsterCloset: {
							if (monsterCount >= 1) {
								draggingMonster = true;
								dragX = x;
								dragY = y;
								return true;
							}
						} break;

						case Applicants: {
							if (applicantCount >= 1) {
								draggingApplicant = true;
								dragX = x;
								dragY = y;
								return true;
							}
						} break;
					}

					return true;
				} else {
					return false;
				}
			}
		});

		// Sell Overlay
		{
			sellOverlay = new Group();
			Image image = new Image(game.skin.getRegion("sell-overlay"));
			image.setPosition(0,0);
			sellOverlay.addActor(image);

			Label sellLabel = new Label("SELL", game.skin, "title");
			sellLabel.setPosition(390, 125, Align.center);
			sellOverlay.addActor(sellLabel);

			Label costLabel = new Label(String.format("(+ %s)", sellMonsterAmount), game.skin, "title");
			costLabel.setPosition(390, 75, Align.center);
			sellOverlay.addActor(costLabel);

			addActor(sellOverlay);
		}

		// Game Over Overlay
		{
			gameOverOverlay = new Group();
			Image image = new Image(game.skin.getRegion("lost-overlay"));
			image.setPosition(0, 0);
			gameOverOverlay.addActor(image);

			Label lostTitle = new Label("Game Over!", game.skin, "title");
			lostTitle.setPosition(390, 500, Align.center);
			gameOverOverlay.addActor(lostTitle);

			Label lostReason = new Label("Unfortunately, you have run out of money.", game.skin, "titleItalic");
			lostReason.setPosition(390, 400, Align.center);
			gameOverOverlay.addActor(lostReason);

			TextButton menuButton = new TextButton("Return to Menu", game.skin);
			menuButton.addListener(new ClickListener(Input.Buttons.LEFT) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					PlayScene.this.game.showMenu();
				}
			});
			menuButton.setPosition(390, 200, Align.center);
			gameOverOverlay.addActor(menuButton);

			addActor(gameOverOverlay);
			gameOverOverlay.setVisible(false);
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// Digging
		diggingCounter += delta;
		while (diggingCounter >= diggingDelay) {
			diggingCounter -= diggingDelay;
			bodyPartCount++;
			outragePercent += 0.1f;
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
			stitchingWaitingLabel.setVisible(false);
		} else {
			stitchingWaitingLabel.setVisible(true);
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
			zappingWaitingLabel.setVisible(false);
		} else {
			zappingWaitingLabel.setVisible(true);
		}

		income.setTotalPence(0);

		moneyChangeCounter += delta;
		if (moneyChangeCounter > 1f) {
			secondsCounter++;
			if (secondsCounter >= 60) {
				secondsCounter -= 60;
				// Remainder
				money.subtract(expensesRemainder);
			}

			moneyChangeCounter -= 1f;
			money.subtract(expensesPerSecond);

			if (money.isLessThan(Money.Zero)) {
				// You're out of cash!
				gameOver(true);
			}
		}

		// New applicants!
		if (applicantCount < 5) {
			applicantCounter += delta;
			if (applicantCounter > 10f) {
				applicantCounter -= 10f;
				applicantCount++;
			}
		}



		updateLabels();
	}

	void updateLabels() {
		float bodyPartsPerMinute = 60f / diggingDelay;
		float bodiesPerMinute = 60f / stitchingDelay;
		float monstersPerMinute = 60f / zappingDelay;

		diggingLabel.setText(String.format("Body Parts: %1$d (%2$.2f / minute)", bodyPartCount, bodyPartsPerMinute));
		stitchingLabel.setText(String.format("Bodies: %1$d (%2$.2f / minute)\nRequires 5 body parts", bodyCount, bodiesPerMinute));
		zappingLabel.setText(String.format("Monsters: %1$d (%2$.2f / minute)", monsterCount, monstersPerMinute));

		monstersLabel.setText(String.format("Monsters: %d\n(Click and drag to assign or sell)", monsterCount));
		applicantsLabel.setText(String.format("Applicants: %d\n(Click and drag to assign)", applicantCount));

		incomeLabel.setText(String.format("Income: %s / minute", income));
		expensesLabel.setText(String.format("Expenses: %s / minute", expenses));
		fundsLabel.setText(String.format("Funds: %s", money));

		outrageLabel.setText(String.format("Public Outrage: %1$.2f%%", outragePercent));
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
		batch.draw(monstersBackground, 0, 200);
		int drawMonsters = monsterCount - (draggingMonster ? 1 : 0);
		for (int i=0; i<drawMonsters; i++) {
			batch.draw(tempMan, (i % 8) * 30, 250 - (i / 8) * 10);
		}

		// Job applicants
		batch.draw(applicantBackground, 260, 200);
		int drawApplicants = applicantCount - (draggingApplicant ? 1 : 0);
		for (int i=0; i<drawApplicants; i++) {
			batch.draw(applicantImage, 260 + (i % 8) * 30, 250 - (i / 8) * 10);
		}

		// Public Outrage
		batch.draw(outrageBackground, 520, 200);
		int drawOutrages = (int) (outragePercent / 10);
		for (int i=0; i<drawOutrages; i++) {
			batch.draw(sadFace, 520 + (i % 5) * 52, 300 - (i / 5) * 100);
		}

		if (draggingMonster) {
			sellOverlay.setVisible(true);
			batch.draw(tempMan, dragX, dragY);
		} else {
			sellOverlay.setVisible(false);
		}

		if (draggingApplicant) {
			batch.draw(applicantImage, dragX, dragY);
		}

		batch.end();

		super.draw();
	}

	void addDigger(boolean isMonster) {
		if (isMonster) {
			diggerMonsters++;
			monsterCount--;
		} else {
			diggers++;
			applicantCount--;
		}
		float digPerMinute = (diggers * 18f) + (diggerMonsters * 4f);
		diggingDelay = 60f / digPerMinute;

		updateExpenses();
	}

	void addSurgeon(boolean isMonster) {
		if (isMonster) {
			surgeonMonsters++;
			monsterCount--;
		} else {
			surgeons++;
			applicantCount--;
		}
		float stitchPerMinute = (surgeons * 4f) + (surgeonMonsters * 1f);
		stitchingDelay = 60f / stitchPerMinute;

		updateExpenses();
	}

	void addZapper(boolean isMonster) {
		if (isMonster) {
			zapperMonsters++;
			monsterCount--;
		} else {
			zappers++;
			applicantCount--;
		}
		float zapPerMinute = (zappers * 3f) + (zapperMonsters * 0.8f);
		zappingDelay = 60f / zapPerMinute;

		updateExpenses();
	}

	void sellMonster() {
		money.add(sellMonsterAmount);
		monsterCount--;
		outragePercent += 0.2f;
	}

	void updateExpenses() {
		int expensesPence = (int)(3 * (diggerMonsters + surgeonMonsters + zapperMonsters)
			+ 12 * (diggers + surgeons + zappers));
		int expensesPencePerSec = expensesPence / 60;
		int remainder = expensesPencePerSec > 0 ? (expensesPence % expensesPencePerSec) : 0;
		expenses.setTotalPence(expensesPence);
		expensesPerSecond.setTotalPence(expensesPencePerSec);
		expensesRemainder.setTotalPence(remainder);
	}

	void gameOver(boolean ranOutOfMoney) {
		if (gameState != GameState.Lost) {
			gameState = GameState.Lost;



			gameOverOverlay.setVisible(true);
		}
	}

	class OutrageButton extends TextButton {
		private final Money cost;
		private final float outrageReduction;

		public OutrageButton(String name, Skin skin, Money cost, float outrageReduction) {
			super(null, skin);
			this.cost = cost;
			this.outrageReduction = outrageReduction;

			setText(String.format("Build %1$s (%2$s)", name, cost));

			addListener(new ClickListener(Input.Buttons.LEFT) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					onClicked();
				}
			});
		}

		@Override
		public void act(float delta) {
			super.act(delta);
			setDisabled(money.isLessThan(cost));
		}

		private void onClicked() {
			if (!money.isLessThan(cost)) {
				// Build an orphanage!
				money.subtract(cost);
				outragePercent = MathUtils.clamp(outragePercent - outrageReduction, 0f, 100f);
			}
		}
	}
}
