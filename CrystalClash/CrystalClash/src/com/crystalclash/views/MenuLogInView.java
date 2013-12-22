package com.crystalclash.views;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.MenuLogIn;
import com.crystalclash.enumerators.StringWriting;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.util.Profile;

public class MenuLogInView extends InputView {

	private static MenuLogInView instance;

	private MenuLogIn controller;

	private Label lblHeading;
	private TextField txtEmail;
	private TextField txtPassword;
	private TextButton btnLogIn;
	private TextButton btnSignUp;
	private TextureRegion txtPopup;
	private Image imgPopupPanel;
	private TextureRegion textFieldTexture;
	private Image textFieldEmail;
	private Image textFieldPassword;
	private Group groupInitialScreen;
	private Group grpPopUp;

	private StringWriting stringWriting;

	public MenuLogInView(MenuLogIn menu) {
		this.controller = menu;
		stringWriting = StringWriting.None;
		setPosition(0, 853);
		load();
	}

	public static MenuLogInView getInstance(MenuLogIn menu) {
		if (instance == null)
			instance = new MenuLogInView(menu);

		return instance;
	}

	@Override
	public void init() {
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		return t.push(Tween.to(grpPopUp, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(213));
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t;
	}

	private void load() {
		final Profile prof = GameController.profileService.retrieveProfile();

		groupInitialScreen = new Group();
		addActor(groupInitialScreen);

		txtPopup = ResourceHelper.getTexture("menu/menu_login_popup");
		imgPopupPanel = new Image(txtPopup);
		imgPopupPanel.setSize(800, 500);
		imgPopupPanel.setPosition(0, 0);

		lblHeading = new Label("Welcome to Crystal Clash", new LabelStyle(ResourceHelper.getNormalBorderFont(),
				Color.WHITE));
		lblHeading.setPosition(
				imgPopupPanel.getWidth() / 2 - lblHeading.getWidth() / 2,
				imgPopupPanel.getTop() - 100);

		textFieldTexture = ResourceHelper.getTexture("text_field_background");
		textFieldEmail = new Image(textFieldTexture);
		textFieldEmail.setPosition(50, imgPopupPanel.getTop() - 200);
		textFieldEmail.setSize(700, 50);

		textFieldPassword = new Image(textFieldTexture);
		textFieldPassword.setPosition(50, imgPopupPanel.getTop() - 300);
		textFieldPassword.setSize(700, 50);

		Skin textFieldSkin = new Skin();
		textFieldSkin.add("textFieldCursor", ResourceHelper.getTexture("menu/cursor_1"));

		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = ResourceHelper.getNormalBorderFont();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = textFieldSkin.getDrawable("textFieldCursor");
		txtEmail = new TextField(prof.getUserEmail(), textFieldStyle);
		txtEmail.setMessageText("Enter your Email...");
		txtEmail.setMaxLength(30);
		txtEmail.setSize(700, 50);
		txtEmail.setPosition(textFieldEmail.getX() + 10, textFieldEmail.getY());
		txtEmail.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				adjustToKeyboard(true);
				stringWriting = StringWriting.Email;
			}
		});

		txtPassword = new TextField(prof.getUserPassword(), textFieldStyle);
		txtPassword.setMessageText("Enter your User Name...");
		txtPassword.setMaxLength(30);
		txtPassword.setSize(700, 50);
		txtPassword.setPosition(textFieldPassword.getX() + 10, textFieldPassword.getY());
		txtPassword.setPasswordCharacter('*');
		txtPassword.setPasswordMode(true);
		txtPassword.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				adjustToKeyboard(true);
				stringWriting = StringWriting.Password;
			}
		});

		btnSignUp = new TextButton("Sign Up", ResourceHelper.getButtonStyle());
		btnSignUp.setPosition(50, 50);
		btnSignUp.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String email = txtEmail.getText().trim();
				String password = txtPassword.getText().trim();
				Gdx.input.setOnscreenKeyboardVisible(false);
				if (!email.isEmpty() && !password.isEmpty()) {
					Gdx.input.setOnscreenKeyboardVisible(false);
					controller.sendSignUp(email, password);
				}
			}
		});

		btnLogIn = new TextButton("Log In", ResourceHelper.getButtonStyle());
		btnLogIn.setPosition(imgPopupPanel.getWidth() - btnSignUp.getWidth() - 50, 50);
		btnLogIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String email = txtEmail.getText().trim();
				String password = txtPassword.getText().trim();
				Gdx.input.setOnscreenKeyboardVisible(false);
				if (!email.isEmpty() && !password.isEmpty()) {
					Gdx.input.setOnscreenKeyboardVisible(false);
					controller.sendLogIn(email, password);
				}
			}
		});

		grpPopUp = new Group();
		grpPopUp.addActor(imgPopupPanel);
		grpPopUp.addActor(lblHeading);
		grpPopUp.addActor(textFieldEmail);
		grpPopUp.addActor(textFieldPassword);
		grpPopUp.addActor(txtEmail);
		grpPopUp.addActor(txtPassword);
		grpPopUp.addActor(btnLogIn);
		grpPopUp.addActor(btnSignUp);

		grpPopUp.setSize(imgPopupPanel.getWidth(), imgPopupPanel.getHeight());
		grpPopUp.setPosition(CrystalClash.WIDTH / 2 - grpPopUp.getWidth() / 2,
				(CrystalClash.HEIGHT + grpPopUp.getHeight()));
		addActor(grpPopUp);
	}

	public void logInError(String message) {
		MessageBox.build()
				.setText(message)
				.setCallback(null)
				.show();
	}

	private void adjustToKeyboard(boolean up) {
		Gdx.input.setOnscreenKeyboardVisible(up);
		// true mueve hacia arriba, false mueve hacia abajo
		float jump = 0;
		if (up)
			jump = 413;
		else
			jump = 213;

		float speed = CrystalClash.SLOW_ANIMATION_SPEED;
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(grpPopUp, ActorAccessor.Y, speed).target(jump)));
	}

	@Override
	public void dispose() {
	}

	// INPUT PROCESSOR--------------------------------------------
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACKSPACE) {
			String aux = "";
			switch (stringWriting) {
			case Email:
				aux = txtEmail.getText();
				if (aux.length() > 0) {
					txtEmail.setText(aux.substring(0, aux.length() - 1));
				}
				break;
			case Password:
				aux = txtPassword.getText();
				if (aux.length() > 0) {
					txtPassword.setText(aux.substring(0, aux.length() - 1));
				}
				break;
			case None:
				break;
			default:
				break;
			}
		}

		if (keycode == Keys.BACK) {
			adjustToKeyboard(false);
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		String aux = "";
		switch (stringWriting) {
		case Email:
			aux = txtEmail.getText();
			if (aux.length() < 30) {
				txtEmail.setText(txtEmail.getText() + character);
			}
			break;
		case Password:
			aux = txtPassword.getText();
			if (aux.length() < 30) {
				txtPassword.setText(txtPassword.getText() + character);
			}
			break;
		case None:
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		adjustToKeyboard(false);
		return true;
	}

	@Override
	public void shown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closed() {
		// TODO Auto-generated method stub

	}
}
