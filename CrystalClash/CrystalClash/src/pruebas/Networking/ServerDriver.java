package pruebas.Networking;

import java.util.HashMap;
import java.util.Map;

import pruebas.Controllers.MenuLogIn;
import pruebas.Controllers.MenuMaster;

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

	public static void SignIn(String email, String password){
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", "email");
		Post("sign_in",data);
	}
	
	public static void LogIn(String email, String password){
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);
		Post("log_in",data);
	}

	private static void Post(String action, Map<String, String> data ) {
		HttpRequest httpPost = new HttpRequest(HttpMethods.POST);
		httpPost.setUrl(SERVER_URL + action);
		httpPost.setContent(HttpParametersUtils
				.convertHttpParameters(data));

		Gdx.net.sendHttpRequest(httpPost, new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				ProcessResponce(httpResponse.getResultAsString());
			}

			public void failed(Throwable t) {
				String status = "failed";
				System.out.println("failed");
				// do stuff here based on the failed attempt
			}
		});
	}

	private static void ProcessResponce(String response) {
		JsonValue root = new JsonReader().parse(response);
	//	MenuMaster.getMenuLogIn().logInCallback(response);
	}
}
