package com.boco.rzserver.service.cache;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.service.ICacheService;
import com.boco.rzserver.service.impl.CacheServiceImpl;

public class CacheManagerThread extends Thread {
	ICacheService cacheSrv;
	private final static Logger logger = Logger.getLogger(CacheManagerThread.class);
	
	public CacheManagerThread(ICacheService cacheSrv) {
		this.cacheSrv = cacheSrv;
	}

	@Override
	public void run() {
		
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		int interval12 = 12*60*60*1000;
		String domain = "PS域主设备";
		boolean initFlag = false;
		String endTime;
		String startTime;
		FunnelWorker funnelWorker = new FunnelWorker(cacheSrv);
		funnelWorker.start();
		BarCoreWorker barCoreWorker = new BarCoreWorker(cacheSrv);
		barCoreWorker.start();
		BarDataWorker barDataWorker = new BarDataWorker(cacheSrv);
		barDataWorker.start();
		OtherWorker otherWorker = new OtherWorker(cacheSrv);
		otherWorker.start();
		
		while(true) {
			try {
				Date dt = new Date();
				if(!initFlag) {
					Calendar calendar = Calendar.getInstance();
					Date dtTmp = df.parse(df.format(dt));
					calendar.setTime(dtTmp);
					int unrounderMinutes = calendar.get(Calendar.MINUTE);
					int mod = unrounderMinutes %5;
					calendar.add(Calendar.MINUTE, -mod);
					long endTimeMS = calendar.getTime().getTime();
					
					dt = new Date(endTimeMS);
					initFlag = true;
				}
				if(dt.getMinutes()%5==0 && dt.getSeconds()<5) {
					startTime = df.format(new Date(dt.getTime()-interval12));
					endTime = df.format(dt);
					logger.info("start cache workers, startTime" + startTime + ", endTime = " + endTime);
					ParamObj paramObj = new ParamObj(domain,startTime,endTime);
					otherWorker.getQueue().poll();
					otherWorker.getQueue().put(paramObj);
					
					funnelWorker.getQueue().poll();
					funnelWorker.getQueue().put(paramObj);
					
					barCoreWorker.getQueue().poll();
					barCoreWorker.getQueue().put(paramObj);
					
					
					barDataWorker.getQueue().poll();
					barDataWorker.getQueue().put(paramObj);
					TimeUnit.SECONDS.sleep(4);
				}
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		new CacheManagerThread(new CacheServiceImpl()).start();
	}
}
