package com.boco.rzserver.model.json;

import java.util.Arrays;

/**
 * 
 * @author lij
 *
 */
public class Resp102 extends BaseResp {
	public Body body = new Body();
	public static class Body{
	@Override
		public String toString() {
			return "Body [tops=" + Arrays.toString(tops) + "]";
		}

	public Tops[] tops;
	}
	public static class Tops {
		@Override
		public String toString() {
			return "Tops [neName=" + neName + ", vendor=" + vendor + ", neType=" + neType + ", domain=" + domain
					+ ", fileName=" + fileName + ", eventTime=" + eventTime + ", city=" + city + ", count=" + count
					+ "]";
		}
		public String neName;
		public String vendor;
		public String neType;
		public String domain;
		public String fileName;
		public String eventTime;
		public String city;
		public long count;
	}
	
	@Override
	public String toString() {
		return "Resp102 [body=" + body + "]";
	}
	
}
