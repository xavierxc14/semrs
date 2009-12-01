package com.googlecode.semrs.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://semrs.googlecode.com/")
public class ComplicationRecord implements Persistable{
	
	private String id;
	
	private String complicationId;
	
	private String encounterId;
	
	private String name;
	
	private Date date;

	@Id
	public String getId() {
		return id;
	}
	
	@Override
	public String getUri() {
		return this.getClass().getAnnotation(Namespace.class).value() + this.getClass().getName()+"/";
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComplicationId() {
		return complicationId;
	}

	public void setComplicationId(String complicationId) {
		this.complicationId = complicationId;
	}
	
	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
	}
	

}
