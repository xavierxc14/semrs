package com.googlecode.semrs.web;


import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.googlecode.semrs.util.PropertiesInfo;


/**
 * @author Roger Marin
 *
 * ContextListener Standard de J2EE
 */

public class ContextListener implements ServletContextListener {

	
	private static String timezone = "GMT-4:30";

	static public final String NL = System.getProperty("line.separator") ;

	private static final Log LOG = LogFactory.getLog(ContextListener.class);
	
	private PropertiesInfo propertiesInfo;
	

	
	public void contextDestroyed(ServletContextEvent ev) {
		
	}

	public void contextInitialized(ServletContextEvent ev) {
		
		ServletContext cx = ev.getServletContext();
		BeanFactory factory = WebApplicationContextUtils.getWebApplicationContext(cx);
        this.propertiesInfo  = (PropertiesInfo) factory.getBean("propertiesInfo");
		//enable Jenabean @RdfProperty
		//System.setProperty("jenabean.fieldaccess", "true");
        timezone = propertiesInfo.getTimeZone();
		TimeZone newZone = TimeZone.getTimeZone(timezone);
		LOG.info("Setting TimeZone = " + timezone);
		TimeZone.setDefault(newZone);

	}

}
