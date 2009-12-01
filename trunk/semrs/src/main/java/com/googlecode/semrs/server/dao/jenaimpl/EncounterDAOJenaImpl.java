package com.googlecode.semrs.server.dao.jenaimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.googlecode.semrs.model.Complication;
import com.googlecode.semrs.model.ComplicationRecord;
import com.googlecode.semrs.model.Diagnosis;
import com.googlecode.semrs.model.DiagnosisContainer;
import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.Encounter;
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.model.LabTestRecord;
import com.googlecode.semrs.model.Patient;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.ProcedureRecord;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.model.SymptomRecord;
import com.googlecode.semrs.model.TreatmentRecord;
import com.googlecode.semrs.server.dao.EncounterDAO;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.util.Util;

public class EncounterDAOJenaImpl extends GenericDAOJenaImpl  implements EncounterDAO  {
	
	private static final Logger LOG = Logger.getLogger(EncounterDAOJenaImpl.class);
		
	private Double ageWeight; 
	
	private Double sexWeight; 
	
	private Double toxicHabitsWeight; 
	
	private Double symptomsWeight; 
	
	private Double labTestsWeight; 
	
	private Double relDiseasesWeight; 
	
	private Double proceduresWeight;
	
	private Double severityLowWeight;
	
	private Double severityMediumWeight;
	
	private Double severityHighWeight;
	
	private Double severityVeryHighWeight;
	
	private boolean addWeightsByDefault;

	@Override
	public void deleteEncounter(final Encounter encounter) throws DeleteException {
		super.delete(encounter);
	
	}

	@Override
	public synchronized Encounter getEncounter(final String id, final boolean deep) {
		final Encounter encounter = super.load(Encounter.class, String.valueOf(id), deep);
		return encounter;
	}

	@Override
	public int getEncounterCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX encounter: <http://semrs.googlecode.com/Encounter>" +
			"SELECT (count (distinct *) As ?encounterCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Encounter> ." +
			"} ";

		return super.count(queryString, "encounterCount");
	}

	@Override
	public int getEncounterCount(final Map params) {
		
		final String id = params.get("id") == null? "" : params.get("id").toString();
		final String patientId =  params.get("patientId") == null? "" : params.get("patientId").toString();
		final String encounterProvider =  params.get("encounterProvider") == null? "" : params.get("encounterProvider").toString();
		final String encounterDate = params.get("encounterDate") == null? "" : params.get("encounterDate").toString();
		final String encounterDateFrom = params.get("encounterDateFrom") == null? "" : params.get("encounterDateFrom").toString();
		final String encounterDateTo = params.get("encounterDateTo") == null? "" : params.get("encounterDateTo").toString();
		final String endDateFrom = params.get("endDateFrom") == null? "" : params.get("endDateFrom").toString();
		final String endDateTo = params.get("endDateTo") == null? "" : params.get("endDateTo").toString();
		final String creationDateFrom = params.get("creationDateFrom") == null? "" : params.get("creationDateFrom").toString();
		final String creationDateTo = params.get("creationDateTo") == null? "" : params.get("creationDateTo").toString();
		final String endDate = params.get("endDate") == null? "" : params.get("endDate").toString();
		final String current =  params.get("current") == null? "" : params.get("current").toString();
		final String finalized =  params.get("finalized") == null? "" : params.get("finalized").toString();
		final String daySearch =  params.get("daySearch") == null? "" : params.get("daySearch").toString();
		final Collection<String> drugs = (Collection<String>)params.get("drugs");
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		final Collection<String> symptoms = (Collection<String>)params.get("symptoms");
		final Collection<String> labTests = (Collection<String>)params.get("labTests");
		final Collection<String> complications = (Collection<String>)params.get("complications");
		final Collection<String> procedures = (Collection<String>)params.get("procedures");
		
		
		
		final StringBuffer queryBuffer = new StringBuffer();
		
	    if(id!=null && !id.equals("")){
			queryBuffer.append("    ?s bean:id ?id .");
			queryBuffer.append(" FILTER regex(?id, \""+id+"\", \"i\") .");
		}
	    if(patientId!=null && !patientId.equals("")){
			queryBuffer.append("    ?s bean:patientId ?patientId .");
			queryBuffer.append(" FILTER regex(?patientId, \""+patientId+"\", \"i\") .");
		}
	    if(encounterProvider!=null && !encounterProvider.equals("")){
			queryBuffer.append("    ?s bean:encounterProvider ?encounterProvider .");
			queryBuffer.append(" FILTER regex(?encounterProvider, \""+encounterProvider+"\", \"i\") .");
		}
	    if((daySearch.equals("")) && (encounterDate!=null && !encounterDate.equals(""))){
			queryBuffer.append("    ?s bean:encounterDate ?encounterDate .");
			queryBuffer.append(" FILTER (?encounterDate = \""+encounterDate+"\"^^xsd:dateTime) .");
		}
	    if(endDate!=null && !endDate.equals("")){
			queryBuffer.append("    ?s bean:endDate ?endDate .");
			queryBuffer.append(" FILTER (?endDate = \""+endDate+"\"^^xsd:dateTime) .");
		}
		if((creationDateFrom!=null && !creationDateFrom.equals("")) && (creationDateTo!=null && !creationDateTo.equals(""))){
			queryBuffer.append("    ?s bean:creationDate ?creationDate .");
			queryBuffer.append(" FILTER (?creationDate >= \""+Util.toXSDString(creationDateFrom)+"\"^^xsd:dateTime && ?creationDate < \""+Util.toXSDString(Util.addStringDate("D", 1,creationDateTo))+"\"^^xsd:dateTime) .");	
		}	   
		if((encounterDateFrom!=null && !encounterDateFrom.equals("")) && (encounterDateTo!=null && !encounterDateTo.equals(""))){
			queryBuffer.append("    ?s bean:encounterDate ?encounterDate .");
			queryBuffer.append(" FILTER (?encounterDate >= \""+Util.toXSDString(encounterDateFrom)+"\"^^xsd:dateTime && ?encounterDate < \""+Util.toXSDString(Util.addStringDate("D", 1,encounterDateTo))+"\"^^xsd:dateTime) .");	
		}	  
		if((endDateFrom!=null && !endDateFrom.equals("")) && (endDateTo!=null && !endDateTo.equals(""))){
			queryBuffer.append("    ?s bean:endDate ?endDate .");
			queryBuffer.append(" FILTER (?endDate >= \""+Util.toXSDString(endDateFrom)+"\"^^xsd:dateTime && ?endDate < \""+Util.toXSDString(Util.addStringDate("D", 1,endDateTo))+"\"^^xsd:dateTime) .");	
		}	  
	    if((daySearch!=null && !daySearch.equals("")) && (encounterDate!=null && !encounterDate.equals(""))){
	    	queryBuffer.append("    ?s bean:encounterDate ?encounterDate .");
	    	queryBuffer.append(" FILTER (?encounterDate >= \""+Util.toXSDString(encounterDate)+"\"^^xsd:dateTime && ?encounterDate < \""+Util.toXSDString(Util.addStringDate("D", 1,encounterDate))+"\"^^xsd:dateTime) .");	
		}
	    if(current!=null && !current.equals("")){
			queryBuffer.append("    ?s bean:current ?current .");
			queryBuffer.append(" FILTER (?current = "+current+") .");
		}
	    if(finalized!=null && !finalized.equals("")){
			queryBuffer.append("    ?s bean:finalized ?finalized .");
			queryBuffer.append(" FILTER (?finalized = "+finalized+") .");
		}
	    
	    if(drugs!=null && drugs.size()>0){
			queryBuffer.append("    ?s bean:treatmentRecords ?treatmentRecords .");
			queryBuffer.append("    ?treatmentRecords bean:drugId ?drugId .");
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
			queryBuffer.append("    ?s bean:symptomRecords ?symptomRecords .");
			queryBuffer.append("    ?symptomRecords bean:diseaseId ?diseaseId .");
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
	    
	    if(symptoms!=null && symptoms.size()>0){
			queryBuffer.append("    ?s bean:symptomRecords ?symptomRecords .");
			queryBuffer.append("    ?symptomRecords bean:symptomId ?symptomId .");
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
			queryBuffer.append("    ?s bean:labTestRecords ?labTestRecords .");
			queryBuffer.append("    ?labTestRecords bean:labTestId ?labTestId .");
			String labTestIds = "";
			int counter = 0;
			for(String labTest : labTests){
				if(counter==labTests.size()-1){
					labTestIds += labTest;
				}else{
					labTestIds += labTest + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?labTestId, \""+labTestIds+"\", \"i\") .");
		}
	    
	    if(complications!=null && complications.size()>0){
			queryBuffer.append("    ?s bean:complicationRecords ?complicationRecords .");
			queryBuffer.append("    ?complicationRecords bean:complicationId ?complicationId .");
			String complicationIds = "";
			int counter = 0;
			for(String complication : complications){
				if(counter==complications.size()-1){
					complicationIds += complication;
				}else{
					complicationIds += complication + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?complicationId, \""+complicationIds+"\", \"i\") .");
		}
	    
	    if(procedures!=null && procedures.size()>0){
			queryBuffer.append("    ?s bean:procedureRecords ?procedureRecords .");
			queryBuffer.append("    ?procedureRecords bean:procedureId ?procedureId .");
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
		
		
	    final String queryString = 
			 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "+
             "PREFIX bean: <http://semrs.googlecode.com/>" +
             "PREFIX encounter: <http://semrs.googlecode.com/Encounter>" +
             "SELECT (count (distinct *) As ?encounterCount) " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Encounter> ." +
                   queryBuffer.toString() +
             "} ";

		 return super.count(queryString, "encounterCount");
		
		
	}

	@Override
	public Collection<Encounter> listEncounters() {
		return (Collection<Encounter>) super.load(Encounter.class);
	}

	@Override
	public Collection<Encounter> listEncounters(final String order, final String orderBy,
			final String limit, final String offset) {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX encounter: <http://semrs.googlecode.com/Encounter>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Encounter> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;

 
		final Collection<Encounter> result = super.execQuery(Encounter.class, queryString, "");
		return result;
	}

	@Override
	public Collection<Encounter> listEncountersByQuery(final Map params,
			final String order, final String orderBy, final String limit, final String offset) {
		
		final String id = params.get("id") == null? "" : params.get("id").toString();
		final String patientId =  params.get("patientId") == null? "" : params.get("patientId").toString();
		final String encounterProvider =  params.get("encounterProvider") == null? "" : params.get("encounterProvider").toString();
		final String encounterDate = params.get("encounterDate") == null? "" : params.get("encounterDate").toString();
		final String encounterDateFrom = params.get("encounterDateFrom") == null? "" : params.get("encounterDateFrom").toString();
		final String encounterDateTo = params.get("encounterDateTo") == null? "" : params.get("encounterDateTo").toString();
		final String endDateFrom = params.get("endDateFrom") == null? "" : params.get("endDateFrom").toString();
		final String endDateTo = params.get("endDateTo") == null? "" : params.get("endDateTo").toString();
		final String creationDateFrom = params.get("creationDateFrom") == null? "" : params.get("creationDateFrom").toString();
		final String creationDateTo = params.get("creationDateTo") == null? "" : params.get("creationDateTo").toString();
		final String endDate = params.get("endDate") == null? "" : params.get("endDate").toString();
		final String current =  params.get("current") == null? "" : params.get("current").toString();
		final String finalized =  params.get("finalized") == null? "" : params.get("finalized").toString();
		final String daySearch =  params.get("daySearch") == null? "" : params.get("daySearch").toString();
		final String beforeDate =  params.get("beforeDate") == null? "" : params.get("beforeDate").toString();
		final Collection<String> drugs = (Collection<String>)params.get("drugs");
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		final Collection<String> symptoms = (Collection<String>)params.get("symptoms");
		final Collection<String> labTests = (Collection<String>)params.get("labTests");
		final Collection<String> complications = (Collection<String>)params.get("complications");
		final Collection<String> procedures = (Collection<String>)params.get("procedures");
		
		
		final StringBuffer query = new StringBuffer();
		if(!orderBy.equals("")){
			query.append(" OPTIONAL { ");
			query.append("    ?s bean:"+orderBy+" ?"+orderBy+" .");
			query.append( "} .");
			
		}
		
		   if(id!=null && !id.equals("")){
			   query.append("    ?s bean:id ?id .");
			   query.append(" FILTER regex(?id, \""+id+"\", \"i\") .");
			}
		    if(patientId!=null && !patientId.equals("")){
		    	query.append("    ?s bean:patientId ?patientId .");
		    	query.append(" FILTER regex(?patientId, \""+patientId+"\", \"i\") .");
			}
		    if(encounterProvider!=null && !encounterProvider.equals("")){
		    	query.append("    ?s bean:encounterProvider ?encounterProvider .");
		    	query.append(" FILTER regex(?encounterProvider, \""+encounterProvider+"\", \"i\") .");
			}
		    if((daySearch.equals("")) && (encounterDate!=null && !encounterDate.equals(""))){
		    	query.append("    ?s bean:encounterDate ?encounterDate .");
		    	query.append(" FILTER (?encounterDate = \""+encounterDate+"\"^^xsd:dateTime) .");
			}
		    if(endDate!=null && !endDate.equals("")){
		    	query.append("    ?s bean:endDate ?endDate .");
		    	query.append(" FILTER (?endDate = \""+endDate+"\"^^xsd:dateTime) .");
			}  
			if((creationDateFrom!=null && !creationDateFrom.equals("")) && (creationDateTo!=null && !creationDateTo.equals(""))){
				query.append("    ?s bean:creationDate ?creationDate .");
				query.append(" FILTER (?creationDate >= \""+Util.toXSDString(creationDateFrom)+"\"^^xsd:dateTime && ?creationDate < \""+Util.toXSDString(Util.addStringDate("D", 1,creationDateTo))+"\"^^xsd:dateTime) .");	
			}	   
			if((encounterDateFrom!=null && !encounterDateFrom.equals("")) && (encounterDateTo!=null && !encounterDateTo.equals(""))){
				query.append("    ?s bean:encounterDate ?encounterDate .");
				query.append(" FILTER (?encounterDate >= \""+Util.toXSDString(encounterDateFrom)+"\"^^xsd:dateTime && ?encounterDate < \""+Util.toXSDString(Util.addStringDate("D", 1,encounterDateTo))+"\"^^xsd:dateTime) .");	
			}	  
			if((endDateFrom!=null && !endDateFrom.equals("")) && (endDateTo!=null && !endDateTo.equals(""))){
				query.append("    ?s bean:endDate ?endDate .");
				query.append(" FILTER (?endDate >= \""+Util.toXSDString(endDateFrom)+"\"^^xsd:dateTime && ?endDate < \""+Util.toXSDString(Util.addStringDate("D", 1,endDateTo))+"\"^^xsd:dateTime) .");	
			}	  
		    if((daySearch!=null && !daySearch.equals("")) && (encounterDate!=null && !encounterDate.equals(""))){
		    	query.append("    ?s bean:encounterDate ?encounterDate .");
		    	query.append(" FILTER (?encounterDate >= \""+Util.toXSDString(encounterDate)+"\"^^xsd:dateTime && ?encounterDate < \""+Util.toXSDString(Util.addStringDate("D", 1,encounterDate))+"\"^^xsd:dateTime) .");	
			}
		    if(beforeDate!=null && !beforeDate.equals("")){
		    	query.append("    ?s bean:encounterDate ?encounterDate .");
		    	query.append("    ?s bean:creationDate ?creationDate .");
		    	query.append(" FILTER (?encounterDate < \""+Util.toXSDString(beforeDate)+"\"^^xsd:dateTime && ?creationDate < \""+Util.toXSDString(beforeDate)+"\"^^xsd:dateTime ) .");	
		    }
		    if(current!=null && !current.equals("")){
		    	query.append("    ?s bean:current ?current .");
		    	query.append(" FILTER (?current = "+current+") .");
			}
		    if(finalized!=null && !finalized.equals("")){
		    	query.append("    ?s bean:finalized ?finalized .");
		    	query.append(" FILTER (?finalized = "+finalized+") .");
			}
		    if(drugs!=null && drugs.size()>0){
		    	query.append("    ?s bean:treatmentRecords ?treatmentRecords .");
		    	query.append("    ?treatmentRecords bean:drugId ?drugId .");
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
		    	query.append("    ?s bean:symptomRecords ?symptomRecords .");
		    	query.append("    ?symptomRecords bean:diseaseId ?diseaseId .");
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
		    
		    if(symptoms!=null && symptoms.size()>0){
		    	query.append("    ?s bean:symptomRecords ?symptomRecords .");
		    	query.append("    ?symptomRecords bean:symptomId ?symptomId .");
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
		    	query.append("    ?s bean:labTestRecords ?labTestRecords .");
		    	query.append("    ?labTestRecords bean:labTestId ?labTestId .");
				String labTestIds = "";
				int counter = 0;
				for(String labTest : labTests){
					if(counter==labTests.size()-1){
						labTestIds += labTest;
					}else{
						labTestIds += labTest + "|";	
					}
					counter++;
				}
				query.append(" FILTER regex(?labTestId, \""+labTestIds+"\", \"i\") .");
			}
		    
		    if(complications!=null && complications.size()>0){
		    	query.append("    ?s bean:complicationRecords ?complicationRecords .");
		    	query.append("    ?complicationRecords bean:complicationId ?complicationId .");
				String complicationIds = "";
				int counter = 0;
				for(String complication : complications){
					if(counter==complications.size()-1){
						complicationIds += complication;
					}else{
						complicationIds += complication + "|";	
					}
					counter++;
				}
				query.append(" FILTER regex(?complicationId, \""+complicationIds+"\", \"i\") .");
			}
		    
		    if(procedures!=null && procedures.size()>0){
		    	query.append("    ?s bean:procedureRecords ?procedureRecords .");
				query.append("    ?procedureRecords bean:procedureId ?procedureId .");
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
		
		String queryString = "";
		if(limit.equals("") && offset.equals("") && orderBy.equals("")){
			
		  queryString = 
			     "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "+
	             "PREFIX bean: <http://semrs.googlecode.com/>" +
	             "PREFIX encounter: <http://semrs.googlecode.com/Encounter>" +
	             "SELECT DISTINCT ?s " +
	             "WHERE { " +
	             "    ?s a  <http://semrs.googlecode.com/Encounter> ." +
	                  query.toString() +
	             "} ";
			
		}else if(limit.equals("") && offset.equals("")){
		
			  queryString = 
				     "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "+
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "PREFIX encounter: <http://semrs.googlecode.com/Encounter>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/Encounter> ." +
		                  query.toString() +
		             "} "+
		             "ORDER BY "+order+"(?"+orderBy+") ";
		
		
		}else{
		
	   
		  queryString = 
			 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "+
             "PREFIX bean: <http://semrs.googlecode.com/>" +
             "PREFIX encounter: <http://semrs.googlecode.com/Encounter>" +
             "SELECT DISTINCT ?s " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Encounter> ." +
                  query.toString() +
             "} "+
             "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
		} 

		final Collection<Encounter> result = super.execQuery(Encounter.class, queryString, "");
		return result;
		
	}
	
	@Override
	public Collection<DiagnosisContainer> getAssistedDiagnosis(final Map params){
		
		String patientAge =  params.get("patientAge") == null? "" : params.get("patientAge").toString().trim();
	    String patientSex =  params.get("patientSex") == null? "" : params.get("patientSex").toString().startsWith("M")?"M":"F";
		final String toxicHabits =  params.get("toxicHabits") == null? "" : params.get("toxicHabits").toString();
		final Collection<TreatmentRecord> treatments = (Collection<TreatmentRecord>)params.get("treatments");
		final Collection<SymptomRecord> diseases = (Collection<SymptomRecord>)params.get("diseases");
		final Collection<SymptomRecord> symptoms = (Collection<SymptomRecord>)params.get("symptoms");
		final Collection<LabTestRecord> labTests = (Collection<LabTestRecord>)params.get("labTests");
		final Collection<ComplicationRecord> complications = (Collection<ComplicationRecord>)params.get("complications");
		final Collection<ProcedureRecord> procedures = (Collection<ProcedureRecord>)params.get("procedures");
		
		
		final StringBuffer query = new StringBuffer();

		
		    if(patientAge!=null && !patientAge.equals("")){
		    	query.append(" OPTIONAL { ");
		    	query.append("    ?p bean:birthDate ?birthDate .");
		    	query.append("    LET (?ageCalc := func:Age(?birthDate) ) .");
		    	query.append("    FILTER (?ageCalc = "+patientAge+") .");	
		    	query.append( "} .");
			}
		    if(patientSex!=null && !patientSex.equals("")){
		    	query.append(" OPTIONAL { ");
		    	query.append("    ?p bean:sex ?sex .");
		    	query.append(" FILTER regex(?sex, \""+patientSex+"\", \"i\") .");	
		    	query.append( "} .");
			}
		    if(toxicHabits!=null && !toxicHabits.equals("")){
		    	query.append(" OPTIONAL { ");
		    	query.append("    ?encounters bean:toxicHabits ?toxicHabits .");
		    	query.append(" FILTER (?toxicHabits = "+toxicHabits+") .");
		    	query.append( "} .");
			}
		    
		    String drugIds = "";
		    if(treatments!=null && treatments.size()>0){
		    	query.append(" OPTIONAL { ");
		    	query.append("    ?encounters bean:treatmentRecords ?treatmentRecords .");
		    	query.append("    ?treatmentRecords bean:drugId ?drugId .");	
				int counter = 0;
				for(TreatmentRecord drug : treatments){
					if(counter==treatments.size()-1){
						drugIds += drug.getDrugId();
					}else{
						drugIds += drug.getDrugId() + "|";	
					}
					counter++;
				}
				query.append(" FILTER regex(?drugId, \""+drugIds+"\", \"i\") .");
				query.append( "} .");
			}

			String diseaseIds = "";
		    if(diseases!=null && diseases.size()>0){
		    	query.append(" OPTIONAL { ");
		    	query.append("    ?encounters bean:symptomRecords ?symptomRecords .");
		    	query.append("    ?symptomRecords bean:diseaseId ?diseaseId .");
				int counter = 0;
				for(SymptomRecord disease : diseases){
					if(counter==diseases.size()-1){
						diseaseIds += disease.getDiseaseId();
					}else{
						diseaseIds += disease.getDiseaseId() + "|";	
					}
					counter++;
				}
				query.append(" FILTER regex(?diseaseId, \""+diseaseIds+"\", \"i\") .");
				query.append( "} .");
			}
		    

			String symptomIds = "";
		    if(symptoms!=null && symptoms.size()>0){
		    	query.append(" OPTIONAL { ");
		    	query.append("    ?encounters bean:symptomRecords ?symptomRecords .");
		    	query.append("    ?symptomRecords bean:symptomId ?symptomId .");
				int counter = 0;
				for(SymptomRecord symptom : symptoms){
					if(counter==symptoms.size()-1){
						symptomIds += symptom.getSymptomId();
					}else{
						symptomIds += symptom.getSymptomId() + "|";	
					}
					counter++;
				}
				query.append(" FILTER regex(?symptomId, \""+symptomIds+"\", \"i\") .");
				query.append( "} .");
			}
		    

			String labTestIds = "";
		    if(labTests!=null && labTests.size()>0){
		    	query.append(" OPTIONAL { ");
		    	query.append("    ?encounters bean:labTestRecords ?labTestRecords .");
		    	query.append("    ?labTestRecords bean:labTestId ?labTestId .");
				int counter = 0;
				for(LabTestRecord labTest : labTests){
					if(counter==labTests.size()-1){
						labTestIds += labTest.getLabTestId();
					}else{
						labTestIds += labTest.getLabTestId() + "|";	
					}
					counter++;
				}
				query.append(" FILTER regex(?labTestId, \""+labTestIds+"\", \"i\") .");
				query.append( "} .");
			}
		    

			String complicationIds = "";
		    if(complications!=null && complications.size()>0){
		    	query.append(" OPTIONAL { ");
		    	query.append("    ?encounters bean:complicationRecords ?complicationRecords .");
		    	query.append("    ?complicationRecords bean:complicationId ?complicationId .");
				int counter = 0;
				for(ComplicationRecord complication : complications){
					if(counter==complications.size()-1){
						complicationIds += complication.getComplicationId();
					}else{
						complicationIds += complication.getComplicationId() + "|";	
					}
					counter++;
				}
				query.append(" FILTER regex(?complicationId, \""+complicationIds+"\", \"i\") .");
				query.append( "} .");
			}
		    

			String procedureIds = "";
		    if(procedures!=null && procedures.size()>0){
		    	query.append(" OPTIONAL { ");
		    	query.append("    ?encounters bean:procedureRecords ?procedureRecords .");
				query.append("    ?procedureRecords bean:procedureId ?procedureId .");
				int counter = 0;
				for(ProcedureRecord procedure : procedures){
					if(counter==procedures.size()-1){
						procedureIds += procedure.getProcedureId();
					}else{
						procedureIds += procedure.getProcedureId() + "|";	
					}
					counter++;
				}
				query.append(" FILTER regex(?procedureId, \""+procedureIds+"\", \"i\") .");
				query.append( "} .");
			}
		
		String queryString = "";
			
		  queryString = 
			     "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "+
	             "PREFIX bean: <http://semrs.googlecode.com/>" +
	             "PREFIX func: <java:com.googlecode.semrs.util.sparql.functions.>  "+
	             "PREFIX diagnosis: <http://semrs.googlecode.com/Diagnosis>" +
	             "SELECT DISTINCT ?s " +
	             "WHERE { " +
	             "    ?e a  <http://semrs.googlecode.com/Encounter> ." +
	             "    ?p a  <http://semrs.googlecode.com/Patient> ." +
	             "    ?p bean:encounters ?encounters ." +
	             "    ?encounters bean:current ?current ." +
	             "    FILTER (?current = false) ."  +
	             "    ?encounters bean:diagnoses ?s ." +
	                   query.toString() +
	             "} ";
			
		

		Collection<Diagnosis> diagnoses = super.execQuery(Diagnosis.class, queryString, "syntaxARQ");
		
		
		Collection<Symptom> compSymptoms = null;
		
		Collection<Procedure> compProcedures = null;
		
		if(!complicationIds.equals("")){
				
			String complicationQueryString = 
	            "PREFIX bean: <http://semrs.googlecode.com/>" +
	            "PREFIX user: <http://semrs.googlecode.com/Complication>" +
	            "SELECT DISTINCT ?s " +
	            "WHERE { " +
	            "    ?s a  <http://semrs.googlecode.com/Complication> ." +
	            "    ?s bean:id ?complicationId ." +
	            " FILTER regex(?complicationId, \""+complicationIds+"\", \"i\") ."+
	            "} ";
			Collection<Complication> comps = super.execQuery(Complication.class, complicationQueryString, "");
		

			if(comps.size()>0){
				compSymptoms = new ArrayList<Symptom>();
				compProcedures = new ArrayList<Procedure>();

				for(Complication comp: comps){
             
					super.fill(comp, "symptoms");
					super.fill(comp, "complicationProcedures");

					compSymptoms.addAll(comp.getSymptoms());
					compProcedures.addAll(comp.getComplicationProcedures());

				}
				
				if(compSymptoms!=null && compSymptoms.size()>0){			
					int counter = 0;
					if(!symptomIds.equals("")){
						symptomIds+="|";
					}
					for(Symptom symptom : compSymptoms){
						if(counter==compSymptoms.size()-1){
							symptomIds += symptom.getId();
						}else{
							symptomIds += symptom.getId() + "|";	
						}
						counter++;
					}
					
				}
				
	            if(compProcedures!=null && compProcedures.size()>0){
	            	int counter = 0;
	            	if(!procedureIds.equals("")){
	            		procedureIds+="|";
					}
					for(Procedure procedure : compProcedures){
						if(counter==compProcedures.size()-1){
							procedureIds += procedure.getId();
						}else{
							procedureIds += procedure.getId() + "|";	
						}
						counter++;
					}
					
				}

			}
			
		}
		
        Collection<Symptom> dSymptoms = null;
		
		Collection<Procedure> dProcedures = null;
		
		Collection<LabTest> lts = null;
		
		Collection<Disease> relDiseases = null;
		
		if(!diseaseIds.equals("")){
				
			String dQueryString = 
	            "PREFIX bean: <http://semrs.googlecode.com/>" +
	            "PREFIX user: <http://semrs.googlecode.com/Disease>" +
	            "SELECT DISTINCT ?s " +
	            "WHERE { " +
	            "    ?s a  <http://semrs.googlecode.com/Disease> ." +
	            "    ?s bean:id ?diseaseId ." +
	            " FILTER regex(?diseaseId, \""+diseaseIds+"\", \"i\") ."+
	            "} ";
			Collection<Disease> ds = super.execQuery(Disease.class, dQueryString, "");
		

			if(ds.size()>0){
				dSymptoms = new ArrayList<Symptom>();
				dProcedures = new ArrayList<Procedure>();
				lts = new ArrayList<LabTest>();
				relDiseases = new ArrayList<Disease>();

				for(Disease dis: ds){
             
					super.fill(dis, "symptoms");
					super.fill(dis, "labTests");
					super.fill(dis, "procedures");
					super.fill(dis, "relDiseases");
					
					dSymptoms.addAll(dis.getSymptoms());
					dProcedures.addAll(dis.getProcedures());
					lts.addAll(dis.getLabTests());
					relDiseases.addAll(dis.getRelDiseases());

				}
				
				if(dSymptoms!=null && dSymptoms.size()>0){			
					int counter = 0;
					if(!symptomIds.equals("")){
						symptomIds+="|";
					}
					for(Symptom symptom : dSymptoms){
						if(counter==dSymptoms.size()-1){
							symptomIds += symptom.getId();
						}else{
							symptomIds += symptom.getId() + "|";	
						}
						counter++;
					}
					
				}
				
	            if(dProcedures!=null && dProcedures.size()>0){
	            	int counter = 0;
	            	if(!procedureIds.equals("")){
	            		procedureIds+="|";
					}
					for(Procedure procedure : dProcedures){
						if(counter==dProcedures.size()-1){
							procedureIds += procedure.getId();
						}else{
							procedureIds += procedure.getId() + "|";	
						}
						counter++;
					}
					
				}
	            
	            if(lts!=null && lts.size()>0){
	            	int counter = 0;
	            	if(!labTestIds.equals("")){
	            		labTestIds+="|";
					}
					for(LabTest lt : lts){
						if(counter==lts.size()-1){
							labTestIds += lt.getId();
						}else{
							labTestIds += lt.getId() + "|";	
						}
						counter++;
					}
					
				}
	            
	            if(relDiseases!=null && relDiseases.size()>0){
	            	int counter = 0;
	            	if(!diseaseIds.equals("")){
	            		diseaseIds+="|";
					}
					for(Disease dis : relDiseases){
						if(counter==relDiseases.size()-1){
							diseaseIds += dis.getId();
						}else{
							diseaseIds += dis.getId() + "|";	
						}
						counter++;
					}
					
				}


			}
			
		}
		
		 
		
		final StringBuffer diseaseQuery = new StringBuffer();
		
	    if(patientAge!=null && !patientAge.equals("")){
	    	diseaseQuery.append(" OPTIONAL { ");
	    	diseaseQuery.append("    ?s bean:minimumAgeRange ?minimumAgeRange .");
	    	diseaseQuery.append("    ?s bean:maximumAgeRange ?maximumAgeRange .");
	    	//diseaseQuery.append("    FILTER (?minimumAgeRange >= "+patientAge+"  && ?maximumAgeRange < "+String.valueOf(Integer.parseInt(patientAge)+1)+") .");	
	    	diseaseQuery.append("    FILTER ("+patientAge+" >= ?minimumAgeRange  &&  "+patientAge+" < ?maximumAgeRange ) .");	
	    	diseaseQuery.append( "} .");
		}
	    if(patientSex!=null && !patientSex.equals("")){
	    	diseaseQuery.append(" OPTIONAL { ");
	    	diseaseQuery.append("    ?s bean:sex ?sex .");
	    	diseaseQuery.append(" FILTER regex(?sex, \""+patientSex+"\", \"i\") .");	
	    	diseaseQuery.append( "} .");
		}
	    if(toxicHabits!=null && !toxicHabits.equals("")){
	    	diseaseQuery.append(" OPTIONAL { ");
	    	diseaseQuery.append("    ?s bean:toxicHabits ?toxicHabits .");
	    	diseaseQuery.append(" FILTER (?toxicHabits = "+toxicHabits+") .");
	    	diseaseQuery.append( "} .");
		}
	    
	    if(!diseaseIds.equals("")){
	    	diseaseQuery.append(" OPTIONAL { ");
	    	diseaseQuery.append("    ?s bean:relDiseases ?relDiseases .");
	    	diseaseQuery.append("    ?relDiseases bean:id ?diseaseId .");
	    	diseaseQuery.append(" FILTER regex(?diseaseId, \""+diseaseIds+"\", \"i\") .");
	    	diseaseQuery.append( "} .");
		}
	    
	    if(!symptomIds.equals("")){
	    	//diseaseQuery.append(" OPTIONAL { ");
	    	diseaseQuery.append("    ?s bean:symptoms ?symptoms .");
	    	diseaseQuery.append("    ?symptoms bean:id ?symptomId .");
	    	diseaseQuery.append(" FILTER regex(?symptomId, \""+symptomIds+"\", \"i\") .");
	    	//diseaseQuery.append( "} .");
		}
	    
	    if(!labTestIds.equals("")){
	    	//diseaseQuery.append(" OPTIONAL { ");
	    	diseaseQuery.append("    ?s bean:labTests ?labTests .");
	    	diseaseQuery.append("    ?labTests bean:id ?labTestId .");
	    	diseaseQuery.append(" FILTER regex(?labTestId, \""+labTestIds+"\", \"i\") .");
	    	//diseaseQuery.append( "} .");
		}
	    
	    if(!procedureIds.equals("")){
	    	diseaseQuery.append(" OPTIONAL { ");
	    	diseaseQuery.append("    ?s bean:procedures ?procedures .");
	    	diseaseQuery.append("    ?procedures bean:id ?procedureId .");
	    	diseaseQuery.append(" FILTER regex(?procedureId, \""+procedureIds+"\", \"i\") .");
	    	diseaseQuery.append( "} .");
		}
	    
	  ArrayList<String> diagIds = new ArrayList<String>();
	    if(diagnoses!=null && diagnoses.size()>0){
	    	diseaseQuery.append(" OPTIONAL { ");
	    	query.append("    ?s bean:id ?id .");
			int counter = 0;
			String diagnosesDiseaseIds = "";
			for(Diagnosis diagnosis : diagnoses){
			 if(!diagIds.contains(diagnosis.getDiseaseId())){
				if(counter==diagnoses.size()-1){
					diagnosesDiseaseIds += diagnosis.getDiseaseId();
				}else{
					diagnosesDiseaseIds += diagnosis.getDiseaseId() + "|";	
				}
				diagIds.add(diagnosis.getDiseaseId());
				counter++;
			 }
			}
			diseaseQuery.append(" FILTER regex(?id, \""+diagnosesDiseaseIds+"\", \"i\") .");
			diseaseQuery.append( "} .");
		}
	    
		
		
		  final String diseaseQueryString = 
	             "PREFIX bean: <http://semrs.googlecode.com/>" +
	             "PREFIX user: <http://semrs.googlecode.com/Disease>" +
	             "SELECT DISTINCT ?s " +
	             "WHERE { " +
	             "    ?s a  <http://semrs.googlecode.com/Disease> ." +
	                   diseaseQuery.toString() +
	             "} ";
	            

	   final Collection<Disease> result = super.execQuery(Disease.class, diseaseQueryString, "");
	   
	   Collection<DiagnosisContainer> dcs = new ArrayList<DiagnosisContainer>();
	   
	   for(Disease disease: result){
		   


		   Double hits = 0.0;
		   if((Integer.parseInt(patientAge) >= disease.getMinimumAgeRange()) 
				   &&  (Integer.parseInt(patientAge) < disease.getMaximumAgeRange())
				   || ((disease.getMinimumAgeRange()==0 && disease.getMaximumAgeRange()==0) && addWeightsByDefault)){
			   hits+=ageWeight;
		   }
		   if(disease.getSex().equals(String.valueOf(patientSex)) || (disease.getSex().equals("") && addWeightsByDefault)){
			   hits+=sexWeight;
		   }
		   if(disease.isToxicHabits() && toxicHabits.equals("true")){
			   hits+=toxicHabitsWeight;
		   }
		   Double sympHits = 0.0;
		   int sympcount = 0;
		   super.fill(disease, "symptoms");
		   for(Symptom symp: disease.getSymptoms()){
			   if(symptoms!=null && symptoms.size()>0){
				   for(SymptomRecord sr : symptoms){
					   if(symp.getId().equals(sr.getSymptomId())){
						   sympcount+=1;
						   if(sr.getSeverity().startsWith("L")){
							   sympHits+=severityLowWeight;
						   }else if(sr.getSeverity().startsWith("M")){
							   sympHits+=severityMediumWeight;
						   }else if(sr.getSeverity().startsWith("H")){
							   sympHits+=severityHighWeight;
						   }else if(sr.getSeverity().startsWith("V")){
							   sympHits+=severityVeryHighWeight;
							   
						   }
					   }
				   }
			   }
			   
		   }
		   
		   if(sympcount>0){
			   hits+=((sympHits/sympcount)*symptomsWeight)/symptoms.size();
		   }
		   
		   int lhcount = 0;
		   super.fill(disease, "labTests");
		   for(com.googlecode.semrs.model.LabTest lh: disease.getLabTests()){
			   if(labTests!=null && labTests.size()>0){
				   for(LabTestRecord ltr : labTests){
					   if(lh.getId().equals(ltr.getLabTestId())){
						   if(ltr.isResult()){
							   lhcount+=1;
							   
						   }
					   }
				   }
			   }
			   
		   }
		   
		   
		   
		   if(lhcount>0){
			   hits+=(lhcount*labTestsWeight)/labTests.size();
		   }
		   
		   Double diseaseHits = 0.0;
		   int rdcount = 0;
		   super.fill(disease, "relDiseases");
		   for(Disease rd: disease.getRelDiseases()){
			   if(diseases!=null && diseases.size()>0){
				   for(SymptomRecord sr : diseases){
					   if(rd.getId().equals(sr.getDiseaseId())){
						   rdcount+=1;
						   if(sr.getSeverity().startsWith("L")){
							   diseaseHits+=severityLowWeight;
						   }else if(sr.getSeverity().startsWith("M")){
							   diseaseHits+=severityMediumWeight;
						   }else if(sr.getSeverity().startsWith("H")){
							   diseaseHits+=severityHighWeight;
						   }else if(sr.getSeverity().startsWith("V")){
							   diseaseHits+=severityVeryHighWeight;
							   
						   }
					   }
				   }
			   }
			   
		   }
		   
		   
		   
		   if(rdcount>0){
			   hits+=((diseaseHits/rdcount)*relDiseasesWeight)/diseases.size();
		   }
		   
		   int prcount = 0;
		   super.fill(disease, "procedures");
		   for(Procedure pr: disease.getProcedures()){
			   if(procedures!=null && procedures.size()>0){
				   for(ProcedureRecord prRecord : procedures){
					   if(pr.getId().equals(prRecord.getProcedureId())){
						   prcount+=1;
					   }
				   }
			   }
			   
		   }
		   
		   
		   
		   if(prcount>0){
			   hits+=(prcount*proceduresWeight)/procedures.size();
		   }
		   
		   if(hits>0){
			   DiagnosisContainer dc = new DiagnosisContainer();
			   dc.setDiseaseName(disease.getName());
			   dc.setDiseaseId(disease.getId());
			   BigDecimal bd = new BigDecimal((hits*100));
			   bd = bd.setScale(2,BigDecimal.ROUND_UP);
			   dc.setProbability(bd .doubleValue());
			   dcs.add(dc);
		   }
		   
	   }
		
         return dcs;
		
	}
	
	
@Override
  public Collection<DiagnosisContainer> getAssistedDiagnosisPrediction(final Map params){

	String patientAge =  params.get("patientAge") == null? "" : params.get("patientAge").toString().trim();
	String patientSex =  params.get("patientSex") == null? "" : params.get("patientSex").toString().startsWith("M")?"M":"F";
	final String toxicHabits =  params.get("toxicHabits") == null? "" : params.get("toxicHabits").toString();
	final Collection<TreatmentRecord> treatments = (Collection<TreatmentRecord>)params.get("treatments");
	final Collection<SymptomRecord> diseases = (Collection<SymptomRecord>)params.get("diseases");
	final Collection<SymptomRecord> symptoms = (Collection<SymptomRecord>)params.get("symptoms");
	final Collection<LabTestRecord> labTests = (Collection<LabTestRecord>)params.get("labTests");
	final Collection<ComplicationRecord> complications = (Collection<ComplicationRecord>)params.get("complications");
	final Collection<ProcedureRecord> procedures = (Collection<ProcedureRecord>)params.get("procedures");

	final StringBuffer query = new StringBuffer();

	if(patientAge!=null && !patientAge.equals("")){
		query.append(" OPTIONAL { ");
		query.append("    ?p bean:birthDate ?birthDate .");
		query.append("    LET (?ageCalc := func:Age(?birthDate) ) .");
		query.append("    FILTER (?ageCalc = "+patientAge+") .");	
		query.append( "} .");
	}
	if(patientSex!=null && !patientSex.equals("")){
		query.append(" OPTIONAL { ");
		query.append("    ?p bean:sex ?sex .");
		query.append(" FILTER regex(?sex, \""+patientSex+"\", \"i\") .");	
		query.append( "} .");
	}
	if(toxicHabits!=null && !toxicHabits.equals("")){
		query.append(" OPTIONAL { ");
		query.append("    ?s bean:toxicHabits ?toxicHabits .");
		query.append(" FILTER (?toxicHabits = "+toxicHabits+") .");
		query.append( "} .");
	}

	String drugIds = "";
	if(treatments!=null && treatments.size()>0){
		query.append("    ?s bean:treatmentRecords ?treatmentRecords .");
		query.append("    ?treatmentRecords bean:drugId ?drugId .");	
		int counter = 0;
		for(TreatmentRecord drug : treatments){
			if(counter==treatments.size()-1){
				drugIds += drug.getDrugId();
			}else{
				drugIds += drug.getDrugId() + "|";	
			}
			counter++;
		}
		query.append(" FILTER regex(?drugId, \""+drugIds+"\", \"i\") .");
	}

	String diseaseIds = "";
	if(diseases!=null && diseases.size()>0){
		query.append("    ?s bean:symptomRecords ?symptomRecords .");
		query.append("    ?symptomRecords bean:diseaseId ?diseaseId .");
		int counter = 0;
		for(SymptomRecord disease : diseases){
			if(counter==diseases.size()-1){
				diseaseIds += disease.getDiseaseId();
			}else{
				diseaseIds += disease.getDiseaseId() + "|";	
			}
			counter++;
		}
		query.append(" FILTER regex(?diseaseId, \""+diseaseIds+"\", \"i\") .");
	}


	String symptomIds = "";
	if(symptoms!=null && symptoms.size()>0){
		query.append("    ?s bean:symptomRecords ?symptomRecords .");
		query.append("    ?symptomRecords bean:symptomId ?symptomId .");
		int counter = 0;
		for(SymptomRecord symptom : symptoms){
			if(counter==symptoms.size()-1){
				symptomIds += symptom.getSymptomId();
			}else{
				symptomIds += symptom.getSymptomId() + "|";	
			}
			counter++;
		}
		query.append(" FILTER regex(?symptomId, \""+symptomIds+"\", \"i\") .");
	}


	String labTestIds = "";
	if(labTests!=null && labTests.size()>0){
		query.append("    ?s bean:labTestRecords ?labTestRecords .");
		query.append("    ?labTestRecords bean:labTestId ?labTestId .");
		int counter = 0;
		for(LabTestRecord labTest : labTests){
			if(counter==labTests.size()-1){
				labTestIds += labTest.getLabTestId();
			}else{
				labTestIds += labTest.getLabTestId() + "|";	
			}
			counter++;
		}
		query.append(" FILTER regex(?labTestId, \""+labTestIds+"\", \"i\") .");
	}


	String complicationIds = "";
	if(complications!=null && complications.size()>0){
		query.append(" OPTIONAL { ");
		query.append("    ?s bean:complicationRecords ?complicationRecords .");
		query.append("    ?complicationRecords bean:complicationId ?complicationId .");
		int counter = 0;
		for(ComplicationRecord complication : complications){
			if(counter==complications.size()-1){
				complicationIds += complication.getComplicationId();
			}else{
				complicationIds += complication.getComplicationId() + "|";	
			}
			counter++;
		}
		query.append(" FILTER regex(?complicationId, \""+complicationIds+"\", \"i\") .");
		query.append("} . ");
	}


	String procedureIds = "";
	if(procedures!=null && procedures.size()>0){
		query.append(" OPTIONAL { ");
		query.append("    ?s bean:procedureRecords ?procedureRecords .");
		query.append("    ?procedureRecords bean:procedureId ?procedureId .");
		int counter = 0;
		for(ProcedureRecord procedure : procedures){
			if(counter==procedures.size()-1){
				procedureIds += procedure.getProcedureId();
			}else{
				procedureIds += procedure.getProcedureId() + "|";	
			}
			counter++;
		}
		query.append(" FILTER regex(?procedureId, \""+procedureIds+"\", \"i\") .");
		query.append("} . ");
	}


	String queryString = "";

	queryString = 
		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "+
		"PREFIX bean: <http://semrs.googlecode.com/>" +
		"PREFIX func: <java:com.googlecode.semrs.util.sparql.functions.>  "+
		"PREFIX encounter: <http://semrs.googlecode.com/Encounter>" +
		"SELECT DISTINCT ?s " +
		"WHERE { " +
		"    ?p a  <http://semrs.googlecode.com/Patient> ." +
		"    ?p bean:encounters ?s ." +
		"    ?s bean:current ?current ." +
		"    FILTER (?current = false) ."  +
		query.toString() +
		"} ";



	Collection<Encounter> encounters = super.execQuery(Encounter.class, queryString, "syntaxARQ");
	
	int attributesSize = 3+treatments.size()+diseases.size()+symptoms.size()+labTests.size()+complications.size()+procedures.size();

	FastVector allAttributes = new FastVector(attributesSize);

	Attribute ageAttribute = new Attribute("age");
	allAttributes.addElement(ageAttribute);

	FastVector sexmttributeValues = new FastVector(2);
	sexmttributeValues.addElement("m");
	sexmttributeValues.addElement("f");
	Attribute sexAttribute = new Attribute("sex",sexmttributeValues);
	allAttributes.addElement(sexAttribute);


	FastVector booleanAttribute = new FastVector(2);
	booleanAttribute.addElement("0");
	booleanAttribute.addElement("1");
	Attribute sinxAttribute = new Attribute("toxicHabits",booleanAttribute);
	allAttributes.addElement(sinxAttribute);

	ArrayList<String> drugIdsarray = new ArrayList<String>();
	ArrayList<String> diseaseIdsarray = new ArrayList<String>();
	ArrayList<String> labTestIdsarray = new ArrayList<String>();
	ArrayList<String> procedureIdsarray = new ArrayList<String>();
	ArrayList<String> complicationIdsarray = new ArrayList<String>();
	ArrayList<String> symptomIdsarray = new ArrayList<String>();
	ArrayList<String> diagIdsarray = new ArrayList<String>();

	if(treatments!=null && treatments.size()>0){
		for(TreatmentRecord drug : treatments){
			Attribute drugAttribute = new Attribute(drug.getDrugId()+"Drug",booleanAttribute);
			allAttributes.addElement(drugAttribute);

		}
	}


	if(diseases!=null && diseases.size()>0){
		for(SymptomRecord disease : diseases){
			Attribute diseaseAttribute = new Attribute(disease.getDiseaseId()+disease.getType(),booleanAttribute);
			allAttributes.addElement(diseaseAttribute);
		}
	}

	if(symptoms!=null && symptoms.size()>0){
		for(SymptomRecord symptom : symptoms){
			Attribute symptomAttribute = new Attribute(symptom.getSymptomId()+symptom.getType(),booleanAttribute);
			allAttributes.addElement(symptomAttribute);

		}

	}

	if(labTests!=null && labTests.size()>0){

		for(LabTestRecord labTest : labTests){
			Attribute labTestAttribute = new Attribute(labTest.getLabTestId()+"LabTest",booleanAttribute);
			allAttributes.addElement(labTestAttribute);

		}
	}

	if(complications!=null && complications.size()>0){

		for(ComplicationRecord complication : complications){
			Attribute complicationAttribute = new Attribute(complication.getComplicationId()+"Complication",booleanAttribute);
			allAttributes.addElement(complicationAttribute);

		}

	}


	if(procedures!=null && procedures.size()>0){

		for(ProcedureRecord procedure : procedures){
			Attribute procedureAttribute = new Attribute(procedure.getProcedureId()+"Procedure",booleanAttribute);
			allAttributes.addElement(procedureAttribute);

		}
	}
	ArrayList<Integer> indexes = new ArrayList<Integer>();
	ArrayList<Diagnosis> diagnosesResults = new ArrayList<Diagnosis>();

	for(Encounter enc : encounters){
		super.fill(enc, "diagnoses");
		for(Diagnosis diag : enc.getDiagnoses()){
			if(!diagIdsarray.contains(diag.getDiseaseId())){
				Attribute diagnosisAttribute = new Attribute(diag.getDiseaseId()+"Diagnosis",booleanAttribute);
				allAttributes.addElement(diagnosisAttribute);
				diagIdsarray.add(diag.getDiseaseId());
				indexes.add(allAttributes.size()-1);
				diagnosesResults.add(diag);

			}
		}

	}

   Collection<DiagnosisContainer> dcs = new ArrayList<DiagnosisContainer>();
   
	for(int cnt=0;cnt<indexes.size();cnt++){
		Instances trainingDataSet = new Instances("semrs", allAttributes, encounters.size()+1);
		trainingDataSet.setClassIndex(indexes.get(cnt)); 
		Instance instance = new Instance(attributesSize+diagIdsarray.size());
		instance.setDataset(trainingDataSet);
		for(Encounter encount : encounters){

			Patient patient  =  super.load(Patient.class, encount.getPatientId(), false);
			instance.setValue(0, Util.getAge(patient.getBirthDate()));
			instance.setValue(1, patient.getSex().startsWith("M")?"m":"f");
			instance.setValue(2, encount.isToxicHabits()?1:0);
			int ctr = 3;


			if(treatments!=null && treatments.size()>0){
				super.fill(encount, "treatmentRecords");
				for(TreatmentRecord tr : encount.getTreatmentRecords()){
					drugIdsarray.add(tr.getDrugId());

				}
				for(TreatmentRecord drug : treatments){
					instance.setValue(ctr,drugIdsarray.contains(drug.getDrugId())?1:0);
					ctr+=1;

				}


			}

			if(diseases!=null && diseases.size()>0){
				super.fill(encount, "symptomRecords");

				for(SymptomRecord tr : encount.getSymptomRecords()){
					if(tr.getType().equals("Disease")){
						diseaseIdsarray.add(tr.getDiseaseId());
					}
				}
				for(SymptomRecord disease : diseases){
					instance.setValue(ctr,diseaseIdsarray.contains(disease.getDiseaseId())?1:0);
					ctr+=1;
				}


			}

			if(symptoms!=null && symptoms.size()>0){
				super.fill(encount, "symptomRecords");

				for(SymptomRecord tr : encount.getSymptomRecords()){
					if(tr.getType().equals("Symptom")){
						symptomIdsarray.add(tr.getSymptomId());

					}
				}

				for(SymptomRecord symptom : symptoms){
					instance.setValue(ctr,symptomIdsarray.contains(symptom.getSymptomId())?1:0);
					ctr+=1;
				}
			}


			if(labTests!=null && labTests.size()>0){
				super.fill(encount, "labTestRecords");

				for(LabTestRecord tr : encount.getLabTestRecords()){
					labTestIdsarray.add(tr.getLabTestId());

				}

				for(LabTestRecord labTest : labTests){
					instance.setValue(ctr,labTestIdsarray.contains(labTest.getLabTestId())?1:0);
					ctr+=1;
				}
			}


			if(complications!=null && complications.size()>0){
				super.fill(encount, "complicationRecords");

				for(ComplicationRecord tr : encount.getComplicationRecords()){
					complicationIdsarray.add(tr.getComplicationId());

				}

				for(ComplicationRecord complication : complications){
					instance.setValue(ctr,complicationIdsarray.contains(complication.getComplicationId())?1:0);
					ctr+=1;
				}
			}



			if(procedures!=null && procedures.size()>0){
				super.fill(encount, "procedureRecords");

				for(ProcedureRecord tr : encount.getProcedureRecords()){
					procedureIdsarray.add(tr.getProcedureId());

				}

				for(ProcedureRecord tr : procedures){
					instance.setValue(ctr,procedureIdsarray.contains(tr.getProcedureId())?1:0);
					ctr+=1;
				}
			}

			ArrayList<String> diagContainer = new ArrayList<String>();
			super.fill(encount, "diagnoses");

			for(Diagnosis diag : encount.getDiagnoses()){
				diagContainer.add(diag.getDiseaseId());
			}

			for(String did : diagIdsarray){
				instance.setValue(ctr,diagContainer.contains(did)?1:0);
				ctr+=1;
			}



			trainingDataSet.add(instance);

		}

		Instance newInstance  = new Instance(attributesSize+diagIdsarray.size());
		newInstance.setDataset(trainingDataSet);
		newInstance.setValue(0, Integer.valueOf(patientAge));
		newInstance.setValue(1, patientSex.startsWith("M")?"m":"f");
		newInstance.setValue(2, toxicHabits.equals("true")?1:0);
		int ctr = 3;
		if(treatments!=null && treatments.size()>0){
			for(TreatmentRecord drug : treatments){
				newInstance.setValue(ctr, 1);
				ctr+=1;

			}

		}

		if(diseases!=null && diseases.size()>0){
			for(SymptomRecord disease : diseases){
				newInstance.setValue(ctr, 1);
				ctr+=1;


			}
		}	    

		if(symptoms!=null && symptoms.size()>0){
			for(SymptomRecord symptom : symptoms){
				newInstance.setValue(ctr, 1);
				ctr+=1;
			}

		}

		if(labTests!=null && labTests.size()>0){

			for(LabTestRecord labTest : labTests){
				newInstance.setValue(ctr, 1);
				ctr+=1;

			}
		}

		if(complications!=null && complications.size()>0){

			for(ComplicationRecord complication : complications){
				newInstance.setValue(ctr, 1);
				ctr+=1;
			}

		}

		if(procedures!=null && procedures.size()>0){

			for(ProcedureRecord procedure : procedures){
				newInstance.setValue(ctr, 1);
				ctr+=1;

			}
		}
		trainingDataSet.add(newInstance);
		
		

		LOG.info(trainingDataSet);

		NaiveBayes nb = new NaiveBayes();
		try {
			nb.buildClassifier(trainingDataSet);
		} catch (Exception e) {
			LOG.error("Error building NaiveBayes Classifier for training data caused by " + e);
		}
		Instance predictedInstance = trainingDataSet.instance(encounters.size());

		double result = 0;
		try {
			result = nb.classifyInstance(predictedInstance);
		} catch (Exception e) {
			LOG.error("Error classifying instance " +  predictedInstance + " caused by " + e);
		}
		double[] dist;
		try {
			dist = nb.distributionForInstance(predictedInstance);
			LOG.info("Distribution for class index " + indexes.get(cnt) + " : Expected=" 
					+ predictedInstance.stringValue(indexes.get(cnt)) + " Predicted=" + result);
			LOG.info("%Distribution false = " + dist[0] + " %Distribution true = " + dist[1]);
			if(result>0){
				Diagnosis diag = diagnosesResults.get(cnt);
				DiagnosisContainer container = new DiagnosisContainer();
				container.setDiseaseId(diag.getDiseaseId());
				container.setDiseaseName(diag.getName());
				BigDecimal bd = new BigDecimal((dist[1]*100));
				bd = bd.setScale(2,BigDecimal.ROUND_UP);
				container.setProbability(bd.doubleValue());
				dcs.add(container);

			}

		} catch (Exception e) {
			LOG.error("Error building distribution for instance " +  predictedInstance +" caused by " + e);
		}
	}

	   return dcs;
    
	  
		
}
	

	@Override
	public Encounter saveEncounter(final Encounter encounter) throws SaveOrUpdateException {
		super.save(encounter);
		return encounter;
	}
	
	@Override
	public Encounter saveDeepEncounter(final Encounter encounter) throws SaveOrUpdateException {
		super.saveDeep(encounter);
		return encounter;
	}


	public void setAgeWeight(Double ageWeight) {
		this.ageWeight = ageWeight;
	}

	public void setSexWeight(Double sexWeight) {
		this.sexWeight = sexWeight;
	}

	public void setToxicHabitsWeight(Double toxicHabitsWeight) {
		this.toxicHabitsWeight = toxicHabitsWeight;
	}

	public void setSymptomsWeight(Double symptomsWeight) {
		this.symptomsWeight = symptomsWeight;
	}

	public void setLabTestsWeight(Double labTestsWeight) {
		this.labTestsWeight = labTestsWeight;
	}

	public void setRelDiseasesWeight(Double relDiseasesWeight) {
		this.relDiseasesWeight = relDiseasesWeight;
	}

	public void setProceduresWeight(Double proceduresWeight) {
		this.proceduresWeight = proceduresWeight;
	}

	public void setSeverityLowWeight(Double severityLowWeight) {
		this.severityLowWeight = severityLowWeight;
	}

	public void setSeverityMediumWeight(Double severityMediumWeight) {
		this.severityMediumWeight = severityMediumWeight;
	}

	public void setSeverityHighWeight(Double severityHighWeight) {
		this.severityHighWeight = severityHighWeight;
	}

	public void setSeverityVeryHighWeight(Double severityVeryHighWeight) {
		this.severityVeryHighWeight = severityVeryHighWeight;
	}

	public void setAddWeightsByDefault(boolean addWeightsByDefault) {
		this.addWeightsByDefault = addWeightsByDefault;
	}



}
