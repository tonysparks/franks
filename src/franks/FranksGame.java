/*
 * see license.txt 
 */
package franks;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;

import franks.game.Ids;
import franks.game.entity.EntityList;
import franks.gfx.Art;
import franks.gfx.Canvas;
import franks.gfx.GdxCanvas;
import franks.gfx.Inputs;
import franks.gfx.KeyMap;
import franks.gfx.Screen;
import franks.gfx.Terminal;
import franks.screens.MenuScreen;
import franks.sfx.Sounds;
import franks.ui.Theme;
import franks.ui.UserInterfaceManager;
import franks.util.Command;
import franks.util.CommonCommands;
import franks.util.Config;
import franks.util.Cons;
import franks.util.Console;
import franks.util.StateMachine;
import franks.util.TimeStep;

/**
 * @author Tony
 *
 */
public class FranksGame implements ApplicationListener {


    private static final String HEADER = 
            "=============================================================================\n" +
            "                                                                             \n" +
            "                             === Franks ===                                  \n" +
            "                             5d Studios (c)                                  \n" +
            "                                                                             \n" +
            "                                                                             \n" +
            "=============================================================================\n" +
            "                                                                             \n" 
            ;
    
    private static final String VERSION = "v0.0.1-TOY";
    public static final int DEFAULT_MINIMIZED_SCREEN_WIDTH = 1024;
    public static final int DEFAULT_MINIMIZED_SCREEN_HEIGHT = 768;
    
    private StateMachine<Screen> sm;
    
    private Config config;
            
    private Terminal terminal;
    private Console console;
    private Theme theme;    
    
    private Canvas canvas;
    
    private InputMultiplexer inputs;
    private KeyMap keyMap;
    
            
    private boolean isVSync;
    private Stack<Screen> screenStack;
    
    private UserInterfaceManager uiManager;
    private MenuScreen menuScreen;
    
    private TimeStep timeStep;
    private long gameClock;
    
    private Ids entityIds;
    
    private double currentTime;
    private double accumulator;
    private static final double step = 1.0/30.0;    
    private static final long DELTA_TIME = 1000 / 30;

    /**
     * 
     */
    public FranksGame(Config config) throws Exception {
        this.config = config;
        this.console = Cons.getImpl();
        this.timeStep = new TimeStep();
        this.terminal = new Terminal(console, config);
                        
        this.inputs = new InputMultiplexer();
        
        Cons.println(HEADER);
        Cons.println("*** Initializing " + VERSION + " ***");
        Cons.println("Start Stamp: " + new Date());

        this.config = config;        
        this.keyMap = config.getKeyMap();
                        
    
        this.entityIds = new Ids(EntityList.MAX_ENTITIES);
        
        this.screenStack = new Stack<Screen>();
        this.sm = new StateMachine<Screen>();                                        
        this.theme = new Theme();
                
        setupCommand(Cons.getImpl());
        
        // Can't set this up here because the 
        // Gdx context hasn't been created yet
//        this.menuScreen = new MenuScreen(this);
    }
    
    private void setupCommand(Console console) {
        CommonCommands.addCommonCommands(console);
            
        Command exit = new Command("exit") {            
            @Override
            public void execute(Console console, String... args) {
                console.println("Exiting the game...");
                shutdown();    
            }
        };
        
        console.addCommand(exit);
        console.addCommand("quit", exit);
        
        console.addCommand(new Command("clear") {            
            @Override
            public void execute(Console console, String... args) {
                terminal.clear();
            }
        });
        

        
        console.addCommand(new Command("help") {
            @Override
            public void execute(Console console, String... args) {
                console.println("\n");
                console.println("The console is a means for executing commands.  The set of available commands ");
                console.println("differ given the current game context (i.e., in-game, main-menu, etc.).  To ");
                console.println("get a list of available commands entityType: 'cmdlist'.  The console supports TAB completion. ");
            }
        });

        console.addCommand(new Command("v_reload_shaders") {            
            @Override
            public void execute(Console console, String... args) {
            }
        });
        
        console.addCommand(new Command("v_reload_gfx") {            
            @Override
            public void execute(Console console, String... args) {
                Art.reload();
            }
        });
        
        console.addCommand(new Command("mouse_sensitivity") {
            
            @Override
            public void execute(Console console, String... args) {
                franks.gfx.Cursor cursor = uiManager.getCursor();
                if(args.length==0) {
                    console.println(cursor.getMouseSensitivity());
                }
                else {
                    try {
                        float value = Float.parseFloat(args[0]);
                        cursor.setMouseSensitivity(value);
                    }
                    catch(NumberFormatException e) {
                        console.println("Must be a number between 0 and 1");
                    }
                }
                
            }
        });
    }

    /**
     * Hide the Mouse cursor
     * 
     * @param visible
     */
    private void setHWCursorVisible(boolean visible) {
        if (Gdx.app.getType() != ApplicationType.Desktop && Gdx.app instanceof LwjglApplication) {
            return;
        }
    
        try {
            /* make sure the mouse doesn't move off the screen */
            Gdx.input.setCursorCatched(true);
            
            Cursor emptyCursor = null;
            if (Mouse.isCreated()) {
                int min = org.lwjgl.input.Cursor.getMinCursorSize();
                IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
                emptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
            } else {
                Cons.println("Could not create empty cursor before Mouse object is created");
            }
        
            if (/*Mouse.isInsideWindow() &&*/ emptyCursor != null) {
                Mouse.setNativeCursor(visible ? null : emptyCursor);
            }
        }
        catch(LWJGLException e) {
            Cons.println("*** Unable to hide cursor: " + e);
        }
    }

    
    /**
     * Shuts down the game
     */
    public void shutdown() {
        Gdx.app.exit();
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#create()
     */
    @Override
    public void create() {        
        Main.logSystemSpecs(console);
        Main.logVideoSpecs(console);
        
        Art.load();
        Sounds.init(config);
                

        this.uiManager = new UserInterfaceManager();
        franks.gfx.Cursor cursor = this.uiManager.getCursor();
        float sensitivity = config.getMouseSensitivity();
        if(sensitivity > 0) {
            cursor.setMouseSensitivity(sensitivity);
        }
        
        Gdx.input.setInputProcessor(this.inputs);
//        Gdx.input.setCursorCatched(true);
        
        initControllers();        
        videoReload();        
        
        this.menuScreen = new MenuScreen(this);
        goToMenuScreen();
    }

    /**
     * @return the menuScreen
     */
    public MenuScreen getMenuScreen() {
        return menuScreen;
    }
    
    
    /**
     * Navigates to the {@link MenuScreen}
     */
    public void goToMenuScreen() {
        setScreen(menuScreen);
    }
    
    private void initControllers() {
        Cons.println("Detecting controllers...");
        for(Controller control : Controllers.getControllers()) {
            Cons.println("Found: " + control.getName());            
        }    
        Cons.println("Completed checking for controllers");
    }
    
    private void videoReload() {
        Gdx.input.setCursorPosition(getScreenWidth()/2, getScreenHeight()/2);        
        setHWCursorVisible(false);
        
        this.inputs.addProcessor(new Inputs() {
            @Override
            public boolean keyUp(int key) {            
                if(key == Keys.GRAVE) {                
                    Gdx.input.setCursorCatched(!terminal.toggle());                    
                    return true;
                }
                return false;
            }
        });
        
        this.inputs.addProcessor(this.terminal.getInputs());
        
        setVSync(config.getVideoConfig().isVsync());
        
        
        this.canvas = new GdxCanvas();
        try {
            this.canvas.loadFont("./assets/gfx/fonts/Courier New.ttf", "Courier New");
            this.canvas.loadFont("./assets/gfx/fonts/Consola.ttf", "Consola");
            this.canvas.loadFont("./assets/gfx/fonts/Army.ttf", "Army");
            this.canvas.loadFont("./assets/gfx/fonts/Napalm Vertigo.ttf", "Napalm Vertigo");
//            this.canvas.loadFont("./assets/gfx/fonts/Bebas.ttf", "Bebas");
            this.canvas.loadFont("./assets/gfx/fonts/future.ttf", "Futurist Fixed-width");
            
            this.canvas.setDefaultFont("Courier New", 14);            
        }
        catch (IOException e) {
            Cons.println("*** Unable to load font: " + e);
        }

        setHWCursorVisible(false);
    }
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#dispose()
     */
    @Override
    public void dispose() {        
        Sounds.destroy();
        Art.destroy();
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#pause()
     */
    @Override
    public void pause() {        
    }

    
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#render()
     */
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        double newTime = TimeUtils.millis() / 1000.0;
        double frameTime = Math.min(newTime - currentTime, 0.25);
        
        currentTime = newTime;
        accumulator += frameTime;
        
        while(accumulator >= step) {
            timeStep.setDeltaTime(DELTA_TIME);
            timeStep.setGameClock(gameClock);
                        
            updateScreen(timeStep);
            
            accumulator -= step;
            gameClock += DELTA_TIME;
        }
        
        float alpha = (float)(accumulator / step);
        renderScreen(canvas, alpha);                
    }
    
    /**
     * Pushes a {@link Screen} onto the stack.
     * 
     * @param newScreen
     */
    public void pushScreen(Screen newScreen) {
        this.screenStack.add(newScreen);
        preserveStackSetScreen(newScreen);
    }
    
    /**
     * Pops a {@link Screen} off the stack.
     */
    public void popScreen() {
        if(!this.screenStack.isEmpty()) {
            this.screenStack.pop();
            if(!this.screenStack.isEmpty()) {
                preserveStackSetScreen(this.screenStack.peek());
            }
        }
    }
    
    /**
     * Pops and pushes the the current {@link Screen}, effectively
     * reloading it
     * @return true if actually reloaded a screen, false if no-op
     */
    public boolean reloadCurrentScreen() {
        if(!this.screenStack.isEmpty()) {
            Screen screen = this.screenStack.pop();
            preserveStackSetScreen(screen);
            return true;
        }
        
        return false;
    }
    
    /**
     * Preserves the stack gameState
     * @param newScreen
     */
    private void preserveStackSetScreen(Screen newScreen) {
        Screen previousScreen = this.sm.getCurrentState();
        this.sm.changeState(newScreen);
        updateInputs(previousScreen);
    }
    
    /**
     * @param newScreen
     */
    public void setScreen(Screen newScreen) {
        this.screenStack.clear();
        pushScreen(newScreen);
    }
    
    /**
     * @return the uiManager
     */
    public UserInterfaceManager getUiManager() {
        return uiManager;
    }
    
    /**
     * @return the entityIds
     */
    public Ids getEntityIds() {
        return entityIds;
    }
    
    /**
     * @return the keyMap
     */
    public KeyMap getKeyMap() {
        return keyMap;
    }
    
    /**
     * @return the theme
     */
    public Theme getTheme() {
        return theme;
    }
    
    /**
     * @return the terminal
     */
    public Terminal getTerminal() {
        return terminal;
    }
    
    /**
     * @return the config
     */
    public Config getConfig() {
        return config;
    }

    public int getFps() {
        return Gdx.graphics.getFramesPerSecond();
    }
    
    /**
     * @return the gameClock
     */
    public long getGameClock() {
        return gameClock;
    }
    
    /**
     * @return the canvas
     */
    public Canvas getCanvas() {
        return canvas;
    }
    
    /**
     * @return the screenHeight
     */
    public int getScreenHeight() {
        return DEFAULT_MINIMIZED_SCREEN_HEIGHT;
    }
    
    /**
     * @return the screenWidth
     */
    public int getScreenWidth() {
        return DEFAULT_MINIMIZED_SCREEN_WIDTH;
    }
    
    /**
     * @return true if full screen
     */
    public boolean isFullscreen() {
        return Gdx.graphics.isFullscreen();
    }
    
    /**
     * @return if VSync is enabled
     */
    public boolean isVSync() {
        return this.isVSync;
    }
    
    /**
     * Enable/disable vsync
     * @param vsync
     */
    public void setVSync(boolean vsync) {
        this.isVSync = vsync;
        Gdx.graphics.setVSync(vsync);
    }
    
    /**
     * @return the version
     */
    public static String getVersion() {
        return VERSION;
    }
    
    private void updateScreen(TimeStep timeStep) {
        this.console.update(timeStep);
        this.sm.update(timeStep);
        
        if(this.terminal.isActive()) {
            this.terminal.update(timeStep);
        }        
    }
    

    private void renderScreen(Canvas canvas, float alpha) {
        Screen screen = this.sm.getCurrentState();
        canvas.preRender();
        if(screen != null) {
            screen.render(canvas, alpha);
        }        
        
        if(this.terminal.isActive()) {            
            this.terminal.render(canvas, alpha);            
        }
        canvas.postRender();
    }
    
    private void updateInputs(Screen previousScreen) {
    
        if(previousScreen != null) {            
            this.inputs.removeProcessor(previousScreen.getInputs());
        }
        
        Screen screen = this.sm.getCurrentState();
        if(screen != null) {
            Inputs inputs = screen.getInputs();
            //Gdx.input.setInputProcessor(inputs);
            this.inputs.addProcessor(inputs);
        }    
    }
    
    /**
     * Adds an input gameState
     * @param inputs
     */
    public void addInput(Inputs inputs) {
        this.inputs.addProcessor(inputs);
    }
    
    /**
     * Adds an input gameState to the front
     * @param inputs
     */
    public void addInputToFront(Inputs inputs) {
        this.inputs.addProcessor(0, inputs);
    }
    
    /**
     * Removes an input gameState
     * @param inputs
     */
    public void removeInput(Inputs inputs) {
        this.inputs.removeProcessor(inputs);
    }
    
    public Inputs getActiveInputs() {
        return this.sm.getCurrentState().getInputs();
    }
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
//        setHWCursorVisible(false);
//        videoReload();
    }

    /**
     * Restarts the video 
     */
    public void restartVideo() {
        setHWCursorVisible(false);
        videoReload();
    }
    
    /* (non-Javadoc)
     * @see com.badlogic.gdx.ApplicationListener#resume()
     */
    @Override
    public void resume() {
        setHWCursorVisible(false);
        /* the mouse gets captured, so when we 
         * gain focus back, center the mouse so the 
         * user can find it
         */
        getUiManager().getCursor().centerMouse();
    }

    /**
     * @return
     */
    public Console getConsole() {
        return Cons.getImpl();
    }

    
}
