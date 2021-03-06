package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.Gdx;
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

	private final TextureRegion diggingBackground, diggingForeground;
	private int diggers, diggerMonsters;
	private float diggingCounter;
	private float diggingDelay;
	private final TextureRegion[] bodyPartImages;
	private int bodyPartIndex;
	private final Group bodyPartsGroup;

	private final TextureRegion surgeryBackground, surgeryInUse;
	private int surgeons, surgeonMonsters;
	private float stitchingCounter;
	private float stitchingDelay;
	private final Label stitchingWaitingLabel;

	private final TextureRegion zappingBackground, zappingInUse;
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
	private final Money retireCost = new Money(10,0,0);

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
								monstersBackground;
	private final TextureRegion applicantImage;
	private int applicantCount;
	private float applicantCounter;
	private final TextureRegion monsterImage;
	private final Group sellOverlay;

	private boolean draggingApplicant;
	private boolean draggingMonster;
	private float dragX, dragY;

	private float outragePercent;
	private final TextureRegion	outrageBackground;
	private final TextureRegion[] protesters;

	enum GameState {
		Playing,
		Won,
		Lost,
	}
	private GameState gameState;
	private final Group gameWonOverlay,
						gameLostOverlay;
	private final Label lostReason;

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
		money = new Money(1, 0, 6);
		income = new Money(0, 0, 0);
		expenses = new Money(0, 0, 0);
		expensesPerSecond = new Money(0, 0, 0);
		expensesRemainder = new Money(0, 0, 0);
		moneyChangeCounter = 0;
		secondsCounter = 0;
		outragePercent = 0.10f;

		monsterImage = game.skin.getRegion("monster");
		applicantImage = game.skin.getRegion("applicant");

		monstersBackground = game.skin.getRegion("monster-closet");
		applicantBackground = game.skin.getRegion("waiting-room");
		outrageBackground = game.skin.getRegion("outrage-room");

		bodyPartCount = 0;
		bodyCount = 0;
		monsterCount = 1;
		applicantCount = 3 + 3;
		applicantCounter = 0;

		diggingBackground = game.skin.getRegion("graves");
		diggingForeground = game.skin.getRegion("graves-foreground");
		bodyPartImages = new TextureRegion[] {
			game.skin.getRegion("head"),
			game.skin.getRegion("hand"),
			game.skin.getRegion("leg"),
		};
		bodyPartIndex = 0;
		bodyPartsGroup = new Group();
		diggers = 0;
		diggerMonsters = 0;
		diggingCounter = 0;
		diggingDelay = 1f;
		diggingLabel = new Label("Body Parts: 0 / minute", game.skin);
		diggingLabel.setAlignment(Align.topLeft);
		addDigger(false);

		surgeryBackground = game.skin.getRegion("operating-theatre");
		surgeryInUse = game.skin.getRegion("operating-theatre-inuse");
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
		zappingInUse = game.skin.getRegion("zap-room-inuse");
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
		table.defaults().fill().width(250).height(200).padLeft(5).padRight(5).top();

		table.add(diggingLabel);
		table.add(stitchingLabel);
		table.add(zappingLabel).row();

		monstersLabel = new Label("Monsters", game.skin);
		monstersLabel.setAlignment(Align.topLeft);

		applicantsLabel = new Label("Job Applicants: 0", game.skin);
		applicantsLabel.setAlignment(Align.topLeft);

		outrageLabel = new Label("Public Outrage: 15%", game.skin);
		outrageLabel.setAlignment(Align.topLeft);
		protesters = new TextureRegion[] {
			game.skin.getRegion("protester"),
			game.skin.getRegion("protester2"),
			game.skin.getRegion("protester3"),
			game.skin.getRegion("protester4"),
			game.skin.getRegion("protester5"),
		};

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
//			statsTable.add(incomeLabel).row(); // Income is meaningless as selling is manual! Easier to just ignore it.
			statsTable.add(expensesLabel).row();

			statsTable.add(new RetireButton("Retire", game.skin, retireCost)).row();

			table.add(statsTable);
		}

		table.add();

		// Buttons
		{
			Table buttonsTable = new Table(game.skin);
			buttonsTable.defaults().fillX();

			buttonsTable.add("Public Relations:").row();
			buttonsTable.add(new OutrageButton("Sponsor an Artist", game.skin, new Money(0, 19, 6), 0.075f)).row();
			buttonsTable.add(new OutrageButton("Hold a Ball", game.skin, new Money(1, 5, 0), 0.15f)).row();
			buttonsTable.add(new OutrageButton("Build an Orphanage", game.skin, new Money(2, 10, 0), 0.30f)).row();
			buttonsTable.add(new OutrageButton("Build a Hospital", game.skin, new Money(3, 5, 0), 0.50f)).row();

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
			image.setPosition(0, 0);
			sellOverlay.addActor(image);

			Label sellLabel = new Label("SELL", game.skin, "title");
			sellLabel.setPosition(390, 125, Align.center);
			sellOverlay.addActor(sellLabel);

			Label costLabel = new Label("(+ " + sellMonsterAmount + ")", game.skin, "title");
			costLabel.setPosition(390, 75, Align.center);
			sellOverlay.addActor(costLabel);

			addActor(sellOverlay);
		}

		// Game Won Overlay
		{
			gameWonOverlay = new Group();
			Image image = new Image(game.skin.getRegion("lost-overlay"));
			image.setBounds(0, 0, 780, 600);
			gameWonOverlay.addActor(image);

			Label title = new Label("You Win!", game.skin, "title");
			title.setPosition(390, 500, Align.center);
			gameWonOverlay.addActor(title);

			String text = "Your impressive business skills have allowed you to retire\n" +
				"at age 30, to a peaceful island in the Caribbean.\n\n" +
				"I think congratulations are in order!\n\n" +
				"Meanwhile, your monstrous creations may or may not be\n" +
				"rampaging through London. Oops.";
			Label winDescription = new Label(text, game.skin, "titleItalic");
			winDescription.setPosition(390, 300, Align.center);
			winDescription.setAlignment(Align.center);
			gameWonOverlay.addActor(winDescription);

			TextButton menuButton = new TextButton("Return to Menu", game.skin);
			menuButton.addListener(new ClickListener(Input.Buttons.LEFT) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					PlayScene.this.game.showMenu();
				}
			});
			menuButton.setPosition(390, 70, Align.center);
			gameWonOverlay.addActor(menuButton);

			addActor(gameWonOverlay);
			gameWonOverlay.setVisible(false);
		}

		// Game Over Overlay
		{
			gameLostOverlay = new Group();
			Image image = new Image(game.skin.getRegion("lost-overlay"));
			image.setBounds(0, 0, 780, 600);
			gameLostOverlay.addActor(image);

			Label lostTitle = new Label("Game Over!", game.skin, "title");
			lostTitle.setPosition(390, 500, Align.center);
			gameLostOverlay.addActor(lostTitle);

			lostReason = new Label("Unfortunately, you have run out of money.", game.skin, "titleItalic");
			lostReason.setPosition(390, 400, Align.center);
			lostReason.setAlignment(Align.center);
			gameLostOverlay.addActor(lostReason);

			TextButton menuButton = new TextButton("Return to Menu", game.skin);
			menuButton.addListener(new ClickListener(Input.Buttons.LEFT) {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					PlayScene.this.game.showMenu();
				}
			});
			menuButton.setPosition(390, 200, Align.center);
			gameLostOverlay.addActor(menuButton);

			addActor(gameLostOverlay);
			gameLostOverlay.setVisible(false);
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// SUPER-DUPER CHEAT CODES!!!
		if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
			monsterCount++;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
			outragePercent += 0.1f;
		}

		// Digging
		diggingCounter += delta;
		while (diggingCounter >= diggingDelay) {
			diggingCounter -= diggingDelay;
			bodyPartCount++;
			outragePercent = MathUtils.clamp(outragePercent + 0.003f, 0f, 1f);

			// Animate a +1 body part
			int bodyPartImage = bodyPartIndex;
			bodyPartIndex = (bodyPartIndex + 1) % bodyPartImages.length;
			bodyPartsGroup.addActor(new BodyPart(bodyPartImages[bodyPartImage], 100, 400,
				MathUtils.random(-30f, 300f), MathUtils.random(300f, 400f), -98f, 2f,
				MathUtils.random(360f), MathUtils.random(-360f, 360f) ));
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
		if (applicantCount < 4) {
			applicantCounter += delta;
			float currentApplicationDelay = 10f + (outragePercent * 30f);
			if (applicantCounter > currentApplicationDelay) {
				applicantCounter -= 10f;
				applicantCount++;
			}
		}

		if (outragePercent >= 1f) {
			gameOver(false);
		}

		updateLabels();

		bodyPartsGroup.act(delta);
	}

	float rounded(float in) {
		return (float)(Math.round(in * 100.0) / 100.0);
	}

	void updateLabels() {
		float bodyPartsPerMinute = 60f / diggingDelay;
		float bodiesPerMinute = 60f / stitchingDelay;
		float monstersPerMinute = 60f / zappingDelay;

		diggingLabel.setText("Body Parts: " + bodyPartCount + " (" + rounded(bodyPartsPerMinute) + " / minute)");
		stitchingLabel.setText("Bodies: " + bodyCount + " (" + rounded(bodiesPerMinute) + " / minute)\nRequires 5 body parts");
		zappingLabel.setText("Monsters: " + monsterCount + " (" + rounded(monstersPerMinute) + " / minute)");

		monstersLabel.setText("Monsters: " + monsterCount + "\n(Click and drag to assign or sell)");
		applicantsLabel.setText("Applicants: " + applicantCount + "\n(Click and drag to assign)");

		incomeLabel.setText("Income: " + income + " / minute");
		expensesLabel.setText("Expenses: " + expenses + " / minute");
		fundsLabel.setText("Funds: " + money);

		outrageLabel.setText("Public Outrage: " + rounded(outragePercent * 100f) + "%");
	}

	@Override
	public void draw() {
		// Draw the departments!
		Batch batch = getBatch();
		batch.begin();

		// Digging
		batch.draw(diggingBackground, 0, 400);
		bodyPartsGroup.draw(batch, 1f);	// Flying body parts
		batch.draw(diggingForeground, 0, 400);

		batch.draw(surgeryBackground, 260, 400);
		if (bodyPartCount >= 5) {
			batch.draw(surgeryInUse, 260, 400);
		}

		batch.draw(zappingBackground, 520, 400);
		if (bodyCount >= 1) {
			batch.draw(zappingInUse, 520, 400);
		}

		// Monster storage
		{
			batch.draw(monstersBackground, 0, 200);
			int drawMonsters = monsterCount - (draggingMonster ? 1 : 0);
			float xDiff = 230f / (float) drawMonsters;
			for (int i = 0; i < drawMonsters; i++) {
				batch.draw(monsterImage, 5 + i * xDiff, 220);
			}
		}

		// Job applicants
		{
			batch.draw(applicantBackground, 260, 200);
			int drawApplicants = applicantCount - (draggingApplicant ? 1 : 0);
			for (int i = 0; i < drawApplicants; i++) {
				batch.draw(applicantImage, 260 + 10 + i * 61, 220);
			}
		}

		// Public Outrage
		{
			batch.draw(outrageBackground, 520, 200);
			int drawProtesters = MathUtils.round(outragePercent * 5f);
			float xDiff = 230f / drawProtesters;
			for (int i = 0; i < drawProtesters; i++) {
				batch.draw(protesters[i % protesters.length], 520 + i * xDiff, 220);
			}
		}

		sellOverlay.setVisible(draggingMonster);

		batch.end();

		super.draw();


		if (draggingMonster) {
			batch.begin();
			batch.draw(monsterImage, dragX - 25, dragY - 70);
			batch.end();
		}

		if (draggingApplicant) {
			batch.begin();
			batch.draw(applicantImage, dragX, dragY);
			batch.end();
		}
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
		outragePercent = MathUtils.clamp(outragePercent + 0.01f, 0f, 1f);
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
		if (gameState == GameState.Playing) {
			gameState = GameState.Lost;

			if (ranOutOfMoney) {
				lostReason.setText("Unfortunately, you have gone bankrupt.\n" +
					"Perhaps you could have kept a closer eye on your\n" +
					"finances and sold more monsters.");
			} else {
				lostReason.setText("The general public have become so outraged that\n" +
					"they have burned down your establishment.\n" +
					"Maybe you should keep a closer eye on public opinion.");
			}

			gameLostOverlay.setVisible(true);
		}
	}

	abstract class CostButton extends TextButton {
		protected final Money cost;

		public CostButton(String name, Skin skin, Money cost) {
			super(null, skin);
			this.cost = cost;

			setText(name + " (" + cost + ")");

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

		abstract void onClicked();
	}

	class OutrageButton extends CostButton {
		private final float outrageReduction;

		public OutrageButton(String name, Skin skin, Money cost, float outrageReduction) {
			super(name, skin, cost);
			this.outrageReduction = outrageReduction;
		}
		void onClicked() {
			if (!money.isLessThan(cost)) {
				// Build an orphanage!
				money.subtract(cost);
				outragePercent = MathUtils.clamp(outragePercent - outrageReduction, 0f, 1f);
			}
		}
	}

	class RetireButton extends CostButton {

		public RetireButton(String name, Skin skin, Money cost) {
			super(name, skin, cost);
		}

		@Override
		void onClicked() {
			if (!money.isLessThan(cost)) {
				// Retire!
				money.subtract(cost);
				gameState = GameState.Won;
				gameWonOverlay.setVisible(true);
			}
		}
	}
}
