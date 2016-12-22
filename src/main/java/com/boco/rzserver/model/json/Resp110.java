package com.boco.rzserver.model.json;

import java.util.Arrays;

/**
 * 
 * @author lij
 *
 */
public class Resp110 extends BaseResp {
	public Body body = new Body(new String[30], new int[30]);

	public static class Body {
		public String[] vendors;
		@Override
		public String toString() {
			return "Body [vendors=" + Arrays.toString(vendors) + ", docCounts=" + Arrays.toString(docCounts) + "]";
		}
		public Body(String[] vendors, int[] docCounts) {
			super();
			this.vendors = vendors;
			this.docCounts = docCounts;
		}
		public int[] docCounts;
	}

	
	
}
