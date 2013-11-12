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
import com.crystalclash.enumerators.MenuLogInState;
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
	private TextButton btnSignIn;
	private TextButton btnConfirm;
	private TextButton btnBack;
	private TextureRegion popupPanelTexture;
	private Image popupPanel;
	private TextureRegion textFieldTexture;
	private Image textFieldEmail;
	private Image textFieldPassword;
	private TextureRegion charactersTexture;
	private Image characters;

	private Group groupInitialScreen;
	private Group popUp;

	private StringWriting stringWriting;
	public MenuLogInState state;

	public MenuLogInView(MenuLogIn menu) {
		this.controller = menu;
		stringWriting = StringWriting.None;
		state = MenuLogInState.Idle;
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
		state = MenuLogInState.Idle;
		return t.push(Tween.set(groupInitialScreen, ActorAccessor.ALPHA)
				.target(0))
				.push(Tween.to(groupInitialScreen, ActorAccessor.ALPHA, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(1));
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		txtPassword.setText("");
		txtPassword.setMessageText("");

		return t.push(Tween.to(popUp, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(CrystalClash.HEIGHT));
	}

	private void load() {
		final Profile prof = GameController.profileService.retrieveProfile();

		charactersTexture = ResourceHelper.getTexture("menu/menu_login_lobby_characters");
		characters = new Image(charactersTexture);

		btnLogIn = new TextButton("Log In", ResourceHelper.getOuterButtonStyle());
		btnLogIn.setPosition(CrystalClash.WIDTH / 4 * 3 - btnLogIn.getWidth()
				/ 2, CrystalClash.HEIGHT / 2 + 50);
		btnLogIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (state == MenuLogInState.Idle)
					moveDown(MenuLogInState.LogIn);
			}
		});

		btnSignIn = new TextButton("Sign In", ResourceHelper.getOuterButtonStyle());
		btnSignIn.setPosition(CrystalClash.WIDTH / 4 * 3 - btnSignIn.getWidth()
				/ 2, CrystalClash.HEIGHT / 2 - 50 - btnSignIn.getHeight());
		btnSignIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (state == MenuLogInState.Idle)
					moveDown(MenuLogInState.SignIn);
			}
		});

		groupInitialScreen = new Group();
		groupInitialScreen.addActor(characters);
		groupInitialScreen.addActor(btnLogIn);
		groupInitialScreen.addActor(btnSignIn);
		addActor(groupInitialScreen);

		popupPanelTexture = ResourceHelper.getTexture("menu/menu_login_popup");
		popupPanel = new Image(popupPanelTexture);
		popupPanel.setSize(800, 500);
		popupPanel.setPosition(0, 0);

		lblHeading = new Label("Welcome to Crystal Clash", new LabelStyle(ResourceHelper.getBigFont(),
				Color.WHITE));
		lblHeading.setPosition(
				popupPanel.getWidth() / 2 - lblHeading.getWidth() / 2,
				popupPanel.getTop() - 100);

		textFieldTexture = ResourceHelper.getTexture("text_field_background");
		textFieldEmail = new Image(textFieldTexture);
		textFieldEmail.setPosition(50, popupPanel.getTop() - 200);
		textFieldEmail.setSize(700, 50);

		textFieldPassword = new Image(textFieldTexture);
		textFieldPassword.setPosition(50, popupPanel.getTop() - 300);
		textFieldPassword.setSize(700, 50);

		Skin textFieldSkin = new Skin();
		textFieldSkin.add("textFieldCursor", ResourceHelper.getTexture("menu/cursor_1"));

		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = ResourceHelper.getBigFont();
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

		btnBack = new TextButton("Back", ResourceHelper.getButtonStyle());
		btnBack.setPosition(50, 50);
		btnBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.setOnscreenKeyboardVisible(false);
				moveUp(MenuLogInState.Idle);
				txtEmail.setText("");
				txtPassword.setText("");
			}
		});

		btnConfirm = new TextButton("Confirm", ResourceHelper.getButtonStyle());
		btnConfirm.setPosition(popupPanel.getWidth() - btnBack.getWidth() - 50,
				50);
		btnConfirm.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String email = txtEmail.getText().trim();
				String password = txtPassword.getText().trim();
				switch (state) {
				case Idle:
					break;
				case LogIn:
					if (!email.isEmpty() && !password.isEmpty()) {
						Gdx.input.setOnscreenKeyboardVisible(false);
						controller.sendLogIn(email, password);
					}
					break;
				case SignIn:
					if (!email.isEmpty() && !password.isEmpty()) {
						Gdx.input.setOnscreenKeyboardVisible(false);
						controller.sendSignIn(email, password);
					}
					break;
				default:
					break;
				}
			}
		});

		popUp = new Group();
		popUp.addActor(popupPanel);
		popUp.addActor(lblHeading);
		popUp.addActor(textFieldEmail);
		popUp.addActor(textFieldPassword);
		popUp.addActor(txtEmail);
		popUp.addActor(txtPassword);
		popUp.addActor(btnConfirm);
		popUp.addActor(btnBack);

		popUp.setSize(popupPanel.getWidth(), popupPanel.getHeight());
		popUp.setPosition(CrystalClash.WIDTH / 2 - popUp.getWidth() / 2,
				(CrystalClash.HEIGHT / 2 - popUp.getHeight() / 2)
						+ CrystalClash.HEIGHT);
		addActor(popUp);
	}

	public void logInError(String message) {
		MessageBox.build()
				.setText(message)
				.setCallback(null)
				.show();
	}

	// Solo para el btnBack (Mueve el panel hacia arriba y hace un fade-in de
	// los otros botones
	private void moveUp(MenuLogInState state) {
		float speed = CrystalClash.SLOW_ANIMATION_SPEED;
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(popUp, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(groupInitialScreen, ActorAccessor.ALPHA,
						speed).target(1))
				.push(Tween.to(popUp, ActorAccessor.Y, speed).target(
						popUp.getY() + CrystalClash.HEIGHT)));

		this.state = state;
	}

	private void moveDown(MenuLogInState state) {
		float speed = CrystalClash.SLOW_ANIMATION_SPEED;
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(popUp, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popUp.getHeight() / 2))
				.push(Tween.to(popUp, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(groupInitialScreen, ActorAccessor.ALPHA,
						speed).target(0)));

		this.state = state;
	}

	private void adjustToKeyboard(boolean up) {
		Gdx.input.setOnscreenKeyboardVisible(up);
		// true mueve hacia arriba, false mueve hacia abajo
		float jump = CrystalClash.HEIGHT / 2 - popUp.getHeight() / 2;
		if (up)
			jump = CrystalClash.HEIGHT / 2 - 30;

		float speed = CrystalClash.SLOW_ANIMATION_SPEED;
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(popUp, ActorAccessor.Y, speed).target(jump)));
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
