import app.AppGame;

public final class Main {

	public static final void main(String[] arguments) {
		String title = "TGD Arcade Input";
		int height = 720;
		boolean fullscreen = false;
		new AppGame(title, height, fullscreen) {
			@Override
			public void init() {
				this.addState(new pages.Welcome(AppGame.PAGES_WELCOME));
				this.addState(new pages.Menu(AppGame.PAGES_MENU));
				this.addState(new pages.Pause(AppGame.PAGES_PAUSE));
				this.addState(new games.test.World(AppGame.PAGES_GAME));
			}
		};
	}

}
