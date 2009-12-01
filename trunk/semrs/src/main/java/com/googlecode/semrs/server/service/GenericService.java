package com.googlecode.semrs.server.service;

import java.util.Collection;

import com.googlecode.semrs.model.Persistable;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.util.ModelContainer;



public interface GenericService {
	
	public boolean exists(Class<?> clazz, String id);
	
	public void save(Persistable persistable) throws SaveOrUpdateException;
	
	public Object load(Class<?> clazz, String id, boolean deep);
	
	public Collection<?> load(Class<?> clazz);
	
	public ModelContainer getModelContainer();
	
	public void delete(Persistable persistable) throws DeleteException;
	
	public void fill(Persistable persistable, String propertyName);

}
