package com.googlecode.semrs.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.semrs.client.model.MenuItems;
import com.googlecode.semrs.client.model.User;
import com.googlecode.semrs.server.service.proxy.UserProxyService;


public class UserServiceServlet extends RemoteServiceServlet implements com.googlecode.semrs.client.remote.UserService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger
    .getLogger(UserServiceServlet.class);
	private UserProxyService service; 

	public void init(ServletConfig config) throws ServletException {
	        super.init(config);
	    
	        BeanFactory factory = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
	        this.service = (UserProxyService) factory.getBean("userProxyService");
	    }

	    public void setService(UserProxyService service) {
	        this.service = service;
	    }
	
	@Override
	public User getLoggedInUser() {
		LOG.info("Using Service: " + service);
		return service.getCurrentUser();
	}
	
	@Override
	public MenuItems getMenuItems(int start, int limit, String sort, String dir, String[][] params) {
		  String[][] menuItems = service.getUserMenu();
		  return new MenuItems(menuItems, menuItems.length);
		  
	    }
}
