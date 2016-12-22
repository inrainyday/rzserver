package com.boco.rzserver.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.json.JsonBinder;
import com.boco.rzserver.model.json.Resp101;
import com.boco.rzserver.model.json.Resp103;
import com.boco.rzserver.model.json.Resp104;
import com.boco.rzserver.model.json.Resp105;
import com.boco.rzserver.model.json.Resp106;
import com.boco.rzserver.model.json.Resp108;
import com.boco.rzserver.model.json.Resp109;
import com.boco.rzserver.model.json.Resp110;
import com.boco.rzserver.service.ICacheService;


/**
 * 
 * @author lij
 *
 */
@Service
public class CacheServiceImpl implements ICacheService {
	
	private Map<String,String> resultMap = new HashMap<>();

	private Lock lock101 = new ReentrantLock();
	private Lock lock103 = new ReentrantLock();
	private Lock lock108 = new ReentrantLock();
	private Lock lock109 = new ReentrantLock();
	private Lock lock104 = new ReentrantLock();
	private Lock lock106 = new ReentrantLock();
	private Lock lock105 = new ReentrantLock();
	private Lock lock110 = new ReentrantLock();
	private Lock lock110_2 = new ReentrantLock();
	
	JsonBinder respJsonBinder = JsonBinder.buildNonNullBinder();
	
	
	int five = 5*60*1000;
	
	@Override
	public String getCityAndDocCounts(String startTime,String endTime, boolean lazyCacheFlag) throws RzProcessException {
		Resp101 resp101;
		StringBuffer sb = new StringBuffer();
		String key = sb.append("cad_").append(startTime).append(endTime).toString();
		if(!resultMap.containsKey(key) && lazyCacheFlag) {
			try {
				sb = new StringBuffer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lazyKey = sb.append("cad_").append(new Date(df.parse(startTime).getTime()-five)).append(new Date(df.parse(endTime).getTime()-five)).toString();
				if(resultMap.containsKey(lazyKey)) {
					return resultMap.get(lazyKey);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			lock101.lock();
			if(!resultMap.containsKey(key)) {
				resp101 = JestServiceImpl.getInstance().getCityAndDocCounts(startTime,endTime);
				String jsonStr = respJsonBinder.toJson(resp101);
				resultMap.put(key, jsonStr);
			}
			return resultMap.get(key);
		} finally {
			lock101.unlock();
		}
		
	}

	@Override
	public String getRangeStat(final String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException {
		StringBuffer sb = new StringBuffer();
		String key = sb.append("rs_").append(startTime).append(endTime).toString();
		if(!resultMap.containsKey(key) && lazyCacheFlag) {
			try {
				sb = new StringBuffer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lazyKey = sb.append("rs_").append(new Date(df.parse(startTime).getTime()-five)).append(new Date(df.parse(endTime).getTime()-five)).toString();
				if(resultMap.containsKey(lazyKey)) {
					return resultMap.get(lazyKey);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			lock103.lock();
			if(!resultMap.containsKey(key)) {
				Resp103 resp103 = JestServiceImpl.getInstance().getRangeStat(startTime,endTime);
				String jsonStr = respJsonBinder.toJson(resp103);
				resultMap.put(key, jsonStr);
			}
			return resultMap.get(key);
		} finally {
			lock103.unlock();
		}
	}

	@Override
	public String getFunnelData(String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException {
		StringBuffer sb = new StringBuffer();
		String key = sb.append("fd_").append(startTime).append(endTime).toString();
		if(!resultMap.containsKey(key) && lazyCacheFlag) {
			try {
				sb = new StringBuffer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lazyKey = sb.append("fd_").append(new Date(df.parse(startTime).getTime()-five)).append(new Date(df.parse(endTime).getTime()-five)).toString();
				if(resultMap.containsKey(lazyKey)) {
					return resultMap.get(lazyKey);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			lock108.lock();
			if(!resultMap.containsKey(key)) {
				Resp108 resp108 = JestServiceImpl.getInstance().getFunnelData(startTime, endTime);
				String jsonStr = respJsonBinder.toJson(resp108);
				resultMap.put(key, jsonStr);
			}
			return resultMap.get(key);
		} finally {
			lock108.unlock();
		}
	}

	@Override
	public String getDataSizeTredns(String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException {
		StringBuffer sb = new StringBuffer();
		String key = sb.append("dst_").append(startTime).append(endTime).toString();
		if(!resultMap.containsKey(key) && lazyCacheFlag) {
			try {
				sb = new StringBuffer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lazyKey = sb.append("dst_").append(new Date(df.parse(startTime).getTime()-five)).append(new Date(df.parse(endTime).getTime()-five)).toString();
				if(resultMap.containsKey(lazyKey)) {
					return resultMap.get(lazyKey);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			lock109.lock();
			if(!resultMap.containsKey(key)) {
				Resp109 resp109 = JestServiceImpl.getInstance().getDataSizeTrends(startTime, endTime);
				String jsonStr = respJsonBinder.toJson(resp109);
				resultMap.put(key, jsonStr);
			}
			return resultMap.get(key);
		} finally {
			lock109.unlock();
		}
	}
	
	@Override
	public String getTrendItem(String domain, String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException {
		StringBuffer sb = new StringBuffer();
		String key = sb.append("ti_").append(domain).append(startTime).append(endTime).toString();
		if(!resultMap.containsKey(key) && lazyCacheFlag) {
			try {
				sb = new StringBuffer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lazyKey = sb.append("ti_").append(domain).append(new Date(df.parse(startTime).getTime()-five)).append(new Date(df.parse(endTime).getTime()-five)).toString();
				if(resultMap.containsKey(lazyKey)) {
					return resultMap.get(lazyKey);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			lock104.lock();
			if(!resultMap.containsKey(key)) {
				Resp104 resp104 = JestServiceImpl.getInstance().getTrendItem(domain, startTime, endTime);
				String jsonStr = respJsonBinder.toJson(resp104);
				resultMap.put(key, jsonStr);
			}
			return resultMap.get(key);
		} finally {
			lock104.unlock();
		}
	}
	
	@Override
	public String getBarTableData(String city, String domain, String startTime, String endTime, boolean lazyCacheFlag)
			throws RzProcessException {
		StringBuffer sb = new StringBuffer();
		String key = sb.append("btd_").append(city).append(domain).append(startTime).append(endTime).toString();
		if(!resultMap.containsKey(key) && lazyCacheFlag) {
			try {
				sb = new StringBuffer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lazyKey = sb.append("btd_").append(city).append(domain).append(new Date(df.parse(startTime).getTime()-five)).append(new Date(df.parse(endTime).getTime()-five)).toString();
				if(resultMap.containsKey(lazyKey)) {
					return resultMap.get(lazyKey);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			lock106.lock();
			if(!resultMap.containsKey(key)) {
				Resp106 resp106 = JestServiceImpl.getInstance().getBarTableData(city, domain, startTime, endTime);
				String jsonStr = respJsonBinder.toJson(resp106);
				resultMap.put(key, jsonStr);
			}
			return resultMap.get(key);
		} finally {
			lock106.unlock();
		}
	}
	
	@Override
	public String getResps105(String domain, String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException {
		StringBuffer sb = new StringBuffer();
		String key = sb.append("r105_").append(domain).append(startTime).append(endTime).toString();
		if(!resultMap.containsKey(key) && lazyCacheFlag) {
			try {
				sb = new StringBuffer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lazyKey = sb.append("r105_").append(domain).append(new Date(df.parse(startTime).getTime()-five)).append(new Date(df.parse(endTime).getTime()-five)).toString();
				if(resultMap.containsKey(lazyKey)) {
					return resultMap.get(lazyKey);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			lock105.lock();
			if(!resultMap.containsKey(key)) {
				Resp105[] resp105 = JestServiceImpl.getInstance().getResps105(domain, startTime, endTime);
				String jsonStr = respJsonBinder.toJson(resp105);
				resultMap.put(key, jsonStr);
			}
			return resultMap.get(key);
		} finally {
			lock105.unlock();
		}
	}
	
	@Override
	public String getResp110(String domain, String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException {
		StringBuffer sb = new StringBuffer();
		String key = sb.append("r110_").append(domain).append(startTime).append(endTime).toString();
		if(!resultMap.containsKey(key) && lazyCacheFlag) {
			try {
				sb = new StringBuffer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lazyKey = sb.append("r110_").append(domain).append(new Date(df.parse(startTime).getTime()-five)).append(new Date(df.parse(endTime).getTime()-five)).toString();
				if(resultMap.containsKey(lazyKey)) {
					return resultMap.get(lazyKey);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			lock110.lock();
			if(!resultMap.containsKey(key)) {
				Resp110[] resp110 = JestServiceImpl.getInstance().getResps110(domain, startTime, endTime);
				String jsonStr = respJsonBinder.toJson(resp110);
				resultMap.put(key, jsonStr);
			}
			return resultMap.get(key);
		} finally {
			lock110.unlock();
		}
	}
	
	@Override
	public String getResp110_2(String domain, String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException {
		StringBuffer sb = new StringBuffer();
		String key = sb.append("r110.2_").append(domain).append(startTime).append(endTime).toString();
		if(!resultMap.containsKey(key) && lazyCacheFlag) {
			try {
				sb = new StringBuffer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String lazyKey = sb.append("r110.2_").append(domain).append(new Date(df.parse(startTime).getTime()-five)).append(new Date(df.parse(endTime).getTime()-five)).toString();
				if(resultMap.containsKey(lazyKey)) {
					return resultMap.get(lazyKey);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			lock110_2.lock();
			if(!resultMap.containsKey(key)) {
				Resp110[] resp110 = JestServiceImpl.getInstance().getResps110(domain, startTime, endTime);
				String jsonStr = respJsonBinder.toJson(resp110);
				resultMap.put(key, jsonStr);
			}
			return resultMap.get(key);
		} finally {
			lock110_2.unlock();
		}
	}
	
	


	
	public static void main(String[] args) throws RzProcessException {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTime parseDateTime = fmt.parseDateTime("2010-10-10 10:10:10");
		Date date = parseDateTime.toDate();
		System.out.println(date);
		date.getTime();
		final String startTime = "";
		final String endTime = "";
		final CacheServiceImpl serviceImpl = new CacheServiceImpl();
		new Thread() {
			public void run() {
				try {
					serviceImpl.getRangeStat(startTime, endTime, true);
				} catch (RzProcessException e) {
					e.printStackTrace();
				}
			};
		}.start();
		
		new Thread() {
			public void run() {
				try {
					serviceImpl.getRangeStat(startTime, endTime, true);
				} catch (RzProcessException e) {
					e.printStackTrace();
				}
			};
		}.start();
		
		serviceImpl.getFunnelData(startTime, endTime, true);
	}

}
