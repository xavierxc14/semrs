package com.googlecode.semrs.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.googlecode.semrs.model.Module;
import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.RoleService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.ExcelExporter;

public class RoleServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(GroupServlet.class);

	private UserService userService;
	
	private RoleService roleService;
	
	private GenericService genericService;


	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.userService  = (UserService) factory.getBean("userService");
		this.roleService = (RoleService) factory.getBean("roleService");
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

			String roleEdit = req.getParameter("roleEdit");
			String export = req.getParameter("export");
			if (roleEdit != null) {
				if (roleEdit.equals("load")) {

				    getRole(req, resp);

				} else if (roleEdit.equals("submit")) {

					saveRole(req, resp);

				} else if (roleEdit.equals("delete")) {

					deleteRole(req, resp);

				} else if (roleEdit.equals("getModules")) {

					getModules(req, resp);

				}

			} else if (export != null) {

				exportRoles(req, resp);

			} else {
				listRoles(req, resp);
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {

			LOG.error("Error in GroupServlet = " + ex);
		}
	}
	
	
	protected void saveRole(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		boolean isNew = (req.getParameter("isNew") != null && (req
				.getParameter("isNew").equals("") || req.getParameter("isNew")
				.equals("true"))) ? true : false;
		String roleId = req.getParameter("id") == null ? "" : URLDecoder
				.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String description = req.getParameter("description") == null ? ""
				: URLDecoder.decode(req.getParameter("description"), "UTF-8");
		String[] modules = req.getParameter("modules") == null ? new String[0]
				: req.getParameter("modules").split(",");

		Role role = roleService.getRole(roleId,false);

		if (role != null && !isNew) {
			if (name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!name.equals("")) {
				role.setDescription(name);
				role.setLongDescription(description);

				if (modules != null) {
					if (modules.length > 0) {
						Module module = null;
						Collection<Module> assignedModules = new ArrayList<Module>();
						for (String assignedModule : modules) {
							if (!assignedModule.equals("")) {
								module = roleService.getModule(URLDecoder
										.decode(assignedModule, "UTF-8"));
								if (module != null) {
									assignedModules.add(module);
								}
							}
						}

						if (assignedModules != null || assignedModules.size() > 0) {
							role.setModules(assignedModules);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				role.setLastEditDate(new Date());
				role.setLastEditUser(userService.getCurrentUser().getUsername());
				roleService.save(role);
			}

		} else if (role != null && isNew) {
			errors.add("Rol ya existe");
		} else if (role == null && isNew) {

			role = new Role();

			if (roleId.equals("") || name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!roleId.equals("") && !name.equals("")) {

				role.setAuth(roleId);
				role.setDescription(name);
				role.setLongDescription(description);

				if (modules != null) {

					if (!modules.equals("") && modules.length > 0) {
						Module module = null;
						Collection<Module> assignedModules = new ArrayList<Module>();
						for (String assignedModule : modules) {
							if (!assignedModule.equals("")) {
								module = roleService.getModule(assignedModule);
								if (module != null) {
									assignedModules.add(module);
								}
							}
						}

						if (assignedModules != null && assignedModules.size() > 0) {
							role.setModules(assignedModules);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				role.setLastEditDate(new Date());
				role.setLastEditUser(userService.getCurrentUser().getUsername());
				roleService.save(role);
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
	
	
	protected void getRole(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		Role role = null;
		final String roleId = req.getParameter("id");
		role = roleService.getRole(roleId,false);
		List dataItems = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			JSONObject data = new JSONObject();
			if (role != null) {
				data.put("id", role.getAuth());
				data.put("name", role.getDescription() );
				data.put("description", role.getLongDescription());
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

	
	protected void deleteRole(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		final String roleId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");

		try {
			final Role role = roleService.getRole(roleId,false);
			if (role != null) {
				   roleService.deleteRole(role);

			} else if (role == null) {
				errors.add("Rol no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar este rol: ");
				for (String error : errors) {
					out.println(error);
				}

			} else {
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deleteRole = " + e);

		}

	}
	
	
	protected void getModules(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		final String roleId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		final Role role = roleService.getRole(roleId,false);
		genericService.fill(role,"modules");
		final Collection<Module> availableModules = roleService.getAvailableModules(role);

		final String roleModulesParam = req.getParameter("roleModules");
		List items = null;
		
		if (role != null) {
			
			final Collection<Module> roleModules = role.getModules();
			
		
			if (roleModulesParam != null) {

				items = new ArrayList();

				for (Module module : roleModules) {
					JSONObject item = new JSONObject();
					item.put("id", module.getId());
					item.put("desc", module.getTitle());
					items.add(item);
				}

			} else {
				

				items = new ArrayList();
				//availableModules = roleService.getModules();
				for (Module module : availableModules) {
					JSONObject item = new JSONObject();
					item.put("id", module.getId());
					item.put("desc", module.getTitle());
					items.add(item);
			}
		}
	

		} else {
			items = new ArrayList();
			//availableModules = roleService.getModules();
			for (Module module : availableModules) {
				JSONObject item = new JSONObject();
				item.put("id", module.getId());
				item.put("desc", module.getTitle());
				items.add(item);
			}
		
			
		}

		JSONObject feeds = new JSONObject();
		feeds.put("data", new JSONArray(items));
		feeds.put("success", true);
		resp.setContentType("application/json; charset=utf-8");
		Writer w = new OutputStreamWriter(resp.getOutputStream(), "utf-8");
		w.write(feeds.toString());
		w.close();
		resp.setStatus(HttpServletResponse.SC_OK);

	}
	

	protected void listRoles(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException {

		String auth = req.getParameter("id") == null ? "" : req
				.getParameter("id");
		String name = req.getParameter("name") == null ? "" : req
				.getParameter("name");
		String description = req.getParameter("description") == null ? "" : req
				.getParameter("description");
		Map queryParams = null;
		if (!auth.equals("") || !name.equals("") || !description.equals("")) {
			queryParams = new HashMap();
			queryParams.put("auth", URLDecoder.decode(auth, "UTF-8"));

			queryParams.put("description", URLDecoder.decode(name, "UTF-8"));

			queryParams.put("longDescription", URLDecoder.decode(description, "UTF-8"));
		}

		String dir = (req.getParameter("dir") == null || req
				.getParameter("dir").equals("")) ? "DESC" : req
						.getParameter("dir");
		String sort = (req.getParameter("sort") == null || req.getParameter(
		"sort").equals("")) ? "auth" : req.getParameter("sort");
		String start_param = (req.getParameter("start") == null || req
				.getParameter("start").equals("")) ? "0" : req
						.getParameter("start");
		String limit_param = (req.getParameter("limit") == null || req
				.getParameter("limit").equals("")) ? "10" : req
						.getParameter("limit");

		Collection<Role> roles = null;
		int total_count = 0;
		if (queryParams == null) {
			roles = roleService.listRoles(dir, sort, limit_param, start_param);
			total_count = roleService.getRoleCount();

		} else {
			roles = roleService.listRolesByQuery(queryParams, dir, sort,limit_param, start_param);
			total_count = roleService.getRoleCount(queryParams);

		}
		
		HttpSession session = req.getSession();
		if(session!=null && roles!=null){
			session.setAttribute("roles", roles);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (Role role : roles) {
			JSONObject item = new JSONObject();
			item.put("auth", role.getId());
			item.put("description", role.getDescription());
			item.put("longDescription", role.getLongDescription());
			item.put("lastEditDate", role.getLastEditDate() == null ? ""
					: format.format(role.getLastEditDate()));
			item.put("lastEditUser", role.getLastEditUser() == null ? "Sistema"
					: role.getLastEditUser());
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

	protected void exportRoles(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		Collection<Role> roles = null;

		HttpSession session = req.getSession();
		if(session!=null){
			roles = (Collection<Role>)session.getAttribute("roles");
		}
		
		if (roles != null) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");

				ArrayList headers = new ArrayList();
				headers.add("Id");
				headers.add("Nombre");
				headers.add("Descripción");
				headers.add("Fecha Última Modificación");
				headers.add("Usuario Última Modificación");

				ArrayList values = new ArrayList();
				for (Role role : roles) {
					Map userMap = new TreeMap();
					userMap.put(1, role.getId());
					userMap.put(2, role.getDescription());
					userMap.put(3, role.getLongDescription());
					userMap.put(4, role.getLastEditDate() == null ? "" : format
							.format(role.getLastEditDate()));
					userMap.put(5, role.getLastEditUser() == null ? "Sistema"
							: role.getLastEditUser());
					values.add(userMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				"attachment; filename=\"roles.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Roles")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportRoles = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}
}

