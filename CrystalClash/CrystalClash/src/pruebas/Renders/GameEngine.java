package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Accessors.MusicAccessor;
import pruebas.Accessors.UnitAccessor;
import pruebas.Audio.AudioManager;
import pruebas.Audio.MusicWrapper;
import pruebas.Controllers.GameController;
import pruebas.Controllers.MenuGames;
import pruebas.Controllers.MenuLogIn;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Unit;
import pruebas.Enumerators.GameState;
import pruebas.Renders.helpers.ResourceHelper;
import pruebas.Renders.helpers.ui.MessageBox;
import pruebas.Renders.helpers.ui.MessageBoxCallback;
import pruebas.Renders.helpers.ui.SuperAnimatedActor;
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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.JsonValue;

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

	private SplashScreen splashRender;
	private MenuLogInRender menuLogInRender;
	private MenuGamesRender menuGamesRender;
	private TutorialRender tutorialRender;
	private WorldController world;
	private WorldRender worldRender;

	private static TweenManager tweenManager;
	private static SuperAnimatedActor loadingTexture;

	private Image background;

	@Override
	public void show() {
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		Tween.registerAccessor(MusicWrapper.class, new MusicAccessor());
		Tween.registerAccessor(Unit.class, new UnitAccessor());

		inputManager = new InputMultiplexer();
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
		ResourceHelper.slowLoad();

		BlackScreen.load();

		MessageBox.build().setTweenManager(tweenManager);

		// loadingTexture = new
		// SuperAnimatedActor(FileUtil.getSuperAnimation("menu/Loading/loading"),
		// true, FACING.right);
		// hideLoading();

		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputManager);
	}

	public static GameState getState() {
		return state;
	}

	private void load() {
		TextureRegion backgroundTexture = ResourceHelper.getTexture("menu/menu_background");
		background = new Image(backgroundTexture);

		Timeline.createSequence()
				.push(Tween.set(background, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(background, ActorAccessor.ALPHA, 2).target(1))
				.start(tweenManager);

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
		if (state == GameState.InGame || state == GameState.InTranstionMenuGamesAndGame) {
			batch.begin();
			worldRender.render(dt, batch);
			batch.end();
		}
		stage.draw();
	}

	private void setState(GameState newState) {
		state = newState;
		inputManager.clear();
		stage.clear();
		stage.addActor(background);
		inputManager.addProcessor(stage);
		switch (newState) {
		case InMenuLogIn:
			inputManager.addProcessor(menuLogInRender);
			stage.addActor(menuLogInRender);
			break;
		case InTranstionMenuLogInAndMenuGames:
			stage.addActor(menuLogInRender);
			stage.addActor(menuGamesRender);
			break;
		case InTranstionSplashAndMenuGames:
			stage.addActor(background);
			stage.addActor(splashRender);
			stage.addActor(menuGamesRender);
			break;
		case InMenuGames:
			stage.addActor(background);
			stage.addActor(menuGamesRender);
			if (worldRender != null) {
				worldRender.dispose();
				worldRender = null;
			}
			break;
		case InTutorial:
			stage.addActor(background);
			stage.addActor(tutorialRender);
			break;
		case InTranstionMenuGamesAndTutorial:
			stage.addActor(background);
			stage.addActor(menuGamesRender);
			stage.addActor(tutorialRender);
			break;
		case InTranstionSplashAndTutorial:
			stage.addActor(background);
			stage.addActor(splashRender);
			stage.addActor(tutorialRender);
			break;
		case InTranstionSplashAndMenuLogIn:
			stage.addActor(background);
			stage.addActor(menuLogInRender);
			stage.addActor(tutorialRender);
			break;
		case InTranstionMenuGamesAndGame:
			stage.addActor(menuGamesRender);
			break;
		case InGame:
			inputManager.addProcessor(worldRender);
			stage.addActor(worldRender);
			break;
		}
		stage.addActor(BlackScreen.getImage());
		stage.addActor(MessageBox.build());
		if (loadingTexture != null)
			stage.addActor(loadingTexture);
	}

	public void openGame(final JsonValue data) {
		Timeline t = Timeline.createSequence();
		menuGamesRender.pushExitAnimation(t);
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (worldRender == null) {
					world = new WorldController(data);
					worldRender = world.getRender();
				}
				menuGamesRender.closed();
				setState(GameState.InTranstionMenuGamesAndGame);
				Timeline.createSequence()
						.push(Tween.to(background, ActorAccessor.ALPHA, CrystalClash.SLOW_ANIMATION_SPEED).target(0))
						.setCallback(new TweenCallback() {
							@Override
							public void onEvent(int type, BaseTween<?> source) {
								setState(GameState.InGame);
							};
						}).start(tweenManager);

				world.getRender().pushEnterAnimation(Timeline.createParallel()).start(tweenManager);
			};
		});
		t.start(tweenManager);
	}

	public void openSplash() {
		splashRender = new SplashScreen();
		stage.addActor(splashRender);

		GameEngine.state = GameState.InSplash;
		Timeline t = Timeline.createSequence();
		splashRender.pushEnterAnimation(t);
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				loadInSplash();
				if (!GameController.willTryToLogin())
					openMenuLogIn();
			}
		});
		t.start(tweenManager);
	}

	public void openMenuLogIn() {
		Timeline t = Timeline.createSequence();
		if (state == GameState.InMenuGames) {
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
					menuLogInRender.init();
				}
				setState(GameState.InMenuLogIn);
				menuLogInRender.pushEnterAnimation(Timeline.createSequence())
						.setCallback(new TweenCallback() {
							@Override
							public void onEvent(int type, BaseTween<?> source) {
								menuLogInRender.shown();
							};
						}).start(tweenManager);
			}
		});
		t.start(tweenManager);
	}

	public void openMenuGames() {
		Timeline t = Timeline.createSequence();
		if (state == GameState.InSplash) {
			splashRender.pushExitAnimation(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					splashRender.closed();
					if (menuGamesRender == null) {
						menuGamesRender = MenuGames.getInstance().getRender();
						menuGamesRender.init();
					}
					MenuGames.getInstance().getGamesList();
					setState(GameState.InTranstionSplashAndMenuGames);
					menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InMenuGames);
									menuGamesRender.shown();
								}
							}).start(tweenManager);
				}
			});
		} else if (state == GameState.InMenuLogIn) {
			menuLogInRender.pushExitAnimation(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					menuLogInRender.closed();
					if (menuGamesRender == null) {
						menuGamesRender = MenuGames.getInstance().getRender();
					}
					menuGamesRender.init();
					setState(GameState.InTranstionMenuLogInAndMenuGames);
					menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InMenuGames);
									menuGamesRender.shown();
								}
							}).start(tweenManager);
				}
			});
		} else if (state == GameState.InGame) {
			worldRender.pushExitAnimation(t);
			t.push(Tween.set(background, ActorAccessor.ALPHA).target(0));
			t.push(Tween.to(background, ActorAccessor.ALPHA, CrystalClash.SLOW_ANIMATION_SPEED).target(1));
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if (menuGamesRender == null) {
						menuGamesRender = MenuGames.getInstance().getRender();
						menuGamesRender.init();
					}
					MenuGames.getInstance().getGamesList();
					setState(GameState.InTranstionMenuGamesAndGame);
					menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InMenuGames);
									menuGamesRender.shown();
								}
							}).start(tweenManager);
				}
			});
		} else if (state == GameState.InTutorial) {
			tutorialRender.pushExitAnimation(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					tutorialRender.closed();
					if (menuGamesRender == null) {
						menuGamesRender = MenuGames.getInstance().getRender();
						menuGamesRender.init();
					}
					MenuGames.getInstance().getGamesList();
					setState(GameState.InTranstionMenuGamesAndTutorial);
					menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InMenuGames);
									menuGamesRender.shown();
								}
							}).start(tweenManager);
				}
			});
		}
		t.start(tweenManager);
	}

	public void openTutorial() {
		Timeline t = Timeline.createSequence();
		if (state == GameState.InSplash) {
			splashRender.pushExitAnimation(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					splashRender.closed();
					if (tutorialRender == null) {
						tutorialRender = new TutorialRender(world);
					}
					MenuGames.getInstance().getGamesList();
					setState(GameState.InTranstionSplashAndTutorial);
					tutorialRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InTutorial);
									tutorialRender.shown();
								}
							}).start(tweenManager);
				}
			});
		} else if (state == GameState.InMenuLogIn) {
			menuLogInRender.pushExitAnimation(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					menuLogInRender.closed();
					if (tutorialRender == null) {
						tutorialRender = new TutorialRender(world);
					}
					MenuGames.getInstance().getGamesList();
					setState(GameState.InTranstionSplashAndTutorial);
					menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InTutorial);
									tutorialRender.shown();
								}
							}).start(tweenManager);
				}
			});
		} else if (state == GameState.InMenuGames) {
			menuGamesRender.pushExitAnimation(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					menuGamesRender.closed();
					if (tutorialRender == null) {
						tutorialRender = new TutorialRender(world);
					}
					MenuGames.getInstance().getGamesList();
					setState(GameState.InTranstionSplashAndTutorial);
					tutorialRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InTutorial);
									tutorialRender.shown();
								}
							}).start(tweenManager);
				}
			});
		}
		t.start(tweenManager);
	}

	public void logInError(String message) {
		if (state == GameState.InMenuLogIn) {
			MessageBox.build()
					.setMessage("Log in failed...\nAre you trying to login with your ex's account to lose all their games?")
					.oneButtonsLayout("Nop...")
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
					.setMessage("You are back!! Hurray!!")
					.oneButtonsLayout("Let's get them")
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
				.setMessage("Loading...")
				.noButtonsLayout()
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
