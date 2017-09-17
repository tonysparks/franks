/*
 * see license.txt 
 */
package franks.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;

import franks.FranksGame;
import franks.game.CameraController;
import franks.game.GameState;
import franks.gfx.Camera;
import franks.gfx.Canvas;
import franks.gfx.Cursor;
import franks.gfx.Inputs;
import franks.gfx.KeyboardGameController;
import franks.gfx.Renderable;
import franks.gfx.Screen;
import franks.math.Rectangle;
import franks.math.Vector2f;
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
public class TestScreen implements Screen {

    private FranksGame app;
    private Camera camera;
    private Cursor cursor;
    private Button endTurnBtn;
        
    private PanelView panel;
    
    private IsometricTiledMapRenderer mapRenderer;
    private CameraController cameraController;

    private com.badlogic.gdx.graphics.OrthographicCamera mapCamera;
    
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
        public boolean scrolled(int amount) {
            if(amount < 0) mapCamera.zoom += 0.01f;
            else mapCamera.zoom -= 0.01f;
            return super.scrolled(amount);
        }
    };

    public TestScreen(FranksGame app) {
        this.app = app;
                
        GameState state = new GameState(app, false, false);
        this.camera = state.getCamera();
        
        this.cursor = app.getUiManager().getCursor();
        
        consoleCommands(app.getConsole());
        
        this.mapCamera = new OrthographicCamera(app.getScreenWidth(), app.getScreenHeight());
        this.mapCamera.setToOrtho(false, app.getScreenWidth(), app.getScreenHeight());
        
        TmxMapLoader loader = new TmxMapLoader();
        TiledMap tmap = loader.load("./assets/maps/franks_stage_03.tmx");
        
        SpriteBatch batch = new SpriteBatch();
        this.mapRenderer = new IsometricTiledMapRenderer(tmap, batch/*app.getCanvas().getSpriteBatch()*/);
        
        createUI();
        
        
    }

    private void createUI() {
        this.panel = new PanelView();
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
            }
        });
        this.panel.addElement(new ButtonView(endTurnBtn));
        app.addInput(app.getUiManager());
    }
    

    
    private void consoleCommands(Console console) {
        console.addCommand(new Command("connect") {
            
            @Override
            public void execute(Console console, String... args) {
                //game.connectToPeer(args[0]);
            }
        });
    }
    
    private Vector2f playerVelocity = new Vector2f();
    public void applyPlayerInput(float mx, float my) {
        
        this.playerVelocity.zeroOut();
        
        final float threshold = 25.0f;
        final float viewportWidth = camera.getViewPort().width;
        final float viewportHeight = camera.getViewPort().height;
        
        if(mx < threshold) {
            this.playerVelocity.x = -1;
        }
        else if(mx > viewportWidth-threshold) {
            this.playerVelocity.x = 1;
        }
        
        if(my < threshold) {
            this.playerVelocity.y = 1;
        }
        else if(my > viewportHeight-threshold) {
            this.playerVelocity.y = -1;
        }
        
        Vector2f.Vector2fMult(playerVelocity, 10f, playerVelocity);
        
        
        this.mapCamera.translate(this.playerVelocity.x, /*app.getScreenHeight()-*/this.playerVelocity.y);
    }
    
    /* (non-Javadoc)
     * @see newera.util.State#enter()
     */
    @Override
    public void enter() {
        this.endTurnBtn.show();
    }

    /* (non-Javadoc)
     * @see newera.util.State#update(newera.util.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {    
        this.panel.update(timeStep);
        
        applyPlayerInput(cursor.getX(), cursor.getY());
        
        //camera.setToOrtho(false);
        //camera.update(true);
        this.mapCamera.update();
        this.mapRenderer.setView(this.mapCamera);
        //camera.setToOrtho(true);
    }

    /* (non-Javadoc)
     * @see newera.gfx.Screen#render(newera.gfx.Canvas, float)
     */
    @Override
    public void render(Canvas canvas, float alpha) {
        this.mapRenderer.render();
        this.panel.render(canvas, camera, alpha);
                
        this.cursor.render(canvas);
    }
    
    /* (non-Javadoc)
     * @see newera.util.State#exit()
     */
    @Override
    public void exit() {
        this.endTurnBtn.hide();
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
