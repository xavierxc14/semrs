package com.googlecode.semrs.model.proxy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.springframework.security.GrantedAuthority;


public class Role implements GrantedAuthority{

	public Role(){

	}

	private transient PropertyChangeSupport changes = new PropertyChangeSupport(
			this);


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
	public Role(String name){
		this.auth = name;
	}
	private String auth;


	public String getAuthority() {
		return auth;
	}

	public void setName(String name) {
		java.lang.String oldValue = this.auth;
	    this.auth = name;
	    this.changes.firePropertyChange("auth", oldValue, name);
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

}
