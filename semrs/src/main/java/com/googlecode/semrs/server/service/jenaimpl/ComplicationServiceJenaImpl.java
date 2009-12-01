package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Complication;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.dao.ComplicationDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.ComplicationService;

public class ComplicationServiceJenaImpl implements ComplicationService{

	private static final Logger LOG = Logger.getLogger(ComplicationServiceJenaImpl.class);
	
	private ComplicationDAO complicationDAO;
	
	public ComplicationServiceJenaImpl(){
		  super();
	}
	
	public void setComplicationDAO(ComplicationDAO complicationDAO) {
		this.complicationDAO = complicationDAO;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public void deleteComplication(final Complication complication) throws DeleteException {
		complicationDAO.deleteComplication(complication);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Complication getComplication(final String id, final boolean deep) {
		return complicationDAO.getComplication(id,deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getComplicationCount() {
		return complicationDAO.getComplicationCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getComplicationCount(final Map params) {
		return complicationDAO.getComplicationCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Complication> listComplications() {
		return complicationDAO.listComplications();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Complication> listComplications(final String order,
			final String orderBy, final String limit, final String offset) {
		return complicationDAO.listComplications(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Complication> listComplicationsByQuery(final Map params,
			final String order, final String orderBy, final String limit, final String offset) {
		return complicationDAO.listComplicationsByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public Complication save(final Complication complication) throws SaveOrUpdateException {
		return complicationDAO.save(complication);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Procedure> getAvailableProcedures(String complicationId,Map params,String order,
			String orderBy,String limit,String offset) {
		return complicationDAO.getAvailableProcedures(complicationId, params, order, orderBy, limit, offset);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableProceduresCount(String complicationId, Map params) {
		return complicationDAO.getAvailableProceduresCount(complicationId, params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Symptom> getAvailableSymptoms(String complicationId,Map params,String order,
			String orderBy,String limit,String offset) {
		return complicationDAO.getAvailableSymptoms(complicationId, params, order, orderBy, limit, offset);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableSymptomsCount(String complicationId,Map params) {
		return complicationDAO.getAvailableSymptomsCount(complicationId, params);
	}


}
