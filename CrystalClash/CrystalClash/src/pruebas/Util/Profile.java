package pruebas.Util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

/**
 * The player's profile.
 */
public class Profile implements Serializable {
	private String userEmail;
	private String userPassword;

	public Profile()
	{
	}

	/**
	 * Retrieves the user email.
	 */
	public String getUserEmail()
	{
		return userEmail;
	}

	/**
	 * Retrieves the user email.
	 */
	public void setUserPassword(String userPassword)
	{
		this.userPassword = userPassword;
	}

	/**
	 * Retrieves the user email.
	 */
	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

	/**
	 * Retrieves the user email.
	 */
	public String getUserPassword()
	{
		return userPassword;
	}

	public boolean hasUserAndPassword() {
		return userPassword != null
				&& !userPassword.isEmpty()
				&& userEmail != null
				&& !userEmail.isEmpty();
	}

	// Serializable implementation

	@Override
	public void write(Json json)
	{
		json.writeValue("user_email", userEmail);
		json.writeValue("user_password", userPassword);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		userEmail = json.readValue("user_email", String.class, jsonData);
		if (userEmail == null)
			userEmail = "";

		userPassword = json.readValue("user_password", String.class, jsonData);
		if (userPassword == null)
			userPassword = "";
	}
}
