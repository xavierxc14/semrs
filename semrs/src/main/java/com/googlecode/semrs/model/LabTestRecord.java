package com.googlecode.semrs.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://semrs.googlecode.com/")
public class LabTestRecord implements Persistable{
	
	private String id;
	
	private String labTestId;
	
	private String encounterId;
	
	private String name;
	
	private Date testDate;
	
	private boolean result;
	
	private String resultDesc;


	@Id
	public String getId() {
		return id;
	}

	@Override
	public String getUri() {
		return this.getClass().getAnnotation(Namespace.class).value() + this.getClass().getName()+"/";
	}

	
	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getLabTestId() {
		return labTestId;
	}

	public void setLabTestId(String labTestId) {
		this.labTestId = labTestId;
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

	public String getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}
	

}
