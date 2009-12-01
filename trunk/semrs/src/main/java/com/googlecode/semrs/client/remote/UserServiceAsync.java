package com.googlecode.semrs.client.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface UserServiceAsync {
	
	public void getLoggedInUser(AsyncCallback callback);
	
	public void getMenuItems(int start, int limit, String sort, String dir, String[][] params, AsyncCallback callback);

}
