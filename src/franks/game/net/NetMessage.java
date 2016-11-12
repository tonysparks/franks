/*
 * see license.txt 
 */
package franks.game.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * @author Tony
 *
 */
public class NetMessage {
	public static enum MessageType {
		FullState,
		Battle,
		Turn,
	}
	
	public MessageType type;
	public JsonElement data;
	
	
	public NetTurn asNetTurn() {
		return gson.fromJson(data, NetTurn.class);
	}
	
	public NetGameFullState asNetGameFullState() {
		return gson.fromJson(data, NetGameFullState.class);
	}
	
	public NetBattle asNetBattle() {
		return gson.fromJson(data, NetBattle.class);
	}
	
	public String toJson() {
		return gson.toJson(this);
	}
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public static NetMessage fullStateMessage(NetGameFullState state) {
		NetMessage msg = new NetMessage();
		msg.type = MessageType.FullState;
		msg.data = gson.toJsonTree(state);
		return msg;
	}
	
	public static NetMessage battleMessage(NetBattle battle) {
		NetMessage msg = new NetMessage();
		msg.type = MessageType.Battle;
		msg.data = gson.toJsonTree(battle);
		return msg;
	}
	
	public static NetMessage turnMessage(NetTurn turn) {
		NetMessage msg = new NetMessage();
		msg.type = MessageType.Turn;
		msg.data = gson.toJsonTree(turn);
		return msg;
	}
	
	public static NetMessage fromJson(String json) {
		NetMessage msg = gson.fromJson(json, NetMessage.class);
		return msg;
	}
}
