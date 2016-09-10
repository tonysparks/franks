/*
 * see license.txt 
 */
package franks.game;

/**
 * @author Tony
 *
 */
public interface ResourceLoader {

	public TextureCache getTextureCache();
	
	public <T> T loadData(String file, Class<T> type);
}
