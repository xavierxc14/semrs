package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.dao.SymptomDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public class SymptomDAOJenaImpl extends GenericDAOJenaImpl implements SymptomDAO{
	
	private static final Logger LOG = Logger.getLogger(SymptomDAOJenaImpl.class);

	@Override
	public void deleteSymptom(final Symptom symptom) throws DeleteException {
		super.delete(symptom);
		
	}

	@Override
	public Symptom getSymptom(final String id, final boolean deep) {
		final Symptom symptom = super.load(Symptom.class, id, deep);
		return symptom;
	}

	@Override
	public int getSymptomCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Symptom>" +
			"SELECT (count (distinct *) As ?symptomCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Symptom> ." +
			"} ";


		return super.count(queryString, "symptomCount");
	}

	@Override
	public int getSymptomCount(final Map params) {
		final String id = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> drugs = (Collection<String>)params.get("drugs");
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		final Collection<String> relatedSymptoms = (Collection<String>)params.get("symptoms");
		
		final StringBuffer queryBuffer = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			queryBuffer.append("    ?s bean:name ?name .");
			queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(id!=null && !id.equals("")){
			queryBuffer.append("    ?s bean:id ?symptomId .");
			queryBuffer.append(" FILTER regex(?symptomId, \""+id+"\", \"i\") .");
		}
	    if(drugs!=null && drugs.size()>0){
			queryBuffer.append("    ?s bean:drugs ?drugs .");
			queryBuffer.append("    ?drugs bean:id ?drugId .");
			String drugIds = "";
			int counter = 0;
			for(String drug : drugs){
				if(counter==drugs.size()-1){
					drugIds += drug;
				}else{
					drugIds += drug + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?drugId, \""+drugIds+"\", \"i\") .");
		}
	    if(diseases!=null && diseases.size()>0){
			queryBuffer.append("    ?s bean:diseases ?diseases .");
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
	    if(relatedSymptoms!=null && relatedSymptoms.size()>0){
			queryBuffer.append("    ?s bean:symptoms ?symptoms .");
			queryBuffer.append("    ?symptoms bean:id ?symptomId .");
			String symptomIds = "";
			int counter = 0;
			for(String symptom : relatedSymptoms){
				if(counter==relatedSymptoms.size()-1){
					symptomIds += symptom;
				}else{
					symptomIds += symptom + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?symptomId, \""+symptomIds+"\", \"i\") .");
		}
		
		
		
		
		final  String queryString = 
             "PREFIX bean: <http://semrs.googlecode.com/>" +
             "PREFIX user: <http://semrs.googlecode.com/Symptom>" +
             "SELECT (count (distinct *) As ?symptomCount) " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Symptom> ." +
                   queryBuffer.toString() +
             "} ";

		
			return super.count(queryString, "symptomCount");
	}

	@Override
	public Collection<Symptom> listSymptoms() {
		return (Collection<Symptom>) super.load(Symptom.class);
	}

	@Override
	public Collection<Symptom> listSymptoms(final String order, final String orderBy,
			final String limit, final String offset) {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Symptom>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Symptom> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


		final Collection<Symptom> result = super.execQuery(Symptom.class, queryString,"");
		return result;
	}

	@Override
	public Collection<Symptom> listSymptomsByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		final String id = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> drugs = (Collection<String>)params.get("drugs");
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		final Collection<String> relatedSymptoms = (Collection<String>)params.get("symptoms");
		
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
	    if(id!=null && !id.equals("")){
			query.append("    ?s bean:id ?symptomId .");
			query.append(" FILTER regex(?symptomId, \""+id+"\", \"i\") .");
		}
	    if(drugs!=null && drugs.size()>0){
			query.append("    ?s bean:drugs ?drugs .");
			query.append("    ?drugs bean:id ?drugId .");
			String drugIds = "";
			int counter = 0;
			for(String drug : drugs){
				if(counter==drugs.size()-1){
					drugIds += drug;
				}else{
					drugIds += drug + "|";	
				}
				counter++;
			}
			query.append(" FILTER regex(?drugId, \""+drugIds+"\", \"i\") .");
		}
	    if(diseases!=null && diseases.size()>0){
			query.append("    ?s bean:diseases ?diseases .");
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
	    if(relatedSymptoms!=null && relatedSymptoms.size()>0){
			query.append("    ?s bean:symptoms ?symptoms .");
			query.append("    ?symptoms bean:id ?symptomId .");
			String symptomIds = "";
			int counter = 0;
			for(String symptom : relatedSymptoms){
				if(counter==relatedSymptoms.size()-1){
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
             "PREFIX user: <http://semrs.googlecode.com/Symptom>" +
             "SELECT DISTINCT ?s " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Symptom> ." +
                  query.toString() +
             "} "+
             "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
	     
	     
	    final Collection<Symptom> result = super.execQuery(Symptom.class, queryString, "");
		return result;
	}

	@Override
	public Symptom save(final Symptom symptom) throws SaveOrUpdateException {
		super.save(symptom);
		return symptom;
	}
	
	@Override
	public Collection<Symptom> getAvailableSymptoms(final String id, final Map params, final String order,
			final String orderBy, final String limit, final String offset){

		Collection<Symptom> result = null; 
		Symptom symptom = getSymptom(id, false);
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

		if(symptom != null){  
			super.fill(symptom, "symptoms");
			Collection<Symptom> symptoms = symptom.getSymptoms();
			symptoms.add(symptom);
			  if(symptoms!=null && symptoms.size()>0){
					String symptomIds = "";
					int counter = 0;
					for(Symptom sym : symptoms){
						if(counter==symptoms.size()-1){
							symptomIds += "?id != " + "\""+sym.getId()+"\"";
						}else{
							symptomIds += "?id != " + "\""+sym.getId()+"\""+ " && ";	
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
	public int getAvailableSymptomsCount(final String id, final Map params){

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

	    Symptom symptom = getSymptom(id, false);
		if(symptom != null){  
			super.fill(symptom, "symptoms");
			Collection<Symptom> symptoms = symptom.getSymptoms();
			symptoms.add(symptom);
			  if(symptoms!=null && symptoms.size()>0){
					String symptomIds = "";
					int counter = 0;
					for(Symptom sym : symptoms){
						if(counter==symptoms.size()-1){
							symptomIds += "?id != " + "\""+sym.getId()+"\"";
						}else{
							symptomIds += "?id != " + "\""+sym.getId()+"\""+ " && ";	
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
	public Collection<Drug> getAvailableDrugs(final String id, final Map params, final String order,
			final String orderBy, final String limit, final String offset){

		Collection<Drug> result = null; 
		Symptom symptom = getSymptom(id, false);
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

		if(symptom != null){  
			super.fill(symptom, "drugs");
			Collection<Drug> drugs = symptom.getDrugs();
			  if(drugs!=null && drugs.size()>0){
					String drugIds = "";
					int counter = 0;
					for(Drug drug : drugs){
						if(counter==drugs.size()-1){
							drugIds += "?id != " + "\""+drug.getId()+"\"";
						}else{
							drugIds += "?id != " + "\""+drug.getId()+"\""+ " && ";	
						}
						counter++;
					}
				
					query.append(" FILTER ("+drugIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/Drug> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";;
			     
			     
			    result = super.execQuery(Drug.class, queryString, "");
		
		
		
		return result;
	}
	
	@Override
	public int getAvailableDrugsCount(final String id, final Map params){

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

	    Symptom symptom = getSymptom(id, false);
		if(symptom != null){  
			super.fill(symptom, "drugs");
			Collection<Drug> drugs = symptom.getDrugs();
			  if(drugs!=null && drugs.size()>0){
					String drugIds = "";
					int counter = 0;
					for(Drug drug : drugs){
						if(counter==drugs.size()-1){
							drugIds += "?id != " + "\""+drug.getId()+"\"";
						}else{
							drugIds += "?id != " + "\""+drug.getId()+"\""+ " && ";	
						}
						counter++;
					}
					
					query.append(" FILTER ("+drugIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT (count (distinct *) As ?drugCount) " +
		             "WHERE { " +
		             "    ?s a <http://semrs.googlecode.com/Drug> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";
			     
			     
			   final int result = super.count(queryString,"drugCount");
		
		
		
		return result;
	}





		

}
