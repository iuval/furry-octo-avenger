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
	private boolean tutorialDone;

	public Profile()
	{
	}

	/**
	 * Sets the user email.
	 */
	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

	/**
	 * Retrieves the user email.
	 */
	public String getUserEmail()
	{
		return userEmail;
	}
	
	/**
	 * Sets the user password.
	 */
	public void setUserPassword(String userPassword)
	{
		this.userPassword = userPassword;
	}

	/**
	 * Retrieves the user password.
	 */
	public String getUserPassword()
	{
		return userPassword;
	}
	
	/**
	 * Sets the tutorial as done.
	 */
	public void setTutorialDone()
	{
		this.tutorialDone = true;
	}
	
	/**
	 * TODO: BORRAR, es solo pa probar
	 */
	public void setTutorialNotDone()
	{
		this.tutorialDone = false;
	}

	/**
	 * Retrieves true if the user has already done the turorial.
	 */
	public boolean isTutorialDone()
	{
		return tutorialDone;
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
		json.writeValue("tutorial_done", String.valueOf(tutorialDone));
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		userEmail = json.readValue("user_email", String.class, jsonData);
		if (userEmail == null)
			userEmail = "";

		userPassword = json.readValue("user_password", String.class, jsonData);
		if (userPassword == null)
			userPassword = "";
		
		String s = json.readValue("tutorial_done", String.class, jsonData);
		if (s == null)
			tutorialDone = false;
		else
			tutorialDone = Boolean.parseBoolean(s);
	}
}
