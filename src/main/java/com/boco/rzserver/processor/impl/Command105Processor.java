package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req105;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.ICacheService;


/**
 * @author lij
 * 
 */
@Component
public class Command105Processor extends BaseProcessor implements IProcessor {
	private static Logger logger = Logger.getLogger(Command105Processor.class);
	private int command = 105;
	
	@Autowired
	private ICacheService cacheService;

	@Override
	public int getCommand() {
		return command;
	}

	@Override
	public String process(String json) throws RzProcessException {
		logger.info("get 105 json = " + json);
		Req105 req105 = jsonBinder.fromJson(json, Req105.class);
		String jsonStr = cacheService.getResps105(req105.body.domain, req105.body.startTime, req105.body.endTime, true);
		logger.info("return 105 json = " + jsonStr);
		return jsonStr;
	}
}
