package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.MenuLogIn;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Enumerators.MenuLogInState;
import pruebas.Enumerators.StringWriting;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuLogInRender extends MenuRender {

	private static MenuLogInRender instance;
	private TweenManager tweenManager;

	private MenuLogIn controller;

	private TextureAtlas atlas;
	private Skin skin;
	private BitmapFont font;
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

	private Group group1;
	private Group group2;

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
	public void render(float dt, Stage stage) {
		stage.addActor(group1);
		stage.addActor(group2);
		group1.act(dt);
		group2.act(dt);
		tweenManager.update(dt);
	}

	@Override
	public void enterAnimation() {
		float speed = CrystalClash.ANIMATION_SPEED;

		Timeline.createParallel()
				.push(Tween.set(group1, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(group1, ActorAccessor.ALPHA, speed).target(1))
				.start(tweenManager);

		tweenManager.update(Float.MIN_VALUE);
	}

	@Override
	public void exitAnimation() {
		txtNick.setText("");
		txtNick.setMessageText("");
		txtEmail.setText("");
		txtEmail.setMessageText("");

		// controller.logIn();
		// float speed = CrystalClash.ANIMATION_SPEED;
		// Timeline.createParallel()
		// .push(Tween.to(group2, ActorAccessor.ALPHA, speed).target(0))
		// .setCallback(new TweenCallback() {
		// @Override
		// public void onEvent(int type, BaseTween<?> source) {
		// controller.logIn();
		// }
		// }).start(tweenManager);
	}

	private void loadStuff() {
		atlas = new TextureAtlas("data/Images/Buttons/buttons.pack");
		skin = new Skin(atlas);

		font = new BitmapFont(Gdx.files.internal("data/Fonts/font.fnt"), false);

		charactersTexture = new Texture(
				Gdx.files
						.internal("data/Images/Menu/menu_login_lobby_characters.png"));
		characters = new Image(charactersTexture);

		TextButtonStyle outerStyle = new TextButtonStyle();
		outerStyle.up = skin.getDrawable("outer_button_orange");
		outerStyle.down = skin.getDrawable("outer_button_orange_pressed");
		outerStyle.font = font;

		btnLogIn = new TextButton("Log In", outerStyle);
		btnLogIn.setPosition(CrystalClash.WIDTH / 4 * 3 - btnLogIn.getWidth()
				/ 2, CrystalClash.HEIGHT / 2 + 50);
		btnLogIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (state == MenuLogInState.Idle)
					moveDown(MenuLogInState.LogIn);
			}
		});

		btnSignIn = new TextButton("Sign In", outerStyle);
		btnSignIn.setPosition(CrystalClash.WIDTH / 4 * 3 - btnSignIn.getWidth()
				/ 2, CrystalClash.HEIGHT / 2 - 50 - btnSignIn.getHeight());
		btnSignIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (state == MenuLogInState.Idle)
					moveDown(MenuLogInState.SignIn);
			}
		});

		group1 = new Group();
		group1.addActor(characters);
		group1.addActor(btnLogIn);
		group1.addActor(btnSignIn);

		popupPanelTexture = new Texture(
				Gdx.files.internal("data/Images/Menu/menu_login_popup.png"));
		popupPanel = new Image(popupPanelTexture);
		popupPanel.setSize(800, 500);
		popupPanel.setPosition(0, 0);

		lblHeading = new Label("Welcome to Crystal Clash", new LabelStyle(font,
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
		textFieldStyle.font = font;
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = textFieldSkin.getDrawable("textFieldCursor");
		// TextFieldStyle textFieldStyle = new TextFieldStyle(font, Color.WHITE,
		// null, textFieldSkin.getDrawable("textFieldCursor"), null);
		txtEmail = new TextField("1@1.com", textFieldStyle);
		txtEmail.setMessageText("Enter your Email...");
		txtEmail.setMaxLength(30);
		txtEmail.setSize(700, 50);
		txtEmail.setPosition(textFieldEmail.getX() + 10, textFieldEmail.getY());
		txtEmail.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				adjustToKeyboard(true);
				Gdx.input.setOnscreenKeyboardVisible(true);
				stringWriting = StringWriting.Email;
			}
		});

		txtNick = new TextField("pepe", textFieldStyle);
		txtNick.setMessageText("Enter your Nick...");
		txtNick.setMaxLength(30);
		txtNick.setSize(700, 50);
		txtNick.setPosition(textFieldNick.getX() + 10, textFieldNick.getY());
		txtNick.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				adjustToKeyboard(true);
				Gdx.input.setOnscreenKeyboardVisible(true);
				stringWriting = StringWriting.Nick;
			}
		});

		TextButtonStyle style = new TextButtonStyle();
		style.up = skin.getDrawable("button_orange");
		style.down = skin.getDrawable("button_orange_pressed");
		style.font = font;

		btnBack = new TextButton("Back", style);
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

		btnConfirm = new TextButton("Confirm", style);
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
						controller.authenticate(email, nick);
					}
					break;
				case SignIn:
					if (!email.isEmpty() && !nick.isEmpty()) {
						GameEngine.showLoading();
						controller.signIn(email, nick);
					}
					break;
				default:
					break;
				}
			}
		});

		group2 = new Group();
		group2.addActor(popupPanel);
		group2.addActor(lblHeading);
		group2.addActor(textFieldEmail);
		group2.addActor(textFieldNick);
		group2.addActor(txtEmail);
		group2.addActor(txtNick);
		group2.addActor(btnConfirm);
		group2.addActor(btnBack);

		group2.setSize(popupPanel.getWidth(), popupPanel.getHeight());
		group2.setPosition(CrystalClash.WIDTH / 2 - group2.getWidth() / 2,
				(CrystalClash.HEIGHT / 2 - group2.getHeight() / 2)
						+ CrystalClash.HEIGHT);

		enterAnimation();
	}

	public void authenticateSuccess(String userId, String name) {
		exitAnimation();
	}

	public void authenticateError(String message) {
		System.out.println(message);
	}

	// Solo para el btnBack (Mueve el panel hacia arriba y hace un fade-in de
	// los otros botones
	private void moveUp(MenuLogInState state) {
		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(group2, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(group1, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(group2, ActorAccessor.Y, speed).target(
						group2.getY() + CrystalClash.HEIGHT))
				.start(tweenManager);

		this.state = state;
	}

	private void moveDown(MenuLogInState state) {
		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(group2, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - group2.getHeight() / 2))
				.push(Tween.to(group2, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(group1, ActorAccessor.ALPHA, speed).target(0))
				.start(tweenManager);

		this.state = state;
	}

	private void adjustToKeyboard(boolean up) {
		// true mueve hacia arriba, false mueve hacia abajo
		float jump = CrystalClash.HEIGHT / 2 - group2.getHeight() / 2;
		if (up)
			jump = CrystalClash.HEIGHT / 2 - 30;

		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(group2, ActorAccessor.Y, speed).target(jump))
				.start(tweenManager);
	}

	public void dispose() {
		popupPanelTexture.dispose();
		atlas.dispose();
		skin.dispose();
		font.dispose();
	}

	public void reset() {
		instance = new MenuLogInRender(controller);

		// TODO: Hay que setear de nuevo el InputController porque sino no puedo
		// escribir

		// No se porque haciendo esto se caga :S
		// enterAnimation();
		//
		// txtEmail.setText("iuvalgoldansky@gmail.com");
		// txtEmail.setMessageText("Enter your Email...");
		// txtNick.setText("pepe");
		// txtNick.setMessageText("Enter your Nick...");
		//
		// group2.setY((CrystalClash.HEIGHT / 2 - group2.getHeight() / 2)
		// + CrystalClash.HEIGHT);
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
			Gdx.input.setOnscreenKeyboardVisible(false);
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
		Gdx.input.setOnscreenKeyboardVisible(false);
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
