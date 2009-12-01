package com.googlecode.semrs.server.dao;

import java.util.Collection;

import com.googlecode.semrs.model.Persistable;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.hp.hpl.jena.query.QuerySolutionMap;


public interface GenericDAO {
	
	public boolean exists(Class<?> clazz, String id);
	
	public void saveDeep(Persistable persistable) throws SaveOrUpdateException;
	
	public void save(Persistable persistable) throws SaveOrUpdateException;
	
	public Object getModel();
	
	public <T> T load(Class<T> clazz, String id, boolean deep);
	
	public <T> Collection<T> load(Class<T> clazz);
	
	public <T> Collection<T> find(Class<T> clazz, String sparqlQuery);
	
	public <T> Collection<T> find(Class<T> clazz, String sparqlQuery, QuerySolutionMap initialBindings);

	public void delete(Persistable persistable) throws DeleteException;
	
	public int count(String queryString, String var);
	
	public <T> Collection<T> execQuery(Class<T> clazz, String queryString, String syntax);
	
	public void fill(Persistable persistable, String property);
}
