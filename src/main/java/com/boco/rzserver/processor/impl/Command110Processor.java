package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req110;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.ICacheService;


/**
 * @author lij
 * 机器日志对比图
 * 事件日志对比图
 * 预警日志对比图
 */
@Component
public class Command110Processor extends BaseProcessor implements IProcessor {
	private static Logger logger = Logger.getLogger(Command110Processor.class);
	private int command = 110;
	

	@Autowired
	private ICacheService cacheService;
	
	@Override
	public int getCommand() {
		return command;
	}

	@Override
	public String process(String json) throws RzProcessException {
		logger.info("get 110 json = " + json);
		Req110 req110 = jsonBinder.fromJson(json, Req110.class);
		String jsonStr =  cacheService.getResp110(req110.body.domain, req110.body.startTime, req110.body.endTime, true); 
		logger.info("return 110 json = " + jsonStr);
		return jsonStr;
	}
}
