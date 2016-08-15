/*
 *	leola-live 
 *  see license.txt
 */
package franks.map;

import franks.graph.Edge;
import franks.graph.GraphNode;

/**
 * Creates {@link GraphNode} and {@link Edge} data elements.
 * 
 * @author Tony
 *
 */
public interface GraphNodeFactory<T> {
	
	/**
	 * @param map
	 * @param left
	 * @param right
	 * @return a new edge data
	 */
	public T createEdgeData(Map map, GraphNode<MapTile, T> left, GraphNode<MapTile, T> right);
}
