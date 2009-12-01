package com.googlecode.semrs.server.security;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.event.authentication.AbstractAuthenticationFailureEvent;
import org.springframework.security.event.authentication.AuthenticationSuccessEvent;

import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.UserService;
/**
 * @author Roger Marin
 *
 * Listener que implementa la interfaz ApplicationListener de Spring
 * Que se encarga de interceptar el evento de autentificación de un usuario
 * y le setea la nueva fecha de Login y guarda el nuevo objeto.
 */
public class LastLoginListener implements ApplicationListener {
	
	private static final Logger LOG = Logger.getLogger(LastLoginListener.class);
	
	private UserService userService;
	

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		
		if (event instanceof AbstractAuthenticationFailureEvent) {
			    // log or similar
			}
		
		if ( event instanceof AuthenticationSuccessEvent ){
				AuthenticationSuccessEvent authenticationSuccessEvent = ( AuthenticationSuccessEvent ) event;
				String username = authenticationSuccessEvent.getAuthentication().getName();
				com.googlecode.semrs.model.User user = userService.getUserByUsername(username,false);
				synchronized(user){
					user.setLastLogin(new Date());
					try {
						userService.save(user);
					} catch (SaveOrUpdateException e) {
						LOG.error(e);
					} 
				}
		}

	}


	public UserService getUserService() {
		return userService;
	}


	public void setUserService(UserService userService) {
		this.userService = userService;
	}




}
