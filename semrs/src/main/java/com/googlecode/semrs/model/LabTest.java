package com.googlecode.semrs.model;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://semrs.googlecode.com/")
public class LabTest implements Persistable{

	private String id;

	private String name;
	
	private String description;
	
	private Date lastEditDate;
	
	private String lastEditUser;
	
	@RdfProperty(inverseOf="labTests")
	private Collection<Disease> relatedDiseases;
	
	
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

    @RdfProperty(inverseOf="labTests")
	public Collection<Disease> getRelatedDiseases() {
		return relatedDiseases;
	}

	public void setRelatedDiseases(Collection<Disease> relatedDiseases) {
		this.relatedDiseases = relatedDiseases;
	}

	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}
	

}
