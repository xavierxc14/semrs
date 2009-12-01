package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.server.dao.ProcedureDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.ProcedureService;

public class ProcedureServiceJenaImpl implements ProcedureService{

private static final Logger LOG = Logger.getLogger(ProcedureServiceJenaImpl.class);
	
	private ProcedureDAO procedureDAO;
	
	public ProcedureServiceJenaImpl(){
		  super();
	}
	
	public void setProcedureDAO(ProcedureDAO procedureDAO) {
		this.procedureDAO = procedureDAO;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public void deleteProcedure(final Procedure procedure) throws DeleteException {
		procedureDAO.deleteProcedure(procedure);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Procedure getProcedure(final String id, final boolean deep) {
		return procedureDAO.getProcedure(id, deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getProcedureCount() {
		return procedureDAO.getProcedureCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getProcedureCount(final Map params) {
		return procedureDAO.getProcedureCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Procedure> listProcedures(final String order, final String orderBy,
			final String limit, final String offset) {
		return procedureDAO.listProcedures(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Procedure> listProceduresByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		return procedureDAO.listProceduresByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Procedure> listProcedures() {
		return procedureDAO.listProcedures();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public Procedure save(final Procedure procedure) throws SaveOrUpdateException {
		return procedureDAO.save(procedure);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Disease> getAvailableDiseases(String procedureId, Map params, String order,
			String orderBy, String limit, String offset) {
		return procedureDAO.getAvailableDiseases(procedureId, params, order, orderBy, limit, offset);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getAvailableDiseasesCount(String procedureId, Map params) {
		return procedureDAO.getAvailableDiseasesCount(procedureId, params);
	}
}
