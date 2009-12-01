package com.googlecode.semrs.server.exception;

public class SaveOrUpdateException extends Exception{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SaveOrUpdateException() {
	        super();
	    }
	    public SaveOrUpdateException(String message){
	        super(message);
	    }
	    
	    public SaveOrUpdateException( String message, Throwable cause){
	        super( message, cause );
	    }
}
