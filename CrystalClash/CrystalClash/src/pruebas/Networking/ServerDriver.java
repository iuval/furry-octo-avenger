package pruebas.Networking;

import java.util.HashMap;
import java.util.Map;

import pruebas.Controllers.MenuGames;
import pruebas.Controllers.MenuLogIn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class ServerDriver {
	private static String SERVER_URL = "http://fuzzy-adventure.herokuapp.com/";

	private final static String ACTION_LOG_IN = "log_in";
	private final static String ACTION_SIGN_IN = "sign_in";
	private final static String ACTION_LIST_GAMES = "list_games";
	private final static String ACTION_ENABLE_RANDOM = "enable_random";
	private final static String ACTION_GAME_TURN = "game_turn";

	public static void sendSignIn(final String email, String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);

		Gdx.net.sendHttpRequest(getPost(ACTION_SIGN_IN, data),
				new HttpResponseListener() {
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue data = values.get("data");
							MenuLogIn.getInstance().sendLogInSuccess(data.getString("id"), email);
						} else {
							MenuLogIn.getInstance().sendLogInError(values.getString("message"));
						}
					}

					public void failed(Throwable t) {
						MenuLogIn.getInstance().serverError("PANIC!");
					}
				});
	}

	public static void sendLogIn(final String email, String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);

		Gdx.net.sendHttpRequest(getPost(ACTION_LOG_IN, data),
				new HttpResponseListener() {
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue data = values.get("data");
							MenuLogIn.getInstance().sendLogInSuccess(data.getString("id"), email);
						} else {
							MenuLogIn.getInstance().sendLogInError(values.getString("message"));
						}
					}

					public void failed(Throwable t) {
						MenuLogIn.getInstance().serverError("PANIC!");
					}
				});
	}

	public static void getListGames(String id) {
		Gdx.net.sendHttpRequest(getGet(ACTION_LIST_GAMES + "/p/" + id),
				new HttpResponseListener() {
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue data = values.get("data");
							String[][] games = new String[data.size][4];
							JsonValue child;
							for (int i = 0; i < games.length; i++) {
								child = data.get(i);
								games[i][0] = child.getString("game_id");
								games[i][1] = child.getString("name");
								games[i][2] = child.getString("turn");
								games[i][3] = child.getString("state");
							}
							MenuGames.getInstance().getGamesListSuccess(games);
						} else {
							MenuGames.getInstance().getGamesListError(values.getString("message"));
						}
					}

					public void failed(Throwable t) {
						MenuLogIn.getInstance().serverError("PANIC!");
					}
				});
	}

	public static void enableRandom(String id) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("id", id);

		Gdx.net.sendHttpRequest(getPost(ACTION_ENABLE_RANDOM, data),
				new HttpResponseListener() {
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							MenuGames.getInstance().enableRandomSuccess();
						} else {
							MenuGames.getInstance().enableRandomError(values.getString("message"));
						}
					}

					public void failed(Throwable t) {
						MenuLogIn.getInstance().serverError("PANIC!");
					}
				});
	}

	public static void sendGameTurn(String playerId, String gameId, int player,
			String turnData, String result) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("player_id", playerId);
		data.put("game_id", gameId);
		data.put("player", player + "");
		if (turnData != null)
			data.put("result", turnData);
		else
			data.put("data", "ended");
		if (result != null) {
			data.put("result", result);
		}

		System.out.println("Sending-> " + turnData);
		Gdx.net.sendHttpRequest(getPost(ACTION_GAME_TURN, data),
				new HttpResponseListener() {
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							MenuGames.getInstance().sendGameTurnSuccess(values.getString("data"));
						} else {
							MenuGames.getInstance().sendGameTurnError(values.getString("message"));
						}
					}

					public void failed(Throwable t) {
						MenuLogIn.getInstance().serverError("PANIC!");
					}
				});
	}

	public static void getGameTurn(String playerId, String gameId, final int turn) {
		Gdx.net.sendHttpRequest(getGet(ACTION_GAME_TURN + "/p/" + playerId
				+ "/g/" + gameId), new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				JsonValue values =
						ServerDriver.ProcessResponce(httpResponse);
				if (values.getString("value").equals("ok")) {
					MenuGames.getInstance().getGameTurnSuccess(values.get("data"), turn);
				} else {
					MenuGames.getInstance().getGameTurnError(values.getString("message"));
				}
			}

			public void failed(Throwable t) {
				MenuLogIn.getInstance().serverError("PANIC!");
			}
		});
	}

	private static HttpRequest getPost(String url, Map<String, String> data) {
		HttpRequest httpPost = new HttpRequest(HttpMethods.POST);
		httpPost.setUrl(SERVER_URL + url);
		httpPost.setContent(HttpParametersUtils.convertHttpParameters(data));
		return httpPost;
	}

	private static HttpRequest getGet(String url) {
		HttpRequest httpGet = new HttpRequest(HttpMethods.GET);
		httpGet.setUrl(SERVER_URL + url);
		return httpGet;
	}

	public static JsonValue parseJson(String response) {
		System.out.println("Parseado->" + response);
		return new JsonReader().parse(response);
	}

	public static JsonValue ProcessResponce(HttpResponse response) {
		String res = response.getResultAsString();
		return parseJson(res);
	}
}
