package app;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public abstract class AppGame extends StateBasedGame {

	public static final int PAGES_WELCOME = 0;
	public static final int PAGES_MENU = 1;
	public static final int PAGES_PAUSE = 2;
	public static final int PAGES_GAME = 3;

	public static final String[] TITLES = new String[] {
		"Accueil",
		"Menu",
		"Pause",
		"Jeu"
	};

	public List<AppPlayer> appPlayers;
	public List<Integer> availableColorIDs;

	public AppGame(String name, int width, int height, boolean fullscreen) {
		super(name);
		this.appPlayers = new ArrayList<AppPlayer>();
		this.availableColorIDs = new ArrayList<Integer>();
		for (int i = 0, l = AppPlayer.COLOR_NAMES.length; i < l; i++) {
			this.availableColorIDs.add(i);
		}
		try {
			AppContainer container = new AppContainer(this, width, height, fullscreen);
			container.setTargetFrameRate(60);
			container.setVSync(true);
			container.setShowFPS(false);
			container.setIcon(AppLoader.resolve("/images/icon.png"));
			container.start();
		} catch (SlickException error) {}
	}

	@Override
	public void initStatesList(GameContainer container) {
		this.init();
	}

	public abstract void init();

	public final void poll(GameContainer container, Input i) {
		((AppState) super.getCurrentState()).poll(container, this, i);
	}

}
