package com.boco.rzserver.service.cache;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.service.ICacheService;

public class FunnelWorker extends BaseWorker {
	

	ICacheService cacheSrv;
	
	public FunnelWorker(ICacheService cacheSrv) {
		this.cacheSrv = cacheSrv;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				ParamObj take = queue.take();
				long tmpSt = System.currentTimeMillis();
				cacheSrv.getFunnelData(take.getStartTime(), take.getEndTime(), false);
				long totalCost = System.currentTimeMillis() - tmpSt;
				
				String costInfo = "cache flushed.getFunnelData startTime = " + take.getStartTime() + ", endTime = " + take.getEndTime() + ",total.cost(funnelCost)= " + totalCost;
				logger.info(costInfo);
				
				if(totalCost>=5*60*1000) {
					logger.error("Cache fulsh cost > filv minuters!!! " + costInfo);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RzProcessException e) {
				e.printStackTrace();
			}
		}
	}
	


}
