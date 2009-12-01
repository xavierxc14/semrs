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
import com.googlecode.semrs.model.LabTest;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.DiseaseService;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.ProcedureService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.ExcelExporter;

public class ProcedureServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(ProcedureServlet.class);
	
	private ProcedureService procedureService;
	
	private DiseaseService diseaseService;
	
	private UserService userService;
	
	private GenericService genericService;
	
	

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.userService  = (UserService) factory.getBean("userService");
		this.procedureService  = (ProcedureService) factory.getBean("procedureService");
		this.diseaseService = (DiseaseService) factory.getBean("diseaseService");
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

			String procedureEdit = req.getParameter("procedureEdit");
			String export = req.getParameter("export");
			if (procedureEdit != null) {
				if (procedureEdit.equals("load")) {

				    getProcedure(req, resp);

				} else if (procedureEdit.equals("submit")) {

					saveProcedure(req, resp);

				} else if (procedureEdit.equals("delete")) {

					deleteProcedure(req, resp);

				} else if (procedureEdit.equals("getDiseases")) {

					getDiseases(req, resp);

				}

			} else if (export != null) {

				exportProcedures(req, resp);

			} else {
				listProcedures(req, resp);
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {

			LOG.error("Error in ProcedureServlet = " + ex);
		}
	}
	
	
	protected void saveProcedure(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		final boolean isNew = (req.getParameter("isNew") != null && (req
				.getParameter("isNew").equals("") || req.getParameter("isNew")
				.equals("true"))) ? true : false;
		final String procedureId = req.getParameter("id") == null ? "" : URLDecoder
				.decode(req.getParameter("id"), "UTF-8");
		final String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		final String description = req.getParameter("description") == null ? ""
				: URLDecoder.decode(req.getParameter("description"), "UTF-8");
		final String[] diseases = req.getParameter("procedureDiseases") == null ? new String[0]
				: req.getParameter("procedureDiseases").split(",");

	    Procedure procedure = procedureService.getProcedure(procedureId,false);

		if (procedure != null && !isNew) {
			if (name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!name.equals("")) {
				procedure.setName(name);
				procedure.setDescription(description);

				if (diseases != null) {
					if (diseases.length > 0) {
						Disease disease = null;
						Collection<Disease> assignedDiseases = new ArrayList<Disease>();
						for (String assignedDisease : diseases) {
							if (!assignedDiseases.equals("")) {
								disease = diseaseService.getDisease(URLDecoder
										.decode(assignedDisease, "UTF-8"),false);
								if (disease != null) {
									assignedDiseases.add(disease);
								}
							}
						}

						if (assignedDiseases != null || assignedDiseases.size() > 0) {
							procedure.setRelatedDiseasesP(assignedDiseases);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				procedure.setLastEditDate(new Date());
				procedure.setLastEditUser(userService.getCurrentUser().getUsername());
				procedureService.save(procedure);
			}

		} else if (procedure != null && isNew) {
			errors.add("C&oacute;digo ya existe");
		} else if (procedure == null && isNew) {

			procedure = new Procedure();

			if (procedureId.equals("") || name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			}else if(procedureId.contains("@")){
				errors.add(" el c&oacute;digo contiene car&aacute;cteres no v&aacute;lidos");
			}else if (!procedureId.equals("") && !name.equals("")) {

				procedure.setId(procedureId);
				procedure.setName(name);
				procedure.setDescription(description);

				if (diseases != null) {

					if (!diseases.equals("") && diseases.length > 0) {
						Disease disease = null;
						Collection<Disease> assignedDiseases = new ArrayList<Disease>();
						for (String assignedDisease : diseases) {
							if (!assignedDisease.equals("")) {
								disease = diseaseService.getDisease(assignedDisease,false);
								if (disease != null) {
									assignedDiseases.add(disease);
								}
							}
						}

						if (assignedDiseases != null && assignedDiseases.size() > 0) {
							procedure.setRelatedDiseasesP(assignedDiseases);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				procedure.setLastEditDate(new Date());
				procedure.setLastEditUser(userService.getCurrentUser().getUsername());
				procedureService.save(procedure);
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
	
	
	protected void getProcedure(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		Procedure procedure = null;
		final String procedureId = req.getParameter("id");
			procedure = procedureService.getProcedure(procedureId,false);
		List dataItems = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			JSONObject data = new JSONObject();
			if (procedure != null) {
				data.put("id", procedure.getId());
				data.put("name", procedure.getName() );
				data.put("description", procedure.getDescription());
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
	
	
	protected void deleteProcedure(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		final String procedureId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");

		try {
			Procedure procedure = procedureService.getProcedure(procedureId,false);
			if (procedure != null) {
				procedureService.deleteProcedure(procedure);

			} else if (procedure == null) {
				errors.add("Procedimiento no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar este procedimiento: ");
				for (String error : errors) {
					out.println(error);
				}

			} else {
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deleteProcedure = " + e);

		}

	}
	
	
	protected void getDiseases(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		String procedureId = req.getParameter("procedureId") == null ? ""
				: URLDecoder.decode(req.getParameter("procedureId"), "UTF-8");
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

		
		
		String procedureDiseasesParam = req.getParameter("procedureDiseases");
		List items = null;
		
	    Collection<Disease> availableDiseases = null;
	    
	    int total_count = 0;
	
			if (procedureDiseasesParam != null) {
				Procedure procedure = procedureService.getProcedure(procedureId,false);
				if(procedure!=null){

				items = new ArrayList();
				genericService.fill(procedure, "relatedDiseasesP");
				final Collection<Disease> procedureDiseases = procedure.getRelatedDiseasesP();

				for (Disease disease : procedureDiseases) {
					JSONObject item = new JSONObject();
					item.put("id", disease.getId());
					item.put("name", disease.getName());
					items.add(item);
				}
				 total_count = procedureDiseases.size();
				}
			} else{
				availableDiseases = procedureService.getAvailableDiseases(procedureId, queryParams, dir, sort, limit_param, start_param);
				total_count = procedureService.getAvailableDiseasesCount(procedureId, queryParams);
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
	

	protected void listProcedures(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException {

		String id = req.getParameter("id") == null ? "" : req
				.getParameter("id");
		String name = req.getParameter("name") == null ? "" : req
				.getParameter("name");
		String[] diseases = req.getParameter("diseases") == null ? new String[0]: req.getParameter("diseases").split(",");
		
		Map queryParams = null;
		if (!id.equals("") || !name.equals("") || (diseases != null && diseases.length >0)) {
			queryParams = new HashMap();
			
			queryParams.put("id", URLDecoder.decode(id, "UTF-8"));

			queryParams.put("name", URLDecoder.decode(name, "UTF-8"));
			
			queryParams.put("diseases", Arrays.asList(diseases));
			
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

		Collection<Procedure> procedures = null;
		int total_count = 0;
		if (queryParams == null) {
			procedures = procedureService.listProcedures(dir, sort, limit_param, start_param);
			total_count = procedureService.getProcedureCount();

		} else {
			procedures = procedureService.listProceduresByQuery(queryParams, dir, sort,limit_param, start_param);
			total_count = procedureService.getProcedureCount(queryParams);

		}
		
		HttpSession session = req.getSession();
		if(session!=null && procedures!=null){
			session.setAttribute("procedures", procedures);
			
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (Procedure procedure : procedures) {
			JSONObject item = new JSONObject();
			item.put("id", procedure.getId());
			item.put("name", procedure.getName());
			item.put("description", procedure.getDescription());
			item.put("lastEditDate", procedure.getLastEditDate() == null ? ""
					: format.format(procedure.getLastEditDate()));
			item.put("lastEditUser", procedure.getLastEditUser() == null ? "Sistema"
					: procedure.getLastEditUser());
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

	protected void exportProcedures(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		
		OutputStream out = null;
		Collection<Procedure> procedures = null;
		HttpSession session = req.getSession();
		if(session!=null){
			procedures = (Collection<Procedure>)session.getAttribute("procedures");
		}

		if (procedures != null) {
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
				for (Procedure procedure : procedures) {
					Map userMap = new TreeMap();
					userMap.put(1, procedure.getId());
					userMap.put(2, procedure.getName());
					userMap.put(3, procedure.getDescription().replaceAll("\\<.*?\\>", "").replaceAll("&nbsp;", ""));
					userMap.put(4, procedure.getLastEditDate() == null ? "" : format
							.format(procedure.getLastEditDate()));
					userMap.put(5, procedure.getLastEditUser() == null ? "Sistema"
							: procedure.getLastEditUser());
					values.add(userMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				"attachment; filename=\"procedimientos.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Procedimientos")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportProcedures = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}
     
}


