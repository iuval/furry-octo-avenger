package pruebas.Networking;

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

public class ServerDriver {
	private static String SERVER_URL = "http://fuzzy-adventure.herokuapp.com/";
		
	private final static String ACTION_LOG_IN = "log_in";
	private final static String ACTION_SIGN_IN = "sign_in";

	public static HttpRequest SignIn(String email, String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);
		return getPost(ACTION_SIGN_IN, data);
	}

	public static HttpRequest LogIn(String email, String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);
		return getPost(ACTION_LOG_IN, data);
	}

	private static HttpRequest getPost(String action, Map<String, String> data) {
		HttpRequest httpPost = new HttpRequest(HttpMethods.POST);
		httpPost.setUrl(SERVER_URL + action);
		httpPost.setContent(HttpParametersUtils.convertHttpParameters(data));
		return httpPost;
	}

	public static JsonValue ProcessResponce(HttpResponse response) {
		System.out.println(response.getResultAsString());
		return new JsonReader().parse(response.getResultAsStream());
	}
}
