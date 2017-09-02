/*
 * The Seventh
 * see license.txt 
 */
package franks.util;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import franks.VideoConfig;
import franks.gfx.KeyMap;
import leola.vm.Leola;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoDouble;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoNull;
import leola.vm.types.LeoObject;
import leola.vm.types.LeoObject.LeoType;
import leola.vm.types.LeoString;

/**
 * Configuration file
 * 
 * @author Tony
 *
 */
public class Config {
    
    private String configName;
    private String fileName;
    private LeoMap config;
    private VideoConfig videoConfig;
    private franks.gfx.KeyMap keyMap;
    
    public Config(String configName, String rootConfig) throws Exception {
        this(configName, rootConfig, new Leola());
    }
    /**
     * 
     */
    public Config(String configName, String rootConfig, Leola runtime) throws Exception {
        this.fileName = configName;
        this.configName = rootConfig;        
        this.config = loadConfig(runtime, configName, rootConfig);
        this.videoConfig = new VideoConfig(this);
        
        LeoObject controls = this.config.get("controls");
        this.keyMap = new KeyMap( (controls.isMap()) ? (LeoMap)controls : new LeoMap() );
    }
    
    private LeoMap loadConfig(Leola runtime, String file, String rootConfig) throws Exception {
        
        runtime.eval(new File(file));
        LeoMap configMap = runtime.get(rootConfig).as();    
        return configMap;
    }
        
    /**
     * @return the videoConfig
     */
    public VideoConfig getVideoConfig() {
        return videoConfig;
    }
    
    /**
     * @return the mouse sensitivity
     */
    public float getMouseSensitivity() {
        return this.getFloat("mouse_sensitivity");
    }
    
    /**
     * Sets the sensitivity
     * 
     * @param sensivity
     */
    public void setMouseSensitivity(float sensivity) {
        this.set(sensivity, "mouse_sensitivity");
    }
    
    /**
     * @return the sound volume
     */
    public float getVolume() {
        return this.getFloat("sound", "volume");
    }
    
    /**
     * @param volume
     */
    public void setVolume(float volume) {
        this.set(volume, "sound", "volume");
    }

    /**
     * The in-game debug console foreground color
     * @return The in-game debug console foreground color
     */
    public int getConsoleForegroundColor() {
        return this.getInt(0xffFFFF00, "console", "foreground_color");
    }
    
    
    /**
     * The in-game debug console background color
     * @return The in-game debug console background color
     */
    public int getConsoleBackgroundColor() {
        return this.getInt(0x8f0000FF, "console", "background_color");
    }
    
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * @return the keyMap
     */
    public franks.gfx.KeyMap getKeyMap() {
        return keyMap;
    }
    
    /**
     * @param keys
     * @return the object defined by the set of keys
     */
    public LeoObject get(String ... keys) {
        int len = keys.length;
        
        LeoObject obj = config;
        for(int i = 0; i < len; i++) {
            LeoObject nextObj = obj.getObject(LeoString.valueOf(keys[i]));            
            if(LeoObject.isTrue(nextObj)) {
                obj = nextObj;
            }
            else return null;
        }
        
        return obj;
    }
    
    public float getFloat(String ...keys ) {
        LeoObject obj = get(keys);
        if(obj!=null) {
            if(obj.isNumber()) {
                if(obj.isOfType(LeoType.REAL)) {
                    LeoDouble d = obj.as();
                    return d.asFloat();
                }
                return obj.asInt();
            }
        }
        return 0;
    }
    
    public int getInt(String ...keys ) {
        LeoObject obj = get(keys);
        return obj.asInt();
    }
    
    public int getInt(int defaultValue, String ...keys ) {
        LeoObject obj = get(keys);
        if(obj != null && obj.isNumber() ) {
            return obj.asInt();
        }
        
        return defaultValue;
    }
    
    public String getStr(String defaultValue, String ...keys ) {
        LeoObject obj = get(keys);
        if(obj != null) {
            return obj.toString();
        }
        
        return defaultValue;
    }
    
    public String getString(String ...keys ) {
        LeoObject obj = get(keys);
        return obj.toString();
    }
    
    public boolean getBool(boolean defaultValue, String ...keys) {
        LeoObject obj = get(keys);
        if(obj==null) {
            return defaultValue;
        }
        return LeoObject.isTrue(obj);
    }
    
    public boolean getBool(String ...keys ) {
        LeoObject obj = get(keys);
        return LeoObject.isTrue(obj);
    }
    
    /**
     * @param value
     * @param keys
     * @return sets a value defined by the set of keys
     */
    public boolean set(Object value, String ... keys) {
        int len = keys.length - 1;
        
        LeoObject obj = config;
        for(int i = 0; i < len; i++) {
            LeoObject nextObj = obj.getObject(keys[i]); 
            if(LeoObject.isTrue(nextObj)) {
                obj = nextObj;
            }
            else return false;
        }
        obj.$sindex(LeoString.valueOf(keys[keys.length-1]), LeoObject.valueOf(value));
        //obj.setObject(keys[keys.length-1], Leola.toLeoObject(value));
        return true;
    }
    
    public LeoObject setIfNull(String key, Object value) {
        LeoObject v = config.getByString(key); 
        if(v == null || v == LeoNull.LEONULL) {
            v = Leola.toLeoObject(value);
            config.putByString(key, v);
        }
        
        return v;
    }
    
    public boolean has(String key) {
        LeoObject v = config.getByString(key); 
        return v != null && v != LeoNull.LEONULL;
    }
    
    /**
     * Saves over this file
     * @throws IOException
     */
    public void save() throws IOException {
        save(this.fileName);
    }
    
    /**
     * Saves the configuration file
     * 
     * @param filename
     * @throws IOException
     */
    public void save(String filename) throws IOException {
        File file = new File(filename);
        RandomAccessFile output = new RandomAccessFile(file, "rw");
        try {
            output.setLength(0);
            output.writeBytes(this.configName + " = ");
            writeOutObject(output, config, 0);
        }
        finally {
            output.close();
        }
    }
    
    private void writeTabs(DataOutput output, int numTabs) throws IOException {
        for(int i = 0; i < numTabs; i++) {
            output.writeBytes("\t");
        }
    }
    
    private void writeLn(DataOutput output, String txt, int numTabs) throws IOException {
        writeTabs(output, numTabs);
        output.writeBytes(txt);
    }
    
    private void writeOutObject(DataOutput output, LeoObject obj, int numTabs) throws IOException {                
        switch(obj.getType()) {
            case ARRAY:
                LeoArray array = obj.as();                
                writeLn(output, "[\n", 0);
                for(LeoObject e : array) {
                    writeOutObject(output, e, numTabs + 1);                    
                    writeLn(output, ",\n", 0);
                }                
                writeLn(output, "]", numTabs);
                break;
            case BOOLEAN:                
                writeLn(output, obj.isTrue() ? "true" : "false", 0);
                break;
            case INTEGER:
            case LONG:
            case REAL:                
                writeLn(output, obj.getValue().toString(), 0);
                break;
            case NULL:                
                writeLn(output, "null", 0);
                break;
            case STRING:                
                writeLn(output, "\"" + obj.toString() + "\"", 0);
                break;
            case MAP:                            
                writeLn(output, "{\n", 0);
                LeoMap map = obj.as();
                for(Map.Entry<LeoObject, LeoObject> entry : map.entrySet()) {                                                            
                    writeLn(output, entry.getKey().toString() + " -> ", numTabs+1);                    
                    writeOutObject(output, entry.getValue(), numTabs+1);                                        
                    writeLn(output, ",\n", 0);
                }
                
                writeLn(output, "}", numTabs);                
                break;
            
            default: {
                throw new IllegalArgumentException(obj + " not supported in configuration!");
            }
        }
    }    
    
}
