package com.googlecode.semrs.client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Role implements IsSerializable {

	private java.lang.String auth;
	private transient PropertyChangeSupport changes = new PropertyChangeSupport(this);
	

	public java.lang.String getAuthority() {
		return auth;
	}

	public void setName(String name) {
	    java.lang.String oldValue = this.auth;
	    this.auth = name;
	        this.changes.firePropertyChange(
	            "auth", oldValue, name);
	}
	
	
	public java.lang.String getAuth() {
		return auth;
	}
	
	
	public void setAuth(String auth) {
	    java.lang.String oldValue = this.auth;
	    this.auth = auth;
	        this.changes.firePropertyChange(
	            "auth", oldValue, auth);
	}
	
	public PropertyChangeListener[] allPropertyChangeListeners() {
		return changes.getPropertyChangeListeners();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void addPropertyChangeListener(
			String propertyName, PropertyChangeListener l) {
		changes.addPropertyChangeListener(propertyName, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	public void removePropertyChangeListener(
			String propertyName, PropertyChangeListener l) {
		changes.removePropertyChangeListener(propertyName, l);
	}

}
