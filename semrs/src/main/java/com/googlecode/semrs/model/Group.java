package com.googlecode.semrs.model;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://semrs.googlecode.com/")
public class Group implements Persistable{
	
	
	private String groupId;
	
	private String name;
	
	private String description;
	
	private Collection<User> users;
	
	private Date lastEditDate;
	
	private String lastEditUser;
	
	private boolean providerGroup;

	
	@Id
	public String getGroupId() {
	   return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
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

	@Override
	public String getId() {
		return groupId;
	}

	@Override
	public String getUri() {
		return this.getClass().getAnnotation(Namespace.class).value() + this.getClass().getName()+"/";
	}

	public boolean isProviderGroup() {
		return providerGroup;
	}

	public void setProviderGroup(boolean providerGroup) {
		this.providerGroup = providerGroup;
	}
	
	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}
	

}
