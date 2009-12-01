package com.googlecode.semrs.server.dao;

import java.util.Collection;
import java.util.Map;

import com.googlecode.semrs.model.Complication;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface ComplicationDAO {
	
    public Complication save(Complication complication) throws SaveOrUpdateException;
	
	public Collection<Complication> listComplications();
	
	public Complication getComplication(String id, boolean deep);
	
	public Collection<Complication> listComplications(String order, String orderBy, String limit, String offset);
	
	public int getComplicationCount();
	
	public Collection<Complication> listComplicationsByQuery(Map params, String order, String orderBy, String limit, String offset);
	
	public int getComplicationCount(Map params);
	
	public void deleteComplication(Complication complication) throws DeleteException;

	public Collection<Symptom> getAvailableSymptoms(String complicationId,Map params,String order,
			String orderBy,String limit,String offset);

	public int getAvailableSymptomsCount(String complicationId, Map params);
		
	public Collection<Procedure> getAvailableProcedures(String complicationId,Map params,String order,
			String orderBy,String limit,String offset);
	
	public int getAvailableProceduresCount(String complicationId, Map params);
}
