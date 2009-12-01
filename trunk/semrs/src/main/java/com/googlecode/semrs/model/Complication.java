package com.googlecode.semrs.model;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://semrs.googlecode.com/")
public class Complication implements Persistable{
	
	private String id;

	private String name;
	
	private String description;

	@RdfProperty(transitive=true) 
	private Collection<Symptom> symptoms;
	 
	private Collection<Procedure> complicationProcedures;
	
	private Date lastEditDate;
	
	private String lastEditUser;
	
	@Id
	public String getId() {
		return id;
	}

	@Override
	public String getUri() {
		return this.getClass().getAnnotation(Namespace.class).value() + this.getClass().getName()+"/";
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


	public Collection<Procedure> getComplicationProcedures() {
		return complicationProcedures;
	}

	public void setComplicationProcedures(Collection<Procedure> complicationProcedures) {
		this.complicationProcedures = complicationProcedures;
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

	public void setId(String id) {
		this.id = id;
	}

	@RdfProperty(transitive=true) 
	public Collection<Symptom> getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(Collection<Symptom> symptoms) {
		this.symptoms = symptoms;
	}
	
	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}
	
	

}
