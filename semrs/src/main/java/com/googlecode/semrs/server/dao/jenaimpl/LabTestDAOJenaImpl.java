package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import thewebsemantic.NotFoundException;
import thewebsemantic.Sparql;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.dao.LabTestDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;

public class LabTestDAOJenaImpl extends GenericDAOJenaImpl implements LabTestDAO {
	
	private static final Logger LOG = Logger.getLogger(LabTestDAOJenaImpl.class);


	@Override
	public void deleteLabTest(final LabTest labTest) throws DeleteException {
		super.delete(labTest);
	}

	@Override
	public int getLabTestCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/LabTest>" +
			"SELECT (count (distinct *) As ?labTestCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/LabTest> ." +
			"} ";


		return super.count(queryString,"labTestCount");
	}

	@Override
	public int getLabTestCount(final Map params) {
		
		final String labTestId = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		
		final StringBuffer queryBuffer = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			queryBuffer.append("    ?s bean:name ?name .");
			queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(labTestId!=null && !labTestId.equals("")){
			queryBuffer.append("    ?s bean:id ?labTestId .");
			queryBuffer.append(" FILTER regex(?labTestId, \""+labTestId+"\", \"i\") .");
		}
	    if(diseases!=null && diseases.size()>0){
			queryBuffer.append("    ?s bean:relatedDiseases ?diseases .");
			queryBuffer.append("    ?diseases bean:id ?diseaseId .");
			String diseaseIds = "";
			int counter = 0;
			for(String disease : diseases){
				if(counter==diseases.size()-1){
					diseaseIds += disease;
				}else{
					diseaseIds += disease + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?diseaseId, \""+diseaseIds+"\", \"i\") .");
		}
		

	    final String queryString = 
            "PREFIX bean: <http://semrs.googlecode.com/>" +
            "PREFIX user: <http://semrs.googlecode.com/LabTest>" +
            "SELECT (count (distinct *) As ?labTestCount) " +
            "WHERE { " +
            "    ?s a  <http://semrs.googlecode.com/LabTest> ." +
                 queryBuffer.toString() +
            "} ";

	
		 return super.count(queryString,"labTestCount");
	}

	@Override
	public LabTest getLabTest(final String id, final boolean deep) {
		final LabTest labTest= super.load(LabTest.class, id, deep);
		return labTest;
	}

	@Override
	public Collection<LabTest> listLabTests() {
		return (Collection<LabTest>) super.load(LabTest.class);
	}

	@Override
	public Collection<LabTest> listLabTests(final String order, final String orderBy,
			final String limit, final String offset) {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/LabTest>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/LabTest> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


		
		final Collection<LabTest> result = super.execQuery(LabTest.class, queryString, "");
		return result;
	}

	@Override
	public Collection<LabTest> listLabTestsByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
	
		final String labTestId = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		
		final StringBuffer query = new StringBuffer();
		if(!orderBy.equals("")){
			query.append(" OPTIONAL { ");
			query.append("    ?s bean:"+orderBy+" ?"+orderBy+" .");
			query.append( "} .");
			
		}
		
	    if(name!=null && !name.equals("")){
			query.append("    ?s bean:name ?name .");
			query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(labTestId!=null && !labTestId.equals("")){
			query.append("    ?s bean:id ?labTestId .");
			query.append(" FILTER regex(?labTestId, \""+labTestId+"\", \"i\") .");
		}
	    if(diseases!=null && diseases.size()>0){
			query.append("    ?s bean:relatedDiseases ?diseases .");
			query.append("    ?diseases bean:id ?diseaseId .");
			String diseaseIds = "";
			int counter = 0;
			for(String disease : diseases){
				if(counter==diseases.size()-1){
					diseaseIds += disease;
				}else{
					diseaseIds += disease + "|";	
				}
				counter++;
			}
			query.append(" FILTER regex(?diseaseId, \""+diseaseIds+"\", \"i\") .");
		}
		
	   
	    final String queryString = 
             "PREFIX bean: <http://semrs.googlecode.com/>" +
             "PREFIX user: <http://semrs.googlecode.com/LabTest>" +
             "SELECT DISTINCT ?s " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/LabTest> ." +
                  query.toString() +
             "} "+
             "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
	     
	     
	    
	    final Collection<LabTest> result = super.execQuery(LabTest.class, queryString, "");
		return result;
	}

	@Override
	public LabTest save(final LabTest labTest) throws SaveOrUpdateException {
		super.save(labTest);
		return labTest;
	}

	
	
	@Override
	public Collection<Disease> getAvailableDiseases(final String labTestId, final Map params, final String order,
			final String orderBy, final String limit, final String offset){

		Collection<Disease> result = null; 
		LabTest labTest = getLabTest(labTestId,false);
		final String diseaseId = params.get("id").toString();
		final String name =  params.get("name").toString();
		
		final StringBuffer query = new StringBuffer();
		if(!orderBy.equals("")){
			query.append(" OPTIONAL { ");
			query.append("    ?s bean:"+orderBy+" ?"+orderBy+" .");
			query.append( "} .");
			
		}
		
	    if(name!=null && !name.equals("")){
			query.append("    ?s bean:name ?name .");
			query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    
	    if(diseaseId!=null && !diseaseId.equals("")){
			query.append(" FILTER regex(?id, \""+diseaseId+"\", \"i\") .");
		}

		if(labTest != null){  
			super.fill(labTest, "relatedDiseases");
			Collection<Disease> diseases = labTest.getRelatedDiseases();
			
			  if(diseases!=null && diseases.size()>0){
					String diseaseIds = "";
					int counter = 0;
					for(Disease disease : diseases){
						if(counter==diseases.size()-1){
							diseaseIds += "?id != " + "\""+disease.getId()+"\"";
						}else{
							diseaseIds += "?id != " + "\""+disease.getId()+"\""+ " && ";	
						}
						counter++;
					}
					
					query.append(" FILTER ("+diseaseIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/Disease> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} "+
		             "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
			     
			     
			    result = super.execQuery(Disease.class, queryString, "");
		
		
		
		return result;
	}
	
	@Override
	public int getAvailableDiseasesCount(final String labTestId, final Map params){

		final String diseaseId = params.get("id").toString();
		final String name =  params.get("name").toString();
		
		final StringBuffer query = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			query.append("    ?s bean:name ?name .");
			query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    
	    if(diseaseId!=null && !diseaseId.equals("")){
			query.append(" FILTER regex(?id, \""+diseaseId+"\", \"i\") .");
		}

	    LabTest labTest = getLabTest(labTestId,false);
		if(labTest != null){  
			super.fill(labTest, "relatedDiseases");
			Collection<Disease> diseases = labTest.getRelatedDiseases();
			  if(diseases!=null && diseases.size()>0){
					String diseaseIds = "";
					int counter = 0;
					for(Disease disease : diseases){
						if(counter==diseases.size()-1){
							diseaseIds += "?id != " + "\""+disease.getId()+"\"";
						}else{
							diseaseIds += "?id != " + "\""+disease.getId()+"\""+ " && ";	
						}
						counter++;
					}
					
					query.append(" FILTER ("+diseaseIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT (count (distinct *) As ?diseaseCount) " +
		             "WHERE { " +
		             "    ?s a <http://semrs.googlecode.com/Disease> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";
			     
			     
			   final int result = super.count(queryString,"diseaseCount");
		
		
		
		return result;
	}

}
