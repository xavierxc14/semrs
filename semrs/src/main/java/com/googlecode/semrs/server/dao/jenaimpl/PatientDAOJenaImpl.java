package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.semrs.model.Patient;
import com.googlecode.semrs.server.dao.PatientDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.util.Util;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;

public class PatientDAOJenaImpl extends GenericDAOJenaImpl  implements PatientDAO{
	
	private static final Logger LOG = Logger.getLogger(PatientDAOJenaImpl.class);
	

	@Override
	public void deletePatient(final Patient patient) throws DeleteException {
		super.delete(patient);
	}
	
	@Override
	public Patient getPatient(final String id, final boolean deep) {
		final Patient patient = super.load(Patient.class, String.valueOf(id), deep);	
		return patient;
	}

	@Override
	public Patient savePatient(final Patient patient) throws SaveOrUpdateException {
		super.saveDeep(patient);
		return patient;
	}

	@Override
	public Collection<Patient> listPatients() {
		return (Collection<Patient>) super.load(Patient.class);
	}
	
	@Override
	public Collection<Patient> listPatients(final String order, final String orderBy,
			final String limit, final String offset) {
		
		String queryString = "";
		if(orderBy.equals("age")){
			queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX func: <java:com.googlecode.semrs.util.sparql.functions.>  "+
			"PREFIX patient: <http://semrs.googlecode.com/Patient>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Patient> ." +
			" OPTIONAL { " +
			"    ?s bean:birthDate ?birthDate ." +
			"    LET (?age := func:Age(?birthDate) ) ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;		
		}else{
		 queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX func: <java:com.googlecode.semrs.util.sparql.functions.>  "+
			"PREFIX patient: <http://semrs.googlecode.com/Patient>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Patient> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
		}

		final Collection<Patient> result = super.execQuery(Patient.class, queryString, "syntaxARQ");
		return result;
	}
	
	@Override
	public int getPatientCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX patient: <http://semrs.googlecode.com/Patient>" +
			"SELECT (count (distinct *) As ?patientCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Patient> ." +
			"} ";

		return super.count(queryString, "patientCount");
	}
	
	@Override
	public Collection<Patient> listPatientsByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		
		final String id =  params.get("id")==null? "" : params.get("id").toString();
		final String name =  params.get("name")==null? "" : params.get("name").toString();
		final String lastName = params.get("lastName")==null? "" : params.get("lastName").toString();
		final String sex =  params.get("sex")==null? "" : params.get("sex").toString();
		final String providerId =  params.get("providerId")==null? "" : params.get("providerId").toString();
		final String voided =  params.get("voided")==null? "" : params.get("voided").toString();
		final String ageFrom =  params.get("ageFrom")==null? "" : params.get("ageFrom").toString();
		final String ageTo =  params.get("ageTo")==null? "" : params.get("ageTo").toString();
		final String birthdayFrom =  params.get("birthdayFrom")==null? "" : params.get("birthdayFrom").toString();
		final String birthdayTo =  params.get("birthdayTo")==null? "" : params.get("birthdayTo").toString();
		final String encounterDateFrom =  params.get("encounterDateFrom")==null? "" : params.get("encounterDateFrom").toString();
		final String encounterDateTo =  params.get("encounterDateTo")==null? "" : params.get("encounterDateTo").toString();
		final String creationDateFrom =  params.get("creationDateFrom")==null? "" : params.get("creationDateFrom").toString();
		final String creationDateTo =  params.get("creationDateTo")==null? "" : params.get("creationDateTo").toString();
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		final Collection<String> drugs = (Collection<String>)params.get("drugs");
		
		final StringBuffer queryBuffer = new StringBuffer();
		if(!orderBy.equals("")){
			if(orderBy.equals("age")){
				queryBuffer.append(" OPTIONAL { ");
				queryBuffer.append("    ?s bean:birthDate ?birthDate .");
				queryBuffer.append("    LET (?age := func:Age(?birthDate) ) .");
				queryBuffer.append( "} .");	
			}else{
			queryBuffer.append(" OPTIONAL { ");
			queryBuffer.append("    ?s bean:"+orderBy+" ?"+orderBy+" .");
			queryBuffer.append( "} .");
			}
	
		}		
		
		if(id!=null && !id.equals("")){
			queryBuffer.append("    ?s bean:id ?patientId .");
			queryBuffer.append(" FILTER regex(?patientId, \""+id+"\", \"i\") .");
		}
		if(name!=null && !name.equals("")){
			queryBuffer.append("    ?s bean:name ?name .");
			queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
		if(lastName!=null && !lastName.equals("")){
			queryBuffer.append("    ?s bean:lastName ?lastName .");
			queryBuffer.append(" FILTER regex(?lastName, \""+lastName+"\", \"i\") .");
		}
		if(sex!=null && !sex.equals("")){
			queryBuffer.append("    ?s bean:sex ?sex .");
			queryBuffer.append(" FILTER regex(?sex, \""+sex+"\", \"i\") .");	
		}
		if(providerId!=null && !providerId.equals("")){
			queryBuffer.append("    ?s bean:provider ?provider .");
			queryBuffer.append("    ?provider  bean:username ?providerId .");
			queryBuffer.append(" FILTER regex(?providerId, \""+providerId+"\", \"i\") .");	
		}
        if((ageFrom!=null && !ageFrom.equals("")) && (ageTo!=null && !ageTo.equals(""))){
			queryBuffer.append("    ?s bean:birthDate ?birthDate .");
			queryBuffer.append("    LET (?ageCalc := func:Age(?birthDate) ) .");
			queryBuffer.append(" FILTER (?ageCalc >= "+ageFrom+" && ?ageCalc < "+String.valueOf(Integer.parseInt(ageTo)+1)+") .");	
		}
        if((birthdayFrom!=null && !birthdayFrom.equals("")) && (birthdayTo!=null && !birthdayTo.equals(""))){
			queryBuffer.append("    ?s bean:birthDate ?birthDate .");
			queryBuffer.append(" FILTER (?birthDate >= \""+Util.toXSDString(birthdayFrom)+"\"^^xsd:dateTime && ?birthDate < \""+Util.toXSDString(Util.addStringDate("D", 1,birthdayTo))+"\"^^xsd:dateTime) .");	
		}
        if((creationDateFrom!=null && !creationDateFrom.equals("")) && (creationDateTo!=null && !creationDateTo.equals(""))){
			queryBuffer.append("    ?s bean:creationDate ?creationDate .");
			queryBuffer.append(" FILTER (?creationDate >= \""+Util.toXSDString(creationDateFrom)+"\"^^xsd:dateTime && ?creationDate < \""+Util.toXSDString(Util.addStringDate("D", 1,creationDateTo))+"\"^^xsd:dateTime) .");	
		}
        if(voided!=null && !voided.equals("")){
			queryBuffer.append("    ?s bean:voided ?voided .");
			queryBuffer.append(" FILTER (?voided = "+voided+") .");	
		}
        if((encounterDateFrom!=null && !encounterDateFrom.equals("")) && (encounterDateTo!=null && !encounterDateTo.equals(""))){
			queryBuffer.append("    ?s bean:encounters ?encounters .");
			queryBuffer.append("    ?encounters bean:encounterDate ?encounterDate .");
			queryBuffer.append(" FILTER (?encounterDate >= \""+Util.toXSDString(encounterDateFrom)+"\"^^xsd:dateTime && ?encounterDate < \""+Util.toXSDString(Util.addStringDate("D", 1,encounterDateTo))+"\"^^xsd:dateTime) .");		
		}
        if(diseases!=null && diseases.size()>0){
			queryBuffer.append("    ?s bean:encounters ?encounters .");
			queryBuffer.append("    ?encounters bean:diagnoses ?diagnoses .");
			queryBuffer.append("    ?diagnoses bean:diseaseId ?diseaseId .");
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
        if(drugs!=null && drugs.size()>0){
			queryBuffer.append("    ?s bean:encounters ?encounters .");
			queryBuffer.append("    ?encounters bean:treatmentRecords ?treatmentRecords .");
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
		
		
        final String queryString = 
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "+
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX func: <java:com.googlecode.semrs.util.sparql.functions.>  "+
			"PREFIX patient: <http://semrs.googlecode.com/Patient>" +
			"SELECT DISTINCT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Patient> ." +
			      queryBuffer.toString() + 
			"} " +
		    "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;

        final Collection<Patient> result = super.execQuery(Patient.class, queryString, "syntaxARQ");
		return result;

	}
	
	
	@Override
	public int getPatientCount(final Map params) {
		
		final String patientId =  params.get("patientId")==null? "" : params.get("patientId").toString();
		final String name =  params.get("name")==null? "" : params.get("name").toString();
		final String lastName = params.get("lastName")==null? "" : params.get("lastName").toString();
		final String sex =  params.get("sex")==null? "" : params.get("sex").toString();
		final String providerId =  params.get("providerId")==null? "" : params.get("providerId").toString();
		final String voided =  params.get("voided")==null? "" : params.get("voided").toString();
		final String ageFrom =  params.get("ageFrom")==null? "" : params.get("ageFrom").toString();
		final String ageTo =  params.get("ageTo")==null? "" : params.get("ageTo").toString();
		final String birthdayFrom =  params.get("birthdayFrom")==null? "" : params.get("birthdayFrom").toString();
		final String birthdayTo =  params.get("birthdayTo")==null? "" : params.get("birthdayTo").toString();
		final String encounterDateFrom =  params.get("encounterDateFrom")==null? "" : params.get("encounterDateFrom").toString();
		final String encounterDateTo =  params.get("encounterDateTo")==null? "" : params.get("encounterDateTo").toString();
		final String creationDateFrom =  params.get("creationDateFrom")==null? "" : params.get("creationDateFrom").toString();
		final String creationDateTo =  params.get("creationDateTo")==null? "" : params.get("creationDateTo").toString();
		final Collection<String> diseases = (Collection<String>)params.get("diseases");
		final Collection<String> drugs = (Collection<String>)params.get("drugs");
		
		final StringBuffer queryBuffer = new StringBuffer();
		
		
		if(patientId!=null && !patientId.equals("")){
			queryBuffer.append("    ?s bean:id ?patientId .");
			queryBuffer.append(" FILTER regex(?patientId, \""+patientId+"\", \"i\") .");
		}
		if(name!=null && !name.equals("")){
			queryBuffer.append("    ?s bean:name ?name .");
			queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
		if(lastName!=null && !lastName.equals("")){
			queryBuffer.append("    ?s bean:lastName ?lastName .");
			queryBuffer.append(" FILTER regex(?lastName, \""+lastName+"\", \"i\") .");
		}
		if(sex!=null && !sex.equals("")){
			queryBuffer.append("    ?s bean:sex ?sex .");
			queryBuffer.append(" FILTER regex(?sex, \""+sex+"\", \"i\") .");	
		}
		if(providerId!=null && !providerId.equals("")){
			queryBuffer.append("    ?s bean:provider ?provider .");
			queryBuffer.append("    ?provider  bean:username ?providerId .");
			queryBuffer.append(" FILTER regex(?providerId, \""+providerId+"\", \"i\") .");	
		}
		if((ageFrom!=null && !ageFrom.equals("")) && (ageTo!=null && !ageTo.equals(""))){
			queryBuffer.append("    ?s bean:birthDate ?birthDate .");
			queryBuffer.append("    LET (?ageCalc := func:Age(?birthDate) ) .");
			queryBuffer.append(" FILTER (?ageCalc >= "+ageFrom+" && ?ageCalc < "+String.valueOf(Integer.parseInt(ageTo)+1)+") .");	
		}
		if((birthdayFrom!=null && !birthdayFrom.equals("")) && (birthdayTo!=null && !birthdayTo.equals(""))){
			queryBuffer.append("    ?s bean:birthDate ?birthDate .");
			queryBuffer.append(" FILTER (?birthDate >= \""+Util.toXSDString(birthdayFrom)+"\"^^xsd:dateTime && ?birthDate < \""+Util.toXSDString(Util.addStringDate("D", 1,birthdayTo))+"\"^^xsd:dateTime) .");	
		}
		if((creationDateFrom!=null && !creationDateFrom.equals("")) && (creationDateTo!=null && !creationDateTo.equals(""))){
			queryBuffer.append("    ?s bean:creationDate ?creationDate .");
			queryBuffer.append(" FILTER (?creationDate >= \""+Util.toXSDString(creationDateFrom)+"\"^^xsd:dateTime && ?creationDate < \""+Util.toXSDString(Util.addStringDate("D", 1,creationDateTo))+"\"^^xsd:dateTime) .");	
		}
		if(voided!=null && !voided.equals("")){
			queryBuffer.append("    ?s bean:voided ?voided .");
			queryBuffer.append(" FILTER (?voided = "+voided+") .");	
		}
		if((encounterDateFrom!=null && !encounterDateFrom.equals("")) && (encounterDateTo!=null && !encounterDateTo.equals(""))){
			queryBuffer.append("    ?s bean:encounters ?encounters .");
			queryBuffer.append("    ?encounters bean:encounterDate ?encounterDate .");
			queryBuffer.append(" FILTER (?encounterDate >= \""+Util.toXSDString(encounterDateFrom)+"\"^^xsd:dateTime && ?encounterDate < \""+Util.toXSDString(Util.addStringDate("D", 1,encounterDateTo))+"\"^^xsd:dateTime) .");		
		}
		if(diseases!=null && diseases.size()>0){
			queryBuffer.append("    ?s bean:encounters ?encounters .");
			queryBuffer.append("    ?encounters bean:diagnoses ?diagnoses .");
			queryBuffer.append("    ?diagnoses bean:diseaseId ?diseaseId .");
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
		if(drugs!=null && drugs.size()>0){
			queryBuffer.append("    ?s bean:encounters ?encounters .");
			queryBuffer.append("    ?encounters bean:drugs ?drugs .");
			queryBuffer.append("    ?drugs bean:drugId ?drugId .");
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
		
		final String queryString = 
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "+
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX func: <java:com.googlecode.semrs.util.sparql.functions.>  "+
			"PREFIX patient: <http://semrs.googlecode.com/Patient>" +
			"SELECT (count (distinct *) As ?patientCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Patient> ." +
			      queryBuffer.toString() + 
			"} ";

		return super.count(queryString, "patientCount");
	}

	@Override
	public int getSexCount(String sex){
		
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX patient: <http://semrs.googlecode.com/Patient>" +
			"SELECT (count (*) As ?patientCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Patient> ." +   
			"    ?s bean:sex ?sex ."  + 
			" FILTER regex(?sex, \""+sex+"\", \"i\") ." +
			"} ";
		
		final com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		final QueryExecution qe  = QueryExecutionFactory.create(query,getModel());
		Integer count = 0;
		try{	
			final ResultSet rs=qe.execSelect();
			final Object value = ((Literal)rs.nextSolution().get("patientCount")).getValue();
			count = Integer.valueOf(value.toString());
			qe.close();
		}catch(Exception e){
			LOG.error(" in getSexCount "+ queryString +" caused by " + e);
		}finally{ 
			qe.close();
		}
		
		
		return count;
	}
	
	
	@Override
	public int getAgeCount(int from, int to){
		
		final String queryString = 
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "+
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX func: <java:com.googlecode.semrs.util.sparql.functions.>  "+
			"PREFIX patient: <http://semrs.googlecode.com/Patient>" +
			"SELECT (count (distinct ?s) As ?patientCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Patient> ." +
		    "    ?s bean:birthDate ?birthDate ." +
			"    LET (?ageCalc := func:Age(?birthDate) ) ." +
			" FILTER (?ageCalc >= "+from+" && ?ageCalc < "+to+") ."+	
			"} ";
		
		final com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		final QueryExecution qe  = QueryExecutionFactory.create(query,getModel());
		Integer count = 0;
		try{	
			final ResultSet rs=qe.execSelect();
			count = (Integer) ((Literal)rs.nextSolution().get("patientCount")).getValue();
			qe.close();
		}catch(Exception e){
			LOG.error(" in getAgeCount " + queryString +"caused by " + e);
		}finally{ 
			qe.close();
		}
		
		
		return count;
	}

   @Override
   public int getVoidCount(boolean voided){
		
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX patient: <http://semrs.googlecode.com/Patient>" +
			"SELECT (count (*) As ?patientCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Patient> ." +   
			"    ?s bean:voided ?voided ."+
			" FILTER (?voided = "+voided+") ."+	
			"}";
		
		final com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		final QueryExecution qe  = QueryExecutionFactory.create(query,getModel());
		Integer count = 0;
		try{	
			final ResultSet rs=qe.execSelect();
			final Object value = ((Literal)rs.nextSolution().get("patientCount")).getValue();
			count = Integer.valueOf(value.toString());
			qe.close();
		}catch(Exception e){
			LOG.error(" in getVoidCount "+ queryString +" caused by " + e);
		}finally{ 
			qe.close();
		}
		
		
		return count;
	}
	

	

}
