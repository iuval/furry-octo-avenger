package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.MenuLogIn;
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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuLogInRender extends MenuRender {

	private static MenuLogInRender instance;
	private TweenManager tweenManager;

	private MenuLogIn menu;

	private TextureAtlas atlas;
	private Skin skin;
	private BitmapFont font;
	private Label lblHeading;
	private TextField txtEmail;
	private TextField txtNick;
	private Button btnLogIn;
	private Button btnSingIn;
	private Button btnConfirm;
	private Button btnBack;
	private Texture popupPanelTexture;
	private Image popupPanel;

	private StringWriting stringWriting;
	public MenuLogInState state;

	public MenuLogInRender(MenuLogIn menu) {
		this.menu = menu;
		tweenManager = new TweenManager();
		stringWriting = StringWriting.None;
		state = MenuLogInState.Idle;

		loadStuff();
	}
	
	public static MenuLogInRender getInstance(MenuLogIn menu){
		if(instance == null)
			instance = new MenuLogInRender(menu);
		
		return instance;
	}
	
	public void render(float dt, Stage stage) {
		switch(state){
		case Idle:
			break;
		case LogIn:
			break;
		case SingIn:
			break;
		default:
			break;
		}
		stage.addActor(btnLogIn);
		stage.addActor(btnSingIn);
		stage.addActor(popupPanel);
		stage.addActor(lblHeading);
		stage.addActor(txtEmail);
		stage.addActor(txtNick);
		stage.addActor(btnConfirm);
		stage.addActor(btnBack);
		tweenManager.update(dt);
	}

	private void loadStuff() {
		atlas = new TextureAtlas("data/Buttons/buttons.pack");
		skin = new Skin(atlas);

		ButtonStyle logInStyle = new ButtonStyle();
		logInStyle.up = skin.getDrawable("boton");
		logInStyle.down = skin.getDrawable("boton");		
		btnLogIn = new Button(logInStyle);
		btnLogIn.setSize(200, 70);
		btnLogIn.setPosition(200, 200);
		btnLogIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(state == MenuLogInState.Idle)
					MoveDown(MenuLogInState.LogIn);
			}
		});

		ButtonStyle singInStyle = new ButtonStyle();
		singInStyle.up = skin.getDrawable("boton");
		singInStyle.down = skin.getDrawable("boton");
		btnSingIn = new Button(singInStyle);
		btnSingIn.setSize(200, 70);
		btnSingIn.setPosition(CrystalClash.WIDTH - 400, 200);
		btnSingIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(state == MenuLogInState.Idle)
					MoveDown(MenuLogInState.SingIn);
			}
		});
		
		font = new BitmapFont(Gdx.files.internal("data/Fonts/font.fnt"), false);
		
		popupPanelTexture = new Texture(Gdx.files.internal("data/Images/Menu/askForEmailBackground.png"));
		popupPanel = new Image(popupPanelTexture);
		popupPanel.setSize(800, 500);
		popupPanel.setPosition(CrystalClash.WIDTH / 2 - popupPanel.getWidth() / 2, (CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2) + CrystalClash.HEIGHT);

		lblHeading = new Label("Welcome to Crystal Clash", new LabelStyle(font, Color.WHITE));
		lblHeading.setPosition(popupPanel.getX() + popupPanel.getWidth() / 2 - lblHeading.getWidth() / 2, popupPanel.getTop() - 100);

		//Skin textFieldSkin = new Skin();
		//textFieldSkin.add("textFieldCursor", new Texture(Gdx.files.internal("data/Images/Menu/cursor_1.png")));
		
		//TextFieldStyle textFieldStyle = new TextFieldStyle(font, Color.WHITE, null, textFieldSkin.getDrawable("textFieldCursor"), null);
		TextFieldStyle textFieldStyle = new TextFieldStyle(font, Color.WHITE, null, null, null);
		txtEmail = new TextField("", textFieldStyle);
		txtEmail.setMessageText("Enter your Email...");
		txtEmail.setMaxLength(30);
		txtEmail.setSize(700, 50);
		txtEmail.setPosition(popupPanel.getX() + 50, popupPanel.getTop() - 200);
		txtEmail.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				txtEmail.getOnscreenKeyboard().show(true);
				stringWriting = StringWriting.Email;
			}
		});

		txtNick = new TextField("", textFieldStyle);
		txtNick.setMessageText("Enter your Nick...");
		txtNick.setMaxLength(30);
		txtNick.setSize(700, 50);
		txtNick.setPosition(popupPanel.getX() + 50, popupPanel.getTop() - 300);
		txtNick.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				txtNick.getOnscreenKeyboard().show(true);
				stringWriting = StringWriting.Nick;
			}
		});

		ButtonStyle confirmStyle = new ButtonStyle();
		confirmStyle.up = skin.getDrawable("boton");
		confirmStyle.down = skin.getDrawable("boton");		
		btnConfirm = new Button(confirmStyle);
		btnConfirm.setSize(200, 70);
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
						MoveUp(MenuLogInState.Idle);
						menu.logIn(email);
					}
				case SingIn:
					if (!email.isEmpty() && !nick.isEmpty()) {
						MoveUp(MenuLogInState.Idle);
						menu.singIn(email, nick);
					}
					break;
				default:
					break;
				}
			}
		});
		
		ButtonStyle backStyle = new ButtonStyle();
		backStyle.up = skin.getDrawable("boton");
		backStyle.down = skin.getDrawable("boton");		
		btnBack = new Button(backStyle);
		btnBack.setSize(200, 70);
		btnBack.setPosition(popupPanel.getX() + popupPanel.getWidth() - btnBack.getWidth() - 50, popupPanel.getY() + 50);
		btnBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MoveUp(MenuLogInState.Idle);
			}
		});
		
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		Timeline.createParallel()
				.push(Tween.set(btnLogIn, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(btnSingIn, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(btnLogIn, ActorAccessor.ALPHA, 2).target(1))
				.push(Tween.to(btnSingIn, ActorAccessor.ALPHA, 2).target(1))
				.start(tweenManager);

		tweenManager.update(Float.MIN_VALUE);
	}

	private void MoveUp(MenuLogInState state){
		Timeline.createParallel()
			.push(Tween.to(popupPanel, ActorAccessor.ALPHA, 2).target(0))
			.push(Tween.to(lblHeading, ActorAccessor.ALPHA, 2).target(0))
			.push(Tween.to(txtEmail, ActorAccessor.ALPHA, 2).target(0))
			.push(Tween.to(txtNick, ActorAccessor.ALPHA, 2).target(0))
			.push(Tween.to(btnConfirm, ActorAccessor.ALPHA, 2).target(0))
			.push(Tween.to(btnBack, ActorAccessor.ALPHA, 2).target(0))
			.push(Tween.to(btnLogIn, ActorAccessor.ALPHA, 2).target(1))
			.push(Tween.to(btnSingIn, ActorAccessor.ALPHA, 2).target(1))
			.push(Tween.to(popupPanel, ActorAccessor.Y, 2).target(popupPanel.getY()	+ CrystalClash.HEIGHT))
			.push(Tween.to(lblHeading, ActorAccessor.Y, 2).target(lblHeading.getY()	+ CrystalClash.HEIGHT))
			.push(Tween.to(txtEmail, ActorAccessor.Y, 2).target(txtEmail.getY()	+ CrystalClash.HEIGHT))
			.push(Tween.to(txtNick, ActorAccessor.Y, 2).target(txtNick.getY() + CrystalClash.HEIGHT))
			.push(Tween.to(btnConfirm, ActorAccessor.Y, 2).target(btnConfirm.getY()	+ CrystalClash.HEIGHT))
			.push(Tween.to(btnBack, ActorAccessor.Y, 2).target(btnBack.getY() + CrystalClash.HEIGHT))
			.start(tweenManager);
		
		this.state = state;
	}
	
	private void MoveDown(MenuLogInState state){
		Timeline.createParallel()
			.push(Tween.to(popupPanel, ActorAccessor.Y, 2).target(CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2))
			.push(Tween.to(lblHeading, ActorAccessor.Y, 2).target(CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2 + popupPanel.getHeight() - 100))
			.push(Tween.to(txtEmail, ActorAccessor.Y, 2).target(CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2 + popupPanel.getHeight() - 200))
			.push(Tween.to(txtNick, ActorAccessor.Y, 2).target(CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2 + popupPanel.getHeight() - 300))
			.push(Tween.to(btnConfirm, ActorAccessor.Y, 2).target(CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2 + 50))
			.push(Tween.to(btnBack, ActorAccessor.Y, 2).target(CrystalClash.HEIGHT / 2 - popupPanel.getHeight() / 2 + 50))
			.push(Tween.to(popupPanel, ActorAccessor.ALPHA, 2).target(1))
			.push(Tween.to(lblHeading, ActorAccessor.ALPHA, 2).target(1))
			.push(Tween.to(txtEmail, ActorAccessor.ALPHA, 2).target(1))
			.push(Tween.to(txtNick, ActorAccessor.ALPHA, 2).target(1))
			.push(Tween.to(btnConfirm, ActorAccessor.ALPHA, 2).target(1))
			.push(Tween.to(btnBack, ActorAccessor.ALPHA, 2).target(1))
			.push(Tween.to(btnLogIn, ActorAccessor.ALPHA, 2).target(0))
			.push(Tween.to(btnSingIn, ActorAccessor.ALPHA, 2).target(0))
			.start(tweenManager);
		
		this.state = state;
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
