/*
 * see license.txt 
 */
package franks.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import franks.gfx.Art;

/**
 * @author Tony
 *
 */
public class TextureCache {

	private Map<String, TextureRegion> textures;
	
	/**
	 * 
	 */
	public TextureCache() {
		this.textures = new HashMap<>();
	}
	
	public TextureRegion getTexture(String name) {
		if(!textures.containsKey(name)) {
			TextureRegion tex = Art.loadImage(name);
			textures.put(name, tex);
		}
		
		return textures.get(name);
	}

	public void removeTexture(String name) {
		textures.remove(name);
	}
	
	public void disposeTexture(String name) {
		TextureRegion tex = textures.remove(name);
		if(tex!=null) {
			tex.getTexture().dispose();
		}
	}
	
	public void dispose() {
		textures.forEach( (k,v) -> v.getTexture().dispose());
		textures.clear();
	}
}
