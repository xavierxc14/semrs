package com.googlecode.semrs.model;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://semrs.googlecode.com/")
public class Disease implements Persistable{
	
	private String id;

	private String name;
	
	private String description;
	
	private int minimumAgeRange;
	
	private int maximumAgeRange;
	
	private String sex;
	
	private boolean toxicHabits;

	@RdfProperty(transitive=true) 
	private Collection<Symptom> symptoms;
			
	//@RdfProperty(transitive=true) 
	private Collection<LabTest> labTests;
	
	//@RdfProperty(transitive=true) 
	private Collection<Procedure> procedures;
	
	@RdfProperty(symmetric=true) 
	private Collection<Disease> relDiseases;
	
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

	@RdfProperty(transitive=true) 
	public Collection<Symptom> getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(Collection<Symptom> symptoms) {
		this.symptoms = symptoms;
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

	//@RdfProperty(transitive=true) 
	public Collection<LabTest> getLabTests() {
		return labTests;
	}

	public void setLabTests(Collection<LabTest> labTests) {
		this.labTests = labTests;
	}

	//@RdfProperty(transitive=true) 
	public Collection<Procedure> getProcedures() {
		return procedures;
	}

	public void setProcedures(Collection<Procedure> procedures) {
		this.procedures = procedures;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getMinimumAgeRange() {
		return minimumAgeRange;
	}

	public void setMinimumAgeRange(int minimumAgeRange) {
		this.minimumAgeRange = minimumAgeRange;
	}

	public int getMaximumAgeRange() {
		return maximumAgeRange;
	}

	public void setMaximumAgeRange(int maximumAgeRange) {
		this.maximumAgeRange = maximumAgeRange;
	}

	public boolean isToxicHabits() {
		return toxicHabits;
	}

	public void setToxicHabits(boolean toxicHabits) {
		this.toxicHabits = toxicHabits;
	}

	@RdfProperty(symmetric=true) 
	public Collection<Disease> getRelDiseases() {
		return relDiseases;
	}

	public void setRelDiseases(Collection<Disease> relDiseases) {
		this.relDiseases = relDiseases;
	}

	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}
	
}
