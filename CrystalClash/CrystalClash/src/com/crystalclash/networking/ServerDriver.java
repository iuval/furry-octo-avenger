package com.crystalclash.networking;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.MenuGames;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.helpers.ui.BaseBox.BoxButtons;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.util.I18n;

public class ServerDriver {
	private final static String ACTION_LOG_IN = "log_in";
	private final static String ACTION_SIGN_IN = "sign_in";
	private final static String ACTION_LIST_GAMES = "list_games";
	private final static String ACTION_ENABLE_RANDOM = "enable_random";
	private final static String ACTION_GAME_TURN = "game_turn";
	private final static String ACTION_UPDATE_PLAYER = "update_player";
	private final static String ACTION_SURRENDER = "surrender";

	public static void sendSignUp(final String email, final String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);
		data.put("password", password);

		Gdx.net.sendHttpRequest(getPost(ACTION_SIGN_IN, data),
				new HttpResponseListener() {
					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue data = values.get("data");
							GameController.signUpSuccess(data.getString("id"),
									data.getString("name"),
									email,
									password,
									data.getInt("emblem"),
									data.getInt("victory_total"),
									data.getInt("defeat_total"),
									data.getInt("draw_total"));
						} else {
							GameEngine.getInstance().singUpError(values.getString("message"));
						}
					}

					@Override
					public void failed(Throwable t) {
						exceptionMessage();
					}
				});
	}

	public static void sendLogIn(final String email, final String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);
		data.put("password", password);

		Gdx.net.sendHttpRequest(getPost(ACTION_LOG_IN, data),
				new HttpResponseListener() {
					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue data = values.get("data");
							GameController.logInSuccess(data.getString("id"),
									data.getString("name"),
									email,
									password,
									data.getInt("emblem"),
									data.getInt("victory_total"),
									data.getInt("draw_total"),
									data.getInt("defeat_total"));
						} else {
							GameEngine.getInstance().logInError(values.getString("message"));
						}
					}

					@Override
					public void failed(Throwable t) {
						exceptionMessage();
					}
				});
	}

	public static void getListGames(String id) {
		Gdx.net.sendHttpRequest(getGet(ACTION_LIST_GAMES + "/p/" + id),
				new HttpResponseListener() {
					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue data = values.get("data");
							String[][] games = new String[data.size][7];
							JsonValue child;
							for (int i = 0; i < games.length; i++) {
								child = data.get(i);
								games[i][0] = child.getString("game_id");
								games[i][1] = child.getString("name");
								games[i][2] = child.getString("victories");
								games[i][3] = child.getString("turn");
								games[i][4] = child.getString("state");
								games[i][5] = child.getString("emblem");
								games[i][6] = child.getString("surrender");
							}
							MenuGames.getInstance().getGamesListSuccess(games);
						} else {
							MenuGames.getInstance().getGamesListError(values.getString("message"));
						}
					}

					@Override
					public void failed(Throwable t) {
						exceptionMessage();
					}
				});
	}

	public static void enableRandom(String id) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("id", id);

		Gdx.net.sendHttpRequest(getPost(ACTION_ENABLE_RANDOM, data),
				new HttpResponseListener() {
					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue child = values.get("data");
							String[] game = null;
							if (child != null && child.isObject()) {
								game = new String[7];
								game[0] = child.getString("game_id");
								game[1] = child.getString("name");
								game[2] = child.getString("victories");
								game[3] = child.getString("turn");
								game[4] = child.getString("state");
								game[5] = child.getString("emblem");
								game[6] = child.getString("surrender");

							}
							MenuGames.getInstance().enableRandomSuccess(game);
						} else {
							MenuGames.getInstance().enableRandomError(values.getString("message"));
						}
					}

					@Override
					public void failed(Throwable t) {
						exceptionMessage();
					}
				});
	}

	public static void sendGameTurn(String playerId, String gameId, String turnData, String result) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("player_id", playerId);
		data.put("game_id", gameId);
		if (turnData == null)
			turnData = "ended";
		data.put("data", turnData);
		if (result != null) {
			data.put("result", result);
		}

		System.out.println("Sending-> " + data);
		Gdx.net.sendHttpRequest(getPost(ACTION_GAME_TURN, data),
				new HttpResponseListener() {
					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							MenuGames.getInstance().sendGameTurnSuccess(values.getString("data"));
						} else {
							MenuGames.getInstance().sendGameTurnError(values.getString("message"));
						}
					}

					@Override
					public void failed(Throwable t) {
						exceptionMessage();
					}
				});
	}

	public static void sendSurrender(String playerId, final String gameId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("player_id", playerId);
		data.put("game_id", gameId);

		System.out.println("Send Surrender-> " + data);
		Gdx.net.sendHttpRequest(getPost(ACTION_SURRENDER, data),
				new HttpResponseListener() {
					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue child = values.get("data");
							MenuGames.getInstance().sendSurrenderSuccess(gameId,
									child.getInt("victory_total"),
									child.getInt("defeat_total"));
						} else {
							MenuGames.getInstance().sendSurrenderError();
						}
					}

					@Override
					public void failed(Throwable t) {
						exceptionMessage();
					}
				});
	}

	public static void sendUpdateUser(String id, String name, String email, int emplem) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("id", id);
		data.put("name", name);
		data.put("email", email);
		data.put("emblem", emplem + "");

		System.out.println("Sending-> " + data);
		Gdx.net.sendHttpRequest(getPost(ACTION_UPDATE_PLAYER, data),
				new HttpResponseListener() {
					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							MenuGames.getInstance().sendGameTurnSuccess(values.getString("data"));
						} else {
							MenuGames.getInstance().sendGameTurnError(values.getString("message"));
						}
					}

					@Override
					public void failed(Throwable t) {
						exceptionMessage();
					}
				});
	}

	public static void getGameTurn(String playerId, String gameId) {
		Gdx.net.sendHttpRequest(getGet(ACTION_GAME_TURN + "/p/" + playerId
				+ "/g/" + gameId), new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				JsonValue values =
						ServerDriver.ProcessResponce(httpResponse);
				if (values.getString("value").equals("ok")) {
					MenuGames.getInstance().getGameTurnSuccess(values.get("data"));
				} else {
					MenuGames.getInstance().getGameTurnError(values.getString("message"));
				}
			}

			@Override
			public void failed(Throwable t) {
				exceptionMessage();
			}
		});
	}

	private static void exceptionMessage() {
		MessageBox.build()
				.setMessage(I18n.t("server_error"), BoxButtons.One)
				.setCallback(null)
				.show();
	}

	private static HttpRequest getPost(String url, Map<String, String> data) {
		HttpRequest httpPost = new HttpRequest(HttpMethods.POST);
		httpPost.setUrl(GameController.SERVER_URL + url);
		httpPost.setContent(HttpParametersUtils.convertHttpParameters(data));
		return httpPost;
	}

	private static HttpRequest getGet(String url) {
		HttpRequest httpGet = new HttpRequest(HttpMethods.GET);
		httpGet.setUrl(GameController.SERVER_URL + url);
		return httpGet;
	}

	public static JsonValue parseJson(String response) {
		System.out.println("Parseado-> " + response);
		return new JsonReader().parse(response);
	}

	public static JsonValue ProcessResponce(HttpResponse response) {
		String res = response.getResultAsString();
		return parseJson(res);
	}
}
