package com.boco.rzserver.service;

import com.boco.rzserver.exception.RzProcessException;

/**
 * 
 * @author lij
 *
 */
public interface ICacheService {

	public String  getCityAndDocCounts(String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException;
	
	public String getRangeStat(String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException;

	public String getFunnelData(String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException;

	public String getDataSizeTredns(String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException;

	public String getTrendItem(String domain, String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException;

	public String getBarTableData(String city, String domain, String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException;

	public String getResps105(String domain, String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException;

	public String getResp110(String domain, String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException;
	
	public String getResp110_2(String domain, String startTime, String endTime, boolean lazyCacheFlag) throws RzProcessException;

}
