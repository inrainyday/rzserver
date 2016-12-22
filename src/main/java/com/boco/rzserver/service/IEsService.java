package com.boco.rzserver.service;

import com.boco.rzserver.model.json.Req107;
import com.boco.rzserver.model.json.Resp107;

/**
 * 
 * @author lij
 *
 */
public interface IEsService {
	
	/**
	 * 获取预警数据
	 */
	public Resp107 getPreAlarm(Req107 req107);

}
