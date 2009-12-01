package com.googlecode.semrs.server.dao;

import java.util.Collection;
import java.util.Map;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface LabTestDAO {
	
	public LabTest save(LabTest labTest) throws SaveOrUpdateException;
	
	public Collection<LabTest> listLabTests();
	
	public LabTest getLabTest(String id, boolean deep);
	
	public Collection<LabTest> listLabTests(String order, String orderBy, String limit, String offset);
	
	public int getLabTestCount();
	
	public Collection<LabTest> listLabTestsByQuery(Map params, String order, String orderBy, String limit, String offset);
	
	public int getLabTestCount(Map params);
	
	public void deleteLabTest(LabTest labTest) throws DeleteException;
	
	public Collection<Disease> getAvailableDiseases(String labTestId,Map params,String order,
			String orderBy,String limit,String offset);

	public int getAvailableDiseasesCount(String labTestId, Map params);
	

}
