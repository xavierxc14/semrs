package com.googlecode.semrs.model;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://semrs.googlecode.com/")
public class Patient implements Persistable{
	
	private String id;
	
	private String name;
	
	private String lastName;
	
	private String email;
	
	private String address;
	
	private String phoneNumber;
	
	private String mobile;
	
	private Date birthDate;
	
	private String sex;
	
	private String birthPlace;
	
	private String bloodType;
	
	private boolean voided;
	
	private String voidReason;
	
	private Date voidDate;
	
	private double weight;
	
	private String weightUnits;
	
	private double height;
	
	private String heightUnits;
	
	private String description;
	
	private String refferalCause;
	
	private Collection<Encounter> encounters = new LinkedList<Encounter>();
	
	private User provider;
	
	private String groupId;
	
	private Date creationDate;
	
	private String creationUser;
	
    private Date lastEditDate;
	
	private String lastEditUser;
	
	private Date lastEncounterDate;
	

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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public boolean isVoided() {
		return voided;
	}

	public void setVoided(boolean voided) {
		this.voided = voided;
	}

	public String getVoidReason() {
		return voidReason;
	}

	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}

	public Date getVoidDate() {
		return voidDate;
	}

	public void setVoidDate(Date voidDate) {
		this.voidDate = voidDate;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getWeightUnits() {
		return weightUnits;
	}

	public void setWeightUnits(String weightUnits) {
		this.weightUnits = weightUnits;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String getHeightUnits() {
		return heightUnits;
	}

	public void setHeightUnits(String heightUnits) {
		this.heightUnits = heightUnits;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRefferalCause() {
		return refferalCause;
	}

	public void setRefferalCause(String refferalCause) {
		this.refferalCause = refferalCause;
	}

	public Collection<Encounter> getEncounters() {
		return encounters;
	}

	public void setEncounters(Collection<Encounter> encounters) {
		this.encounters = encounters;
	}
	
	public void addEncounter(Encounter encounter) {
		if(encounter!=null && this.encounters!=null){
		   this.encounters.add(encounter);
		}
	}
	
	public void removeEncounter(Encounter encounter) {
		if(encounter!=null && this.encounters!=null && this.encounters.contains(encounter)){
		   this.encounters.remove(encounter);
		}
	}
	

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
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

	public User getProvider() {
		return provider;
	}

	public void setProvider(User provider) {
		this.provider = provider;
	}

	public String getBloodType() {
		return bloodType;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Date getLastEncounterDate() {
		return lastEncounterDate;
	}

	public void setLastEncounterDate(Date lastEncounterDate) {
		this.lastEncounterDate = lastEncounterDate;
	}
	
	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}

}
