package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req104;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.ICacheService;


/**
 * 获取时间趋势图
 * @author lij
 *
 */
@Component
public class Command104Processor extends BaseProcessor implements IProcessor {
	private static Logger logger = Logger.getLogger(Command104Processor.class);
	private int command = 104;
	
	@Autowired
	private ICacheService cacheService;

	@Override
	public int getCommand() {
		return command;
	}

	@Override
	public String process(String json) throws RzProcessException {
		logger.info("getTrendItems json = " + json);
		Req104 req104 = jsonBinder.fromJson(json, Req104.class);
		String jsonStr = cacheService.getTrendItem(req104.body.domain, req104.body.startTime, req104.body.endTime,true);
		logger.info("getTrendItems return" + jsonStr);
		return jsonStr;
	}
}
