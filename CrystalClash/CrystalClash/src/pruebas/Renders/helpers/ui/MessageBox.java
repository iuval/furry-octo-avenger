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

		imgWindowBackground = new Image(ResourceHelper.getTexture("Menu/games_list_background.png"));
		imgWindowBackground.setSize(getWidth(), getHeight());
		imgWindowBackground.setPosition(0, 0);
		addActor(imgWindowBackground);

		lblMessage = new Label("", new LabelStyle(ResourceHelper.getFont(), Color.WHITE));
		lblMessage.setAlignment(Align.center);
		lblMessage.setWrap(true);
		addActor(lblMessage);

		btnYes = new TextButton("Yeah!!", ResourceHelper.getButtonStyle());
		btnYes.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				callCallback(MessageBoxCallback.YES);
				if (hideOnAction)
					hide();
			}
		});

		btnNo = new TextButton("Meh", ResourceHelper.getButtonStyle());
		btnNo.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				callCallback(MessageBoxCallback.NO);
				if (hideOnAction)
					hide();
			}
		});
	}

	public static MessageBox build() {
		if (instance == null)
			instance = new MessageBox();
		instance.setHideOnAction(true);
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

	public MessageBox setTweenManager(TweenManager manager) {
		this.manager = manager;
		return this;
	}

	public MessageBox setUserData(Object data) {
		userData = data;
		return this;
	}

	public MessageBox twoButtonsLayout(String yes, String no) {
		btnYes.setText(yes);
		btnYes.setSize(360, 100);
		btnYes.setPosition(getWidth() - btnYes.getWidth() - 30, 30);
		addActor(btnYes);

		btnNo.setText(no);
		btnNo.setSize(360, 100);
		btnNo.setPosition(30, 30);
		addActor(btnNo);

		lblMessage.setSize(getWidth() - 100, getHeight() - 150);
		lblMessage.setPosition(50, 100);
		return this;
	}

	public MessageBox oneButtonsLayout(String yes) {
		btnYes.setText(yes);
		btnYes.setSize(500, 100);
		btnYes.setPosition(getWidth() / 2 - btnYes.getWidth() / 2, 30);
		addActor(btnYes);

		btnNo.remove();

		lblMessage.setSize(getWidth() - 100, getHeight() - 150);
		lblMessage.setPosition(50, 100);
		return this;
	}

	public MessageBox noButtonsLayout() {
		btnYes.remove();

		btnNo.remove();

		lblMessage.setSize(getWidth() - 100, getHeight() - 100);
		lblMessage.setPosition(50, 50);
		return this;
	}

	public static void show(TweenManager manager, String message, Object userData) {
		build()
				.setMessage(message)
				.setTweenManager(manager);
		show(userData);
	}

	public static void show(Object userData) {
		build().setUserData(userData).show();
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
				.push(Tween.to(this, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT / 4));
	}

	protected Timeline getExitAnimation() {
		return GameEngine.pushHideBlackScreen(Timeline.createParallel())
				.push(Tween.to(this, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT + getHeight()));
	}
}
