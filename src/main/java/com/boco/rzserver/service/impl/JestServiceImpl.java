package com.boco.rzserver.service.impl;



import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.SynchronousQueue;

import org.apache.log4j.Logger;
import org.elasticsearch.common.Strings;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.CronScheduleBuilder.cronSchedule;  
import static org.quartz.JobBuilder.newJob;  
import static org.quartz.TriggerBuilder.newTrigger;  


import com.boco.rzserver.constants.SysConstantsUtil;
import com.boco.rzserver.exception.RzProcessException;
import com.boco.rzserver.json.JsonBinder;
import com.boco.rzserver.model.json.Req101;
import com.boco.rzserver.model.json.Req102;
import com.boco.rzserver.model.json.Req103;
import com.boco.rzserver.model.json.Req110;
import com.boco.rzserver.model.json.Resp101;
import com.boco.rzserver.model.json.Resp102;
import com.boco.rzserver.model.json.Resp102.Tops;
import com.boco.rzserver.model.json.Resp103;
import com.boco.rzserver.model.json.Resp103.StatItem;
import com.boco.rzserver.model.json.Resp104;
import com.boco.rzserver.model.json.Resp104.TrendItem;
import com.boco.rzserver.model.json.Resp105;
import com.boco.rzserver.model.json.Resp106;
import com.boco.rzserver.model.json.Resp106.Datas;
import com.boco.rzserver.model.json.Resp108;
import com.boco.rzserver.model.json.Resp108.Item;
import com.boco.rzserver.model.json.Resp109;
import com.boco.rzserver.model.json.Resp110;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 使用es 9200 restful的接口进行调用
 * @author lij
 *
 */
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

//Learn it
//http://www.ibm.com/developerworks/java/library/j-javadev2-24/index.html?ca=drs-
	
public class JestServiceImpl implements Job{
	
	static Logger logger = Logger.getLogger(JestServiceImpl.class);
	private static JestServiceImpl jestServiceImpl = new JestServiceImpl();
	
	static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static DateFormat dfMdh = new SimpleDateFormat("MM/dd HH");
	static DateFormat dfMdhm = new SimpleDateFormat("MM/dd HH:mm");
	
	
	public static JestServiceImpl getInstance() {
		return jestServiceImpl;
	}
	
	JestClient client;
	public JestServiceImpl() {
		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig
				.Builder("http://" + SysConstantsUtil.esUrl + ":9200")
				.multiThreaded(true).connTimeout(300000).readTimeout(300000)
				.build());
		client = factory.getObject();
	}
	
	Map<String, Long> totalCount = new HashMap<>();
	
	DecimalFormat dfNum = new DecimalFormat("#.00");
	public Resp103 getRangeStat(String startTime, String endTime) throws RzProcessException {
		try {
			Resp103 resp103 = new Resp103();
			resp103.body.statItems = new StatItem[3];
			String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
			StatItem statItem = new StatItem();
			statItem.rangeName = "核心网";
			resp103.body.statItems[1] = statItem;
			
			String query = SysConstantsUtil.getQueryStringByDomain("核心网汇总");
			query = query.replace("now+8h", endTime);
			query = query.replace("now-4h", startTime);
			System.out.println("my query: "+query);
			Search search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			SearchResult result = client.execute(search);
			logger.info("core result.getJsonString() = " + result.getJsonString());
			JsonObject jsonObject = result.getJsonObject();
			List<String> docCountCountList = new ArrayList<>();
			List<String>neCountList = new ArrayList<>();
			
			getValuesFromJsonObject(docCountCountList,jsonObject,"total");
			getValuesFromJsonObject(neCountList,jsonObject,"value");
			
			if(docCountCountList.size()>1) {
				statItem.logCount = Long.valueOf(docCountCountList.get(1));
			}
			if(neCountList.size()>0) {
				statItem.neCount = Long.valueOf(neCountList.get(0));
			}
			statItem.size = SysConstantsUtil.logItemSize*statItem.logCount/SysConstantsUtil.BYTE_TO_GB;
			statItem.size =Double.parseDouble(dfNum.format(statItem.size));
			
			
			
			statItem = new StatItem();
			resp103.body.statItems[2] = statItem;
			statItem.rangeName = "数据网";
			
			
			query = SysConstantsUtil.getQueryStringByDomain("数据网汇总");
			query = query.replace("now+8h", endTime);
			query = query.replace("now-4h", startTime);
			search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			result = client.execute(search);
			logger.info("data result.getJsonString() = " + result.getJsonString());
			jsonObject = result.getJsonObject();
			docCountCountList = new ArrayList<>();
			neCountList = new ArrayList<>();
			
			getValuesFromJsonObject(docCountCountList,jsonObject,"total");
			getValuesFromJsonObject(neCountList,jsonObject,"value");
			
			if(docCountCountList.size()>1) {
				statItem.logCount = Long.valueOf(docCountCountList.get(1));
			}
			if(neCountList.size()>0) {
				statItem.neCount = Long.valueOf(neCountList.get(0));
			}
			statItem.size = SysConstantsUtil.logItemSize*statItem.logCount/SysConstantsUtil.BYTE_TO_GB;
			statItem.size =Double.parseDouble(dfNum.format(statItem.size));
			
			//全网
			statItem = new StatItem();
			resp103.body.statItems[0]=statItem;
			statItem.rangeName = "全网";
			statItem.logCount = resp103.body.statItems[1].logCount+resp103.body.statItems[2].logCount;
			statItem.neCount = resp103.body.statItems[1].neCount+resp103.body.statItems[2].neCount;
			statItem.size = SysConstantsUtil.logItemSize*statItem.logCount/SysConstantsUtil.BYTE_TO_GB;
			statItem.size =Double.parseDouble(dfNum.format(statItem.size));
			
			resp103.getHead().setStatus_code("000");
			resp103.getHead().setStatus_desc("sucessful.");
			return resp103;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RzProcessException("103");
		}
	}


	public Resp101 getCityAndDocCounts(String startTime, String endTime) throws RzProcessException{
		try {
			String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
			Resp101 resp101 = new Resp101();
			Resp101.Body body = new Resp101.Body();
			resp101.getHead().setStatus_code("000");
			resp101.getHead().setStatus_desc("successful");
			String query = SysConstantsUtil.getQueryStringByDomain("地图");
			query = query.replace("now+8h", endTime);
			query = query.replace("now-4h", startTime);
			logger.info("the query is "+query);
			Search search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			SearchResult result = client.execute(search);
			logger.info("result.getJsonString() = " + result.getJsonString());
			JsonObject jsonObject = result.getJsonObject();
			JsonArray ja = jsonObject.get("aggregations").getAsJsonObject().get("recent_12h").getAsJsonObject().get("cities").getAsJsonObject().get("buckets").getAsJsonArray();
			logger.info(ja.size()+"******************************"+ja.toString());
			
			if(ja.size() == 0) {System.out.println("ja size 0");
				body.citys = new String[cities.length];
				body.docCounts = new int[cities.length];
				int i=0;
				for(String cityName : cities) {
					body.citys[i] = cityName;
					body.docCounts[i] = 0;
					i++;
				}
			} 
			else if(ja.size()>0) {
				body.citys = new String[cities.length];
				body.docCounts = new int[cities.length];
				for(int j=0;j<ja.size();j++){System.out.println(ja.get(j).getAsJsonObject().get("key").getAsString());
					int i=0;Boolean flag = true;
					for(String cityName : cities) {
						System.out.print(cityName);
						body.citys[i] = cityName;
						if(cityName.equals(ja.get(j).getAsJsonObject().get("key").getAsString())){
							System.out.println("cityname is equal");
						body.docCounts[i] = ja.get(j).getAsJsonObject().get("doc_count").getAsInt();
						}else if(0==body.docCounts[i]){
							body.docCounts[i] = 0;
						}
						i++;
					}
					
			}
			}
			
			
			
			
			
//			else if(ja.size()>0) {
//				System.out.println(ja.get(0).getAsJsonObject().get("key").getAsString());
//				body.citys = new String[ja.size()];
//				body.docCounts = new int[ja.size()];
//				for(int i=0;i<ja.size();i++){
//					body.citys[i]=ja.get(i).getAsJsonObject().get("key").getAsString();
//					body.docCounts[i]=ja.get(i).getAsJsonObject().get("doc_count").getAsInt();
//				}
//			}
			resp101.body=body;
			return resp101;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RzProcessException("101");
		}
	}
	public Resp104 getTrendItem(String domain,String startTime, String endTime) throws RzProcessException {
		
		try {
			String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
			Resp104 resp104 = new Resp104();
			resp104.getHead().setStatus_code("000");
			resp104.getHead().setStatus_desc("successful");
			if(domain.equals("PS域主设备")){
				String query = SysConstantsUtil.getQueryStringByDomain("PS域主设备重启闪断");
				query=query.replace("now+8h", endTime);
				query=query.replace("now-4h", startTime);
				logger.info("the query is "+query);
				Search search = new Search.Builder(query)
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				SearchResult result = client.execute(search);
				logger.info("result.getJsonString() = " + result.getJsonString());
				JsonObject jsonObject = result.getJsonObject();
				List<String> timePointList = new ArrayList<>();
				List<String> valueList = new ArrayList<>();
				getValuesFromJsonObject(timePointList,jsonObject,"key_as_string");
				getValuesFromJsonObject(valueList,jsonObject,"value");
				
				
				resp104.body.trendItems = new Resp104.TrendItem[1];
				TrendItem trendItem = new Resp104.TrendItem();
				trendItem.trendName = "重启闪断";
				String[] stdTimePoints = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
				trendItem.timePoints = stdTimePoints;
				trendItem.values = getValues(stdTimePoints,timePointList,valueList);
				resp104.body.trendItems[0] = trendItem;
				
//				String query1 = SysConstantsUtil.getQueryStringByDomain("PS域主设备端口down");
//				query1.replace("now+8h", endTime);
//				query1.replace("now-4h", startTime);
//				logger.info("the query is "+query);
//				Search search1 = new Search.Builder(query1)
//		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
//		                .build();
//				SearchResult result1 = client.execute(search1);
//				logger.info("result.PS域主设备端口down getJsonString() = " + result1.getJsonString());
//				JsonObject jsonObject1 = result1.getJsonObject();
//				List<String> timePointList1 = new ArrayList<>();
//				List<String> valueList1 = new ArrayList<>();
//				getValuesFromJsonObject(timePointList1,jsonObject1,"key_as_string");
//				getValuesFromJsonObject(valueList1,jsonObject1,"value");
//				TrendItem trendItem1 = new Resp104.TrendItem();
//				trendItem1.trendName = "端口down";
//				String[] stdTimePoints1 = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
//				trendItem1.timePoints = stdTimePoints;
//				trendItem1.values = getValues(stdTimePoints1,timePointList1,valueList1);
//				resp104.body.trendItems[1] = trendItem1;
				
				
				return resp104;
			}else if(domain.equals("数通设备")){
				String query = SysConstantsUtil.getQueryStringByDomain("数通设备重启闪断");
				query=query.replace("now+8h", endTime);
				query=query.replace("now-4h", startTime);
				logger.info("the query is "+query);
				Search search = new Search.Builder(query)
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				SearchResult result = client.execute(search);
				logger.info("result.getJsonString() = " + result.getJsonString());
				JsonObject jsonObject = result.getJsonObject();
				List<String> timePointList = new ArrayList<>();
				List<String> valueList = new ArrayList<>();
				getValuesFromJsonObject(timePointList,jsonObject,"key_as_string");
				getValuesFromJsonObject(valueList,jsonObject,"value");
				
				
				resp104.body.trendItems = new Resp104.TrendItem[1];
				TrendItem trendItem = new Resp104.TrendItem();
				trendItem.trendName = "重启闪断";
				String[] stdTimePoints = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
				trendItem.timePoints = stdTimePoints;
				trendItem.values = getValues(stdTimePoints,timePointList,valueList);
				resp104.body.trendItems[0] = trendItem;
				return resp104;
			}else if(domain.equals("承载网")){
				String query = SysConstantsUtil.getQueryStringByDomain("承载网重启闪断");
				query=query.replace("now+8h", endTime);
				query=query.replace("now-4h", startTime);
				logger.info("the query is "+query);
				Search search = new Search.Builder(query)
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				SearchResult result = client.execute(search);
				logger.info("result.getJsonString() = " + result.getJsonString());
				JsonObject jsonObject = result.getJsonObject();
				List<String> timePointList = new ArrayList<>();
				List<String> valueList = new ArrayList<>();
				
				getValuesFromJsonObject(timePointList,jsonObject,"key_as_string");
				getValuesFromJsonObject(valueList,jsonObject,"value");
				resp104.body.trendItems = new Resp104.TrendItem[2];
				TrendItem trendItem = new Resp104.TrendItem();
				trendItem.trendName = "重启闪断";
				String[] stdTimePoints = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
				trendItem.timePoints = stdTimePoints;
				trendItem.values = getValues(stdTimePoints,timePointList,valueList);
				resp104.body.trendItems[0] = trendItem;
				
				String query1 = SysConstantsUtil.getQueryStringByDomain("承载网端口down");
				query1.replace("now+8h", endTime);
				query1.replace("now-4h", startTime);
				logger.info("the query is "+query);
				Search search1 = new Search.Builder(query1)
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				SearchResult result1 = client.execute(search1);
				logger.info("result.getJsonString() = " + result1.getJsonString());
				JsonObject jsonObject1 = result1.getJsonObject();
				List<String> timePointList1 = new ArrayList<>();
				List<String> valueList1 = new ArrayList<>();
				getValuesFromJsonObject(timePointList1,jsonObject1,"key_as_string");
				getValuesFromJsonObject(valueList1,jsonObject1,"value");
				TrendItem trendItem1 = new Resp104.TrendItem();
				trendItem1.trendName = "端口down";
				String[] stdTimePoints1 = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
				trendItem1.timePoints = stdTimePoints;
				trendItem1.values = getValues(stdTimePoints1,timePointList1,valueList1);
				resp104.body.trendItems[1] = trendItem1;
			}else if(domain.equals("城域网")){
				String query = SysConstantsUtil.getQueryStringByDomain("城域网重启闪断");
				query=query.replace("now+8h", endTime);
				query=query.replace("now-4h", startTime);
				logger.info("the query is "+query);
				Search search = new Search.Builder(query)
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				SearchResult result = client.execute(search);
				logger.info("result.getJsonString() = " + result.getJsonString());
				JsonObject jsonObject = result.getJsonObject();
				List<String> timePointList = new ArrayList<>();
				List<String> valueList = new ArrayList<>();
				getValuesFromJsonObject(timePointList,jsonObject,"key_as_string");
				getValuesFromJsonObject(valueList,jsonObject,"value");
				resp104.body.trendItems = new Resp104.TrendItem[2];
				TrendItem trendItem = new Resp104.TrendItem();
				trendItem.trendName = "重启闪断";
				String[] stdTimePoints = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
				trendItem.timePoints = stdTimePoints;
				trendItem.values = getValues(stdTimePoints,timePointList,valueList);
				resp104.body.trendItems[0] = trendItem;
				
				String query1 = SysConstantsUtil.getQueryStringByDomain("城域网端口down");
				query1.replace("now+8h", endTime);
				query1.replace("now-4h", startTime);
				logger.info("the query is "+query);
				Search search1 = new Search.Builder(query1)
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				SearchResult result1 = client.execute(search1);
				logger.info("result.getJsonString() = " + result1.getJsonString());
				JsonObject jsonObject1 = result1.getJsonObject();
				List<String> timePointList1 = new ArrayList<>();
				List<String> valueList1 = new ArrayList<>();
				getValuesFromJsonObject(timePointList1,jsonObject1,"key_as_string");
				getValuesFromJsonObject(valueList1,jsonObject1,"value");
				TrendItem trendItem1 = new Resp104.TrendItem();
				trendItem1.trendName = "端口down";
				String[] stdTimePoints1 = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
				trendItem1.timePoints = stdTimePoints;
				trendItem1.values = getValues(stdTimePoints1,timePointList1,valueList1);
				resp104.body.trendItems[1] = trendItem1;
			}else if(domain.equals("CMNET")){
				String query = SysConstantsUtil.getQueryStringByDomain("CMNET重启闪断");
				query=query.replace("now+8h", endTime);
				query=query.replace("now-4h", startTime);
				logger.info("the query is "+query);
				Search search = new Search.Builder(query)
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				SearchResult result = client.execute(search);
				logger.info("result.getJsonString() = " + result.getJsonString());
				JsonObject jsonObject = result.getJsonObject();
				List<String> timePointList = new ArrayList<>();
				List<String> valueList = new ArrayList<>();
				getValuesFromJsonObject(timePointList,jsonObject,"key_as_string");
				getValuesFromJsonObject(valueList,jsonObject,"value");
				resp104.body.trendItems = new Resp104.TrendItem[3];
				TrendItem trendItem = new Resp104.TrendItem();
				trendItem.trendName = "重启闪断";
				String[] stdTimePoints = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
				trendItem.timePoints = stdTimePoints;
				trendItem.values = getValues(stdTimePoints,timePointList,valueList);
				resp104.body.trendItems[0] = trendItem;
				
				String query1 = SysConstantsUtil.getQueryStringByDomain("CMNET端口down");
				query1.replace("now+8h", endTime);
				query1.replace("now-4h", startTime);
				logger.info("the query is "+query);
				Search search1 = new Search.Builder(query1)
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				SearchResult result1 = client.execute(search1);
				logger.info("result.getJsonString() = " + result1.getJsonString());
				JsonObject jsonObject1 = result1.getJsonObject();
				List<String> timePointList1 = new ArrayList<>();
				List<String> valueList1 = new ArrayList<>();
				getValuesFromJsonObject(timePointList1,jsonObject1,"key_as_string");
				getValuesFromJsonObject(valueList1,jsonObject1,"value");
				TrendItem trendItem1 = new Resp104.TrendItem();
				trendItem1.trendName = "端口down";
				String[] stdTimePoints1 = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
				trendItem1.timePoints = stdTimePoints;
				trendItem1.values = getValues(stdTimePoints1,timePointList1,valueList1);
				resp104.body.trendItems[1] = trendItem1;
				
				String query2 = SysConstantsUtil.getQueryStringByDomain("CMNET协议变更");
				query2.replace("now+8h", endTime);
				query2.replace("now-4h", startTime);
				logger.info("the query is "+query);
				Search search2 = new Search.Builder(query2)
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				SearchResult result2 = client.execute(search2);
				logger.info("result.getJsonString() = " + result1.getJsonString());
				JsonObject jsonObject2 = result2.getJsonObject();
				List<String> timePointList2 = new ArrayList<>();
				List<String> valueList2 = new ArrayList<>();
				getValuesFromJsonObject(timePointList2,jsonObject2,"key_as_string");
				getValuesFromJsonObject(valueList2,jsonObject2,"value");
				TrendItem trendItem2 = new Resp104.TrendItem();
				trendItem2.trendName = "协议变更";
				String[] stdTimePoints2 = initStdTimePoint(endTime,12); //根据endTime时间，构建连续的时间点
				trendItem2.timePoints = stdTimePoints;
				trendItem2.values = getValues(stdTimePoints2,timePointList2,valueList2);
				resp104.body.trendItems[2] = trendItem2;
			}
			return resp104;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RzProcessException("104");
		}
	}
	
	public Resp109 getDataSizeTrends(String startTime, String endTime)  throws RzProcessException {
		try {
			String query = SysConstantsUtil.getQueryStringByDomain("数据量动态统计");
			String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
			query=query.replace("now+8h", endTime);
			query=query.replace("now-4h", startTime);
			logger.info("the query is "+query);
			Search search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			SearchResult result = client.execute(search);
			logger.info("result.getJsonString() = " + result.getJsonString());
			JsonObject jsonObject = result.getJsonObject();
			List<String> timePointList = new ArrayList<>();
			List<String> valueList = new ArrayList<>();
			
			getValuesFromJsonObject(timePointList,jsonObject,"key_as_string");
			getValuesFromJsonObject(valueList,jsonObject,"doc_count");
			
			
			
			Resp109 resp = new Resp109();
			resp.getHead().setStatus_code("000");
			resp.getHead().setStatus_desc("successful");
			resp.body.trendItems = new Resp109.TrendItem[1];
			Resp109.TrendItem trendItem = new Resp109.TrendItem();
			trendItem.trendName = "数据量动态统计";
			String[] stdTimePoints = initQuarterHourStdTimePoint(endTime,12*4); //根据endTime时间，构建连续的时间点
			trendItem.timePoints = stdTimePoints;
			trendItem.values = getDataSizeValues(stdTimePoints,timePointList,valueList);
			resp.body.trendItems[0] = trendItem;
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RzProcessException("109");
		}
	}
	
	private long[] getValues(String[] stdTimePoints,
			List<String> timePointList, List<String> valueList) {
		long[] values = new long[stdTimePoints.length];
		for(int i=0; i<stdTimePoints.length; i++) {
			values[i] = 0; //默认为0
			for(int j=0; j<timePointList.size(); j++) {
				//如果返回结果中有结果，则使用返回的结果
				if(stdTimePoints[i].equals(timePointList.get(j))) {
					values[i] = Long.valueOf(valueList.get(j));
					break;
				}
			}
		}
		return values;
	}
	
	DecimalFormat numFormat = new DecimalFormat("#.00");  
	private double[] getDataSizeValues(String[] stdTimePoints,
			List<String> timePointList, List<String> valueList) {
		double[] values = new double[stdTimePoints.length];
		for(int i=0; i<stdTimePoints.length; i++) {
			values[i] = 0; //默认为0
			for(int j=0; j<timePointList.size(); j++) {
				//如果返回结果中有结果，则使用返回的结果
				if(stdTimePoints[i].equals(timePointList.get(j))) {
					Long valueOf = Long.valueOf(valueList.get(j));
					double size = SysConstantsUtil.logItemSize*valueOf/SysConstantsUtil.BYTE_TO_GB;
					values[i] = Double.parseDouble(numFormat.format(size));
					break;
				}
			}
			stdTimePoints[i] = stdTimePoints[i].replace(" ", "\n"); //为了页面显示 在月时和时分秒中间增加换行
		}
		return values;
	}


	static final long hourMS = 60*60*1000;
	private String[] initStdTimePoint(String endTime, int count) throws ParseException {
		String[] stdTimePoints = new String[count];
		Date dt = df.parse(endTime);
		long endTimeMS = dt.getTime();
		
		for(int i=0; i<count; i++) {
			long stdTimePoint = endTimeMS - hourMS*(count-1-i);
			stdTimePoints[i] = dfMdh.format(new Date(stdTimePoint));
		}
		return stdTimePoints;
	}


	static final long quarterMS = 15*60*1000;
	/**
	 * 获取按一刻钟分时间点
	 * @param endTime
	 * @param count
	 * @return
	 * @throws ParseException
	 */
	private String[] initQuarterHourStdTimePoint(String endTime, int count) throws ParseException {
		String[] stdTimePoints = new String[count];
		Calendar calendar = Calendar.getInstance();
		Date dt = df.parse(endTime);
		calendar.setTime(dt);
		int unrounderMinutes = calendar.get(Calendar.MINUTE);
		int mod = unrounderMinutes %15;
		calendar.add(Calendar.MINUTE, -mod);
		long endTimeMS = calendar.getTime().getTime();
		
		
		for(int i=0; i<count; i++) {
			long stdTimePoint = endTimeMS - quarterMS*(count-1-i);
			stdTimePoints[i] = dfMdhm.format(new Date(stdTimePoint));
		}
		return stdTimePoints;
	}




	private void getValuesFromJsonObject(List<String> list, JsonObject jsonObject, String key) {
		for(Entry<String,JsonElement> entry : jsonObject.entrySet()) {
			if(key.equalsIgnoreCase(entry.getKey())){
				list.add(entry.getValue().getAsString());
			}
			else if(entry.getValue() !=null && entry.getValue().isJsonArray()) {
				JsonArray asJsonArray = entry.getValue().getAsJsonArray();
				for(int i=0; i< asJsonArray.size(); i++) {
					JsonElement jsonElement = asJsonArray.get(i);
					if(jsonElement.isJsonObject()) {
						getValuesFromJsonObject(list,jsonElement.getAsJsonObject(),key);
					}

				}
			} else if(entry.getValue() !=null && entry.getValue().isJsonObject()) {
				getValuesFromJsonObject(list,entry.getValue().getAsJsonObject(),key);
			}
		}
	}

	public void close() {
		client.shutdownClient();
	}
	
	public Resp102 getTop5(Req102 req102) throws RzProcessException{
		try {
				SearchResult result;
				String[] suffix = SysConstantsUtil.changeIndexNameByDate(req102.body.endTime);
				Resp102 resp102 = new Resp102();
				resp102.getHead().setStatus_code("000");
				resp102.getHead().setStatus_desc("successful");
				String query = SysConstantsUtil.getQueryStringByDomain(req102.body.domain);
				if(query==null || query.trim().length()<1) {
					resp102.body.tops = new Tops[0];
					return resp102;
				}
				query=query.replace("now+8h", req102.body.endTime);
				query=query.replace("now-4h", req102.body.startTime);
				logger.info(req102.body.domain+"the query is "+query);
				Search search = new Search.Builder(query)
                // multiple index or types can be added.
                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
                .build();
		
			result = client.execute(search);
			logger.info("TEST102 "+result.getJsonString());
			logger.info("**************************************\n"+result.getTotal()+"\n**************************************");
			JsonObject jsonObject = result.getJsonObject();
			if(jsonObject.has("aggregations")){
			JsonArray ja = jsonObject.get("aggregations").getAsJsonObject().get("recent_12h").getAsJsonObject().get("neName").getAsJsonObject().get("buckets").getAsJsonArray();
//			logger.info(ja.get(ja.size()));
			
			resp102.body.tops = new Tops[ja.size()];
			for(int i=0;i<ja.size();i++){
				String querys ="{\n" + 
						"	\"query\": {\n" +
						"	\"term\":{\"neName\":"+ ja.get(i).getAsJsonObject().get("key") + "}\n" +
						"	},\n" +
						"	\"size\":\"1\"\n" +
						"}";
				Search search1 = new Search.Builder(querys)
		                // multiple index or types can be added.
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				JsonObject jo = client.execute(search1).getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray().get(0).getAsJsonObject().get("_source").getAsJsonObject();
				Tops tp = new Tops();
				tp.city = jo.get("city").getAsString();
				tp.domain= jo.get("domain").getAsString();
				tp.eventTime = jo.get("eventTime").getAsString();
				tp.fileName = jo.get("fileName").getAsString();
				tp.neName = jo.get("neName").getAsString();
				tp.neType = jo.get("neType").getAsString();
				tp.vendor = jo.get("vendor").getAsString();
				tp.count = ja.get(i).getAsJsonObject().get("doc_count").getAsLong();
				resp102.body.tops[i]=tp;
				logger.info("tp "+tp.toString());
				logger.info(i);
			}}
			return resp102;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RzProcessException("102");
		}
	}
	
	String[] cities ={"广州","深圳","珠海","汕头","佛山","韶关","河源","梅州","惠州","汕尾","东莞","中山","江门","阳江","湛江","茂名","肇庆","清远","潮州","揭阳","云浮"};
	public Resp105 getBarData105(String domain, String startTime, String endTime) throws RzProcessException{
		try {
			String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
		String query = SysConstantsUtil.getQueryStringByDomain(domain);
		query=query.replace("now+8h", endTime);
		query=query.replace("now-4h", startTime);
		logger.info("the domain is "+ domain+"and  the bar_query is "+query);
		Search search = new Search.Builder(query)
                // multiple index or types can be added.
                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
                .build();
			SearchResult result;
			result = client.execute(search);
			logger.info("TEST "+result.getJsonString());
			JsonObject jsonObject = result.getJsonObject();
//			 jsonObject.get("aggregations").getAsJsonObject().has(memberName)
			JsonArray jsonArray = jsonObject.get("aggregations").getAsJsonObject().get("recent_12h").getAsJsonObject().get("cities").getAsJsonObject().get("buckets").getAsJsonArray();
			logger.info("351 "+jsonArray);
			Resp105 resp105 = new Resp105();
			resp105.getHead().setStatus_code("000");
			resp105.getHead().setStatus_desc("successful");
			Resp105.Body body = new Resp105.Body(new String[cities.length], new int[cities.length]);
			for(int j=0;j<cities.length;j++){
				body.docCounts[j]=0;
				for(int i=0;i<jsonArray.size();i++){
					System.out.println(i);
					String city = jsonArray.get(i).getAsJsonObject().get("key").getAsString();
					int docCount = jsonArray.get(i).getAsJsonObject().get("doc_count").getAsInt();
					if(cities[j].equals(city)){
						body.docCounts[j]=docCount;
						continue;
					}
					
					logger.info(jsonArray.get(i).getAsJsonObject().get("key").getAsString());
				}
			}
			body.citys=cities;
			resp105.body = body;
			logger.info("resp105.body "+resp105.body);
			return resp105;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RzProcessException("105");
		}
	}
	
	public Resp105[] getResps105(String domain, String startTime, String endTime) throws RzProcessException {
		Resp105[] r = new Resp105[2];
		String temp = domain;
		domain = temp+"高危操作";
		r[0]=getBarData105(domain, startTime, endTime);
		domain = temp+"设备升级数量";
		r[1]=getBarData105(domain, startTime, endTime);
		return r;
	}
	public Resp110[] getResps110(String domain, String startTime, String endTime) throws RzProcessException{
		Resp110[] r = new Resp110[3];
			String temp = domain;
			domain = temp+"机器日志对比图";
			r[0]=getBarData110(domain, startTime, endTime);
			domain = temp+"事件日志对比图";
			logger.info("r1"+domain);
			r[1]=getBarData110(domain, startTime, endTime);
			domain = temp+"预警日志对比图";
			r[2]=getBarData110(domain, startTime, endTime);
			return r;
	}
	
	public Resp106 getBarTableData(String city, String domain,String startTime, String endTime) throws RzProcessException{
		try {	
				String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
				SearchResult result;
				Resp106 resp106 = new Resp106();
				resp106.getHead().setStatus_code("000");
				resp106.getHead().setStatus_desc("successful");
				
				String query = SysConstantsUtil.getQueryStringByDomain(domain);
				query=query.replace("now+8h", endTime);
				query=query.replace("now-4h", startTime);
				logger.info("the query is "+query);
				query = query.replace("根据当前柱状图的变量", city);
				logger.info("new query "+query);
				Search search = new Search.Builder(query)
                // multiple index or types can be added.
                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
                .build();
		
			result = client.execute(search);
			logger.info("TEST "+result.getJsonString());
			logger.info("**************************************\n"+result.getTotal()+"\n**************************************");
			JsonObject jsonObject = result.getJsonObject();
			JsonArray ja = jsonObject.get("aggregations").getAsJsonObject().get("recent_12h").getAsJsonObject().get("neName").getAsJsonObject().get("buckets").getAsJsonArray();
//			logger.info(ja.get(ja.size()));
			resp106.body.datas = new Datas[ja.size()];
			for(int i=0;i<ja.size();i++){
				String querys ="{\n" + 
						"	\"query\": {\n" +
						"	\"term\":{\"neName\":"+ ja.get(i).getAsJsonObject().get("key") + "}\n" +
						"	},\n" +
						"	\"size\":\"1\"\n" +
						"}";
				Search search1 = new Search.Builder(querys)
		                // multiple index or types can be added.
		                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
		                .build();
				JsonObject jo = client.execute(search1).getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray().get(0).getAsJsonObject().get("_source").getAsJsonObject();
				Datas tp = new Datas();
				tp.city = jo.get("city").getAsString();
				tp.domain= jo.get("domain").getAsString();
				tp.eventTime = jo.get("eventTime").getAsString();
				tp.fileName = jo.get("fileName").getAsString();
				tp.neName = jo.get("neName").getAsString();
				tp.neType = jo.get("neType").getAsString();
				tp.vendor = jo.get("vendor").getAsString();
				tp.count = ja.get(i).getAsJsonObject().get("doc_count").getAsLong();
				resp106.body.datas[i]=tp;
				logger.info("tp "+tp.toString());
				logger.info(i);
			}
			return resp106;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RzProcessException("106");
		}
	}
	
	/**
	 * 获取漏斗图数据
	 * @param req108
	 * @return
	 */
	public Resp108 getFunnelData(String startTime, String endTime) throws RzProcessException { 
//		public long logCount; //日志总数
//		public long eventCount; //事件总数
//		public long preAlarmCount; //预警总数
//		public long faultCount; //故障总数
		
		try {
			String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
			Resp108 resp108 = new Resp108();
			Resp108.Body body = new Resp108.Body();
			body.items = new Item[3];
			Item item = new Item();
			String key = startTime + "-" + endTime;
			String query;
			Search search;
			SearchResult result;
			JsonObject jsonObject;
			List<String> valueList;
			if(totalCount.containsKey(key)) {
				item.name="机器日志";
				item.value=totalCount.get(key);
				body.items[0]=item;
			} else {
				query = SysConstantsUtil.getQueryStringByDomain("漏斗图机器日志");
				query=query.replace("now+8h", endTime);
				query=query.replace("now-4h", startTime);
				logger.info("the query is "+query);
				search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
				result = client.execute(search);
				logger.info("result.getJsonString() = " + result.getJsonString());
				jsonObject = result.getJsonObject();
				valueList = new ArrayList<>();
				
				getValuesFromJsonObject(valueList,jsonObject,"doc_count");
				item.name="机器日志";
				item.value=Long.parseLong(valueList.get(0));
				body.items[0]=item;
			}
			
			query = SysConstantsUtil.getQueryStringByDomain("漏斗图日志事件");
			query=query.replace("now+8h", endTime);
			query=query.replace("now-4h", startTime);
			logger.info("the query is "+query);
			search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			result = client.execute(search);
			logger.info("result.getJsonString() = " + result.getJsonString());
			jsonObject = result.getJsonObject();
			valueList = new ArrayList<>();
			
			getValuesFromJsonObject(valueList,jsonObject,"doc_count");
//			resp108.body.eventCount =  Long.parseLong(valueList.get(0));
			Item item1 = new Item();
			item1.name="日志事件";
			item1.value=Long.parseLong(valueList.get(0));
			body.items[1]=item1;
			
			query = SysConstantsUtil.getQueryStringByDomain("漏斗图预警");
			query=query.replace("now+8h", endTime);
			query=query.replace("now-4h", startTime);
			logger.info("the query is "+query);
			search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			result = client.execute(search);
			logger.info("result.getJsonString() = " + result.getJsonString());
			jsonObject = result.getJsonObject();
			valueList = new ArrayList<>();
			
			getValuesFromJsonObject(valueList,jsonObject,"doc_count");
//			resp108.body.faultCount =  Long.parseLong(valueList.get(0));
			Item item2 = new Item();
			item2.name="预警提示";
			item2.value=Long.parseLong(valueList.get(0));
			body.items[2]=item2;
			
			resp108.body = body;
			
			
			
			resp108.getHead().setStatus_code("000");
			resp108.getHead().setStatus_desc("successful");
			
			return resp108;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RzProcessException("108");
		}
	}
	public Resp110 getBarData110(String domain, String startTime, String endTime) throws RzProcessException{
		try {
		String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
		String[] cities={"中兴","华为","H3C","爱立信","Juniper","阿朗","思科","华三"};
		String query = SysConstantsUtil.getQueryStringByDomain(domain);
		query=query.replace("now+8h", endTime);
		query=query.replace("now-4h", startTime);
		logger.info("the domain is "+domain+"and  the bar_query is "+query);
		Search search = new Search.Builder(query)
                // multiple index or types can be added.
                .addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1])
                .build();
			SearchResult result;
			result = client.execute(search);
			logger.info("TEST "+result.getJsonString());
			JsonObject jsonObject = result.getJsonObject();
//			 jsonObject.get("aggregations").getAsJsonObject().has(memberName)
			
			List<String> keyList = new ArrayList<>();
			List<String> valueList = new ArrayList<>();
			getValuesFromJsonObject(keyList,jsonObject,"key");
			getValuesFromJsonObject(valueList,jsonObject,"doc_count");
			
//			JsonArray jsonArray = jsonObject.get("aggregations").getAsJsonObject().get("recent_12h").getAsJsonObject().get("cities").getAsJsonObject().get("buckets").getAsJsonArray();
//			logger.info("351 "+jsonArray);
			Resp110 resp105 = new Resp110();
			resp105.getHead().setStatus_code("000");
			resp105.getHead().setStatus_desc("successful");
			Resp110.Body body = new Resp110.Body(new String[cities.length], new int[cities.length]);
			for(int j=0;j<cities.length;j++){
				body.docCounts[j]=0;
				for(int i=0;i<keyList.size(); i++) {
					if(cities[j].equals(keyList.get(i))){
						body.docCounts[j]=Integer.valueOf(valueList.get(i+1));
						continue;
					}
				}
			}
			body.vendors=cities;
			resp105.body = body;
			logger.info("resp105.body "+resp105.body);
			return resp105;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RzProcessException("110");
		}
	}
	public void quartzJob(String start){
		try {
			new SysConstantsUtil().init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(df.format(new Date()));
		SysConstantsUtil.esUrl = "120.92.21.229";
		JestServiceImpl serviceImpl = new JestServiceImpl();
		Date dd = new Date();
//		2016-12-13 05:45 
//		serviceImpl.getCityAndDocCounts(startTime, endTime);
	}
	
	
	
	
	public void zzm(String name,String data,String endTime) throws RzProcessException{
		try {
			String query = SysConstantsUtil.getQueryStringByDomain("每小时数据");
			Date start = new Date();Date end = new Date();
			start.setHours(start.getHours()-1);
			start.setMinutes(0);start.setSeconds(0);
			end.setMinutes(0);end.setSeconds(0);
			query = query.replace("我的date", endTime);
			query = query.replace("我的name", name);
			String data1 = data.replace("\"","\\\"");
			query = query.replace("我的body", data1);
			Index.Builder builder = new Index.Builder(query);
			Index index = builder.index("zzm").type("typebo").build();
			JestResult jr;
			
				jr = client.execute(index);
			
			logger.info("the query is "+query);
			logger.info("result.getJsonString() = " + jr.getJsonString());
			JsonObject jsonObject = jr.getJsonObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Resp101 getCityAndDocCountsNew(String startTime, String endTime) throws RzProcessException{
		try {
			String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
			Resp101 resp101 = new Resp101();
			Resp101.Body body = new Resp101.Body();
			//传入时间的整点时间
			Date starts = df.parse(endTime);
			starts.setMinutes(0);starts.setSeconds(0);
			//传入的时间参数
			Date ends = df.parse(endTime);
			//starts1 12小时前
			Date starts1 = df.parse(endTime);
			starts1.setHours(starts1.getHours()-12);
			//ends1 11小时前整点时间
			Date ends1 = df.parse(endTime);
			ends1.setHours(ends1.getHours()-11);
			ends1.setMinutes(0);ends1.setSeconds(0);
			
			resp101.getHead().setStatus_code("000");
			resp101.getHead().setStatus_desc("successful");
			String query = SysConstantsUtil.getQueryStringByDomain("地图");
			String query1 = SysConstantsUtil.getQueryStringByDomain("地图");
			String query2 = SysConstantsUtil.getQueryStringByDomain("11小时数据");
			query = query.replace("now+8h", df.format(ends));
			query = query.replace("now-4h", df.format(starts));
			query1 = query1.replace("now+8h", df.format(ends1));
			query1 = query1.replace("now-4h", df.format(starts1));
			query2 = query2.replace("我的name", "地图");
			query2 = query2.replace("now+8h", df.format(starts));
			query2 = query2.replace("now-4h", df.format(ends1));
			logger.info("the query is "+query);
			logger.info("the query2 is "+query2);
			Search search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			Search search_1 = new Search.Builder(query1).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			Search new_search = new Search.Builder(query2).addIndex("zzm").build();
			SearchResult result = client.execute(search);
			SearchResult result1 = client.execute(search_1);
			SearchResult result_new = client.execute(new_search);
			logger.info("result.getJsonString() = " + result.getJsonString());
			logger.info("result1.getJsonString() = " + result1.getJsonString());
			logger.info("result_new.getJsonString() = " + result_new.getJsonString());
			JsonObject jsonObject = result.getJsonObject();
			JsonObject js = result_new.getJsonObject();
			JsonArray ja1 = result_new.getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
			JsonArray myArray = result_new.getJsonObject().get("hits").getAsJsonObject().getAsJsonArray("hits");
			System.out.println("myArray.size"+myArray.size());
			
			JsonObject jsonObject_new = result_new.getJsonObject();
			List<String> valueList;
			valueList = new ArrayList<>();
			getValuesFromJsonObject(valueList, jsonObject_new, "body");
			System.out.println("test valuelist "+valueList.get(0)+valueList.get(1)+valueList.get(2));
			String m = myArray.get(0).getAsJsonObject().get("_source").getAsJsonObject().get("body").toString().replace("\"[", "");
//			String[] a = Strings.split(valueList.get(1).replace("[",",").replace("]","").trim(),"," );
			String[] a = Strings.splitStringByCommaToArray(valueList.get(1).replace("[", "").replace("]",""));
			System.out.println("split  "+Arrays.toString(a));
			System.out.println("split "+a[10]);
//			System.out.println("split1 "+a[1]);
			m = m.replace("]\"", "");m=m.replace(",", "");
			int[] mm = SysConstantsUtil.stringToInts(m);
//			System.out.println(Arrays.toString(mm));
			System.out.println("1049 "+result_new.getJsonObject().get("hits").getAsJsonObject().getAsJsonArray("hits"));
			String test = ja1.get(1).getAsJsonObject().get("_source").getAsJsonObject().get("body").toString();
			System.out.println("1051 "+new JsonParser().parse(test).toString());
//			logger.info(ja1.get(1).getAsJsonObject().get("_source").getAsJsonObject().get("body").toString().replace("\\\"", "\""));
			JsonArray ja = jsonObject.get("aggregations").getAsJsonObject().get("recent_12h").getAsJsonObject().get("cities").getAsJsonObject().get("buckets").getAsJsonArray();
			logger.info(ja.size()+"******************************"+ja.toString());
			int[] temp = null;
//			if(ja.size() == 0) {System.out.println("ja size 0");
//				body.citys = new String[cities.length];
//				body.docCounts = new int[cities.length];
//				int i=0;
//				for(String cityName : cities) {
//					body.citys[i] = cityName;
//					body.docCounts[i] = 0;
//					i++;
//				}
//			} 
//			else 
				body.citys = new String[cities.length];
				body.docCounts = new int[cities.length];
				int[][] datass = new int[myArray.size()][];
				for(int j=0;j<ja.size();j++){
					System.out.println(ja.get(j).getAsJsonObject().get("key").getAsString());
					int i=0;Boolean flag = true;
					for(String cityName : cities) {
						System.out.print(cityName);
						body.citys[i] = cityName;
						if(cityName.equals(ja.get(j).getAsJsonObject().get("key").getAsString())){
							System.out.println("cityname is equal");
							if(ja.size()>0) {System.out.println("ja size > 0");
						body.docCounts[i] = ja.get(j).getAsJsonObject().get("doc_count").getAsInt();
							}else{
								body.docCounts[i] = 0;
							}
						temp = new int[cities.length];
						for(int k = 0;k<myArray.size();k++){
							String data = myArray.get(k).getAsJsonObject().get("_source").getAsJsonObject().get("body").toString().replace("\"[", "");
							data = data.replace("]\"", "");data=data.replace(",", "");
							int[] datas = SysConstantsUtil.stringToInts(data);
							temp = SysConstantsUtil.getSumArray(temp, datas);
							System.out.println(Arrays.toString(temp));
//							System.out.println(data);
						}
						}
//						else if(0==body.docCounts[i]){
//							body.docCounts[i] = 0;
//						}
						i++;
					}
			}
			System.out.println(Arrays.toString(temp));
			System.out.println("1084 "+Arrays.toString(body.docCounts));
			System.out.println("1103" + Arrays.toString(SysConstantsUtil.getSumArray(temp, body.docCounts)));
			
			
//			else if(ja.size()>0) {
//				System.out.println(ja.get(0).getAsJsonObject().get("key").getAsString());
//				body.citys = new String[ja.size()];
//				body.docCounts = new int[ja.size()];
//				for(int i=0;i<ja.size();i++){
//					body.citys[i]=ja.get(i).getAsJsonObject().get("key").getAsString();
//					body.docCounts[i]=ja.get(i).getAsJsonObject().get("doc_count").getAsInt();
//				}
//			}
			resp101.body=body;
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RzProcessException("101");
		}
	}
	public Resp108 getFunnelDataNew(String startTime, String endTime) throws RzProcessException { 
//		public long logCount; //日志总数
//		public long eventCount; //事件总数
//		public long preAlarmCount; //预警总数
//		public long faultCount; //故障总数
		
		try {//传入时间的整点时间
			Date starts = df.parse(endTime);
			starts.setMinutes(0);starts.setSeconds(0);
			//传入的时间参数
			Date ends = df.parse(endTime);
			//starts1 12小时前
			Date starts1 = df.parse(endTime);
			starts1.setHours(starts1.getHours()-12);
			//ends1 11小时前整点时间
			Date ends1 = df.parse(endTime);
			ends1.setHours(ends1.getHours()-11);
			ends1.setMinutes(0);ends1.setSeconds(0);
			
			String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
			Resp108 resp108 = new Resp108();
			Resp108.Body body = new Resp108.Body();
			body.items = new Item[3];
			Item item = new Item();
			String key = startTime + "-" + endTime;
			String query;String query1;String query2;
			Search search,search_1,new_search;
			SearchResult result,result1,result_new;
			JsonObject jsonObject,jsonObject1,jsonObject_new;
			List<String> valueList,valueList1,valueList_new;
			if(totalCount.containsKey(key)) {
				item.name="机器日志";
				item.value=totalCount.get(key);
				body.items[0]=item;
			} else {
				query = SysConstantsUtil.getQueryStringByDomain("漏斗图机器日志");
				query=query.replace("now+8h", df.format(ends));
				query=query.replace("now-4h", df.format(starts));
				query1 = SysConstantsUtil.getQueryStringByDomain("漏斗图机器日志");
				query1 = query1.replace("now+8h", df.format(ends1));
				query1 = query1.replace("now-4h", df.format(starts1));
				query2 = SysConstantsUtil.getQueryStringByDomain("11小时数据");
				query2 = query2.replace("我的name", "漏斗图机器日志");
				query2 = query2.replace("now+8h", df.format(starts));
				query2 = query2.replace("now-4h", df.format(ends1));
				logger.info("the query is "+query);
				search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
				search_1 = new Search.Builder(query1).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
				new_search = new Search.Builder(query2).addIndex("zzm").build();
				result = client.execute(search);
				result1 = client.execute(search_1);
				result_new = client.execute(new_search);
				logger.info("result.getJsonString() = " + result.getJsonString());
				logger.info("result1.getJsonString() = " + result1.getJsonString());
				logger.info("result_new.getJsonString() = " + result_new.getJsonString());
				jsonObject = result.getJsonObject();jsonObject1 = result1.getJsonObject();jsonObject_new = result_new.getJsonObject();
				valueList = new ArrayList<>();valueList1 = new ArrayList<>();valueList_new = new ArrayList<>();
				getValuesFromJsonObject(valueList,jsonObject,"doc_count");
				getValuesFromJsonObject(valueList1,jsonObject1,"doc_count");
				getValuesFromJsonObject(valueList_new,jsonObject_new,"body");

				int first = 0;
				for(String s:valueList){
					first+=Integer.parseInt(s);
				}
				item.name="机器日志";
				item.value=Long.parseLong(valueList.get(0)+valueList1.get(0)+first);
				body.items[0]=item;
			}
			
			query = SysConstantsUtil.getQueryStringByDomain("漏斗图日志事件");
			query=query.replace("now+8h", df.format(ends));
			query=query.replace("now-4h", df.format(starts));
			query1 = SysConstantsUtil.getQueryStringByDomain("漏斗图日志事件");
			query1 = query1.replace("now+8h", df.format(ends1));
			query1 = query1.replace("now-4h", df.format(starts1));
			query2 = SysConstantsUtil.getQueryStringByDomain("11小时数据");
			query2 = query2.replace("我的name", "漏斗图日志事件");
			query2 = query2.replace("now+8h", df.format(starts));
			query2 = query2.replace("now-4h", df.format(ends1));
			logger.info("the query is "+query);
			search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			search_1 = new Search.Builder(query1).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			new_search = new Search.Builder(query2).addIndex("zzm").build();
			result1 = client.execute(search_1);
			result_new = client.execute(new_search);
			result = client.execute(search);
			logger.info("result.getJsonString() = " + result.getJsonString());
			jsonObject = result.getJsonObject();jsonObject1 = result1.getJsonObject();jsonObject_new = result_new.getJsonObject();
			valueList = new ArrayList<>();valueList1 = new ArrayList<>();valueList_new = new ArrayList<>();
			getValuesFromJsonObject(valueList,jsonObject,"doc_count");
			getValuesFromJsonObject(valueList1,jsonObject1,"doc_count");
			getValuesFromJsonObject(valueList_new,jsonObject_new,"body");
//			resp108.body.eventCount =  Long.parseLong(valueList.get(0));
			int second = 0;
			for(String s:valueList_new){
				second+=Integer.parseInt(s);
			}
			Item item1 = new Item();
			item1.name="日志事件";
			item1.value=Long.parseLong(valueList.get(0)+valueList1.get(0)+second);
			body.items[1]=item1;
			
			query = SysConstantsUtil.getQueryStringByDomain("漏斗图预警");
			query=query.replace("now+8h", df.format(ends));
			query=query.replace("now-4h", df.format(starts));
			query1 = SysConstantsUtil.getQueryStringByDomain("漏斗图预警");
			query1 = query1.replace("now+8h", df.format(ends1));
			query1 = query1.replace("now-4h", df.format(starts1));
			query2 = SysConstantsUtil.getQueryStringByDomain("11小时数据");
			query2 = query2.replace("我的name", "漏斗图预警提示");
			query2 = query2.replace("now+8h", df.format(starts));
			query2 = query2.replace("now-4h", df.format(ends1));
			logger.info("the query is "+query);
			search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			search_1 = new Search.Builder(query1).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			new_search = new Search.Builder(query2).addIndex("zzm").build();
			result = client.execute(search);
			result1 = client.execute(search_1);
			result_new = client.execute(new_search);
			logger.info("result.getJsonString() = " + result.getJsonString());
			logger.info("result1.getJsonString() = " + result1.getJsonString());
			logger.info("result_new.getJsonString() = " + result_new.getJsonString());
			jsonObject = result.getJsonObject();jsonObject1 = result1.getJsonObject();jsonObject_new = result_new.getJsonObject();
			valueList = new ArrayList<>();valueList1 = new ArrayList<>();valueList_new = new ArrayList<>();
			
			getValuesFromJsonObject(valueList,jsonObject,"doc_count");getValuesFromJsonObject(valueList1,jsonObject1,"doc_count");
			getValuesFromJsonObject(valueList_new,jsonObject_new,"body");
//			resp108.body.faultCount =  Long.parseLong(valueList.get(0));
			int thirds = 0;
			for(String s:valueList){
				thirds+=Integer.parseInt(s);
			}
			Item item2 = new Item();
			item2.name="预警提示";
			item2.value=Long.parseLong(valueList.get(0)+valueList1.get(0)+thirds);
			body.items[2]=item2;
			
			resp108.body = body;
			
			
			
			resp108.getHead().setStatus_code("000");
			resp108.getHead().setStatus_desc("successful");
			
			return resp108;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RzProcessException("108");
		}
	}
	public Resp103 getRangeStatNew(String startTime, String endTime) throws RzProcessException {
		try {
			Resp103 resp103 = new Resp103();
			resp103.body.statItems = new StatItem[3];
			String[] suffix = SysConstantsUtil.changeIndexNameByDate(endTime);
			StatItem statItem = new StatItem();
			statItem.rangeName = "核心网";
			resp103.body.statItems[1] = statItem;
			
			String query = SysConstantsUtil.getQueryStringByDomain("核心网汇总");
			query = query.replace("now+8h", endTime);
			query = query.replace("now-4h", startTime);
			System.out.println("my query: "+query);
			Search search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			SearchResult result = client.execute(search);
			logger.info("core result.getJsonString() = " + result.getJsonString());
			JsonObject jsonObject = result.getJsonObject();
			List<String> docCountCountList = new ArrayList<>();
			List<String>neCountList = new ArrayList<>();
			
			getValuesFromJsonObject(docCountCountList,jsonObject,"total");
			getValuesFromJsonObject(neCountList,jsonObject,"value");
			
			if(docCountCountList.size()>1) {
				statItem.logCount = Long.valueOf(docCountCountList.get(1));
			}
			if(neCountList.size()>0) {
				statItem.neCount = Long.valueOf(neCountList.get(0));
			}
			statItem.size = SysConstantsUtil.logItemSize*statItem.logCount/SysConstantsUtil.BYTE_TO_GB;
			statItem.size =Double.parseDouble(dfNum.format(statItem.size));
			
			
			
			statItem = new StatItem();
			resp103.body.statItems[2] = statItem;
			statItem.rangeName = "数据网";
			
			
			query = SysConstantsUtil.getQueryStringByDomain("数据网汇总");
			query = query.replace("now+8h", endTime);
			query = query.replace("now-4h", startTime);
			search = new Search.Builder(query).addIndex(SysConstantsUtil.indexName+suffix[0]).addIndex(SysConstantsUtil.indexName+suffix[1]).build();
			result = client.execute(search);
			logger.info("data result.getJsonString() = " + result.getJsonString());
			jsonObject = result.getJsonObject();
			docCountCountList = new ArrayList<>();
			neCountList = new ArrayList<>();
			
			getValuesFromJsonObject(docCountCountList,jsonObject,"total");
			getValuesFromJsonObject(neCountList,jsonObject,"value");
			
			if(docCountCountList.size()>1) {
				statItem.logCount = Long.valueOf(docCountCountList.get(1));
			}
			if(neCountList.size()>0) {
				statItem.neCount = Long.valueOf(neCountList.get(0));
			}
			statItem.size = SysConstantsUtil.logItemSize*statItem.logCount/SysConstantsUtil.BYTE_TO_GB;
			statItem.size =Double.parseDouble(dfNum.format(statItem.size));
			
			//全网
			statItem = new StatItem();
			resp103.body.statItems[0]=statItem;
			statItem.rangeName = "全网";
			statItem.logCount = resp103.body.statItems[1].logCount+resp103.body.statItems[2].logCount;
			statItem.neCount = resp103.body.statItems[1].neCount+resp103.body.statItems[2].neCount;
			statItem.size = SysConstantsUtil.logItemSize*statItem.logCount/SysConstantsUtil.BYTE_TO_GB;
			statItem.size =Double.parseDouble(dfNum.format(statItem.size));
			
			resp103.getHead().setStatus_code("000");
			resp103.getHead().setStatus_desc("sucessful.");
			return resp103;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RzProcessException("103");
		}
	}
	public void addDataPerHour(JestServiceImpl serviceImpl){
		try {
		Date start = new Date();Date end = new Date();
		start.setHours(start.getHours()-1);
		start.setMinutes(0);start.setSeconds(0);
		end.setMinutes(0);end.setSeconds(0);
		String startTime = df.format(start);String endTime = df.format(end);
		String r101 = JsonBinder.buildNormalBinder().toJson(serviceImpl.getCityAndDocCounts(startTime, endTime).body.docCounts);
		Resp108 r108 = serviceImpl.getFunnelData(startTime, endTime);
		Resp103 r103 = serviceImpl.getRangeStatNew(startTime, endTime);
		Item[] funnel_data = r108.body.items;
		StatItem[] range_data = r103.body.statItems;
//		System.out.println("funnel "+JsonBinder.buildNormalBinder().toJson(r108.body.items));
		System.out.println("funnel "+r108.body.items[0].name+"  "+r108.body.items[0].value);
		System.out.println("range "+r103);
//		r101 = r101.replace("\"","\\\"");
//		serviceImpl.zzm("地图", r101, endTime);
//		for(int i= 0;i<funnel_data.length;i++){
//			serviceImpl.zzm("漏斗图"+funnel_data[i].name, funnel_data[i].value+"", endTime);
//		}
//		serviceImpl.zzm(name, data, endTime);	
		for(int j=1;j<range_data.length;j++){
			serviceImpl.zzm(range_data[j].rangeName+"汇总", "{"+range_data[j].logCount+","+range_data[j].neCount+","+range_data[j].size+"}", endTime);
		}
		} catch (RzProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void quartzTest(){
		try {
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
//		JobDetail job = new Job(JestServiceImpl.class).withIdentity("job1", "group1").build();  
		JobDetail jd = newJob(JestServiceImpl.class).withIdentity("job1", "group1").build();
		CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1").withSchedule(cronSchedule("0 0 * * * ?")).build();  
		Date ft = sched.scheduleJob(jd, trigger); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");  
		System.out.println(jd.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression()); 
		sched.start();
		Thread.sleep(60L*10000L);
		
		sched.shutdown(true); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		try {
			new SysConstantsUtil().init();
			System.out.println(df.format(new Date()));
			SysConstantsUtil.esUrl = "120.92.21.229";
			JestServiceImpl serviceImpl = new JestServiceImpl();
			
//			Req109 req109 = new Req109();
//			req109.body.startTime = "2016-05-03 00:00:10";
//			req109.body.endTime = "2016-05-02 17:00:00";
//			Req108 req108 = new Req108();
//			Resp108 funnelData = serviceImpl.getFunnelData(req108);
//			logger.info("Resp108 = "+JsonBinder.buildNormalBinder().toJson(funnelData));
			
//			Resp109 dataSizeTrends = serviceImpl.getDataSizeTrends("数据量动态统计", req109);
//			System.out.println("Resp109 = "+JsonBinder.buildNormalBinder().toJson(dataSizeTrends));
			
//			Req104 req104 = new Req104();
//			req104.body.domain = "CMNET";
//			startTime = "2016-07-11 01:10:10";
//			endTime = "2016-05-04 20:10:10";
//					
//			Resp104 trendItem = serviceImpl.getTrendItem(req104);
//			System.out.println("379."+JsonBinder.buildNormalBinder().toJson(trendItem));
//			Req110 req110 = new Req110();
//			req110.body.domain = "核心网";
//			req110.body.startTime = "2016-05-01 02:10:10";
//			req110.body.endTime = "2016-08-01 18:10:10";
////			req106.body.city = "广州";
			Req103 req103 = new Req103();
			req103.body.startTime = "2016-05-01 02:10:10";
			req103.body.endTime = "2016-12-14 12:10:10";
			Date zzm = new Date();
			Date start = new Date();Date end = new Date();
			start.setMinutes(0);start.setSeconds(0);
			end.setMinutes(0);end.setSeconds(0);
			for(int i =0;i<2;i++){
				if(i==0){
					start.setHours(start.getHours()-1);
				}else{
					start.setHours(start.getHours()-1);
					end.setHours(end.getHours()-1);
				}
				System.out.println(df.format(start)+" "+df.format(end));
			};
//			req103.body.endTime.substring(5, 7);
//			String my = req103.body.endTime.substring(0, 10);
//			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
//			Date dj = sdf.parse(my);
//			Date dz = sdf.parse(my);
//			dz.setDate(dz.getDate()-1);
//			System.out.println(sdf.format(dj));
//			System.out.println("ending"+req103.body.endTime.substring(0, 10));
//			Resp103 resp103 = serviceImpl.getRangeStat(req103.body.startTime, req103.body.endTime);
//			Resp101 resp101 = serviceImpl.getCityAndDocCounts(req103.body.startTime, req103.body.endTime);
//			serviceImpl.addDataPerHour(serviceImpl);
			serviceImpl.getCityAndDocCountsNew("", df.format(zzm));
//			serviceImpl.addDataPerHour(serviceImpl);
//			serviceImpl.getFunnelDataNew(df.format(start), df.format(end));
//			Resp106 trendItem = serviceImpl.getBarTableData(req106);
//			Resp110 r = serviceImpl.getBarData110("核心网", "2016-05-01 02:10:10", "2016-08-01 18:10:10");
//			Resp110 r2[]=serviceImpl.getResps110("核心网", "2016-05-01 02:10:10", "2016-08-01 18:10:10");
//			System.out.println("367."+JsonBinder.buildNormalBinder().toJson(trendItem));
//			System.out.println("resp" + JsonBinder.buildNormalBinder().toJson(resp101));
			String endtime = "2016-12-14 12:10:10";
			Date xxx = new Date();
			
			Date starts = df.parse(endtime);
			starts.setMinutes(0);starts.setSeconds(0);
			Date ends = df.parse(endtime);
			Date starts1 = df.parse(endtime);
			starts1.setHours(starts1.getHours()-12);
			Date ends1 = df.parse(endtime);
			ends1.setHours(ends1.getHours()-11);
			ends1.setMinutes(0);ends1.setSeconds(0);
			System.out.println(df.format(starts)+" to "+df.format(ends));
			System.out.println(df.format(starts1)+" to "+df.format(ends1));
//			System.out.println(starts+" to "+ends);
//			serviceImpl.getCityAndDocCountsNew("", df.format(xxx));
			serviceImpl.close();
			System.out.println(df.format(zzm));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		try {
		new SysConstantsUtil().init();
		JestServiceImpl serviceImpl = new JestServiceImpl();
		serviceImpl.addDataPerHour(serviceImpl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
