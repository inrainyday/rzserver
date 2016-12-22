package com.boco.rzserver.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * 
 * @author lij
 *
 */
@Component
public class MessageContainer  {

	private static final Logger logger = LoggerFactory.getLogger(MessageContainer.class);
	private Properties p = new Properties();

	public String getProperty(String key) {
		if (key == null)
			return null;
		else
			return this.p.getProperty(key);
	}

	@PostConstruct
	public void init() throws IOException {
		ClassPathResource resource=new ClassPathResource("message/message.properties");
		if (!resource.exists()) {
			logger.error("file message.properties is missing, check the file!");
			System.exit(0);
		}
		InputStream inputStream =resource.getInputStream();
		this.p.load(inputStream);
		inputStream.close();
		logger.info("init message success !");
	}
	

}
