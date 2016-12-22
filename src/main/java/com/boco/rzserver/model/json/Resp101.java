package com.boco.rzserver.model.json;

/**
 * 
 * @author lij
 *
 */
public class Resp101 extends BaseResp {
	public Body body = new Body();

	public static class Body {
		public String[] citys;
		public int[] docCounts;
	}
}
