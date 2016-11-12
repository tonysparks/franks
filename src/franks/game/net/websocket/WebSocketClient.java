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

import franks.game.GameState;
import franks.game.net.NetMessage;
import franks.game.net.PeerConnection;
import franks.util.Cons;

/**
 * @author Tony
 *
 */
@ClientEndpoint
public class WebSocketClient {
	
	private PeerConnection connection;
	private GameState state;
	
	/**
	 * @param game
	 */
	public WebSocketClient(GameState state) {
		this.state = state;
	}
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		session.setMaxIdleTimeout(0);
		Cons.println("Session connected: " + session.getId());
		this.connection = this.state.peerConnection(session);		
	}
	
	@OnClose
	public void onClose(Session session) {
		Cons.println("Session closed: " + session.getId());
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
