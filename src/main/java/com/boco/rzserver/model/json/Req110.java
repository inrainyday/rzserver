package com.boco.rzserver.model.json;

/**
 * 
 * @author lij
 *
 */
public class Req110 extends BaseReq {
	public Body body = new Body();

	public static class Body {
		public String startTime;
		public String endTime;
		public String domain;
	}
}
