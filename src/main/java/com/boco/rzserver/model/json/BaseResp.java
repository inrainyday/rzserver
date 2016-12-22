package com.boco.rzserver.model.json;

/**
 * 
 * @author lij
 *
 */
public class BaseResp {
	protected RespHead head = new RespHead();

	public RespHead getHead() {
		return head;
	}

	public void setHead(RespHead head) {
		this.head = head;
	}

}
