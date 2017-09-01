/*
 * see license.txt 
 */
package franks.screens;

import com.badlogic.gdx.Input.Keys;

import franks.FranksGame;
import franks.game.Game;
import franks.game.GameState;
import franks.game.actions.Action.ActionType;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Cursor;
import franks.gfx.Inputs;
import franks.gfx.KeyboardGameController;
import franks.gfx.Renderable;
import franks.gfx.Screen;
import franks.math.Rectangle;
import franks.sfx.Sounds;
import franks.ui.Button;
import franks.ui.Label.TextAlignment;
import franks.ui.events.ButtonEvent;
import franks.ui.events.OnButtonClickedListener;
import franks.ui.view.ButtonView;
import franks.ui.view.PanelView;
import franks.util.Command;
import franks.util.Console;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class InGameScreen implements Screen {

	private FranksGame app;
	private Game game;
	private GameState gameState;
	private Camera camera;
	private Cursor cursor;
	private Button endTurnBtn;
		
	private PanelView<Renderable> panel;
	
	private KeyboardGameController inputs = new KeyboardGameController() {
		
		@Override
		public boolean mouseMoved(int x, int y) {
			cursor.moveTo(x, y);			
			return super.mouseMoved(x, y);
		}				
		
		@Override
		public boolean touchDragged(int x, int y, int button) {
			return mouseMoved(x,y);				
		}
		
		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if(button == 0) {
				if(game.selectEntity()) {
					Sounds.playGlobalSound(Sounds.uiSelect);
				}			
			}
						
			// TODO: Pick action type thru menu
            boolean doAction = isKeyDown(Keys.CONTROL_LEFT);
            
            if(button == 1) {
                game.dispatchCommand(doAction ? ActionType.Build : ActionType.Move);
            }
			
			return super.touchUp(x, y, pointer, button);
		}
	};
	
	
	public InGameScreen(FranksGame app) {
		this(app, true, false);
	}
	
	public InGameScreen(FranksGame app, boolean isHost, boolean isSinglePlayer) {
		this.app = app;
				
		this.gameState  = new GameState(app, isHost, isSinglePlayer);		
		this.camera = this.gameState.getCamera();
		
		this.game = this.gameState.getMetaGame();
		this.cursor = app.getUiManager().getCursor();
		
		consoleCommands(app.getConsole());
		
		
		createUI();
	}

	private void createUI() {
		this.panel = new PanelView<>();
		this.endTurnBtn = new Button();
		this.endTurnBtn.setBounds(new Rectangle(840, 700, 120, 40));
		//this.endTurnBtn.getBounds().centerAround(600, 500);
		this.endTurnBtn.setText("End Turn");
		this.endTurnBtn.setTextSize(16f);
		this.endTurnBtn.setHoverTextSize(18f);
		this.endTurnBtn.setBackgroundColor(0xffcacaca);
		this.endTurnBtn.getTextLabel().setTextAlignment(TextAlignment.CENTER);
		this.endTurnBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				game.endCurrentTurn();
			}
		});
		this.panel.addElement(new ButtonView(endTurnBtn));
		app.addInput(app.getUiManager());
	}
	

	
	private void consoleCommands(Console console) {
		console.addCommand(new Command("connect") {
			
			@Override
			public void execute(Console console, String... args) {
				if(args==null||args.length==0) {
					gameState.connectToPeer("ws://localhost:8121/socket");
				}
				else {
					gameState.connectToPeer(args[0]);
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see newera.util.State#enter()
	 */
	@Override
	public void enter() {
		this.endTurnBtn.show();
		this.game.enter();
	}

	/* (non-Javadoc)
	 * @see newera.util.State#update(newera.util.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		this.game.update(timeStep);		
		this.panel.update(timeStep);		
	}

	/* (non-Javadoc)
	 * @see newera.gfx.Screen#render(newera.gfx.Canvas, float)
	 */
	@Override
	public void render(Canvas canvas, float alpha) {		
		this.game.render(canvas, camera, alpha);		
		this.panel.render(canvas, camera, alpha);
				
		this.cursor.render(canvas);
	}
	
	/* (non-Javadoc)
	 * @see newera.util.State#exit()
	 */
	@Override
	public void exit() {
		this.endTurnBtn.hide();
		this.game.exit();
	}

	/* (non-Javadoc)
	 * @see newera.gfx.Screen#destroy()
	 */
	@Override
	public void destroy() {		
	}



	/* (non-Javadoc)
	 * @see newera.gfx.Screen#getInputs()
	 */
	@Override
	public Inputs getInputs() {
		return this.inputs;
	}

}
