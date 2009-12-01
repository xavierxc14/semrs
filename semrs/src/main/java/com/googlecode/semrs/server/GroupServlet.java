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
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.semrs.model.Group;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.GroupService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.ExcelExporter;

public class GroupServlet extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(GroupServlet.class);

	private UserService userService;
	
	private GroupService groupService;
	
	private GenericService genericService;
	
	
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.userService  = (UserService) factory.getBean("userService");
		this.groupService = (GroupService) factory.getBean("groupService");
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

			String groupEdit = req.getParameter("groupEdit");
			String export = req.getParameter("export");
			if (groupEdit != null) {
				if (groupEdit.equals("load")) {

				    getGroup(req, resp);

				} else if (groupEdit.equals("submit")) {

					saveGroup(req, resp);

				} else if (groupEdit.equals("delete")) {

					deleteGroup(req, resp);

				} else if (groupEdit.equals("getUsers")) {

					getUsers(req, resp);

				}

			} else if (export != null) {

				exportGroups(req, resp);

			} else {
				listGroups(req, resp);
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {

			LOG.error("Error in GroupServlet = " + ex);
		}
	}
	
	
	protected void saveGroup(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		boolean isNew = (req.getParameter("isNew") != null && (req
				.getParameter("isNew").equals("") || req.getParameter("isNew")
				.equals("true"))) ? true : false;
		String groupId = req.getParameter("id") == null ? "" : URLDecoder
				.decode(req.getParameter("id"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String description = req.getParameter("description") == null ? ""
				: URLDecoder.decode(req.getParameter("description"), "UTF-8");
		String[] users = req.getParameter("users") == null ? new String[0]
				: req.getParameter("users").split(",");

		Group group = groupService.getGroup(groupId,false);

		if (group != null && !isNew) {
			if (name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!name.equals("")) {
				
				Map queryParams = new HashMap();
				queryParams.put("name", URLDecoder.decode(name, "UTF-8"));
				queryParams.put("groupId","");

				if(groupService.getGroupCount(queryParams) == 0){
					group.setName(name);
				}else{
					errors.add(" el grupo ya existe en el sistema");
				}
				group.setName(name);
				group.setDescription(description);

				if (users != null) {
					if (users.length > 0) {
						User user = null;
						Collection<User> assignedUsers = new ArrayList<User>();
						for (String assignedUser : users) {
							if (!assignedUser.equals("")) {
								user = userService.getUserByUsername(URLDecoder
										.decode(assignedUser, "UTF-8"),false);
								if (user != null) {
									assignedUsers.add(user);
								}
							}
						}

						if (assignedUsers != null || assignedUsers.size() > 0) {
							group.setUsers(assignedUsers);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				group.setLastEditDate(new Date());
				group.setLastEditUser(userService.getCurrentUser().getUsername());
				groupService.save(group);
			}

		} else if (group != null && isNew) {
			errors.add("Grupo ya existe");
		} else if (group == null && isNew) {

			group = new Group();

			if (name.equals("")) {
				errors
						.add(" por favor complete los campos obligatorios para continuar");
			} else if (!name.equals("")) {
				Map queryParams = new HashMap();
				queryParams.put("name", URLDecoder.decode(name, "UTF-8"));
				queryParams.put("groupId","");

				if(groupService.getGroupCount(queryParams) == 0){
					group.setName(name);
				}else{
					errors.add(" el grupo ya existe en el sistema");
				}

				group.setGroupId(groupId);
				group.setName(name);
				group.setDescription(description);

				if (users != null) {

					if (!users.equals("") && users.length > 0) {
						User user = null;
						Collection<User> assignedUsers = new ArrayList<User>();
						for (String assignedUser : users) {
							if (!assignedUser.equals("")) {
								user = userService
										.getUserByUsername(assignedUser,false);
								if (user != null) {
									assignedUsers.add(user);
								}
							}
						}

						if (assignedUsers != null && assignedUsers.size() > 0) {
							group.setUsers(assignedUsers);
						}
					}
				}
			}
			if (errors == null || errors.size() <= 0) {
				group.setLastEditDate(new Date());
				group.setLastEditUser(userService.getCurrentUser().getUsername());
				groupService.save(group);
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
	
	
	protected void getGroup(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		Group group = null;
		String groupId = req.getParameter("id");
			group = groupService.getGroup(groupId,false);
		List dataItems = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			JSONObject data = new JSONObject();
			if (group != null) {
				data.put("id", group.getId());
				data.put("name", group.getName());
				data.put("description", group.getDescription());
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
	
	
	protected void deleteGroup(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		String groupId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");

		try {
			Group group = groupService.getGroup(groupId,false);
			if (group != null) {
				   groupService.deleteGroup(group);

			} else if (group == null) {
				errors.add("Grupo no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar este grupo: ");
				for (String error : errors) {
					out.println(error);
				}

			} else {
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deleteGroup = " + e);

		}

	}
	
	
	protected void getUsers(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		String groupId = req.getParameter("id") == null ? ""
				: URLDecoder.decode(req.getParameter("id"), "UTF-8");
		final Group group = groupService.getGroup(groupId,false);
	
		String groupUsersParam = req.getParameter("groupUsers");
		List items = null;
		if(group!=null){
		 genericService.fill(group, "users");
		}
		final Collection<User> availableUsers = groupService.getAvailableUsers(group);
		if (group != null) {
			
			final Collection<User> groupUsers = group.getUsers();
			
		
			if (groupUsersParam != null) {

				items = new ArrayList();

				for (User user : groupUsers) {
					JSONObject item = new JSONObject();
					item.put("id", user.getUsername());
					item.put("desc", user.getName() + " " + user.getLastName());
					items.add(item);
				}

			} else {
				

				items = new ArrayList();
				for (User user : availableUsers) {
					JSONObject item = new JSONObject();
					item.put("id", user.getUsername());
					item.put("desc", user.getName() + " " + user.getLastName());
					items.add(item);
			}
			
		}
	

		} else {
			items = new ArrayList();
			
			for (User user : availableUsers) {
				JSONObject item = new JSONObject();
				item.put("id", user.getUsername());
				item.put("desc", user.getName() + " " + user.getLastName());
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
	

	protected void listGroups(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException {

		String username = req.getParameter("id") == null ? "" : req
				.getParameter("id");
		String name = req.getParameter("name") == null ? "" : req
				.getParameter("name");
		String description = req.getParameter("description") == null ? "" : req
				.getParameter("description");
		Map queryParams = null;
		if (!username.equals("") || !name.equals("") || !description.equals("")) {
			queryParams = new HashMap();
			queryParams.put("id", URLDecoder.decode(username, "UTF-8"));

			queryParams.put("name", URLDecoder.decode(name, "UTF-8"));

			queryParams.put("description", URLDecoder.decode(description, "UTF-8"));
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

		Collection<Group> groups = null;
		int total_count = 0;
		if (queryParams == null) {
			groups = groupService.listGroups(dir, sort, limit_param, start_param);
			total_count = groupService.getGroupCount();

		} else {
			groups = groupService.listGroupsByQuery(queryParams, dir, sort,limit_param, start_param);
			total_count = groupService.getGroupCount(queryParams);

		}
		
		HttpSession session = req.getSession();
		if(session!=null && groups!=null){
			session.setAttribute("groups", groups);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (Group group : groups) {
			JSONObject item = new JSONObject();
			item.put("id", group.getId());
			item.put("name", group.getName());
			item.put("description", group.getDescription());
			item.put("lastEditDate", group.getLastEditDate() == null ? ""
					: format.format(group.getLastEditDate()));
			item.put("lastEditUser", group.getLastEditUser() == null ? "Sistema"
					: group.getLastEditUser());
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

	protected void exportGroups(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		Collection<Group> groups = null;
		HttpSession session = req.getSession();
		if(session!=null){
			groups = (Collection<Group>)session.getAttribute("groups");
		}

		if (groups != null) {
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
				for (Group group : groups) {
					Map userMap = new TreeMap();
					userMap.put(1, group.getId());
					userMap.put(2, group.getName());
					userMap.put(3, group.getDescription().replaceAll("\\<.*?\\>", ""));
					userMap.put(4, group.getLastEditDate() == null ? "" : format
							.format(group.getLastEditDate()));
					userMap.put(5, group.getLastEditUser() == null ? "Sistema"
							: group.getLastEditUser());
					values.add(userMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
				"attachment; filename=\"grupos.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Grupos")
				.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportGroups = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}

}
