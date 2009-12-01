package com.googlecode.semrs.client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.List;


import com.google.gwt.user.client.rpc.IsSerializable;


public class User implements IsSerializable {

	private java.lang.String email;
	private java.lang.String name;
	private java.lang.String lastName;
	private Date birthDate;
	private java.lang.String username;
	private java.lang.String password;
	private java.lang.String idNumber;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	
	/**
	 * @gwt.typeArgs <com.googlecode.semrs.client.model.Role>
	 */
	private java.util.Collection roles;
	
    private transient PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public java.lang.String getId() {
		return username;
	}
	
	public void setId(java.lang.String id) {
		 java.lang.String oldValue = this.username;
		    this.username = id;
		        this.changes.firePropertyChange(
		            "username", oldValue, id);
	}
	
	public java.lang.String getPassword() {
		return password.trim();
	}

	public void setPassword(java.lang.String password) {
		 java.lang.String oldValue = this.password;
		    this.password = password;
		        this.changes.firePropertyChange(
		            "password", oldValue, password);
	}

	public java.lang.String getEmail() {
		return email;
	}

	public void setEmail(java.lang.String email) {
		 java.lang.String oldValue = this.email;
		    this.email = email;
		        this.changes.firePropertyChange(
		            "email", oldValue, email);
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		 java.lang.String oldValue = this.name;
		    this.name = name;
		        this.changes.firePropertyChange(
		            "name", oldValue, name);
	}

	public java.lang.String getLastName() {
		return lastName;
	}

	public void setLastName(java.lang.String lastName) {
		 java.lang.String oldValue = this.lastName;
		    this.lastName = lastName;
		        this.changes.firePropertyChange(
		            "lastName", oldValue, lastName);
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		 Date oldValue = this.birthDate;
		    this.birthDate = birthDate;
		        this.changes.firePropertyChange(
		            "birthDate", oldValue, birthDate);
	}

	public java.lang.String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(java.lang.String idNumber) {
		java.lang.String oldValue = this.idNumber;
	    this.idNumber = idNumber;
	        this.changes.firePropertyChange(
	            "idNumber", oldValue, idNumber);
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		boolean oldValue = this.accountNonExpired;
	    this.accountNonExpired = accountNonExpired;
	        this.changes.firePropertyChange(
	            "accountNonExpired", oldValue, accountNonExpired);
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		boolean oldValue = this.accountNonLocked;
	    this.accountNonLocked = accountNonLocked;
	        this.changes.firePropertyChange(
	            "accountNonLocked", oldValue, accountNonLocked);
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		boolean oldValue = this.credentialsNonExpired;
	    this.credentialsNonExpired = credentialsNonExpired;
	        this.changes.firePropertyChange(
	            "credentialsNonExpired", oldValue, credentialsNonExpired);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		boolean oldValue = this.enabled;
	    this.enabled = enabled;
	        this.changes.firePropertyChange(
	            "enabled", oldValue, enabled);
	}
    
    /**
     * @gwt.typeArgs <com.googlecode.semrs.client.model.Role>
     */
	public java.util.Collection getRoles() {
		return roles;
	}
    
	/**
     * @gwt.typeArgs roles <com.googlecode.semrs.client.model.Role>
     */
	public void setRoles(java.util.Collection roles) {
		java.util.Collection oldValue = this.roles;
	    this.roles = roles;
	        this.changes.firePropertyChange(
	            "roles", oldValue, roles);
	}

	public java.lang.String getUsername() {
		return username;
	}

	public void setUsername(java.lang.String username) {
		java.lang.String oldValue = this.username;
	    this.username = username;
	        this.changes.firePropertyChange(
	            "username", oldValue, username);
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
