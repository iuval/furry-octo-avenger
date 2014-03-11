package com.crystalclash.renders.helpers.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.controllers.GameController;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.util.I18n;

public class UpdateProfile extends Group {
	private Table table;
	private Table emblemTable;
	private ScrollPane scrollPane;
	private EmblemListItem selectedItem;
	private Label lblEmail;
	private TextField txtEmail;
	private Label lblPassword;
	private TextField txtPassword;

	public UpdateProfile() {
		table = new Table();
		table.defaults().padLeft(3).padTop(3);
		table.setFillParent(true);

		lblEmail = new Label(I18n.t("profile_email"), new LabelStyle(ResourceHelper.getNormalBorderFont(), Color.WHITE));
		lblPassword = new Label(I18n.t("profile_password"), new LabelStyle(ResourceHelper.getNormalBorderFont(), Color.WHITE));

		Skin textFieldSkin = new Skin();
		textFieldSkin.add("textFieldCursor", ResourceHelper.getTexture("menu/cursor_1"));

		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = ResourceHelper.getNormalBorderFont();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = textFieldSkin.getDrawable("textFieldCursor");

		TextureRegion txrFieldTexture = ResourceHelper.getTexture("text_field_background");

		txtEmail = new TextField(GameController.getUser().getEmail(), textFieldStyle);
		txtEmail.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.setOnscreenKeyboardVisible(true);
			}
		});

		txtPassword = new TextField("", textFieldStyle);
		txtPassword.setMaxLength(16);
		txtPassword.setPasswordCharacter('*');
		txtPassword.setPasswordMode(true);
		txtPassword.setMessageText(I18n.t("profile_password_placeholder"));
		txtPassword.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.setOnscreenKeyboardVisible(true);
			}
		});

		table.row();
		table.add(lblEmail).spaceRight(10f);
		Group textEmailGroup = new Group();
		Image imgEmailField = new Image(txrFieldTexture);
		textEmailGroup.addActor(imgEmailField);
		textEmailGroup.addActor(txtEmail);
		table.add(textEmailGroup).expandX().fill();
		txtEmail.setWidth(620);
		txtEmail.setMaxLength(30);
		imgEmailField.setFillParent(true);

		table.row();
		table.add(lblPassword).spaceRight(10f);
		Group textPassGroup = new Group();
		Image imgPassField = new Image(txrFieldTexture);
		textPassGroup.addActor(imgPassField);
		textPassGroup.addActor(txtPassword);
		table.add(textPassGroup).expandX().fill();
		txtPassword.setWidth(620);
		txtPassword.setMaxLength(30);
		imgPassField.setFillParent(true);

		emblemTable = new Table();
		scrollPane = new ScrollPane(emblemTable);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setOverscroll(false, true);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setForceScroll(false, true);
		scrollPane.invalidate();

		emblemTable.align(Align.top | Align.left);
		emblemTable.defaults().width(100).height(100).padLeft(3).padTop(3);

		ClickListener unitItemClickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				EmblemListItem item = (EmblemListItem) event.getListenerActor().getParent();
				if (item != null) {
					if (item != selectedItem) {
						if (selectedItem != null)
							selectedItem.desselect();
						item.select();
						selectedItem = item;
					}
				}
			}
		};

		for (int i = 0; i < GameController.EMBLEM_COUNT; i++) {
			if (i % 7 == 0) {
				emblemTable.row();
			}
			EmblemListItem item = new EmblemListItem(i, unitItemClickListener);
			if (i == GameController.getUser().getEmblem()) {
				item.select();
				selectedItem = item;
			}
			emblemTable.add(item);
		}

		table.row();
		table.add(scrollPane).colspan(2).expand().fill();
		addActor(table);
	}

	public int getSelectedEmblem() {
		return selectedItem.getEmblemNumber();
	}

	public String getNewEmail() {
		return txtEmail.getText();
	}

	public String getNewPass() {
		return txtPassword.getText();
	}
}
