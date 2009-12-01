package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.server.dao.LabTestDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.LabTestService;

public class LabTestServiceJenaImpl implements LabTestService{
	
private static final Logger LOG = Logger.getLogger(LabTestServiceJenaImpl.class);
	
	private LabTestDAO labTestDAO;
	
	public LabTestServiceJenaImpl(){
		  super();
	}
	
	public void setLabTestDAO(LabTestDAO labTestDAO) {
		this.labTestDAO = labTestDAO;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public void deleteLabTest(final LabTest labTest) throws DeleteException {
		labTestDAO.deleteLabTest(labTest);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public LabTest getLabTest(final String id, final boolean deep) {
		return labTestDAO.getLabTest(id, deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getLabTestCount() {
		return labTestDAO.getLabTestCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getLabTestCount(final Map params) {
		return labTestDAO.getLabTestCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<LabTest> listLabTests(final String order, final String orderBy,
			final String limit, final String offset) {
		return labTestDAO.listLabTests(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<LabTest> listLabTestsByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		return labTestDAO.listLabTestsByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<LabTest> listLabTests() {
		return labTestDAO.listLabTests();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public LabTest save(final LabTest labTest) throws SaveOrUpdateException {
		return labTestDAO.save(labTest);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Disease> getAvailableDiseases(String labTestId, Map params, String order,
			String orderBy,String limit, String offset) {
		return labTestDAO.getAvailableDiseases(labTestId, params,order,orderBy,limit,offset);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableDiseasesCount(String labTestId, Map params) {
		return labTestDAO.getAvailableDiseasesCount(labTestId, params);
	}

}
