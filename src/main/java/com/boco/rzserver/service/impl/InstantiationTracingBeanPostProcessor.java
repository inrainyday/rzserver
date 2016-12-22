package com.boco.rzserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.boco.rzserver.service.ICacheService;
import com.boco.rzserver.service.cache.CacheManagerThread;

public class InstantiationTracingBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {  
	
	@Autowired
	ICacheService cacheSrv;

	
	@Override  
	public void onApplicationEvent(ContextRefreshedEvent event) {  
		//需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。  
		//root application context 没有parent，他就是老大.
		if(event.getApplicationContext().getParent() == null) {
			new CacheManagerThread(cacheSrv).start();
		}
	}  
}  

