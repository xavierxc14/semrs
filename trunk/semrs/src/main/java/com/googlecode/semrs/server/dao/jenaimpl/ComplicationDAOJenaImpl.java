package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.semrs.model.Complication;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.dao.ComplicationDAO;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public class ComplicationDAOJenaImpl extends GenericDAOJenaImpl implements ComplicationDAO {
	
	private static final Logger LOG = Logger.getLogger(ComplicationDAOJenaImpl.class);
	

	@Override
	public void deleteComplication(final Complication complication) throws DeleteException {
		super.delete(complication);
		

	}

	@Override
	public Complication getComplication(final String id, final boolean deep) {
		final Complication complication = super.load(Complication.class, id, deep);
		return complication;
	}

	@Override
	public int getComplicationCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Complication>" +
			"SELECT (count (distinct *) As ?complicationCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Complication> ." +
			"} ";


		return super.count(queryString, "complicationCount");
	}

	@Override
	public int getComplicationCount(final Map params) {
		
		final String complicationId = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> procedures = (Collection<String>)params.get("procedures");
		final Collection<String> symptoms = (Collection<String>)params.get("symptoms");
		
		final StringBuffer queryBuffer = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			queryBuffer.append("    ?s bean:name ?name .");
			queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(complicationId!=null && !complicationId.equals("")){
			queryBuffer.append("    ?s bean:id ?complicationId .");
			queryBuffer.append(" FILTER regex(?complicationId, \""+complicationId+"\", \"i\") .");
		}
	    if(procedures!=null && procedures.size()>0){
			queryBuffer.append("    ?s bean:complicationProcedures ?procedures .");
			queryBuffer.append("    ?procedures bean:id ?procedureId .");
			String procedureIds = "";
			int counter = 0;
			for(String procedure : procedures){
				if(counter==procedures.size()-1){
					procedureIds += procedure;
				}else{
					procedureIds += procedure + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?procedureId, \""+procedureIds+"\", \"i\") .");
			
		}
	    if(symptoms!=null && symptoms.size()>0){
			queryBuffer.append("    ?s bean:symptoms ?symptoms .");
			queryBuffer.append("    ?symptoms bean:id ?symptomId .");
			String symptomIds = "";
			int counter = 0;
			for(String symptom : symptoms){
				if(counter==procedures.size()-1){
					symptomIds += symptom;
				}else{
					symptomIds += symptom + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?symptomId, \""+symptomIds+"\", \"i\") .");
		}
		

		 String queryString = 
            "PREFIX bean: <http://semrs.googlecode.com/>" +
            "PREFIX user: <http://semrs.googlecode.com/Complication>" +
            "SELECT (count (distinct *) As ?complicationCount) " +
            "WHERE { " +
            "    ?s a  <http://semrs.googlecode.com/Complication> ." +
                 queryBuffer.toString() +
            "} ";


		 return super.count(queryString, "complicationCount");
	}

	@Override
	public Collection<Complication> listComplications() {
		return (Collection<Complication>) super.load(Complication.class);
	}

	@Override
	public Collection<Complication> listComplications(final String order,
			final String orderBy, final String limit, final String offset) {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Complication>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Complication> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


		final Collection<Complication> result = super.execQuery(Complication.class, queryString, "");
		return result;
	}

	@Override
	public Collection<Complication> listComplicationsByQuery(final Map params,
			final String order,final String orderBy, final String limit, final String offset) {
		
		final String complicationId = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> procedures = (Collection<String>)params.get("procedures");
		final Collection<String> symptoms = (Collection<String>)params.get("symptoms");
		
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
	    if(complicationId!=null && !complicationId.equals("")){
			query.append("    ?s bean:id ?complicationId .");
			query.append(" FILTER regex(?complicationId, \""+complicationId+"\", \"i\") .");
		}
	    if(procedures!=null && procedures.size()>0){
			query.append("    ?s bean:complicationProcedures ?procedures .");
			query.append("    ?procedures bean:id ?procedureId .");
			String procedureIds = "";
			int counter = 0;
			for(String procedure : procedures){
				if(counter==procedures.size()-1){
					procedureIds += procedure;
				}else{
					procedureIds += procedure + "|";	
				}
				counter++;
			}
			query.append(" FILTER regex(?procedureId, \""+procedureIds+"\", \"i\") .");
			
		}
	    if(symptoms!=null && symptoms.size()>0){
			query.append("    ?s bean:symptoms ?symptoms .");
			query.append("    ?symptoms bean:id ?symptomId .");
			String symptomIds = "";
			int counter = 0;
			for(String symptom : symptoms){
				if(counter==procedures.size()-1){
					symptomIds += symptom;
				}else{
					symptomIds += symptom + "|";	
				}
				counter++;
			}
			query.append(" FILTER regex(?symptomId, \""+symptomIds+"\", \"i\") .");
		}
		
	   
	    final String queryString = 
             "PREFIX bean: <http://semrs.googlecode.com/>" +
             "PREFIX user: <http://semrs.googlecode.com/Complication>" +
             "SELECT DISTINCT ?s " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Complication> ." +
                  query.toString() +
             "} "+
             "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
	     
	     
	    final Collection<Complication> result = super.execQuery(Complication.class, queryString, "");
		return result;
	}

	@Override
	public Complication save(final Complication complication) throws SaveOrUpdateException {
		super.save(complication);
		return complication;
	}

	
	@Override
	public Collection<Symptom> getAvailableSymptoms(final String complicationId, final Map params, final String order,
			final String orderBy, final String limit, final String offset){

		Collection<Symptom> result = null; 
		Complication complication = getComplication(complicationId, false);
		final String symptomId = params.get("id").toString();
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
	    
	    if(symptomId!=null && !symptomId.equals("")){
			query.append(" FILTER regex(?id, \""+symptomId+"\", \"i\") .");
		}

		if(complication != null){  
			super.fill(complication, "symptoms");
			Collection<Symptom> symptoms = complication.getSymptoms();
			  if(symptoms!=null && symptoms.size()>0){
					String symptomIds = "";
					int counter = 0;
					for(Symptom symptom : symptoms){
						if(counter==symptoms.size()-1){
							symptomIds += "?id != " + "\""+symptom.getId()+"\"";
						}else{
							symptomIds += "?id != " + "\""+symptom.getId()+"\""+ " && ";	
						}
						counter++;
					}
					
					query.append(" FILTER ("+symptomIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/Symptom> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";;
			     
			     
			    result = super.execQuery(Symptom.class, queryString, "");
		
		
		
		return result;
	}
	
	@Override
	public int getAvailableSymptomsCount(final String complicationId, final Map params){

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

	    Complication complication = getComplication(complicationId,false);
		if(complication != null){  
			super.fill(complication, "symptoms");
			Collection<Symptom> symptoms = complication.getSymptoms();
			  if(symptoms!=null && symptoms.size()>0){
					String symptomIds = "";
					int counter = 0;
					for(Symptom symptom : symptoms){
						if(counter==symptoms.size()-1){
							symptomIds += "?id != " + "\""+symptom.getId()+"\"";
						}else{
							symptomIds += "?id != " + "\""+symptom.getId()+"\""+ " && ";	
						}
						counter++;
					}
					
					query.append(" FILTER ("+symptomIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT (count (distinct *) As ?symptomCount) " +
		             "WHERE { " +
		             "    ?s a <http://semrs.googlecode.com/Symptom> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";
			     
			     
			   final int result = super.count(queryString,"symptomCount");
		
		
		
		return result;
	}

	
	
	@Override
	public Collection<Procedure> getAvailableProcedures(final String complicationId, final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		
		Collection<Procedure> result = null; 
		Complication complication = getComplication(complicationId,false);
		final String procedureId = params.get("id").toString();
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
	    
	    if(procedureId!=null && !procedureId.equals("")){
			query.append(" FILTER regex(?id, \""+procedureId+"\", \"i\") .");
		}

		if(complication != null){  
			super.fill(complication, "complicationProcedures");
			Collection<Procedure> procedures = complication.getComplicationProcedures();
			  if(procedures!=null && procedures.size()>0){
					String procedureIds = "";
					int counter = 0;
					for(Procedure procedure : procedures){
						if(counter==procedures.size()-1){
							procedureIds += "?id != " + "\""+procedure.getId()+"\"";
						}else{
							procedureIds += "?id != " + "\""+procedure.getId()+"\""+ " && ";	
						}
						counter++;
					}
					
					query.append(" FILTER ("+procedureIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/Procedure> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";;
			     
			     
			    result = super.execQuery(Procedure.class, queryString, "");
		
		
		
		return result;
	}

	@Override
	public int getAvailableProceduresCount(String complicationId, Map params) {
	
		final String procedureId = params.get("id").toString();
		final String name =  params.get("name").toString();
		
		final StringBuffer query = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			query.append("    ?s bean:name ?name .");
			query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    
	    if(procedureId!=null && !procedureId.equals("")){
			query.append(" FILTER regex(?id, \""+procedureId+"\", \"i\") .");
		}

	    Complication complication = getComplication(complicationId,false);
		if(complication != null){  
			super.fill(complication, "complicationProcedures");
			Collection<Procedure> procedures = complication.getComplicationProcedures();
			  if(procedures!=null && procedures.size()>0){
					String procedureIds = "";
					int counter = 0;
					for(Procedure procedure : procedures){
						if(counter==procedures.size()-1){
							procedureIds += "?id != " + "\""+procedure.getId()+"\"";
						}else{
							procedureIds += "?id != " + "\""+procedure.getId()+"\""+ " && ";	
						}
						counter++;
					}
					
					query.append(" FILTER ("+procedureIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT (count (distinct *) As ?procedureCount) " +
		             "WHERE { " +
		             "    ?s a <http://semrs.googlecode.com/Procedure> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";
			     
			     
			   final int result = super.count(queryString,"procedureCount");
		
		
		
		return result;
	}
	

}
