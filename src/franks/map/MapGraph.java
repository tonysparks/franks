/*
 *    leola-live 
 *  see license.txt
 */
package franks.map;

import java.util.List;
import java.util.Random;

import franks.graph.AStarGraphSearch;
import franks.graph.GraphNode;
import franks.graph.GraphSearchPath;
import franks.math.Vector2f;

/**
 * A {@link GraphNode} of the {@link Map}
 * 
 * @author Tony
 *
 */
@SuppressWarnings("all")
public class MapGraph<T> {

    public GraphNode[][] graph;
    private franks.map.Map map;
    private Random random;
    private int width, height;
    private GraphSearchPath<MapTile, T> defaultSearchPath;
    /**
     * 
     */
    public MapGraph(Map map, GraphNode[][] graph) {
        this.map = map;
        this.graph = graph;
        
        this.height = graph.length;
        this.width = graph[0].length;
        
        this.random = new Random();
        
        this.defaultSearchPath = new AStarGraphSearch<>();
    }
    
    /**
     * @param x
     * @param y
     * @return get the {@link GraphNode} by the x and y index (not world coordinates)
     */
    public GraphNode<MapTile, T> getNodeByIndex(int x, int y) {
        return (GraphNode<MapTile, T>)graph[y][x];
    }
    
    /**
     * @param wx
     * @param wy
     * @return the graph node at a world coordinate
     */
    public GraphNode<MapTile, T> getNodeByWorld(int wx, int wy) {                
        int tileOffset_x = 0;// (wx % map.getTileWidth());
        int x = (tileOffset_x + wx) / map.getTileWidth();

        int tileOffset_y = 0; //(wy % map.getTileHeight());
        int y = (tileOffset_y + wy) / map.getTileHeight();
        
        if(map.checkTileBounds(x, y)) {
            return null;
        }
        
        return x<width && y<height ? (GraphNode<MapTile, T>)graph[y][x] : null;
    }
    
    public GraphNode<MapTile, T> getNearestNodeByWorld(Vector2f pos) {
        return getNearestNodeByWorld((int)pos.x, (int)pos.y);
    }
    
    public GraphNode<MapTile, T> getNearestNodeByWorld(int wx, int wy) {
        GraphNode<MapTile, T> node = getNodeByWorld(wx, wy);
        
        if(node != null) return node;
        
        node = getNodeByWorld(wx+map.getTileHeight(), wy);
                
        if(node != null) return node;
        
        node = getNodeByWorld(wx-map.getTileHeight(), wy);
        
        if(node != null) return node;
        
        node = getNodeByWorld(wx, wy + map.getTileWidth());
        
        if(node != null) return node;
        
        node = getNodeByWorld(wx, wy - map.getTileWidth());
        
        if(node != null) return node;
        
        node = getNodeByWorld(wx + map.getTileHeight(), wy + map.getTileWidth());
        
        if(node != null) return node;
        
        node = getNodeByWorld(wx - map.getTileHeight(), wy - map.getTileWidth());
        
        if(node != null) return node;
        
        node = getNodeByWorld(wx - map.getTileHeight(), wy + map.getTileWidth());
    
        if(node != null) return node;
        
        node = getNodeByWorld(wx + map.getTileHeight(), wy - map.getTileWidth());
        
        return node;
    }

    
    /**
     * Calculate the estimated cost of the path from the start to destination
     * 
     * @param start
     * @param destination
     * @return the estimated cost of moving from start to destination
     */
    public int pathCost(Vector2f start, Vector2f destination) {
        List<GraphNode<MapTile, T>> newPath = this.findPath(this.defaultSearchPath, start, destination);
        int cost = newPath.size() * 32;
        return cost;
    }
    
    
    /**
     * Finds a fuzzy (meaning not necessarily the most optimal but different) path between the start and end point
     * 
     * @param start
     * @param destination
     * @return the list of node to travel to reach the destination
     */
    public List<GraphNode<MapTile, T>> findPath(GraphSearchPath<MapTile, T> searchPath, Vector2f start, Vector2f destination) {                            
        GraphNode<MapTile, T> startNode = getNearestNodeByWorld(start);        
        GraphNode<MapTile, T> destNode = getNearestNodeByWorld(destination);
        List<GraphNode<MapTile, T>> resultPath = searchPath.search(startNode, destNode);
        return resultPath; 
    }

}
