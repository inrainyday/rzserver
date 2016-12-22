package com.boco.rzserver.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import com.boco.rzserver.constants.SysConstantsUtil;
import com.boco.rzserver.json.JsonBinder;
import com.boco.rzserver.model.PreAlarmRule;
import com.boco.rzserver.model.json.Req107;
import com.boco.rzserver.model.json.Resp107;
import com.boco.rzserver.model.json.Resp107.PreAlarm;
import com.boco.rzserver.service.IEsService;


/**
 * 
 * @author lij
 *
 */
@Service
public class EsServiceImpl implements IEsService {

	JsonBinder jsonBinder = JsonBinder.buildNormalBinder();
	
	private TransportClient client;
	
	static Logger logger = Logger.getLogger(EsServiceImpl.class);

	@SuppressWarnings("resource")
	public EsServiceImpl() {
		try {
			new SysConstantsUtil().init();
			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", SysConstantsUtil.clusterName).build();
			this.client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(SysConstantsUtil.esUrl, SysConstantsUtil.esPort));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取预警数据
	 */
	public Resp107 getPreAlarm(Req107 req107) {
		Resp107 resp107 = new Resp107();
		
		List<PreAlarmRule> preAlarmRules = SysConstantsUtil.getPreAlarmRules(req107.body.domain);
		BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
		
		if(preAlarmRules==null || preAlarmRules.size()<1) {
			resp107.body.preAlarms = new Resp107.PreAlarm[0];
			return resp107;
		}
//		<preAlarmRule name="[事件告警]设备有单板被异常拔出" domain="CMNET/IP承载网/城域网" neType="路由器/交换机" vendor="华为" logType="系统日志" >
		for(PreAlarmRule alarmRule : preAlarmRules) {
			String[] keys = alarmRule.getKeys().split("@@");
			
			BoolQueryBuilder mustQuery = QueryBuilders.boolQuery();
			for(String key : keys) {
				mustQuery.must(QueryBuilders.matchPhraseQuery("body", key.trim()));
			}
					
			mustQuery.must(QueryBuilders.termQuery("domain", alarmRule.getDomain()))
					.must(QueryBuilders.termQuery("neType", alarmRule.getNeType()))
					.must(QueryBuilders.termQuery("vendor", alarmRule.getVendor()))
					.must(QueryBuilders.termQuery("logType", alarmRule.getLogType()))
					;
			shouldQuery.should(mustQuery);
		}
		
		// 请求
		SearchRequestBuilder searchBuilder = client.prepareSearch(SysConstantsUtil.indexName).setTypes(SysConstantsUtil.typeName);
		searchBuilder.setQuery(shouldQuery);
		if(req107.body.index > 0 &&  req107.body.size >0 ) {
			searchBuilder.setFrom(req107.body.size*(req107.body.index-1));
			searchBuilder.setSize(req107.body.size);
		}
		searchBuilder.addSort("eventTime", SortOrder.DESC);
		SearchResponse response = searchBuilder.execute().actionGet();
		logger.info("getPreAlarm = " + response);
		SearchHits hits = response.getHits();
		resp107.body.preAlarms = new Resp107.PreAlarm[hits.getHits().length];
		for (int i = 0; i < hits.getHits().length; i++) {
			Map<String, Object> source = hits.getHits()[i].getSource();
			PreAlarm preAlarm = new Resp107.PreAlarm();
			preAlarm.city = (String) source.get("city");
			preAlarm.domain = (String) source.get("domain");
			preAlarm.logType = (String) source.get("logType");
			preAlarm.neName = (String) source.get("neName");
			preAlarm.neType = (String) source.get("neType");
			preAlarm.vendor = (String) source.get("vendor");
			preAlarm.alarmTime = (String) source.get("eventTime");
			
			//再次判断使用的那个规则
			for(PreAlarmRule alarmRule : preAlarmRules) {
				String body = (String) source.get("body"); 
				String[] keys = alarmRule.getKeys().split("@@");
				boolean isRule = true; 
				for(String key : keys) {
					//如果不包含，则不匹配
					if(!body.toLowerCase().contains(key.toLowerCase())) {
						isRule = false;
						break;
					}
				}
				//如果匹配，则赋值
				if(isRule){
					preAlarm.alarmLevel = alarmRule.getAlarmLevel();
					preAlarm.alarmTitle = alarmRule.getAlarmTitle();
					preAlarm.alarmText = alarmRule.getAlarmText().replace("${body}", body);
					preAlarm.keys = alarmRule.getKeys().replace("@@", " ");
					resp107.body.preAlarms[i] = preAlarm;
					break;
				} else {
					logger.warn("body not matched!!! = " + body);
				}
			}
			resp107.getHead().setStatus_code("000");
			resp107.getHead().setStatus_desc("successful.");
			
		}
		return resp107;
	}
	





	public static void main(String[] args) throws ParseException {
//		 Resp101 cityAndDocCountsByEventTime = (Resp101) new EsServiceImpl().getCityAndDocCountsByEventTime("", "");
		// new EsServiceImpl().dateHistogramFacet();
//		 new EsServiceImpl().search();
		// new EsServiceImpl().histogramFacet();
		// System.out.println(cityAndDocCountsByEventTime);
//		 new EsServiceImpl().getTop5("","");
//		 System.out.println( new EsServiceImpl().getTop5("","",""));
//		new EsServiceImpl().getRangeStat("", "");
//		Resp106 a =new EsServiceImpl().getNeNameAndCounts("", "", "", "广州");
//		System.out.println(a.toString());
//		Resp106 a =new EsServiceImpl().getNeNameAndCounts("", "", "", "广州");
//		System.out.println(a.toString());
		try {
			new SysConstantsUtil().init();
			Req107 req107 = new Req107();
			req107.body.index = 1;
			req107.body.size = 10;
			req107.body.domain = "PS域主设备";
			Resp107 preAlarm = new EsServiceImpl().getPreAlarm(req107);
			System.out.println(JsonBinder.buildNonNullBinder().toJson(preAlarm));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
