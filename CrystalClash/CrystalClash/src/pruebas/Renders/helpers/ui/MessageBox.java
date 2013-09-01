package pruebas.Renders.helpers.ui;

import pruebas.Accessors.ActorAccessor;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Renders.GameEngine;
import pruebas.Renders.helpers.ResourceHelper;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MessageBox extends Group {
	private static MessageBox instance;

	private Image imgWindowBackground;
	private Label lblMessage;
	private TextButton btnYes;
	private TextButton btnNo;

	private TweenManager manager;
	private MessageBoxCallback callback;
	private boolean hideOnAction = true;
	private Object userData;

	private MessageBox() {
		setSize(800, CrystalClash.HEIGHT / 2);
		setPosition(CrystalClash.WIDTH / 2 - 400, CrystalClash.HEIGHT + getHeight());

		imgWindowBackground = new Image(ResourceHelper.getTexture("data/Images/Menu/games_list_background.png"));
		imgWindowBackground.setSize(getWidth(), getHeight());
		imgWindowBackground.setPosition(0, 0);
		addActor(imgWindowBackground);

		lblMessage = new Label("", new LabelStyle(ResourceHelper.getFont(), Color.WHITE));
		lblMessage.setAlignment(Align.center);
		lblMessage.setWrap(true);
		lblMessage.setSize(getWidth() - 100, getHeight() - 150);
		lblMessage.setPosition(50, 100);
		addActor(lblMessage);

		btnYes = new TextButton("Yeah!!", ResourceHelper.getButtonStyle());
		btnYes.setSize(360, 100);
		btnYes.setPosition(getWidth() - btnYes.getWidth() - 30, 30);
		btnYes.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				callCallback(MessageBoxCallback.YES);
				if (hideOnAction)
					hide();
			}
		});
		addActor(btnYes);

		btnNo = new TextButton("Meh", ResourceHelper.getButtonStyle());
		btnNo.setSize(360, 100);
		btnNo.setPosition(30, 30);
		btnNo.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				callCallback(MessageBoxCallback.NO);
				if (hideOnAction)
					hide();
			}
		});
		addActor(btnNo);
	}

	public static MessageBox create() {
		if (instance == null)
			instance = new MessageBox();
		return instance;
	}

	public MessageBox setCallback(MessageBoxCallback callback) {
		this.callback = callback;
		return this;
	}

	public MessageBox setHideOnAction(boolean hide) {
		hideOnAction = hide;
		return this;
	}

	public MessageBox setMessage(String message) {
		lblMessage.setText(message);
		return this;
	}

	public MessageBox setYesText(String text) {
		btnYes.setText(text);
		return this;
	}

	public MessageBox setNoText(String text) {
		btnNo.setText(text);
		return this;
	}

	public MessageBox setTweenManager(TweenManager manager) {
		this.manager = manager;
		return this;
	}

	public MessageBox setUserData(Object data) {
		userData = data;
		return this;
	}

	public static void show(TweenManager manager, String message, Object userData) {
		create()
				.setMessage(message)
				.setTweenManager(manager);
		show(userData);
	}

	public static void show(Object userData) {
		create().setUserData(userData).show();
	}

	public void show() {
		setZIndex(99);
		getEnterAnimation().start(manager);
	}

	public void hide() {
		getExitAnimation().start(manager);
	}

	protected void callCallback(int type) {
		if (callback != null)
			callback.onEvent(type, userData);
	}

	protected Timeline getEnterAnimation() {
		return GameEngine.pushShowBlackScreen(Timeline.createParallel())
				.push(Tween.to(this, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT / 4));
	}

	protected Timeline getExitAnimation() {
		return GameEngine.pushHideBlackScreen(Timeline.createParallel())
				.push(Tween.to(this, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT + getHeight()));
	}
}
