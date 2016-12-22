package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req101;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.ICacheService;


/**
 * 获取地图的城市和日志数统计项
 * @author lij
 * 
 */
@Component
public class Command101Processor extends BaseProcessor implements IProcessor {
	private static Logger logger = Logger.getLogger(Command101Processor.class);
	private int command = 101;
	
	@Autowired
	private ICacheService cacheService;

	@Override
	public int getCommand() {
		return command;
	}

	@Override
	public String process(String json) throws RzProcessException {
		logger.info("getCityAndDocCounts json = " + json);
		Req101 req101 = jsonBinder.fromJson(json, Req101.class);
		
		String jsonStr = cacheService.getCityAndDocCounts(req101.body.startTime, req101.body.endTime, true);
		

		logger.info("getCityAndDocCounts return json = " + jsonStr);
		return jsonStr;
	}
}
