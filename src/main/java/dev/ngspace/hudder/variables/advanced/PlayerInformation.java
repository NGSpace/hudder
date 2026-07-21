package dev.ngspace.hudder.variables.advanced;

import java.util.UUID;

import dev.ngspace.hudder.api.variableregistry.ComponentWrapper;
import net.minecraft.network.chat.Component;

public class PlayerInformation {

	public String gamemode;
	public ComponentWrapper displayname;
	public String uuid;
	public String username;
	public int tabOrder;
	public String teamname;

	public PlayerInformation(String username, UUID uuid, Component displayname, int tabOrder, String teamname, String gamemode) {
		this.username = username;
		this.uuid = uuid.toString();
		this.displayname = new ComponentWrapper(displayname != null ? displayname : Component.literal(username));
		this.tabOrder = tabOrder;
		this.teamname = teamname;
		this.gamemode = gamemode;
	}

	@Override
	public String toString() {
		return "PlayerInformation [gamemode=" + gamemode + ", displayname=" + displayname + ", uuid=" + uuid
				+ ", username=" + username + ", tabOrder=" + tabOrder + ", teamname=" + teamname + "]";
	}
	
}
