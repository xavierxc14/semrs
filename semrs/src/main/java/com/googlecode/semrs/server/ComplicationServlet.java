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

import com.googlecode.semrs.model.Complication;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.Symptom;
import com.googlecode.semrs.server.service.ComplicationService;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.ProcedureService;
import com.googlecode.semrs.server.service.SymptomService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.ExcelExporter;

public class ComplicationServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(ComplicationServlet.class);

	private SymptomService symptomService;
	
	private ProcedureService procedureService;
	
	private ComplicationService complicationService;
	
	private UserService userService;
	
	private GenericService genericService;

	
	

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.userService  = (UserService) factory.getBean("userService");
		this.symptomService = (SymptomService) factory.getBean("symptomService");
		this.procedureService = (ProcedureService) factory.getBean("procedureService");
		this.complicationService = (ComplicationService) factory.getBean("complicationService");
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

			String complicationEdit = req.getParameter("complicationEdit");
			String export = req.getParameter("export");
			if (complicationEdit != null) {
				if (complicationEdit.equals("load")) {

				    getComplication(req, resp);

				} else if (complicationEdit.equals("submit")) {

					saveComplication(req, resp);

				} else if (complicationEdit.equals("delete")) {

					deleteComplication(req, resp);

				} else if (complicationEdit.equals("getSymptoms")) {

					getSymptoms(req, resp);

				}else if (complicationEdit.equals("getProcedures")) {

					getProcedures(req, resp);

				}

			} else if (export != null) {

				exportComplications(req, resp);

			} else {
				listComplications(req, resp);
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {

			LOG.error("Error in ComplicationServlet = " + ex);
		}
	}
	
	
	protected void saveComplication(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		boolean isNew = (req.getParameter("isNew") != null && (req
				.getParameter("isNew").equals("") || req.getParameter("isNew")
				.equals("true"))) ? true : false;
		String complicationId = req.getParameter("id") == null ? "" : URLDecoder
				.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String description = req.getParameter("description") == null ? ""
				: URLDecoder.decode(req.getParameter("description"), "UTF-8");
		String[] symptoms = req.getParameter("complicationSymptoms") == null ? new String[0]
				: req.getParameter("complicationSymptoms").split(",");
		String[] procedures = req.getParameter("complicationProcedures") == null ? new String[0]
		        : req.getParameter("complicationProcedures").split(",");

		Complication complication = complicationService.getComplication(complicationId,false);

		if (complication != null && !isNew) {
			if (name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!name.equals("")) {
				complication.setName(name);
				complication.setDescription(description);

				if (symptoms != null) {
					if (symptoms.length > 0) {
						Symptom symptom = null;
						Collection<Symptom> assignedSymptoms = new ArrayList<Symptom>();
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
							complication.setSymptoms(assignedSymptoms);
						}
					}
				}
				
				if (procedures != null) {
					if (procedures.length > 0) {
						Procedure procedure = null;
						Collection<Procedure> assignedProcedures = new ArrayList<Procedure>();
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
							complication.setComplicationProcedures(assignedProcedures);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				complication.setLastEditDate(new Date());
				complication.setLastEditUser(userService.getCurrentUser().getUsername());
				complicationService.save(complication);
			}

		} else if (complication != null && isNew) {
			errors.add("Complicaci&oacute;n ya existe");
		} else if (complication == null && isNew) {

			complication = new Complication();

			if (complicationId.equals("") || name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			}else if(complicationId.contains("@")){
					errors.add(" el c&oacute;digo contiene car&aacute;cteres no v&aacute;lidos");
			}else if (!complicationId.equals("") && !name.equals("")) {

				complication.setId(complicationId);
				complication.setName(name);
				complication.setDescription(description);

				if (symptoms != null) {

					if (!symptoms.equals("") && symptoms.length > 0) {
						Symptom symptom = null;
						Collection<Symptom> assignedSymptoms = new ArrayList<Symptom>();
						for (String assignedSymptom : symptoms) {
							if (!assignedSymptom.equals("")) {
								symptom = symptomService.getSymptom(assignedSymptom,false);
								if (symptom != null) {
									assignedSymptoms.add(symptom);
								}
							}
						}

						if (assignedSymptoms != null && assignedSymptoms.size() > 0) {
							complication.setSymptoms(assignedSymptoms);
						}
					}
				}
				
				if (procedures != null) {
					if (procedures.length > 0) {
						Procedure procedure = null;
						Collection<Procedure> assignedProcedures = new ArrayList<Procedure>();
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
							complication.setComplicationProcedures(assignedProcedures);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				complication.setLastEditDate(new Date());
				complication.setLastEditUser(userService.getCurrentUser().getUsername());
				complicationService.save(complication);
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
	
	
	protected void getComplication(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		Complication complication = null;
		String complicationId = req.getParameter("id");
			complication = complicationService.getComplication(complicationId,false);
		List dataItems = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			JSONObject data = new JSONObject();
			if (complication != null) {
				data.put("id", complication.getId());
				data.put("name", complication.getName() );
				data.put("description", complication.getDescription());
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
	
	
	protected void deleteComplication(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		String complicationId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");

		try {
			Complication complication = complicationService.getComplication(complicationId,false);
			if (complication != null) {
				complicationService.deleteComplication(complication);

			} else if (complication == null) {
				errors.add("Complicaci&oacute;n no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar esta complicaci&oacute;n: ");
				for (String error : errors) {
					out.println(error);
				}

			} else {
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deleteComplication = " + e);

		}

	}
	
	
	protected void getSymptoms(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {


		String complicationId = req.getParameter("complicationId") == null ? ""
				: URLDecoder.decode(req.getParameter("complicationId"), "UTF-8");
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


		String complicationSymptomsParam = req.getParameter("complicationSymptoms");
		List items = null;
		
	    Collection<Symptom> availableSymptoms = null;
	    
	    int total_count = 0;
		
			if (complicationSymptomsParam != null) {
				Complication complication = complicationService.getComplication(complicationId,false);
				if(complication!=null){

				items = new ArrayList();
				genericService.fill(complication, "symptoms");
				final Collection<Symptom> drugSymptoms = complication.getSymptoms();

				for (Symptom symp : drugSymptoms) {
					JSONObject item = new JSONObject();
					item.put("id", symp.getId());
					item.put("name", symp.getName());
					items.add(item);
				}
				 total_count = drugSymptoms.size();
				}
			} else{
				
		        availableSymptoms = complicationService.getAvailableSymptoms(complicationId, queryParams, dir, sort, limit_param, start_param);
			 	total_count = complicationService.getAvailableSymptomsCount(complicationId, queryParams);
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
	
	
	
	protected void getProcedures(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {


		String complicationId = req.getParameter("complicationId") == null ? ""
				: URLDecoder.decode(req.getParameter("complicationId"), "UTF-8");
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


		String complicationProceduresParam = req.getParameter("complicationProcedures");
		List items = null;
		
	    Collection<Procedure> availableProcedures= null;
	    
	    int total_count = 0;
			if (complicationProceduresParam != null) {
				Complication complication = complicationService.getComplication(complicationId,false);
				if(complication!=null){

				items = new ArrayList();
				genericService.fill(complication, "complicationProcedures");
				final Collection<Procedure> complicationProcedures = complication.getComplicationProcedures();

				for (Procedure proc : complicationProcedures) {
					JSONObject item = new JSONObject();
					item.put("id", proc.getId());
					item.put("name", proc.getName());
					items.add(item);
				}
				 total_count = complicationProcedures.size();
				}
			} else{

		        availableProcedures = complicationService.getAvailableProcedures(complicationId, queryParams, dir, sort, limit_param, start_param);
			 	total_count = complicationService.getAvailableProceduresCount(complicationId, queryParams);
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
	
	

	protected void listComplications(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException {

		String id = req.getParameter("id") == null ? "" : req
				.getParameter("id");
		String name = req.getParameter("name") == null ? "" : req
				.getParameter("name");
		String[] symptoms = req.getParameter("symptoms") == null ? new String[0]: req.getParameter("symptoms").split(",");
		
		String[] procedures = req.getParameter("procedures") == null ? new String[0]: req.getParameter("procedures").split(",");
		
		Map queryParams = null;
		if (!id.equals("") || !name.equals("") || (symptoms != null && symptoms.length >0) || (procedures != null && procedures.length >0)) {
			queryParams = new HashMap();
			
			queryParams.put("id", URLDecoder.decode(id, "UTF-8"));

			queryParams.put("name", URLDecoder.decode(name, "UTF-8"));
			
			queryParams.put("symptoms", Arrays.asList(symptoms));
			
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
		
		Collection<Complication> complications = null;
	    int total_count = 0;
		if (queryParams == null) {
			complications = complicationService.listComplications(dir, sort, limit_param, start_param);
			total_count = complicationService.getComplicationCount();

		} else {
			complications = complicationService.listComplicationsByQuery(queryParams, dir, sort,limit_param, start_param);
			total_count = complicationService.getComplicationCount(queryParams);

		}
		
		HttpSession session = req.getSession();
		if(session!=null && complications!=null){
			session.setAttribute("complications", complications);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (Complication complication : complications) {
			JSONObject item = new JSONObject();
			item.put("id", complication.getId());
			item.put("name", complication.getName());
			item.put("description", complication.getDescription());
			item.put("lastEditDate", complication.getLastEditDate() == null ? ""
					: format.format(complication.getLastEditDate()));
			item.put("lastEditUser", complication.getLastEditUser() == null ? "Sistema"
					: complication.getLastEditUser());
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

	protected void exportComplications(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		HttpSession session = req.getSession();
		Collection<Complication> complications = null;
		if(session!=null){
			 complications = (Collection<Complication>)session.getAttribute("complications");
		}

		if (complications != null) {
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
				for (Complication complication : complications) {
					Map userMap = new TreeMap();
					userMap.put(1,complication.getId());
					userMap.put(2,complication.getName());
					userMap.put(3,complication.getDescription().replaceAll("\\<.*?\\>", "").replaceAll("&nbsp;", ""));
					userMap.put(4,complication.getLastEditDate() == null ? "" : format
							.format(complication.getLastEditDate()));
					userMap.put(5,complication.getLastEditUser() == null ? "Sistema"
							:complication.getLastEditUser());
					values.add(userMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				"attachment; filename=\"complicaciones.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Complicaciones")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportComplications = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}

}



