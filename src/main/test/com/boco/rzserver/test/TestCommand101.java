package com.boco.rzserver.test;

import com.boco.rzserver.model.json.Req101;
import com.boco.rzserver.model.json.ReqHead;

/**
 * @author lij
 *
 */
public class TestCommand101 extends BaseTest {
	public static void main(String[] args) {
		TestCommand101 tc = new TestCommand101();
		ReqHead head = tc.getDefaultHead(101, null);
		Req101 req = new Req101();
		req.setHead(head);
		req.body.startTime = "2015-01-01 10:10:10";
		req.body.endTime = "2017-01-01 10:10:10";
		try {
			System.out.println("req101 = " + BaseTest.binder.toJson(req));
			String result = tc.execute(BaseTest.binder.toJson(req));
			System.out.println("result =" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
