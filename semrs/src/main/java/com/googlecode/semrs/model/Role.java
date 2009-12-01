package com.googlecode.semrs.model;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;


@Namespace("http://semrs.googlecode.com/")
public class Role implements Persistable{
	
	private String auth;

	private String description;
	
	private String longDescription;
	
	private Date lastEditDate;
	
	private String lastEditUser;
	
	private Collection<Module> modules; 
	
	
	public Role(){
		
	}
	public Role(String name){
		this.auth = name;
	}

	
	@Id
	public String getAuthority() {
		return auth;
	}

	public void setName(String name) {
		this.auth = name;
	}
	
	
	public String getAuth() {
		return auth;
	}
	
	
	public void setAuth(String auth) {
		this.auth = auth;
	}
	
	@Override
	public String getId() {

		return auth;
	}
	@Override
	public String getUri() {
		return this.getClass().getAnnotation(Namespace.class).value() + this.getClass().getName()+"/";
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLongDescription() {
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
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
	public Collection<Module> getModules() {
		return modules;
	}
	public void setModules(Collection<Module> modules) {
		this.modules = modules;
	}
	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}
	

}
