package com.boco.rzserver.model.json;

/**
 * 
 * @author lij
 *
 */
public class ReqHead {
	private int command;
	private String version;
	private String token_id;

	public String getToken_id() {
		return token_id;
	}

	public void setToken_id(String token_id) {
		this.token_id = token_id;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
