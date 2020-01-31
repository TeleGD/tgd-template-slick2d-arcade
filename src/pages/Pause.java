package pages;

import java.util.Arrays;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppMenu;
import app.AppWorld;
import app.elements.MenuItem;

public class Pause extends AppMenu {

	private int previousID;
	private int nextID;

	public Pause(int ID) {
		super(ID);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) {
		super.initSize(container, game, 600, 400);
		super.init(container, game);
		this.setTitle("Pause");
		this.setSubtitle("Le temps de prendre un goûter");
		this.setMenu(Arrays.asList(new MenuItem[] {
			new MenuItem("Retour") {
				public void itemSelected() {
					((AppWorld) game.getState(Pause.this.previousID)).setState(2);
					game.enterState(Pause.this.previousID, new FadeOutTransition(), new FadeInTransition());
				}
			},
			new MenuItem("Abandon") {
				public void itemSelected() {
					((AppWorld) game.getState(Pause.this.previousID)).setState(0);
					game.enterState(Pause.this.nextID, new FadeOutTransition(), new FadeInTransition());
				}
			}
		}));
		this.setHint("HAVE A SNACK");
	}

	public void setPreviousID(int ID) {
		this.previousID = ID;
	}

	public int getPreviousID() {
		return this.previousID;
	}

	public void setNextID(int ID) {
		this.nextID = ID;
	}

	public int getNextID() {
		return this.nextID;
	}

}
