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
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.semrs.model.Drug;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.service.DrugService;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.SymptomService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.ExcelExporter;

public class DrugServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(DrugServlet.class);
	
	private DrugService drugService;
	
	private SymptomService symptomService;
	
	private UserService userService;
	
	private GenericService genericService;
	
	

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.userService  = (UserService) factory.getBean("userService");
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

			String drugEdit = req.getParameter("drugEdit");
			String export = req.getParameter("export");
			if (drugEdit != null) {
				if (drugEdit.equals("load")) {

				    getDrug(req, resp);

				} else if (drugEdit.equals("submit")) {

					saveDrug(req, resp);

				} else if (drugEdit.equals("delete")) {

					deleteDrug(req, resp);

				} else if (drugEdit.equals("getSymptoms")) {

					getSymptoms(req, resp);

				} 
			} else if (export != null) {

				exportDrugs(req, resp);

			} else {
				listDrugs(req, resp);
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {

			LOG.error("Error in DrugServlet = " + ex);
		}
	}
	
	
	protected void saveDrug(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		boolean isNew = (req.getParameter("isNew") != null && (req
				.getParameter("isNew").equals("") || req.getParameter("isNew")
				.equals("true"))) ? true : false;
		String drugId = req.getParameter("id") == null ? "" : URLDecoder
				.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String description = req.getParameter("description") == null ? ""
				: URLDecoder.decode(req.getParameter("description"), "UTF-8");
		String[] symptoms = req.getParameter("drugSymptoms") == null ? new String[0]
				: req.getParameter("drugSymptoms").split(",");

		Drug drug = drugService.getDrug(drugId,false);

		if (drug != null && !isNew) {
			if (name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!name.equals("")) {
				drug.setName(name);

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
							drug.setSymptoms(assignedSymptoms);
						}
					}
				}
				
			}
			if (errors == null || errors.size() <= 0) {
				drug.setLastEditDate(new Date());
				drug.setLastEditUser(userService.getCurrentUser().getUsername());
				drugService.save(drug);
			}

		} else if (drug != null && isNew) {
			errors.add("C&oacute;digo ya existe");
		} else if (drug == null && isNew) {

			drug = new Drug();

			if (drugId.equals("") || name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			}else if(drugId.contains("@")){
				errors.add(" el c&oacute;digo contiene car&aacute;cteres no v&aacute;lidos");
		    }else if (!drugId.equals("") && !name.equals("")) {

				drug.setId(drugId);
				drug.setName(name);
				drug.setDescription(description);

				Collection<Symptom> assignedSymptoms = new ArrayList<Symptom>();
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

						if (assignedSymptoms != null && assignedSymptoms.size() > 0) {
							drug.setSymptoms(assignedSymptoms);
						}
					}
				}
				
			}
			if (errors == null || errors.size() <= 0) {
				drug.setLastEditDate(new Date());
				drug.setLastEditUser(userService.getCurrentUser().getUsername());
				drugService.save(drug);
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
	
	
	protected void getDrug(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		Drug drug = null;
		String drugId = req.getParameter("id");
			drug = drugService.getDrug(drugId,false);
		List dataItems = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			JSONObject data = new JSONObject();
			if (drug != null) {
				data.put("id", drug.getId());
				data.put("name", drug.getName() );
				data.put("description", drug.getDescription());
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
	
	
	protected void deleteDrug(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		String drugId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");

		try {
			Drug drug = drugService.getDrug(drugId,false);
			if (drug != null) {
				drugService.deleteDrug(drug);

			} else if (drug == null) {
				errors.add("Medicamento no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar este medicamento: ");
				for (String error : errors) {
					out.println(error);
				}

			} else {
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deleteDrug = " + e);

		}

	}
	
	
	protected void getSymptoms(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {


		String drugId = req.getParameter("drugId") == null ? ""
				: URLDecoder.decode(req.getParameter("drugId"), "UTF-8");
		String symptomId = req.getParameter("id") == null ? ""
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
	    queryParams.put("id", URLDecoder.decode(symptomId, "UTF-8"));
		queryParams.put("name", URLDecoder.decode(name, "UTF-8"));


		String drugSymptomsParam = req.getParameter("drugSymptoms");
		List items = null;
		
	    Collection<Symptom> availableSymptoms = null;
	    
	    int total_count = 0;
			if (drugSymptomsParam != null) {
				Drug drug = drugService.getDrug(drugId,false);
				if(drug!=null){

				items = new ArrayList();
				genericService.fill(drug,"symptoms");
				final Collection<Symptom> drugSymptoms = drug.getSymptoms();

				for (Symptom symp : drugSymptoms) {
					JSONObject item = new JSONObject();
					item.put("id", symp.getId());
					item.put("name", symp.getName());
					items.add(item);
				}
				 total_count = drugSymptoms.size();
				}
			} else{
		        availableSymptoms = drugService.getAvailableSymptoms(drugId, queryParams, dir, sort, limit_param, start_param);
			 	total_count = drugService.getAvailableSymptomsCount(drugId, queryParams);
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
	
	

	protected void listDrugs(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException {

		String id = req.getParameter("id") == null ? "" : req
				.getParameter("id");
		String name = req.getParameter("name") == null ? "" : req
				.getParameter("name");
		String[] symptoms = req.getParameter("symptoms") == null ? new String[0]: req.getParameter("symptoms").split(",");
		
		Map queryParams = null;
		if (!id.equals("") || !name.equals("") || (symptoms != null && symptoms.length >0)) {
			queryParams = new HashMap();
			
			queryParams.put("id", URLDecoder.decode(id, "UTF-8"));

			queryParams.put("name", URLDecoder.decode(name, "UTF-8"));
			
			queryParams.put("symptoms", Arrays.asList(symptoms));
			
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

		Collection<Drug> drugs = null;
		int total_count = 0;
		if (queryParams == null) {
			drugs = drugService.listDrugs(dir, sort, limit_param, start_param);
			total_count = drugService.getDrugCount();

		} else {
			drugs = drugService.listDrugsByQuery(queryParams, dir, sort,limit_param, start_param);
			total_count = drugService.getDrugCount(queryParams);

		}
		
		HttpSession session = req.getSession();
		if(session!=null && drugs!=null){
			session.setAttribute("drugs", drugs);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (Drug drug : drugs) {
			JSONObject item = new JSONObject();
			item.put("id", drug.getId());
			item.put("name", drug.getName());
			item.put("description", drug.getDescription());
			item.put("lastEditDate", drug.getLastEditDate() == null ? ""
					: format.format(drug.getLastEditDate()));
			item.put("lastEditUser", drug.getLastEditUser() == null ? "Sistema"
					: drug.getLastEditUser());
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

	protected void exportDrugs(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		HttpSession session = req.getSession();
		Collection<Drug> drugs = null;
		if(session!=null){
			drugs = (Collection<Drug>)session.getAttribute("drugs");
		}

		if (drugs != null) {
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
				for (Drug drug : drugs) {
					Map userMap = new TreeMap();
					userMap.put(1, drug.getId());
					userMap.put(2, drug.getName());
					userMap.put(3, drug.getDescription().replaceAll("\\<.*?\\>", "").replaceAll("&nbsp;", ""));
					userMap.put(4, drug.getLastEditDate() == null ? "" : format
							.format(drug.getLastEditDate()));
					userMap.put(5, drug.getLastEditUser() == null ? "Sistema"
							: drug.getLastEditUser());
					values.add(userMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				"attachment; filename=\"medicamentos.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Medicamentos")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportDrugs = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}
}
