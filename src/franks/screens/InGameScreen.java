/*
 * see license.txt 
 */
package franks.screens;

import franks.FranksGame;
import franks.game.Game;
import franks.gfx.Camera;
import franks.gfx.Camera2d;
import franks.gfx.Canvas;
import franks.gfx.Cursor;
import franks.gfx.Inputs;
import franks.gfx.KeyboardGameController;
import franks.gfx.Renderable;
import franks.gfx.Screen;
import franks.math.Rectangle;
import franks.math.Vector2f;
import franks.sfx.Sounds;
import franks.ui.Button;
import franks.ui.Label.TextAlignment;
import franks.ui.events.ButtonEvent;
import franks.ui.events.OnButtonClickedListener;
import franks.ui.view.ButtonView;
import franks.ui.view.PanelView;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class InGameScreen implements Screen {

	private FranksGame app;
	private Game game;
	private Camera camera;
	private Cursor cursor;
	private Button endTurnBtn;
	
	private PanelView<Renderable> panel;
//	private Vector2f selectStartPos;
//	private Vector2f selectEndPos;
//	private boolean isSelecting;
	
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
		public boolean touchDragged(int x, int y, int button) {
//			if(button==0 && !isSelecting) {
//				selectStartPos.set(cursor.getCursorPos());
//				isSelecting = true;
//			}
//			
//			if (isSelecting) {
//				selectEndPos.set(cursor.getCursorPos());
//			}
			
			return mouseMoved(x,y);				
		}
		
		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if(button == 0) {
//				if(isSelecting) {
//					selectEndPos.set(cursor.getCursorPos());
//					//game.getWorld().screenToWorldCoordinates(selectEndPos, selectEndPos);
//					
//					if(game.selectRegion(selectStartPos, selectEndPos)) {
//						Sounds.playGlobalSound(Sounds.uiSelect);
//					}
//				}
//				else if(game.selectEntity()) {
//					Sounds.playGlobalSound(Sounds.uiSelect);
//				}
				if(game.selectEntity()) {
					Sounds.playGlobalSound(Sounds.uiSelect);
				}			
			}
			if(button == 1) {
				game.queueCommand();
			}
			
//			isSelecting = false;
			
			return super.touchUp(x, y, pointer, button);
		}
	};
	/**
	 * 
	 */
	public InGameScreen(FranksGame app) {
		this.app = app;
		this.camera = newCamera(1024*3, 1024*3);
		this.game = new Game(app, this.camera);
		
//		this.selectStartPos = new Vector2f();
//		this.selectEndPos = new Vector2f();
		
		this.cursor = app.getUiManager().getCursor();
		
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
		
//		if(isSelecting) {			
//			canvas.drawRect(selectStartPos.x, selectStartPos.y, 
//					(selectEndPos.x - selectStartPos.x), (selectEndPos.y - selectStartPos.y), 0xffffffff);
//		}
		
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
