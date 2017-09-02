/*
 * see license.txt 
 */
package franks.game.net;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.Session;

import franks.util.Cons;
import franks.util.TimeStep;
import franks.util.Updatable;

/**
 * @author Tony
 *
 */
public class PeerConnection implements Updatable {

    private Queue<NetMessage> receivedMessages;
    private NetworkProtocol protocol;
    private Session session;
    
    public PeerConnection(NetworkProtocol protocol, Session session) {
        this.protocol = protocol;
        this.session = session;
        
        this.receivedMessages = new ConcurrentLinkedQueue<>();
    }
    
    
    @Override
    public void update(TimeStep timeStep) {
        while(!receivedMessages.isEmpty()) {
            NetMessage msg = receivedMessages.poll();
            switch(msg.type) {
                case FullState:
                    this.protocol.onGameFullState(msg.asNetGameFullState());
                    break;
                case Battle:
                    this.protocol.onBattle(msg.asNetBattle());
                    break;
                case Turn:
                    this.protocol.onTurnEnd(msg.asNetTurn());
                    break;
                default:
                    Cons.println("Unknown message type: " + msg.type);
                    break;
            
            }
        }
        
    }
    
    public void sendMessage(NetMessage msg) {
        if(session.isOpen()) {
            try {
                System.out.println("Sent: \n" + msg.toJson());
                session.getBasicRemote().sendText(msg.toJson());
            }
            catch(IOException e) {
                Cons.println("*** Unable to send message to remote client: " + e);
            }
        }
    }
    
    public void receiveMessage(NetMessage msg) {
        System.out.println("Received: \n" + msg.toJson());
        this.receivedMessages.add(msg);
    }    
    
    public boolean isConnected() {
        return this.session.isOpen();
    }
    
    public void close() {
        if(session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                Cons.println("*** Unable to safely close socket connect: " + e);
            }
        }
    }
}
