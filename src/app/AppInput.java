package app;

import org.newdawn.slick.Input;

import java.util.ArrayList;

public class AppInput extends Input {

	public static final int BUTTON_A = 1;
	public static final int BUTTON_B = 2;
	public static final int BUTTON_X = 4;
	public static final int BUTTON_Y = 8;
	public static final int DPAD_DOWN = 16;
	public static final int DPAD_RIGHT = 32;
	public static final int DPAD_LEFT = 64;
	public static final int DPAD_UP = 128;
	public static final int BUTTON_START = 256;
	public static final int BUTTON_SELECT = 512;

	public static int AXIS_XL = 1;
	public static int AXIS_YL = 2;
	public static int AXIS_XR = 4;
	public static int AXIS_YR = 5;

	private static final int BUTTON_COUNT = 10; //seulement 4 boutons utilisés sur la manette : ABXY
	private static final int AXIS_COUNT = 6; //meme si il n't a que 4 axes utiles, il faut lire jusqu'au 6eme sur Linux

	//TODO : buttonsDown et buttonsUp ne sont pas encore implémentés
	//les boutons sont stockés sous un entier afin de récupérer les plusieurs inputs en un seul appel
	private int buttonsPressed; //les boutons enfoncés
	private int buttonsDown; //les boutons qui viennent d'être enfoncés
	private int buttonsUp; //les boutons qui viennent d'être relachés

	private float[] rawAxes;
	private float[] axes;
	private float deadZone = 0.1f; //seuil minimale du joystick

	private int[] keyboardButtons;
	private int[] keyboardAxes;

	private float scale;
	private float offsetX;
	private float offsetY;

	private float scaleX;
	private float scaleY;
	private float xoffset;
	private float yoffset;

	public AppInput(int height) {
		super(height);
		this.scaleX = 1;
		this.scaleY = 1;
		this.xoffset = 0;
		this.yoffset = 0;

		this.buttonsPressed = 0;
		this.buttonsDown = 0;
		this.buttonsUp = 0;
		this.rawAxes = new float[AXIS_COUNT];
		this.axes = new float[AXIS_COUNT];

		//Liste des boutons manette activés au clavier
		keyboardButtons = new int[] {
			KEY_SPACE, //A
			KEY_B, //B
			KEY_C, //X
			KEY_V, //Y
			KEY_G, //DPAD DOWN
			KEY_H, //DPAD RIGHT
			KEY_F, //DPAD LEFT
			KEY_T, //DPAD UP
			KEY_RETURN, //START
			KEY_BACK //SELECT
		};
		
		//Simulation des axes au clavier
		keyboardAxes = new int[]{
			KEY_D, //XL+
			KEY_Q, //XL-
			KEY_S, //YL+
			KEY_Z, //YL-
			KEY_RIGHT, //XR+
			KEY_LEFT, //XR-
			KEY_DOWN, //YR+
			KEY_UP //YR-
		};

		//corrige les axes sur windows
		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			AXIS_XL = 1;
			AXIS_YL = 0;
			AXIS_XR = 3;
			AXIS_YR = 2;
		}
	}

	//############
	//  GAMEPAD  #
	//############

	//mise a jour des inputs
	@Override
	public void poll(int width, int height) {
		super.poll(width, height);

		//détecte la manette parmis tout les controlleurs
		int gamepadIndex = -1;
		for(int i = 0; i < super.getControllerCount(); i++) {
			if(super.getAxisCount(i) > 2)
				gamepadIndex = i;
		}
		//si gamepadIndex reste égal à -1, il n'y a pas de manette branchée

		//lecture des boutons manette ABXY
		buttonsPressed = 0;
		if(gamepadIndex != -1) {
			for (int i = 0; i < 4; i++) {
				try {
					if (super.isButtonPressed (i, gamepadIndex))
						buttonsPressed |= 1 << i;
				} catch (IndexOutOfBoundsException exception) {}
			}
		}

		//permet d'actioner les boutons manette depuis le clavier
		for(int i = 0; i < keyboardButtons.length; i++)
		{
			if(super.isKeyDown(keyboardButtons[i]))
				buttonsPressed |= 1 << i;
		}

		//lecture des axes sur la manette
		for (int i = 0; i < AXIS_COUNT; i++) {
			rawAxes[i] = 0;
			if (gamepadIndex != -1 && i < super.getAxisCount(gamepadIndex))
				rawAxes[i] = super.getAxisValue(gamepadIndex, i);
		}

		//simulation des axes au clavier
		updateKeyboardAxis(AXIS_XL, keyboardAxes[0], keyboardAxes[1]);
		updateKeyboardAxis(AXIS_YL, keyboardAxes[2], keyboardAxes[3]);
		updateKeyboardAxis(AXIS_XR, keyboardAxes[4], keyboardAxes[5]);
		updateKeyboardAxis(AXIS_YR, keyboardAxes[6], keyboardAxes[7]);

		//lissage des axes et zone morte
		for(int i = 0; i < AXIS_COUNT; i++)
		{
			axes[i] = (axes[i] + rawAxes[i]) / 2.0f;
			if(Math.abs(axes[i]) < deadZone)
				axes[i] = 0;
		}
	}

	private void updateKeyboardAxis(int axis, int posKey, int negKey) {
		int posVal = super.isKeyDown(posKey) ? 1 : 0;
		int negVal = super.isKeyDown(negKey) ? -1 : 0;
		if(Math.abs(rawAxes[axis]) < deadZone) //si le stick de la manette est dans la zone morte
			rawAxes[axis] = posVal + negVal;
	}

	//verifie si un des boutons demandé est maintenu
	//il est possible de demander plusieurs boutons en meme temps avec un OR bit à bit sur les membres statiques
	public boolean isButtonPressed(int buttons) {
		for (int i = 0; i < BUTTON_COUNT; i++) {
			if ((buttons >> i & 1) == 1 && (buttonsPressed >> i & 1) == 1)
				return true;
		}
		return false;
	}

	//permet d'accéder à l'axe demandé (il faut utiliser les membres statiques)
	public float getAxisValue(int axis) {
		return axes[axis];
	}

	//verifie si l'axe est vraiment positif
	public boolean isAxisPos(int axis) {
		return axes[axis] > 0.7;
	}

	//verifie si l'axe est vraiment negatif
	public boolean isAxisNeg(int axis) {
		return axes[axis] < -0.7;
	}

	public void setDeadZone(float newVal) {
		this.deadZone = newVal;
	}

	//##############
	//  OVERRIDES  #
	//##############

	@Override
	public float getAxisValue(int axis, int controller) {
		return getAxisValue(axis);
	}

	@Override
	public boolean isButtonPressed(int buttons, int controller) {
		return isButtonPressed(buttons);
	}

	//TODO : refaire les menus pour pouvoir supprimer cette fonction
	public int getButtonCount(int id) {
		return BUTTON_COUNT;
	}

	//####################
	//  SOURIS ET ECRAN  #
	//####################

	void setCanvasClip(float scale, float offsetX, float offsetY) {
		this.scale = scale;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		float scaleX = this.scaleX / this.scale;
		float scaleY = this.scaleY / this.scale;
		float xoffset = this.xoffset - this.offsetX * scaleX;
		float yoffset = this.yoffset - this.offsetY * scaleY;
		super.setOffset(xoffset, yoffset);
		super.setScale(scaleX, scaleY);
	}

	@Override
	public int getAbsoluteMouseX() {
		return (int) ((super.getAbsoluteMouseX() - this.offsetX) / this.scale);
	}

	@Override
	public int getAbsoluteMouseY() {
		return (int) ((super.getAbsoluteMouseY() - this.offsetY) / this.scale);
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		scaleX /= this.scale;
		scaleY /= this.scale;
		float xoffset = this.xoffset - this.offsetX * scaleX;
		float yoffset = this.yoffset - this.offsetY * scaleY;
		super.setOffset(xoffset, yoffset);
		super.setScale(scaleX, scaleY);
	}

	@Override
	public void setOffset(float xoffset, float yoffset) {
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		xoffset -= this.offsetX * this.scaleX / this.scale;
		yoffset -= this.offsetY * this.scaleY / this.scale;
		super.setOffset(xoffset, yoffset);
	}

	@Override
	public void resetInputTransform() {
		this.scaleX = 1;
		this.scaleY = 1;
		this.xoffset = 0;
		this.yoffset = 0;
		float scaleX = 1 / this.scale;
		float scaleY = 1 / this.scale;
		float xoffset = -this.offsetX * scaleX;
		float yoffset = -this.offsetY * scaleY;
		super.setOffset(xoffset, yoffset);
		super.setScale(scaleX, scaleY);
	}
}
