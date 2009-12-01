package com.googlecode.semrs.server.service;

import java.util.Collection;
import java.util.Map;

import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface SymptomService {
	
	    public Symptom save(Symptom symptom) throws SaveOrUpdateException;
		
		public Collection<Symptom> listSymptoms();
		
		public Symptom getSymptom(String id, boolean deep);
		
		public Collection<Symptom> listSymptoms(String order, String orderBy, String limit, String offset);
		
		public int getSymptomCount();
		
		public Collection<Symptom> listSymptomsByQuery(Map params, String order, String orderBy, String limit, String offset);
		
		public int getSymptomCount(Map params);
		
		public void deleteSymptom(Symptom symptom) throws DeleteException;
	
		public Collection<Drug> getAvailableDrugs(String id,Map params,String order,
				String orderBy,String limit,String offset);
		
		public Collection<Symptom> getAvailableSymptoms(String id,Map params,String order,
				String orderBy,String limit,String offset);

		public int getAvailableSymptomsCount(String symptomId, Map params);

		public int getAvailableDrugsCount(String symptomId, Map params);
	

}
