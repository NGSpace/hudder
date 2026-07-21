package dev.ngspace.hudder.variables.advanced;

import java.util.UUID;

public class PlayerInformation {

	public String gamemode;
	public String displayname;
	public String uuid;
	public String username;
	public int tabOrder;
	public String teamname;

	public PlayerInformation(String username, UUID uuid, String displayname, int tabOrder, String teamname, String gamemode) {
		this.username = username;
		this.uuid = uuid.toString();
		this.displayname = displayname;
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
