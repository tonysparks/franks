/*
 * see license.txt 
 */
package franks.game.net.websocket;

import javax.websocket.ClientEndpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import franks.game.Game;
import franks.game.net.NetMessage;
import franks.game.net.PeerConnection;
import franks.util.Cons;

/**
 * @author Tony
 *
 */
@ClientEndpoint
public class WebSocketClient {
	public static Game game;
	
	private PeerConnection connection;
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		Cons.println("Session connected: " + session.getId());
		this.connection = game.peerConnection(session);		
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
		Cons.println("*** Client socket error: " + error);
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		NetMessage msg = NetMessage.fromJson(message);
		this.connection.receiveMessage(msg);
	}
}
