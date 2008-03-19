package example;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;

import example.model.PersistentOntology;



public class ContextListener implements ServletContextListener {

	
	public static final String DB_URL = "jdbc:postgresql://localhost/jenabean";
	public static final String DB_USER = "postgres";
    public static final String DB_PASSWD = "123456";
    public static final String DB = "PostgreSQL";
    public static final String DB_DRIVER = "org.postgresql.Driver";

    // Static variables
    //////////////////////////////////

    // database connection parameters, with defaults
    private static String s_dbURL = DB_URL;
    private static String s_dbUser = DB_USER;
    private static String s_dbPw = DB_PASSWD;
    private static String s_dbType = DB;
    private static String s_dbDriver = DB_DRIVER;

    // if true, reload the data
    private static boolean s_reload = false;

    // source URL to load data from; if null, use default
    private static String s_source;
    

    static public final String NL = System.getProperty("line.separator") ;
    
    
    
	
	
	public void contextDestroyed(ServletContextEvent ev) {
	}

	public void contextInitialized(ServletContextEvent ev) {
		
		   // check for default sources
        if (s_source == null) {
            s_source = "http://example.org/";
        }

		
		   // create the helper class we use to handle the persistent ontologies
        PersistentOntology po = new PersistentOntology();

        // ensure the JDBC driver class is loaded
        try {
            Class.forName( s_dbDriver );
        }
        catch (Exception e) {
            System.err.println( "Failed to load the driver for the database: " + e.getMessage() );
            System.err.println( "Have you got the CLASSPATH set correctly?" );
        }

        // are we re-loading the data this time?
        if (s_reload) {

            // we pass cleanDB=true to clear out existing models
            // NOTE: this will remove ALL Jena models from the named persistent store, so
            // use with care if you have existing data stored
            ModelMaker maker = po.getRDBMaker( s_dbURL, s_dbUser, s_dbPw, s_dbType, true );

            // now load the source data into the newly cleaned db
            po.loadDB( maker, s_source );
        }

        // now we list the classes in the database, to show that the persistence worked
        ModelMaker maker = po.getRDBMaker( s_dbURL, s_dbUser, s_dbPw, s_dbType, false );
        //po.listClasses( maker, s_source );
        //Model model = po.getModel( maker, s_source );
		
		
		//OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF );
        Model  m =  po.getModel( maker, s_source );
		Jenabean.instance().bind(m);
	}


}
