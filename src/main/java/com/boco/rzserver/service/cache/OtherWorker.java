package com.boco.rzserver.service.cache;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.service.ICacheService;

/**
 * 核心机器、预警、事件对比图
 * @author lij
 *
 */
public class OtherWorker extends BaseWorker {
	

	ICacheService cacheSrv;
	
	public OtherWorker(ICacheService cacheSrv) {
		this.cacheSrv = cacheSrv;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				ParamObj take = queue.take();
				long st = System.currentTimeMillis();
				long tmpSt = System.currentTimeMillis();
				cacheSrv.getCityAndDocCounts(take.getStartTime(), take.getEndTime(),false);
				long cityDocCost = System.currentTimeMillis()-tmpSt;
				tmpSt = System.currentTimeMillis();
				cacheSrv.getDataSizeTredns(take.getStartTime(), take.getEndTime(),false);
				long dataSizeCost = System.currentTimeMillis() - tmpSt;
				tmpSt = System.currentTimeMillis();
				cacheSrv.getRangeStat(take.getStartTime(), take.getEndTime(),false);
				long rangeStat = System.currentTimeMillis() - tmpSt;
				
				tmpSt = System.currentTimeMillis();
				cacheSrv.getTrendItem(take.getDomain(), take.getStartTime(), take.getEndTime(),false);
				long trendCost = System.currentTimeMillis() - tmpSt;
				tmpSt = System.currentTimeMillis();
				cacheSrv.getResps105(take.getDomain(), take.getStartTime(), take.getEndTime(),false);
				long bar105Cost = System.currentTimeMillis() - tmpSt;
				long totalCost = System.currentTimeMillis() - st;
				
				String costInfo = "cache flushed.other startTime = " + take.getStartTime() + ", endTime = " + take.getEndTime() + ",total.costs = " + totalCost
						+"ms, \ncityDocCost = " + cityDocCost + ", dataSizeCost = " + dataSizeCost + ", rangeStat = " + rangeStat + ",trendCost = " + trendCost  + ",bar105Cost = " + bar105Cost;
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
