package com.googlecode.semrs.server.dao;

import java.util.Collection;
import java.util.Map;

import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface SymptomDAO {
	
    public Symptom save(Symptom symptom) throws SaveOrUpdateException;
	
	public Collection<Symptom> listSymptoms();
	
	public Symptom getSymptom(String id, boolean deep);
	
	public Collection<Symptom> listSymptoms(String order, String orderBy, String limit, String offset);
	
	public int getSymptomCount();
	
	public Collection<Symptom> listSymptomsByQuery(Map params, String order, String orderBy, String limit, String offset);
	
	public int getSymptomCount(Map params);
	
	public void deleteSymptom(Symptom symptom) throws DeleteException;
	
	public Collection<Symptom> getAvailableSymptoms(String id, Map params,String order,
			String orderBy,String limit,String offset);

	public int getAvailableSymptomsCount(String id, Map params);

	public Collection<Drug> getAvailableDrugs(String id, Map params, String order,
			String orderBy, String limit, String offset);

	public int getAvailableDrugsCount(String id, Map params);
	

}
