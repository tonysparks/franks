/*
 * see license.txt 
 */
package franks.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import franks.util.Cons;

/**
 * @author Tony
 *
 */
public class Shader {

    private ShaderProgram shader;
    private String vert, frag;
    
    /**
     * @param vert
     * @param frag
     */
    public Shader(String vert, String frag) {
        this.vert = vert;
        this.frag = frag;
        
        reload();
    }
    
    /**
     * Reloads the shader from disk
     */
    public void reload() {
        shader = new ShaderProgram(Gdx.files.internal(vert), Gdx.files.internal(frag));
    
        if (!shader.isCompiled()) {
            Cons.println("Unable to compile shaders: " + vert + " : " + frag);
        }

        // Good idea to log any warnings if they exist
        if (shader.getLog().length() != 0) {
            Cons.println(shader.getLog());
        }
    }
    public Shader begin() {
        ShaderProgram shader = getShader();
        shader.begin();        
        return this;
    }
    
    public Shader end() {
        ShaderProgram shader = getShader();
        shader.end();        
        return this;
    }
    
    public Shader setParam(String name, float v) {
        ShaderProgram shader = getShader();        
        shader.setUniformf(name, v);                
        return this;
    }
    
    public Shader setParam(String name, float v1, float v2) {
        ShaderProgram shader = getShader();        
        shader.setUniformf(name, v1, v2);            
        return this;
    }
    
    /**
     * Destroys this shader
     */
    public void destroy() {
        try {
            getShader().dispose();
        }
        catch(Exception ignore) {}
    }
    
    /**
     * @return the shader
     */
    public ShaderProgram getShader() {
        return shader;
    }
    
    
}
