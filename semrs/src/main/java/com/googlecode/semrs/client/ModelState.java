package com.googlecode.semrs.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.googlecode.semrs.client.model.User;


public class ModelState {
	 private PropertyChangeSupport changes = new PropertyChangeSupport( this );
	    
	    
	    private User currentUser;
	    
	    private static ModelState instance;
	    
	    
	    /** Creates a new instance of ModelState */
	    private ModelState() {
	        super();
	    }
	    
	    public static ModelState getInstance(){
	        return instance == null ? instance = new ModelState() : instance ;
	    }
	    
	    
	    public void addPropertyChangeListener( PropertyChangeListener l ){
	        changes.addPropertyChangeListener( l );
	    }
	    public void addPropertyChangeListener( String property, PropertyChangeListener l ){
	        changes.addPropertyChangeListener( property, l );
	    }
	    public void removeProertyChangeListener( PropertyChangeListener l ){
	        changes.removePropertyChangeListener( l );
	    }
	    public void removePropertyChangeListener( String property, PropertyChangeListener l ){
	        changes.removePropertyChangeListener( property, l);
	    }
	    
	    public void clearPropertyChangeListeners(){
	        PropertyChangeListener[] listeners = changes.getPropertyChangeListeners();
	        for( int i=0; listeners != null && i < listeners.length; i++ ){
	            this.removeProertyChangeListener( listeners[i] );
	        }
	    }
	    
	    public User getCurrentUser(){
	    	return currentUser;
	    }


	    public void setCurrentUser(User user) {
	        this.currentUser = user;
	        changes.firePropertyChange( "currentUser", null, this.currentUser);
	    }
	    

}
