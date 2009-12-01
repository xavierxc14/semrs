package com.googlecode.semrs.server.dao;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.googlecode.semrs.jena.sdb.SDBConnector;
import com.googlecode.semrs.util.PropertiesInfo;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.store.DatasetStore;

import thewebsemantic.binding.Jenabean;

/**
 * 
 * @author Roger Marin
 *
 */
public class DataManager {
	
	private DataSource dataSource;
	
	private PropertiesInfo propertiesInfo;

	private SDBConnector sdbConnector;
	
	private String nameSpace = "http://semrs.googlecode.com/";
	
	private Jenabean jenabean;
	
	private Dataset dataset;
	
	private OntModel ontModel;
	
	private Store store;
	
	private static final Logger LOG = Logger.getLogger(DataManager.class);
	
    public DataManager(){
    }

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PropertiesInfo getPropertiesInfo() {
		return propertiesInfo;
	}

	public void setPropertiesInfo(PropertiesInfo propertiesInfo) {
		this.propertiesInfo = propertiesInfo;
	}

	/**
	 * 
	 * @return El objeto que contiene la "Instancie" actual de Jenabean
	 */
	public synchronized Jenabean getJenabean() {
		LOG.debug("Loading Jenabean instance");
		if(jenabean == null){
			synchronized(this){
			 if(jenabean == null){
				 LOG.info("Creating new Jenabean instance");
				//if(System.getProperty("jenabean.fieldaccess")==null || 
				//		!Boolean.parseBoolean(System.getProperty("jenabean.fieldaccess"))){
				//	System.setProperty("jenabean.fieldaccess", "true");
				//}
				nameSpace = propertiesInfo.getNameSpace();
				OntModel  m  = getOntModel(nameSpace);
				LOG.info("Binding OntModel to jenabean...");
				jenabean = Jenabean.instance();
				jenabean.bind(m);
			 }else{
				 jenabean = getSDBConnector().reconnectModel(jenabean);
			 }
			}
		}else{
			   jenabean = getSDBConnector().reconnectModel(jenabean);
		}
		return jenabean;
	}
	
	/**
	 * 
	 * @return El objeto Dataset
	 */
	public Dataset getDataSet(){
	    dataset = DatasetStore.create(getStore());
		return dataset;
	}
	
	/**
	 * 
	 * @return
	 */
	public SDBConnector getSDBConnector(){
		if(sdbConnector == null){
			synchronized(this){
		      if(sdbConnector == null){
			     sdbConnector = new SDBConnector(dataSource, propertiesInfo);
		      }
			}
		}
		return sdbConnector;
	}

	/**
	 * 
	 * @param modelName
	 * @return
	 */
	public OntModel getOntModel(String modelName) {
		if(ontModel==null){
			synchronized(this){
			 if(ontModel==null){	
			   ontModel = getSDBConnector().getJenaOntModel(modelName);
			 }
			}
		}
		return ontModel;
	}

	/**
	 * 
	 * @return El Objeto Triple Store 
	 */
	public Store getStore() {
		store = getSDBConnector().getSDBStore();
		return store;
	}
	
	/**
	 * 
	 */
	public void closeStore() {
		  getSDBConnector().close();

	}
	
	/**
	 * 
	 */
	public void reconnect(){
		 store = getSDBConnector().reconnect();
	}
	
	/**
	 * 
	 */
	public synchronized void closeConnection(){
		try {
			getSDBConnector().closeConnection();
		} catch (Exception e) {
			LOG.error("Error closing connection caused by : ", e);
		}
		
	}
	



}
