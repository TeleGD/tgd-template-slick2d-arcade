package app;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;

public class AppContainer extends AppGameContainer {

	private static int screenWidth;
	private static int screenHeight;

	static {
		DisplayMode display = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		AppContainer.screenWidth = display.getWidth();
		AppContainer.screenHeight = display.getHeight();
	}

	private Graphics graphics;
	private int windowWidth;
	private int windowHeight;
	private int canvasWidth;
	private int canvasHeight;
	private float scale;
	private float offsetX;
	private float offsetY;
	private boolean inputToClip;
	private boolean graphicsToClip;

	public AppContainer(Game game, int width, int height, boolean fullscreen) throws SlickException {
		super(game, width, height, fullscreen);
	}

	@Override
	public void setDisplayMode(int width, int height, boolean fullscreen) throws SlickException {
		int screenWidth = AppContainer.screenWidth;
		int screenHeight = AppContainer.screenHeight;
		if (width == 0 && height == 0) {
			width = screenWidth;
			height = screenHeight;
		} else if (width == 0) {
			width = height * screenWidth / screenHeight;
		} else if (height == 0) {
			height = width * screenHeight / screenWidth;
		}
		this.windowWidth = width;
		this.windowHeight = height;
		if (fullscreen) {
			int a = screenWidth * height;
			int b = screenHeight * width;
			int scaledWidth = screenWidth;
			int scaledHeight = screenHeight;
			if (a < b) {
				this.scale = (float) scaledWidth / width;
				scaledHeight = (int) (height * this.scale);
			} else {
				this.scale = (float) scaledHeight / height;
				scaledWidth = (int) (width * this.scale);
			}
			this.offsetX = (screenWidth - scaledWidth) / 2;
			this.offsetY = (screenHeight - scaledHeight) / 2;
			this.canvasWidth = scaledWidth;
			this.canvasHeight = scaledHeight;
			width = screenWidth;
			height = screenHeight;
		} else {
			this.scale = 1;
			this.offsetX = 0;
			this.offsetY = 0;
			this.canvasWidth = width;
			this.canvasHeight = height;
		}
		super.setDisplayMode(width, height, fullscreen);
		if (super.input != null) {
			this.clipInput();
		} else {
			this.inputToClip = true;
		}
		if (this.graphics != null) {
			this.clipGraphics();
		} else {
			this.graphicsToClip = true;
		}
	}

	@Override
	public void setFullscreen(boolean fullscreen) throws SlickException {
		if (super.isFullscreen() == fullscreen) {
			return;
		}
		this.setDisplayMode(this.windowWidth, this.windowHeight, fullscreen);
		super.getDelta();
	}

	@Override
	protected void updateAndRender(int delta) throws SlickException {
		if (super.smoothDeltas) {
			if (super.getFPS() != 0) {
				delta = 1000 / super.getFPS();
			}
		}
		super.input.poll(width, height);
		Music.poll(delta);
		((AppGame) super.game).poll((GameContainer) this, (Input) super.input);
		if (!super.paused) {
			super.storedDelta += delta;
			if (super.storedDelta >= super.minimumLogicInterval) {
				try {
					if (super.maximumLogicInterval != 0) {
						long cycles = super.storedDelta / super.maximumLogicInterval;
						for (int i = 0; i < cycles; i++) {
							super.game.update(this, (int) super.maximumLogicInterval);
						}
						int remainder = (int) (super.storedDelta % super.maximumLogicInterval);
						if (remainder > super.minimumLogicInterval) {
							super.game.update(this, (int) (remainder % super.maximumLogicInterval));
							super.storedDelta = 0;
						} else {
							super.storedDelta = remainder;
						}
					} else {
						super.game.update(this, (int) super.storedDelta);
						super.storedDelta = 0;
					}
				} catch (Throwable e) {
					Log.error(e);
					throw new SlickException("Game.update() failure - check the game code.");
				}
			}
		} else {
			super.game.update(this, 0);
		}
		if (super.hasFocus() || super.getAlwaysRender()) {
			if (super.clearEachFrame) {
				((AppOutput) this.graphics).clearCanvasClip();
				FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
				GL.glGetFloat(SGL.GL_COLOR_CLEAR_VALUE, buffer);
				GL.glClearColor(0, 0, 0, 1);
				GL.glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
				GL.glLoadIdentity();
				GL.glEnable(SGL.GL_SCISSOR_TEST);
				((AppOutput) this.graphics).restoreCanvasClip();
				GL.glClearColor(buffer.get(), buffer.get(), buffer.get(), buffer.get());
				GL.glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
			} else {
				GL.glLoadIdentity();
				GL.glEnable(SGL.GL_SCISSOR_TEST);
				((AppOutput) this.graphics).restoreCanvasClip();
			}
			this.graphics.resetTransform();
			this.graphics.resetFont();
			this.graphics.resetLineWidth();
			this.graphics.setAntiAlias(false);
			try {
				super.game.render(this, this.graphics);
			} catch (Throwable e) {
				Log.error(e);
				throw new SlickException("Game.render() failure - check the game code.");
			}
			((AppOutput) this.graphics).restoreCanvasClip();
			this.graphics.resetTransform();
			if (super.isShowingFPS()) {
				super.getDefaultFont().drawString(10, 10, "FPS: " + recordedFPS);
			}
			GL.flush();
		}
		if (super.targetFPS != -1) {
			Display.sync(super.targetFPS);
		}
	}

	@Override
	protected void initGL() {
		if (super.input == null) {
			super.input = new AppInput(super.height);
		}
		if (this.inputToClip) {
			this.clipInput();
			this.inputToClip = false;
		}
		if (this.graphics != null) {
			((AppOutput) this.graphics).setDimensions(super.width, super.height);
			if (this.graphicsToClip) {
				this.clipGraphics();
				this.graphicsToClip = false;
			}
		}
		super.initGL();
	}

	protected void initSystem() throws SlickException {
		this.initGL();
		super.setMusicVolume(1f);
		super.setSoundVolume(1f);
		this.graphics = new AppOutput(super.width, super.height);
		if (this.graphicsToClip) {
			this.clipGraphics();
			this.graphicsToClip = false;
		}
		super.setDefaultFont(this.graphics.getFont());
	}

	private void clipInput() {
		((AppInput) super.input).setCanvasClip(this.scale, this.offsetX, this.offsetY);
	}

	private void clipGraphics() {
		((AppOutput) this.graphics).setCanvasClip(this.scale, this.offsetX, this.offsetY, this.canvasWidth, this.canvasHeight);
	}

	@Override
	public int getWidth() {
		return this.windowWidth;
	}

	@Override
	public int getHeight() {
		return this.windowHeight;
	}

	@Override
	@Deprecated
	public Input getInput() {
		return super.input;
	}

	@Override
	@Deprecated
	public Graphics getGraphics() {
		return this.graphics;
	}

}
