package games.test;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import app.AppFont;
import app.AppGame;
import app.AppInput;
import app.AppLoader;
import app.AppWorld;

public class World extends AppWorld {

	private Player[] players;
	private String log;
	private int maxLineCount;
	private List<String> lines;
	private Font lineFont;
	private int lineHeight;

	public World(int ID) {
		super(ID);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois au chargement du programme */
		super.init(container, game);
	}

	@Override
	public void play(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois au début du jeu */
		AppGame appGame = (AppGame) game;
		int n = appGame.appPlayers.size();
		this.players = new Player[n];
		for (int i = 0; i < n; i++) {
			this.players[i] = new Player(appGame.appPlayers.get(i));
		}
		this.log = "";
		this.maxLineCount = 20;
		this.lines = new ArrayList<String>();
		this.lineFont = AppLoader.loadFont("/fonts/vt323.ttf", AppFont.BOLD, 24);
		this.lineHeight = 30;
		System.out.println("PLAY");
	}

	@Override
	public void stop(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois à la fin du jeu */
		System.out.println("STOP");
	}

	@Override
	public void resume(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la reprise du jeu */
		System.out.println("RESUME");
	}

	@Override
	public void pause(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la mise en pause du jeu */
		System.out.println("PAUSE");
	}

	@Override
	public void poll(GameContainer container, StateBasedGame game, Input user) {
		/* Méthode exécutée environ 60 fois par seconde */
		super.poll(container, game, user);
		AppInput input = (AppInput) user;
		for (Player player: this.players) {
			String name = player.getName();
			int controllerID = player.getControllerID();
			for (int i = 0, l = input.getControlCount(controllerID); i < l; i++) {
				if (input.isControlPressed(1 << i, controllerID)) {
					String line = "(" + name + ").isControlPressed: " + i + "\n";
					if (this.lines.size() == this.maxLineCount) {
						this.lines.remove(0);
					}
					this.lines.add(line);
					this.log += line;
				}
			}
			for (int i = 0, l = input.getButtonCount(controllerID); i < l; i++) {
				if (input.isButtonPressed(1 << i, controllerID)) {
					String line = "(" + name + ").isButtonPressed: " + i + "\n";
					if (this.lines.size() == this.maxLineCount) {
						this.lines.remove(0);
					}
					this.lines.add(line);
					this.log += line;
				}
			}
			for (int i = 0, l = input.getAxisCount(controllerID); i < l; i++) {
				float j = input.getAxisValue(i, controllerID);
				if (j <= -.5f || j >= .5f) {
					String line = "(" + name + ").getAxisValue: " + i + " -> " + j + "\n";
					if (this.lines.size() == this.maxLineCount) {
						this.lines.remove(0);
					}
					this.lines.add(line);
					this.log += line;
				}
			}
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		/* Méthode exécutée environ 60 fois par seconde */
		super.update(container, game, delta);
		if (this.log.length() != 0) {
			System.out.print(this.log);
			this.log = "";
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics context) {
		/* Méthode exécutée environ 60 fois par seconde */
		super.render(container, game, context);
		context.setColor(Color.white);
		context.setFont(this.lineFont);
		int x = 10;
		int y = 10;
		for (String line: this.lines) {
			context.drawString(line, x, y + (this.lineHeight - this.lineFont.getHeight(line)) / 2);
			y += this.lineHeight;
		}
	}

}
