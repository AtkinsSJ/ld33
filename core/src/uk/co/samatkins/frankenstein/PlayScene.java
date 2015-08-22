package uk.co.samatkins.frankenstein;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PlayScene extends Scene {

	private float diggingCounter;
	private float diggingDelay;

	private float stitchingCounter;
	private float stitchingDelay;

	private float zappingCounter;
	private float zappingDelay;

	private int bodyPartCount;
	private int bodyCount;
	private int monsterCount;
	private final Table table;
	private final Label diggingLabel;
	private final Label stitchingLabel;
	private final Label zappingLabel;
	private final Label monstersLabel;
	private final Label salesLabel;
	private final Label outrageLabel;
	private final Label fundsLabel;
	private final Label incomeLabel;
	private final Label expensesLabel;

	PlayScene(FrankGame game) {
		super(game);

		bodyPartCount = 0;
		bodyCount = 0;
		monsterCount = 0;

		diggingCounter = 0;
		diggingDelay = 1f;
		diggingLabel = new Label("Body Parts: 10 / minute", game.skin);

		stitchingCounter = 0;
		stitchingDelay = 5f;
		stitchingLabel = new Label("Bodies: 1 / minute", game.skin);

		zappingCounter = 0;
		zappingDelay = 5f;
		zappingLabel = new Label("Monsters: 1 / minute", game.skin);

		table = new Table(game.skin);
		table.setFillParent(true);
		addActor(table);
		table.defaults().fill().expand().top();

		table.add(diggingLabel);
		table.add(stitchingLabel);
		table.add(zappingLabel).row();

		monstersLabel = new Label("Monsters", game.skin);
		salesLabel = new Label("This space for rent", game.skin);
		outrageLabel = new Label("Public Outrage: 15%", game.skin);

		table.add(monstersLabel);
		table.add(salesLabel);
		table.add(outrageLabel).row();

		Table statsTable = new Table(game.skin);
		fundsLabel = new Label("Funds: £22.37", game.skin);
		incomeLabel = new Label("Income: £0.35 / minute", game.skin);
		expensesLabel = new Label("Expenses: £0.20 / minute", game.skin);

		statsTable.add(fundsLabel).row();
		statsTable.add(incomeLabel).row();
		statsTable.add(expensesLabel).row();
		table.add(statsTable).colspan(3);

		updateLabels();
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
}
