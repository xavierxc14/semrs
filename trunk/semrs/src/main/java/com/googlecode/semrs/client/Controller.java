package com.googlecode.semrs.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.googlecode.semrs.client.model.User;
import com.googlecode.semrs.client.remote.UserService;
import com.googlecode.semrs.client.remote.UserServiceAsync;
import com.gwtext.client.widgets.MessageBox;


public class Controller {
	private static Controller instance;
    private UserServiceAsync service =
            (UserServiceAsync) GWT.create( UserService.class );
    private  ModelState model = ModelState.getInstance();
    
    private Controller() {
        super();
        ServiceDefTarget endpoint = (ServiceDefTarget) service;
        endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "UserService");
    }
    
    public static Controller getInstance(){
        return instance == null ? instance = new Controller() : instance;
    }
    
    native void redirect(String url)
    /*-{
            $wnd.location.replace(url);

    }-*/; 
    
    
    public void getCurrentUser(){
        service.getLoggedInUser( new AsyncCallback(){
            public void onSuccess(Object object) {
                model.setCurrentUser((User) object );
            }

            public void onFailure(Throwable throwable) {
                GWT.log( "Exception getting logged in user", throwable);
                String errorMessage = throwable.toString();
            	if (errorMessage.indexOf("403") != -1)
            	{
            	//	Access denied for this role
            	//
            		MessageBox.alert("Error", "Acceso Denegado.");
            	}
            	else if (errorMessage.indexOf("login") != -1)
            	{
            	//	Session expired : display login form
            	//
            		MessageBox.alert("Error", "Su sesi&oacute;n  de usuario ha expirado, presione OK para volver a loguearse." ,  
                            new MessageBox.AlertCallback() { 
								public void execute() {
									redirect("/semrs/");
								}  
                            });  
            	}
            	else
            	{
            		MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de usuarios.");
            	}
            }
            
        });
    }
    
  
    	
    


}
