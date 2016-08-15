/*
 * see license.txt 
 */
package franks;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import franks.util.Config;
import franks.util.Logger;
import franks.util.PrintStreamLogger;



/**
 * @author Tony
 *
 */
public class Main {

	/**
	 * Client configuration file location
	 */
	private static final String CLIENT_CFG_PATH = "./assets/config.leola";		
	
	/**
	 * Loads the client configuration file.
	 * 
	 * @return the client configuration file
	 * @throws Exception
	 */
	private static Config loadConfig() throws Exception {
		Config config = new Config(CLIENT_CFG_PATH, "config");
		return config;
	}
	
	
	/**
	 * Attempts to find the best display mode
	 * @param config 
	 * @return the best display mode
	 * @throws Exception
	 */
	private static DisplayMode findBestDimensions(VideoConfig config) throws Exception {
		
		if(config.isPresent()) {
			
			int width = config.getWidth();
			int height = config.getHeight();
			
			DisplayMode[] modes = LwjglApplicationConfiguration.getDisplayModes();
			for(DisplayMode mode : modes) {
				if(mode.width == width && mode.height == height) {
					return mode;
				}
			}
		}
				
		return LwjglApplicationConfiguration.getDesktopDisplayMode();
	}
	
	/**
	 * Handle the exception -- piping out to a log file
	 * @param e
	 */
	private static void catchException(Throwable e) {
		try {
			PrintStream out = new PrintStream(new File("./seventh_error.log"));
			try {
				Logger logger = new PrintStreamLogger(out);
				logSystemSpecs(logger);
				logVideoSpecs(logger);
				
				e.printStackTrace(out);
			}
			finally {
				out.close();
			}
		}
		catch(Exception e1) {
			e1.printStackTrace();
		}
		finally {
			System.exit(1);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Config config = null;
		try {
			
			/*
			 * LibGDX spawns another thread which we don't have access
			 * to for catching its exceptions
			 */
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					if(t.getName().equals("LWJGL Application")) {
						catchException(e);
					}
				}
			});
		
			config = loadConfig();
			VideoConfig vConfig = config.getVideoConfig();
			DisplayMode displayMode = findBestDimensions(vConfig);
			
			LwjglApplicationConfiguration.disableAudio = true;			
			LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
			
			cfg.setFromDisplayMode(displayMode);
			cfg.fullscreen = vConfig.isFullscreen();
			cfg.title = "newera " + FranksGame.getVersion();
			cfg.forceExit = true;
			cfg.resizable = false;
			cfg.useGL30 = true;			
			cfg.vSyncEnabled = vConfig.isVsync();
						
			if(!cfg.fullscreen) {
				cfg.width = 1024;
				cfg.height = 768;
			}
			
			new LwjglApplication(new FranksGame(config), cfg);			
			
		}
		catch(Exception e) {
			catchException(e);
		}
		finally {
	//		System.exit(0);
			if(config!=null) {
				//config.save(CLIENT_CFG_PATH);
			}
		}
	}
	
	public static void logVideoSpecs(Logger console) {
		try {
			if(Gdx.graphics!=null) {				
				console.println("GL30: " + Gdx.graphics.isGL30Available());
				console.println("OpenGL Version: " + Gdx.gl.glGetString(GL20.GL_VERSION));
				console.println("OpenGL Vendor: " + Gdx.gl.glGetString(GL20.GL_VENDOR));
				console.println("Renderer: " + Gdx.gl.glGetString(GL20.GL_RENDERER));
				console.println("Gdx Version: " + Gdx.app.getVersion());
				console.println("Is Fullscreen: " + Gdx.graphics.isFullscreen());
			}
			else {
				console.println("OpenGL Version: " + Gdx.gl.glGetString(GL20.GL_VERSION));
				console.println("OpenGL Vendor: " + Gdx.gl.glGetString(GL20.GL_VENDOR));
				console.println("Renderer: " + Gdx.gl.glGetString(GL20.GL_RENDERER));				
			}
		}
		catch(Throwable t) {
			console.println("Error retrieving video specifications: " + t);
		}
	}

	/**
	 * Prints out system specifications
	 * 
	 * @param console
	 */
	public static void logSystemSpecs(Logger console) {
		Runtime runtime = Runtime.getRuntime();
		final long MB = 1024 * 1024;
		console.println("");
		console.println("newera: " + FranksGame.getVersion());
		console.println("Available processors (cores): " + runtime.availableProcessors());
		console.println("Free memory (MiB): " + runtime.freeMemory()/MB);
		console.println("Max memory (MiB): " + (runtime.maxMemory()==Long.MAX_VALUE ? "no limit" : Long.toString(runtime.maxMemory()/MB)) );
		console.println("Available for JVM (MiB): " + runtime.totalMemory() / MB);
		
		/* Get a list of all filesystem roots on this system */
	    File[] roots = File.listRoots();

	    /* For each filesystem root, print some info */
	    for (File root : roots) {
	      console.println("File system root: " + root.getAbsolutePath());
	      console.println("\tTotal space (MiB): " + root.getTotalSpace()/MB);
	      console.println("\tFree space (MiB): " + root.getFreeSpace()/MB);
	      console.println("\tUsable space (MiB): " + root.getUsableSpace()/MB);
	    }
		
	    
	    console.println("Java Version: " + System.getProperty("java.version"));
	    console.println("Java Vendor: " + System.getProperty("java.vendor"));
	    console.println("Java VM Version: " + System.getProperty("java.vm.version"));
	    console.println("Java VM Name: " + System.getProperty("java.vm.name"));
	    console.println("Java Class Version: " + System.getProperty("java.class.version"));
	    console.println("Java VM Spec. Version: " + System.getProperty("java.vm.specification.version"));
	    console.println("Java VM Spec. Vendor: " + System.getProperty("java.vm.specification.vendor"));
	    console.println("Java VM Spec. Name: " + System.getProperty("java.vm.specification.name"));
	    
	    console.println("OS: " + System.getProperty("os.name"));
	    console.println("OS Arch: " + System.getProperty("os.arch"));
	    console.println("OS Version: " + System.getProperty("os.version"));
	    console.println("");
	}

}
