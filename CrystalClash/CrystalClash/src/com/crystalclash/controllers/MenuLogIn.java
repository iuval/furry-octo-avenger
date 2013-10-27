package com.crystalclash.controllers;

import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.views.MenuLogInView;

public class MenuLogIn {

	private static MenuLogIn instance;

	public static MenuLogIn getInstance() {
		if (instance == null)
			instance = new MenuLogIn();
		return instance;
	}

	private String email;
	private String password;
	private MenuLogInView render;

	private MenuLogIn() {
		render = MenuLogInView.getInstance(this);
		email = "";
		password = "";
	}

	public MenuLogInView getRender() {
		return render;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public void sendLogIn(String email, String password) {
		this.email = email;
		this.password = password;
		GameController.logIn(email, password);
	}

	public void serverError(String message) {
		MessageBox.build()
				.setMessage(message)
				.oneButtonsLayout("D:")
				.setCallback(null)
				.show();
	}

	public void sendSignIn(String email, String password) {
		this.email = email;
		this.password = password;
		GameController.signIn(email, password);
	}
}
