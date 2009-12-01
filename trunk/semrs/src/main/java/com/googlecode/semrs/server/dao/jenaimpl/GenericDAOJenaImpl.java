package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletInfGraph;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;
import thewebsemantic.binding.Jenabean;

import com.googlecode.semrs.model.Persistable;
import com.googlecode.semrs.server.dao.DataManager;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.util.SparqlUtil;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.sdb.store.DatasetStore;

public class GenericDAOJenaImpl implements GenericDAO {
   
	
    public GenericDAOJenaImpl() {
        super();
    }
    
    private static RDF2Bean reader;
    
    private static Bean2RDF writer;
    
    private static OntModel model;
    
    private static Jenabean jenabean;
    
    private static DataManager dataManager;
    
   
    private static final Logger LOG = Logger.getLogger(GenericDAOJenaImpl.class);
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean exists(final Class<?> clazz, final String id) {
	      return getReader().exists(clazz,id);
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public  void save(final Persistable persistable) throws SaveOrUpdateException {
		LOG.debug("Saving object " + persistable.toString());
		if(persistable.getId().contains("@")){
			throw new SaveOrUpdateException("C&oacute;digo o identificador no permitido");
		}
		try{
			getWriter().save(persistable);
			classify();
		}catch(Exception e){
			LOG.error("Saving object " + persistable.toString() + " caused by " + e);
			throw new SaveOrUpdateException("Error Saving object " + persistable.toString() + " caused by " + e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void saveDeep(final Persistable persistable) throws SaveOrUpdateException {
		LOG.debug("Deep Saving object " + persistable.toString());
		if(persistable.getId().contains("@")){
			throw new SaveOrUpdateException("C&oacute;digo o identificador no permitido");
		}
		try{
			getWriter().saveDeep(persistable);
			classify();
		}catch(Exception e){
			LOG.error("Deep Saving object " + persistable.toString() + " caused by " + e);
			throw new SaveOrUpdateException("Error Deep Saving object " + persistable.toString() + " caused by " + e);
		}
	}


	@Override
	@SuppressWarnings("unchecked")
	public  void delete(final Persistable persistable) throws DeleteException {
		LOG.debug("Deleting object " + persistable.toString());
			try{
				getWriter().delete(persistable);
				classify();
			}catch(Exception e){
				LOG.error("Deleting object " + persistable.toString() + " caused by " + e);
				throw new DeleteException("Deleting object " + persistable.toString() + " caused by " + e);
			}
	}
	
	
	/**
	 * Used to classify pellet model after modifications
	 */
	protected  final void classify(){
	     ((PelletInfGraph) getModel().getGraph()).classify();
	}
	

	protected  final RDF2Bean getReader(){
	   	LOG.debug("Loading RDF2Bean instance" );
		reader = getJenabean().reader();
		return reader;
		
	}
    
	
	protected final Bean2RDF getWriter(){
		LOG.debug("Loading Bean2RDF instance" );
		writer = getJenabean().writer();
		return writer;
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public final OntModel getModel(){
		model = (OntModel)getJenabean().model();
		return model;
	
	}


	@Override
	@SuppressWarnings("unchecked")
	public  <T> T load(final Class<T> clazz, final String id, boolean deep) {
		LOG.debug("loading instance of " + clazz.getName() + " with id "+ id );
		T o = null;
		if(!id.equals("")){
			try {
				if(deep){
					o = getReader().loadDeep(clazz, id);
				}else{
					o = getReader().load(clazz, id);	
				}
			} catch (NotFoundException e) {
				LOG.debug("Instance of " + clazz + " with id " + id + " not found");
			}
		}
		return o;
	}


	@Override
	@SuppressWarnings("unchecked")
	public  <T> Collection<T> load(final Class<T> clazz) {
		LOG.debug("Loading instances of " + clazz);
		Collection<T> results = null;
			try {
				results = getReader().load(clazz);			
			} catch (Exception e) {
				LOG.error("Failed Loading instances of " + clazz + " caused by: " + e);
			}
		return results;
	}


	@Override
	@SuppressWarnings("unchecked")
	public  <T> Collection<T> find(final Class<T> clazz, final String sparqlQuery) {
        return Sparql.exec( getModel(), clazz, sparqlQuery);
	}


	@Override
	@SuppressWarnings("unchecked")
	public  <T> Collection<T> find(final Class<T> clazz, final String sparqlQuery,
			QuerySolutionMap initialBindings) {
		 return SparqlUtil.exec( getModel(), clazz, sparqlQuery, initialBindings);
	}


	


	protected final Jenabean getJenabean() {
		LOG.debug("Loading Jenabean instance");
		   jenabean = getDataManager().getJenabean();
		//}
		return jenabean;
	}


	public void setDataManager(DataManager dataManager) {
		GenericDAOJenaImpl.dataManager = dataManager;
	}
	
	public final DataManager getDataManager(){
		return dataManager;
	}


	@Override
	@SuppressWarnings("unchecked")
	public int count(final String queryString, final String var) {
		LOG.debug("Executing Query for " + var + " : "+ queryString);
		//getDataManager().reconnect();
		//Dataset ds = DatasetStore.create(getDataManager().getStore()) ;
		final com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		final QueryExecution qe  = QueryExecutionFactory.create(query,getModel());
		//final QueryExecution qe  = QueryExecutionFactory.create(query,ds);
		Integer count = 0;
		try{	
			final ResultSet rs=qe.execSelect();
			final Object value = ((Literal)rs.nextSolution().get(var)).getValue();
			count = Integer.valueOf(value.toString());
			qe.close();
			//getDataManager().closeStore();
		}catch(Exception e){
			LOG.error("Executing Query for " + var + " : "+ queryString + " caused by " + e);
		}finally{ 
			qe.close();
		}
		//getDataManager().closeStore();
		return count;
			

	}


	@Override
	@SuppressWarnings("unchecked")
	public  <T> Collection<T> execQuery(final Class<T> clazz, final String queryString, final String sintax) {
		LOG.debug("Executing Query "+ queryString);
			//getDataManager().reconnect();
			//Dataset ds = DatasetStore.create(getDataManager().getStore()) ;
			Collection<T> result = null;
			try{
			  result = SparqlUtil.exec(getModel(),getReader(), clazz, queryString, sintax);
			  // result = SparqlUtil.exec(ds, getReader(), clazz, queryString, sintax);
			}catch(Exception e){
				LOG.error("Executing Query "+ queryString + " caused by " + e);
			}
			//getDataManager().closeStore();
			return result;
	}


	@Override
	public final void fill(final Persistable persistable, final String property){
		 getReader().fill(persistable, property);
	}




}
