package com.googlecode.semrs.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
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

import com.googlecode.semrs.model.Complication;
import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.DiseaseService;
import com.googlecode.semrs.server.service.DrugService;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.LabTestService;
import com.googlecode.semrs.server.service.ProcedureService;
import com.googlecode.semrs.server.service.SymptomService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.ExcelExporter;

public class DiseaseServlet  extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(DiseaseServlet.class);

	private DiseaseService diseaseService;
	
	private SymptomService symptomService;
	
	private LabTestService labTestService;
	
	private ProcedureService procedureService;
	
	private GenericService genericService;
	
	private UserService userService;

	
	

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.userService  = (UserService) factory.getBean("userService");
		this.diseaseService = (DiseaseService) factory.getBean("diseaseService");
		this.labTestService  = (LabTestService) factory.getBean("labTestService");
		this.symptomService = (SymptomService) factory.getBean("symptomService");
		this.procedureService = (ProcedureService) factory.getBean("procedureService");
		this.genericService = (GenericService) factory.getBean("genericService");

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		handleRequest(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		handleRequest(req, resp);
	}

	private void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {

			String diseaseEdit = req.getParameter("diseaseEdit");
			String export = req.getParameter("export");
			if (diseaseEdit != null) {
				if (diseaseEdit.equals("load")) {

				    getDisease(req, resp);

				} else if (diseaseEdit.equals("submit")) {

					saveDisease(req, resp);

				} else if (diseaseEdit.equals("delete")) {

					deleteDisease(req, resp);

				} else if (diseaseEdit.equals("getSymptoms")) {

					getSymptoms(req, resp);

				}else if (diseaseEdit.equals("getLabTests")) {

					getLabTests(req, resp);

				}else if (diseaseEdit.equals("getProcedures")) {

					getProcedures(req, resp);
					
				}else if (diseaseEdit.equals("getRelatedDiseases")) {

					getRelatedDiseases(req, resp);
				}

			} else if (export != null) {

				exportDiseases(req, resp);

			} else {
				listDiseases(req, resp);
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {

			LOG.error("Error in DiseaseServlet = " + ex);
		}
	}
	
	
	protected void saveDisease(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		boolean isNew = (req.getParameter("isNew") != null && (req
				.getParameter("isNew").equals("") || req.getParameter("isNew")
				.equals("true"))) ? true : false;
		String diseaseId = req.getParameter("id") == null ? "" : URLDecoder
				.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String description = req.getParameter("description") == null ? ""
				: URLDecoder.decode(req.getParameter("description"), "UTF-8");
		String ages = req.getParameter("ages") == null ? ""
				: URLDecoder.decode(req.getParameter("ages"), "UTF-8").trim().startsWith("I")?"":URLDecoder.decode(req.getParameter("ages"), "UTF-8");
		String sex = req.getParameter("sex") == null ? ""
				: URLDecoder.decode(req.getParameter("sex"), "UTF-8").startsWith("I") || URLDecoder.decode(req.getParameter("sex"), "UTF-8").equals("")?"":URLDecoder.decode(req.getParameter("sex"), "UTF-8").startsWith("M")?"M":"F";
		String[] symptoms = req.getParameter("diseaseSymptoms") == null ? new String[0]
				: req.getParameter("diseaseSymptoms").split(",");
		String[] labTests = req.getParameter("diseaseLabTests") == null ? new String[0]
		        : req.getParameter("diseaseLabTests").split(",");
		String[] procedures = req.getParameter("diseaseProcedures") == null ? new String[0]
		        : req.getParameter("diseaseProcedures").split(",");
		String[] relatedDiseases = req.getParameter("relatedDiseasesD") == null ? new String[0]
		        : req.getParameter("relatedDiseasesD").split(",");
		boolean toxicHabits = (req.getParameter("toxicHabits") != null && (req.getParameter("toxicHabits").equals("true")) || req.getParameter("toxicHabits").equals("Si")) ? true : false;

		Disease disease = diseaseService.getDisease(diseaseId, false);

		if (disease != null && !isNew) {
			if (name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!name.equals("")) {
				disease.setName(name);
				disease.setDescription(description);
				disease.setSex(sex);
				disease.setToxicHabits(toxicHabits);
				if(ages.equals("60 +") || ages.equals("60")){
				  disease.setMinimumAgeRange(60);
				  disease.setMaximumAgeRange(120);
				}else if(!ages.trim().equals("")){
				  String[] ageRange = ages.split("-");
				  disease.setMinimumAgeRange(Integer.valueOf(ageRange[0]));
				  disease.setMaximumAgeRange(Integer.valueOf(ageRange[1]));
				  
				}

				final Collection<Symptom> assignedSymptoms = new ArrayList<Symptom>();
				if (symptoms != null) {
					if (symptoms.length > 0) {
						Symptom symptom = null;
						
						for (String assignedSymptom : symptoms) {
							if (!assignedSymptom.equals("")) {
								symptom = symptomService.getSymptom(URLDecoder
										.decode(assignedSymptom, "UTF-8"),false);
								if (symptom != null) {
									assignedSymptoms.add(symptom);
								}
							}
						}

						if (assignedSymptoms != null || assignedSymptoms.size() > 0) {
						    disease.setSymptoms(assignedSymptoms);
						}
					}
				}
				
				
				final Collection<LabTest> assignedLabTests = new ArrayList<LabTest>();
				
				if (labTests != null) {
					if (labTests.length > 0) {
						LabTest labTest = null;
						
						for (String assignedLabTest : labTests) {
							if (!assignedLabTest.equals("")) {
								labTest = labTestService.getLabTest(URLDecoder
										.decode(assignedLabTest, "UTF-8"),false);
								if (labTest != null) {
									assignedLabTests.add(labTest);
								}
							}
						}

						if (assignedLabTests != null || assignedLabTests.size() > 0) {
						    disease.setLabTests(assignedLabTests); 
						}
					}
				}
				
				
				final Collection<Procedure> assignedProcedures = new ArrayList<Procedure>();
				
				if (procedures != null) {
					if (procedures.length > 0) {
						Procedure procedure = null;
						
						for (String assignedProcedure : procedures) {
							if (!assignedProcedure.equals("")) {
								procedure = procedureService.getProcedure(URLDecoder
										.decode(assignedProcedure, "UTF-8"),false);
								if (procedure != null) {
									assignedProcedures.add(procedure);
								}
							}
						}

						if (assignedProcedures != null || assignedProcedures.size() > 0) {
							disease.setProcedures(assignedProcedures);
						}
					}
				}
				
				
				final Collection<Disease> assignedDiseases = new ArrayList<Disease>();
				if (relatedDiseases != null) {
					if (relatedDiseases.length > 0) {
						Disease dis = null;
						
						for (String assignedDisease : relatedDiseases) {
							if (!assignedDisease.equals("")) {
								dis = diseaseService.getDisease(URLDecoder
										.decode(assignedDisease, "UTF-8"),false);
								if (dis != null) {
									assignedDiseases.add(dis);
								}
							}
						}

						if (assignedDiseases != null || assignedDiseases.size() > 0) {
							disease.setRelDiseases(assignedDiseases);
						}
					}
				}
				
			}
			
			if (errors == null || errors.size() <= 0) {
				disease.setLastEditDate(new Date());
				disease.setLastEditUser(userService.getCurrentUser().getUsername());
				diseaseService.save(disease);
			}

		} else if (disease != null && isNew) {
			errors.add("C&oacute;digo ya existe");
		} else if (disease == null && isNew) {

			disease = new Disease();

			if (diseaseId.equals("") || name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			}else if(diseaseId.contains("@")){
				errors.add(" el c&oacute;digo contiene car&aacute;cteres no v&aacute;lidos");
			} else if (!diseaseId.equals("") && !name.equals("")) {

				disease.setId(diseaseId);
				disease.setName(name);
				disease.setDescription(description);
				disease.setSex(sex);
				disease.setToxicHabits(toxicHabits);
				if(ages.equals("60 +") || ages.equals("60")){
					  disease.setMinimumAgeRange(60);
					  disease.setMaximumAgeRange(120);
					}else if(!ages.equals("")){
					  String[] ageRange = ages.split("-");
					  disease.setMinimumAgeRange(Integer.valueOf(ageRange[0]));
					  disease.setMaximumAgeRange(Integer.valueOf(ageRange[1]));
					  
					}

				if (symptoms != null) {

					final Collection<Symptom> assignedSymptoms = new ArrayList<Symptom>();
					if (symptoms != null) {
						if (symptoms.length > 0) {
							Symptom symptom = null;
							
							for (String assignedSymptom : symptoms) {
								if (!assignedSymptom.equals("")) {
									symptom = symptomService.getSymptom(URLDecoder
											.decode(assignedSymptom, "UTF-8"),false);
									if (symptom != null) {
										assignedSymptoms.add(symptom);
									}
								}
							}

							if (assignedSymptoms != null || assignedSymptoms.size() > 0) {
							  disease.setSymptoms(assignedSymptoms);
							}
						}
					}
					
					
					final Collection<LabTest> assignedLabTests = new ArrayList<LabTest>();
					
					if (labTests != null) {
						if (labTests.length > 0) {
							LabTest labTest = null;
							
							for (String assignedLabTest : labTests) {
								if (!assignedLabTest.equals("")) {
									labTest = labTestService.getLabTest(URLDecoder
											.decode(assignedLabTest, "UTF-8"),false);
									if (labTest != null) {
										assignedLabTests.add(labTest);
									}
								}
							}

							if (assignedLabTests != null || assignedLabTests.size() > 0) {
								disease.setLabTests(assignedLabTests);
							}
						}
					}
					
					
					final Collection<Procedure> assignedProcedures = new ArrayList<Procedure>();
					
					if (procedures != null) {
						if (procedures.length > 0) {
							Procedure procedure = null;
							
							for (String assignedProcedure : procedures) {
								if (!assignedProcedure.equals("")) {
									procedure = procedureService.getProcedure(URLDecoder
											.decode(assignedProcedure, "UTF-8"),false);
									if (procedure != null) {
										assignedProcedures.add(procedure);
									}
								}
							}

							if (assignedProcedures != null || assignedProcedures.size() > 0) {
								disease.setProcedures(assignedProcedures);
							}
						}
					}
					
					
					final Collection<Disease> assignedDiseases = new ArrayList<Disease>();
					if (relatedDiseases != null) {
						if (relatedDiseases.length > 0) {
							Disease dis = null;
							
							for (String assignedDisease : relatedDiseases) {
								if (!assignedDisease.equals("")) {
									dis = diseaseService.getDisease(URLDecoder
											.decode(assignedDisease, "UTF-8"),false);
									if (dis != null) {
										assignedDiseases.add(dis);
									}
								}
							}

							if (assignedDiseases != null || assignedDiseases.size() > 0) {
								disease.setRelDiseases(assignedDiseases);
							}
							
						}
					}
				}
					
				}
			if (errors == null || errors.size() <= 0) {
				disease.setLastEditDate(new Date());
				disease.setLastEditUser(userService.getCurrentUser().getUsername());
				diseaseService.save(disease);
			}
		}

		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();

		if (errors != null && errors.size() > 0) {
			out.println("Ocurrier&oacute;n errores al guardar: ");
			for (String error : errors) {
				out.println(error);
			}

		} else {
			out.println("Registro guardado ex&iacute;tosamente");
		}

	}
	
	
	protected void getDisease(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		Disease disease = null;
		String diseaseId = req.getParameter("id");
			disease = diseaseService.getDisease(diseaseId,false);
		List dataItems = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			JSONObject data = new JSONObject();
			if (disease != null) {
				data.put("id", disease.getId());
				data.put("name", disease.getName() );
				data.put("description", disease.getDescription());
				data.put("sex", disease.getSex()==null?"":disease.getSex().equals("")?"":disease.getSex().startsWith("M")?"Masculino":"Femenino");
				data.put("toxicHabits", disease.isToxicHabits() == true ? "Si" : "No");
				if(disease.getMinimumAgeRange() == 60){
				   data.put("ages", "60 +");
				}else if(disease.getMinimumAgeRange()==0 && disease.getMaximumAgeRange()==0){
				   data.put("ages", "");
				}else{
					data.put("ages", disease.getMinimumAgeRange()+"-"+disease.getMaximumAgeRange());
				}
				data.put("loadSuccess", "true");

			}else{
				data.put("id", "");
				data.put("name", "");
				data.put("description", "");
				data.put("sex", "");
				data.put("toxicHabits", "");
			    data.put("ages", "");
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
	}
	
	
	protected void deleteDisease(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		String diseaseId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");

		try {
			Disease disease = diseaseService.getDisease(diseaseId,false);
			if (disease != null) {
				diseaseService.deleteDisease(disease);

			} else if (disease == null) {
				errors.add("Enfermedad no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar esta enfermedad: ");
				for (String error : errors) {
					out.println(error);
				}

			} else {
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deleteDisease = " + e);

		}

	}
	
	
	protected void getSymptoms(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {
		
		String diseaseId = req.getParameter("diseaseId") == null ? ""
				: URLDecoder.decode(req.getParameter("diseaseId"), "UTF-8");
		String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
	
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
		
		Map queryParams = new HashMap();
	    queryParams.put("id", URLDecoder.decode(id, "UTF-8"));
		queryParams.put("name", URLDecoder.decode(name, "UTF-8"));


		String diseaseSymptomsParam = req.getParameter("diseaseSymptoms");
		List items = null;
		
	    Collection<Symptom> availableSymptoms = null;
	    
	    int total_count = 0;
		
			if (diseaseSymptomsParam != null) {
				Disease disease = diseaseService.getDisease(diseaseId,false);
				if(disease!=null){

				items = new ArrayList();
				genericService.fill(disease, "symptoms");
				final Collection<Symptom> diseaseSymptoms = disease.getSymptoms();

				for (Symptom symp : diseaseSymptoms) {
					JSONObject item = new JSONObject();
					item.put("id", symp.getId());
					item.put("name", symp.getName());
					items.add(item);
				}
				 total_count = diseaseSymptoms.size();
				}
			} else{
				availableSymptoms = diseaseService.getAvailableSymptoms(diseaseId, queryParams, dir, sort, limit_param, start_param);
				total_count = diseaseService.getAvailableSymptomsCount(diseaseId, queryParams);
				items = new ArrayList();

				for (Symptom symp : availableSymptoms) {
					JSONObject item = new JSONObject();
					item.put("id", symp.getId());
					item.put("name",symp.getName());
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
	
	protected void getLabTests(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {
		
		String diseaseId = req.getParameter("diseaseId") == null ? ""
				: URLDecoder.decode(req.getParameter("diseaseId"), "UTF-8");
		String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
	
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
		
		Map queryParams = new HashMap();
	    queryParams.put("id", URLDecoder.decode(id, "UTF-8"));
		queryParams.put("name", URLDecoder.decode(name, "UTF-8"));


		String diseaseLabTestsParam = req.getParameter("diseaseLabTests");
		List items = null;
		
	    Collection<LabTest> availableLabTests = null;
	    
	    int total_count = 0;

			if (diseaseLabTestsParam != null) {
				Disease disease = diseaseService.getDisease(diseaseId,false);
				if(disease!=null){
				items = new ArrayList();
				genericService.fill(disease, "labTests");
				final Collection<LabTest> diseaseLabTests= disease.getLabTests();

				for (LabTest labTest : diseaseLabTests) {
					JSONObject item = new JSONObject();
					item.put("id", labTest.getId());
					item.put("name", labTest.getName());
					items.add(item);
				}
				 total_count = diseaseLabTests.size();
				}
			} else{
				availableLabTests = diseaseService.getAvailableLabTests(diseaseId, queryParams, dir, sort, limit_param, start_param);
				total_count = diseaseService.getAvailableLabTestsCount(diseaseId, queryParams);
				items = new ArrayList();

				for (LabTest labTest : availableLabTests) {
					JSONObject item = new JSONObject();
					item.put("id", labTest.getId());
					item.put("name",labTest.getName());
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
	
	protected void getProcedures(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		String diseaseId = req.getParameter("diseaseId") == null ? ""
				: URLDecoder.decode(req.getParameter("diseaseId"), "UTF-8");
		String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
	
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
		
		Map queryParams = new HashMap();
	    queryParams.put("id", URLDecoder.decode(id, "UTF-8"));
		queryParams.put("name", URLDecoder.decode(name, "UTF-8"));


		String diseaseProceduresParam = req.getParameter("diseaseProcedures");
		List items = null;
		
	    Collection<Procedure> availableProcedures= null;
	    
	    int total_count = 0;

			if (diseaseProceduresParam != null) {
				Disease disease = diseaseService.getDisease(diseaseId,false);
				if(disease!=null){

				items = new ArrayList();
				genericService.fill(disease, "procedures");
				final Collection<Procedure> complicationProcedures = disease.getProcedures();

				for (Procedure proc : complicationProcedures) {
					JSONObject item = new JSONObject();
					item.put("id", proc.getId());
					item.put("name", proc.getName());
					items.add(item);
				}
				 total_count = complicationProcedures.size();
				}
			} else{

		        availableProcedures = diseaseService.getAvailableProcedures(diseaseId, queryParams, dir, sort, limit_param, start_param);
			 	total_count = diseaseService.getAvailableProceduresCount(diseaseId, queryParams);
				items = new ArrayList();

				for (Procedure proc : availableProcedures) {
					JSONObject item = new JSONObject();
					item.put("id", proc.getId());
					item.put("name",proc.getName());
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
	
	
	
	protected void getRelatedDiseases(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		String diseaseId = req.getParameter("diseaseId") == null ? ""
				: URLDecoder.decode(req.getParameter("diseaseId"), "UTF-8");
		String id = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
	
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
		
		Map queryParams = new HashMap();
        queryParams.put("id", URLDecoder.decode(id, "UTF-8"));
	    queryParams.put("name", URLDecoder.decode(name, "UTF-8"));
        
	    String relatedDiseasesParam = req.getParameter("relatedDiseases");
		List items = null;
		
	    Collection<Disease> availableDiseases = null;
	    
	    int total_count = 0;
	
			if (relatedDiseasesParam != null) {
				Disease disease = diseaseService.getDisease(diseaseId,false);
				if(disease!=null){

				items = new ArrayList();
				genericService.fill(disease, "relDiseases");
				final Collection<Disease> relatedDiseases = disease.getRelDiseases();

				for (Disease dis : relatedDiseases) {
					if(!dis.getId().equals(disease.getId())){
						JSONObject item = new JSONObject();
						item.put("id", dis.getId());
						item.put("name", dis.getName());
						items.add(item);
					}
				}
				 total_count = relatedDiseases.size();
				}
			} else{

				availableDiseases = diseaseService.getAvailableDiseases(diseaseId, queryParams, dir, sort, limit_param, start_param);
				total_count = diseaseService.getAvailableDiseasesCount(diseaseId, queryParams);
			
				items = new ArrayList();

				for (Disease disease : availableDiseases) {
					JSONObject item = new JSONObject();
					item.put("id", disease.getId());
					item.put("name", disease.getName());
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
	
	
	

	protected void listDiseases(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException {

		String id = req.getParameter("id") == null ? "" : req
				.getParameter("id");
		String name = req.getParameter("name") == null ? "" : req
				.getParameter("name");
		String[] symptoms = req.getParameter("symptoms") == null ? new String[0]: req.getParameter("symptoms").split(",");
		
		String[] labTests = req.getParameter("labTests") == null ? new String[0]: req.getParameter("labTests").split(",");
		
		String[] procedures = req.getParameter("procedures") == null ? new String[0]: req.getParameter("procedures").split(",");
		
		Map queryParams = null;
		if (!id.equals("") || !name.equals("") || (symptoms != null && symptoms.length >0) || (labTests != null && labTests.length >0) || (procedures != null && procedures.length >0)) {
			queryParams = new HashMap();
			
			queryParams.put("id", URLDecoder.decode(id, "UTF-8"));

			queryParams.put("name", URLDecoder.decode(name, "UTF-8"));
			
			queryParams.put("symptoms", Arrays.asList(symptoms));
			
			queryParams.put("labTests", Arrays.asList(labTests));
			
			queryParams.put("procedures", Arrays.asList(procedures));
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

		Collection<Disease> diseases = null;
		int total_count = 0;
		if (queryParams == null) {
			diseases = diseaseService.listDiseases(dir, sort, limit_param, start_param);
			total_count = diseaseService.getDiseaseCount();

		} else {
			diseases = diseaseService.listDiseasesByQuery(queryParams, dir, sort,limit_param, start_param);
			total_count = diseaseService.getDiseaseCount(queryParams);

		}
		
		HttpSession session = req.getSession();
		if(session!=null && diseases!=null){
			session.setAttribute("diseases", diseases);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (Disease disease : diseases) {
			JSONObject item = new JSONObject();
			item.put("id", disease.getId());
			item.put("name", disease.getName());
			item.put("description", disease.getDescription());
			item.put("lastEditDate", disease.getLastEditDate() == null ? ""
					: format.format(disease.getLastEditDate()));
			item.put("lastEditUser", disease.getLastEditUser() == null ? "Sistema"
					: disease.getLastEditUser());
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

	protected void exportDiseases(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		HttpSession session = req.getSession();
		Collection<Disease> diseases = null;
		if(session!=null){
			diseases = (Collection<Disease>)session.getAttribute("diseases");
		}

		if (diseases != null) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");

				ArrayList headers = new ArrayList();
				headers.add("Código");
				headers.add("Nombre");
				headers.add("Descripción");
				headers.add("Fecha Última Modificación");
				headers.add("Usuario Última Modificación");

				ArrayList values = new ArrayList();
				for (Disease disease : diseases) {
					Map userMap = new TreeMap();
					userMap.put(1,disease.getId());
					userMap.put(2,disease.getName());
					userMap.put(3,disease.getDescription().replaceAll("\\<.*?\\>", "").replaceAll("&nbsp;", ""));
					userMap.put(4,disease.getLastEditDate() == null ? "" : format
							.format(disease.getLastEditDate()));
					userMap.put(5,disease.getLastEditUser() == null ? "Sistema"
							:disease.getLastEditUser());
					values.add(userMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				       "attachment; filename=\"enfermedades.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Enfermedades")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportDiseases = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}

}



