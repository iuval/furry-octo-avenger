package com.crystalclash.renders.helpers.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.renders.helpers.EmblemHelper;

public class EmblemListItem extends Group {
	private int emblemNumber;
	private Image imgEmblem;
	private Image imgSelectedBorder;

	public EmblemListItem(int number, ClickListener clickListener) {
		emblemNumber = number;

		imgEmblem = new Image(EmblemHelper.getEmblem(number));
		imgEmblem.setPosition(0, 0);
		imgEmblem.setSize(100, 100);
		addActor(imgEmblem);

		imgSelectedBorder = new Image(EmblemHelper.getSelectedBorder());
		imgSelectedBorder.setPosition(0, 0);

		imgEmblem.addListener(clickListener);
		imgSelectedBorder.setTouchable(Touchable.disabled);
	}

	public void select() {
		addActor(imgSelectedBorder);
	}

	public void desselect() {
		imgSelectedBorder.remove();
	}

	public int getEmblemNumber() {
		return emblemNumber;
	}
}
