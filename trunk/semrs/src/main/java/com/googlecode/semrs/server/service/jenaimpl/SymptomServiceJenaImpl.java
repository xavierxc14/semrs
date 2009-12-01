package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.dao.SymptomDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.SymptomService;

public class SymptomServiceJenaImpl implements SymptomService {
	
	
	private static final Logger LOG = Logger.getLogger(SymptomServiceJenaImpl.class);
	
	private SymptomDAO symptomDAO;
	
	public SymptomServiceJenaImpl(){
		  super();
	}
	
	public void setSymptomDAO(SymptomDAO symptomDAO) {
		this.symptomDAO = symptomDAO;
	}
	

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public void deleteSymptom(final Symptom symptom) throws DeleteException {
		symptomDAO.deleteSymptom(symptom);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Symptom getSymptom(final String id, final boolean deep) {
		return symptomDAO.getSymptom(id, deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getSymptomCount() {
		return symptomDAO.getSymptomCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getSymptomCount(final Map params) {
		return symptomDAO.getSymptomCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Symptom> listSymptoms() {
		return symptomDAO.listSymptoms();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Symptom> listSymptoms(final String order, final String orderBy,
			final String limit, final String offset) {
		return symptomDAO.listSymptoms(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Symptom> listSymptomsByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		return symptomDAO.listSymptomsByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public Symptom save(final Symptom symptom) throws SaveOrUpdateException {
		return symptomDAO.save(symptom);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Symptom> getAvailableSymptoms(String id,Map params,String order,
			String orderBy,String limit,String offset) {
		return symptomDAO.getAvailableSymptoms(id, params, order, orderBy, limit, offset);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableSymptomsCount(String symptomId, Map params) {
		return symptomDAO.getAvailableSymptomsCount(symptomId, params);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Drug> getAvailableDrugs(String id,Map params,String order,
			String orderBy,String limit,String offset) {
		return symptomDAO.getAvailableDrugs(id, params, order, orderBy, limit, offset);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableDrugsCount(String symptomId, Map params) {
		return symptomDAO.getAvailableDrugsCount(symptomId, params);
	}

}
