package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.DiagnosisContainer;
import com.googlecode.semrs.model.Encounter;
import com.googlecode.semrs.server.dao.EncounterDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.EncounterService;

public class EncounterServiceJenaImpl implements EncounterService{
	
	private static final Logger LOG = Logger.getLogger(EncounterServiceJenaImpl.class);

	private EncounterDAO encounterDAO;
	
	public EncounterServiceJenaImpl(){
		  super();
	}
	
	public void setEncounterDAO(EncounterDAO encounterDAO) {
		this.encounterDAO = encounterDAO;
	}
	
	

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public void deleteEncounter(final Encounter encounter)
			throws DeleteException {
		synchronized(encounter){
			if(!encounter.isCurrent()){
				LOG.error("Tried to delete encounter with id: " + encounter.getId());
				throw new DeleteException("Encounter cannot be deleted");

			}else{
				encounterDAO.deleteEncounter(encounter);
			}
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Encounter getEncounter(final String id, final boolean deep) {
		return encounterDAO.getEncounter(id,deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getEncounterCount() {
		return encounterDAO.getEncounterCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getEncounterCount(final Map params) {
		return encounterDAO.getEncounterCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Encounter> listEncounters() {
		return encounterDAO.listEncounters();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Encounter> listEncounters(final String order, final String orderBy,
			final String limit, final String offset) {
		return encounterDAO.listEncounters(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Encounter> listEncountersByQuery(final Map params,
			final String order, final String orderBy, final String limit, String offset) {
	    return encounterDAO.listEncountersByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public Encounter saveEncounter(final Encounter encounter) throws SaveOrUpdateException{
		synchronized(encounter){	
			if(!encounter.isFinalized()){
				if(!encounter.isCurrent()){
					encounter.setFinalized(true);
				}
				encounterDAO.saveEncounter(encounter);
			}else{
				LOG.error("Tried to modify encounter with id: " + encounter.getId());
				throw new SaveOrUpdateException("Encounter cannot be modified");
			}
		}
		return encounter;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Encounter saveDeepEncounter(Encounter encounter)
			throws SaveOrUpdateException {
		synchronized(encounter){	
			if(!encounter.isFinalized()){
				if(!encounter.isCurrent()){
					encounter.setFinalized(true);
				}
				encounterDAO.saveDeepEncounter(encounter);
			}else{
				LOG.error("Tried to modify encounter with id: " + encounter.getId());
				throw new SaveOrUpdateException("Encounter cannot be modified");
			}
		}
		return encounter;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<DiagnosisContainer> getAssistedDiagnosis(Map params) {
		return encounterDAO.getAssistedDiagnosis(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<DiagnosisContainer> getAssistedDiagnosisPrediction(Map params) {
		return encounterDAO.getAssistedDiagnosisPrediction(params);
	}

}
