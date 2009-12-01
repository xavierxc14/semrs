package com.googlecode.semrs.model.proxy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;




public class User implements UserDetails {
	
	
	private String name;
	private String lastName;
	private Date birthDate;
	private String username;
	private String password;
	private String idNumber;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	private String email;
	private Collection<Role> roles;
	private String sex;
	private String phoneNumber;
	private String mobile;
	private String address;
	private Date lastLogin;
	private Date lastEditDate;
	private String lastEditUser;
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

	@Override
	public GrantedAuthority[] getAuthorities() {
		
		Collection<Role> roles = getRoles();
		GrantedAuthority[] rtn = new GrantedAuthority[roles.size()];
		int i = 0;
		for(Role role: roles){
			rtn[i] = role;
			++i;
		}
		return rtn;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		java.lang.String oldValue = this.name;
	    this.name = name;
	    this.changes.firePropertyChange("name", oldValue, name);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		java.lang.String oldValue = this.lastName;
	    this.lastName = lastName;
	    this.changes.firePropertyChange("lastName", oldValue, lastName);
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		java.util.Date oldValue = this.birthDate;
	    this.birthDate = birthDate;
	    this.changes.firePropertyChange("birthDate", oldValue, birthDate);
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String id) {
		java.lang.String oldValue = this.idNumber;
	    this.idNumber = id;
	    this.changes.firePropertyChange("id", oldValue, id);
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		Collection<com.googlecode.semrs.model.proxy.Role> oldValue = this.roles;
	    this.roles = roles;
	    this.changes.firePropertyChange("roles", oldValue, roles);
	}

	public void setUsername(String username) {
		java.lang.String oldValue = this.username;
	    this.username = username;
	    this.changes.firePropertyChange("username", oldValue, username);
	}

	public void setPassword(String password) {
		java.lang.String oldValue = this.password;
	    this.password = password;
	    this.changes.firePropertyChange("password", oldValue, password);
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		boolean oldValue = this.accountNonExpired;
	    this.accountNonExpired = accountNonExpired;
	    this.changes.firePropertyChange("accountNonExpired", oldValue, accountNonExpired);
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		boolean oldValue = this.accountNonLocked;
	    this.accountNonLocked = accountNonLocked;
	    this.changes.firePropertyChange("accountNonLocked", oldValue, accountNonLocked);
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		boolean oldValue = this.credentialsNonExpired;
	    this.credentialsNonExpired = credentialsNonExpired;
	    this.changes.firePropertyChange("credentialsNonExpired", oldValue, credentialsNonExpired);
	}

	public void setEnabled(boolean enabled) {
		boolean oldValue = this.enabled;
	    this.enabled = enabled;
	    this.changes.firePropertyChange("enabled", oldValue, enabled);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastEditDate() {
		return lastEditDate;
	}

	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
	}

	public String getLastEditUser() {
		return lastEditUser;
	}

	public void setLastEditUser(String lastEditUser) {
		this.lastEditUser = lastEditUser;
	}

	public PropertyChangeSupport getChanges() {
		return changes;
	}

	public void setChanges(PropertyChangeSupport changes) {
		this.changes = changes;
	}

}
