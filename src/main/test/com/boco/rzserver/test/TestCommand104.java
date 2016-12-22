package com.boco.rzserver.test;

import com.boco.rzserver.model.json.Req104;
import com.boco.rzserver.model.json.ReqHead;

/**
 * @author lij
 *
 */
public class TestCommand104 extends BaseTest {
	public static void main(String[] args) {
		TestCommand104 tc = new TestCommand104();
		ReqHead head = tc.getDefaultHead(104, null);
		Req104 req = new Req104();
		req.setHead(head);
		req.body.domain = "³ÐÔØÍø";
		req.body.startTime = "2015-01-01 10:10:10";
		req.body.endTime = "2016-05-04 01:10:10";
		try {
			System.out.println("req101 = " + BaseTest.binder.toJson(req));
			String result = tc.execute(BaseTest.binder.toJson(req));
			System.out.println("result =" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
