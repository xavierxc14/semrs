package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.dao.DrugDAO;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public class DrugDAOJenaImpl extends GenericDAOJenaImpl  implements DrugDAO {
	
	private static final Logger LOG = Logger.getLogger(DrugDAOJenaImpl.class);
	

	@Override
	public void deleteDrug(final Drug drug) throws DeleteException {
		super.delete(drug);

	}

	@Override
	public Drug getDrug(final String id, final boolean deep) {
		final Drug drug = super.load(Drug.class, id, deep);
		return drug;
	}

	@Override
	public int getDrugCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Drug>" +
			"SELECT (count (distinct *) As ?drugCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Drug> ." +
			"} ";


		return super.count(queryString, "drugCount");
	}

	@Override
	public int getDrugCount(final Map params) {
		final String drugId = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> symptoms = (Collection<String>)params.get("symptoms");
		
		final StringBuffer queryBuffer = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			queryBuffer.append("    ?s bean:name ?name .");
			queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(drugId!=null && !drugId.equals("")){
			queryBuffer.append("    ?s bean:id ?drugId .");
			queryBuffer.append(" FILTER regex(?drugId, \""+drugId+"\", \"i\") .");
		}
	    if(symptoms!=null && symptoms.size()>0){
			queryBuffer.append("    ?s bean:symptoms ?symptoms .");
			queryBuffer.append("    ?symptoms bean:id ?symptomId .");
			String symptomIds = "";
			int counter = 0;
			for(String symptom : symptoms){
				if(counter==symptoms.size()-1){
					symptomIds += symptom;
				}else{
					symptomIds += symptom + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?symptomId, \""+symptomIds+"\", \"i\") .");
		}
		

	    final String queryString = 
            "PREFIX bean: <http://semrs.googlecode.com/>" +
            "PREFIX user: <http://semrs.googlecode.com/Drug>" +
            "SELECT (count (distinct *) As ?drugCount) " +
            "WHERE { " +
            "    ?s a  <http://semrs.googlecode.com/Drug> ." +
                 queryBuffer.toString() +
            "} ";


		 return super.count(queryString, "drugCount");
	}

	@Override
	public Collection<Drug> listDrugs() {
		return (Collection<Drug>) super.load(Drug.class);
	}

	@Override
	public Collection<Drug> listDrugs(final String order, final String orderBy,
			final String limit, final String offset) {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Drug>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Drug> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


		
		final Collection<Drug> result = super.execQuery(Drug.class, queryString, "");
		return result;
	}

	@Override
	public Collection<Drug> listDrugsByQuery(final Map params, final String order,
			final String orderBy, final String limit,final String offset) {
		
		final String drugId = params.get("id").toString();
		final String name =  params.get("name").toString();
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
	    if(drugId!=null && !drugId.equals("")){
			query.append("    ?s bean:id ?drugId .");
			query.append(" FILTER regex(?drugId, \""+drugId+"\", \"i\") .");
		}
	    if(symptoms!=null && symptoms.size()>0){
			query.append("    ?s bean:symptoms ?symptoms .");
			query.append("    ?symptoms bean:id ?symptomId .");
			String symptomIds = "";
			int counter = 0;
			for(String symptom : symptoms){
				if(counter==symptoms.size()-1){
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
             "PREFIX user: <http://semrs.googlecode.com/Drug>" +
             "SELECT DISTINCT ?s " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Drug> ." +
                  query.toString() +
             "} "+
             "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
	     
	     
	    
	    final Collection<Drug> result = super.execQuery(Drug.class, queryString, "");
		 return result;
	}

	@Override
	public Drug save(final Drug drug) throws SaveOrUpdateException {
		super.save(drug);
		return drug;
	}


	
	@Override
	public Collection<Symptom> getAvailableSymptoms(final String drugId, final Map params, final String order,
			final String orderBy, final String limit, final String offset){

		Collection<Symptom> result = null; 
		Drug drug = getDrug(drugId,false);
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

		if(drug != null){  
			super.fill(drug, "symptoms");
			Collection<Symptom> symptoms = drug.getSymptoms();
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
	public int getAvailableSymptomsCount(final String drugId, final Map params){

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

	    Drug drug = getDrug(drugId,false);
		if(drug != null){  
			super.fill(drug, "symptoms");
			Collection<Symptom> symptoms = drug.getSymptoms();
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

}
