package com.boco.rzserver.processor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;


/**
 * 
 * @author lij
 *
 */
@Component
public class CommandDispatcher {

	private Map<Integer,IProcessor> commandMap=new HashMap<>();

	public Map<Integer, IProcessor> getCommandMap() {
		return commandMap;
	}

	/**
	 * 加入到命令和处理器对应的map里面
	 * @param processor
	 */
    public void addProcessor(IProcessor processor){
    	this.commandMap.put(processor.getCommand(), processor);
    }
	
	
	/**
	 * 得到对应的处理器,这里的处理类，是在每个处理类初始化的时候，被加进来了的
	 * @param command
	 * @return
	 */
	public IProcessor getProcessor(int command){
		if( command > 0 ){
			return commandMap.get(command);
		}else{
			return null;
		}
	}
	
}
