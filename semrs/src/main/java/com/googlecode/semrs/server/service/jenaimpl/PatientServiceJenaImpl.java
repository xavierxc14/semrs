package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Encounter;
import com.googlecode.semrs.model.Patient;
import com.googlecode.semrs.server.dao.PatientDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.PatientService;

public class PatientServiceJenaImpl implements PatientService {
	
    private static final Logger LOG = Logger.getLogger(PatientServiceJenaImpl.class);
	
	private PatientDAO patientDAO;
	
	public PatientServiceJenaImpl(){
		  super();
	}
	
	public void setPatientDAO(PatientDAO patientDAO) {
		this.patientDAO = patientDAO;
	}


	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public void deletePatient(final Patient patient) throws DeleteException {
		synchronized(patient){
			boolean hasPastEncounters = false; 
			for(Encounter encounter : patient.getEncounters()){
				if(!encounter.isCurrent()){
					hasPastEncounters = true;
					break;
				}
			}
			if(!hasPastEncounters){
				LOG.debug("Deleting patient with id: " + patient.getId());
				patientDAO.deletePatient(patient);
			}else{
				LOG.error("Tried to delete patient with id: " + patient.getId());
				throw new DeleteException("Patient cannot be deleted");
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Patient getPatient(final String id, final boolean deep) {
		return patientDAO.getPatient(id, deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Patient> listPatients() {
		return patientDAO.listPatients();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public Patient savePatient(final Patient patient) throws SaveOrUpdateException {
		return patientDAO.savePatient(patient);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getPatientCount(final Map params) {
		return patientDAO.getPatientCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getPatientCount() {
		return patientDAO.getPatientCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Patient> listPatients(final String order, final String orderBy,
			final String limit, final String offset) {
		return patientDAO.listPatients(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Patient> listPatientsByQuery(final Map params, final String order,
			final String orderBy,final String limit, final String offset) {
		return patientDAO.listPatientsByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	public Map getSexDemographics() {
		int sexm = patientDAO.getSexCount("M");
		int sexf = patientDAO.getSexCount("F");
		Map sexMap = new HashMap();
		sexMap.put("m", sexm);
		sexMap.put("f", sexf);
		return sexMap;
	}
	

	@Override
	public Map getAgeDemographics() {	
		int age0 = patientDAO.getAgeCount(0, 10);
		int age1 = patientDAO.getAgeCount(10, 20);
		int age2 = patientDAO.getAgeCount(20, 30);
		int age3 = patientDAO.getAgeCount(30, 40);
		int age4 = patientDAO.getAgeCount(40, 50);
		int age5 = patientDAO.getAgeCount(50, 60);
		int age6 = patientDAO.getAgeCount(60, 200);
		
		Map ageMap = new HashMap();
		ageMap.put("0-10", age0);
		ageMap.put("10-20", age1);
		ageMap.put("20-30", age2);
		ageMap.put("30-40", age3);
		ageMap.put("40-50", age4);
		ageMap.put("50-60", age5);
		ageMap.put("+60", age6);
		return ageMap;
	}

	@Override
	public Map getActive() {
		
		int active = patientDAO.getVoidCount(false);
		int inactive = patientDAO.getVoidCount(true);
		
		Map activeMap = new HashMap();
		activeMap.put("active",active);
		activeMap.put("inactive",inactive);
		
		return activeMap;
	}

}
