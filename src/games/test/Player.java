package games.test;

import org.newdawn.slick.Color;

import app.AppPlayer;

public class Player {

	private Color fillColor;
	private Color strokeColor;
	private int controllerID;
	private String name;

	public Player(AppPlayer appPlayer) {
		int colorID = appPlayer.getColorID();
		int controllerID = appPlayer.getControllerID();
		String name = appPlayer.getName();
		this.fillColor = AppPlayer.FILL_COLORS[colorID];
		this.strokeColor = AppPlayer.STROKE_COLORS[colorID];
		this.controllerID = controllerID;
		this.name = name;
	}

	public int getControllerID() {
		return this.controllerID;
	}

	public String getName() {
		return this.name;
	}

}
