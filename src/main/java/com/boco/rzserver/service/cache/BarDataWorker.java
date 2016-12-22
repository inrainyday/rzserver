package com.boco.rzserver.service.cache;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.service.ICacheService;

/**
 * 数据网机器、预警、事件对比图
 * @author lij
 *
 */
public class BarDataWorker extends BaseWorker {
	

	ICacheService cacheSrv;
	
	public BarDataWorker(ICacheService cacheSrv) {
		this.cacheSrv = cacheSrv;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				ParamObj take = queue.take();
				long st = System.currentTimeMillis();
				cacheSrv.getResp110_2("数据网", take.getStartTime(), take.getEndTime(), false);
				long totalCost = System.currentTimeMillis()-st;
				logger.info("getResp110 data cost = " + totalCost);
				if(totalCost>=5*60*1000) {
					logger.error("getResp110 data cost > filv minuters!!! totalCost =" + totalCost);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RzProcessException e) {
				e.printStackTrace();
			}
		}
	}
	


}
