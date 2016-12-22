package com.boco.rzserver.model.json;

import java.util.Arrays;

/**
 * 
 * @author lij
 *
 */
public class Resp105 extends BaseResp {
	public Body body = new Body(new String[30], new int[30]);

	public static class Body {
		public String[] citys;
		@Override
		public String toString() {
			return "Body [citys=" + Arrays.toString(citys) + ", docCounts=" + Arrays.toString(docCounts) + "]";
		}
		public Body(String[] citys, int[] docCounts) {
			super();
			this.citys = citys;
			this.docCounts = docCounts;
		}
		public int[] docCounts;
	}

	
	
}
