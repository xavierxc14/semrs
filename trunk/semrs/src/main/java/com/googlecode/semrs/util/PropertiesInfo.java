package com.googlecode.semrs.util;

/**
 * @author Roger Marin
 *
 * Clase Utilitaria utilizada como bean
 * para Algunas de las propiedades del archivo
 * semrs-config.properties.
 */
public class PropertiesInfo {
	
	private String dbType;
	
	private String reload;
	
	private String nameSpace;
	
	private String timeZone;

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getReload() {
		return reload;
	}

	public void setReload(String reload) {
		this.reload = reload;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	


}
