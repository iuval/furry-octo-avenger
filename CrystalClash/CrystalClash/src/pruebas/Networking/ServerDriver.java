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

	public static void signIn(String email, String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);

		Gdx.net.sendHttpRequest(getPost(ACTION_SIGN_IN, data),
				new HttpResponseListener() {
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue data = values.get("data");
							MenuLogIn.getInstance().authenticateSuccess(
									data.getString("id"),
									data.getString("name"));
						} else {
							MenuLogIn.getInstance().authenticateError(
									values.getString("message"));
						}
					}

					public void failed(Throwable t) {
						MenuLogIn.getInstance().serverError("PANIC!");
					}
				});
	}

	public static void logIn(final String email, String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);

		Gdx.net.sendHttpRequest(getPost(ACTION_LOG_IN, data),
				new HttpResponseListener() {
					public void handleHttpResponse(HttpResponse httpResponse) {
						JsonValue values = ServerDriver
								.ProcessResponce(httpResponse);
						if (values.getString("value").equals("ok")) {
							JsonValue data = values.get("data");
							MenuLogIn.getInstance().authenticateSuccess(
									data.getString("id"), email);
						} else {
							MenuLogIn.getInstance().authenticateError(
									values.getString("message"));
						}
					}

					public void failed(Throwable t) {
						MenuLogIn.getInstance().serverError("PANIC!");
					}
				});
	}

	public static void ListGames(String id) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("id", id);

		Gdx.net.sendHttpRequest(getPost(ACTION_LIST_GAMES, data),
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
							MenuGames.getInstance().listGamesSuccess(games);
						} else {
							MenuGames.getInstance().listGamesError(
									values.getString("message"));
						}
					}

					public void failed(Throwable t) {
						MenuLogIn.getInstance().serverError("PANIC!");
					}
				});
	}

	private static HttpRequest getPost(String action, Map<String, String> data) {
		HttpRequest httpPost = new HttpRequest(HttpMethods.POST);
		httpPost.setUrl(SERVER_URL + action);
		httpPost.setContent(HttpParametersUtils.convertHttpParameters(data));
		return httpPost;
	}

	public static JsonValue ProcessResponce(HttpResponse response) {
		String res = response.getResultAsString();
		System.out.println(res);
		return new JsonReader().parse(res);
	}

}
