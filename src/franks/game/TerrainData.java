/*
 * see license.txt 
 */
package franks.game;

import java.util.List;

import franks.map.MapObjectData;
import franks.map.MapTile;

/**
 * Bonus properties for a {@link MapTile}
 * 
 * @author Tony
 *
 */
public class TerrainData {
    public static class TilePos {
        public int tileX; 
        public int tileY;
        
        public boolean equals(int tx, int ty) {
            return this.tileX==tx && this.tileY==ty;
        }
    }
    
    public static class TerrainTileData {        
        public int defenseBonus;
        public int attackBonus;
        public int movementBonus;
        public List<TilePos> tiles;
    }
    
    public List<TerrainTileData> terrainTiles;
    public List<MapObjectData> mapObjects; 
    
    
    public TerrainTileData getTileTerrainData(int tileX, int tileY) {
        if(terrainTiles != null) {
            for(TerrainTileData t : terrainTiles) {
                for(TilePos pos : t.tiles) {
                    if(pos.equals(tileX, tileY)) {
                        return t;
                    }
                }
            }
        }
        return null;
    }

}
