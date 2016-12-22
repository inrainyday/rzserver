package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req107;
import com.boco.rzserver.model.json.Resp107;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.IEsService;


@Component
public class Command107Processor extends BaseProcessor implements IProcessor{
	
	private static Logger logger = Logger.getLogger(Command107Processor.class);
	private int command = 107;
	@Autowired
	private IEsService esSrv;	
	@Override
	public int getCommand() {
		return command;
	}

	public String process(String json) throws RzProcessException {
		logger.info("107 json = " + json);
		Req107 req = jsonBinder.fromJson(json, Req107.class);
		Resp107 resp = esSrv.getPreAlarm(req);
		String jsonStr = jsonBinder.toJson(resp);
		logger.info("107 return json = " + jsonStr);
		return jsonStr;
	}


}
