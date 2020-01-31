package pages;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppGame;
import app.AppInput;
import app.AppLoader;
import app.AppPage;
import app.AppPlayer;

public class Welcome extends AppPage {

	private boolean backFlag;
	private boolean forwardFlag;
	private int gameMasterID;

	private Image logo;

	private boolean logoVisibility;

	protected int logoBoxWidth;
	protected int logoBoxHeight;
	protected int logoBoxX;
	protected int logoBoxY;

	private int logoWidth;
	private int logoHeight;
	private int logoX;
	private int logoY;

	private int logoNaturalWidth;
	private int logoNaturalHeight;

	public Welcome(int ID) {
		super(ID);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) {
		super.initSize(container, game, 600, 400);
		super.init(container, game);

		this.backFlag = false;
		this.forwardFlag = false;
		this.gameMasterID = AppInput.ANY_CONTROLLER;

		this.hintBoxX = this.contentX;
		this.hintBoxY = this.contentY;

		this.logoBoxX = this.contentX;
		this.logoBoxY = this.hintBoxY + this.hintBoxHeight + AppPage.gap;
		this.logoBoxWidth = this.contentWidth;
		this.logoBoxHeight = this.contentY + this.contentHeight - this.logoBoxY;

		this.logoVisibility = true;

		this.titleVisibility = false;
		this.subtitleVisibility = false;
		this.hintBlink = true;

		this.setHint("PRESS [START]");
		this.setLogo(AppLoader.loadPicture("/images/logo.png"));
	}

	@Override
	public void poll(GameContainer container, StateBasedGame game, Input user) {
		super.poll(container, game, user);
		AppInput input = (AppInput) container.getInput();
		this.backFlag = false;
		this.forwardFlag = false;
		this.gameMasterID = AppInput.ANY_CONTROLLER;
		for (int i = input.getControllerCount() - 1; i >= 0; i--) {
			if (input.isButtonPressed(AppInput.BUTTON_MINUS, i)) {
				this.backFlag = true;
				break;
			}
			if (input.isButtonPressed(AppInput.BUTTON_PLUS, i)) {
				this.forwardFlag = true;
				this.gameMasterID = i;
				break;
			}
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		super.update(container, game, delta);
		AppGame appGame = (AppGame) game;
		if (this.backFlag) {
			this.backFlag = false;
			container.exit();
		}
		if (this.forwardFlag) {
			this.forwardFlag = false;
			if (appGame.appPlayers.size() == 0) {
				int colorID = appGame.availableColorIDs.remove(0);
				String name = "Joueur " + AppPlayer.COLOR_NAMES[colorID];
				appGame.appPlayers.add(0, new AppPlayer(colorID, this.gameMasterID, name, AppInput.BUTTON_PLUS));
				appGame.enterState(AppGame.PAGES_MENU, new FadeOutTransition(), new FadeInTransition());
			}
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics context) {
		super.render(container, game, context);
		this.renderLogo(container, game, context);
	}

	private void renderLogo(GameContainer container, StateBasedGame game, Graphics context) {
		if (this.logoVisibility) {
			context.drawImage(
				this.logo,
				this.logoX,
				this.logoY,
				this.logoX + this.logoWidth,
				this.logoY + this.logoHeight,
				0,
				0,
				this.logoNaturalWidth,
				this.logoNaturalHeight
			);
		}
	}

	public void setLogo(Image logo) {
		this.logo = logo.copy();
		this.logoNaturalWidth = logo.getWidth();
		this.logoNaturalHeight = logo.getHeight();
		this.logoWidth = Math.min(Math.max(this.logoBoxWidth, 0), this.logoNaturalWidth);
		this.logoHeight = Math.min(Math.max(this.logoBoxHeight, 0), this.logoNaturalHeight);
		int a = this.logoWidth * this.logoNaturalHeight;
		int b = this.logoNaturalWidth * this.logoHeight;
		if (a < b) {
			this.logoHeight = this.logoNaturalHeight * this.logoWidth / this.logoNaturalWidth;
		} else if (b < a) {
			this.logoWidth = this.logoNaturalWidth * this.logoHeight / this.logoNaturalHeight;
		}
		this.logoX = this.logoBoxX + (this.logoBoxWidth - this.logoWidth) / 2;
		this.logoY = this.logoBoxY + (this.logoBoxHeight - this.logoHeight) / 2;
	}

	public Image getLogo() {
		return logo.copy();
	}

}
