package com.googlecode.semrs.server.dao;

import java.util.Collection;
import java.util.Map;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface ProcedureDAO {
	
    public Procedure save(Procedure procedure) throws SaveOrUpdateException;
	
	public Collection<Procedure> listProcedures();
	
	public Procedure getProcedure(String id,boolean deep);
	
	public Collection<Procedure> listProcedures(String order, String orderBy, String limit, String offset);
	
	public int getProcedureCount();
	
	public Collection<Procedure> listProceduresByQuery(Map params, String order, String orderBy, String limit, String offset);
	
	public int getProcedureCount(Map params);
	
	public void deleteProcedure(Procedure procedure) throws DeleteException;
	
	public Collection<Disease> getAvailableDiseases(String procedureId, Map params, String order,
			 String orderBy, String limit, String offset);
	
	public int getAvailableDiseasesCount(String procedureId, Map params);

}
