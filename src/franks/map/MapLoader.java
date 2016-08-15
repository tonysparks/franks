/*
 *	leola-live 
 *  see license.txt
 */
package franks.map;

import leola.vm.types.LeoMap;

/**
 * @author Tony
 *
 */
public interface MapLoader {

	
	/**
	 * Loads a {@link Map}
	 * 
	 * @param map
	 * @param loadAssets
	 * @return
	 * @throws Exception
	 */
	public Map loadMap(LeoMap map, boolean loadAssets) throws Exception;
}
