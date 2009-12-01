package com.googlecode.semrs.server;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.semrs.model.ComplicationRecord;
import com.googlecode.semrs.model.Diagnosis;
import com.googlecode.semrs.model.Encounter;
import com.googlecode.semrs.model.LabTestRecord;
import com.googlecode.semrs.model.Module;
import com.googlecode.semrs.model.Patient;
import com.googlecode.semrs.model.ProcedureRecord;
import com.googlecode.semrs.model.SymptomRecord;
import com.googlecode.semrs.model.TreatmentRecord;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.model.proxy.Role;
import com.googlecode.semrs.server.exception.DeleteException;
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

public class PatientServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(PatientServlet.class);

	private PatientService patientService;
	
	private UserService userService;
	
	private GenericService genericService;
	
	private EncounterService encounterService;
	
	private static final String patientFolder = "/patient/";
	
	 static final Comparator<Integer> ageOrder = new Comparator<Integer>() {
		 public int compare(Integer int1, Integer int2) {
			 return int1.compareTo(int2);
		 }
	 };

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

            String patientAction = req.getParameter("patientAction");

			if (patientAction != null) {
				if(patientAction.equals("validateExisting")){
					validatePatient(req,resp);
				}else if(patientAction.equals("savePatient")){
					savePatient(req,resp);
				}else if(patientAction.equals("getPatient")){
					getPatient(req,resp);
				}else if(patientAction.equals("listPatients")){
					listPatients(req,resp);
				}else if(patientAction.equals("exportPatients")){
					exportPatients(req,resp);
				}else if(patientAction.equals("deletePatient")){
					deletePatient(req,resp);
				}else if(patientAction.equals("getProviders")){
					getProviders(req,resp);
				}else if(patientAction.equals("changeStatus")){
					changeStatus(req,resp);	
				}else if(patientAction.equals("exportRecord")){
					exportRecord(req,resp);	
				}else if(patientAction.equals("sexChart")){
					getSexDistributionChartData(req,resp);	
				}else if(patientAction.equals("ageChart")){
					getAgeDistributionChartData(req,resp);	
				}else if(patientAction.equals("activeChart")){
					getActiveChartData(req,resp);	
				}
				
				
				
				
			}
			
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {
    
			LOG.error("Error in PatientServlet = " + ex);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		handleRequest(req, resp);
	}
	
	
	
	protected void validatePatient(HttpServletRequest req, HttpServletResponse resp)throws IOException, Exception {
		
		String id = req.getParameter("id") == null ? ""
					: URLDecoder.decode(req.getParameter("id"), "UTF-8").trim();
		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();

		Pattern idPattern = Pattern.compile("^[[V|E]\\d\\d\\d\\d\\d\\d\\d\\d]{2,10}$");
		boolean validId = false;
		Matcher idFit = idPattern.matcher(id);
		if (idFit.matches() && (id.startsWith("V") || id.startsWith("F"))) {
			validId = true;
		} else {
			validId = false;
		}
		if (!validId) {
			out.println("Error: el campo C&eacute;dula de identidad debe tener un formato de tipo VXXXXXXXX o EXXXXXXXX");
		} else {
		
		if(patientService.getPatient(id, false)!=null){
			
			out.println("Error: Paciente Ya Existe ");
			
		}else{
			out.println("Exito: Paciente no Existe");
			
		  }
		}
		
	}
	
	
	protected void savePatient(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		boolean isNew = (req.getParameter("isNew") != null &&( req.getParameter("isNew").equals("") || req.getParameter("isNew").equals("true"))) ? true : false;
		
		String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String lastName = req.getParameter("lastName") == null ? ""
				: URLDecoder.decode(req.getParameter("lastName"), "UTF-8");
		String email = req.getParameter("email") == null ? "" : URLDecoder
				.decode(req.getParameter("email"), "UTF-8");
		String birthDate = req.getParameter("birthDate") == null ? ""
				: URLDecoder.decode(req.getParameter("birthDate"), "UTF-8");
		String sex = (req.getParameter("sex") == null || req.getParameter("sex").equals("null"))  ? "" : URLDecoder.decode(
				req.getParameter("sex"), "UTF-8").startsWith("M")?"M":"F";
		String phoneNumber = req.getParameter("phoneNumber") == null ? ""
				: URLDecoder.decode(req.getParameter("phoneNumber"), "UTF-8");
		String mobile = req.getParameter("mobile") == null ? "" : URLDecoder
				.decode(req.getParameter("mobile"), "UTF-8");
		String address = req.getParameter("address") == null ? "" : URLDecoder
				.decode(req.getParameter("address"), "UTF-8");
		String birthPlace = req.getParameter("birthPlace") == null ? "" : URLDecoder
				.decode(req.getParameter("birthPlace"), "UTF-8");
		String bloodType = req.getParameter("bloodType") == null ? "" : req.getParameter("bloodType");
		String weight = req.getParameter("weight") == null ? "" : URLDecoder
				.decode(req.getParameter("weight"), "UTF-8");
		String weightUnits = req.getParameter("weightUnits") == null ? "" : URLDecoder
				.decode(req.getParameter("weightUnits"), "UTF-8");
		String height = req.getParameter("height") == null ? "" : URLDecoder
				.decode(req.getParameter("height"), "UTF-8");
		String heightUnits = req.getParameter("heightUnits") == null ? "" : URLDecoder
				.decode(req.getParameter("heightUnits"), "UTF-8");
		String description = req.getParameter("description") == null ? "" : URLDecoder
				.decode(req.getParameter("description"), "UTF-8");
		String refferalCause = req.getParameter("referralCause") == null ? "" : URLDecoder
				.decode(req.getParameter("referralCause"), "UTF-8");
		String providerId = req.getParameter("providerId") == null ? "" : URLDecoder
				.decode(req.getParameter("providerId"), "UTF-8");
		String groupId = req.getParameter("groupId") == null ? "" : URLDecoder
				.decode(req.getParameter("groupId"), "UTF-8");
	//	boolean voided = (req.getParameter("voided") != null &&( req.getParameter("voided").equals("") || req.getParameter("voided").equals("true"))) ? true : false;
	//	String voidReason = req.getParameter("voidReason") == null ? "" : URLDecoder.decode(req.getParameter("voidReason"), "UTF-8");
		
		Patient patient = patientService.getPatient(id, false);
		User currentUser = userService.getCurrentUser();
		if (patient == null && isNew) {
			if (id.equals("") || name.equals("") || lastName.equals("") || birthDate.equals("")
					|| sex.equals("") || phoneNumber.equals("") || address.equals("")) {
				errors
				.add(" por favor complete los campos obligatorios para continuar");
			} else {
				patient = new Patient();
				Pattern idPattern = Pattern.compile("^[[V|E]\\d\\d\\d\\d\\d\\d\\d\\d]{2,10}$");
				boolean validId = false;
				Matcher idFit = idPattern.matcher(id);
				if (idFit.matches()&&(id.startsWith("V") || id.startsWith("E"))) {
					validId = true;
				} else {
					validId = false;
				}
				if (!validId) {
					errors.add(" el campo C&eacute;dula de identidad debe tener un formato de tipo VXXXXXXXX o EXXXXXXXX");
				} else {
					patient.setId(id);
				}
				
				patient.setName(name);
				patient.setLastName(lastName);
				if(!email.equals("")){
				Pattern emailPatten = Pattern.compile("^\\S+@\\S+$");
					boolean validEmail = false;
					Matcher fit = emailPatten.matcher(email);
					if (fit.matches()) {
						validEmail = true;
					} else {
						validEmail = false;
					}
					if (!validEmail) {
						errors
						.add(" el campo Email debe tener un formato de tipo usuario@dominio.com");
					} else {

						Map queryParams = new HashMap();
						queryParams.put("email", URLDecoder.decode(email, "UTF-8"));
						if(patientService.getPatientCount(queryParams) == 0){
							patient.setEmail(email);
						}else{
							errors.add(" el Email suministrado ya existe en el sistema");
						}
					}
				}
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",
						Locale.getDefault());
				if (!birthDate.equals("")) {
					Date d = sdf.parse(birthDate);
					Calendar cal = Calendar.getInstance();
					cal.setTime(d);
					patient.setBirthDate(cal.getTime());
				}
				patient.setSex(sex);
				patient.setPhoneNumber(phoneNumber);
				patient.setMobile(mobile);
				patient.setAddress(address);
				patient.setBirthPlace(birthPlace);
				patient.setBloodType(bloodType);
				patient.setDescription(description);
				patient.setRefferalCause(refferalCause);
				if(!weight.equals("")){
				patient.setWeight(Double.parseDouble(weight));
				patient.setWeightUnits(weightUnits);
				}
				if(!height.equals("")){
				patient.setHeight(Double.parseDouble(height));
				patient.setHeightUnits(heightUnits);
				}
				if(!providerId.equals("")){
					User provider = userService.getUserByUsername(providerId,false);
					if(provider!=null){
						patient.setProvider(provider);
						patient.setGroupId(groupId);
					}
				}
			
				

			}
			if (errors == null || errors.size() <= 0) {
				
				patient.setCreationDate(new Date());
				patient.setCreationUser(currentUser.getUsername());
				patient.setLastEditDate(new Date());
				patient.setLastEditUser(currentUser.getUsername());
				patientService.savePatient(patient);
				
				String folder = patientFolder;
				URL resource = this.getClass().getResource(folder);
				File patientFileFolder = new File(resource.getFile() + patient.getId() + "/");
				
				if(!patientFileFolder.exists()){
					patientFileFolder.mkdir();
				}
		
				
			}

		} else if (patient != null && isNew) {
			errors.add("Paciente ya existe");
		} else if(patient!=null && !isNew){
			if (id.equals("") || name.equals("") || lastName.equals("") || birthDate.equals("")
					|| sex.equals("") || phoneNumber.equals("") || address.equals("")) {
				errors
				.add(" por favor complete los campos obligatorios para continuar");
			}else{
				patient.setName(name);
				patient.setLastName(lastName);
				if(!email.equals("")){
					if(!email.equals(patient.getEmail())){
					Pattern emailPatten = Pattern.compile("^\\S+@\\S+$");
					boolean validEmail = false;
					Matcher fit = emailPatten.matcher(email);
					if (fit.matches()) {
						validEmail = true;
					} else {
						validEmail = false;
					}
					if (!validEmail) {
						errors
						.add(" el campo Email debe tener un formato de tipo usuario@dominio.com");
					} else {

						Map queryParams = new HashMap();
						queryParams.put("email", URLDecoder.decode(email, "UTF-8"));
						if(patientService.getPatientCount(queryParams) == 0){
							patient.setEmail(email);
						}else{
							errors.add(" el Email suministrado ya existe en el sistema");
						}
					}
					}
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",
						Locale.getDefault());
				if (!birthDate.equals("")) {
					Date d = sdf.parse(birthDate);
					Calendar cal = Calendar.getInstance();
					cal.setTime(d);
					patient.setBirthDate(cal.getTime());
				}
				patient.setSex(sex);
				patient.setPhoneNumber(phoneNumber);
				patient.setMobile(mobile);
				patient.setAddress(address);
				patient.setBirthPlace(birthPlace);
				patient.setBloodType(bloodType);
				patient.setDescription(description);
				patient.setRefferalCause(refferalCause);
				if(!weight.equals("")){
					patient.setWeight(Double.parseDouble(weight));
					patient.setWeightUnits(weightUnits);
				}
				if(!height.equals("")){
					patient.setHeight(Double.parseDouble(height));
					patient.setHeightUnits(heightUnits);
				}
				if(!providerId.equals("")){
					User provider = userService.getUserByUsername(providerId,false);
					if(provider!=null){
						patient.setProvider(provider);
						patient.setGroupId(groupId);
					}
				}
				/*if(voided){
					if(!voidReason.equals("")){
					patient.setVoidReason(voidReason);
					}else{
						errors.add(" Por favor introduzca la raz&oacute;n de desactivaci&ioacute;n");
					}
				}
				patient.setVoided(voided);
				patient.setVoidDate(new Date());
				*/
				if (errors == null || errors.size() <= 0) {

					patient.setLastEditDate(new Date());
					patient.setLastEditUser(currentUser.getUsername());
					patientService.savePatient(patient);

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
			out.println("Registro guardado ex&iacute;tosamente");
		}

	}
	
	
	protected void getPatient(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		final String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");

				Patient patient = patientService.getPatient(id,false);
				List dataItems = new ArrayList();
				try {
					JSONObject data = new JSONObject();
					if (patient != null) {
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						SimpleDateFormat birthFormat = new SimpleDateFormat("dd/MM/yyyy");
						data.put("id", patient.getId());
						data.put("name", patient.getName() == null? "" : patient.getName() );
						data.put("lastName", patient.getLastName() == null? "" : patient.getLastName());
						data.put("sex", patient.getSex().startsWith("M")?"Masculino":"Femenino");
						data.put("birthDate", patient.getBirthDate() == null? "" : birthFormat
								.format(patient.getBirthDate()) );
						if(patient.getBirthDate() == null || patient.getBirthDate().equals("")){
							data.put("age","");
						}else{
							Map ageMap = Util.getAgeInYearsDaysOrMonths(patient.getBirthDate());
							if(Integer.valueOf(String.valueOf(ageMap.get("years"))) > 1){
								data.put("age", String.valueOf(Util.getAge(patient.getBirthDate())) + " Años");	
							}else{
								data.put("age", String.valueOf(ageMap.get("years")) + " Años " +  String.valueOf(ageMap.get("months")) +  " Meses " + String.valueOf(ageMap.get("days")) + " Dias");
							}
						}

						//data.put("age", patient.getBirthDate() == null? "" : Util.getAge(patient.getBirthDate()));
						data.put("phoneNumber", patient.getPhoneNumber() == null ? "" : patient
								.getPhoneNumber());
						data.put("mobile", patient.getMobile() == null ? "" : patient
								.getMobile());
						data.put("address", patient.getAddress() == null ? "" : patient.getAddress());
						data.put("description",  patient.getDescription() == null ? "" : patient.getDescription());
						data.put("birthPlace", patient.getBirthPlace() == null ? "" : patient.getBirthPlace());
						data.put("email", patient.getEmail() == null ? "" : patient.getEmail());
						data.put("creationDate", patient.getCreationDate() == null ? ""
								: format.format(patient.getCreationDate()));
						data.put("provider", patient.getProvider() == null ? ""
								: patient.getProvider().getName() + " " + patient.getProvider().getLastName());
						data.put("providerId", patient.getProvider() == null ? ""
								: patient.getProvider().getUsername());
						data.put("voided", patient.isVoided() == true ? "No" : "Si");
						data.put("voidReason", patient.getVoidReason() == null ? "" : patient.getVoidReason());
						data.put("voidDate", patient.getVoidDate() == null ? "" : format.format(patient.getVoidDate()));
						data.put("lastEncounterDate", patient.getLastEncounterDate() == null? "" : format
								.format(patient.getLastEncounterDate()) );
						data.put("bloodType", patient.getBloodType() == null ? "" :  patient.getBloodType());
						data.put("loadSuccess", "true");
					}else{
						data.put("id", "");
						data.put("name", "");
						data.put("lastName", "");
						data.put("sex", "");
						data.put("birthDate", "");
						data.put("age","");
						data.put("phoneNumber", "");
						data.put("mobile", "");
						data.put("address","");
						data.put("description",  "");
						data.put("birthPlace", "");
						data.put("email", "");
						data.put("creationDate", "");
						data.put("provider", "");
						data.put("providerId", "");
						data.put("voided","");
						data.put("voidReason", "");
						data.put("voidDate","");
						data.put("lastEncounterDate", "");
						data.put("bloodType","");
						data.put("loadSuccess", "false");
			
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
		//}

	}
	

	
	protected void listPatients(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {
		
		String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String lastName = req.getParameter("lastName") == null ? ""
				: URLDecoder.decode(req.getParameter("lastName"), "UTF-8");
		String sex = (req.getParameter("sex") == null || req.getParameter("sex").equals("null"))  ? "" : URLDecoder.decode(
				req.getParameter("sex"), "UTF-8").startsWith("M")?"M":"F";
		String providerId = req.getParameter("providerId") == null ? "" : URLDecoder
				.decode(req.getParameter("providerId"), "UTF-8");
		String ageFrom = req.getParameter("ageFrom") == null ? "" : URLDecoder
				.decode(req.getParameter("ageFrom"), "UTF-8");
		String ageTo = req.getParameter("ageTo") == null ? "" : URLDecoder
				.decode(req.getParameter("ageTo"), "UTF-8");
		String birthdayFrom = req.getParameter("birthdayFrom") == null ? "" : URLDecoder
				.decode(req.getParameter("birthdayFrom"), "UTF-8");
		String birthdayTo = req.getParameter("birthdayTo") == null ? "" : URLDecoder
				.decode(req.getParameter("birthdayTo"), "UTF-8");
		String encounterDateFrom = req.getParameter("encounterDateFrom") == null ? "" : URLDecoder
				.decode(req.getParameter("encounterDateFrom"), "UTF-8");
		String encounterDateTo = req.getParameter("encounterDateTo") == null ? "" : URLDecoder
				.decode(req.getParameter("encounterDateTo"), "UTF-8");
		String creationDateFrom = req.getParameter("creationDateFrom") == null ? "" : URLDecoder
				.decode(req.getParameter("creationDateFrom"), "UTF-8");
		String creationDateTo = req.getParameter("creationDateTo") == null ? "" : URLDecoder
				.decode(req.getParameter("creationDateTo"), "UTF-8");
		String voided = req.getParameter("voided") == null ? "" : URLDecoder
				.decode(req.getParameter("voided"), "UTF-8");
		String[] diseases = req.getParameter("diseases") == null ? new String[0]: URLDecoder
				.decode(req.getParameter("diseases"), "UTF-8").split(",");
		String[] drugs = req.getParameter("drugs") == null ? new String[0]: URLDecoder
				.decode(req.getParameter("drugs"), "UTF-8").split(",");

		
		Map queryParams = null;
		if (!id.equals("") || !name.equals("") || !lastName.equals("") || !sex.equals("") 
				|| !providerId.equals("") || !ageFrom.equals("") || !ageTo.equals("")
				|| !birthdayFrom.equals("") || !birthdayTo.equals("") || !encounterDateFrom.equals("") 
				|| !encounterDateTo.equals("") || !creationDateFrom.equals("") || !creationDateTo.equals("") || !voided.equals("") 
				|| (diseases!=null && diseases.length>0) || (drugs!=null && drugs.length>0)) {
			
			queryParams = new HashMap();
			queryParams.put("id", URLDecoder.decode(id, "UTF-8"));

			queryParams.put("name", URLDecoder.decode(name, "UTF-8"));

			queryParams.put("lastName", URLDecoder.decode(lastName, "UTF-8"));
			
			queryParams.put("sex", URLDecoder.decode(sex, "UTF-8"));
			
			if(!providerId.equals("")){
			  queryParams.put("providerId", URLDecoder.decode(userService.getCurrentUser().getUsername(), "UTF-8"));
			}
			
			queryParams.put("ageFrom", URLDecoder.decode(ageFrom, "UTF-8"));

			queryParams.put("ageTo", URLDecoder.decode(ageTo, "UTF-8"));
			
			queryParams.put("birthdayFrom", URLDecoder.decode(birthdayFrom, "UTF-8"));
			
			queryParams.put("birthdayTo", URLDecoder.decode(birthdayTo, "UTF-8"));
			
			queryParams.put("encounterDateFrom", URLDecoder.decode(encounterDateFrom, "UTF-8"));
			
		    queryParams.put("encounterDateTo", URLDecoder.decode(encounterDateTo, "UTF-8"));
			
			queryParams.put("creationDateFrom", URLDecoder.decode(creationDateFrom, "UTF-8"));
			
			queryParams.put("creationDateTo", URLDecoder.decode(creationDateTo, "UTF-8"));
			
			queryParams.put("voided", URLDecoder.decode(voided, "UTF-8"));
			
			if(!diseases[0].equals("")){
              queryParams.put("diseases", Arrays.asList(diseases));
			}
		    
			if(!drugs[0].equals("")){
			  queryParams.put("drugs", Arrays.asList(drugs));
			}
			
		}

		String dir = (req.getParameter("dir") == null || req
				.getParameter("dir").equals("")) ? "DESC" : req
				.getParameter("dir");
		String sort = (req.getParameter("sort") == null || req.getParameter(
				"sort").equals("")) ? "id" : req.getParameter("sort");
		String start_param = (req.getParameter("start") == null || req
				.getParameter("start").equals("")) ? "0" : req
				.getParameter("start");
		String limit_param = (req.getParameter("limit") == null || req
				.getParameter("limit").equals("")) ? "10" : req
				.getParameter("limit");
		
		Collection<Patient> patients = null;
		int total_count = 0;
		if (queryParams == null) {
			patients = patientService.listPatients(dir, sort, limit_param, start_param);
			total_count = patientService.getPatientCount();

		} else {
			patients = patientService.listPatientsByQuery(queryParams, dir, sort,
					limit_param, start_param);
			total_count = patientService.getPatientCount(queryParams);

		}
		HttpSession session = req.getSession();
		if(patients!=null && session!=null){
			session.setAttribute("patients", patients);
		}

		// Object[][] data=LiveGridDataProxy.data;
		// if (sorted_data.containsKey(dir+sort))
		// data = (Object[][]) sorted_data.get(dir+sort);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat birthFormat = new SimpleDateFormat("dd/MM/yyyy");
		boolean edit = false;
		User currentUser = userService.getCurrentUser();
		genericService.fill(currentUser, "roles");
		for(com.googlecode.semrs.model.Role role: currentUser.getRoles()){
				genericService.fill(role, "modules");
				for(Module module : role.getModules()){
					if(module.getId().equals("patient-category")){
						edit = true;
						break;
					}
				}
		}
		
		List items = new ArrayList();
		for (Patient patient : patients) {
			JSONObject item = new JSONObject();
			item.put("id", patient.getId());
			item.put("name", patient.getName());
			item.put("lastName", patient.getLastName());
			item.put("sex", patient.getSex().startsWith("M")?"Masculino":"Femenino");
			item.put("birthDate", patient.getBirthDate() == null? "" : birthFormat
					.format(patient.getBirthDate()) );
			item.put("age", patient.getBirthDate() == null? "" : Util.getAge(patient.getBirthDate()));
			item.put("phoneNumber", patient.getPhoneNumber() == null ? "" : patient
					.getPhoneNumber());
			item.put("mobile", patient.getMobile() == null ? "" : patient
							.getMobile());
			item.put("creationDate", patient.getCreationDate() == null ? ""
					: format.format(patient.getCreationDate()));
			item.put("lastEditDate", patient.getLastEditDate() == null ? ""
					: format.format(patient.getLastEditDate()));
			item.put("lastEditUser", patient.getLastEditUser() == null ? "Sistema"
					: patient.getLastEditUser());
			item.put("provider", patient.getProvider() == null ? ""
					: patient.getProvider().getName() + " " + patient.getProvider().getLastName());
			item.put("voided", patient.isVoided() == true ? "No" : "Si");
			item.put("lastEncounterDate", patient.getLastEncounterDate() == null? "" : format
					.format(patient.getLastEncounterDate()) );
			item.put("edit", edit);
			items.add(item);
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
	
	protected void deletePatient(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		final String patientId = req.getParameter("patientId") == null ? ""
				: URLDecoder.decode(req.getParameter("patientId"), "UTF-8");

		try {
			Patient patient = patientService.getPatient(patientId, false);
			if (patient != null) {

				try{
				    patientService.deletePatient(patient);
					Collection<Encounter> encountersToDelete = null;
					Map queryParams = new HashMap();
					queryParams.put("patientId", patientId);
					queryParams.put("current", "true");
					//encounterService.deleteEncountersByQuery(queryParams);
					encountersToDelete = encounterService.listEncountersByQuery(queryParams,"", "", "", "");
					for(Encounter enc : encountersToDelete){
						encounterService.deleteEncounter(enc);
					}
					
				}catch(DeleteException pe){
					errors.add("Este Paciente tiene consultas asociadas y no puede ser eliminado. ");
				}
				if(errors == null || errors.size() <= 0){
					URL resource = this.getClass().getResource("/patient/images/");
					
					synchronized(resource){
						if(resource!=null){
							File imageFolder = new File(resource.getFile());
							if(imageFolder!=null){
								File[] images = imageFolder.listFiles();
								if (images != null) {
									for (File file : images) {
										String fileName = file.getName().substring(0,
												file.getName().lastIndexOf("."));
										if (fileName.equals(patientId)) {
											LOG.info("Deleting file = " + file.getName()
													+ " for patient = " + patientId);
											file.delete();
										}
									}
								}

								String folder = patientFolder;
								resource = this.getClass().getResource(folder);
								File patientFileFolder = new File(resource.getFile() + patientId + "/");

								if(patientFileFolder.exists()){
									patientFileFolder.delete();
								}
							}
						  }
						}
					}


			} else if (patient == null) {
				errors.add("Paciente no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar este paciente: ");
				for (String error : errors) {
					out.println(error);
				}
				return;

			} else {
				resp.setStatus(HttpServletResponse.SC_OK);
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deletePatient = " + e);

		}

	}
	
	
	protected void exportPatients(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		HttpSession session = req.getSession();
		Collection<Patient> patients = null;
		if(session!=null){
		   patients = (Collection<Patient>)session.getAttribute("patients");
		}
		if (patients != null) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");

				ArrayList headers = new ArrayList();
				headers.add("Cédula de Identidad");
				headers.add("Nombres");
				headers.add("Apellidos");
				headers.add("Sexo");
				headers.add("Fecha de Nacimiento");
				headers.add("Edad");
				headers.add("Telefóno");
				headers.add("Móvil");
				headers.add("Fecha de Ingreso");
				headers.add("Fecha Última Modificación");
				headers.add("Usuario Última Modificación");
				headers.add("Médico");
				headers.add("Activo");
				headers.add("Fecha Última Consulta");

				ArrayList values = new ArrayList();
				for (Patient patient : patients) {
					Map patientMap = new TreeMap();
					patientMap.put(1,patient.getId());
					patientMap.put(2,patient.getName());
					patientMap.put(3,patient.getLastName());
					patientMap.put(4,patient.getSex().startsWith("M")?"Masculino":"Femenino");
					patientMap.put(5,patient.getBirthDate() == null ? "" : format
							.format(patient.getBirthDate()));
					patientMap.put(6,String.valueOf(Util.getAge(patient.getBirthDate())));
					patientMap.put(7,patient.getPhoneNumber());
					patientMap.put(8,patient.getMobile());
					patientMap.put(9,patient.getCreationDate() == null ? "" : format
							.format(patient.getCreationDate()));
					patientMap.put(10,patient.getLastEditDate() == null ? "" : format
							.format(patient.getLastEditDate()));
					patientMap.put(11,patient.getLastEditUser());
					patientMap.put(12, patient.getProvider() == null? "" : patient.getProvider().getName() + " " + patient.getProvider().getLastName());
					patientMap.put(13,patient.isVoided() == true ? "No" : "Si");
					patientMap.put(14,patient.getLastEncounterDate() == null ? "" : format
							.format(patient.getLastEncounterDate() ));
					values.add(patientMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				       "attachment; filename=\"pacientes.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Pacientes")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportPatients = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}
	
	protected void getProviders(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException {
		
		String dir = (req.getParameter("dir") == null || req
				.getParameter("dir").equals("")) ? "DESC" : req
						.getParameter("dir");
		String sort = (req.getParameter("sort") == null || req.getParameter(
		"sort").equals("")) ? "username" : req.getParameter("sort");
		String start_param = (req.getParameter("start") == null || req
				.getParameter("start").equals("")) ? "0" : req
						.getParameter("start");
		String limit_param = (req.getParameter("limit") == null || req
				.getParameter("limit").equals("")) ? "10" : req
						.getParameter("limit");
		
		Map queryParams =  new HashMap();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add("ROLE_DOCT");
		queryParams.put("roles", roles);

	    Collection<User> users = userService.getUsersByQuery(queryParams, dir, sort,
				limit_param, start_param);
		int total_count = userService.getUsersCount(queryParams);

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (User user : users) {
			JSONObject item = new JSONObject();
			item.put("username", user.getUsername());
			item.put("name", user.getName() + " " + user.getLastName());
			items.add(item);
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
	
	
	protected void changeStatus(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();
		String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		String status = req.getParameter("status") == null ? ""
				: URLDecoder.decode(req.getParameter("status"), "UTF-8");
		
		Patient patient = patientService.getPatient(id, false);
		if(patient!=null){
		
		if(status.equals("void")){
			if(!patient.isVoided()){
			String voidReason = req.getParameter("voidReason") == null ? ""
					: URLDecoder.decode(req.getParameter("voidReason"), "UTF-8");
			if(voidReason.equals("")){
				errors.add(" por favor introduzca la razon de desactivaci&oacute;n");
			}else{
					patient.setVoided(true);
					patient.setVoidReason(voidReason);
					patient.setVoidDate(new Date());
					//Deleting pending encounters
					Collection<Encounter> encountersToDelete = null;
					Map queryParams = new HashMap();
					queryParams.put("patientId", id);
					queryParams.put("current", "true");
					//encounterService.deleteEncountersByQuery(queryParams);
					encountersToDelete = encounterService.listEncountersByQuery(queryParams,"", "", "", "");				
					for(Encounter enc : encountersToDelete){
						encounterService.deleteEncounter(enc);
					}
					

			}
		 }else{
			 errors.add(" el paciente ya se encuentra inactivo.");
		 }
		}else if(status.equals("active")){
		  if(patient.isVoided()){
			patient.setVoided(false);
		  }else{
			  errors.add(" el paciente ya se encuentra activo");
		  }
		}
		}else{
			errors.add(" paciente no existe");
		}
		
		if (errors == null || errors.size() <= 0) {

			patient.setLastEditDate(new Date());
			patient.setLastEditUser(userService.getCurrentUser().getUsername());
			patientService.savePatient(patient);

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
	
	
	
	protected void exportRecord(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {

		
		String id = req.getParameter("patientId") == null ? ""
				: URLDecoder.decode(req.getParameter("patientId"), "UTF-8");

		if(!id.equals("")){
			Patient patient = patientService.getPatient(id, false);
			if(patient!=null){
			Collection<Encounter> encounters = null;
			Map queryParams = new HashMap();
			queryParams.put("patientId", URLDecoder.decode(id, "UTF-8"));
			queryParams.put("current", "false");
			encounters = encounterService.listEncountersByQuery(queryParams, "DESC", "encounterDate", "", "");
			
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			SimpleDateFormat hourFormat = new SimpleDateFormat("EEE, dd MMM yyyy",Locale.getDefault());
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

			User provider =  patient.getProvider();
			Paragraph p9 = new Paragraph();
			p9.setAlignment(Paragraph.ALIGN_LEFT);
			p9.add(new Phrase("Médico Tratante: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
			p9.add(new Phrase(provider.getName() + " " + provider.getLastName(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));

			Paragraph p10 = new Paragraph();
			p10.setAlignment(Paragraph.ALIGN_LEFT);
			p10.add(new Phrase("Peso: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
			p10.add(new Phrase(patient.getWeight() + " " + patient.getWeightUnits(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
			p10.add(new Phrase("     Altura: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
			p10.add(new Phrase(patient.getHeight() + " " + patient.getHeightUnits(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
			p10.add(new Phrase("     Tipo de Sangre: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
			p10.add(new Phrase(patient.getBloodType(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
			
			Paragraph p11 = new Paragraph();
			p11.setAlignment(Paragraph.ALIGN_LEFT);
			p11.add(new Phrase("Observaciones: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
			p11.add(new Phrase(patient.getDescription(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
			
			Paragraph p12 = new Paragraph();
			p12.setAlignment(Paragraph.ALIGN_LEFT);
			p12.add(new Phrase("Fecha de Ingreso: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
			p12.add(new Phrase(timeFormat.format(patient.getCreationDate()),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
			if(patient.isVoided()){
				p12.add(new Phrase("      Fecha de Egreso: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
				p12.add(new Phrase(timeFormat.format(patient.getVoidDate()),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
				p12.add(new Phrase("      Razón de Egreso: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
				p12.add(new Phrase(patient.getVoidReason(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
			}
			
			
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			try {
				PdfWriter writer = PdfWriter.getInstance(document, baos);
	            writer.setPageEvent(new EndPage());
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
				
				Paragraph pc = new Paragraph();
				pc.add(new Phrase("Lista de Consultas", FontFactory.getFont(FontFactory.TIMES, 13, Font.BOLD)));
				pc.setAlignment(Paragraph.ALIGN_CENTER);
				pc.setSpacingBefore(10);
				document.add(pc);

				for(Encounter encounter: encounters){

					Paragraph p13 = new Paragraph();
					p13.setAlignment(Paragraph.ALIGN_LEFT);
					p13.add(new Phrase("Fecha de Consulta: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
					p13.add(new Phrase(timeFormat.format(encounter.getEncounterDate()),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
					
					p13.setSpacingBefore(20);
					document.add(p13);
					
					Paragraph p13a = new Paragraph();
					p13a.setAlignment(Paragraph.ALIGN_LEFT);
					p13a.add(new Phrase("Referido de: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
					p13a.add(new Phrase(encounter.getRefferral(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
					document.add(p13a);
					
					Paragraph p13b = new Paragraph();
					p13b.setAlignment(Paragraph.ALIGN_LEFT);
					p13b.add(new Phrase("Mótivo de Consulta: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
					p13b.add(new Phrase(encounter.getEncounterReason(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
					document.add(p13b);
					

					User encounterProvider =  userService.getUserByUsername(encounter.getEncounterProvider(),false);
					Paragraph p14 = new Paragraph();
					p14.setAlignment(Paragraph.ALIGN_LEFT);
					p14.add(new Phrase("Médico Tratante: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
					p14.add(new Phrase(encounterProvider.getName() + " " + encounterProvider.getLastName(),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
					document.add(p14);

					Paragraph p15 = new Paragraph();
					p15.setAlignment(Paragraph.ALIGN_LEFT);
					String toxicHabits = !encounter.isToxicHabits()?"No":encounter.getToxicHabitsDesc();
					p15.add(new Phrase("Habitos Tóxicos: ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
					p15.add(new Phrase(toxicHabits ,FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
					document.add(p15);

					Paragraph p16 = new Paragraph();
					p16.setAlignment(Paragraph.ALIGN_LEFT);
					p16.add(new Phrase("Antecedentes/Observaciones:  ",FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD)));
					p16.add(new Phrase(encounter.getBackground().replaceAll("\\<.*?\\>", ""),FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL)));
					p16.setSpacingAfter(18);
					document.add(p16);



					genericService.fill(encounter, "symptomRecords");
					if(encounter.getSymptomRecords().size()>0){
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

						document.add(symptomTable);
					}

					genericService.fill(encounter, "labTestRecords");

					if(encounter.getLabTestRecords().size()>0){

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

						for(LabTestRecord ltRecord : encounter.getLabTestRecords()){		
							labTestTable.addCell(ltRecord.getLabTestId());
							labTestTable.addCell(ltRecord.getName());
							labTestTable.addCell(ltRecord.isResult()?"Positivo":"Negativo");
							labTestTable.addCell(ltRecord.getResultDesc()==null?"":ltRecord.getResultDesc());
							labTestTable.addCell(ltRecord.getTestDate()==null?"":format.format(ltRecord.getTestDate()));

						}

						document.add(labTestTable);

					}



					genericService.fill(encounter, "complicationRecords");
					if(encounter.getComplicationRecords().size()>0){
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

						for(ComplicationRecord compRecord : encounter.getComplicationRecords()){
							complicationTable.addCell(compRecord.getComplicationId());
							complicationTable.addCell(compRecord.getName());
							complicationTable.addCell(compRecord.getDate()==null?"":format.format(compRecord.getDate()));

						}

						document.add(complicationTable);

					}



					genericService.fill(encounter, "procedureRecords");
					if(encounter.getProcedureRecords().size()>0){
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
						for(ProcedureRecord proRecord : encounter.getProcedureRecords()){
							procedureTable.addCell(proRecord.getProcedureId());
							procedureTable.addCell(proRecord.getName());
							procedureTable.addCell(proRecord.getDate()==null?"":format.format(proRecord.getDate()));	
						}
						document.add(procedureTable);
					}



					genericService.fill(encounter, "diagnoses");
					if(encounter.getDiagnoses().size()>0){
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

						document.add(diagnosisTable);
					}




					genericService.fill(encounter, "treatmentRecords");

					if(encounter.getTreatmentRecords().size()>0){
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
						for(TreatmentRecord trRecord : encounter.getTreatmentRecords()){
							treatmentTable.addCell(trRecord.getDrugId());
							treatmentTable.addCell(trRecord.getName());
							treatmentTable.addCell(trRecord.getStartDate()==null?"":format.format(trRecord.getStartDate()));
							treatmentTable.addCell(trRecord.getEndDate()==null?"":format.format(trRecord.getEndDate()));
							treatmentTable.addCell(trRecord.getInstructions()==null?"":trRecord.getInstructions());	
						}

						document.add(treatmentTable);

					}



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
					"attachment; filename=\"historia_"+patient.getId()+".pdf\"");
			resp.setContentLength(baos.size());
			ServletOutputStream out = resp.getOutputStream();
			baos.writeTo(out);
			out.flush();
			
			}
		}
	}
	
	
	
	
	protected void getSexDistributionChartData(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {
		
		Map chartData = patientService.getSexDemographics();
		SimpleDateFormat birthFormat = new SimpleDateFormat("dd/MM/yyyy");
		int count = Integer.valueOf(String.valueOf(chartData.get("m")))+ 
		Integer.valueOf(String.valueOf(chartData.get("f")))>0?Integer.valueOf(String.valueOf(chartData.get("m")))+
				Integer.valueOf(String.valueOf(chartData.get("f")))-1:0;

		String data = "{ \"elements\": [ { \"type\": \"pie\", \"colours\": [ \"#356aa0\", \"#d01f3c\" ]," +
				" \"border\": 2, \"start-angle\": 35, \"animate\": true, " +
				"\"tip\": \"#val# de #total# #percent# de 100%\", " +
				"\"values\": [{ \"value\": "+chartData.get("m")+", \"label\": \"Masculinos\" },{ \"value\": "+chartData.get("f")+", \"label\": \"Femeninos\" } ] } ]," +
				" \"title\": { \"text\": \"Distribución por sexo al "+birthFormat.format(new Date())+"\" }, \"x_axis\": null, \"bg_colour\" : \"#FFFFFF\"  }";
		
		resp.getWriter().write(data);
	
		
		
	}
	
	protected void getAgeDistributionChartData(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {
		
		Map chartData = patientService.getAgeDemographics();
		List<Integer> ages = new ArrayList<Integer>();
		ages.add(Integer.valueOf(String.valueOf(chartData.get("0-10"))));
		ages.add(Integer.valueOf(String.valueOf(chartData.get("10-20"))));
		ages.add(Integer.valueOf(String.valueOf(chartData.get("20-30"))));
		ages.add(Integer.valueOf(String.valueOf(chartData.get("30-40"))));
		ages.add(Integer.valueOf(String.valueOf(chartData.get("40-50"))));
		ages.add(Integer.valueOf(String.valueOf(chartData.get("50-60"))));
		ages.add(Integer.valueOf(String.valueOf(chartData.get("+60"))));
		
		Collections.sort(ages, ageOrder);
		int maxItems = 10;
		if(ages.get(0)>10){
			maxItems = ages.get(0);
		}
		
		SimpleDateFormat birthFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		String data = "{\"elements\": [ { \"type\": \"bar_3d\", \"values\": [  "+chartData.get("0-10")+", "+chartData.get("10-20")+", "+chartData.get("20-30")+", "+chartData.get("30-40")+", "+chartData.get("40-50")+", "+chartData.get("50-60")+", "+chartData.get("+60")+" ], \"colour\": \"#D54C78\" } ]," +
				" \"title\": { \"text\": \"Distribución por edad al "+birthFormat.format(new Date())+"\" }, \"x_axis\": { \"3d\": 5, \"colour\": \"#909090\"," +
				" \"labels\": { \"labels\": [ \"0-10\", \"10-20\", \"20-30\", \"30-40\", \"40-50\", \"50-60\", \"+60\" ] } }," +
				"\"y_axis\": { \"min\": 0, \"max\": "+maxItems+", \"steps\": 5 }, \"bg_colour\" : \"#FFFFFF\" }";

		resp.getWriter().write(data);
	
		
		
	}
	
	
	protected void getActiveChartData(HttpServletRequest req, HttpServletResponse resp)
	throws IOException, Exception {
		
		Map chartData = patientService.getActive();
		SimpleDateFormat birthFormat = new SimpleDateFormat("dd/MM/yyyy");
		

		String data = "{ \"elements\": [ { \"type\": \"pie\", \"colours\": [ \"#356aa0\", \"#d01f3c\" ]," +
				" \"border\": 2, \"start-angle\": 35, \"animate\": true, " +
				"\"tip\": \"#val# de #total# #percent# de 100%\", " +
				"\"values\": [{ \"value\": "+chartData.get("active")+", \"label\": \"Activos\" },{ \"value\": "+chartData.get("inactive")+", \"label\": \"Inactivos\" } ] } ]," +
				" \"title\": { \"text\": \"Pacientes Activos/Inactivos al "+birthFormat.format(new Date())+"\" }, \"x_axis\": null, \"bg_colour\" : \"#FFFFFF\"  }";
		
		resp.getWriter().write(data);
	
		
		
	}


}
