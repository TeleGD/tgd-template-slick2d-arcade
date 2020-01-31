package app;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public abstract class AppState extends BasicGameState {

	private int ID;

	public AppState(int ID) {
		this.setID(ID);
	}

	private void setID(int ID) {
		this.ID = ID;
	}

	@Override
	public final int getID() {
		return this.ID;
	}

	public void poll(GameContainer container, StateBasedGame game, Input i) {}

}
