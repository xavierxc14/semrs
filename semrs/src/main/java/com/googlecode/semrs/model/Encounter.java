package com.googlecode.semrs.model;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://semrs.googlecode.com/")
public class Encounter implements Persistable{
	
	private String id;
	
	private String patientId;
	
	private String encounterProvider;
	
	private Date encounterDate;
	
	private Date endDate;
	
	private String encounterReason;
	
	private String background;
	
	private String refferral;
	
	private boolean current;
	
	private boolean finalized;
	
	private Collection<SymptomRecord> symptomRecords = new LinkedList<SymptomRecord>();
	
	private Collection<TreatmentRecord> treatmentRecords = new LinkedList<TreatmentRecord>();
	
	private Collection<LabTestRecord> labTestRecords = new LinkedList<LabTestRecord>();
	
	private Collection<ComplicationRecord> complicationRecords = new LinkedList<ComplicationRecord>();
	
	private Collection<ProcedureRecord> procedureRecords = new LinkedList<ProcedureRecord>();
	
	private Collection<Diagnosis> diagnoses = new LinkedList<Diagnosis>();
	
	private Date creationDate;
	
	private String creationUser;
	
	private Date lastEditDate;
		
	private String lastEditUser;
	
	private boolean toxicHabits;
	
	private String toxicHabitsDesc;
	
	

	@Id
	public String getId() {
		return id;
	}

	@Override
	public String getUri() {
		return this.getClass().getAnnotation(Namespace.class).value() + this.getClass().getName()+"/";
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getEncounterProvider() {
		return encounterProvider;
	}

	public void setEncounterProvider(String encounterProvider) {
		this.encounterProvider = encounterProvider;
	}

	public Date getEncounterDate() {
		return encounterDate;
	}

	public void setEncounterDate(Date encounterDate) {
		this.encounterDate = encounterDate;
	}

	public String getEncounterReason() {
		return encounterReason;
	}

	public void setEncounterReason(String encounterReason) {
		this.encounterReason = encounterReason;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public Collection<Diagnosis> getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(Collection<Diagnosis> diagnoses) {
		this.diagnoses = diagnoses;
	}
	
	public void addDiagnosis(Diagnosis diagnosis) {
		if(diagnosis!=null && this.diagnoses!=null){
		   this.diagnoses.add(diagnosis);
		}
	}
	

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	public Collection<SymptomRecord> getSymptomRecords() {
		return symptomRecords;
	}

	public void setSymptomRecords(Collection<SymptomRecord> symptomRecords) {
		this.symptomRecords = symptomRecords;
	}
	
	public void addSymptomRecord(SymptomRecord symptomRecord) {
		if(symptomRecord!=null && this.symptomRecords!=null){
		   this.symptomRecords.add(symptomRecord);
		}
	}

	public Collection<LabTestRecord> getLabTestRecords() {
		return labTestRecords;
	}

	public void setLabTestRecords(Collection<LabTestRecord> labTestRecords) {
		this.labTestRecords = labTestRecords;
	}
	
	public void addLabTestRecord(LabTestRecord labTestRecord) {
		if(labTestRecord!=null && this.labTestRecords!=null){
		   this.labTestRecords.add(labTestRecord);
		}
	}

	public Collection<ComplicationRecord> getComplicationRecords() {
		return complicationRecords;
	}

	public void setComplicationRecords(
			Collection<ComplicationRecord> complicationRecords) {
		this.complicationRecords = complicationRecords;
	}
	
	public void addComplicationRecord(ComplicationRecord complicationRecord) {
		if(complicationRecord!=null && this.complicationRecords!=null){
		   this.complicationRecords.add(complicationRecord);
		}
	}

	public Collection<ProcedureRecord> getProcedureRecords() {
		return procedureRecords;
	}

	public void setProcedureRecords(Collection<ProcedureRecord> procedureRecords) {
		this.procedureRecords = procedureRecords;
	}
	
	public void addProcedureRecord(ProcedureRecord procedureRecord) {
		if(procedureRecord!=null && this.procedureRecords!=null){
		   this.procedureRecords.add(procedureRecord);
		}
	}
	

	public String getRefferral() {
		return refferral;
	}

	public void setRefferral(String refferral) {
		this.refferral = refferral;
	}

	public boolean isFinalized() {
		return finalized;
	}

	public void setFinalized(boolean finalized) {
		this.finalized = finalized;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}

	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}

	public boolean isToxicHabits() {
		return toxicHabits;
	}

	public void setToxicHabits(boolean toxicHabits) {
		this.toxicHabits = toxicHabits;
	}

	public String getToxicHabitsDesc() {
		return toxicHabitsDesc;
	}

	public void setToxicHabitsDesc(String toxicHabitsDesc) {
		this.toxicHabitsDesc = toxicHabitsDesc;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public Collection<TreatmentRecord> getTreatmentRecords() {
		return treatmentRecords;
	}

	public void setTreatmentRecords(Collection<TreatmentRecord> treatmentRecord) {
		this.treatmentRecords = treatmentRecord;
	}
	
	public void addTreatmentRecord(TreatmentRecord treatmentRecord) {
		if(treatmentRecord!=null && this.treatmentRecords!=null){
		   this.treatmentRecords.add(treatmentRecord);
		}
	}
	
}
