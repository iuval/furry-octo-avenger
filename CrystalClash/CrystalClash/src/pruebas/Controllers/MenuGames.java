package pruebas.Controllers;

import pruebas.Networking.ServerDriver;
import pruebas.renders.helpers.ui.MessageBox;
import pruebas.renders.helpers.ui.MessageBoxCallback;
import pruebas.renders.views.MenuGamesView;
import pruebas.renders.GameEngine;

import com.badlogic.gdx.utils.JsonValue;

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
		ServerDriver.sendGameTurn(GameController.getUser().getId(),
				gameId, "ended", "defeat");
	}

	private MessageBoxCallback logoutCallback = new MessageBoxCallback() {

		@Override
		public void onEvent(int type, Object data) {
			if (type == YES)
				GameController.logOut();
		}
	};

	public void logOut() {
		MessageBox.build()
				.setMessage("Farewell Commander, we await your return to the fields.")
				.twoButtonsLayout("I'll be back", "I'm not leaving")
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
				.setMessage(string)
				.oneButtonsLayout("OK...")
				.setCallback(null)
				.show();
	}

	public void sendGameTurnSuccess(String data) {
		GameEngine.getInstance().openMenuGames();
	}

	public void sendGameTurnError(String message) {
		MessageBox.build()
				.setMessage(message)
				.oneButtonsLayout("OK...")
				.setCallback(null)
				.show();
	}
}
