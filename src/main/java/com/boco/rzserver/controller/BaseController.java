package com.boco.rzserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.boco.rzserver.json.JsonBinder;
import com.boco.rzserver.message.MessageContainer;
import com.boco.rzserver.model.json.BaseResp;
import com.boco.rzserver.model.json.RespHead;

/**
 * @author lij
 *
 */
public class BaseController {

	private final Logger logger = LoggerFactory.getLogger(BaseController.class);

	protected JsonBinder jsonBinder = JsonBinder.buildNormalBinder();

	@Autowired
	private MessageContainer messageContainer;

	/**
	 * 产生异常消息的json
	 * 
	 * @param errorCode
	 * @return
	 */
	protected String createErrorRespMessage(String errorCode) {
		String errDesc = messageContainer.getProperty(errorCode);
		logger.error("error code :" + errorCode + ", errDesc:" + errDesc);
		BaseResp resp = new BaseResp();
		RespHead head = new RespHead();
		head.setStatus_code(errorCode);
		head.setStatus_desc(errDesc);
		resp.setHead(head);
		String json = this.jsonBinder.toJson(resp);
		return json;

	}

	// 把响应头设置成成功
	protected void initRespHead(RespHead head) {
		this.initRespHead(head, "000");
	}

	// 把响应头设置成制定的code
	protected void initRespHead(RespHead head, String code) {
		head.setStatus_code(code);
		head.setStatus_desc(this.messageContainer.getProperty(code));
	}

}
