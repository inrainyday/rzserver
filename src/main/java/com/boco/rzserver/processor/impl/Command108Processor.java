package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req108;
import com.boco.rzserver.model.json.Resp108;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.ICacheService;

/**
 * 处理首页漏斗图
 */
@Component
public class Command108Processor extends BaseProcessor implements IProcessor{
	
	private static Logger logger = Logger.getLogger(Command108Processor.class);
	private int command = 108;

	@Autowired
	private ICacheService cacheService;
	
	@Override
	public int getCommand() {
		return command;
	}

	public String process(String json) throws RzProcessException {
		logger.info("108 json = " + json);
		Req108 req = jsonBinder.fromJson(json, Req108.class);
		String jsonStr = cacheService.getFunnelData(req.body.startTime, req.body.endTime, true);
		logger.info("108 return json = " + jsonStr);
		return jsonStr;
	}
}
