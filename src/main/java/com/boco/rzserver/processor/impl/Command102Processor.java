package com.boco.rzserver.processor.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.Req102;
import com.boco.rzserver.model.json.Resp102;
import com.boco.rzserver.processor.BaseProcessor;
import com.boco.rzserver.processor.IProcessor;
import com.boco.rzserver.service.IEsService;
import com.boco.rzserver.service.impl.JestServiceImpl;

@Component
public class Command102Processor extends BaseProcessor implements IProcessor{
	
	private static Logger logger = Logger.getLogger(Command102Processor.class);
	private int command = 102;
	@Autowired
	private IEsService esSrv;	
	@Override
	public int getCommand() {
		// TODO Auto-generated method stub
		return command;
	}

	public String process(String json) throws RzProcessException {
		// TODO Auto-generated method stub
		logger.info("getTop5 json = " + json);
		Req102 req102 = jsonBinder.fromJson(json, Req102.class);
		System.out.println("********************************************/n"+req102.body.domain);
		logger.info("********************************************"+req102.body.domain);
//		Resp102 resp102 = esSrv.getTop5(req102.body.startTime, req102.body.endTime,req102.body.domain);
		Resp102 resp102 = JestServiceImpl.getInstance().getTop5(req102);
//		ArrayList resp102 = esSrv.getTop5("", "");
		String jsonStr = jsonBinder.toJson(resp102);
		logger.info("getTop5 return json = " + jsonStr);
		return jsonStr;
	}


}
