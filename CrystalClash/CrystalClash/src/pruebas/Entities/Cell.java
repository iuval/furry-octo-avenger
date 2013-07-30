package pruebas.Entities;

public class Cell extends GameObject {
	public enum State {
		NONE, ABLE_TO_ATTACK, ABLE_TO_MOVE, ABLE_TO_PLACE, ATTACK_TARGET_CENTER, ATTACK_TARGET_RADIUS, MOVE_TARGET
	}

	private State state = State.NONE;
	public int[][] neigbours;

	public State getState() {
		return state;
	}

	public void setState(State s) {
		state = s;
	}
}
