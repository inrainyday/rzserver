package com.boco.rzserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.model.json.BaseReq;
import com.boco.rzserver.processor.CommandDispatcher;
import com.boco.rzserver.processor.IProcessor;

/**
 * 
 * @author lij
 *
 */
@Controller
public class EntranceController extends BaseController {

	private final Logger logger = LoggerFactory
			.getLogger(EntranceController.class);

	@Autowired
	private CommandDispatcher dispatcher;

	@RequestMapping(value="/command",method={RequestMethod.GET,RequestMethod.POST},produces = "application/json; charset=utf-8")
	public @ResponseBody
	String process(HttpServletRequest request) {
		logger.debug("request received , begin process..");
		try {
			String json = request.getParameter("jsondata");
			logger.info("request json:" + json);
			BaseReq req = this.jsonBinder.fromJson(json, BaseReq.class);
			if (req == null) {
				return this.createErrorRespMessage("100");
			}
			int command = req.getHead().getCommand();
			IProcessor processor = dispatcher.getProcessor(command);
			if (processor == null) {
				return this.createErrorRespMessage("101");
			} else {
				String respJson = processor.process(json);
				logger.info("resp json:"+respJson);
				return respJson;
			}

		} catch (RzProcessException e) {
			return this.createErrorRespMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return this.createErrorRespMessage("999");
		}

	}

	

}
