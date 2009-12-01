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

public class LabTestServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(LabTestServlet.class);
	
	private LabTestService labTestService;
	
	private DiseaseService diseaseService;
	
	private GenericService genericService;
	
	private UserService userService;
	
	

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.userService  = (UserService) factory.getBean("userService");
		this.labTestService  = (LabTestService) factory.getBean("labTestService");
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

			String labTestEdit = req.getParameter("labTestEdit");
			String export = req.getParameter("export");
			if (labTestEdit != null) {
				if (labTestEdit.equals("load")) {

				    getLabTest(req, resp);

				} else if (labTestEdit.equals("submit")) {

					saveLabTest(req, resp);

				} else if (labTestEdit.equals("delete")) {

					deleteLabTest(req, resp);

				} else if (labTestEdit.equals("getDiseases")) {

					getDiseases(req, resp);

				}

			} else if (export != null) {

				exportLabTests(req, resp);

			} else {
				listLabTests(req, resp);
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {

			LOG.error("Error in LabTestServlet = " + ex);
		}
	}
	
	
	protected void saveLabTest(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		boolean isNew = (req.getParameter("isNew") != null && (req
				.getParameter("isNew").equals("") || req.getParameter("isNew")
				.equals("true"))) ? true : false;
		String labTestId = req.getParameter("id") == null ? "" : URLDecoder
				.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String description = req.getParameter("description") == null ? ""
				: URLDecoder.decode(req.getParameter("description"), "UTF-8");
		String[] diseases = req.getParameter("labTestDiseases") == null ? new String[0]
				: req.getParameter("labTestDiseases").split(",");

		LabTest labTest = labTestService.getLabTest(labTestId,false);

		if (labTest != null && !isNew) {
			if (name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!name.equals("")) {
				labTest.setName(name);
				labTest.setDescription(description);

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
							labTest.setRelatedDiseases(assignedDiseases);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				labTest.setLastEditDate(new Date());
				labTest.setLastEditUser(userService.getCurrentUser().getUsername());
				labTestService.save(labTest);
			}

		} else if (labTest != null && isNew) {
			errors.add("C&oacute;digo ya existe");
		} else if (labTest == null && isNew) {

			labTest = new LabTest();

			if (labTestId.equals("") || name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			}else if(labTestId.contains("@")){
				errors.add(" el c&oacute;digo contiene car&aacute;cteres no v&aacute;lidos");
		    }else if (!labTestId.equals("") && !name.equals("")) {

				labTest.setId(labTestId);
				labTest.setName(name);
				labTest.setDescription(description);

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
							labTest.setRelatedDiseases(assignedDiseases);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				labTest.setLastEditDate(new Date());
				labTest.setLastEditUser(userService.getCurrentUser().getUsername());
				labTestService.save(labTest);
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
	
	
	protected void getLabTest(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		LabTest labTest = null;
		String labTestId = req.getParameter("id");
			labTest = labTestService.getLabTest(labTestId,false);
		List dataItems = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			JSONObject data = new JSONObject();
			if (labTest != null) {
				data.put("id", labTest.getId());
				data.put("name", labTest.getName() );
				data.put("description", labTest.getDescription());
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
	
	
	protected void deleteLabTest(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		String labTestId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");

		try {
			LabTest labTest = labTestService.getLabTest(labTestId,false);
			if (labTest != null) {
				labTestService.deleteLabTest(labTest);

			} else if (labTest == null) {
				errors.add("Examen no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar este examen: ");
				for (String error : errors) {
					out.println(error);
				}

			} else {
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deleteLabTest = " + e);

		}

	}
	
	
	protected void getDiseases(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		String labTestId = req.getParameter("labTestId") == null ? ""
				: URLDecoder.decode(req.getParameter("labTestId"), "UTF-8");
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

		String labTestDiseasesParam = req.getParameter("labTestDiseases");
		List items = null;
		
	    Collection<Disease> availableDiseases = null;
	    
	    int total_count = 0;
		
			if (labTestDiseasesParam != null) {
				LabTest labTest = labTestService.getLabTest(labTestId,false);
				if(labTest!=null){

				items = new ArrayList();
				genericService.fill(labTest, "relatedDiseases");
				final Collection<Disease> labTestDiseases = labTest.getRelatedDiseases();

				for (Disease disease : labTestDiseases) {
					JSONObject item = new JSONObject();
					item.put("id", disease.getId());
					item.put("name", disease.getName());
					items.add(item);
				}
				 total_count = labTestDiseases.size();
				}
			} else{

				availableDiseases = labTestService.getAvailableDiseases(labTestId, queryParams, dir, sort, limit_param, start_param);
				total_count = labTestService.getAvailableDiseasesCount(labTestId, queryParams);
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
	

	protected void listLabTests(HttpServletRequest req, HttpServletResponse resp)
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

		Collection<LabTest> labTests = null;
		int total_count = 0;
		if (queryParams == null) {
			labTests = labTestService.listLabTests(dir, sort, limit_param, start_param);
			total_count = labTestService.getLabTestCount();

		} else {
			labTests = labTestService.listLabTestsByQuery(queryParams, dir, sort,limit_param, start_param);
			total_count = labTestService.getLabTestCount(queryParams);

		}
		
		HttpSession session = req.getSession();
		if(session!=null && labTests!=null){
			session.setAttribute("labTests", labTests);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (LabTest labTest : labTests) {
			JSONObject item = new JSONObject();
			item.put("id", labTest.getId());
			item.put("name", labTest.getName());
			item.put("description", labTest.getDescription());
			item.put("lastEditDate", labTest.getLastEditDate() == null ? ""
					: format.format(labTest.getLastEditDate()));
			item.put("lastEditUser", labTest.getLastEditUser() == null ? "Sistema"
					: labTest.getLastEditUser());
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

	protected void exportLabTests(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		Collection<LabTest> labTests = null;
		HttpSession session = req.getSession();
		if(session!=null){
			 labTests = (Collection<LabTest>)session.getAttribute("labTests");
		}

		if (labTests != null) {
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
				for (LabTest labTest : labTests) {
					Map userMap = new TreeMap();
					userMap.put(1, labTest.getId());
					userMap.put(2, labTest.getName());
					userMap.put(3, labTest.getDescription().replaceAll("\\<.*?\\>", "").replaceAll("&nbsp;", ""));
					userMap.put(4, labTest.getLastEditDate() == null ? "" : format
							.format(labTest.getLastEditDate()));
					userMap.put(5, labTest.getLastEditUser() == null ? "Sistema"
							: labTest.getLastEditUser());
					values.add(userMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				"attachment; filename=\"examenes_laboratorio.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Examenes")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportLabTests = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}
}

