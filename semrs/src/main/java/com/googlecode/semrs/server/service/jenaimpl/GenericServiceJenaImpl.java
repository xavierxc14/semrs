package com.googlecode.semrs.server.service.jenaimpl;


import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Persistable;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.util.ModelContainer;
import com.hp.hpl.jena.ontology.OntModel;

public class GenericServiceJenaImpl implements GenericService {
	
    private static final Logger LOG = Logger.getLogger(GenericServiceJenaImpl.class);
    
    private GenericDAO genericDAO;
    
    public GenericServiceJenaImpl() {
        super();
    }

	public void setGenericDAO(GenericDAO genericDAO) {
		this.genericDAO = genericDAO;
	}
    
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public boolean exists(final Class<?> clazz, final String id) {
		boolean exists = false;
		if(clazz!=null && (id!=null && !id.equals(""))){
			exists = genericDAO.exists(clazz, id);
		}else{
			LOG.warn("Invalid class and id parameters passed, class :" + clazz +" id :" + id);
		}
		return exists;
	}


	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public void save(final Persistable persistable) throws SaveOrUpdateException {
		 genericDAO.saveDeep(persistable);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public ModelContainer getModelContainer() {
		ModelContainer mc =  new ModelContainer((OntModel) genericDAO.getModel());
		return mc;
	 // ((OntModel) genericDAO.getModel()).write(System.out, "N3");	
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Object load(final Class<?> clazz, final String id, final boolean deep) {
		return genericDAO.load(clazz, id, deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<?> load(final Class<?> clazz) {
		return genericDAO.load(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly=false, propagation=Propagation.REQUIRED  )
	public void delete(final Persistable persistable) throws DeleteException {
		genericDAO.delete(persistable);
		
	}

	@Override
	public void fill(Persistable persistable, String propertyName) {
		genericDAO.fill(persistable, propertyName);
	}

	
	
}
