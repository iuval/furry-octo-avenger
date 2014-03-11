package com.crystalclash.controllers;

import com.badlogic.gdx.utils.JsonValue;
import com.crystalclash.networking.ServerDriver;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.helpers.ui.BaseBox.BoxButtons;
import com.crystalclash.renders.helpers.ui.BoxCallback;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.views.MenuGamesView;

public class MenuGames {

	private static MenuGames instance;

	public static MenuGames getInstance() {
		if (instance == null)
			instance = new MenuGames();
		return instance;
	}

	public MenuGamesView render;

	private MenuGames() {
		render = MenuGamesView.getInstance(this);
	}

	public MenuGamesView getRender() {
		return render;
	}

	public void enableRandom() {
		ServerDriver.enableRandom(GameController.getUser()
				.getId());
	}

	public void getGamesList() {
		GameEngine.showLoading();
		ServerDriver.getListGames(GameController.getUser().getId());
	}

	public void getGamesListSuccess(String[][] games) {
		render.listGamesSuccess(games);
	}

	public void getGamesListError(String message) {
		render.listGamesError(message);
	}

	public void enableRandomSuccess(String[] game) {
		render.enableRandomSuccess(game);
	}

	public void enableRandomError(String message) {
		render.enableRandomError(message);
	}

	public void surrenderGame(String gameId) {
		ServerDriver.sendSurrender(GameController.getUser().getId(), gameId);
	}

	private BoxCallback logoutCallback = new BoxCallback() {

		@Override
		public boolean onEvent(int type, Object data) {
			if (type == YES)
				GameController.logOut();
			return true;
		}
	};

	public void logOut() {
		MessageBox.build()
				.setMessage("menu_games_log_out", BoxButtons.Two)
				.setCallback(logoutCallback)
				.show();
	}

	public void openTutorial() {
		GameEngine.showLoading();
		GameEngine.getInstance().openGame(null);
	}

	public void getGameTurn(String gameId) {
		GameEngine.showLoading();
		ServerDriver.getGameTurn(GameController.getUser().getId(), gameId);
	}

	public void getGameTurnSuccess(JsonValue data) {
		GameEngine.getInstance().openGame(data);
	}

	public void getGameTurnError(String string) {
		MessageBox.build()
				.setText(string) // TODO: el sever tiene que enviar comandos,
									// que aca convertimos a texto
				.setCallback(null)
				.show();
	}

	public void sendGameTurnSuccess(String data) {
		GameEngine.getInstance().openMenuGames();
	}

	public void sendSurrenderSuccess(String gameId, int v, int l) {
		GameController.getUser().setVictoryCount(v);
		GameController.getUser().setLostCount(l);
		getRender().updateListGameSurrender(gameId);
	}

	public void sendSurrenderError() {
		MessageBox.build()
				.setText("Something went wrong, try later :(")
				.setCallback(null)
				.show();
	}

	public void sendGameTurnError(String message) {
		MessageBox.build()
				.setText(message)
				.setCallback(null)
				.show();
	}
}
