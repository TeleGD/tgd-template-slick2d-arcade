package games.test;

import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;
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
	private Font lineFont;

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
		this.lineFont = AppLoader.loadFont("/fonts/vt323.ttf", AppFont.BOLD, 48);
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
		/*
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
		 */

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
	public void render(GameContainer container, StateBasedGame game, Graphics context)
	{
		/* Méthode exécutée environ 60 fois par seconde */
		super.render(container, game, context);
		AppInput input = (AppInput) container.getInput();
		context.setColor(Color.white);
		context.setFont(this.lineFont);
		drawInput(context, "A", input.isButtonPressed(AppInput.BUTTON_A), 400, 200);
		drawInput(context,"B", input.isButtonPressed(AppInput.BUTTON_B), 450, 150);
		drawInput(context,"X", input.isButtonPressed(AppInput.BUTTON_X), 350, 150);
		drawInput(context,"Y", input.isButtonPressed(AppInput.BUTTON_Y), 400, 100);
		drawInput(context, "DOWN", input.isButtonPressed(AppInput.DPAD_DOWN), 100, 200);
		drawInput(context,"RIGHT", input.isButtonPressed(AppInput.DPAD_RIGHT), 160, 150);
		drawInput(context,"LEFT", input.isButtonPressed(AppInput.DPAD_LEFT), 50, 150);
		drawInput(context,"UP", input.isButtonPressed(AppInput.DPAD_UP), 120, 100);
		drawInput(context,"START", input.isButtonPressed(AppInput.BUTTON_START), 400, 10);
		drawInput(context,"SELECT", input.isButtonPressed(AppInput.BUTTON_SELECT), 200, 10);

		drawAxis(context, "Axis XL : ", input, AppInput.AXIS_XL, 20, 350);
		drawAxis(context, "Axis YL : ", input, AppInput.AXIS_YL, 20, 400);
		drawAxis(context, "Axis XR : ", input, AppInput.AXIS_XR, 20, 450);
		drawAxis(context, "Axis YR : ", input, AppInput.AXIS_YR, 20, 500);

		context.setColor(Color.cyan);
		context.drawOval(550, 300, 100, 100);
		float offsetX = 45 + input.getAxisValue(AppInput.AXIS_XL) * 50;
		float offsetY = 45 + input.getAxisValue(AppInput.AXIS_YL) * 50;
		context.drawOval(550 + offsetX, 300 + offsetY, 10, 10);

		context.drawOval(550, 450, 100, 100);
		offsetX = 45 + input.getAxisValue(AppInput.AXIS_XR) * 50;
		offsetY = 45 + input.getAxisValue(AppInput.AXIS_YR) * 50;
		context.drawOval(550 + offsetX, 450 + offsetY, 10, 10);
	}

	//affiche l'état d'un bouton
	private void drawInput(Graphics context, String name, boolean input, int x, int y)
	{
		context.setColor(input ? Color.green : Color.red);
		context.drawString(name, x, y);
	}

	//affiche l'état d'un axe
	private void drawAxis(Graphics context, String name, AppInput input, int axis, int x, int y)
	{
		context.setColor(input.isAxisPos(axis) ? Color.green : (input.isAxisNeg(axis) ? Color.red : Color.white));
		context.drawString(name + input.getAxisValue(axis), x, y);
	}

}