package com.crystalclash.renders;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.JsonValue;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.accessors.MusicAccessor;
import com.crystalclash.accessors.UnitAccessor;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.audio.MusicWrapper;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.MenuGames;
import com.crystalclash.controllers.MenuLogIn;
import com.crystalclash.controllers.WorldController;
import com.crystalclash.entities.Unit;
import com.crystalclash.enumerators.GameState;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.renders.helpers.ui.MessageBox.Buttons;
import com.crystalclash.renders.helpers.ui.MessageBoxCallback;
import com.crystalclash.renders.helpers.ui.SuperAnimatedActor;
import com.crystalclash.util.I18n;
import com.crystalclash.views.MenuGamesView;
import com.crystalclash.views.MenuLogInView;
import com.crystalclash.views.SplashView;
import com.crystalclash.views.TutorialView;
import com.crystalclash.views.WorldView;

public class GameEngine implements Screen {
	private static GameEngine instance;

	public static GameEngine getInstance() {
		if (instance == null)
			instance = new GameEngine();
		return instance;
	}

	private InputMultiplexer inputManager;
	private SpriteBatch batch;
	private Stage stage;
	public static OrthographicCamera camera;

	private static GameState state = GameState.InSplash;

	private SplashView splashRender;
	private MenuLogInView menuLogInRender;
	private MenuGamesView menuGamesRender;
	private TutorialView tutorialRender;
	private WorldController world;
	private WorldView worldRender;

	private static TweenManager tweenManager;
	private static SuperAnimatedActor loadingTexture;

	private ParallaxBackgound background;

	@Override
	public void show() {
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		Tween.registerAccessor(MusicWrapper.class, new MusicAccessor());
		Tween.registerAccessor(Unit.class, new UnitAccessor());

		inputManager = new InputMultiplexer();
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputManager);

		batch = new SpriteBatch();
		stage = new Stage();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, CrystalClash.WIDTH, CrystalClash.HEIGHT);

		batch.setProjectionMatrix(camera.combined);
		stage.setCamera(camera);

		ResourceHelper.fastLoad();
		AudioManager.load();

		load();
		openSplash();
	}

	private void loadInSplash() {
		GameController.loadSharedStats();
		I18n.load();

		stage.addActor(BlackOverlay.build());
		ParallaxBackgound.getInstance().loadGamesList();
		// stage.addActor(MessageBox.build());
		// if (loadingTexture != null)
		// stage.addActor(loadingTexture);
		// loadingTexture = new
		// SuperAnimatedActor(FileUtil.getSuperAnimation("menu/Loading/loading"),
		// true, FACING.right);
		// hideLoading();

	}

	public static GameState getState() {
		return state;
	}

	private void load() {
		ResourceHelper.slowLoad();

		background = ParallaxBackgound.getInstance();
		stage.addActor(background);

		tweenManager.update(Float.MIN_VALUE);
	}

	@Override
	public void render(float delta) {
		updateGame(delta);
		renderGame(delta);
	}

	private void updateGame(float dt) {
		switch (state) {
		case InGame:
			world.update(dt);
			break;
		}
		tweenManager.update(dt);
		stage.act(dt);
	}

	private void renderGame(float dt) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (state == GameState.InGame ||
				state == GameState.InTranstionMenuGamesAndGame) {
			batch.begin();
			worldRender.render(dt, batch);
			batch.end();
		}
		stage.draw();
	}

	private void setState(GameState newState) {
		state = newState;
		inputManager.clear();
		inputManager.addProcessor(stage);
		switch (newState) {
		case InSplash:
			stage.addActor(splashRender);
			break;
		case InMenuLogIn:
			inputManager.addProcessor(menuLogInRender);
			stage.addActor(menuLogInRender);
			break;
		case InTranstionMenuLogInAndMenuGames:
			stage.addActor(menuLogInRender);
			stage.addActor(menuGamesRender);
			break;
		case InTranstionSplashAndMenuGames:
			stage.addActor(splashRender);
			stage.addActor(menuGamesRender);
			break;
		case InMenuGames:
			inputManager.addProcessor(menuGamesRender);
			stage.addActor(menuGamesRender);
			break;
		case InTranstionSplashAndMenuLogIn:
			stage.addActor(menuLogInRender);
			stage.addActor(tutorialRender);
			break;
		case InTranstionMenuGamesAndGame:
			stage.addActor(worldRender);
			stage.addActor(menuGamesRender);
			break;
		case InGame:
			inputManager.addProcessor(worldRender);
			stage.addActor(worldRender);
			break;
		}
		stage.addActor(BlackOverlay.build());
	}

	private void createWorld(JsonValue data) {
		if (worldRender == null) {
			world = new WorldController(data);
			worldRender = world.getRender();
		}
	}

	public void openGame(final JsonValue data) {
		Timeline t = Timeline.createSequence();
		menuGamesRender.pushExitAnimation(t);
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				createWorld(data);
				menuGamesRender.closed();
				setState(GameState.InTranstionMenuGamesAndGame);
				Timeline t = background.pushHide(background.pushMoveToGame(Timeline.createSequence()));
				worldRender.pushEnterAnimation(t);
				t.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						setState(GameState.InGame);
						menuGamesRender.remove();
					};
				}).start(tweenManager);
			};
		});
		t.start(tweenManager);
	}

	public void openSplash() {
		splashRender = new SplashView();
		setState(GameState.InSplash);
		Timeline t = Timeline.createSequence();
		background.pushShow(t);
		splashRender.pushEnterAnimation(t);
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				loadInSplash();
				if (!GameController.willTryToLogin())
					openMenuLogIn();
				splashRender.remove();
			}
		});
		t.start(tweenManager);
	}

	public void openMenuLogIn() {
		Timeline t = Timeline.createSequence();
		if (state == GameState.InMenuGames) {
			ParallaxBackgound.getInstance().pushMoveToLogin(t);
			menuGamesRender.pushExitAnimation(t);
		} else if (state == GameState.InSplash) {
			splashRender.pushExitAnimation(t);
		}
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (state == GameState.InMenuGames) {
					menuGamesRender.closed();
				} else if (state == GameState.InSplash) {
					splashRender.closed();
				}
				if (menuLogInRender == null) {
					menuLogInRender = MenuLogIn.getInstance().getRender();
				}
				menuLogInRender.init();
				ParallaxBackgound.getInstance().loadLogIn();
				setState(GameState.InMenuLogIn);
				menuLogInRender.pushEnterAnimation(Timeline.createSequence())
						.setCallback(new TweenCallback() {
							@Override
							public void onEvent(int type, BaseTween<?> source) {
								menuLogInRender.shown();
								menuGamesRender.remove();
								splashRender.remove();
							};
						}).start(tweenManager);
			}
		});
		t.start(tweenManager);
	}

	public void openMenuGames() {
		Timeline t = Timeline.createSequence();
		if (state == GameState.InSplash) {
			background.pushMoveToGamesList(t);
			splashRender.pushExitAnimation(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					splashRender.closed();
					if (menuGamesRender == null) {
						menuGamesRender = MenuGames.getInstance().getRender();
					}
					menuGamesRender.init();
					setState(GameState.InTranstionSplashAndMenuGames);
					menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InMenuGames);
									menuGamesRender.shown();
									splashRender.remove();
								}
							}).start(tweenManager);
				}
			});
		} else if (state == GameState.InMenuLogIn) {
			menuLogInRender.pushExitAnimation(t);
			background.pushMoveToGamesList(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					menuLogInRender.closed();
					if (menuGamesRender == null) {
						menuGamesRender = MenuGames.getInstance().getRender();
					}
					menuGamesRender.init();
					setState(GameState.InTranstionMenuLogInAndMenuGames);
					background.pushMoveToGamesList(menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InMenuGames);
									menuGamesRender.shown();
									menuLogInRender.remove();
								}
							})).start(tweenManager);
				}
			});
		} else if (state == GameState.InGame) {
			worldRender.pushExitAnimation(t);
			background.pushShow(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if (menuGamesRender == null) {
						menuGamesRender = MenuGames.getInstance().getRender();
					}
					menuGamesRender.init();
					setState(GameState.InTranstionMenuGamesAndGame);
					background.pushShow(menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InMenuGames);
									menuGamesRender.shown();
									worldRender.remove();
									worldRender.dispose();
									worldRender = null;
								}
							})).start(tweenManager);
				}
			});
		}
		t.start(tweenManager);
	}

	public void openTutorial() {
		openGame(null);
	}

	public void singUpError(String message) {
		MessageBox.build()
				.setMessage("game_engine_sign_up_error", Buttons.One)
				.setCallback(null)
				.show();
	}

	public void logInError(String message) {
		if (state == GameState.InMenuLogIn) {
			MessageBox.build()
					.setMessage("game_engine_sign_in_error", Buttons.One)
					.setCallback(null)
					.show();
		} else {
			openMenuLogIn();
			MessageBox.build().hide();
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
		if (state == GameState.InGame)
			worldRender.pause();
		GameController.saveProfile();
	}

	@Override
	public void resume() {
		if (state == GameState.InGame) {
			MessageBox.build()
					.setMessage("game_engine_user_back", Buttons.One)
					.setCallback(new MessageBoxCallback() {
						@Override
						public void onEvent(int type, Object data) {
							worldRender.resume();
						}
					})
					.show();
		}
	}

	@Override
	public void dispose() {
		GameController.saveProfile();
		batch.dispose();
		stage.dispose();
		menuLogInRender.dispose();
		worldRender.dispose();
	}

	public static Vector2 getRealPosition(float x, float y) {
		Vector3 vec = new Vector3(x, y, 0);
		camera.unproject(vec);
		return new Vector2(vec.x, vec.y);
	}

	public static void showLoading() {
		MessageBox.build()
				.setMessage("game_engine_loading", Buttons.None)
				.setHideOnAction(false)
				.setCallback(null)
				.show();
	}

	public static void hideLoading() {
		MessageBox.build().hide();
	}

	public static void start(Timeline t) {
		t.start(tweenManager);
	}

	public static void kill(Object o) {
		tweenManager.killTarget(o);
	}
}
