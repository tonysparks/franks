/*
 *	leola-live 
 *  see license.txt
 */
package newera.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import newera.game.entity.Entity;
import newera.graph.AStarGraphSearch;
import newera.graph.GraphNode;
import newera.map.MapGraph;
import newera.map.MapTile;
import newera.math.Vector2f;


/**
 * Feeds the next graph node.  This is the path planner for an agent.  This allows an agent to
 * know which tile to move to next
 * 
 * @author Tony
 *
 */
public class PathPlanner<E> {
	
	private MapGraph<E> graph;
	private List<GraphNode<MapTile, E>> path;
	private SearchPath searchPath; 	
	private int currentNode;
	private Vector2f nextWaypoint;
	private Vector2f finalDestination;	
	
	private Game game;
	private Entity entity;
	
	public class SearchPath extends AStarGraphSearch<MapTile, E> {
		public List<MapTile> tilesToAvoid = new ArrayList<>();
		
		@Override
		protected int heuristicEstimateDistance(
				GraphNode<MapTile, E> startNode,
				GraphNode<MapTile, E> currentNode,
				GraphNode<MapTile, E> goal) {
			MapTile startTile = startNode.getValue();
			MapTile currentTile = currentNode.getValue();
			MapTile goalTile = goal.getValue();
						
			int dx = Math.abs(currentTile.getX() - goalTile.getX());
			int dy = Math.abs(currentTile.getY() - goalTile.getY());						
			
			int sdx = Math.abs(startTile.getX() - goalTile.getX());
			int sdy = Math.abs(startTile.getY() - goalTile.getY());
			
			final int D = 1;
			//final int D2 = 2;
			
			//distance = D * (dx+dy) + (D2 - 2 * D) * Math.min(dx, dy);
			int distance = D * (dx+dy);
			int cross = Math.abs(dx*sdy - sdx*dy);
			return distance + (cross);//
		}	
		
		
		@Override
		protected boolean shouldIgnore(GraphNode<MapTile, E> node) {
			MapTile tile = node.getValue();
			Optional<Entity> ent = game.getEntityOnTile(tile);
			if(ent.isPresent()) {
				return ent.get() != entity;
			}
			return false;
		}
	}
	
	
	/**
	 * @param path
	 */
	public PathPlanner(Game game, MapGraph<E> graph, Entity entity) {
		this.game = game;
		this.graph = graph;
		this.entity = entity;
		
		this.finalDestination = new Vector2f();
		this.nextWaypoint = new Vector2f();
		
		this.path = new ArrayList<GraphNode<MapTile, E>>();
		
		this.currentNode = 0;		
		this.searchPath = new SearchPath();			
	} 
	
	private void setPath(List<GraphNode<MapTile, E>> newPath) {
		clearPath();
		if(newPath != null) {
			for(int i = 0; i < newPath.size(); i++) {
				this.path.add(newPath.get(i));
			}
		}
	}
	
	/**
	 * Clears out the path
	 */
	public void clearPath() {
		this.currentNode = 0;
		this.finalDestination.zeroOut();
		this.path.clear();
		this.nextWaypoint.zeroOut();
	}
	
	/**
	 * Calculate the estimated cost of the path from the start to destination
	 * 
	 * @param start
	 * @param destination
	 * @return the estimated cost of moving from start to destination
	 */
	public int pathCost(Vector2f start, Vector2f destination) {
		List<GraphNode<MapTile, E>> newPath = this.graph.findPath(this.searchPath, start, destination);
		int cost = newPath.size();
		return cost;
	}
	
	/**
	 * Finds the optimal path between the start and end point
	 * 
	 * @param start
	 * @param destination
	 */
	public void findPath(Vector2f start, Vector2f destination) {				
		List<GraphNode<MapTile, E>> newPath = this.graph.findPath(this.searchPath, start, destination);
		setPath(newPath);
		
		this.finalDestination.set(destination);
	}
		

	
	/**
	 * @return if there is currently a path
	 */
	public boolean hasPath() {
		return !this.path.isEmpty();
	}
	
	/**
	 * @return the final destination
	 */
	public Vector2f getDestination() {
		return this.finalDestination;
	}

	/**
	 * @return the path
	 */
	public List<GraphNode<MapTile, E>> getPath() {
		return path;
	}

	/**
	 * @return the current node that the entity is trying to reach
	 */
	public GraphNode<MapTile, E> getCurrentNode() {
		if (!path.isEmpty() && currentNode < path.size()) {
			return path.get(currentNode);
		}
		return null;
	}
	
	/**
	 * @return true if this path is on the first node (just started)
	 */
	public boolean onFirstNode() {
		return currentNode == 0 && !path.isEmpty();
	}

	
	/**
	 * Retrieves the next way-point on the path.
	 * 
	 * @param ent
	 * @return the next way-point on the path
	 */
	public Vector2f nextWaypoint(Entity ent) {
		Vector2f cPos = ent.getCenterPos();
		int x = (int)cPos.x;
		int y = (int)cPos.y;
				
		nextWaypoint.zeroOut();
		
		if(! path.isEmpty() && currentNode < path.size() ) {
			GraphNode<MapTile, E> node = path.get(currentNode);
			MapTile tile = node.getValue();
		
			int centerX = tile.getX() + tile.getWidth()/2;
			int centerY = tile.getY() + tile.getHeight()/2;
			if( Math.abs(centerX - x) < 6 // 6
				&& Math.abs(centerY - y) < 6) {
				currentNode++;
			}
			
			nextWaypoint.x = (centerX - x);
			nextWaypoint.y = (centerY - y);
		}
		
		return nextWaypoint;
	}
	
	/**
	 * @return true if the current position is about the end of the path
	 */
	public boolean atDestination() {
		return (currentNode >= path.size());
	}
}

