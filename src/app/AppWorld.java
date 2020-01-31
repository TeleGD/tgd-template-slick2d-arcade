package app;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public abstract class AppWorld extends AppState {

	private int state;

	public AppWorld(int ID) {
		super(ID);
		this.state = 0;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) {}

	public void play(GameContainer container, StateBasedGame game) {}

	public void stop(GameContainer container, StateBasedGame game) {}

	public void resume(GameContainer container, StateBasedGame game) {}

	public void pause(GameContainer container, StateBasedGame game) {}

	@Override
	public final void enter(GameContainer container, StateBasedGame game) {
		AppInput appInput = (AppInput) container.getInput();
		appInput.clearControlPressedRecord();
		if (this.state == 0) {
			this.play(container, game);
		} else if (this.state == 2) {
			this.resume(container, game);
		}
	}

	@Override
	public final void leave(GameContainer container, StateBasedGame game) {
		if (this.state == 1) {
			this.pause(container, game);
		} else if (this.state == 3) {
			this.stop(container, game);
			this.state = 0; // TODO: remove
		}
	}

	@Override
	public void poll(GameContainer container, StateBasedGame game, Input user) {}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		AppInput appInput = (AppInput) container.getInput();
		AppGame appGame = (AppGame) game;
		AppPlayer gameMaster = appGame.appPlayers.get(0);
		int gameMasterID = gameMaster.getControllerID();
		boolean BUTTON_PLUS = appInput.isButtonPressed(AppInput.BUTTON_PLUS, gameMasterID);
		int gameMasterRecord = gameMaster.getButtonPressedRecord();
		if (BUTTON_PLUS == ((gameMasterRecord & AppInput.BUTTON_PLUS) == 0)) {
			gameMasterRecord ^= AppInput.BUTTON_PLUS;
			if (BUTTON_PLUS) {
				this.state = 1;
				appGame.enterState(AppGame.PAGES_PAUSE, new FadeOutTransition(), new FadeInTransition());
			}
		}
		gameMaster.setButtonPressedRecord(gameMasterRecord);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics context) {}

	public final void setState(int state) {
		this.state = state;
	}

	public final int getState() {
		return this.state;
	}

}
