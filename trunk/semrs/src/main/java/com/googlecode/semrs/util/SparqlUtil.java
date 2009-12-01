package com.googlecode.semrs.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletQueryExecution;

import thewebsemantic.RDF2Bean;


import com.googlecode.semrs.server.UserServlet;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Roger Marin
 * @see thewebsemantic.Sparql
 * Clase Que extiende las funcionalidades de la clase thewebsemantic.
 * Sparql de Jenabean, incluye funcionalidades para escojer la syntaxis,
 * incluir bindings como variables de SPARQL y utilizar el Query Execution Factory de Pellet.
 */
public class SparqlUtil {
	
	private static final Logger LOG = Logger.getLogger(SparqlUtil.class);


	/**
	 * Helpful for binding a query result set with a single solution
	 * subject to a particular java bean.  This returns a collection of beans.
	 * Queries are required to follow this pattern in the select clause:
	 * 
	 * <code>SELECT ?s WHERE ...</code>
	 * 
	 * Jenabean will attempt to create an instance of type <code>c</code> bound to 
	 * the RDF resources returned in your query.  It's important to use 
	 * name variable ?s.  This is the named variable Jenabean will expect.
	 * You should make sure that your query
	 * only returns one type or base type, for example, this snippet ensures that
	 * only resources of OWL type Bird are selected...
	 * 
	 * <code>SELECT ?s WHERE { ?s a :Bird ...</code>
	 * 
	 * If you SPARQL query returns heterogenous types, classcast exceptions
	 * will be thrown.
	 * 
	 * @param <T>
	 * @param m jena model
	 * @param c Java Class to which the OWL type is bound to
	 * @param query a full SPARQL query
	 * @return
	 */

	public static <T> Collection<T> exec(Dataset ds, RDF2Bean reader, Class<T> c, String query, String sintax) {
		QueryExecution qexec = getQueryExec(ds, query, getSyntax(sintax));
		Collection<T> beans = new LinkedList<T>();
		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();)beans.add(reader.load(c, resource(results)));
			
		} catch(Exception e){
			LOG.error("executing query (ds) " + query + " caused by " + e);
		}finally {
			qexec.close();
		}
		return beans;
	}

	/**
	 * 
	 * @param ds
	 * @param query
	 * @param jenaSyntax
	 * @return
	 */
	private static QueryExecution getQueryExec(Dataset ds, String query, Syntax jenaSyntax ) {
		Query q = null;
		if(jenaSyntax!=null){
			q = QueryFactory.create(query, jenaSyntax); 
		}else{
			q = QueryFactory.create(query);
		}
		return QueryExecutionFactory.create(q, ds);
	}

	private static Resource resource(ResultSet results) {
		return results.nextSolution().getResource("s");
	}

	/**
	 * 
	 * @param <T>
	 * @param m
	 * @param c
	 * @param query
	 * @param initialBindings
	 * @return
	 */
	public static <T> Collection<T> exec(Model m, Class<T> c, String query, QuerySolutionMap initialBindings) {
		RDF2Bean reader = new RDF2Bean(m);
		QueryExecution qexec = getQueryExec(m, query, initialBindings);
		Collection<T> beans = new LinkedList<T>();
		try {
			ResultSet results = qexec.execSelect();
			for (;results.hasNext();) beans.add(reader.load(c, resource(results)));
			return beans;
		} finally {
			qexec.close();
		}
	}

	/**
	 * 
	 * @param m
	 * @param query
	 * @param initialBindings
	 * @return
	 */
	private static QueryExecution getQueryExec(Model m, String query, QuerySolutionMap initialBindings) {
		Query q = QueryFactory.create(query);
		return QueryExecutionFactory.create(q, m, initialBindings);
	}

	/**
	 * 
	 * @param <T>
	 * @param m
	 * @param c
	 * @param query
	 * @return
	 */       
	public static <T> Collection<T> pelletExec(Model m, Class<T> c, String query) {
		RDF2Bean reader = new RDF2Bean(m);
		QueryExecution qexec = getPelletQueryExec(m, query);
		Collection<T> beans = new LinkedList<T>();
		try {
			ResultSet results = qexec.execSelect();
			for (;results.hasNext();) beans.add(reader.load(c, resource(results)));
			return beans;
		} finally {
			qexec.close();
		}
	}
	/**
	 * 
	 * @param m
	 * @param query
	 * @return
	 */
	private static QueryExecution getPelletQueryExec(Model m, String query){
		Query q = QueryFactory.create(query);
		QueryExecution qexec = new PelletQueryExecution(q, m);
		return qexec;
	}


	/**
	 * 
	 * @param <T>
	 * @param m
	 * @param c
	 * @param query
	 * @param sintax
	 * @return
	 */
	public static <T> Collection<T> exec(Model m, Class<T> c, String query, String sintax) {
		RDF2Bean reader = new RDF2Bean(m);
		QueryExecution qexec = getQueryExec(m, query, getSyntax(sintax));
		Collection<T> beans = new LinkedList<T>();
		try {
			ResultSet results = qexec.execSelect();
			for (;results.hasNext();) beans.add(reader.load(c, resource(results)));
			return beans;
		} finally {
			qexec.close();
		}
	}

	/**
	 * 
	 * @param m
	 * @param queryString
	 * @param jenaSyntax
	 * @return
	 */
	private static final QueryExecution getQueryExec(final Model m, final String queryString, final Syntax jenaSyntax){
		        com.hp.hpl.jena.query.Query query = null;
			    QueryExecution qe = null;
				try{
					if(jenaSyntax!=null){
						query = QueryFactory.create(queryString, jenaSyntax);
					}else{
						query = QueryFactory.create(queryString);
					}
				  qe = QueryExecutionFactory.create(query, m);
				}catch(Exception e){
					LOG.error("creating query " + queryString + " caused by " + e);
				}
				return qe;
}

	/**
	 * 
	 * @param <T>
	 * @param m
	 * @param reader
	 * @param c
	 * @param query
	 * @param sintax
	 * @return
	 */
	public static final <T> Collection<T> exec(final Model m, final RDF2Bean reader,final Class<T> c, final String query, final String sintax) {
		        QueryExecution qexec = null;
		        final Collection<T> beans =  Collections.synchronizedList(new LinkedList<T>());
		        try {
		        	qexec = getQueryExec(m, query, getSyntax(sintax));
		        	final ResultSet results = qexec.execSelect();
		        	synchronized(beans){
		        		for (;results.hasNext();) beans.add(reader.load(c, resource(results)));
		        	}
		        }catch(Exception e){
		        	LOG.error("executing query " + query + " caused by " + e);
		        } finally {
		        	qexec.close();
		        }
		        return beans;
	}

	/**
	 * Metodo Utilitario que recibe una representación
	 * String de la Syntaxis ARQ a utilizar en los querys
	 * y retorna su representación real.
	 * @param syntaxName
	 * @return
	 */
	private static final Syntax getSyntax(final String syntaxName){

		Syntax jenaSyntax = null;

		if(syntaxName!=null && !syntaxName.equals("")){ 
			if(syntaxName.equals("syntaxARQ")){
				jenaSyntax = Syntax.syntaxARQ;
			}else if(syntaxName.equals("syntaxSPARQL")){
				jenaSyntax = Syntax.syntaxSPARQL;
			}else if(syntaxName.equals("syntaxRDQL")){
				jenaSyntax = Syntax.syntaxRDQL;
			}else if(syntaxName.equals("syntaxAlgebra")){
				jenaSyntax = Syntax.syntaxAlgebra;
			}else if(syntaxName.equals("defaultSyntax")){
				jenaSyntax = Syntax.defaultSyntax;
			}
		}

		return jenaSyntax;

	}


}
