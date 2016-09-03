/*
 * see license.txt 
 */
package franks.game.net.websocket;

import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import franks.game.Game;
import franks.util.Cons;

/**
 * @author Tony
 *
 */
@ServerEndpoint("/server")
public class GameServer {	
	private Server server;	
	
	/**
	 * 
	 */
	public GameServer(Game game) {
		WebSocketServer.game = game;
	}
	
	public boolean start(int port) {
		try {
			Cons.println("Starting server on port: " + port);
			
			shutdown();
			
			this.server = new Server();		
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(port);
			server.addConnector(connector);
			
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	        context.setContextPath("/");
	        server.setHandler(context);
	        
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context); 
            wscontainer.addEndpoint(WebSocketServer.class);
            
			Thread thread = new Thread( () -> {
				try {
					server.start();
					server.join();
				}
				catch(Exception e) {
					Cons.println("*** Error in the server: " + e);		
				}
			}, "game-server");
			
			thread.start();
			
			return true;
			
		}
		catch(Exception e) {
			Cons.println("*** Error starting the server: " + e);
			return false;
		}
	}
	
	public void shutdown() {
		if(this.server != null) {
			try {
				this.server.stop();
			}
			catch(Exception e) {
				Cons.println("*** Error stopping server: " + e);
			}
			finally {
				this.server.destroy();
			}
		}
	}
	
	
}
