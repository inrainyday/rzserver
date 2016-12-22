package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req103;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.ICacheService;


/**
 * @author lij
 * 按专业进行网元数、日志数等统计
 * 
 */
@Component
public class Command103Processor extends BaseProcessor implements IProcessor {
	private static Logger logger = Logger.getLogger(Command103Processor.class);
	private int command = 103;
	
	@Autowired
	private ICacheService cacheService;
	

	@Override
	public int getCommand() {
		return command;
	}

	@Override
	public String process(String json) throws RzProcessException {
		logger.info("getRangeStat json = " + json);
		Req103 req103 = jsonBinder.fromJson(json, Req103.class);
		String jsonStr = cacheService.getRangeStat(req103.body.startTime, req103.body.endTime,true);
		logger.info("getRangeStat return json = " + jsonStr);
		return jsonStr;
	}
}
