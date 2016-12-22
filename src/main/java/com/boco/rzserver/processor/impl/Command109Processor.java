package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req109;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.ICacheService;

/**
 * 获取数据量曲线图
 * @author lij
 *
 */
@Component
public class Command109Processor extends BaseProcessor implements IProcessor{
	
	private static Logger logger = Logger.getLogger(Command109Processor.class);
	private int command = 109;

	@Autowired
	private ICacheService cacheService;
	
	@Override
	public int getCommand() {
		return command;
	}

	public String process(String json) throws RzProcessException {
		logger.info("109 getDataSizeTrends json = " + json);
		Req109 req = jsonBinder.fromJson(json, Req109.class);
		String jsonStr = cacheService.getDataSizeTredns(req.body.startTime, req.body.endTime, true);
		logger.info("109 return json = " + jsonStr);
		return jsonStr;
	}


}
