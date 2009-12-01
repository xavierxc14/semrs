package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.dao.DiseaseDAO;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public class DiseaseDAOJenaImpl extends GenericDAOJenaImpl  implements DiseaseDAO{
	
	private static final Logger LOG = Logger.getLogger(DiseaseDAOJenaImpl.class);
	
	@Override
	public void deleteDisease(final Disease disease) throws DeleteException {
		super.delete(disease);
	
		
	}

	@Override
	public Disease getDisease(final String id, final boolean deep) {
		final Disease disease = super.load(Disease.class, id, deep);
		return disease;
	}

	@Override
	public int getDiseaseCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Disease>" +
			"SELECT (count (distinct *) As ?diseaseCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Disease> ." +
			"} ";


		return super.count(queryString, "diseaseCount" );
	}

	@Override
	public int getDiseaseCount(final Map params) {
		final String id = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> symptoms = (Collection<String>)params.get("symptoms");
		final Collection<String> labTests = (Collection<String>)params.get("labTests");
		final Collection<String> procedures = (Collection<String>)params.get("procedures");
		
		final StringBuffer queryBuffer = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			queryBuffer.append("    ?s bean:name ?name .");
			queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(id!=null && !id.equals("")){
			queryBuffer.append("    ?s bean:id ?diseaseId .");
			queryBuffer.append(" FILTER regex(?diseaseId, \""+id+"\", \"i\") .");
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
	    if(labTests!=null && labTests.size()>0){
			queryBuffer.append("    ?s bean:labTests ?labTests .");
			queryBuffer.append("    ?labTests bean:id ?labTestId .");
			String labTestIds = "";
			int counter = 0;
			for(String labTest : labTests){
				if(counter==symptoms.size()-1){
					labTestIds += labTest;
				}else{
					labTestIds += labTest + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?labTestId, \""+labTestIds+"\", \"i\") .");
			
		}
	    if(procedures!=null && procedures.size()>0){
			queryBuffer.append("    ?s bean:procedures ?procedures .");
			queryBuffer.append("    ?procedures bean:id ?procedureId .");
			String procedureIds = "";
			int counter = 0;
			for(String procedure : procedures){
				if(counter==symptoms.size()-1){
					procedureIds += procedure;
				}else{
					procedureIds += procedure + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?procedureId, \""+procedureIds+"\", \"i\") .");
		}
		
	   
	    final String queryString = 
            "PREFIX bean: <http://semrs.googlecode.com/>" +
            "PREFIX user: <http://semrs.googlecode.com/Disease>" +
            "SELECT (count (distinct *) As ?diseaseCount) " +
            "WHERE { " +
            "    ?s a  <http://semrs.googlecode.com/Disease> ." +
                  queryBuffer.toString() +
            "} ";

	
			return super.count(queryString, "diseaseCount" );
	}

	@Override
	public Collection<Disease> listDiseases() {
		return (Collection<Disease>) super.load(Disease.class);
	}

	@Override
	public Collection<Disease> listDiseases(final String order, final String orderBy,
			final String limit,final String offset) {
		
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Disease>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Disease> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


		
		final Collection<Disease> result = super.execQuery(Disease.class, queryString, "");
		return result;
	}

	@Override
	public Collection<Disease> listDiseasesByQuery(final Map params,final  String order,
			final String orderBy, final String limit, final String offset) {
		
		final String id = params.get("id").toString();
		final String name =  params.get("name").toString();
		final Collection<String> symptoms = (Collection<String>)params.get("symptoms");
		final Collection<String> labTests = (Collection<String>)params.get("labTests");
		final Collection<String> procedures = (Collection<String>)params.get("procedures");
		
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
			query.append("    ?s bean:id ?diseaseId .");
			query.append(" FILTER regex(?diseaseId, \""+id+"\", \"i\") .");
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
	    if(labTests!=null && labTests.size()>0){
			query.append("    ?s bean:labTests ?labTests .");
			query.append("    ?labTests bean:id ?labTestId .");
			String labTestIds = "";
			int counter = 0;
			for(String labTest : labTests){
				if(counter==symptoms.size()-1){
					labTestIds += labTest;
				}else{
					labTestIds += labTest + "|";	
				}
				counter++;
			}
			query.append(" FILTER regex(?labTestId, \""+labTestIds+"\", \"i\") .");
		}
	    if(procedures!=null && procedures.size()>0){
			query.append("    ?s bean:procedures ?procedures .");
			query.append("    ?procedures bean:id ?procedureId .");
			String procedureIds = "";
			int counter = 0;
			for(String procedure : procedures){
				if(counter==symptoms.size()-1){
					procedureIds += procedure;
				}else{
					procedureIds += procedure + "|";	
				}
				counter++;
			}
			query.append(" FILTER regex(?procedureId, \""+procedureIds+"\", \"i\") .");
		}
		
	   
	    final String queryString = 
             "PREFIX bean: <http://semrs.googlecode.com/>" +
             "PREFIX user: <http://semrs.googlecode.com/Disease>" +
             "SELECT DISTINCT ?s " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Disease> ." +
                  query.toString() +
             "} "+
             "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
	     
	     
	  
	    final Collection<Disease> result = super.execQuery(Disease.class, queryString, "");
		return result;
		
	}

	@Override
	public Disease save(final Disease disease) throws SaveOrUpdateException {
		
		/*
		TypeWrapper diseaseType = TypeWrapper.wrap(Disease.class);
		OntModel model = (OntModel)genericDAO.getModel();
        OntProperty pDiseases =  model.getOntProperty( diseaseType.namespace() + "relatedDiseases" );
        OntProperty pDiseasesP =  model.getOntProperty( diseaseType.namespace() + "relatedDiseasesP" );
        OntProperty pProcedures =  model.getOntProperty( diseaseType.namespace() + "procedures" );
        OntProperty plabTests =  model.getOntProperty( diseaseType.namespace() + "labTests" );
        
        if(pProcedures!=null && plabTests!=null){
        	if(pDiseases!=null){
        		pDiseases.setInverseOf(plabTests);	
        	}
        	if(pDiseasesP!=null){
        		pDiseasesP.setInverseOf(pProcedures);
        	}
        }
		*/
		
		super.save(disease);
		return disease;
		
	}


	@Override
	public Collection<Symptom> getAvailableSymptoms(final String diseaseId, final Map params, final String order,
			final String orderBy, final String limit, final String offset){

		Collection<Symptom> result = null; 
		Disease disease = getDisease(diseaseId,false);
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

		if(disease != null){  
			super.fill(disease, "symptoms");
			Collection<Symptom> symptoms = disease.getSymptoms();
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
	public int getAvailableSymptomsCount(final String diseaseId, final Map params){

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

	    Disease disease = getDisease(diseaseId,false);
		if(disease != null){  
			super.fill(disease, "symptoms");
			Collection<Symptom> symptoms = disease.getSymptoms();
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
	public Collection<Disease> getAvailableDiseases(final String diseaseId, final Map params, final String order,
			final String orderBy, final String limit, final String offset){

		Collection<Disease> result = null; 
		Disease disease = getDisease(diseaseId,false);
		final String disId = params.get("id").toString();
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
	    
	    if(disId!=null && !disId.equals("")){
			query.append(" FILTER regex(?id, \""+disId+"\", \"i\") .");
		}

		if(disease != null){  
			super.fill(disease, "relDiseases");
			Collection<Disease> diseases = disease.getRelDiseases();
			diseases.add(disease);
			  if(diseases!=null && diseases.size()>0){
					String diseaseIds = "";
					int counter = 0;
					for(Disease dis: diseases){
						if(counter==diseases.size()-1){
							diseaseIds += "?id != " + "\""+dis.getId()+"\"";
						}else{
							diseaseIds += "?id != " + "\""+dis.getId()+"\""+ " && ";	
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
	public int getAvailableDiseasesCount(final String diseaseId, final Map params){

		final String disId = params.get("id").toString();
		final String name =  params.get("name").toString();
		
		final StringBuffer query = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			query.append("    ?s bean:name ?name .");
			query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    
	    if(disId!=null && !disId.equals("")){
			query.append(" FILTER regex(?id, \""+disId+"\", \"i\") .");
		}

	    Disease disease = getDisease(diseaseId,false);
		if(disease != null){  
			super.fill(disease, "relDiseases");
			Collection<Disease> diseases = disease.getRelDiseases();
			diseases.add(disease);
			  if(diseases!=null && diseases.size()>0){
					String diseaseIds = "";
					int counter = 0;
					for(Disease dis : diseases){
						if(counter==diseases.size()-1){
							diseaseIds += "?id != " + "\""+dis.getId()+"\"";
						}else{
							diseaseIds += "?id != " + "\""+dis.getId()+"\""+ " && ";	
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
	
	@Override
	public Collection<Procedure> getAvailableProcedures(final String diseaseId, final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		
		Collection<Procedure> result = null; 
		Disease disease = getDisease(diseaseId,false);
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

		if(disease != null){  
			super.fill(disease, "procedures");
			Collection<Procedure> procedures = disease.getProcedures();
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
	public int getAvailableProceduresCount(String diseaseId, Map params) {
	
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

	    Disease disease = getDisease(diseaseId,false);
		if(disease != null){  
			super.fill(disease, "procedures");
			Collection<Procedure> procedures = disease.getProcedures();
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
	
	
	@Override
	public Collection<LabTest> getAvailableLabTests(final String diseaseId, final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		
		Collection<LabTest> result = null; 
		Disease disease = getDisease(diseaseId,false);
		final String labTestId = params.get("id").toString();
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
	    
	    if(labTestId!=null && !labTestId.equals("")){
			query.append(" FILTER regex(?id, \""+labTestId+"\", \"i\") .");
		}

		if(disease != null){  
			super.fill(disease, "labTests");
			Collection<LabTest> labTests = disease.getLabTests();
			  if(labTests!=null && labTests.size()>0){
					String labTestIds = "";
					int counter = 0;
					for(LabTest labTest : labTests){
						if(counter==labTests.size()-1){
							labTestIds += "?id != " + "\""+labTest.getId()+"\"";
						}else{
							labTestIds += "?id != " + "\""+labTest.getId()+"\""+ " && ";	
						}
						counter++;
					}
					
					query.append(" FILTER ("+labTestIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/LabTest> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";;
			     
			     
			    result = super.execQuery(LabTest.class, queryString, "");
		
		
		
		return result;
		
	}
	
	@Override
	public int getAvailableLabTestsCount(String diseaseId, Map params){
		
		final String labTestId = params.get("id").toString();
		final String name =  params.get("name").toString();
		
		final StringBuffer query = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
			query.append("    ?s bean:name ?name .");
			query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    
	    if(labTestId!=null && !labTestId.equals("")){
			query.append(" FILTER regex(?id, \""+labTestId+"\", \"i\") .");
		}

	    Disease disease = getDisease(diseaseId,false);

		if(disease != null){  
			super.fill(disease, "labTests");
			Collection<LabTest> labTests = disease.getLabTests();
			  if(labTests!=null && labTests.size()>0){
					String labTestIds = "";
					int counter = 0;
					for(LabTest labTest : labTests){
						if(counter==labTests.size()-1){
							labTestIds += "?id != " + "\""+labTest.getId()+"\"";
						}else{
							labTestIds += "?id != " + "\""+labTest.getId()+"\""+ " && ";	
						}
						counter++;
					}
					
					query.append(" FILTER ("+labTestIds+") .");
				}
		}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT (count (distinct *) As ?labTestCount) " +
		             "WHERE { " +
		             "    ?s a <http://semrs.googlecode.com/LabTest> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";
			     
			     
			   final int result = super.count(queryString,"labTestCount");
		
		
		
		return result;
		
	}
	


}
