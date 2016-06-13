/*
 * see license.txt 
 */
package newera.screens;

import newera.NewEraGame;
import newera.game.Game;
import newera.gfx.Camera;
import newera.gfx.Camera2d;
import newera.gfx.Canvas;
import newera.gfx.Cursor;
import newera.gfx.Inputs;
import newera.gfx.KeyboardGameController;
import newera.gfx.Renderable;
import newera.gfx.Screen;
import newera.math.Rectangle;
import newera.math.Vector2f;
import newera.sfx.Sounds;
import newera.ui.Button;
import newera.ui.Label.TextAlignment;
import newera.ui.events.ButtonEvent;
import newera.ui.events.OnButtonClickedListener;
import newera.ui.view.ButtonView;
import newera.ui.view.PanelView;
import newera.util.TimeStep;

/**
 * @author Tony
 *
 */
public class InGameScreen implements Screen {

	private NewEraGame app;
	private Game game;
	private Camera camera;
	private Cursor cursor;
	private Button endTurnBtn;
	
	private PanelView<Renderable> panel;
	
	private Inputs inputs = new KeyboardGameController() {
		
		@Override
		public boolean mouseMoved(int x, int y) {
			cursor.moveTo(x, y);
			if(game.hoveringOverEntity()) {
				//Sounds.playGlobalSound(Sounds.uiHover);
			}
			return super.mouseMoved(x, y);
		}				
		
		@Override
		public boolean touchDragged(int x, int y, int pointer) {						
			return mouseMoved(x,y);				
		}
		
		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if(button == 0) {
				if(game.selectEntity()) {
					Sounds.playGlobalSound(Sounds.uiSelect);
				}
			}
			if(button == 1) {
				//game.dispatchCommand("moveTo");
				//game.dispatchCommand();
				game.queueCommand();
			}
			return super.touchUp(x, y, pointer, button);
		}
	};
	/**
	 * 
	 */
	public InGameScreen(NewEraGame app) {
		this.app = app;
		this.camera = newCamera(512, 512);
		this.game = new Game(app, this.camera);
		
		this.cursor = app.getUiManager().getCursor();
		
		createUI();
	}

	private void createUI() {
		this.panel = new PanelView<>();
		this.endTurnBtn = new Button();
		this.endTurnBtn.setBounds(new Rectangle(540, 470, 120, 40));
		//this.endTurnBtn.getBounds().centerAround(600, 500);
		this.endTurnBtn.setText("End Turn");
		this.endTurnBtn.setTextSize(16f);
		this.endTurnBtn.setHoverTextSize(18f);
		this.endTurnBtn.setBackgroundColor(0xffcacaca);
		this.endTurnBtn.getTextLabel().setTextAlignment(TextAlignment.CENTER);
		this.endTurnBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				game.nextTurn();
			}
		});
		this.panel.addElement(new ButtonView(endTurnBtn));
		app.addInput(app.getUiManager());
	}
	
	/**
	 * Creates a new {@link Camera}
	 * @param map
	 * @return
	 */
	private Camera newCamera(int mapWidth, int mapHeight) {
		Camera camera = new Camera2d();		
		camera.setWorldBounds(new Vector2f(mapWidth, mapHeight));		
		camera.setViewPort(new Rectangle(this.app.getScreenWidth(), this.app.getScreenHeight()));
//		camera.setMovementSpeed(new Vector2f(4000, 4000));
		camera.setMovementSpeed(new Vector2f(130, 130));
				
		return camera;
	}
	
	/* (non-Javadoc)
	 * @see newera.util.State#enter()
	 */
	@Override
	public void enter() {		
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
