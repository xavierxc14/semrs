package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.dao.ProcedureDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public class ProcedureDAOJenaImpl extends GenericDAOJenaImpl implements ProcedureDAO  {

	private static final Logger LOG = Logger.getLogger(ProcedureDAOJenaImpl.class);
	

	@Override
	public void deleteProcedure(final Procedure procedure) throws DeleteException {
		super.delete(procedure);
	}

	@Override
	public int getProcedureCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Procedure>" +
			"SELECT (count (distinct *) As ?procedureCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/ProcedureTest> ." +
			"} ";
 
		return super.count(queryString, "procedureCount");
	}

	@Override
	public int getProcedureCount(final Map params) {
		
		final String procedureId = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		
		final StringBuffer queryBuffer = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			queryBuffer.append("    ?s bean:name ?name .");
			queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(procedureId!=null && !procedureId.equals("")){
			queryBuffer.append("    ?s bean:id ?procedureId .");
			queryBuffer.append(" FILTER regex(?procedureId, \""+procedureId+"\", \"i\") .");
		}
	    if(diseases!=null && diseases.size()>0){
			queryBuffer.append("    ?s bean:relatedDiseasesP ?diseases .");
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
            "PREFIX user: <http://semrs.googlecode.com/Procedure>" +
            "SELECT (count (distinct *) As ?procedureCount) " +
            "WHERE { " +
            "    ?s a  <http://semrs.googlecode.com/Procedure> ." +
                 queryBuffer.toString() +
            "} ";

			return super.count(queryString, "procedureCount");
	}

	@Override
	public Procedure getProcedure(final String id, final boolean deep) {
		final Procedure procedure = super.load(Procedure.class, id, deep);
		return procedure;
	}

	@Override
	public Collection<Procedure> listProcedures() {
		return (Collection<Procedure>) super.load(Procedure.class);
	}

	@Override
	public Collection<Procedure> listProcedures(final String order, final String orderBy,
			final String limit, final String offset) {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Procedure>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Procedure> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


		final Collection<Procedure> result = super.execQuery(Procedure.class, queryString, "");
		return result;
	}

	@Override
	public Collection<Procedure> listProceduresByQuery(Map params, String order,
			String orderBy, String limit, String offset) {
	
		final String procedureId = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		
		StringBuffer query = new StringBuffer();
		if(!orderBy.equals("")){
			query.append(" OPTIONAL { ");
			query.append("    ?s bean:"+orderBy+" ?"+orderBy+" .");
			query.append( "} .");
			
		}
		
	    if(name!=null && !name.equals("")){
			query.append("    ?s bean:name ?name .");
			query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(procedureId!=null && !procedureId.equals("")){
			query.append("    ?s bean:id ?procedureId .");
			query.append(" FILTER regex(?procedureId, \""+procedureId+"\", \"i\") .");
		}
	    if(diseases!=null && diseases.size()>0){
			query.append("    ?s bean:relatedDiseasesP ?diseases .");
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
             "PREFIX user: <http://semrs.googlecode.com/Procedure>" +
             "SELECT DISTINCT ?s " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Procedure> ." +
                  query.toString() +
             "} "+
             "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
	     
	     
	    final Collection<Procedure> result = super.execQuery(Procedure.class, queryString, "");
		return result;
	}

	@Override
	public Procedure save(final Procedure procedure) throws SaveOrUpdateException {
		super.save(procedure);
		return procedure;
	}


	@Override
	public Collection<Disease> getAvailableDiseases(String procedureId, Map params, String order,
			 String orderBy, String limit, String offset){

		Collection<Disease> result = null; 
		Procedure procedure = getProcedure(procedureId,false);
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

		if(procedure != null){  
			super.fill(procedure, "relatedDiseasesP");
			Collection<Disease> diseases = procedure.getRelatedDiseasesP();
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
		             "} ";;
			     
			     
			    result = super.execQuery(Disease.class, queryString, "");
		
		
		
		return result;
	}

	@Override
	public int getAvailableDiseasesCount(String procedureId, Map params) {
		
		final String symptomId = params.get("id").toString();
		final String name =  params.get("name").toString();
		
		final StringBuffer query = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			query.append("    ?s bean:name ?name .");
			query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    
	    if(symptomId!=null && !symptomId.equals("")){
			query.append(" FILTER regex(?id, \""+symptomId+"\", \"i\") .");
		}

	    Procedure procedure = getProcedure(procedureId,false);
		if(procedure != null){  
			super.fill(procedure, "relatedDiseasesP");
			Collection<Disease> diseases = procedure.getRelatedDiseasesP();
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
