package pruebas.Renders.helpers.ui;

import pruebas.Accessors.ActorAccessor;
import pruebas.CrystalClash.CrystalClash;
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
	private Image imgBackground;
	private Label lblMessage;
	private TextButton btnYes;
	private TextButton btnNo;

	private TweenManager manager;
	private MessageBoxCallback callback;
	private boolean hideOnAction = true;
	private Object userData;

	private MessageBox() {
		setSize(CrystalClash.WIDTH / 2, CrystalClash.HEIGHT / 2);
		setPosition(CrystalClash.WIDTH / 4, CrystalClash.HEIGHT + getHeight());

		imgBackground = new Image(ResourceHelper.getTexture("data/Images/Menu/games_list_background.png"));
		imgBackground.setSize(getWidth(), getHeight());
		imgBackground.setPosition(0, 0);
		addActor(imgBackground);

		lblMessage = new Label("", new LabelStyle(ResourceHelper.getFont(), Color.WHITE));
		lblMessage.setAlignment(Align.center);
		lblMessage.setWrap(true);
		lblMessage.setSize(getWidth() - 100, getHeight() - 150);
		lblMessage.setPosition(50, 100);
		addActor(lblMessage);

		btnYes = new TextButton("Yeah!!", ResourceHelper.getButtonStyle());
		btnYes.setSize(250, 70);
		btnYes.setPosition(getWidth() - btnYes.getWidth() - 50, 50);
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
		btnNo.setSize(250, 70);
		btnNo.setPosition(50, 50);
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
		return new MessageBox();
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

	public void show(TweenManager manager, String message, Object userData) {
		lblMessage.setText(message);
		this.manager = manager;
		show(userData);
	}

	public void show(Object userData) {
		this.userData = userData;
		show();
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
		return Timeline.createSequence()
				.push(Tween.to(this, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT / 4));
	}

	protected Timeline getExitAnimation() {
		return Timeline.createSequence()
				.push(Tween.to(this, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT + getHeight()));
	}
}
