package com.googlecode.semrs.server.exception;

public class DeleteException extends Exception{
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeleteException() {
	        super();
	    }
	    public DeleteException(String message){
	        super(message);
	    }
	    
	    public DeleteException( String message, Throwable cause){
	        super( message, cause );
	    }

}