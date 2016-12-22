package com.boco.rzserver.processor;

import com.boco.rzserver.exception.RzProcessException;

/**
 * 
 * @author lij
 *
 */
public interface IProcessor {
	
	
	 /**
     * 得到处理的命令码
     * @return
     */
	public int getCommand();
	
	/**
	 * 处理的方法
	 * @param json
	 * @return
	 */
	public String process(String json) throws RzProcessException;

}
