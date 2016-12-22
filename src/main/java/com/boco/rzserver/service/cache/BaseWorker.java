package com.boco.rzserver.service.cache;

import java.util.concurrent.SynchronousQueue;

import org.apache.log4j.Logger;

public class BaseWorker extends Thread {
	static Logger logger = Logger.getLogger(BaseWorker.class);
	
	protected SynchronousQueue<ParamObj> queue =  new SynchronousQueue<>();
	
	public SynchronousQueue<ParamObj> getQueue() {
		return queue;
	}
}
