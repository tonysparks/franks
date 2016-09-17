/*
 * see license.txt 
 */
package franks.screens;

import franks.FranksGame;
import franks.game.GameState;
import franks.gfx.Canvas;
import franks.gfx.Colors;
import franks.gfx.Inputs;
import franks.gfx.RenderFont;
import franks.gfx.Renderable;
import franks.gfx.Screen;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.sfx.Sounds;
import franks.ui.Button;
import franks.ui.Panel;
import franks.ui.Theme;
import franks.ui.UserInterfaceManager;
import franks.ui.events.ButtonEvent;
import franks.ui.events.OnButtonClickedListener;
import franks.ui.view.ButtonView;
import franks.ui.view.PanelView;
import franks.util.Console;
import franks.util.TimeStep;

/**
 * The main menu screen
 * 
 * @author Tony
 *
 */
public class MenuScreen implements Screen {
	
	private FranksGame app;	
	private Theme theme;
	
	private UserInterfaceManager uiManager;
	
	private Button singlePlyBtn, multiPlyBtn
				, optionsBtn, creditsBtn, exitBtn;	
	
	private Panel menuPanel;
	private PanelView<Renderable> panelView;

	/**
	 * 
	 */
	public MenuScreen(final FranksGame app) {
		this.app = app;
		this.theme = app.getTheme();
						
		this.uiManager = app.getUiManager();
		this.panelView = new PanelView<>();
		this.menuPanel = new Panel();
				
		Vector2f uiPos = new Vector2f(app.getScreenWidth()/2, 300);
		
		this.singlePlyBtn = setupButton(uiPos, "Single Player");
		this.singlePlyBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {											
				app.setScreen(new InGameScreen(getApp(), false, true));
				
				
				//GameState state = new GameState(getApp());
				//app.setScreen(new BattleScreen(getApp(), state, null));
				
			}
		});
		
		uiPos.y += 80;
		
		this.multiPlyBtn = setupButton(uiPos, "Multiplayer Server");
		this.multiPlyBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.setScreen(new InGameScreen(getApp(), true, false));
			}
		});
		
		uiPos.y += 80;
		
		this.optionsBtn = setupButton(uiPos, "Multiplayer Client");
		this.optionsBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.setScreen(new InGameScreen(getApp(), false, false));
			}
		});
		
		uiPos.y += 80;
		
		this.creditsBtn = setupButton(uiPos, "Credits");
		this.creditsBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				//app.pushScreen(new AnimationEditorScreen(app));
			}
		});
		
		uiPos.y += 80;
		
		this.exitBtn = setupButton(uiPos, "Quit");
		this.exitBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.shutdown();
			}
		});
		
		this.menuPanel.addWidget(singlePlyBtn);
		this.menuPanel.addWidget(multiPlyBtn);
		this.menuPanel.addWidget(optionsBtn);
		this.menuPanel.addWidget(creditsBtn);
		this.menuPanel.addWidget(exitBtn);
		
		this.panelView.addElement(new ButtonView(singlePlyBtn));
		this.panelView.addElement(new ButtonView(multiPlyBtn));
		this.panelView.addElement(new ButtonView(optionsBtn));
		this.panelView.addElement(new ButtonView(creditsBtn));
		this.panelView.addElement(new ButtonView(exitBtn));
		
		
		Console console = app.getConsole();
		console.execute("help");

	}
	
	private Button setupButton(Vector2f uiPos, String text) {
		Button btn = new Button();
		btn.setText(text);
		btn.setBounds(new Rectangle(280, 80));
		btn.getBounds().centerAround(uiPos);
		btn.setForegroundColor(theme.getForegroundColor());
		btn.getTextLabel().setForegroundColor(theme.getForegroundColor());
		btn.setTheme(theme);
		btn.setEnableGradiant(false);
		return btn;
	}

	
		
	/* (non-Javadoc)
	 * @see palisma.shared.State#enter()
	 */
	@Override
	public void enter() {	
		menuPanel.show();
		Sounds.playGlobalSound(Sounds.uiNavigate);
	}
	
	/* (non-Javadoc)
	 * @see palisma.shared.State#exit()
	 */
	@Override
	public void exit() {
		menuPanel.hide();
		Sounds.playGlobalSound(Sounds.uiNavigate);
	}


	
	/* (non-Javadoc)
	 * @see palisma.shared.State#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
	}


	/* (non-Javadoc)
	 * @see palisma.client.Screen#destroy()
	 */
	@Override
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see palisma.client.Screen#render(leola.live.gfx.Canvas)
	 */
	@Override
	public void render(Canvas canvas, float alpha) {			
		canvas.fillRect(0, 0, canvas.getWidth() + 100,  canvas.getHeight() + 100, theme.getBackgroundColor());
		
		canvas.setFont(theme.getPrimaryFontName(), 18);	
		this.panelView.render(canvas, null, 0);
		
		canvas.begin();
		canvas.setFont(theme.getPrimaryFontName(), 94);
				
		int fontColor = theme.getForegroundColor();
		String message = "The Seventh";
		int center = (canvas.getWidth() - canvas.getWidth(message)) / 2;
		RenderFont.drawShadedString(canvas, message, center, canvas.getHeight()/6, fontColor);
		
		canvas.setFont(theme.getSecondaryFontName(), 12);
		RenderFont.drawShadedString(canvas, FranksGame.getVersion(), 5, canvas.getHeight()-5, Colors.setAlpha(fontColor, 150));
		
		this.uiManager.render(canvas);
				
		canvas.end();		
	}
	
	/**
	 * @return the theme
	 */
	public Theme getTheme() {
		return theme;
	}
	
	/**
	 * @return the app
	 */
	public FranksGame getApp() {
		return app;
	}
	
	/**
	 * @return the uiManager
	 */
	public UserInterfaceManager getUiManager() {
		return uiManager;
	}

	/* (non-Javadoc)
	 * @see palisma.client.Screen#getInputs()
	 */
	@Override
	public Inputs getInputs() {
		return this.uiManager;
	}

}
