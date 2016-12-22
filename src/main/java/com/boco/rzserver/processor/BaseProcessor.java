package com.boco.rzserver.processor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.boco.rzserver.json.JsonBinder;
import com.boco.rzserver.message.MessageContainer;
import com.boco.rzserver.model.json.RespHead;
import com.boco.rzserver.service.ICacheService;

/**
 * 
 * @author lij
 *
 */
public abstract class BaseProcessor implements IProcessor{
	
	public final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected ICacheService cacheSrv;
	
	@Autowired
	private CommandDispatcher dispatcher;
	
	@Autowired
	private MessageContainer messageContainer;
	
	protected JsonBinder jsonBinder=JsonBinder.buildNormalBinder();
	
    protected JsonBinder respJsonBinder = JsonBinder.buildNonNullBinder();
    
	
	/**
	 * json obj field must public
	 * @param o
	 * @param t
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void autoSetJsonObject(Object o,Object t){
		Class tc = t.getClass();
		Field[] tf = tc.getDeclaredFields();
		Class oc = o.getClass();
		Method m;
		String name;
		for(Field f : tf) {
			name = f.getName();
			if(name.indexOf("_") >= 0) {
				name = name.substring(0, name.indexOf("_")) + name.substring(name.indexOf("_")+1).toUpperCase().substring(0,1) 
						+ name.substring(name.indexOf("_")+2);
			}
			try {
				m = oc.getDeclaredMethod("get"+name.toUpperCase().substring(0, 1)+name.substring(1));
				if( m.getReturnType().getName().equals("java.util.Date")){
					continue;
				}
				f.set(t, m.invoke(o));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}	
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	/**
	 * 把该实现类，加入到处理器的map里面
	 */
	@PostConstruct
	public void init(){
		dispatcher.addProcessor(this);
	}

	//把响应头设置成成功
	protected void initRespHead(RespHead head){
		this.initRespHead(head, "000");
	}
	
	//把响应头设置成制定的code
    protected void initRespHead(RespHead head,String code){
    	head.setStatus_code(code);
    	head.setStatus_desc(this.messageContainer.getProperty(code));
	}
    
    // 获取成功的响应头
    protected RespHead getsucHead() {
    	RespHead head = new RespHead();
    	initRespHead(head);
    	return head;
    }

}
