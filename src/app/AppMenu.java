package app;

import app.elements.MenuItem;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;
import java.util.List;

public abstract class AppMenu extends AppPage {

	static private Font menuFont;

	static private int menuLineHeight;

	static {
		AppMenu.menuFont = AppLoader.loadFont("/fonts/vt323.ttf", AppFont.BOLD, 24);
		AppMenu.menuLineHeight = 30;
	}

	private boolean backFlag;
	private boolean forwardFlag;
	private boolean upFlag;
	private boolean downFlag;
	private boolean canMove; //vrai quand on attends un deplaceement du stick

	private List<MenuItem> menu;

	private boolean menuVisibility;

	protected int menuBoxWidth;
	protected int menuBoxHeight;
	protected int menuBoxX;
	protected int menuBoxY;

	private int menuWidth;
	private int menuHeight;
	private int menuX;
	private int menuY;

	private int menuScrollY;
	private int menuScrollHeight;
	private int itemHeight;
	private int selectedItem;

	private boolean menuBlink;
	private int menuBlinkPeriod;
	private int menuBlinkCountdown;

	public AppMenu(int ID) {
		super(ID);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) {
		super.init(container, game);

		this.backFlag = false;
		this.forwardFlag = false;
		this.upFlag = false;
		this.downFlag = false;
		this.canMove = false;

		this.menuBoxX = this.contentX;
		this.menuBoxY = this.subtitleBoxY + this.subtitleBoxHeight + AppPage.gap;
		this.menuBoxWidth = this.contentWidth;
		this.menuBoxHeight = this.hintBoxY - this.menuBoxY - AppPage.gap;

		this.itemHeight = AppMenu.menuLineHeight;

		this.menuVisibility = true;

		this.menuBlink = false;
		this.menuBlinkPeriod = 1000;
		this.menuBlinkCountdown = 0;

		this.setMenu(new ArrayList<MenuItem>());
	}

	@Override
	public final void poll(GameContainer container, StateBasedGame game, Input user) {
		super.poll(container, game, user);
		AppInput input = (AppInput) user;
		this.forwardFlag = input.isButtonPressed(AppInput.BUTTON_A);
		this.backFlag =  input.isButtonPressed(AppInput.BUTTON_B);
		this.upFlag = canMove && input.isAxisPos(AppInput.AXIS_YL);
		this.downFlag = canMove && input.isAxisNeg(AppInput.AXIS_YL);

		if(this.upFlag || this.downFlag)
			canMove = false;
		if(input.getAxisValue(AppInput.AXIS_YL) == 0)
			canMove = true;
	}

	@Override
	public final void update(GameContainer container, StateBasedGame game, int delta) {
		super.update(container, game, delta);
		if (this.forwardFlag) {
			int size = this.menu.size();
			if (size != 0) {
				this.menu.get(this.selectedItem).itemSelected();
			}
		}
		if (this.backFlag) {
			int size = this.menu.size();
			if (size != 0) {
				this.menu.get(size - 1).itemSelected();
			}
		}
		if (this.upFlag) {
			if (this.selectedItem > 0) {
				this.selectedItem--;
				if (this.selectedItem == this.menuScrollY - 1) {
					this.menuScrollY--;
				}
			} else {
				this.selectedItem = menu.size() - 1;
				this.menuScrollY = menu.size() - this.menuScrollHeight;
			}
		}
		if (this.downFlag) {
			if (this.selectedItem < menu.size() - 1) {
				this.selectedItem++;
				if (this.selectedItem == this.menuScrollY + this.menuScrollHeight) {
					this.menuScrollY++;
				}
			} else {
				this.selectedItem = 0;
				this.menuScrollY = 0;
			}
		}
		if (this.menuBlink) {
			this.menuBlinkCountdown = (this.menuBlinkCountdown + this.menuBlinkPeriod - delta) % this.menuBlinkPeriod;
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics context) {
		super.render(container, game, context);
		this.renderMenu(container, game, context);
	}

	private void renderMenu(GameContainer container, StateBasedGame game, Graphics context) {
		if (this.menuVisibility) {
			int dx = -35;
			context.setFont(AppMenu.menuFont);
			context.setColor(AppPage.foregroundColor);
			for (int i = this.menuScrollY, l = i + this.menuScrollHeight; i < l; i++) {
				int dy = this.itemHeight * (i - this.menuScrollY);
				context.drawString(this.menu.get(i).getContent(), this.menuX, this.menuY + dy);
				if (i == this.selectedItem) {
					boolean menuBlink = this.menuBlink && this.menuBlinkCountdown <= this.menuBlinkPeriod / 2;
					if (!menuBlink) {
						context.setColor(AppPage.highlightColor);
					}
					context.drawString(">>", this.menuX + dx, this.menuY + dy);
					context.drawString("<<", this.menuX + this.menuWidth - dx, this.menuY + dy);
					if (!menuBlink) {
						context.setColor(AppPage.foregroundColor);
					}
				}
			}
		}
	}

	public void setMenu(List<MenuItem>menu) {
		this.menu = new ArrayList<MenuItem>();
		this.menu.addAll(menu);
		this.selectedItem = 0;
		this.menuScrollY = 0;
		this.menuScrollHeight = Math.min(this.menuBoxHeight / this.itemHeight, this.menu.size());
		this.menuWidth = 0;
		for (MenuItem item: this.menu) {
			int width = AppMenu.menuFont.getWidth(item.getContent());
			if (width > this.menuWidth) {
				this.menuWidth = width;
			}
		}
		this.menuHeight = this.menuScrollHeight * this.itemHeight;
		this.menuX = this.menuBoxX + (this.menuBoxWidth - this.menuWidth) / 2;
		this.menuY = this.menuBoxY + (this.menuBoxHeight - this.menuHeight) / 2;
	}

	public List<MenuItem>getMenu() {
		List<MenuItem> menu = new ArrayList<MenuItem>();
		menu.addAll(this.menu);
		return menu;
	}

}
