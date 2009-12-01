package com.googlecode.semrs.client.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.googlecode.semrs.client.model.MenuItems;
import com.googlecode.semrs.client.model.User;

public interface UserService extends RemoteService {
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static UserServiceAsync instance;
		public static UserServiceAsync getInstance(){
			if (instance == null) {
				instance = (UserServiceAsync) GWT.create(UserService.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				target.setServiceEntryPoint(GWT.getModuleBaseURL() + "UserService");
			}
			return instance;
		}
	}
	
	public User getLoggedInUser();
	
    public MenuItems getMenuItems(int start, int limit, String sort, String dir, String[][] params);

}
