package com.googlecode.semrs.server.service;

import java.util.Collection;
import java.util.Map;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface DiseaseService {
	
    public Disease save(Disease disease) throws SaveOrUpdateException;
	
	public Collection<Disease> listDiseases();
	
	public Disease getDisease(String id,boolean deep);
	
	public Collection<Disease> listDiseases(String order, String orderBy, String limit, String offset);
	
	public int getDiseaseCount();
	
	public Collection<Disease> listDiseasesByQuery(Map params, String order, String orderBy, String limit, String offset);
	
	public int getDiseaseCount(Map params);
	
	public void deleteDisease(Disease disease) throws DeleteException;
	
	public Collection<Symptom> getAvailableSymptoms(String diseaseId, Map params,
			String order, String orderBy, String limit, String offset);

	public int getAvailableSymptomsCount(String diseaseId, Map params);

	public int getAvailableDiseasesCount(String diseaseId, Map params);

	public Collection<Disease> getAvailableDiseases(String diseaseId, Map params,
			String order, String orderBy, String limit, String offset);

	public Collection<Procedure> getAvailableProcedures(String diseaseId, Map params,
			String order, String orderBy, String limit, String offset);

	public int getAvailableProceduresCount(String diseaseId, Map params);

	public Collection<LabTest> getAvailableLabTests(String diseaseId, Map params,
			String order, String orderBy, String limit, String offset);

	public int getAvailableLabTestsCount(String diseaseId, Map params);

}
