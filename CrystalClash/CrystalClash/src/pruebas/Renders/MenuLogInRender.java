package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.MenuLogIn;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Enumerators.MenuLogInState;
import pruebas.Enumerators.StringWriting;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

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
	private TextButton btnSingIn;
	private TextButton btnConfirm;
	private TextButton btnBack;
	private Texture popupPanelTexture;
	private Image popupPanel;
	private Texture textFieldTexture;
	private Image textFieldLogIn;
	private Image textFieldSingIn;
	private Texture charactersTexture;
	private Image characters;

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
		stage.addActor(characters);
		stage.addActor(btnLogIn);
		stage.addActor(btnSingIn);
		stage.addActor(popupPanel);
		stage.addActor(lblHeading);
		stage.addActor(textFieldLogIn);
		stage.addActor(textFieldSingIn);
		stage.addActor(txtEmail);
		stage.addActor(txtNick);
		stage.addActor(btnConfirm);
		stage.addActor(btnBack);
		tweenManager.update(dt);
	}

	@Override
	public void enterAnimation() {
		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.set(characters, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(btnLogIn, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(btnSingIn, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(characters, ActorAccessor.ALPHA, speed)
						.target(1))
				.push(Tween.to(btnLogIn, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(btnSingIn, ActorAccessor.ALPHA, speed).target(1))
				.start(tweenManager);

		tweenManager.update(Float.MIN_VALUE);
	}

	@Override
	public void exitAnimation() {
		txtNick.setText("");
		txtNick.setMessageText("");
		txtEmail.setText("");
		txtEmail.setMessageText("");

		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(popupPanel, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(lblHeading, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(textFieldLogIn, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(textFieldSingIn, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(txtEmail, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(txtNick, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(btnConfirm, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(btnBack, ActorAccessor.ALPHA, speed).target(0))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						controller.logIn();
					}
				}).start(tweenManager);
	}

	private void loadStuff() {
		atlas = new TextureAtlas("data/Buttons/buttons.pack");
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

		btnSingIn = new TextButton("Sing In", outerStyle);
		btnSingIn.setPosition(CrystalClash.WIDTH / 4 * 3 - btnSingIn.getWidth()
				/ 2, CrystalClash.HEIGHT / 2 - 50 - btnSingIn.getHeight());
		btnSingIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (state == MenuLogInState.Idle)
					moveDown(MenuLogInState.SingIn);
			}
		});

		popupPanelTexture = new Texture(
				Gdx.files.internal("data/Images/Menu/menu_login_popup.png"));
		popupPanel = new Image(popupPanelTexture);
		popupPanel.setSize(800, 500);
		popupPanel.setPosition(CrystalClash.WIDTH / 2 - popupPanel.getWidth()
				/ 2, (CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2)
				+ CrystalClash.HEIGHT);

		lblHeading = new Label("Welcome to Crystal Clash", new LabelStyle(font,
				Color.WHITE));
		lblHeading.setPosition(popupPanel.getX() + popupPanel.getWidth() / 2
				- lblHeading.getWidth() / 2, popupPanel.getTop() - 100);

		textFieldTexture = new Texture(
				Gdx.files.internal("data/Images/text_field_background.png"));
		textFieldLogIn = new Image(textFieldTexture);
		textFieldLogIn.setPosition(popupPanel.getX() + 50,
				popupPanel.getTop() - 200);
		textFieldLogIn.setSize(700, 50);
		textFieldSingIn = new Image(textFieldTexture);
		textFieldSingIn.setPosition(popupPanel.getX() + 50,
				popupPanel.getTop() - 300);
		textFieldSingIn.setSize(700, 50);

		Skin textFieldSkin = new Skin();
		textFieldSkin
				.add("textFieldCursor",
						new Texture(Gdx.files.internal("data/Images/Menu/cursor_1.png")));

		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = font;
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = textFieldSkin.getDrawable("textFieldCursor");
		// TextFieldStyle textFieldStyle = new TextFieldStyle(font, Color.WHITE,
		// null, null, null);
		txtEmail = new TextField("", textFieldStyle);
		txtEmail.setMessageText("Enter your Email...");
		txtEmail.setMaxLength(30);
		txtEmail.setSize(700, 50);
		txtEmail.setPosition(textFieldLogIn.getX() + 10, textFieldLogIn.getY());
		txtEmail.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				txtEmail.getOnscreenKeyboard().show(true);
				adjustToKeyboard(true);
				stringWriting = StringWriting.Email;
			}
		});

		txtNick = new TextField("", textFieldStyle);
		txtNick.setMessageText("Enter your Nick...");
		txtNick.setMaxLength(30);
		txtNick.setSize(700, 50);
		txtNick.setPosition(textFieldSingIn.getX() + 10, textFieldSingIn.getY());
		txtNick.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				txtNick.getOnscreenKeyboard().show(true);
				adjustToKeyboard(true);
				stringWriting = StringWriting.Nick;
			}
		});

		TextButtonStyle style = new TextButtonStyle();
		style.up = skin.getDrawable("button_orange");
		style.down = skin.getDrawable("button_orange_pressed");
		style.font = font;

		btnConfirm = new TextButton("Confirm", style);
		btnConfirm.setPosition(popupPanel.getX() + 50, popupPanel.getY() + 50);
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
						controller.authenticate(email, nick);
					}
					break;
				case SingIn:
					if (!email.isEmpty() && !nick.isEmpty()) {
						controller.singIn(email, nick);
					}
					break;
				default:
					break;
				}
			}
		});

		btnBack = new TextButton("Back", style);
		btnBack.setPosition(
				popupPanel.getX() + popupPanel.getWidth() - btnBack.getWidth()
						- 50, popupPanel.getY() + 50);
		btnBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				moveUp(MenuLogInState.Idle);
			}
		});

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
				.push(Tween.to(popupPanel, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(lblHeading, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(textFieldLogIn, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(textFieldSingIn, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(txtEmail, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(txtNick, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(btnConfirm, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(btnBack, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(characters, ActorAccessor.ALPHA, speed)
						.target(1))
				.push(Tween.to(btnLogIn, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(btnSingIn, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(popupPanel, ActorAccessor.Y, speed).target(
						popupPanel.getY() + CrystalClash.HEIGHT))
				.push(Tween.to(lblHeading, ActorAccessor.Y, speed).target(
						lblHeading.getY() + CrystalClash.HEIGHT))
				.push(Tween.to(textFieldLogIn, ActorAccessor.Y, speed).target(
						textFieldLogIn.getY() + CrystalClash.HEIGHT))
				.push(Tween.to(textFieldSingIn, ActorAccessor.Y, speed).target(
						textFieldSingIn.getY() + CrystalClash.HEIGHT))
				.push(Tween.to(txtEmail, ActorAccessor.Y, speed).target(
						txtEmail.getY() + CrystalClash.HEIGHT))
				.push(Tween.to(txtNick, ActorAccessor.Y, speed).target(
						txtNick.getY() + CrystalClash.HEIGHT))
				.push(Tween.to(btnConfirm, ActorAccessor.Y, speed).target(
						btnConfirm.getY() + CrystalClash.HEIGHT))
				.push(Tween.to(btnBack, ActorAccessor.Y, speed).target(
						btnBack.getY() + CrystalClash.HEIGHT))
				.start(tweenManager);

		this.state = state;
	}

	private void moveDown(MenuLogInState state) {
		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(popupPanel, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2))
				.push(Tween.to(lblHeading, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2
								+ popupPanel.getHeight() - 100))
				.push(Tween.to(textFieldLogIn, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2
								+ popupPanel.getHeight() - 200))
				.push(Tween.to(textFieldSingIn, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2
								+ popupPanel.getHeight() - 300))
				.push(Tween.to(txtEmail, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2
								+ popupPanel.getHeight() - 200))
				.push(Tween.to(txtNick, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2
								+ popupPanel.getHeight() - 300))
				.push(Tween.to(btnConfirm, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2
								+ 50))
				.push(Tween.to(btnBack, ActorAccessor.Y, speed).target(
						CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2
								+ 50))
				.push(Tween.to(popupPanel, ActorAccessor.ALPHA, speed)
						.target(1))
				.push(Tween.to(lblHeading, ActorAccessor.ALPHA, speed)
						.target(1))
				.push(Tween.to(textFieldLogIn, ActorAccessor.ALPHA, speed)
						.target(1))
				.push(Tween.to(textFieldSingIn, ActorAccessor.ALPHA, speed)
						.target(1))
				.push(Tween.to(txtEmail, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(txtNick, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(btnConfirm, ActorAccessor.ALPHA, speed)
						.target(1))
				.push(Tween.to(btnBack, ActorAccessor.ALPHA, speed).target(1))
				.push(Tween.to(characters, ActorAccessor.ALPHA, speed)
						.target(0))
				.push(Tween.to(btnLogIn, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(btnSingIn, ActorAccessor.ALPHA, speed).target(0))
				.start(tweenManager);

		this.state = state;
	}

	private void adjustToKeyboard(boolean up) {
		// TODO: true mueve hacia arriba, false mueve hacia abajo
	}

	public void dispose() {
		popupPanelTexture.dispose();
		atlas.dispose();
		skin.dispose();
		font.dispose();
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
		return false;
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
