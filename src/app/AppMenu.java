package app;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import app.elements.MenuItem;

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
		this.backFlag = false;
		this.forwardFlag = false;
		this.upFlag = false;
		this.downFlag = false;
		AppGame appGame = (AppGame) game;
		if (appGame.appPlayers.size() != 0) {
			AppPlayer gameMaster = appGame.appPlayers.get(0);
			int gameMasterID = gameMaster.getControllerID();
			boolean BUTTON_A = input.isButtonPressed(AppInput.BUTTON_A, gameMasterID);
			boolean BUTTON_B = input.isButtonPressed(AppInput.BUTTON_B, gameMasterID);
			boolean KEY_UP = input.isControllerUp(gameMasterID);
			boolean KEY_DOWN = input.isControllerDown(gameMasterID);
			boolean BUTTON_UP = KEY_UP && !KEY_DOWN;
			boolean BUTTON_DOWN = KEY_DOWN && !KEY_UP;
			int gameMasterRecord = gameMaster.getButtonPressedRecord();
			if (BUTTON_A == ((gameMasterRecord & AppInput.BUTTON_A) == 0)) {
				gameMasterRecord ^= AppInput.BUTTON_A;
				this.forwardFlag = BUTTON_A;
			}
			if (BUTTON_B == ((gameMasterRecord & AppInput.BUTTON_B) == 0)) {
				gameMasterRecord ^= AppInput.BUTTON_B;
				this.backFlag = BUTTON_B;
			}
			int BIT_UP = 1 << (input.getButtonCount(gameMasterID) + (AppInput.AXIS_YL << 1));
			if (BUTTON_UP == ((gameMasterRecord & BIT_UP) == 0)) {
				gameMasterRecord ^= BIT_UP;
				this.upFlag = BUTTON_UP;
			}
			int BIT_DOWN = 1 << (input.getButtonCount(gameMasterID) + ((AppInput.AXIS_YL << 1) | 1));
			if (BUTTON_DOWN == ((gameMasterRecord & BIT_DOWN) == 0)) {
				gameMasterRecord ^= BIT_DOWN;
				this.downFlag = BUTTON_DOWN;
			}
			gameMaster.setButtonPressedRecord(gameMasterRecord);
		}
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
