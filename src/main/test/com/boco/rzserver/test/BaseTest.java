package com.boco.rzserver.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.boco.rzserver.json.JsonBinder;
import com.boco.rzserver.model.json.ReqHead;

/**
 * 
 * @author lij
 *
 */
public class BaseTest {

	protected static JsonBinder binder = JsonBinder.buildNonDefaultBinder();

	public static String baseUrl = "http://120.92.21.229:8800/rzserver/command";
//	public static String baseUrl = "http://localhost:8080/rzserver/command";

	protected ReqHead getDefaultHead(int command, String token_id) {
		ReqHead head = new ReqHead();
		head.setVersion("1.0.0");
		head.setCommand(command);
		if (token_id == null) {
			head.setToken_id(UUID.randomUUID().toString());
		} else {
			head.setToken_id(token_id);
		}
		return head;
	}

	/**
	 * 执行方法
	 * 
	 * @param jsonStr
	 * @throws Exception
	 */
	@SuppressWarnings({ "deprecation" })
	protected String execute(String jsondata) throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String respProtocal = "";
		try {
			HttpPost post = new HttpPost(baseUrl);
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("jsondata", jsondata));
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = httpClient.execute(post);
			HttpEntity he = httpResponse.getEntity();
			respProtocal = EntityUtils.toString(he);
			System.out.println("respStr = " + respProtocal);
		} finally {
			httpClient.close();
		}
		return respProtocal;
	}
}
