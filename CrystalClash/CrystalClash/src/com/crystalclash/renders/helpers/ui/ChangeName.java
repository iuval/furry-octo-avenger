package com.crystalclash.renders.helpers.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.crystalclash.controllers.GameController;
import com.crystalclash.renders.helpers.ResourceHelper;

public class ChangeName extends Group {
	private Table table;
	private Label lblHeading;
	private TextField txtName;

	public ChangeName() {
		table = new Table();
		lblHeading = new Label("New name", new LabelStyle(ResourceHelper.getNormalBorderFont(), Color.WHITE));

		Skin textFieldSkin = new Skin();
		textFieldSkin.add("textFieldCursor", ResourceHelper.getTexture("menu/cursor_1"));

		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = ResourceHelper.getNormalBorderFont();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = textFieldSkin.getDrawable("textFieldCursor");

		TextureRegion textFieldTexture = ResourceHelper.getTexture("text_field_background");
		Image textFieldName = new Image(textFieldTexture);

		txtName = new TextField(GameController.getUser().getName(), textFieldStyle);

		table.setFillParent(true);
		table.row();
		table.add(lblHeading).expandX();
		table.row();
		Group textGroup = new Group();
		textGroup.addActor(textFieldName);
		textGroup.addActor(txtName);
		txtName.setSize(textFieldName.getWidth(), textFieldName.getHeight());
		textGroup.setSize(textFieldName.getWidth(), textFieldName.getHeight());
		table.add(textGroup).expandX();
		addActor(table);
	}

	public String getNewName() {
		return txtName.getText();
	}
}
