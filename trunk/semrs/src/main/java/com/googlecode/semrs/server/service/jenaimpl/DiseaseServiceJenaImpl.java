package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.dao.DiseaseDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.DiseaseService;

public class DiseaseServiceJenaImpl implements DiseaseService {
	
	private static final Logger LOG = Logger.getLogger(DiseaseServiceJenaImpl.class);
	
	private DiseaseDAO diseaseDAO;
	
	public DiseaseServiceJenaImpl(){
		  super();
	}
	
	public void setDiseaseDAO(DiseaseDAO diseaseDAO) {
		this.diseaseDAO = diseaseDAO;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public void deleteDisease(final Disease disease) throws DeleteException {
		diseaseDAO.deleteDisease(disease);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Disease getDisease(final String id,final boolean deep) {
		return diseaseDAO.getDisease(id,deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getDiseaseCount() {
		return diseaseDAO.getDiseaseCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getDiseaseCount(final Map params) {
		return diseaseDAO.getDiseaseCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Disease> listDiseases() {
		return diseaseDAO.listDiseases();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Disease> listDiseases(final String order, final String orderBy,
			final String limit, final String offset) {
		return diseaseDAO.listDiseases(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Disease> listDiseasesByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		return diseaseDAO.listDiseasesByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public Disease save(final Disease disease) throws SaveOrUpdateException {
		return diseaseDAO.save(disease);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Disease> getAvailableDiseases(String diseaseId,
			Map params, String order, String orderBy, String limit,
			String offset) {
		return diseaseDAO.getAvailableDiseases(diseaseId, params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableDiseasesCount(String diseaseId, Map params) {
		return diseaseDAO.getAvailableDiseasesCount(diseaseId, params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<LabTest> getAvailableLabTests(String diseaseId,
			Map params, String order, String orderBy, String limit,
			String offset) {
		return diseaseDAO.getAvailableLabTests(diseaseId, params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableLabTestsCount(String diseaseId, Map params) {
		return diseaseDAO.getAvailableLabTestsCount(diseaseId, params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Procedure> getAvailableProcedures(String diseaseId,
			Map params, String order, String orderBy, String limit,
			String offset) {
		return diseaseDAO.getAvailableProcedures(diseaseId, params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableProceduresCount(String diseaseId, Map params) {
		return diseaseDAO.getAvailableProceduresCount(diseaseId, params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Symptom> getAvailableSymptoms(String diseaseId,
			Map params, String order, String orderBy, String limit,
			String offset) {
		return diseaseDAO.getAvailableSymptoms(diseaseId, params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableSymptomsCount(String diseaseId, Map params) {
		return diseaseDAO.getAvailableSymptomsCount(diseaseId, params);
	}


}
