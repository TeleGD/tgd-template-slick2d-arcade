package app;

import org.newdawn.slick.Color;

public class AppPlayer {

	static public final int GOLD = 0;
	static public final int PINK = 1;
	static public final int BLUE = 2;
	static public final int LIME = 3;
	static public final int PERU = 4;
	static public final int PLUM = 5;
	static public final int TEAL = 6;
	static public final int GRAY = 7;

	static public final String[] COLOR_NAMES = new String[] {
		"Gold",
		"Pink",
		"Blue",
		"Lime",
		"Peru",
		"Plum",
		"Teal",
		"Gray"
	};

	static public final Color[] FILL_COLORS = new Color[] {
		new Color(1f, .8f, .2f),
		new Color(1f, .6f, .6f),
		new Color(.4f, .6f, .8f),
		new Color(.2f, .6f, .2f),
		new Color(.8f, .6f, .2f),
		new Color(.8f, .6f, .8f),
		new Color(.2f, .6f, .6f),
		new Color(.6f, .6f, .6f)
	};

	static public final Color[] STROKE_COLORS = new Color[] {
		new Color(.8f, .6f, 0f),
		new Color(.8f, .4f, .4f),
		new Color(.2f, .4f, .6f),
		new Color(0f, .4f, 0f),
		new Color(.6f, .4f, 0f),
		new Color(.6f, .4f, .6f),
		new Color(0f, .4f, .4f),
		new Color(.4f, .4f, .4f)
	};

	private int colorID;
	private int controllerID;
	private String name;
	private int buttonPressedRecord;

	public AppPlayer(int colorID, int controllerID, String name, int buttonPressedRecord) {
		this.setColorID(colorID);
		this.setControllerID(controllerID);
		this.setName(name);
		this.setButtonPressedRecord(buttonPressedRecord);
	}

	public void setColorID(int colorID) {
		this.colorID = colorID;
	}

	public int getColorID() {
		return this.colorID;
	}

	public void setControllerID(int controllerID) {
		this.controllerID = controllerID;
	}

	public int getControllerID() {
		return this.controllerID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setButtonPressedRecord(int buttonPressedRecord) {
		this.buttonPressedRecord = buttonPressedRecord;
	}

	public int getButtonPressedRecord() {
		return this.buttonPressedRecord;
	}

}
