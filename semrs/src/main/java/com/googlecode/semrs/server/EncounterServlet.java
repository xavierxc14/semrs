package com.googlecode.semrs.server;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.semrs.model.ComplicationRecord;
import com.googlecode.semrs.model.Diagnosis;
import com.googlecode.semrs.model.DiagnosisContainer;
import com.googlecode.semrs.model.Encounter;
import com.googlecode.semrs.model.LabTestRecord;
import com.googlecode.semrs.model.Patient;
import com.googlecode.semrs.model.ProcedureRecord;
import com.googlecode.semrs.model.SymptomRecord;
import com.googlecode.semrs.model.TreatmentRecord;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.EncounterService;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.PatientService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.EndPage;
import com.googlecode.semrs.util.ExcelExporter;
import com.googlecode.semrs.util.Util;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class EncounterServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(EncounterServlet.class);

	private PatientService patientService;
	
	private UserService userService;
	
	private GenericService genericService;
	
	private EncounterService encounterService;
	
	private static final String patientFolder = "/patient/";
	
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.patientService = (PatientService) factory.getBean("patientService");
		this.userService = (UserService) factory.getBean("userService");
		this.genericService = (GenericService) factory.getBean("genericService");
		this.encounterService = (EncounterService) factory.getBean("encounterService");
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		handleRequest(req, resp);
	}

	private void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {

            String encounterAction = req.getParameter("encounterAction");

			if (encounterAction != null) {
		        if(encounterAction.equals("newEncounter")){
					newEncounter(req,resp);
				}else if(encounterAction.equals("deleteEncounter")){
					deleteEncounter(req,resp);
				}else if(encounterAction.equals("listCurrentEncounters")){
					listCurrentEncounters(req,resp);
				}else if(encounterAction.equals("listPreviousEncounters")){
					listPreviousEncounters(req,resp);
				}else if(encounterAction.equals("exportEncounters")){
					exportEncounters(req,resp);
				}else if(encounterAction.equals("getEncounter")){
					getEncounter(req,resp);
				}else if(encounterAction.equals("saveEncounter")){
					saveEncounter(req,resp);
				}else if(encounterAction.equals("getDiagnosis")){
					getDiagnosis(req,resp);
				}else if(encounterAction.equals("getEncounterRecords")){
					getEncounterRecords(req,resp);
				}else if(encounterAction.equals("getEncounterReport")){
					getEncounterReport(req,resp);
				}else if(encounterAction.equals("getLatestEncounterRecords")){
					getLatestEncounterRecords(req,resp);
				}else if(encounterAction.equals("listEncounters")){
					listEncounters(req,resp);
				}
		        
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {
    
			LOG.error("Error in EncounterServlet = " + ex);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		handleRequest(req, resp);
	}
	
	
	protected void newEncounter(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		String patientId = req.getParameter("patientId") == null ? ""
				: URLDecoder.decode(req.getParameter("patientId"), "UTF-8");
		String providerId = req.getParameter("providerIdEncounter") == null ? ""
				: URLDecoder.decode(req.getParameter("providerIdEncounter"), "UTF-8");
		String encounterDate = req.getParameter("encounterDate") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterDate"), "UTF-8");
		String encounterTime = req.getParameter("encounterTime") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterTime"), "UTF-8").toUpperCase();
		String refferral = req.getParameter("refferral") == null ? ""
				: URLDecoder.decode(req.getParameter("refferral"), "UTF-8");
		String reason = req.getParameter("reason") == null ? ""
				: URLDecoder.decode(req.getParameter("reason"), "UTF-8");

		if(providerId.equals("") || encounterDate.equals("") || encounterTime.equals("")){
			errors.add(" por favor complete los campos obligatorios para continuar");
		}else{
			Patient patient = patientService.getPatient(patientId, false);
			DateFormat dfm = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
			Date newDate = dfm.parse(encounterDate+" "+encounterTime);
			if(patient!=null){
				if(!patient.isVoided()){
					Date today = new Date();
					if(newDate.before(today)){
						errors.add(" por favor seleccione una fecha y hora igual o superior a la actual.");
					}else{
						Map params = new HashMap();
						params.put("daySearch", "true");
						params.put("encounterDate", encounterDate);
						//params.put("current", "true");
						Collection<Encounter> encounters = encounterService.listEncountersByQuery(params, "", "", "", "");
						for(Encounter enc : encounters){
							Date encDate = enc.getEncounterDate();
							if(enc.getEncounterProvider().equals(providerId) && enc.getPatientId().equals(patientId)){
								errors.add(" este paciente ya posee consultas para el dia seleccionado con el medico asignado.");
								break;
							}else if(enc.getPatientId().equals(patientId) && encDate.equals(newDate)){
								errors.add(" este paciente ya posee una consulta programada para la fecha y hora seleccionada.");
								break;
							}else if(enc.getEncounterProvider().equals(providerId) && encDate.equals(newDate)){
								errors.add(" el medico asignado ya posee una consulta programada para la fecha y hora seleccionada.");
								break;
							}
						}
						encounters = null;
					}
				}else{
					errors.add(" el paciente se encuentra inactivo.");
				}

			}else{
				errors.add(" paciente no existe");
			}
			
			if(errors == null || errors.size()==0){
				Encounter encounter = new Encounter();
				User currentUser = userService.getCurrentUser();
				encounter.setId(patientId+providerId.toUpperCase()+newDate.getTime());
				encounter.setEncounterDate(newDate);
				encounter.setCurrent(true);
				encounter.setPatientId(patientId);
				encounter.setEncounterProvider(providerId);
				encounter.setRefferral(refferral);
				encounter.setEncounterReason(reason);
				encounter.setLastEditDate(new Date());
				encounter.setLastEditUser(currentUser.getUsername());
				encounter.setCreationUser(currentUser.getUsername());
				encounter.setCreationDate(new Date());
				encounterService.saveEncounter(encounter);
				patient.addEncounter(encounter);
				patient.setLastEditDate(new Date());
				patient.setLastEditUser(currentUser.getUsername());
				patientService.savePatient(patient);
				
				String folder = patientFolder;
				URL resource = this.getClass().getResource(folder);
				File patientFileFolder = new File(resource.getFile() + patient.getId() + "/" + encounter.getId() + "/");
				if(!patientFileFolder.exists()){		
					patientFileFolder.setWritable(true);
					patientFileFolder.setReadable(true);
					patientFileFolder.mkdir();
				}
			}
		}
		
		

		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();

		if (errors != null && errors.size() > 0) {
			out.println("Ocurrier&oacute;n errores al guardar: ");
			for (String error : errors) {
				out.println(error);
			}
			return;

		} else {
			resp.setStatus(HttpServletResponse.SC_OK);
			out.println("Registro modificado ex&iacute;tosamente");
		}

	}
	
	protected void deleteEncounter(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();
		
		String encounterId = req.getParameter("encounterId") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterId"), "UTF-8");
        if(!encounterId.equals("")){
		Encounter encounter = encounterService.getEncounter(encounterId,false);
		synchronized(encounter){
			if(encounter!=null){
				if(encounter.isCurrent()){
					User currentUser = userService.getCurrentUser();
					genericService.fill(currentUser, "roles");
					boolean isAdmin = false;
					for(com.googlecode.semrs.model.Role role : currentUser.getRoles()){
						if(role.getAuth().equals("ROLE_ADMIN")){
							isAdmin = true;
							break;
						}
					}
					Patient patient = patientService.getPatient(encounter.getPatientId(),false);
					synchronized(patient){
						if(patient!=null){
							User patientProvider = patient.getProvider();
							String patientProviderId = "";
							if(patientProvider!=null){
								patientProviderId = patientProvider.getUsername();
							}
							if((currentUser.getUsername().equals(encounter.getEncounterProvider())) 
									|| (currentUser.getUsername().equals(patientProviderId))
									|| (currentUser.getUsername().equals(encounter.getCreationUser()))
									|| isAdmin){
								patient.removeEncounter(encounter);
								patient.setLastEditDate(new Date());
								patient.setLastEditUser(currentUser.getUsername());
								patientService.savePatient(patient);
								encounterService.deleteEncounter(encounter);

							}else{
								errors.add(" este usuario no se encuentra autorizado para eliminar esta consulta");
							}
						}else{
							errors.add(" este paciente no existe");
						}
					}
				}else{
					errors.add(" esta consulta no puede ser eliminada");
				}

			}else{
				errors.add(" esta consulta no existe");
			}
		}

		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();

		if (errors != null && errors.size() > 0) {
			out.println("Ocurrier&oacute;n errores al guardar: ");
			for (String error : errors) {
				out.println(error);
			}
			return;

		} else {
			resp.setStatus(HttpServletResponse.SC_OK);
			out.println("Registro eliminado ex&iacute;tosamente");
		}
        }

	}
	
	
	protected void listCurrentEncounters(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {


		String patientId = req.getParameter("patientId") == null ? ""
				: URLDecoder.decode(req.getParameter("patientId"), "UTF-8");

		String encounterProvider = req.getParameter("encounterProvider") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterProvider"), "UTF-8");

		String encounterDate = req.getParameter("encounterDate") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterDate"), "UTF-8");



		Map queryParams = null;
		if (!patientId.equals("") || !encounterProvider.equals("") || !encounterDate.equals("")) {

			queryParams = new HashMap();
			queryParams.put("patientId", URLDecoder.decode(patientId, "UTF-8"));

			queryParams.put("encounterProvider", URLDecoder.decode(encounterProvider, "UTF-8"));

			queryParams.put("current", "true");

			queryParams.put("encounterDate", URLDecoder.decode(encounterDate, "UTF-8"));


		}

		String dir = (req.getParameter("dir") == null || req
				.getParameter("dir").equals("")) ? "DESC" : req
						.getParameter("dir");
		String sort = (req.getParameter("sort") == null || req.getParameter(
		"sort").equals("")) ? "patientId" : req.getParameter("sort");
		String start_param = (req.getParameter("start") == null || req
				.getParameter("start").equals("")) ? "0" : req
						.getParameter("start");
		String limit_param = (req.getParameter("limit") == null || req
				.getParameter("limit").equals("")) ? "10" : req
						.getParameter("limit");
		Collection<Encounter> currentEncounters =null;
		int total_count = 0;
		if (queryParams == null) {
			
			try{
				currentEncounters = encounterService.listEncounters(dir, sort, limit_param, start_param);
			}catch(Exception e){
				LOG.error("Error in listCurrentEncounters = " + e);
			}finally{
				try{
					total_count = encounterService.getEncounterCount();
				}catch(Exception e2){
					LOG.error("Error in listCurrentEncounters = " + e2);
				}
			}

		} else {
			
			try{
				currentEncounters = encounterService.listEncountersByQuery(queryParams, dir, sort,
						limit_param, start_param);
			}catch(Exception e){
				LOG.error("Error in listCurrentEncounters = " + e);
			}finally{
				try{
					total_count = encounterService.getEncounterCount(queryParams);
				}catch(Exception e2){
					LOG.error("Error in listCurrentEncounters = " + e2);
				}
			}

		}
		
		HttpSession session = req.getSession();
		if(session!=null && currentEncounters!=null){
			session.setAttribute("currentEncounters", currentEncounters);
		}

	
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat birthFormat = new SimpleDateFormat("dd/MM/yyyy");

		User currentUser = userService.getCurrentUser();
		genericService.fill(currentUser, "roles");
		boolean isAdmin = false;
		for(com.googlecode.semrs.model.Role role : currentUser.getRoles()){
			if(role.getAuth().equals("ROLE_ADMIN")){
				isAdmin = true;
				break;
			}
		}

		List items = new ArrayList();
		Patient patient = null;
		if (!patientId.equals("")){
			patient = patientService.getPatient(patientId,false);
		}
				
		for (Encounter encounter : currentEncounters) {
			JSONObject item = new JSONObject();
			if(patientId.equals("")){
			   patient = patientService.getPatient(encounter.getPatientId(),false);
			}
			if(patient!=null){
			User patientProvider = patient.getProvider();
			String patientProviderId = "";
			if(patientProvider!=null){
				patientProviderId = patientProvider.getUsername();
			}
			item.put("id", encounter.getId());
			item.put("patientId", encounter.getPatientId());
			item.put("patientName", patient.getName() + " " + patient.getLastName());
			User provider = null;
			if(encounter.getEncounterProvider()!=null){
			  provider = userService.getUserByUsername(encounter.getEncounterProvider(),false);
			}
			item.put("encounterProvider", provider == null ? "" : provider.getName() + " " + provider.getLastName());
			item.put("encounterDate", encounter.getEncounterDate() == null ? "" : format.format(encounter.getEncounterDate()));
			item.put("refferral", encounter.getRefferral() == null ? "" : encounter.getRefferral() );
			item.put("reason", encounter.getEncounterReason() == null ? "" : encounter.getEncounterReason());
			item.put("creationDate", encounter.getCreationDate() == null ? "" : format.format(encounter.getCreationDate()));
			item.put("creationUser", encounter.getCreationUser() == null ? "" : encounter.getCreationUser());
			if((currentUser.getUsername().equals(encounter.getEncounterProvider())) 
					|| (currentUser.getUsername().equals(patientProviderId))
					|| (currentUser.getUsername().equals(encounter.getCreationUser()))
					|| isAdmin){
				item.put("edit", "true");

			}else{
				item.put("edit", "false");
			}
			items.add(item);
			}
		}

		JSONObject feeds = new JSONObject();
		JSONObject response = new JSONObject();
		JSONObject value = new JSONObject();
		feeds.put("response", response);
		response.put("value", value);
		value.put("items", new JSONArray(items));
		value.put("total_count", Integer.toString(total_count));
		value.put("version", new Long(1));

		resp.setContentType("application/json; charset=utf-8");
		Writer w = new OutputStreamWriter(resp.getOutputStream(), "utf-8");
		w.write(feeds.toString());
		w.close();
		resp.setStatus(HttpServletResponse.SC_OK);

	}
	
	
	protected void listPreviousEncounters(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {


		String patientId = req.getParameter("patientId") == null ? ""
				: URLDecoder.decode(req.getParameter("patientId"), "UTF-8");

		String encounterProvider = req.getParameter("encounterProvider") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterProvider"), "UTF-8");

		String encounterDate = req.getParameter("encounterDate") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterDate"), "UTF-8");



		Map queryParams = null;
		if (!patientId.equals("") || !encounterProvider.equals("") || !encounterDate.equals("")) {

			queryParams = new HashMap();
			queryParams.put("patientId", URLDecoder.decode(patientId, "UTF-8"));

			queryParams.put("encounterProvider", URLDecoder.decode(encounterProvider, "UTF-8"));

			queryParams.put("current", "false");

			queryParams.put("encounterDate", URLDecoder.decode(encounterDate, "UTF-8"));


		}

		String dir = (req.getParameter("dir") == null || req
				.getParameter("dir").equals("")) ? "DESC" : req
						.getParameter("dir");
		String sort = (req.getParameter("sort") == null || req.getParameter(
		"sort").equals("")) ? "patientId" : req.getParameter("sort");
		String start_param = (req.getParameter("start") == null || req
				.getParameter("start").equals("")) ? "0" : req
						.getParameter("start");
		String limit_param = (req.getParameter("limit") == null || req
				.getParameter("limit").equals("")) ? "10" : req
						.getParameter("limit");
		
		Collection<Encounter> previousEncounters = null;
		int total_count = 0;
		if (queryParams == null) {
			try{
				previousEncounters = encounterService.listEncounters(dir, sort, limit_param, start_param);
			}catch(Exception e){
				LOG.error("Error in listPreviousEncounters = " + e);
			}finally{
				try{
					total_count = encounterService.getEncounterCount();
				}catch(Exception e2){
					LOG.error("Error in listPreviousEncounters = " + e2);
				}
			}

		} else {
			try{
				previousEncounters = encounterService.listEncountersByQuery(queryParams, dir, sort,
						limit_param, start_param);
			}catch(Exception e){
				LOG.error("Error in listPreviousEncounters = " + e);
			}finally{
				try{
					total_count = encounterService.getEncounterCount(queryParams);
				}catch(Exception e2){
					LOG.error("Error in listPreviousEncounters = " + e2);
				}
			}
		}

		HttpSession session = req.getSession();
		if(session!=null && previousEncounters!=null){
			session.setAttribute("previousEncounters", previousEncounters);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat birthFormat = new SimpleDateFormat("dd/MM/yyyy");


		List items = new ArrayList();
		Patient patient = null;
		if (!patientId.equals("")){
			patient = patientService.getPatient(patientId,false);
		}
		for (Encounter encounter : previousEncounters) {
			JSONObject item = new JSONObject();
			if(patientId.equals("")){
				   patient = patientService.getPatient(encounter.getPatientId(),false);
				}
			if(patient!=null){
				item.put("id", encounter.getId());
				item.put("patientId", encounter.getPatientId());
				item.put("patientName", patient.getName() + " " + patient.getLastName());
				User provider = null;
				if(encounter.getEncounterProvider()!=null){
				  provider = userService.getUserByUsername(encounter.getEncounterProvider(),false);
				}
				item.put("encounterProvider", provider == null ? "" : provider.getName() + " " + provider.getLastName());
				item.put("encounterDate", encounter.getEncounterDate() == null ? "" : format.format(encounter.getEncounterDate()));
				item.put("refferral", encounter.getRefferral() == null ? "" : encounter.getRefferral());
				item.put("reason", encounter.getEncounterReason() == null ? "" : encounter.getEncounterReason());
				item.put("creationDate", encounter.getCreationDate() == null ? "" : format.format(encounter.getCreationDate()));
				item.put("creationUser", encounter.getCreationUser() == null ? "" : encounter.getCreationUser());
				item.put("endDate", encounter.getEndDate() == null ? "" : format.format(encounter.getEndDate()));
				item.put("lastEditDate", encounter.getLastEditDate() == null? "" : format.format(encounter.getLastEditDate()));
				item.put("lastEditUser", encounter.getLastEditUser() == null? "" : encounter.getLastEditUser());
				items.add(item);
			}
		}

		JSONObject feeds = new JSONObject();
		JSONObject response = new JSONObject();
		JSONObject value = new JSONObject();
		feeds.put("response", response);
		response.put("value", value);
		value.put("items", new JSONArray(items));
		value.put("total_count", Integer.toString(total_count));
		value.put("version", new Long(1));

		resp.setContentType("application/json; charset=utf-8");
		Writer w = new OutputStreamWriter(resp.getOutputStream(), "utf-8");
		w.write(feeds.toString());
		w.close();
		resp.setStatus(HttpServletResponse.SC_OK);

	}
	
	
	
	protected void listEncounters(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {


		String patientId = req.getParameter("patientId") == null ? ""
				: URLDecoder.decode(req.getParameter("patientId"), "UTF-8");

		String encounterProvider = req.getParameter("encounterProvider") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterProvider"), "UTF-8");

		String encounterDateFrom = req.getParameter("encounterDateFrom") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterDateFrom"), "UTF-8");
		
		String encounterDateTo = req.getParameter("encounterDateTo") == null ? ""
				: URLDecoder.decode(req.getParameter("encounterDateTo"), "UTF-8");
		
		String endDateFrom = req.getParameter("endDateFrom") == null ? ""
				: URLDecoder.decode(req.getParameter("endDateFrom"), "UTF-8");
		
		String endDateTo = req.getParameter("endDateTo") == null ? ""
				: URLDecoder.decode(req.getParameter("endDateTo"), "UTF-8");
		
		String creationDateFrom = req.getParameter("creationDateFrom") == null ? ""
				: URLDecoder.decode(req.getParameter("creationDateFrom"), "UTF-8");
		
		String creationDateTo = req.getParameter("creationDateTo") == null ? ""
				: URLDecoder.decode(req.getParameter("creationDateTo"), "UTF-8");
		
		String current = req.getParameter("current") == null ? "true"
				: URLDecoder.decode(req.getParameter("current"), "UTF-8").equals("optional")?"":URLDecoder.decode(req.getParameter("current"), "UTF-8");



		Map queryParams = null;
		if (!patientId.equals("") || !encounterProvider.equals("") 
				|| !encounterDateFrom.equals("") || !encounterDateTo.equals("")
				|| !endDateFrom.equals("") || !endDateTo.equals("") 
				|| !creationDateFrom.equals("") || !creationDateTo.equals("")
				|| !current.equals("") ) {

			queryParams = new HashMap();
			queryParams.put("patientId", URLDecoder.decode(patientId, "UTF-8"));
			if(encounterProvider.equals("currentUser")){
				queryParams.put("encounterProvider", userService.getCurrentUser().getUsername());
			}else{
                queryParams.put("encounterProvider", URLDecoder.decode(encounterProvider, "UTF-8"));
			}
			queryParams.put("current", URLDecoder.decode(current, "UTF-8"));

			queryParams.put("encounterDateFrom", URLDecoder.decode(encounterDateFrom, "UTF-8"));
			
			queryParams.put("encounterDateTo", URLDecoder.decode(encounterDateTo, "UTF-8"));
			
			queryParams.put("endDateFrom", URLDecoder.decode(endDateFrom, "UTF-8"));
			
			queryParams.put("endDateTo", URLDecoder.decode(endDateTo, "UTF-8"));
			
            queryParams.put("creationDateFrom", URLDecoder.decode(creationDateFrom, "UTF-8"));
			
			queryParams.put("creationDateTo", URLDecoder.decode(creationDateTo, "UTF-8"));


		}

		String dir = (req.getParameter("dir") == null || req
				.getParameter("dir").equals("")) ? "DESC" : req
						.getParameter("dir");
		String sort = (req.getParameter("sort") == null || req.getParameter(
		"sort").equals("")) ? "encounterDate" : req.getParameter("sort");
		String start_param = (req.getParameter("start") == null || req
				.getParameter("start").equals("")) ? "0" : req
						.getParameter("start");
		String limit_param = (req.getParameter("limit") == null || req
				.getParameter("limit").equals("")) ? "10" : req
						.getParameter("limit");
		
		Collection<Encounter> encounterList = null;
		int total_count = 0;
		if (queryParams == null) {
			

			encounterList = encounterService.listEncounters(dir, sort, limit_param, start_param);

			total_count = encounterService.getEncounterCount();
			

		} else {
			

			encounterList = encounterService.listEncountersByQuery(queryParams, dir, sort,
					limit_param, start_param);

			total_count = encounterService.getEncounterCount(queryParams);


		}
		
		HttpSession session = req.getSession();
		if(session!=null && encounterList!=null){
			session.setAttribute("encounterList", encounterList);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		User currentUser = userService.getCurrentUser();
		genericService.fill(currentUser, "roles");
		boolean isAdmin = false;
		for(com.googlecode.semrs.model.Role role : currentUser.getRoles()){
			if(role.getAuth().equals("ROLE_ADMIN")){
				isAdmin = true;
				break;
			}
		}

		Patient patient = null;
		if (!patientId.equals("")){
			patient = patientService.getPatient(patientId,false);
		}
		User provider = null;
		if(!encounterProvider.equals("")){
			provider = userService.getUserByUsername(encounterProvider,false);
		}
		
		List items = new ArrayList();
	
		for (Encounter encounter : encounterList) {
			JSONObject item = new JSONObject();
			if (patientId.equals("")){
			 patient = patientService.getPatient(encounter.getPatientId(),false);
			}
			if(patient!=null){
			User patientProvider = patient.getProvider();
			String patientProviderId = "";
			if(patientProvider!=null){
				patientProviderId = patientProvider.getUsername();
			}
			item.put("id", encounter.getId());
			item.put("patientId", encounter.getPatientId());
			item.put("patientName", patient.getName() + " " + patient.getLastName());
			if(encounter.getEncounterProvider()!=null && encounterProvider.equals("")){
			  provider = userService.getUserByUsername(encounter.getEncounterProvider(),false);
			}
			item.put("encounterProvider", provider == null ? "" : provider.getName() + " " + provider.getLastName());
			item.put("encounterDate", encounter.getEncounterDate() == null ? "" : format.format(encounter.getEncounterDate()));
			item.put("refferral", encounter.getRefferral() == null ? "" : encounter.getRefferral() );
			item.put("reason", encounter.getEncounterReason() == null ? "" : encounter.getEncounterReason());
			item.put("creationDate", encounter.getCreationDate() == null ? "" : format.format(encounter.getCreationDate()));
			item.put("creationUser", encounter.getCreationUser() == null ? "" : encounter.getCreationUser());
			if(((currentUser.getUsername().equals(encounter.getEncounterProvider())) 
					|| (currentUser.getUsername().equals(patientProviderId))
					|| (currentUser.getUsername().equals(encounter.getCreationUser()))
					|| isAdmin) && encounter.isCurrent()){
				item.put("edit", "true");

			}else{
				item.put("edit", "false");
			}
			item.put("endDate", encounter.getEndDate() == null ? "" : format.format(encounter.getEndDate()));
			item.put("finalized", String.valueOf(encounter.isFinalized()));
			item.put("lastEditDate", encounter.getLastEditDate() == null ? "" : format.format(encounter.getLastEditDate()));
			item.put("lastEditUser", encounter.getLastEditUser()== null ? "" : encounter.getLastEditUser());
			items.add(item);
			}
		}

		JSONObject feeds = new JSONObject();
		JSONObject response = new JSONObject();
		JSONObject value = new JSONObject();
		feeds.put("response", response);
		response.put("value", value);
		value.put("items", new JSONArray(items));
		value.put("total_count", Integer.toString(total_count));
		value.put("version", new Long(1));

		resp.setContentType("application/json; charset=utf-8");
		Writer w = new OutputStreamWriter(resp.getOutputStream(), "utf-8");
		w.write(feeds.toString());
		w.close();
		resp.setStatus(HttpServletResponse.SC_OK);

	}
	
	
	
	
	protected void exportEncounters(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		String current = req.getParameter("current") == null ? ""
				: URLDecoder.decode(req.getParameter("current"), "UTF-8");

		String searchList = req.getParameter("searchList");
				

		if(current.equals("true")){
			
			HttpSession session = req.getSession();
			Collection<Encounter> currentEncounters  = null;
			if(session!=null){
				
				currentEncounters = (Collection<Encounter>)session.getAttribute("currentEncounters");
			}

			if (currentEncounters != null) {
				try {
					SimpleDateFormat format = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm:ss");

					ArrayList headers = new ArrayList();
					headers.add("C.I Paciente");
					headers.add("Nombres Paciente");
					headers.add("Médico tratante");
					headers.add("Fecha de Consulta");
					headers.add("Referido De");
					headers.add("Motivo");
					headers.add("Fecha de Creación");
					headers.add("Usuario Creación");

					ArrayList values = new ArrayList();
					for (Encounter encounter : currentEncounters) {
						Map encounterMap = new TreeMap();
						Patient patient = patientService.getPatient(encounter.getPatientId(), false);
						if(patient!=null){
							encounterMap.put(1, encounter.getPatientId());
							encounterMap.put(2, patient.getName() + " " + patient.getLastName());
							User provider = null;
							if(encounter.getEncounterProvider()!=null){
							  provider = userService.getUserByUsername(encounter.getEncounterProvider(),false);
							}
							encounterMap.put(3, provider == null ? "" : provider.getName() + " " + provider.getLastName());
							encounterMap.put(4, encounter.getEncounterDate() == null ? "" : format.format(encounter.getEncounterDate()));
							encounterMap.put(5, encounter.getRefferral() == null ? "" :  encounter.getRefferral());
							encounterMap.put(6, encounter.getEncounterReason() == null ? "" :  encounter.getEncounterReason());
							encounterMap.put(7, encounter.getCreationDate() == null ? "" : format.format(encounter.getCreationDate()));
							encounterMap.put(8, encounter.getCreationUser() == null ? "" : encounter.getCreationUser());
							values.add(encounterMap);
						}
					}

					resp.setContentType("application/vnd.ms-excel");
					resp.setHeader("Content-Disposition",
					"attachment; filename=\"consultas.xls\"");
					out = resp.getOutputStream();
					ExcelExporter.export(values, headers, "Consultas")
					.write(out);
				} catch (Exception e) {
					LOG.error("Error in exportEncounters = " + e);
				} finally {
					if (out != null)
						out.close();
				}
			}
			
		}else if(current.equals("false")){
			HttpSession session = req.getSession();
			Collection<Encounter> previousEncounters = null;
			if(session!=null){
				previousEncounters = (Collection<Encounter>)session.getAttribute("previousEncounters");
			}
			
			if (previousEncounters != null) {
				try {
					SimpleDateFormat format = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm:ss");

					ArrayList headers = new ArrayList();
					headers.add("C.I Paciente");
					headers.add("Nombres Paciente");
					headers.add("Médico tratante");
					headers.add("Fecha de Consulta");
					headers.add("Referido De");
					headers.add("Motivo");
					headers.add("Fecha de Creación");
					headers.add("Usuario Creación");
					headers.add("Fecha Fín");
					headers.add("Fecha Última Modificación");
					headers.add("Usuario Última Modificación");

					ArrayList values = new ArrayList();
					for (Encounter encounter : previousEncounters) {
						Map encounterMap = new TreeMap();
						Patient patient = patientService.getPatient(encounter.getPatientId(), false);
						if(patient!=null){
							encounterMap.put(1, encounter.getPatientId());
							encounterMap.put(2, patient.getName() + " " + patient.getLastName());
							User provider = null;
							if(encounter.getEncounterProvider()!=null){
							  provider = userService.getUserByUsername(encounter.getEncounterProvider(),false);
							}
							encounterMap.put(3, provider == null ? "" : provider.getName() + " " + provider.getLastName());
							encounterMap.put(4, encounter.getEncounterDate() == null ? "" : format.format(encounter.getEncounterDate()));
							encounterMap.put(5, encounter.getRefferral() == null ? "" :  encounter.getRefferral());
							encounterMap.put(6, encounter.getEncounterReason() == null ? "" :  encounter.getEncounterReason());
							encounterMap.put(7, encounter.getCreationDate() == null ? "" : format.format(encounter.getCreationDate()));
							encounterMap.put(8, encounter.getCreationUser() == null ? "" : encounter.getCreationUser());
							encounterMap.put(9, encounter.getEndDate() == null ? "" : format.format(encounter.getEndDate()));
							encounterMap.put(10, encounter.getLastEditDate() == null ? "" : format.format(encounter.getLastEditDate()));
							encounterMap.put(11,encounter.getLastEditUser() == null ? "" : encounter.getLastEditUser());
							values.add(encounterMap);
						}
					}

					resp.setContentType("application/vnd.ms-excel");
					resp.setHeader("Content-Disposition",
					       "attachment; filename=\"consultas.xls\"");
					out = resp.getOutputStream();
					ExcelExporter.export(values, headers, "Consultas")
					.write(out);
				} catch (Exception e) {
					LOG.error("Error in exportEncounters= " + e);
				} finally {
					if (out != null)
						out.close();
				}
			}
				
		}
	
		if(searchList!=null){
			
			Collection<Encounter> encounterList = null;
			HttpSession session = req.getSession();
			if(session!=null){
				encounterList = (Collection<Encounter>) session.getAttribute("encounterList");
			}
			
			if(encounterList!=null){
			try {
				SimpleDateFormat format = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");

				ArrayList headers = new ArrayList();
				headers.add("C.I Paciente");
				headers.add("Nombres Paciente");
				headers.add("Médico tratante");
				headers.add("Fecha de Consulta");
				headers.add("Referido De");
				headers.add("Motivo");
				headers.add("Fecha de Creación");
				headers.add("Usuario Creación");
				headers.add("Fecha Fín");
				headers.add("Fecha Última Modificación");
				headers.add("Usuario Última Modificación");

				ArrayList values = new ArrayList();
				for (Encounter encounter : encounterList) {
					Map encounterMap = new TreeMap();
					Patient patient = patientService.getPatient(encounter.getPatientId(), false);
					if(patient!=null){
						encounterMap.put(1, encounter.getPatientId());
						encounterMap.put(2, patient.getName() + " " + patient.getLastName());
						User provider = null;
						if(encounter.getEncounterProvider()!=null){
						  provider = userService.getUserByUsername(encounter.getEncounterProvider(),false);
						}
						encounterMap.put(3, provider == null ? "" : provider.getName() + " " + provider.getLastName());
						encounterMap.put(4, encounter.getEncounterDate() == null ? "" : format.format(encounter.getEncounterDate()));
						encounterMap.put(5, encounter.getRefferral() == null ? "" :  encounter.getRefferral());
						encounterMap.put(6, encounter.getEncounterReason() == null ? "" :  encounter.getEncounterReason());
						encounterMap.put(7, encounter.getCreationDate() == null ? "" : format.format(encounter.getCreationDate()));
						encounterMap.put(8, encounter.getCreationUser() == null ? "" : encounter.getCreationUser());
						encounterMap.put(9, encounter.getEndDate() == null ? "" : format.format(encounter.getEndDate()));
						encounterMap.put(10, encounter.getLastEditDate() == null ? "" : format.format(encounter.getLastEditDate()));
						encounterMap.put(11,encounter.getLastEditUser() == null ? "" : encounter.getLastEditUser());
						values.add(encounterMap);
					}
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				       "attachment; filename=\"consultas.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Consultas")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportEncounters= " + e);
			} finally {
				if (out != null)
					out.close();
			}
			}
		}
			
		
	}
	
	protected void getEncounter(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		final String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		final String edit = req.getParameter("edit") == null ? ""
				: URLDecoder.decode(req.getParameter("edit"), "UTF-8");

				Encounter encounter = encounterService.getEncounter(id, false);
				List dataItems = new ArrayList();
				try {
					JSONObject data = new JSONObject();
					if (encounter != null) {
						Patient patient = null;
						if (!encounter.isCurrent() && edit.equals("true")) {
							data.put("id", encounter.getId());
							data.put("patientId","");
							data.put("patientWeight", "");
							data.put("patientWeightUnits", "");
							data.put("patientHeight", "");
							data.put("patientHeightUnits", "");
							data.put("patientName", "");
							data.put("patientLastName", "");
							data.put("patientSex", "");
							data.put("patientAge", "");
							data.put("patientAgeH","");
							data.put("encounterRefferral", "");
							data.put("encounterReason", "");
							data.put("background", "");
							data.put("toxicHabits", "");
							data.put("toxicHabitsDesc", "");
							data.put("loadSuccess", "true");
							data.put("editSuccess", "false");
							data.put("loadPatientSuccess", "true");
						}else if(encounter.isCurrent()){
							data.put("id", encounter.getId()==null?"":encounter.getId());
							if(encounter.getPatientId()!=null && !encounter.getPatientId().equals("") ){
								patient = patientService.getPatient(encounter.getPatientId(), false);
							}
							if(patient!=null && !patient.isVoided()){
								data.put("patientId", patient.getId());
								data.put("patientWeight", patient.getWeight());
								data.put("patientWeightUnits", patient.getWeightUnits()==null?"Kilogramos":patient.getWeightUnits());
								data.put("patientHeight", patient.getHeight());
								data.put("patientHeightUnits", patient.getHeightUnits()==null?"Metros":patient.getHeightUnits());
								data.put("patientName", patient.getName()==null?"":patient.getName());
								data.put("patientLastName", patient.getLastName()==null?"":patient.getLastName());
								data.put("patientSex", patient.getSex()==null?"":patient.getSex().startsWith("M")?"Masculino":"Femenino");
								if(patient.getBirthDate() == null || patient.getBirthDate().equals("")){
									data.put("patientAge","");
									data.put("patientAgeH","");
								}else{
									Map ageMap = Util.getAgeInYearsDaysOrMonths(patient.getBirthDate());
									if(Integer.valueOf(String.valueOf(ageMap.get("years"))) > 1){
										data.put("patientAge", String.valueOf(Util.getAge(patient.getBirthDate())) + " Años");	
									}else{
										data.put("patientAge", String.valueOf(ageMap.get("years")) + " Años " +  String.valueOf(ageMap.get("months")) +  " Meses " + String.valueOf(ageMap.get("days")) + " Dias");
									}
									data.put("patientAgeH",String.valueOf(Util.getAge(patient.getBirthDate())));
								}
								
								data.put("encounterRefferral", encounter.getRefferral()==null?"":encounter.getRefferral());
								data.put("encounterReason", encounter.getEncounterReason()==null?"":encounter.getEncounterReason());
								data.put("background", encounter.getBackground()==null?"":encounter.getBackground());
								data.put("toxicHabits", encounter.isToxicHabits()?"Si":"No");
								data.put("toxicHabitsDesc", encounter.getToxicHabitsDesc()==null?"":encounter.getToxicHabitsDesc());
								data.put("loadSuccess", "true");
								data.put("editSuccess", "true");
								data.put("loadPatientSuccess", "true");
							}else{
								data.put("patientId","");
								data.put("patientWeight", "");
								data.put("patientWeightUnits", "");
								data.put("patientHeight", "");
								data.put("patientHeightUnits", "");
								data.put("patientName", "");
								data.put("patientLastName", "");
								data.put("patientSex", "");
								data.put("patientAge", "");
								data.put("patientAgeH","");
								data.put("encounterRefferral", "");
								data.put("encounterReason", "");
								data.put("background", "");
								data.put("toxicHabits", "");
								data.put("toxicHabitsDesc", "");
								data.put("loadSuccess", "true");
								data.put("editSuccess", "true");
								data.put("loadPatientSuccess", "false");
							}		
						}else if (!encounter.isCurrent() && edit.equals("false")) {
							data.put("id", encounter.getId()==null?"":encounter.getId());
							if(encounter.getPatientId()!=null && !encounter.getPatientId().equals("") ){
								patient = patientService.getPatient(encounter.getPatientId(), false);
							}
							if(patient!=null){
							data.put("patientId", patient.getId());
							data.put("patientWeight", patient.getWeight());
							data.put("patientWeightUnits", patient.getWeightUnits()==null?"Kilogramos":patient.getWeightUnits());
							data.put("patientHeight", patient.getHeight());
							data.put("patientHeightUnits", patient.getHeightUnits()==null?"Metros":patient.getHeightUnits());
							data.put("patientName", patient.getName()==null?"":patient.getName());
							data.put("patientLastName", patient.getLastName()==null?"":patient.getLastName());
							data.put("patientSex", patient.getSex()==null?"":patient.getSex().startsWith("M")?"Masculino":"Femenino");
							if(patient.getBirthDate() == null || patient.getBirthDate().equals("")){
								data.put("patientAge","");
								data.put("patientAgeH","");
							}else{
								Map ageMap = Util.getAgeInYearsDaysOrMonths(patient.getBirthDate());
								if(Integer.valueOf(String.valueOf(ageMap.get("years"))) > 1){
									data.put("patientAge", String.valueOf(Util.getAge(patient.getBirthDate())) + " Años");	
								}else{
									data.put("patientAge", String.valueOf(ageMap.get("years")) + " Años " +  String.valueOf(ageMap.get("months")) +  " Meses " + String.valueOf(ageMap.get("days")) + " Dias");
								}
								data.put("patientAgeH",String.valueOf(Util.getAge(patient.getBirthDate())));
							}
							data.put("encounterRefferral", encounter.getRefferral()==null?"":encounter.getRefferral());
							data.put("encounterReason", encounter.getEncounterReason()==null?"":encounter.getEncounterReason());
							data.put("background", encounter.getBackground()==null?"":encounter.getBackground());
							data.put("toxicHabits", encounter.isToxicHabits()?"Si":"No");
							data.put("toxicHabitsDesc", encounter.getToxicHabitsDesc()==null?"":encounter.getToxicHabitsDesc());
							data.put("loadSuccess", "true");
							data.put("editSuccess", "true");
							data.put("loadPatientSuccess", "true");
							}
						
						}
					}else{
						data.put("id", "");
						data.put("patientId","");
						data.put("patientWeight", "");
						data.put("patientWeightUnits", "");
						data.put("patientHeight", "");
						data.put("patientHeightUnits", "");
						data.put("patientName", "");
						data.put("patientLastName", "");
						data.put("patientToxicHabits", "");
						data.put("patientToxicHabitsDesc", "");
						data.put("patientSex", "");
						data.put("patientAge", "");
						data.put("patientAgeH","");
						data.put("encounterRefferral", "");
						data.put("encounterReason", "");
						data.put("background", "");
						data.put("toxicHabits", "");
						data.put("toxicHabitsDesc", "");
						data.put("loadSuccess", "false");
						data.put("editSuccess", "true");
						data.put("loadPatientSuccess", "true");
			
					}
					dataItems.add(data);
					JSONObject feeds = new JSONObject();
					feeds.put("data", new JSONArray(dataItems));
					feeds.put("success", true);
					resp.setContentType("application/json; charset=utf-8");
					Writer w = new OutputStreamWriter(resp.getOutputStream(),
					"utf-8");
					w.write(feeds.toString());
					w.close();
					resp.setStatus(HttpServletResponse.SC_OK);
				} catch (org.json.JSONException e) {
					throw new IOException();
				}

	}
	
	protected void saveEncounter(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		final String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		Encounter encounter = encounterService.getEncounter(id,false);
		ArrayList<String> errors = new ArrayList<String>();
		if(encounter!=null){
			if(!encounter.isCurrent()){
				errors.add(" esta consulta no puede ser modificada");
			}else{
				User currentUser = userService.getCurrentUser();
				boolean isAdmin = false;
				for(com.googlecode.semrs.model.Role role : currentUser.getRoles()){
					if(role.getAuth().equals("ROLE_ADMIN")){
						isAdmin = true;
						break;
					}
				}
				Patient patient = patientService.getPatient(encounter.getPatientId(),false);
				if(patient!=null){
					User patientProvider = patient.getProvider();
					String patientProviderId = "";
					if(patientProvider!=null){
						patientProviderId = patientProvider.getUsername();
					}
					if((currentUser.getUsername().equals(encounter.getEncounterProvider())) 
							|| (currentUser.getUsername().equals(patientProviderId))
							|| (currentUser.getUsername().equals(encounter.getCreationUser()))
							|| isAdmin){
						if(!patient.isVoided()){
							final String patientWeight = req.getParameter("patientWeight") == null ? "0"
									: URLDecoder.decode(req.getParameter("patientWeight"), "UTF-8");
							final String patientWeightUnits = req.getParameter("patientWeightUnits") == null ? ""
									: URLDecoder.decode(req.getParameter("patientWeightUnits"), "UTF-8");
							final String patientHeight = req.getParameter("patientHeight") == null ? "0"
									: URLDecoder.decode(req.getParameter("patientHeight"), "UTF-8");
							final String patientHeightUnits = req.getParameter("patientHeightUnits") == null ? ""
									: URLDecoder.decode(req.getParameter("patientHeightUnits"), "UTF-8");
							final String patientToxicHabits = req.getParameter("toxicHabits") == null ? "false"
									: URLDecoder.decode(req.getParameter("toxicHabits").startsWith("S")?"true":"false", "UTF-8");
							final String patientToxicHabitsDesc = req.getParameter("toxicHabitsDesc") == null ? ""
									: URLDecoder.decode(req.getParameter("toxicHabitsDesc"), "UTF-8");
							final String encounterReason = req.getParameter("encounterReason") == null ? ""
									: URLDecoder.decode(req.getParameter("encounterReason"), "UTF-8");
							final String background = req.getParameter("background") == null ? ""
									: URLDecoder.decode(req.getParameter("background"), "UTF-8");
							final String[] symptomsDiseases = req.getParameter("symptomsDiseases") == null ? new String[0]
							        : req.getParameter("symptomsDiseases").split(",");
							final String[] labTests = req.getParameter("labTests") == null ? new String[0]
							        : req.getParameter("labTests").split(",");
							final String[] procedures = req.getParameter("procedures") == null ? new String[0]
							        : req.getParameter("procedures").split(",");
							final String[] complications = req.getParameter("complications") == null ? new String[0]
							        : req.getParameter("complications").split(",");
							final String[] diagnoses = req.getParameter("diagnoses") == null ? new String[0]
							        : req.getParameter("diagnoses").split(",");
							final String[] treatments = req.getParameter("treatments") == null ? new String[0]
							        : req.getParameter("treatments").split(",");


							SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
							if(symptomsDiseases.length>0){
								for(String sd: symptomsDiseases){
									sd = URLDecoder.decode(sd, "UTF-8");
									String[] records = sd.split("@");
									if(records.length>0){
									String sdId = records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
									if(!sdId.equals("")){
									String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
									String sdType = records[2]==null?"":URLDecoder.decode(records[2], "UTF-8");
									String sdSeverity = (records[3]==null||records[3].equals("null"))?"":URLDecoder.decode(records[3], "UTF-8");
									if(!sdId.equals("") && !sdName.equals("") && !sdType.equals("")){
										SymptomRecord sr =  new SymptomRecord();
										sr.setId(encounter.getId()+sdId+sdType);
										sr.setEncounterId(encounter.getId());
										if(sdType.equals("Symptom")){
											sr.setSymptomId(sdId);
										}else if(sdType.equals("Disease")){
											sr.setDiseaseId(sdId);
										}
										sr.setType(sdType);
										sr.setName(sdName);
										String severity = "";
										if(sdSeverity.equals("Baja")){
											severity= "Low";
										}else if(sdSeverity.equals("Media")){
											severity = "Medium";
										}else if(sdSeverity.equals("Alta")){
											severity = "High";
										}else if(sdSeverity.equals("Muy Alta")){
											severity = "Very High";
										}		
						 
										sr.setSeverity(severity);
										encounter.addSymptomRecord(sr);
									}
									}
								  }
								}
							}

							if(labTests.length>0){
								for(String sd: labTests){
									sd = URLDecoder.decode(sd, "UTF-8");
									String[] records = sd.split("@");
									if(records.length>0){
									String sdId = records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
									if(!sdId.equals("")){
									String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
									String sdResult = records[2]==null?"":URLDecoder.decode(records[2], "UTF-8");
									String sdResultDesc = (records[3]==null||records[3].equals("null"))?"":URLDecoder.decode(records[3], "UTF-8");
									String sdDate = (records[4]==null||records[4].equals("null"))?"":URLDecoder.decode(records[4], "UTF-8");
									if(!sdId.equals("") && !sdName.equals("")){
										LabTestRecord ltRecord = new LabTestRecord();
										ltRecord.setId(encounter.getId()+sdId);
										ltRecord.setLabTestId(sdId);
										ltRecord.setEncounterId(encounter.getId());
										ltRecord.setName(sdName);
										if(!sdResult.equals("")){
											ltRecord.setResult(sdResult.startsWith("P")?true:false);
										}
										ltRecord.setResultDesc(sdResultDesc);
										if(!sdDate.equals("")){

											try {
												ltRecord.setTestDate(format.parse(sdDate));
											} catch (ParseException e) {
												LOG.error(" error parsing labtest date " + e);
											}
										}
										encounter.addLabTestRecord(ltRecord);
									}
									}
								  }
								}
							}
							if(procedures.length>0){
								for(String sd: procedures){
									sd = URLDecoder.decode(sd, "UTF-8");
									String[] records = sd.split("@");
									if(records.length>0){
									String sdId = records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
									if(!sdId.equals("")){
									String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
									String sdDate = (records[2]==null||records[2].equals("null"))?"":URLDecoder.decode(records[2], "UTF-8");
									if(!sdId.equals("") && !sdName.equals("")){
										ProcedureRecord prRecord = new ProcedureRecord();
										prRecord.setId(encounter.getId() + sdId);
										prRecord.setProcedureId(sdId);
										prRecord.setEncounterId(encounter.getId());
										prRecord.setName(sdName);
										if(!sdDate.equals("")){
											try {
												prRecord.setDate(format.parse(sdDate));
											} catch (ParseException e) {
												LOG.error(" error parsing procedure date " + e);
											}
										}
										encounter.addProcedureRecord(prRecord);
									}
									}
								  }
								}
							}

							if(complications.length>0){
								for(String sd: complications){
									sd = URLDecoder.decode(sd, "UTF-8");
									String[] records = sd.split("@");
									if(records.length>0){
									String sdId = records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
									if(!sdId.equals("")){
									String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
									String sdDate = (records[2]==null||records[2].equals("null"))?"":URLDecoder.decode(records[2], "UTF-8");
									if(!sdId.equals("") && !sdName.equals("")){
										ComplicationRecord compRecord = new ComplicationRecord();
										compRecord.setId(encounter.getId() + sdId);
										compRecord.setComplicationId(sdId);
										compRecord.setEncounterId(encounter.getId());
										compRecord.setName(sdName);
										if(!sdDate.equals("")){
											try {
												compRecord.setDate(format.parse(sdDate));
											} catch (ParseException e) {
												LOG.error(" error parsing complication date " + e);
											}
										}
										encounter.addComplicationRecord(compRecord);
									}
									}
								  }
								}
							}
							if(diagnoses.length>0){
								for(String sd: diagnoses){
									sd = URLDecoder.decode(sd, "UTF-8");
									String[] records = sd.split("@");
									if(records.length>0){
									String sdId = records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
									if(!sdId.equals("")){
									String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
									String sdSeverity = (records[2]==null||records[2].equals("null"))?"":URLDecoder.decode(records[2], "UTF-8");
									String sdType =(records[3]==null||records[3].equals("null"))?"":URLDecoder.decode(records[3], "UTF-8");
									if(!sdId.equals("") && !sdName.equals("")){
										Diagnosis diagnosis = new Diagnosis();
										diagnosis.setId(encounter.getId()+sdId);
										diagnosis.setDiseaseId(sdId);
										diagnosis.setEncounterId(encounter.getId());
										diagnosis.setName(sdName);
										String severity = "";
										if(sdSeverity.equals("Baja")){
											severity= "Low";
										}else if(sdSeverity.equals("Media")){
											severity = "Medium";
										}else if(sdSeverity.equals("Alta")){
											severity = "High";
										}else if(sdSeverity.equals("Muy Alta")){
											severity = "Very High";
										}		
						 
										diagnosis.setSeverity(severity);
										diagnosis.setType(sdType);
										diagnosis.setActive(true);
										encounter.addDiagnosis(diagnosis);
									}
									}
								  }
								}
							}

							if(treatments.length>0){
								for(String sd: treatments){
									sd = URLDecoder.decode(sd, "UTF-8");
									String[] records = sd.split("@");
									if(records.length>0){
									String sdId =  records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
									if(!sdId.equals("")){
									String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
									String sdInstructions = (records[2]==null||records[2].equals("null"))?"":URLDecoder.decode(records[2], "UTF-8");
									String sdStartDate= (records[3]==null||records[3].equals("null"))?"":URLDecoder.decode(records[3], "UTF-8");
									String sdEndDate = (records[4]==null||records[4].equals("null"))?"":URLDecoder.decode(records[4], "UTF-8");
									if(!sdId.equals("") && !sdName.equals("")){
										TreatmentRecord trRecord = new TreatmentRecord();
										trRecord.setId(encounter.getId()+sdId);
										trRecord.setDrugId(sdId);
										trRecord.setEncounterId(encounter.getId());
										trRecord.setName(sdName);
										trRecord.setActive(true);
										trRecord.setInstructions(sdInstructions);
										if(!sdStartDate.equals("")){
											try {
												trRecord.setStartDate(format.parse(sdStartDate));
											} catch (ParseException e) {
												LOG.error(" error parsing treatment start date " + e);
											}
										}
										if(!sdEndDate.equals("")){
											try {
												trRecord.setEndDate(format.parse(sdEndDate));
											} catch (ParseException e) {
												LOG.error(" error parsing treatment end date " + e);
											}
										}
										encounter.addTreatmentRecord(trRecord);
									}
									}
								  }
								}
							}


							patient.setWeight(Double.valueOf(patientWeight));
							patient.setWeightUnits(patientWeightUnits);
							patient.setHeight(Double.valueOf(patientHeight));
							patient.setHeightUnits(patientHeightUnits);
							Date endDate = new Date();
							patient.setLastEncounterDate(endDate);
							encounter.setEndDate(endDate);
							encounter.setBackground(background);
							encounter.setEncounterReason(encounterReason);
							encounter.setToxicHabits(Boolean.valueOf(patientToxicHabits));
							encounter.setToxicHabitsDesc(patientToxicHabitsDesc);
							encounter.setLastEditDate(new Date());
							encounter.setLastEditUser(userService.getCurrentUser().getUsername());
							encounter.setCurrent(false);
							try {
								encounterService.saveDeepEncounter(encounter);
								patientService.savePatient(patient);
							} catch (SaveOrUpdateException e) {
								errors.add(" ocurrio un error al guardar esta consulta");
							}

						}else{
							errors.add(" el paciente asociado a esta consulta se encuentra inactivo");
						}

					}else{
						errors.add(" usted no se encuentra autorizado para modificar esta consulta");
					}
				}else{
					errors.add(" el paciente asociado a esta consulta no existe");
				}

			}
		}else{
			errors.add(" consulta no existe");
		}


		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();

		if (errors != null && errors.size() > 0) {
			out.println("Ocurrier&oacute;n errores al guardar: ");
			for (String error : errors) {
				out.println(error);
			}
			return;

		} else {
			resp.setStatus(HttpServletResponse.SC_OK);
			out.println("Registro modificado ex&iacute;tosamente");
		}

	}
	
	protected void getEncounterRecords(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {
		
		String encounterId = req.getParameter("encounterId")==null?"":req.getParameter("encounterId");
		
		Encounter encounter = encounterService.getEncounter(encounterId,false);
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		List items = new ArrayList();
		if(encounter!=null){
			
			String recordType = req.getParameter("recordType")==null?"":req.getParameter("recordType");
			
			if(recordType.equals("diagnoses")){
				genericService.fill(encounter, "diagnoses");
				for(Diagnosis diagnosis : encounter.getDiagnoses()){
					JSONObject item = new JSONObject();
					item.put("id", diagnosis.getDiseaseId());
					item.put("name", diagnosis.getName());
					String severity = "";
					if(diagnosis.getSeverity()!=null){
						
						if(diagnosis.getSeverity().startsWith("L")){
							severity = "Baja";
						}else if(diagnosis.getSeverity().startsWith("M")){
							severity = "Media";
						}else if(diagnosis.getSeverity().startsWith("H")){
							severity = "Alta";
						}else if(diagnosis.getSeverity().startsWith("V")){
							severity = "Muy Alta";
						}
						
					}
					item.put("severity", severity);
					item.put("type", diagnosis.getType()==null?"":diagnosis.getType());
					items.add(item);
					
				}
				
			}else if(recordType.equals("symptomOrDiseases")){
				genericService.fill(encounter, "symptomRecords");
				for(SymptomRecord sr : encounter.getSymptomRecords()){
					JSONObject item = new JSONObject();
					if(sr.getType().equals("Symptom")){
					    item.put("id", sr.getSymptomId());
					    item.put("name", sr.getName());
					    item.put("type", "S&iacute;ntoma");
					}else if(sr.getType().equals("Disease")){
						item.put("id", sr.getDiseaseId());
						item.put("name", sr.getName());
						item.put("type", "Enfermedad");
					}
					item.put("htype", sr.getType());
					String severity = "";
                     if(sr.getSeverity()!=null){
						
						if(sr.getSeverity().startsWith("L")){
							severity = "Baja";
						}else if(sr.getSeverity().startsWith("M")){
							severity = "Media";
						}else if(sr.getSeverity().startsWith("H")){
							severity = "Alta";
						}else if(sr.getSeverity().startsWith("V")){
							severity = "Muy Alta";
						}
						
					}
					
					item.put("severity", severity);
					items.add(item);
				
			 }
			}else if(recordType.equals("labTests")){
				genericService.fill(encounter, "labTestRecords");
					for(LabTestRecord ltRecord : encounter.getLabTestRecords()){
						JSONObject item = new JSONObject();
						item.put("id", ltRecord.getLabTestId());
						item.put("name", ltRecord.getName());
						item.put("result", ltRecord.isResult()?"Positivo":"Negativo");
						item.put("resultDesc", ltRecord.getResultDesc());
						item.put("date", ltRecord.getTestDate()==null?"":format.format(ltRecord.getTestDate()));
						items.add(item);
					}
					
			}else if(recordType.equals("complications")){
				genericService.fill(encounter, "complicationRecords");
				for(ComplicationRecord compRecord : encounter.getComplicationRecords()){
					JSONObject item = new JSONObject();
					item.put("id", compRecord.getComplicationId());
					item.put("name", compRecord.getName());
					item.put("date", compRecord.getDate()==null?"":format.format(compRecord.getDate()));
					items.add(item);		
			     }
			}else if(recordType.equals("procedures")){
				genericService.fill(encounter, "procedureRecords");
				for(ProcedureRecord proRecord : encounter.getProcedureRecords()){
					JSONObject item = new JSONObject();
					item.put("id", proRecord.getProcedureId());
					item.put("name", proRecord.getName());
					item.put("date", proRecord.getDate()==null?"":format.format(proRecord.getDate()));
					items.add(item);		
			     }
			}else if(recordType.equals("treatments")){
				genericService.fill(encounter, "treatmentRecords");
				for(TreatmentRecord trRecord : encounter.getTreatmentRecords()){
					JSONObject item = new JSONObject();
					item.put("id", trRecord.getDrugId());
					item.put("name", trRecord.getName());
					item.put("startDate", trRecord.getStartDate()==null?"":format.format(trRecord.getStartDate()));
					item.put("endDate", trRecord.getEndDate()==null?"":format.format(trRecord.getEndDate()));
					item.put("instructions", trRecord.getInstructions()==null?"":trRecord.getInstructions());
					items.add(item);		
			     }
			}
			
	

		JSONObject feeds = new JSONObject();
		JSONObject response = new JSONObject();
		JSONObject value = new JSONObject();
		feeds.put("response", response);
		response.put("value", value);
		value.put("items", new JSONArray(items));
		value.put("total_count", Integer.toString(encounter.getDiagnoses().size()));
		value.put("version", new Long(1));

		resp.setContentType("application/json; charset=utf-8");
		Writer w = new OutputStreamWriter(resp.getOutputStream(), "utf-8");
		w.write(feeds.toString());
		w.close();
		resp.setStatus(HttpServletResponse.SC_OK);
		}
		
	}
	
	protected void getEncounterReport(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {
		
		String encounterId = req.getParameter("encounterId")==null?"":req.getParameter("encounterId");
		Encounter encounter = encounterService.getEncounter(encounterId,false);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat hourFormat = new SimpleDateFormat("EEE, dd MMM yyyy",Locale.getDefault());
		Patient patient  = patientService.getPatient(encounter.getPatientId(),false);
		Document document = new Document(PageSize.LETTER);
		document.setMargins(62, 62, 35, 35);
	
		
		Chunk patientImg = null;
		String filename = "";
		File nopic = null;
		URL resource = this.getClass().getResource("/patient/images/");
		File imageFolder = new File(resource.getFile());
		if(imageFolder!=null){
			File[] images = imageFolder.listFiles();
			if(images!=null && images.length>0){
				for(File file : images){
					String fileName = file.getName().substring(0,file.getName().lastIndexOf("."));
					if(fileName.equals(patient.getId())){
						filename = file.getPath();

					}else if(fileName.equals("nopic")){
						nopic = file;
					}
				}
			}
		}
		Image img = null;
		try {
			if(!filename.equals("")){
				img = Image.getInstance(filename);


			}else{
				img = Image.getInstance(nopic.getPath());
			}

		} catch (BadElementException e) {
			e.printStackTrace();
		}
		img.setBorder(Image.BOX);
		img.setBorderColor(Color.BLACK);
		img.setBorderWidth(1);
		img.scaleAbsolute(80, 80); 
		img.setAlignment(Paragraph.ALIGN_RIGHT | Image.TEXTWRAP);
		patientImg = new Chunk(img, 0, -15);
		Paragraph p = new Paragraph();
		p.add(new Phrase("ALCALDIA DEL MUNICIPIO VARGAS", FontFactory.getFont(FontFactory.TIMES, 13, Font.BOLD)));
		p.setAlignment(Paragraph.ALIGN_CENTER);
		Paragraph p1 = new Paragraph();
		p1.add(new Phrase("CENTRO INTEGRAL DE SALUD", FontFactory.getFont(FontFactory.TIMES, 13, Font.BOLD)));
		p1.setAlignment(Paragraph.ALIGN_CENTER);
		Paragraph p2 = new Paragraph();
		p2.add(new Phrase("HISTORIA CLINICA", FontFactory.getFont(FontFactory.TIMES, 13, Font.BOLD)));
		p2.setAlignment(Paragraph.ALIGN_CENTER);
		Paragraph p3 = new Paragraph();
		p3.setSpacingBefore(50);
		p3.add(patientImg);
		p3.setAlignment(Paragraph.ALIGN_RIGHT);
		Paragraph p4 = new Paragraph();
		p4.setAlignment(Paragraph.ALIGN_LEFT);
		p4.add(new Phrase("Apellidos: " , FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p4.add(new Phrase(patient.getLastName() , FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		p4.add(new Phrase("           Nombres: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p4.add(new Phrase(patient.getName(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		
		Paragraph p5 = new Paragraph();
		p5.setAlignment(Paragraph.ALIGN_LEFT);
		p5.add(new Phrase("Cédula de Identidad: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p5.add(new Phrase(patient.getId(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		
		Map ageMap = Util.getAgeInYearsDaysOrMonths(patient.getBirthDate());
		p5.add(new Phrase("     Edad: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		if(Integer.valueOf(String.valueOf(ageMap.get("years"))) > 1){
			p5.add(new Phrase(String.valueOf(Util.getAge(patient.getBirthDate())) + " Años",FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		}else{
			p5.add(new Phrase(String.valueOf(ageMap.get("years")) + " Años " +  String.valueOf(ageMap.get("months")) +  " Meses " + String.valueOf(ageMap.get("days")) + " Dias",FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		}
		Paragraph p6 = new Paragraph();
		p6.setAlignment(Paragraph.ALIGN_LEFT);
		p6.add(new Phrase("Sexo: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p6.add(new Phrase(patient.getSex().startsWith("M")?"Masculino":"Femenino",FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		
		p6.add(new Phrase("       Fecha de Nacimiento: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p6.add(new Phrase(format.format(patient.getBirthDate()),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		
		Paragraph p6a = new Paragraph();
		p6a.setAlignment(Paragraph.ALIGN_LEFT);
		p6a.add(new Phrase("Lugar de Nacimiento: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p6a.add(new Phrase(patient.getBirthPlace(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		


		Paragraph p7 = new Paragraph();
		p7.setAlignment(Paragraph.ALIGN_LEFT);
		p7.add(new Phrase("Dirección: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p7.add(new Phrase(patient.getAddress(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		
		Paragraph p8 = new Paragraph();
		p8.setAlignment(Paragraph.ALIGN_LEFT);
		p8.add(new Phrase("Telefóno: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p8.add(new Phrase(patient.getPhoneNumber(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		p8.add(new Phrase("    Móvil: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p8.add(new Phrase(patient.getMobile(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		String email = patient.getEmail()==null?"":patient.getEmail();
		p8.add(new Phrase("    Email: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p8.add(new Phrase(email,FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));

		Paragraph p9 = new Paragraph();
		p9.setAlignment(Paragraph.ALIGN_LEFT);
		p9.add(new Phrase("Fecha de Consulta: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p9.add(new Phrase(timeFormat.format(encounter.getEncounterDate()),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		p9.add(new Phrase("    Referido de: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p9.add(new Phrase(encounter.getRefferral(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		p9.add(new Phrase("    Mótivo de Consulta: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p9.add(new Phrase(encounter.getEncounterReason(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));

		User provider =  userService.getUserByUsername(encounter.getEncounterProvider(),false);
		Paragraph p10 = new Paragraph();
		p10.setAlignment(Paragraph.ALIGN_LEFT);
		p10.add(new Phrase("Médico Tratante: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p10.add(new Phrase(provider.getName() + " " + provider.getLastName(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));

		Paragraph p11 = new Paragraph();
		p11.setAlignment(Paragraph.ALIGN_LEFT);
		String toxicHabits = !encounter.isToxicHabits()?"No":encounter.getToxicHabitsDesc();
		p11.add(new Phrase("Habitos Tóxicos: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p11.add(new Phrase(toxicHabits ,FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));

		Paragraph p12 = new Paragraph();
		p12.setAlignment(Paragraph.ALIGN_LEFT);
		p12.add(new Phrase("Peso: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p12.add(new Phrase(patient.getWeight() + " " + patient.getWeightUnits(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		p12.add(new Phrase("     Altura: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p12.add(new Phrase(patient.getHeight() + " " + patient.getHeightUnits(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		p12.add(new Phrase("     Tipo de Sangre: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p12.add(new Phrase(patient.getBloodType(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));


		Paragraph p13 = new Paragraph();
		p13.setAlignment(Paragraph.ALIGN_LEFT);
		p13.add(new Phrase("Antecedentes/Observaciones:  ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
		p13.add(new Phrase(encounter.getBackground().replaceAll("\\<.*?\\>", ""),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
		
		p13.setSpacingAfter(25);

		PdfPTable symptomTable = new PdfPTable(4);   
		PdfPCell symptomCell = new PdfPCell(new Paragraph("Síntomas"));
		symptomCell.setColspan(4);  
		symptomCell.setBackgroundColor(Color.GRAY);
		symptomTable.addCell(symptomCell);      
		symptomTable.addCell("Código");   
		symptomTable.addCell("Nombre");
		symptomTable.addCell("Tipo");
		symptomTable.addCell("Severidad");
		symptomTable.setWidthPercentage(100);
		symptomTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		symptomTable.setSpacingAfter(25);

		genericService.fill(encounter, "symptomRecords");
		for(SymptomRecord sr : encounter.getSymptomRecords()){
			if(sr.getType().equals("Symptom")){
				symptomTable.addCell(sr.getSymptomId());
				symptomTable.addCell(sr.getName());
				symptomTable.addCell("Síntoma");
			}else if(sr.getType().equals("Disease")){
				symptomTable.addCell(sr.getDiseaseId());
				symptomTable.addCell(sr.getName());
				symptomTable.addCell("Enfermedad");
			}
			String severity = "";
			if(sr.getSeverity()!=null){

				if(sr.getSeverity().startsWith("L")){
					severity = "Baja";
				}else if(sr.getSeverity().startsWith("M")){
					severity = "Media";
				}else if(sr.getSeverity().startsWith("H")){
					severity = "Alta";
				}else if(sr.getSeverity().startsWith("V")){
					severity = "Muy Alta";
				}

			}

			symptomTable.addCell(severity);
		}


		PdfPTable labTestTable = new PdfPTable(5);   
		PdfPCell labTestCell = new PdfPCell(new Paragraph("Exámenes de Laboratorio"));
		labTestCell.setColspan(5);  
		labTestCell.setBackgroundColor(Color.GRAY);
		labTestTable.addCell(labTestCell);      
		labTestTable.addCell("Código");   
		labTestTable.addCell("Nombre");
		labTestTable.addCell("Resultado");
		labTestTable.addCell("Observaciones");
		labTestTable.addCell("Fecha");
		labTestTable.setWidthPercentage(100);
		labTestTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		labTestTable.setSpacingAfter(25);


		genericService.fill(encounter, "labTestRecords");
		for(LabTestRecord ltRecord : encounter.getLabTestRecords()){		
			labTestTable.addCell(ltRecord.getLabTestId());
			labTestTable.addCell(ltRecord.getName());
			labTestTable.addCell(ltRecord.isResult()?"Positivo":"Negativo");
			labTestTable.addCell(ltRecord.getResultDesc()==null?"":ltRecord.getResultDesc());
			labTestTable.addCell(ltRecord.getTestDate()==null?"":format.format(ltRecord.getTestDate()));

		}

		PdfPTable complicationTable = new PdfPTable(3);   
		PdfPCell complicationCell = new PdfPCell(new Paragraph("Complicaciones"));
		complicationCell.setColspan(3);   
		complicationCell.setBackgroundColor(Color.GRAY);
		complicationTable.addCell(complicationCell);      
		complicationTable.addCell("Código");   
		complicationTable.addCell("Nombre");
		complicationTable.addCell("Fecha");
		complicationTable.setWidthPercentage(100);
		complicationTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		complicationTable.setSpacingAfter(25);




		genericService.fill(encounter, "complicationRecords");
		for(ComplicationRecord compRecord : encounter.getComplicationRecords()){
			complicationTable.addCell(compRecord.getComplicationId());
			complicationTable.addCell(compRecord.getName());
			complicationTable.addCell(compRecord.getDate()==null?"":format.format(compRecord.getDate()));

		}


		PdfPTable procedureTable = new PdfPTable(3);   
		PdfPCell procedureCell = new PdfPCell(new Paragraph("Procedimientos/Operaciones"));
		procedureCell.setColspan(3);   
		procedureCell.setBackgroundColor(Color.GRAY);
		procedureTable.addCell(procedureCell);      
		procedureTable.addCell("Código");   
		procedureTable.addCell("Nombre");
		procedureTable.addCell("Fecha");
		procedureTable.setWidthPercentage(100);
		procedureTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		procedureTable.setSpacingAfter(25);



		genericService.fill(encounter, "procedureRecords");
		for(ProcedureRecord proRecord : encounter.getProcedureRecords()){
			procedureTable.addCell(proRecord.getProcedureId());
			procedureTable.addCell(proRecord.getName());
			procedureTable.addCell(proRecord.getDate()==null?"":format.format(proRecord.getDate()));	
		}


		PdfPTable diagnosisTable = new PdfPTable(4);   
		PdfPCell diagnosisCell = new PdfPCell(new Paragraph("Diagnósticos"));
		diagnosisCell.setColspan(4);   
		diagnosisCell.setBackgroundColor(Color.GRAY);
		diagnosisTable.addCell(diagnosisCell);      
		diagnosisTable.addCell("Código");   
		diagnosisTable.addCell("Nombre");
		diagnosisTable.addCell("Severidad");
		diagnosisTable.addCell("Tipo");
		diagnosisTable.setWidthPercentage(100);
		diagnosisTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		diagnosisTable.setSpacingAfter(25);

		genericService.fill(encounter, "diagnoses");
		for(Diagnosis diagnosis : encounter.getDiagnoses()){
			diagnosisTable.addCell(diagnosis.getDiseaseId());
			diagnosisTable.addCell(diagnosis.getName());
			String severity = "";
			if(diagnosis.getSeverity()!=null){

				if(diagnosis.getSeverity().startsWith("L")){
					severity = "Baja";
				}else if(diagnosis.getSeverity().startsWith("M")){
					severity = "Media";
				}else if(diagnosis.getSeverity().startsWith("H")){
					severity = "Alta";
				}else if(diagnosis.getSeverity().startsWith("V")){
					severity = "Muy Alta";
				}

			}
			diagnosisTable.addCell(severity);
			diagnosisTable.addCell(diagnosis.getType()==null?"":diagnosis.getType());


		}



		PdfPTable treatmentTable = new PdfPTable(5);   
		PdfPCell treatmentCell = new PdfPCell(new Paragraph("Tratamientos"));
		treatmentCell.setColspan(5);   
		treatmentCell.setBackgroundColor(Color.GRAY);
		treatmentTable.addCell(treatmentCell);      
		treatmentTable.addCell("Código");   
		treatmentTable.addCell("Nombre");
		treatmentTable.addCell("Fecha Inicio");
		treatmentTable.addCell("Fecha Fín");
		treatmentTable.addCell("Instrucciones");
		treatmentTable.setWidthPercentage(100);
		treatmentTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		treatmentTable.setSpacingAfter(25);


		genericService.fill(encounter, "treatmentRecords");
		for(TreatmentRecord trRecord : encounter.getTreatmentRecords()){
			treatmentTable.addCell(trRecord.getDrugId());
			treatmentTable.addCell(trRecord.getName());
			treatmentTable.addCell(trRecord.getStartDate()==null?"":format.format(trRecord.getStartDate()));
			treatmentTable.addCell(trRecord.getEndDate()==null?"":format.format(trRecord.getEndDate()));
			treatmentTable.addCell(trRecord.getInstructions()==null?"":trRecord.getInstructions());	
		}



		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new EndPage());
			//PdfWriter.getInstance(document, baos).setPageEvent(new EndPage());
		} catch (DocumentException e) {
			LOG.error(e);
		}
	

		document.open();




		try {
			document.add(p);
			document.add(p1);
			document.add(p2);
			document.add(p3);
			document.add(p4);
			document.add(p5);
			document.add(p6);
			document.add(p6a);
			document.add(p7);
			document.add(p8);
			document.add(p9);
			document.add(p10);
			document.add(p11);
			document.add(p12);
			document.add(p13);
			if(encounter.getSymptomRecords().size()>0){
		    	document.add(symptomTable);
			}
			if(encounter.getLabTestRecords().size()>0){
			document.add(labTestTable);
			}
			if(encounter.getComplicationRecords().size()>0){
			document.add(complicationTable);
			}
			if(encounter.getProcedureRecords().size()>0){
			document.add(procedureTable);
			}
			if(encounter.getDiagnoses().size()>0){
			document.add(diagnosisTable);
			}
			if(encounter.getTreatmentRecords().size()>0){
			document.add(treatmentTable);
			}

		} catch (DocumentException e1) {
			LOG.error(e1);
		}
		document.close();

		resp.setHeader("Expires", "0");
		resp.setHeader("Cache-Control",
		"must-revalidate, post-check=0, pre-check=0");
		resp.setHeader("Pragma", "public");
		resp.setContentType("application/pdf");
		resp.setHeader("Content-Disposition",
				"attachment; filename=\"consulta_"+patient.getId()+"("+hourFormat.format(encounter.getEndDate())+").pdf\"");
		resp.setContentLength(baos.size());
		ServletOutputStream out = resp.getOutputStream();
		baos.writeTo(out);
		out.flush();


	}
	
	protected void getDiagnosis(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {
		
	
		
		final String patientAge = req.getParameter("patientAgeH") == null ? "0"
				: URLDecoder.decode(req.getParameter("patientAgeH"), "UTF-8");
		final String patientSex = req.getParameter("patientSex") == null ? ""
				: URLDecoder.decode(req.getParameter("patientSex"), "UTF-8");
		final String toxicHabits = req.getParameter("toxicHabits") == null ? "false"
				: URLDecoder.decode(req.getParameter("toxicHabits").startsWith("S")?"true":"false", "UTF-8");
		final String[] symptomsDiseases = req.getParameter("symptomsDiseases") == null ? new String[0]
		        : req.getParameter("symptomsDiseases").split(",");
		final String[] labTests = req.getParameter("labTests") == null ? new String[0]
		        : req.getParameter("labTests").split(",");
		final String[] procedures = req.getParameter("procedures") == null ? new String[0]
		        : req.getParameter("procedures").split(",");
		final String[] complications = req.getParameter("complications") == null ? new String[0]
		        : req.getParameter("complications").split(",");
		final String[] treatments = req.getParameter("treatments") == null ? new String[0]
		        : req.getParameter("treatments").split(",");
		final String getPrediction = req.getParameter("getPrediction") == null ? ""
				: URLDecoder.decode(req.getParameter("getPrediction"), "UTF-8");
		
       
		Collection<SymptomRecord> symptomRecords = new ArrayList<SymptomRecord>();
		Collection<SymptomRecord> diseaseRecords = new ArrayList<SymptomRecord>();
		Collection<LabTestRecord> labTestRecords = new ArrayList<LabTestRecord>();
		Collection<ProcedureRecord> procedureRecords = new ArrayList<ProcedureRecord>();
		Collection<ComplicationRecord> complicationRecords = new ArrayList<ComplicationRecord>();
		Collection<TreatmentRecord> treatmentRecords = new ArrayList<TreatmentRecord>();



		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if(symptomsDiseases.length>0){
			for(String sd: symptomsDiseases){
				sd = URLDecoder.decode(sd, "UTF-8");
				String[] records = sd.split("@");
				if(records.length>0){
				String sdId = records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
				if(!sdId.equals("")){
				String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
				String sdType = records[2]==null?"":URLDecoder.decode(records[2], "UTF-8");
				String sdSeverity = records[3]==null?"":URLDecoder.decode(records[3], "UTF-8");
				if(!sdId.equals("") && !sdName.equals("") && !sdType.equals("")){
					SymptomRecord sr =  new SymptomRecord();
					String severity = "";
					if(!sdSeverity.equals("")){
						if(sdSeverity.equals("Baja")){
							severity = "Low";
						}else if(sdSeverity.equals("Media")){
							severity = "Medium";
						}else if(sdSeverity.equals("Alta")){
							severity = "High";
						}else if(sdSeverity.equals("Muy Alta")){
							severity = "Very High";
							
						}
					}
					if(sdType.equals("Symptom")){
						sr.setSymptomId(sdId);
						sr.setType(sdType);
						sr.setName(sdName);
						sr.setSeverity(severity);
						symptomRecords.add(sr);
					}else if(sdType.equals("Disease")){
						sr.setDiseaseId(sdId);
						sr.setType(sdType);
						sr.setName(sdName);
						sr.setSeverity(severity);
						diseaseRecords.add(sr);
					}
				}
				}
			  }
			}
		}

		if(labTests.length>0){
			for(String sd: labTests){
				sd = URLDecoder.decode(sd, "UTF-8");
				String[] records = sd.split("@");
				if(records.length>0){
				String sdId = records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
				if(!sdId.equals("")){
				String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
				String sdResult = records[2]==null?"":URLDecoder.decode(records[2], "UTF-8");
				String sdDate = records[3]==null?"":URLDecoder.decode(records[3], "UTF-8");
				if(!sdId.equals("") && !sdName.equals("")){
					LabTestRecord ltRecord = new LabTestRecord();
					ltRecord.setLabTestId(sdId);
					ltRecord.setName(sdName);
					if(!sdResult.equals("")){
						ltRecord.setResult(sdResult.startsWith("P")?true:false);
					}
					labTestRecords.add(ltRecord);
				}
				}
			  }
			}
		}
		if(procedures.length>0){
			for(String sd: procedures){
				sd = URLDecoder.decode(sd, "UTF-8");
				String[] records = sd.split("@");
				if(records.length>0){
				String sdId = records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
				if(!sdId.equals("")){
				String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
				String sdDate = records[2]==null?"":URLDecoder.decode(records[2], "UTF-8");
				if(!sdId.equals("") && !sdName.equals("")){
					ProcedureRecord prRecord = new ProcedureRecord();
					prRecord.setProcedureId(sdId);
					prRecord.setName(sdName);
					procedureRecords.add(prRecord);
				}
				}
			  }
			}
		}

		if(complications.length>0){
			for(String sd: complications){
				sd = URLDecoder.decode(sd, "UTF-8");
				String[] records = sd.split("@");
				if(records.length>0){
				String sdId = records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
				if(!sdId.equals("")){
				String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
				String sdDate = records[2]==null?"":URLDecoder.decode(records[2], "UTF-8");
				if(!sdId.equals("") && !sdName.equals("")){
					ComplicationRecord compRecord = new ComplicationRecord();
					compRecord.setComplicationId(sdId);
					compRecord.setName(sdName);
					complicationRecords.add(compRecord);
				}
				}
			  }
			}
		}
		if(treatments.length>0){
			for(String sd: treatments){
				sd = URLDecoder.decode(sd, "UTF-8");
				String[] records = sd.split("@");
				if(records.length>0){
				String sdId =  records[0]==null?"":URLDecoder.decode(records[0], "UTF-8");
				if(!sdId.equals("")){
				String sdName = records[1]==null?"":URLDecoder.decode(records[1], "UTF-8");
				String sdInstructions = records[2]==null?"":URLDecoder.decode(records[2], "UTF-8");
				String sdStartDate= records[3]==null?"":URLDecoder.decode(records[3], "UTF-8");
				String sdEndDate = records[4]==null?"":URLDecoder.decode(records[4], "UTF-8");
				if(!sdId.equals("") && !sdName.equals("")){
					TreatmentRecord trRecord = new TreatmentRecord();
					trRecord.setDrugId(sdId);
					trRecord.setName(sdName);
					trRecord.setActive(true);
					treatmentRecords.add(trRecord);
				}
				}
			  }
			}
		}
		
		Map params = new TreeMap();       
		params.put("patientAge",patientAge);
		params.put("patientSex",patientSex);
		params.put("toxicHabits",toxicHabits);
		params.put("symptoms",symptomRecords);
		params.put("diseases",diseaseRecords);
		params.put("labTests",labTestRecords);
		params.put("procedures",procedureRecords);
		params.put("complications",complicationRecords);
		params.put("treatments",treatmentRecords);
		
		Collection<DiagnosisContainer> diagnoses = null;
		if(getPrediction.equals("")){
		
		    diagnoses = encounterService.getAssistedDiagnosis(params);
		 
		}else{
			
			diagnoses = encounterService.getAssistedDiagnosisPrediction(params);
		}

		List items = new ArrayList();
		if(diagnoses!=null){

		for(DiagnosisContainer diagnosis : diagnoses){
			JSONObject item = new JSONObject();
			item.put("id", diagnosis.getDiseaseId());
			item.put("name", diagnosis.getDiseaseName());
			item.put("probability", diagnosis.getProbability());
			items.add(item);
					
		}
		
		
		JSONObject feeds = new JSONObject();
		JSONObject response = new JSONObject();
		JSONObject value = new JSONObject();
		feeds.put("response", response);
		response.put("value", value);
		value.put("items", new JSONArray(items));
		value.put("total_count", Integer.toString(diagnoses.size()));
		value.put("version", new Long(1));

		resp.setContentType("application/json; charset=utf-8");
		Writer w = new OutputStreamWriter(resp.getOutputStream(), "utf-8");
		w.write(feeds.toString());
		w.close();
		resp.setStatus(HttpServletResponse.SC_OK);
		
		}
	
		
		
	}
	
	protected void getLatestEncounterRecords(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {
		
		String patientId = req.getParameter("patientId")==null?"":req.getParameter("patientId");
		String limit = req.getParameter("limit")==null?"10":req.getParameter("limit");
		String recordType = req.getParameter("recordType")==null?"":req.getParameter("recordType");
		
		Map params = new HashMap();
		params.put("patientId",patientId);
		params.put("current","false");
		Collection<Encounter> patientEncounters = encounterService.listEncountersByQuery(params, "DESC", "lastEditDate", "2", "0");
		List items = new ArrayList();
		int counter = 0;
		String username = "";
        User provider = null;
		String providerName = "";
		for(Encounter encounter : patientEncounters){
		
			
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if(!username.equals(encounter.getEncounterProvider())){
         provider = userService.getUserByUsername(encounter.getEncounterProvider(),false);
		 providerName = provider.getName() + " " + provider.getLastName();	
		 username = encounter.getEncounterProvider();
		}
			
			if(recordType.equals("diagnoses")){
				genericService.fill(encounter, "diagnoses");
				for(Diagnosis diagnosis : encounter.getDiagnoses()){
					if(counter<=Integer.valueOf(limit)){
					JSONObject item = new JSONObject();
					item.put("id", diagnosis.getDiseaseId());
					item.put("name", diagnosis.getName());
					String severity = "";
					if(diagnosis.getSeverity()!=null){
						
						if(diagnosis.getSeverity().startsWith("L")){
							severity = "Baja";
						}else if(diagnosis.getSeverity().startsWith("M")){
							severity = "Media";
						}else if(diagnosis.getSeverity().startsWith("H")){
							severity = "Alta";
						}else if(diagnosis.getSeverity().startsWith("V")){
							severity = "Muy Alta";
						}
						
					}
					item.put("severity", severity);
					item.put("type", diagnosis.getType()==null?"":diagnosis.getType());
					item.put("encounterDate", format.format(encounter.getLastEditDate()));
					item.put("encounterProvider", providerName);
					item.put("encounterId", encounter.getId());
					items.add(item);
					counter+=1;
					}
					
				}
				
			}else if(recordType.equals("symptomOrDiseases")){
				genericService.fill(encounter, "symptomRecords");
				for(SymptomRecord sr : encounter.getSymptomRecords()){
					if(counter<=Integer.valueOf(limit)){
					JSONObject item = new JSONObject();
					if(sr.getType().equals("Symptom")){
					    item.put("id", sr.getSymptomId());
					    item.put("name", sr.getName());
					    item.put("type", "S&iacute;ntoma");
					}else if(sr.getType().equals("Disease")){
						item.put("id", sr.getDiseaseId());
						item.put("name", sr.getName());
						item.put("type", "Enfermedad");
					}
					item.put("htype", sr.getType());
					String severity = "";
                     if(sr.getSeverity()!=null){
						
						if(sr.getSeverity().startsWith("L")){
							severity = "Baja";
						}else if(sr.getSeverity().startsWith("M")){
							severity = "Media";
						}else if(sr.getSeverity().startsWith("H")){
							severity = "Alta";
						}else if(sr.getSeverity().startsWith("V")){
							severity = "Muy Alta";
						}
						
					}
					
					item.put("severity", severity);
					item.put("encounterDate", format.format(encounter.getLastEditDate()));
					item.put("encounterProvider", providerName);
					item.put("encounterId", encounter.getId());
					items.add(item);
					counter+=1;
					}
				
			 }
			}else if(recordType.equals("labTests")){
			     	genericService.fill(encounter, "labTestRecords");
					for(LabTestRecord ltRecord : encounter.getLabTestRecords()){
						if(counter<=Integer.valueOf(limit)){
						JSONObject item = new JSONObject();
						item.put("id", ltRecord.getLabTestId());
						item.put("name", ltRecord.getName());
						item.put("result", ltRecord.isResult()?"Positivo":"Negativo");
						item.put("date", ltRecord.getTestDate()==null?"":format.format(ltRecord.getTestDate()));
						item.put("encounterDate", format.format(encounter.getLastEditDate()));
						item.put("encounterProvider", providerName);
						item.put("encounterId", encounter.getId());
						items.add(item);
						counter+=1;
						}
					
					}
					
			}else if(recordType.equals("complications")){
				genericService.fill(encounter, "complicationRecords");
				for(ComplicationRecord compRecord : encounter.getComplicationRecords()){
					if(counter<=Integer.valueOf(limit)){
					JSONObject item = new JSONObject();
					item.put("id", compRecord.getComplicationId());
					item.put("name", compRecord.getName());
					item.put("date", compRecord.getDate()==null?"":format.format(compRecord.getDate()));
					item.put("encounterDate", format.format(encounter.getLastEditDate()));
					item.put("encounterProvider", providerName);
					item.put("encounterId", encounter.getId());
					items.add(item);
					counter+=1;
					}
			     }
			}else if(recordType.equals("procedures")){
				genericService.fill(encounter, "procedureRecords");
				for(ProcedureRecord proRecord : encounter.getProcedureRecords()){
					if(counter<=Integer.valueOf(limit)){
					JSONObject item = new JSONObject();
					item.put("id", proRecord.getProcedureId());
					item.put("name", proRecord.getName());
					item.put("date", proRecord.getDate()==null?"":format.format(proRecord.getDate()));
					item.put("encounterDate", format.format(encounter.getLastEditDate()));
					item.put("encounterProvider", providerName);
					item.put("encounterId", encounter.getId());
					items.add(item);
					counter+=1;
					}
			     }
			}else if(recordType.equals("treatments")){
				genericService.fill(encounter, "treatmentRecords");
				for(TreatmentRecord trRecord : encounter.getTreatmentRecords()){
					if(counter<=Integer.valueOf(limit)){
					JSONObject item = new JSONObject();
					item.put("id", trRecord.getDrugId());
					item.put("name", trRecord.getName());
					item.put("startDate", trRecord.getStartDate()==null?"":format.format(trRecord.getStartDate()));
					item.put("endDate", trRecord.getEndDate()==null?"":format.format(trRecord.getEndDate()));
					item.put("instructions", trRecord.getInstructions()==null?"":trRecord.getInstructions().equals("")||trRecord.getInstructions().equals("null")?"":trRecord.getInstructions());
					item.put("encounterDate", format.format(encounter.getLastEditDate()));
					item.put("encounterProvider", providerName);
					item.put("encounterId", encounter.getId());
					items.add(item);
					counter+=1;
					}
			     }
			}
		
	 }

		JSONObject feeds = new JSONObject();
		JSONObject response = new JSONObject();
		JSONObject value = new JSONObject();
		feeds.put("response", response);
		response.put("value", value);
		value.put("items", new JSONArray(items));
		value.put("total_count", Integer.valueOf(limit));
		value.put("version", new Long(1));

		resp.setContentType("application/json; charset=utf-8");
		Writer w = new OutputStreamWriter(resp.getOutputStream(), "utf-8");
		w.write(feeds.toString());
		w.close();
		resp.setStatus(HttpServletResponse.SC_OK);
	
		
		
	}
	

	

}
