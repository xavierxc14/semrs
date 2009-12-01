package com.googlecode.semrs.util;

/**
 * @author Roger Marin
 *
 * Clase Utilitaria que sirve como Bean para algunos
 * de los parametors de configuración especificados en el
 * archivo semrs-config.properties
 */

public class ConnectionInfo {

	private String dbClass = null;  
	private String dbPassword = null;
	private String dbType = null;
	private String dbURL = null;    
	private String dbUser = null;  
	private boolean reload = false;



	public String getDbClass() {
		return dbClass;
	}
	public void setDbClass(String dbClass) {
		this.dbClass = dbClass;
	}

	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}


	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}


	public String getDbURL() {
		return dbURL;
	}
	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}



	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	
	public boolean isReload() {
		return reload;
	}
	public void setReload(boolean reload) {
		this.reload = reload;
	}


}


