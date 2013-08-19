package pruebas.Controllers;

import pruebas.Networking.ServerDriver;
import pruebas.Renders.GameEngine;
import pruebas.Renders.MenuGamesRender;

import com.badlogic.gdx.utils.JsonValue;

public class MenuGames {

	private static MenuGames instance;

	public static MenuGames getInstance() {
		if (instance == null)
			instance = new MenuGames();
		return instance;
	}

	public MenuGamesRender render;

	private MenuGames() {
		render = MenuGamesRender.getInstance(this);
	}

	public MenuGamesRender getRender() {
		return render;
	}

	public void enableRandom() {
		ServerDriver.enableRandom(GameController.getInstancia().getUser()
				.getId());
	}

	public void getGamesList() {
		GameEngine.showLoading();
		ServerDriver.getListGames(GameController.getInstancia().getUser().getId());
	}

	public void getGamesListSuccess(String[][] games) {
		render.listGamesSuccess(games);
	}

	public void getGamesListError(String message) {
		render.listGamesError(message);
	}

	public void enableRandomSuccess() {
		render.enableRandomSuccess();
	}

	public void enableRandomError(String message) {
		render.enableRandomError(message);
	}

	public void logOut() {
		GameController.getInstancia().logOut();
	}

	public void getGameTurn(String gameId, int turn) {
		GameEngine.showLoading();
		ServerDriver.getGameTurn(GameController.getInstancia().getUser().getId(), gameId, turn);
	}

	public void getGameTurnSuccess(JsonValue data, int turn) {
		GameEngine.getInstance().openGame(data, turn);
	}

	public void getGameTurnError(String string) {
		// TODO Auto-generated method stub
	}

	public void sendGameTurnSuccess(String data) {
		GameEngine.getInstance().openMenuGames();
	}

	public void sendGameTurnError(String message) {
		// render.listGamesError(message);
	}
}
