/*
 * see license.txt 
 */
package franks.game.net.websocket;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import franks.game.Game;
import franks.game.net.NetMessage;
import franks.game.net.PeerConnection;
import franks.util.Cons;

/**
 * @author Tony
 *
 */
@ServerEndpoint(value="/socket")
public class WebSocketServer {
	public static Game game;
	
	private PeerConnection connection;
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		session.setMaxIdleTimeout(0);
		
		Cons.println("Session connected: " + session.getId());
//		this.connection = game.peerConnection(session);
//		
//		NetMessage msg = NetMessage.fullStateMessage(game.getNetGameFullState());
//		this.connection.sendMessage(msg);
	}
	
	@OnClose
	public void onClose(Session session) {
		this.connection.close();
	}
	
	@OnError
	public void onError(Session session, Throwable error) {
		if(this.connection!=null) {
			this.connection.close();
		}
		Cons.println("*** Server socket error: " + error);
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		NetMessage msg = NetMessage.fromJson(message);
		this.connection.receiveMessage(msg);
	}
}
