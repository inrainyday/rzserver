package com.boco.rzserver.constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.boco.rzserver.model.PreAlarmRule;

/**
 * 
 * @author lij
 *
 */
@Component
public class SysConstantsUtil  {
	
	
	public static String esUrl = "120.92.21.229";//120.92.21.229 //188.1.141.213
	public static int esPort = 9300;
	public static String clusterName = "my-elasticsearch";
	public static String indexName = "logbo";
	public static String typeName = "typebo";
	public static long logItemSize = 314; //单位byte
	public static final double BYTE_TO_GB = 1024*1024*1024; 
	
	private static final Logger logger = LoggerFactory.getLogger(SysConstantsUtil.class);
	private static Map<String,String> domainQueryMap = new HashMap<>();
	private static Map<String, List<PreAlarmRule>> preAlarmRulesMap = new HashMap<>();
	
	private Properties p = new Properties();

	public String getProperty(String key) {
		if (key == null)
			return null;
		else
			return this.p.getProperty(key);
	}
	
	public int getIntProperty(String key) {
		if (key == null) {
			return 0;
		}
		else {
			return Integer.valueOf(this.p.getProperty(key));
		}
	}
	
	boolean isInit = false;

	@PostConstruct
	public synchronized void init() throws IOException {
		if(!isInit) {
			initProperties();
			initEsQueryXml();
			initPreAlarmRuleXml();
		}
		isInit = true;
	}

	
	private void initPreAlarmRuleXml() {
		String fileName = 
				this.getClass().getResource("/").getPath() + "preAlarmConfig.xml";
//		"/D:/workspaces/eclipse_workspace/rzserver/target/classes/preAlarmConfig.xml";
		File myXML = new File(fileName);
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(myXML);
			Element queryItems = document.getRootElement();
			List<Element> elements = queryItems.elements();
			for(Element item : elements) {
				String domainTmp = item.attributeValue("domain");
				String[] domains = domainTmp.split("/");
				String[] neTypes = item.attributeValue("neType").split("/");
				
				for(String domain: domains) {
					for(String neType : neTypes) {
						PreAlarmRule preAlarmRule = new PreAlarmRule();
						if(preAlarmRulesMap.get(domain)==null) {
							ArrayList<PreAlarmRule> arrayList = new ArrayList<PreAlarmRule>();
							preAlarmRulesMap.put(domain, arrayList);
						}
						
						preAlarmRule.setName(item.attributeValue("name"));
						preAlarmRule.setDomain(domain.trim());
						preAlarmRule.setNeType(neType.trim());
						preAlarmRule.setLogType(item.attributeValue("logType"));
						preAlarmRule.setVendor(item.attributeValue("vendor"));
						List<Element> alarmElements = item.elements();
						for(Element alarmItem : alarmElements) {
							QName qName = alarmItem.getQName();
							if("keys".equals(qName.getName())) {
								preAlarmRule.setKeys(alarmItem.getTextTrim());
							} else if("alarmTitle".equals(qName.getName())) {
								preAlarmRule.setAlarmTitle(alarmItem.getTextTrim());
							} else if("alarmText".equals(qName.getName())) {
								preAlarmRule.setAlarmText(alarmItem.getTextTrim());
							} else if("alarmLevel".equals(qName.getName())) {
								preAlarmRule.setAlarmLevel(alarmItem.getTextTrim());
							}
						}
						preAlarmRulesMap.get(domain).add(preAlarmRule);
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void initEsQueryXml() {
		String fileName = 
				this.getClass().getResource("/").getPath() + "esQueryConfig.xml";
//		"/D:/workspaces/eclipse_workspace/rzserver/target/classes/esQueryConfig.xml";
		File myXML = new File(fileName);
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(myXML);
			Element queryItems = document.getRootElement();
			List<Element> elements = queryItems.elements();
			for(Element item : elements) {
				String text = item.getText();
				String domain = item.attribute("domain").getStringValue();
				String name = item.attribute("name").getStringValue();
				domainQueryMap.put(domain, text);
//				logger.info("domain = " + domain + " name = " + name + " text = " + text);
				
			}
		} catch (DocumentException e) {
			logger.error(e.getMessage());
		}
	}

	private void initProperties() throws IOException {
		ClassPathResource resource=new ClassPathResource("rzserver.properties");
		if (!resource.exists()) {
			logger.error("file rzserver.properties is missing, check the file!");
		} else {
			InputStream inputStream =resource.getInputStream();
			this.p.load(inputStream);
			inputStream.close();
			logger.info("init rzserver success !" + p);
			esUrl = this.getProperty("esUrl");
			esPort = this.getIntProperty("esPort");
			clusterName = this.getProperty("clusterName");
			typeName = this.getProperty("typeName");
			indexName = this.getProperty("indexName");
			logItemSize = this.getIntProperty("logItemSize");
			
		}
		
	}

	
	public static String getQueryStringByDomain(String domain) {
		return domainQueryMap.get(domain);
	}
	
	public static List<PreAlarmRule> getPreAlarmRules(String domain) {
		 //PS域主设备,PS域
		//数通设备 PS域数通
		//承载网 
		if("PS域主设备".equals(domain)) {
			domain = "PS域";
		} else if("数通设备".equals(domain)) {
			domain = "PS域数通";
			
		} else if("承载网".equals(domain)) {
			String[] domains = new String[]{"CS_CE","PS_CE","IP承载网"};
			List<PreAlarmRule> list = new ArrayList<>();
			for(String tmp : domains) {
				list.addAll(preAlarmRulesMap.get(tmp));
			}
			return list;
		}
		return preAlarmRulesMap.get(domain);
	}
	
	public static String[] changeIndexNameByDate(String endtime) throws Exception{
		
		String[] suffix = new String[2];
		String temp = endtime.substring(0, 10);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date start = sdf.parse(temp);
		Date end = sdf.parse(temp);
		System.out.println(start);
		start.setDate(start.getDate()-1);
		suffix[0] = sdf.format(start);
		suffix[1] = sdf.format(end);
		return suffix;
	}
	public static int[] getSumArray(int[] a1, int[] a2) {
        int[] sumArray = new int[a2.length] ;
        if (a1.length != a2.length) {
            System.out.println("两个数组的长度不同，不能相加！");
        } else {
            for (int i=0;i<a2.length;i++) {
                sumArray[i] = a1[i] + a2[i] ;
            }
        }
        return sumArray;
    }
	public static int[] stringToInts(String s){
		   int[] n = new int[s.length()]; 
		   for(int i = 0;i<s.length();i++){
		     n[i] = Integer.parseInt(s.substring(i,i+1));
		   }
		   return n;
		}
	public static int[] stringArrayToIntArray(String[] s){
		   int[] n = new int[s.length]; 
		   for(int i = 0;i<s.length;i++){
		     n[i] = Integer.parseInt(s[i]);
		   }
		   return n;
		}
	public static void main(String[] args) {
		new SysConstantsUtil().initPreAlarmRuleXml();
	}

}
