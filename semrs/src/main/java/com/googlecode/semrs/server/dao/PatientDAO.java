package com.googlecode.semrs.server.dao;

import java.util.Collection;
import java.util.Map;

import com.googlecode.semrs.model.Patient;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface PatientDAO {
	
	public Patient getPatient(String id, boolean deep);
	
	public Collection<Patient> listPatients();
	
	public Collection<Patient> listPatients(String order, String orderBy, String limit, String offset);
	
	public int getPatientCount();
	
	public Collection<Patient> listPatientsByQuery(Map params, String order,
			String orderBy, String limit, String offset);
	
	public int getPatientCount(Map params); 
	
	public void deletePatient(Patient patient) throws DeleteException;
	
	public Patient savePatient(Patient patient) throws SaveOrUpdateException;
	
	public int getSexCount(String sex);

	public int getAgeCount(int from, int to);

	public int getVoidCount(boolean voided);
	
	

}
