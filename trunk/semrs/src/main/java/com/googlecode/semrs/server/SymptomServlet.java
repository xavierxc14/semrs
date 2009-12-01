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

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.DiseaseService;
import com.googlecode.semrs.server.service.DrugService;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.LabTestService;
import com.googlecode.semrs.server.service.SymptomService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.ExcelExporter;

public class SymptomServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(SymptomServlet.class);

	private DiseaseService diseaseService;
	
	private SymptomService symptomService;
	
	private DrugService drugService;
	
	private UserService userService;
	
	private GenericService genericService;

	
	

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.userService  = (UserService) factory.getBean("userService");
		this.diseaseService = (DiseaseService) factory.getBean("diseaseService");
		this.drugService  = (DrugService) factory.getBean("drugService");
		this.symptomService = (SymptomService) factory.getBean("symptomService");
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

			String symptomEdit = req.getParameter("symptomEdit");
			String export = req.getParameter("export");
			if (symptomEdit != null) {
				if (symptomEdit.equals("load")) {

				    getSymptom(req, resp);

				} else if (symptomEdit.equals("submit")) {

					saveSymptom(req, resp);

				} else if (symptomEdit.equals("delete")) {

					deleteSymptom(req, resp);

				} else if (symptomEdit.equals("getRelatedSymptoms")) {

					getRelatedSymptoms(req, resp);

				}else if (symptomEdit.equals("getDrugs")) {

					getDrugs(req, resp);

				}
			} else if (export != null) {

				exportSymptoms(req, resp);

			} else {
				listSymptoms(req, resp);
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {

			LOG.error("Error in SymptomServlet = " + ex);
		}
	}
	
	
	protected void saveSymptom(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		boolean isNew = (req.getParameter("isNew") != null && (req
				.getParameter("isNew").equals("") || req.getParameter("isNew")
				.equals("true"))) ? true : false;
		String symptomId = req.getParameter("id") == null ? "" : URLDecoder
				.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String description = req.getParameter("description") == null ? ""
				: URLDecoder.decode(req.getParameter("description"), "UTF-8");
		String[] relatedSymptoms = req.getParameter("relatedSymptoms") == null ? new String[0]
				: req.getParameter("relatedSymptoms").split(",");
		String[] drugs = req.getParameter("symptomDrugs") == null ? new String[0]
		        : req.getParameter("symptomDrugs").split(",");

		Symptom symptom = symptomService.getSymptom(symptomId,false);

		if (symptom != null && !isNew) {
			if (name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!name.equals("")) {
				symptom.setName(name);
				symptom.setDescription(description);

				if (relatedSymptoms != null) {
					if (relatedSymptoms.length > 0) {
						Symptom symp = null;
						final Collection<Symptom> assignedSymptoms = new ArrayList<Symptom>();
						for (String assignedSymptom : relatedSymptoms) {
							if (!assignedSymptom.equals("")) {
								symp = symptomService.getSymptom(URLDecoder
										.decode(assignedSymptom, "UTF-8"),false);
								if (symp != null) {
									assignedSymptoms.add(symp);
								}
							}
						}

						if (assignedSymptoms != null || assignedSymptoms.size() > 0) {
							symptom.setSymptoms(assignedSymptoms);
						}
					}
				}
				
				if (drugs != null) {
					if (drugs.length > 0) {
						Drug drug = null;
						final Collection<Drug> assignedDrugs = new ArrayList<Drug>();
						for (String assignedDrug : drugs) {
							if (!assignedDrug.equals("")) {
								drug = drugService.getDrug(URLDecoder
										.decode(assignedDrug, "UTF-8"),false);
								if (drug != null) {
									assignedDrugs.add(drug);
								}
							}
						}

						if (assignedDrugs != null || assignedDrugs.size() > 0) {
							symptom.setDrugs(assignedDrugs);
						}
					}
				}

				
			}
			if (errors == null || errors.size() <= 0) {
				symptom.setLastEditDate(new Date());
				symptom.setLastEditUser(userService.getCurrentUser().getUsername());
				symptomService.save(symptom);
			}

		} else if (symptom != null && isNew) {
			errors.add("C&oacute;digo ya existe");
		} else if (symptom == null && isNew) {

			symptom = new Symptom();

			if (symptomId.equals("") || name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			}else if(symptomId.contains("@")){
					errors.add(" el c&oacute;digo contiene car&aacute;cteres no v&aacute;lidos");
			}else if (!symptomId.equals("") && !name.equals("")) {

				symptom.setId(symptomId);
				symptom.setName(name);
				symptom.setDescription(description);

				if (relatedSymptoms != null) {
					if (relatedSymptoms.length > 0) {
						Symptom symp = null;
						final Collection<Symptom> assignedSymptoms = new ArrayList<Symptom>();
						for (String assignedSymptom : relatedSymptoms) {
							if (!assignedSymptom.equals("")) {
								symp = symptomService.getSymptom(URLDecoder
										.decode(assignedSymptom, "UTF-8"),false);
								if (symp != null) {
									assignedSymptoms.add(symp);
								}
							}
						}

						if (assignedSymptoms != null || assignedSymptoms.size() > 0) {
							symptom.setSymptoms(assignedSymptoms);
						}
					}
				}
				
				if (drugs != null) {
					if (drugs.length > 0) {
						Drug drug = null;
						final Collection<Drug> assignedDrugs = new ArrayList<Drug>();
						for (String assignedDrug : drugs) {
							if (!assignedDrug.equals("")) {
								drug = drugService.getDrug(URLDecoder
										.decode(assignedDrug, "UTF-8"),false);
								if (drug != null) {
									assignedDrugs.add(drug);
								}
							}
						}

						if (assignedDrugs != null || assignedDrugs.size() > 0) {
							symptom.setDrugs(assignedDrugs);
						}
					}
				}
				
			}
			if (errors == null || errors.size() <= 0) {
				symptom.setLastEditDate(new Date());
				symptom.setLastEditUser(userService.getCurrentUser().getUsername());
				symptomService.save(symptom);
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
	
	
	protected void getSymptom(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {

		final String symptomId = req.getParameter("id");
		final Symptom symptom  = symptomService.getSymptom(symptomId,false);
		List dataItems = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			JSONObject data = new JSONObject();
			if (symptom != null) {
				data.put("id", symptom.getId());
				data.put("name", symptom.getName() );
				data.put("description", symptom.getDescription());
				data.put("loadSuccess", "true");

			}else{
				data.put("id", "");
				data.put("name", "");
				data.put("description", "");
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
	
	
	protected void deleteSymptom(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		final String symptomId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");

		try {
			final Symptom symptom = symptomService.getSymptom(symptomId,false);
			if (symptom != null) {
				symptomService.deleteSymptom(symptom);

			} else if (symptom == null) {
				errors.add("Sintoma no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar este sintoma: ");
				for (String error : errors) {
					out.println(error);
				}

			} else {
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deleteSymptom = " + e);

		}

	}
	
	
	protected void getRelatedSymptoms(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		String id = req.getParameter("symptomId") == null ? ""
				: URLDecoder.decode(req.getParameter("symptomId"), "UTF-8");
		String diseaseId = req.getParameter("id") == null ? ""
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
		queryParams.put("id", URLDecoder.decode(diseaseId, "UTF-8"));
		queryParams.put("name", URLDecoder.decode(name, "UTF-8"));

		String relatedSymptomsParam = req.getParameter("relatedSymptoms");
		List items = null;
		
	    Collection<Symptom> availableSymptoms = null;
	    
	    int total_count = 0;
			if (relatedSymptomsParam != null) {
				Symptom symptom = symptomService.getSymptom(id,false);
				if(symptom!=null){

				items = new ArrayList();
				genericService.fill(symptom, "symptoms");
				final Collection<Symptom> relatedSymptoms = symptom.getSymptoms();

				for (Symptom sym : relatedSymptoms) {
					if(!sym.getId().equals(symptom.getId())){
						JSONObject item = new JSONObject();
						item.put("id", sym.getId());
						item.put("name", sym.getName());
						items.add(item);
					}
					
				}
				 total_count = relatedSymptoms.size();
				}
			} else{
				
		        availableSymptoms = symptomService.getAvailableSymptoms(id, queryParams, dir, sort, limit_param, start_param);
				total_count = symptomService.getAvailableSymptomsCount(id, queryParams);
			
				items = new ArrayList();

				for (Symptom sym : availableSymptoms) {
					JSONObject item = new JSONObject();
					item.put("id", sym.getId());
					item.put("name", sym.getName());
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
	
	protected void getDrugs(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		String id = req.getParameter("symptomId") == null ? ""
				: URLDecoder.decode(req.getParameter("symptomId"), "UTF-8");
		String diseaseId = req.getParameter("id") == null ? ""
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
		queryParams.put("id", URLDecoder.decode(diseaseId, "UTF-8"));
		queryParams.put("name", URLDecoder.decode(name, "UTF-8"));

		String relatedDrugsParam = req.getParameter("relatedDrugs");
		List items = null;
		
	    Collection<Drug> availableDrugs = null;
	    
	    int total_count = 0;
			
	       
		
			if (relatedDrugsParam != null) {
				Symptom symptom = symptomService.getSymptom(id,false);
				if(symptom!=null){

				items = new ArrayList();
				genericService.fill(symptom, "drugs");
				final Collection<Drug> relatedDrugs = symptom.getDrugs();

				for (Drug drug : relatedDrugs) {
					JSONObject item = new JSONObject();
					item.put("id", drug.getId());
					item.put("name", drug.getName());
					items.add(item);
				}
				 total_count = relatedDrugs.size();
				}
			} else{
				availableDrugs = symptomService.getAvailableDrugs(id, queryParams, dir, sort, limit_param, start_param);
			    total_count = symptomService.getAvailableDrugsCount(id, queryParams);
				items = new ArrayList();

				for (Drug drug : availableDrugs) {
					JSONObject item = new JSONObject();
					item.put("id", drug.getId());
					item.put("name", drug.getName());
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
	
	
	
	

	protected void listSymptoms(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException {

		String id = req.getParameter("id") == null ? "" : req
				.getParameter("id");
		String name = req.getParameter("name") == null ? "" : req
				.getParameter("name");
		String[] relatedSymptoms = req.getParameter("symptoms") == null ? new String[0]: req.getParameter("symptoms").split(",");
		
		String[] diseases = req.getParameter("diseases") == null ? new String[0]: req.getParameter("diseases").split(",");
		
		String[] drugs = req.getParameter("drugs") == null ? new String[0]: req.getParameter("drugs").split(",");
		
		Map queryParams = null;
		if (!id.equals("") || !name.equals("") || (relatedSymptoms != null && relatedSymptoms.length >0) || (diseases != null && diseases.length >0) || (drugs != null && drugs.length >0)) {
			queryParams = new HashMap();
			
			queryParams.put("id", URLDecoder.decode(id, "UTF-8"));

			queryParams.put("name", URLDecoder.decode(name, "UTF-8"));
			
			queryParams.put("symptoms", Arrays.asList(relatedSymptoms));
			
			queryParams.put("diseases", Arrays.asList(diseases));
			
			queryParams.put("drugs", Arrays.asList(drugs));
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
 
		Collection<Symptom> symptoms = null;
		int total_count = 0;
		if (queryParams == null) {
			symptoms = symptomService.listSymptoms(dir, sort, limit_param, start_param);
			total_count = symptomService.getSymptomCount();

		} else {
			symptoms = symptomService.listSymptomsByQuery(queryParams, dir, sort,limit_param, start_param);
			total_count = symptomService.getSymptomCount(queryParams);

		}
		
		HttpSession session = req.getSession();
		
		if(session!=null && symptoms!=null){
			session.setAttribute("symptoms", symptoms);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (Symptom symptom : symptoms) {
			JSONObject item = new JSONObject();
			item.put("id", symptom.getId());
			item.put("name", symptom.getName());
			item.put("description", symptom.getDescription());
			item.put("lastEditDate", symptom.getLastEditDate() == null ? ""
					: format.format(symptom.getLastEditDate()));
			item.put("lastEditUser", symptom.getLastEditUser() == null ? "Sistema"
					: symptom.getLastEditUser());
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

	protected void exportSymptoms(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		HttpSession session = req.getSession();
		Collection<Symptom> symptoms = null;
		if(session!=null){
		   symptoms = (Collection<Symptom>)session.getAttribute("symptoms");
			
		}

		if (symptoms != null) {
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
				for (Symptom symptom : symptoms) {
					Map userMap = new TreeMap();
					userMap.put(1,symptom.getId());
					userMap.put(2,symptom.getName());
					userMap.put(3,symptom.getDescription().replaceAll("\\<.*?\\>", "").replaceAll("&nbsp;", ""));
					userMap.put(4,symptom.getLastEditDate() == null ? "" : format
							.format(symptom.getLastEditDate()));
					userMap.put(5,symptom.getLastEditUser() == null ? "Sistema"
							:symptom.getLastEditUser());
					values.add(userMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				"attachment; filename=\"sintomas.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Sintomas")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportSymptoms = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}
}



