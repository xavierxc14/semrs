package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.dao.DrugDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.DrugService;

public class DrugServiceJenaImpl implements DrugService{
	
	
	private static final Logger LOG = Logger.getLogger(DrugServiceJenaImpl.class);
	
	private DrugDAO drugDAO;
	
	public DrugServiceJenaImpl(){
		  super();
	}
	
	public void setDrugDAO(DrugDAO drugDAO) {
		this.drugDAO = drugDAO;
	}
	

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public void deleteDrug(final Drug drug) throws DeleteException {
		drugDAO.deleteDrug(drug);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Drug getDrug(String id, boolean deep) {
		return drugDAO.getDrug(id,deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getDrugCount() {
		return drugDAO.getDrugCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getDrugCount(final Map params) {
		return drugDAO.getDrugCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Drug> listDrugs() {
		return drugDAO.listDrugs();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Drug> listDrugs(final String order, final String orderBy,
			final String limit, final String offset) {
		return drugDAO.listDrugs(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Drug> listDrugsByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		return drugDAO.listDrugsByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public Drug save(final Drug drug) throws SaveOrUpdateException {
		return drugDAO.save(drug);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Symptom> getAvailableSymptoms(String drugId, Map params,String order,
			String orderBy,String limit,String offset) {
		return drugDAO.getAvailableSymptoms(drugId,params,order,orderBy,limit,offset);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableSymptomsCount(String drugId, Map params) {
		return drugDAO.getAvailableSymptomsCount(drugId,params);
	}

}
