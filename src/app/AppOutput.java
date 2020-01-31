package app;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class AppOutput extends Graphics {

	private float scale;
	private float offsetX;
	private float offsetY;
	private int canvasWidth;
	private int canvasHeight;
	private Rectangle clip;

	public AppOutput(int width, int height) {
		super(width, height);
	}

	void setDimensions(int width, int height) {
		super.screenWidth = width;
		super.screenHeight = height;
	}

	void clearCanvasClip() {
		GL.glScissor(0, 0, super.screenWidth, super.screenHeight);
	}

	void setCanvasClip(float scale, float offsetX, float offsetY, int canvasWidth, int canvasHeight) {
		this.scale = scale;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
	}

	void restoreCanvasClip() {
		GL.glScissor((int) this.offsetX, super.screenHeight - (int) this.offsetY - this.canvasHeight, this.canvasWidth, this.canvasHeight);
	}

	public void resetTransform() {
		super.resetTransform();
		super.translate(this.offsetX, this.offsetY);
		super.scale(this.scale, this.scale);
	}

	// TODO: clearWorldClip, setWorldClip, getWorldClip

	@Override
	public void clearClip() {
		this.clip = null;
		this.restoreCanvasClip();
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		if (this.clip == null) {
			this.clip = new Rectangle(x, y, width, height);
		} else {
			this.clip.setBounds(x, y, width, height);
		}
		x = Math.max((int) (x * this.scale), 0);
		y = Math.max((int) (y * this.scale), 0);
		width = Math.min((int) (width * this.scale), this.canvasWidth);
		height = Math.min((int) (height * this.scale), this.canvasHeight);
		GL.glScissor((int) this.offsetX + x, super.screenHeight - (int) this.offsetY - y - height, width, height);
	}

	@Override
	public void setClip(Rectangle rect) {
		if (rect == null) {
			this.clearClip();
			return;
		}
		this.setClip((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
	}

	@Override
	public Rectangle getClip() {
		return this.clip;
	}

}
