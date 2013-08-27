package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.MenuLogIn;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Enumerators.MenuLogInState;
import pruebas.Enumerators.StringWriting;
import pruebas.Renders.helpers.UIHelper;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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

public class MenuLogInRender extends MenuRender {

	private static MenuLogInRender instance;
	private TweenManager tweenManager;

	private MenuLogIn controller;

	private Label lblHeading;
	private TextField txtEmail;
	private TextField txtNick;
	private TextButton btnLogIn;
	private TextButton btnSignIn;
	private TextButton btnConfirm;
	private TextButton btnBack;
	private Texture popupPanelTexture;
	private Image popupPanel;
	private Texture textFieldTexture;
	private Image textFieldEmail;
	private Image textFieldNick;
	private Texture charactersTexture;
	private Image characters;

	private Group groupInitialScreen;
	private Group popUp;

	private StringWriting stringWriting;
	public MenuLogInState state;

	public MenuLogInRender(MenuLogIn menu) {
		this.controller = menu;
		tweenManager = new TweenManager();
		stringWriting = StringWriting.None;
		state = MenuLogInState.Idle;

		loadStuff();
	}

	public static MenuLogInRender getInstance(MenuLogIn menu) {
		if (instance == null)
			instance = new MenuLogInRender(menu);

		return instance;
	}

	@Override
	public void act(float delta) {
		tweenManager.update(delta);
		super.act(delta);
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		state = MenuLogInState.Idle;
		return t.push(Tween.set(groupInitialScreen, ActorAccessor.ALPHA)
				.target(0))
				.push(Tween.to(groupInitialScreen, ActorAccessor.ALPHA, CrystalClash.ANIMATION_SPEED)
						.target(1));
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		txtNick.setText("");
		txtNick.setMessageText("");

		return t.push(Tween.to(popUp, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(CrystalClash.HEIGHT));
	}

	private void loadStuff() {
		charactersTexture = new Texture(
				Gdx.files
						.internal("data/Images/Menu/menu_login_lobby_characters.png"));
		characters = new Image(charactersTexture);

		btnLogIn = new TextButton("Log In", UIHelper.getOuterButtonStyle());
		btnLogIn.setPosition(CrystalClash.WIDTH / 4 * 3 - btnLogIn.getWidth()
				/ 2, CrystalClash.HEIGHT / 2 + 50);
		btnLogIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (state == MenuLogInState.Idle)
					moveDown(MenuLogInState.LogIn);
			}
		});

		btnSignIn = new TextButton("Sign In", UIHelper.getOuterButtonStyle());
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

		popupPanelTexture = new Texture(
				Gdx.files.internal("data/Images/Menu/menu_login_popup.png"));
		popupPanel = new Image(popupPanelTexture);
		popupPanel.setSize(800, 500);
		popupPanel.setPosition(0, 0);

		lblHeading = new Label("Welcome to Crystal Clash", new LabelStyle(UIHelper.getFont(),
				Color.WHITE));
		lblHeading.setPosition(
				popupPanel.getWidth() / 2 - lblHeading.getWidth() / 2,
				popupPanel.getTop() - 100);

		textFieldTexture = new Texture(
				Gdx.files.internal("data/Images/text_field_background.png"));
		textFieldEmail = new Image(textFieldTexture);
		textFieldEmail.setPosition(50, popupPanel.getTop() - 200);
		textFieldEmail.setSize(700, 50);

		textFieldNick = new Image(textFieldTexture);
		textFieldNick.setPosition(50, popupPanel.getTop() - 300);
		textFieldNick.setSize(700, 50);

		Skin textFieldSkin = new Skin();
		textFieldSkin
				.add("textFieldCursor",
						new Texture(Gdx.files
								.internal("data/Images/Menu/cursor_1.png")));

		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = UIHelper.getFont();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = textFieldSkin.getDrawable("textFieldCursor");
		txtEmail = new TextField("", textFieldStyle);
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

		txtNick = new TextField("", textFieldStyle);
		txtNick.setMessageText("Enter your User Name...");
		txtNick.setMaxLength(30);
		txtNick.setSize(700, 50);
		txtNick.setPosition(textFieldNick.getX() + 10, textFieldNick.getY());
		txtNick.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				adjustToKeyboard(true);
				stringWriting = StringWriting.Nick;
			}
		});

		btnBack = new TextButton("Back", UIHelper.getButtonStyle());
		btnBack.setPosition(50, 50);
		btnBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.setOnscreenKeyboardVisible(false);
				moveUp(MenuLogInState.Idle);
				txtEmail.setText("");
				txtNick.setText("");
			}
		});

		btnConfirm = new TextButton("Confirm", UIHelper.getButtonStyle());
		btnConfirm.setPosition(popupPanel.getWidth() - btnBack.getWidth() - 50,
				50);
		btnConfirm.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String email = txtEmail.getText().trim();
				String nick = txtNick.getText().trim();
				switch (state) {
				case Idle:
					break;
				case LogIn:
					if (!email.isEmpty() && !nick.isEmpty()) {
						GameEngine.showLoading();
						Gdx.input.setOnscreenKeyboardVisible(false);
						controller.sendLogIn(email, nick);
					}
					break;
				case SignIn:
					if (!email.isEmpty() && !nick.isEmpty()) {
						GameEngine.showLoading();
						Gdx.input.setOnscreenKeyboardVisible(false);
						controller.sendSignIn(email, nick);
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
		popUp.addActor(textFieldNick);
		popUp.addActor(txtEmail);
		popUp.addActor(txtNick);
		popUp.addActor(btnConfirm);
		popUp.addActor(btnBack);

		popUp.setSize(popupPanel.getWidth(), popupPanel.getHeight());
		popUp.setPosition(CrystalClash.WIDTH / 2 - popUp.getWidth() / 2,
				(CrystalClash.HEIGHT / 2 - popUp.getHeight() / 2)
						+ CrystalClash.HEIGHT);
		addActor(popUp);
	}

	public void authenticateError(String message) {
		System.out.println(message);
	}

	// Solo para el btnBack (Mueve el panel hacia arriba y hace un fade-in de
	// los otros botones
	private void moveUp(MenuLogInState state) {
		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(popUp, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(groupInitialScreen, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(popUp, ActorAccessor.Y, speed).target(
						popUp.getY() + CrystalClash.HEIGHT))
				.start(tweenManager);

		this.state = state;
	}

	private void moveDown(MenuLogInState state) {
		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(popUp, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popUp.getHeight() / 2))
				.push(Tween.to(popUp, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(groupInitialScreen, ActorAccessor.ALPHA, speed).target(0))
				.start(tweenManager);

		this.state = state;
	}

	private void adjustToKeyboard(boolean up) {
		Gdx.input.setOnscreenKeyboardVisible(up);
		// true mueve hacia arriba, false mueve hacia abajo
		float jump = CrystalClash.HEIGHT / 2 - popUp.getHeight() / 2;
		if (up)
			jump = CrystalClash.HEIGHT / 2 - 30;

		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(popUp, ActorAccessor.Y, speed).target(jump))
				.start(tweenManager);
	}

	public void dispose() {
		popupPanelTexture.dispose();
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
			case Nick:
				aux = txtNick.getText();
				if (aux.length() > 0) {
					txtNick.setText(aux.substring(0, aux.length() - 1));
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
		return false;
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
		case Nick:
			aux = txtNick.getText();
			if (aux.length() < 30) {
				txtNick.setText(txtNick.getText() + character);
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
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
