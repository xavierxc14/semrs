package com.googlecode.semrs.model;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://semrs.googlecode.com/")
public class Symptom implements Persistable {

	private String id;

	private String name;
	
	private String description;
	
	private Date lastEditDate;
	
	private String lastEditUser;
	
	@RdfProperty(transitive=true) 
	private Collection<Symptom> symptoms;
	
	//@RdfProperty(transitive=true) 
	private Collection<Drug> drugs;
	
	//@RdfProperty(transitive=true) 
	private Collection<Disease> diseases;

	
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

	public void setId(String id) {
		this.id = id;
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

	@RdfProperty(transitive=true) 
	public Collection<Symptom> getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(Collection<Symptom> symptoms) {
		this.symptoms = symptoms;
	}
	
	//@RdfProperty(transitive=true) 
	public Collection<Drug> getDrugs() {
		return drugs;
	}

	public void setDrugs(Collection<Drug> drugs) {
		this.drugs = drugs;
	}

	public Collection<Disease> getDiseases() {
		return diseases;
	}

	public void setDiseases(Collection<Disease> diseases) {
		this.diseases = diseases;
	}
	
	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}

}
