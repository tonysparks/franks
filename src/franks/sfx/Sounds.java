/*
 * see license.txt 
 */
package franks.sfx;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import franks.math.Vector2f;
import franks.util.Command;
import franks.util.Config;
import franks.util.Cons;
import franks.util.Console;
import paulscode.sound.ListenerData;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

/**
 * @author Tony
 *
 */
public class Sounds {

//	private static ExecutorService service = Executors.newCachedThreadPool();
	public static final long uiChannel = 2004 >> 2;
							
	public static final int[] die = {0,1,2};
	public static final int[] hit = {3,4,5};
				
	public static final int[] uiHover = {6};
	public static final int[] uiSelect = {7};
	
	public static final int[] logAlert = {8};
	
	public static final int[] ruffle = {9,10,11};
	
	public static final int[] meleeSwing = {12,13};
	public static final int[] meleeHit = {14,15};
	
	public static final int[] normalWalk = {16,17,18,19};
	public static final int[] dirtWalk = {20,21,22,23};
	public static final int[] grassWalk = {24,25,26,27};
	public static final int[] metalWalk = {28,29,30,31};
	public static final int[] waterWalk = {32,33,34,35};
	public static final int[] woodWalk = {36,37,38,39};
	
	public static final int[] uiNavigate = {40,41};
	public static final int[] uiKeyType = {42,43,44};
		
	public static final int[] flagCaptured = {45};
	public static final int[] flagStolen = {46};
	public static final int[] flagReturned = {47};
	public static final int[] enemyFlagCaptured = {48};
	public static final int[] enemyFlagStolen = {49};
	
	public static final int[] build = {50};
		
	private static final Random random = new Random();
	private static Map<String, SoundBuffer> loadedSounds = new ConcurrentHashMap<>();
	private static Sound[][] channels = new Sound[16][];
	private static float volume = 0.1f;
	private static Vector2f listenerPosition = new Vector2f();
	private static Config config;
	
	private static Sound[] createChannel() {
		return new Sound[] {					
			loadSound("./assets/sfx/player/die1.wav") ,   // 0
			loadSound("./assets/sfx/player/die2.wav") ,   // 1
			loadSound("./assets/sfx/player/die3.wav") ,   // 2
			loadSound("./assets/sfx/player/hit1.wav") ,   // 3
			loadSound("./assets/sfx/player/hit2.wav") ,   // 4
			loadSound("./assets/sfx/player/hit3.wav") ,   // 5												
					
			loadSound("./assets/sfx/ui/element_hover.wav") ,   // 6,
			loadSound("./assets/sfx/ui/element_select.wav") ,   // 7,
			
			// UI stuff
			loadSound("./assets/sfx/log_alert.wav"), // 8
															
			// misc.			
			loadSound("./assets/sfx/player/ruffle1.wav"), // 9
			loadSound("./assets/sfx/player/ruffle2.wav"), // 10
			loadSound("./assets/sfx/player/ruffle3.wav"), // 11
						
			loadSound("./assets/sfx/melee/melee_swing01.wav") ,   // 12
			loadSound("./assets/sfx/melee/melee_swing02.wav") ,   // 13
			loadSound("./assets/sfx/melee/melee_hit01.wav") ,   // 14
			loadSound("./assets/sfx/melee/melee_hit02.wav") ,   // 15
			
			// footsteps
			loadSound("./assets/sfx/player/footsteps/foot_normal01.wav") ,   // 16
			loadSound("./assets/sfx/player/footsteps/foot_normal02.wav") ,   // 17
			loadSound("./assets/sfx/player/footsteps/foot_normal03.wav") ,   // 18
			loadSound("./assets/sfx/player/footsteps/foot_normal04.wav") ,   // 19
			
			loadSound("./assets/sfx/player/footsteps/foot_dirt01.wav") ,   // 20
			loadSound("./assets/sfx/player/footsteps/foot_dirt02.wav") ,   // 21
			loadSound("./assets/sfx/player/footsteps/foot_dirt03.wav") ,   // 22
			loadSound("./assets/sfx/player/footsteps/foot_dirt04.wav") ,   // 23
			
			loadSound("./assets/sfx/player/footsteps/foot_grass01.wav") ,   // 24
			loadSound("./assets/sfx/player/footsteps/foot_grass02.wav") ,   // 25
			loadSound("./assets/sfx/player/footsteps/foot_grass03.wav") ,   // 26
			loadSound("./assets/sfx/player/footsteps/foot_grass04.wav") ,   // 27
			
			loadSound("./assets/sfx/player/footsteps/foot_metal01.wav") ,   // 28
			loadSound("./assets/sfx/player/footsteps/foot_metal02.wav") ,   // 29
			loadSound("./assets/sfx/player/footsteps/foot_metal03.wav") ,   // 30
			loadSound("./assets/sfx/player/footsteps/foot_metal04.wav") ,   // 31
			
			loadSound("./assets/sfx/player/footsteps/foot_water01.wav") ,   // 32
			loadSound("./assets/sfx/player/footsteps/foot_water02.wav") ,   // 33
			loadSound("./assets/sfx/player/footsteps/foot_water03.wav") ,   // 34
			loadSound("./assets/sfx/player/footsteps/foot_water04.wav") ,   // 35
			
			loadSound("./assets/sfx/player/footsteps/foot_wood01.wav") ,   // 36
			loadSound("./assets/sfx/player/footsteps/foot_wood02.wav") ,   // 37
			loadSound("./assets/sfx/player/footsteps/foot_wood03.wav") ,   // 38
			loadSound("./assets/sfx/player/footsteps/foot_wood04.wav") ,   // 39
			
			loadSound("./assets/sfx/ui/navigate01.wav") ,   // 40
			loadSound("./assets/sfx/ui/navigate02.wav") ,   // 41
			
			loadSound("./assets/sfx/ui/key_type01.wav") ,   // 42
			loadSound("./assets/sfx/ui/key_type02.wav") ,   // 43
			loadSound("./assets/sfx/ui/key_type03.wav") ,   // 44			
			
            loadSound("./assets/sfx/ctf/flag_captured.wav") ,   // 45
            loadSound("./assets/sfx/ctf/flag_stolen.wav") ,   // 46
            loadSound("./assets/sfx/ctf/flag_returned.wav") ,   // 47
            loadSound("./assets/sfx/ctf/enemy_flag_captured.wav") ,   // 48
            loadSound("./assets/sfx/ctf/enemy_flag_stolen.wav") ,   // 49
            
            loadSound("./assets/sfx/player/build.wav") ,   // 50 
		};
	};

	private static SoundSystem soundSystem;
	
	public static void init(Config cfg) {
		try {
			Cons.println("Initializing the sound subsystem...");
			Cons.getImpl().addCommand(getVolumeCommand());
			
			config = cfg;
			volume = config.getVolume();
			
			SoundSystemConfig.setMasterGain(volume);					
			SoundSystemConfig.setLogger(new SoundSystemLogger() {
				@Override
				public void errorMessage(String message, String error, int code) {
					Cons.println("*** Error in the sound system: " + message + " (" + error + ") :: " + code);
				}
				@Override
				public void message(String message, int code) {
					Cons.println("*** Sound system: " + message + " :: " + code);
				}
				
				@Override
				public void importantMessage(String message, int code) {
					Cons.println("*** (I) Sound system: " + message + " :: " + code);
				}
			});
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec( "wav", CodecWav.class );
			SoundSystemConfig.setDefaultFadeDistance(10000f);
			
			soundSystem = new SoundSystem(LibraryLWJGLOpenAL.class);
			setVolume(volume);		
			
			for(int i = 0; i < channels.length; i++) {
				channels[i] = createChannel();
			}
			
			Cons.println("Sound system online!");
		}
		catch(SoundSystemException e) {
			Cons.println("Unable to initialize the sound plugins.");
		}
	}
	
	/**
	 * @param volume the volume to set
	 */
	public static void setVolume(float volume) {
		Sounds.volume = volume;		
		soundSystem.setMasterVolume(volume);
		if(config!=null) {			
			config.setVolume(volume);
		}
	}
	
	/**
	 * @return the volume
	 */
	public static float getVolume() {
		return volume;
	}
	
	
	private static Command getVolumeCommand() {
		return new Command("volume") {
			
			@Override
			public void execute(Console console, String... args) {
				if(args == null || args.length < 1) {
					console.println(volume);
				}
				else {
					try {
						float v = Float.parseFloat(args[0]);
						setVolume(v);
					}
					catch(Exception e) {
						console.println("*** Must be a number between 0 and 1");
					}
				}
			}
		};
	}
	
	public static void setPosition(Vector2f pos) {
		if(soundSystem!=null) {
			listenerPosition.set(pos);
			soundSystem.setListenerPosition(pos.x, pos.y, 0);
		}
	}
	
	public static Vector2f getPosition() {
		return listenerPosition;
	}
	
	public static synchronized void destroy() {
		if(soundSystem!=null) {
			for(SoundBuffer sound : loadedSounds.values()) {
				sound.destroy();
			}
			loadedSounds.clear();
			
			soundSystem.removeTemporarySources();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			try {soundSystem.cleanup(); } catch(Exception e) {}
			
		}
	}
	
	
	
	/**
	 * Attempts to load a {@link Sound}
	 * 
	 * @param soundFile
	 * @return the {@link Sound} if loaded successfully
	 */
	public static synchronized Sound loadSound(String soundFile) {
		try {			
			Sound sound = null;
			if(loadedSounds.containsKey(soundFile)) {
				sound = loadedSounds.get(soundFile).newSound();
			}
			else {
				SoundBuffer buffer = new SoundBuffer(soundSystem, soundFile, random);
				loadedSounds.put(soundFile, buffer);
				
				sound = buffer.newSound();
			}
			
			sound.setVolume(volume);
			return sound;
		}
		catch(Exception e) {
			Cons.println("*** Error loading sound: " + soundFile + " - " + e);
		}
		
		return null;
	}
	
	
	public static Sound findFreeSound(int soundIndex) {
		for(int i = 0; i < channels.length; i++) {
			Sound[] sounds = channels[i];		
			Sound sound = sounds[soundIndex];
			if(!sound.isPlaying()) {
				return sound;
			}
		}		
		return null;
	}
	
	public static Sound startPlaySound(int[] soundBank, long channelId, Vector2f pos) {
		return startPlaySound(soundBank, channelId, pos.x, pos.y);
	}
	
	public static Sound startPlaySound(int[] soundBank, long channelId, float x, float y) {
		int index = random.nextInt(soundBank.length);
		int soundIndex = soundBank[index];
		
		Sound[] sounds = channels[ (int)channelId % channels.length];		
		Sound sound = sounds[soundIndex];
		
		sound.setVolume(volume); // TODO global config
		sound.play(x,y);	
		return sound;
	}
		
	public static Sound playGlobalSound(SoundType type) {
		return playGlobalSound(type, 1.0f);
	}
	public static Sound playGlobalSound(SoundType type, float damp) {
		return playGlobalSound(soundBank(type), damp);
	}
	
	
	public static Sound playGlobalSound(int[] soundBank) {
		return playGlobalSound(soundBank, 1.0f);
	}
	
	/**
	 * Plays the sound right next to the sound gameState so it is
	 * always audible.
	 * @param soundBank
	 * @return the {@link Sound}
	 */
	public static Sound playGlobalSound(int[] soundBank, float damp) {
		float x = 0;
		float y = 0;
		if(soundSystem != null) {
			ListenerData data = soundSystem.getListenerData();
			x = data.position.x;
			y = data.position.y;
		}
		return playFreeSound(soundBank, x, y, damp);
	}
	
	/**
	 * Plays a sound at a particular position.
	 * 
	 * @param soundBank
	 * @param channelId
	 * @param pos
	 * @return
	 */
	public static Sound playSound(int[] soundBank, long channelId, Vector2f pos) {
		return playSound(soundBank, channelId, pos.x, pos.y);
	}
	
	
	/**
	 * Plays a sound at a particular position.
	 * 
	 * @param soundBank
	 * @param channelId
	 * @param x
	 * @param y
	 * @return
	 */
	public static Sound playSound(int[] soundBank, long channelId, float x, float y) {
		int index = random.nextInt(soundBank.length);
		int soundIndex = soundBank[index];
		
		Sound[] sounds = channels[ (int)channelId % channels.length];		
		Sound sound = sounds[soundIndex];
		if(!sound.isPlaying()) {
			sound.setVolume(volume); // TODO global config
			sound.play(x,y);
		}
		
		return sound;
	}

	public static Sound playFreeSound(int[] soundBank, Vector2f pos) {
		return playFreeSound(soundBank, pos.x, pos.y);
	}
	
	public static Sound playFreeSound(int[] soundBank, float x, float y) {
		return playFreeSound(soundBank, x, y, 1.0f);
	}
	
	public static Sound playFreeSound(int[] soundBank, float x, float y, float damp) {
		if(soundBank != null) {
			int index = random.nextInt(soundBank.length);
			int soundIndex = soundBank[index];
			Sound snd = findFreeSound(soundIndex);
			if(snd!=null) {
				snd.setVolume(volume*damp); // TODO global config
				snd.play(x,y);
			}
			return snd;
		}
		
		return null;
	}
	
	public static Sound playSound(byte soundId, float x, float y) {
		return playSound(soundId, x, y, 1.0f);
	}
	
	public static Sound playSound(byte soundId, float x, float y, float damp) {
		SoundType type = SoundType.fromNet(soundId);
		return playSound(type, x, y, damp);
	}
	
	/**
	 * @param type
	 * @return the corresponding sound bank for the {@link SoundType}
	 */
	public static int[] soundBank(SoundType type) {
		int[] sound = null;
		switch(type) {
			
		case SURFACE_GRASS:			
			sound = grassWalk;			
			break;
		case SURFACE_METAL:
			sound = metalWalk;
			break;
		case SURFACE_NORMAL:
			sound = normalWalk;
			break;
		case SURFACE_WATER:
			sound = waterWalk;
			break;
		case SURFACE_WOOD:
			sound = woodWalk;
			break;
		case SURFACE_DIRT: 
			sound = dirtWalk;
			break;
		case SURFACE_SAND: 
			sound = dirtWalk;
			break;

		case RUFFLE:
			sound = ruffle;
			break;
		case MELEE_SWING:
			sound = meleeSwing;
			break;
		case MELEE_HIT:
			sound = meleeHit;
			break;

		case UI_ELEMENT_HOVER:
			sound = uiHover;
			break;
		case UI_ELEMENT_SELECT:
			sound = uiSelect;
			break;			
		case UI_NAVIGATE:
			sound = uiNavigate;
			break;				
		case UI_KEY_TYPE:
			sound = uiKeyType;
			break;
		
		case IMPACT_FLESH:
			sound = hit;
			break;			
		
		case ENEMY_FLAG_CAPTURED:
			sound = enemyFlagCaptured;
		    break;
		case ENEMY_FLAG_STOLEN:
			sound = enemyFlagStolen;
		    break;
		case FLAG_CAPTURED:
			sound = flagCaptured;
		    break;
		case FLAG_RETURNED:
			sound = flagReturned;
		    break;
		case FLAG_STOLEN:
			sound = flagStolen;
		    break;
		
		case MUTE:
			
		default:
			break;
		}
		return sound;
	}
	
	public static Sound playSound(SoundType type, float x, float y) {
		return playSound(type, x, y, 1.0f);
	}
	
	
	/**
	 * Play a sound at the specified location
	 * 
	 * @param type
	 * @param x
	 * @param y
	 * @return the {@link Sound}
	 */
	public static Sound playSound(SoundType type, float x, float y, float damp) {
		Sound sound = null;
		int[] soundBank = soundBank(type);
		if(soundBank != null) {
			sound = playFreeSound(soundBank, x, y, dampSound(type, damp));
		}
		
		return sound;
	}

	private static float dampSound(SoundType type, float damp) {
		// avoid these from being too loud and
		// annoying
		switch(type) {
			case SURFACE_WOOD:
			case SURFACE_GRASS:
			case SURFACE_METAL:
			case SURFACE_WATER:
			case SURFACE_SAND:				
				return damp * 0.22f;
			case TANK_FIRE:
			case TANK_IDLE:
			case TANK_MOVE:
			case TANK_OFF:
			case TANK_ON:
			case TANK_REV_DOWN:
			case TANK_REV_UP:
			case TANK_SHIFT:
			case TANK_TURRET_MOVE:
				return damp * 1.82f;
			default:
		}
		return damp;
	}
}
