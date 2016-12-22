package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req106;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.ICacheService;

/**
 * 机器日志对比图
 * 事件日志对比图
 * 预警日志对比图
 * @author lij
 *
 */
@Component
public class Command106Processor extends BaseProcessor implements IProcessor{
	
	private static Logger logger = Logger.getLogger(Command106Processor.class);
	private int command = 106;
	
	@Autowired
	private ICacheService cacheService;
	
	@Override
	public int getCommand() {
		// TODO Auto-generated method stub
		return command;
	}

	public String process(String json) throws RzProcessException {
		// TODO Auto-generated method stub
		logger.info("106 json = " + json);
		Req106 req106 = jsonBinder.fromJson(json, Req106.class);
		String jsonStr = cacheService.getBarTableData(req106.body.city, req106.body.domain, req106.body.startTime, req106.body.endTime, true);
		logger.info("106 return json = " + jsonStr);
		return jsonStr;
	}


}
