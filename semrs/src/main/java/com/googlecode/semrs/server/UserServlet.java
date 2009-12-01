package com.googlecode.semrs.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationTrustResolver;
import org.springframework.security.AuthenticationTrustResolverImpl;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.semrs.model.Encounter;
import com.googlecode.semrs.model.Patient;
import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.EncounterService;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.PatientService;
import com.googlecode.semrs.server.service.UserService;
import com.googlecode.semrs.util.ExcelExporter;
import com.googlecode.semrs.util.ModelContainer;
import com.googlecode.semrs.util.Util;


public class UserServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(UserServlet.class);

	private UserService service;
	
	private EncounterService encounterService;
	
	private PatientService patientService;
	
	private GenericService genericService;
	


	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanFactory factory = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.service = (UserService) factory.getBean("userService");
		this.encounterService = (EncounterService) factory.getBean("encounterService");
		this.patientService = (PatientService) factory.getBean("patientService");
		this.genericService = (GenericService) factory.getBean("genericService");
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		handleRequest(req, resp);
	}

	private void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {

			String userEdit = req.getParameter("userEdit");
			String export = req.getParameter("export");
			String sessionTimeout = req.getParameter("getSessionTimeOut");
			String dumpModel = req.getParameter("dumpModel");
			if (userEdit != null) {
				if (userEdit.equals("load")) {

					getUser(req, resp);

				} else if (userEdit.equals("submit")) {

					saveUser(req, resp);

				} else if (userEdit.equals("delete")) {

					deleteUser(req, resp);

				} else if (userEdit.equals("getRoles")) {

					getRoles(req, resp);

				} else if (userEdit.equals("listEncountersForUser")) {

					listEncountersForUser(req, resp);

				} else if (userEdit.equals("exportEncounters")) {

					exportEncounters(req, resp);

				}


			} else if (export != null) {

				exportUsers(req, resp);

			} else if(sessionTimeout != null){
				
				getSessionTimeout(req,resp);
			
			}else if(dumpModel != null){
					
				dumpModel(req,resp);
				
				
			}else {
				listUsers(req, resp);
			}
		} catch (org.json.JSONException e) {
			throw new IOException();
		} catch (Exception ex) {
    
			LOG.error("Error in UserServlet = " + ex);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		handleRequest(req, resp);
	}

	protected void getUser(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		User user = null;
		String userName = req.getParameter("id");
		if (userName.equals("currentUser")) {
			user = service.getCurrentUser();

		} else {
			user = service.getUserByUsername(userName,false);
		}
		List dataItems = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			JSONObject data = new JSONObject();
			if (user != null) {
				data.put("username", user.getId());
				data.put("name", user.getName());
				data.put("lastName", user.getLastName());
				data.put("email", user.getEmail());
				data.put("birthDate", user.getBirthDate() == null ? "" : format
						.format(user.getBirthDate()));
				data.put("sex", user.getSex() == null ? "" : user.getSex());
				data.put("phoneNumber", user.getPhoneNumber() == null ? ""
						: user.getPhoneNumber());
				data.put("mobile", user.getMobile() == null ? "" : user
						.getMobile());
				data.put("address", user.getAddress() == null ? "" : user
						.getAddress());
				data.put("password", "");
				data.put("passwordRetype", "");
				data.put("enabled", user.isEnabled() == true ? "Si" : "No");
				data.put("loadSuccess", "true");
			}else{
				data.put("username", "");
				data.put("name", "");
				data.put("lastName","");
				data.put("email", "");
				data.put("birthDate", "");
				data.put("sex", "");
				data.put("phoneNumber", "");
				data.put("mobile", "");
				data.put("address", "");
				data.put("password", "");
				data.put("passwordRetype", "");
				data.put("enabled", "");
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

	protected void saveUser(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, Exception {

		ArrayList<String> errors = new ArrayList<String>();

		boolean isNew = (req.getParameter("isNew") != null &&( req.getParameter("isNew").equals("") || req.getParameter("isNew").equals("true"))) ? true : false;
		String username = req.getParameter("username") == null ? ""
				: URLDecoder.decode(req.getParameter("username"), "UTF-8");
		String name = req.getParameter("name") == null ? "" : URLDecoder
				.decode(req.getParameter("name"), "UTF-8");
		String lastName = req.getParameter("lastName") == null ? ""
				: URLDecoder.decode(req.getParameter("lastName"), "UTF-8");
		String email = req.getParameter("email") == null ? "" : URLDecoder
				.decode(req.getParameter("email"), "UTF-8");
		String birthDate = req.getParameter("birthDate") == null ? ""
				: URLDecoder.decode(req.getParameter("birthDate"), "UTF-8");
		String sex = (req.getParameter("sex") == null || req.getParameter("sex").equals("null"))  ? "" : URLDecoder.decode(
				req.getParameter("sex"), "UTF-8");
		String phoneNumber = req.getParameter("phoneNumber") == null ? ""
				: URLDecoder.decode(req.getParameter("phoneNumber"), "UTF-8");
		String mobile = req.getParameter("mobile") == null ? "" : URLDecoder
				.decode(req.getParameter("mobile"), "UTF-8");
		String address = req.getParameter("address") == null ? "" : URLDecoder
				.decode(req.getParameter("address"), "UTF-8");
		String password = req.getParameter("password") == null ? ""
				: URLDecoder.decode(req.getParameter("password"), "UTF-8");
		String passwordRetype = req.getParameter("passwordRetype") == null ? ""
				: URLDecoder
						.decode(req.getParameter("passwordRetype"), "UTF-8");
		String enabled = req.getParameter("enabled") == null ? "" : URLDecoder
				.decode(req.getParameter("enabled"), "UTF-8");
		String[] roles = req.getParameter("roles") == null? new String[0] : req.getParameter("roles").split(",");

			User user = service.getUserByUsername(username,false);

			if (user != null && !isNew) {
				if (name.equals("") || lastName.equals("") || email.equals("")) {
					errors
							.add(" por favor complete los campos obligatorios para continuar");
				} else if (!name.equals("") && !lastName.equals("")
						&& !email.equals("")) {
					user.setName(name);
					user.setLastName(lastName);
					Pattern emailPatten = Pattern.compile("^\\S+@\\S+$");
					if(!user.getEmail().equals(email)){
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
							queryParams.put("username","");
							queryParams.put("name","");
							queryParams.put("lastName","");
							if(service.getUsersCount(queryParams) == 0){
								user.setEmail(email);
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
						user.setBirthDate(cal.getTime());
					}
					user.setSex(sex);
					user.setPhoneNumber(phoneNumber);
					user.setMobile(mobile);
					user.setAddress(address);

					if (enabled.equals("") || enabled.equals("Si")) {
						user.setEnabled(true);
						user.setAccountNonExpired(true);
						user.setAccountNonLocked(true);
						user.setCredentialsNonExpired(true);
					} else {
						user.setEnabled(false);
					}

				

					if (!password.equals("") && !passwordRetype.equals("")) {
						if (password.equals(passwordRetype)) {
							if(password.length()<6){
								errors.add("el password debe contener almenos 6 caracteres");
							}else{
							   user.setPassword(Util.hashString(password));
							}
						} else {
							errors
									.add("el Password de confirmaci&oacute;n debe ser igual al Password Original");
						}
					} else if ((password.equals("") && !passwordRetype
							.equals(""))
							|| (!password.equals("") && passwordRetype
									.equals(""))) {
						errors
								.add("el Password de confirmaci&oacute;n debe ser igual al Password Original");
					}
					if (roles != null) {
						if (roles.length > 0) {
							Role role = null;
							Collection<Role> assignedRoles = new ArrayList<Role>();
							for (String assignedRole : roles) {
								if(!assignedRole.equals("")){
								role = service.getRoleById(URLDecoder.decode(
										assignedRole, "UTF-8"));
								if (role != null) {
									assignedRoles.add(role);
								}
								}
							}

							if (assignedRoles != null || assignedRoles.size() > 0) {
								user.setRoles(assignedRoles);
							}
						}
					}
				}
				if (errors == null || errors.size() <= 0) {
					user.setLastEditDate(new Date());
					user.setLastEditUser(service.getCurrentUser().getUsername());
					service.save(user);
				}

			} else if (user != null && isNew) {
				errors.add("Usuario ya existe");
			} else if (user == null && isNew) {

				user = new User();

				if (username.equals("") || name.equals("")
						|| lastName.equals("") || email.equals("")) {
					errors
							.add(" por favor complete los campos obligatorios para continuar");
				} else if (!username.equals("") && !name.equals("")
						&& !lastName.equals("") && !email.equals("")) {

					user.setUsername(username);
					user.setName(name);
					user.setLastName(lastName);

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
						queryParams.put("username","");
						queryParams.put("name","");
						queryParams.put("lastName","");
						if(service.getUsersCount(queryParams) == 0){
						user.setEmail(email);
						}else{
							errors.add(" el Email suministrado ya existe en el sistema");
						}
					}
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",
							Locale.getDefault());
					if (!birthDate.equals("")) {
						Date d = sdf.parse(birthDate);
						Calendar cal = Calendar.getInstance();
						cal.setTime(d);
						user.setBirthDate(cal.getTime());
					}
					user.setSex(sex);
					user.setPhoneNumber(phoneNumber);
					user.setMobile(mobile);
					user.setAddress(address);

					if (enabled.equals("") || enabled.equals("Si") ) {
							user.setEnabled(true);
							user.setAccountNonExpired(true);
							user.setAccountNonLocked(true);
							user.setCredentialsNonExpired(true);
						} else {
							user.setEnabled(false);
					}
					if (password.equals("")) {
						errors.add("el campo Password es obligatorio");
					} else {
						if (!password.equals("") && !passwordRetype.equals("")) {
							if (password.equals(passwordRetype)) {
								if(password.length()<6){
									errors.add("el password debe contener almenos 6 caracteres");
								}else{
								   user.setPassword(Util.hashString(password));
								}
							} else {
								errors
										.add("el Password de confirmaci&oacute;n debe ser igual al Password Original");
							}
						} else if ((password.equals("") && !passwordRetype
								.equals(""))
								|| (!password.equals("") && passwordRetype
										.equals(""))) {
							errors
									.add("el Password de confirmaci&oacute;n debe ser igual al Password Original");
						}

					}
					if (roles != null) {

						if (!roles.equals("") && roles.length > 0) {
							Role role = null;
							Collection<Role> assignedRoles = new ArrayList<Role>();
							for (String assignedRole : roles) {
								if (!assignedRole.equals("")) {
									role = service.getRoleById(assignedRole);
									if (role != null) {
										assignedRoles.add(role);
									}
								}
							}

							if (assignedRoles != null
									&& assignedRoles.size() > 0) {
								user.setRoles(assignedRoles);
							}
						}
					}
				}
				if (errors == null || errors.size() <= 0) {
					user.setLastEditDate(new Date());
					user.setLastEditUser(service.getCurrentUser().getUsername());
					service.save(user);
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

	protected void listUsers(HttpServletRequest req, HttpServletResponse resp)
			throws JSONException, IOException {

		String username = req.getParameter("username") == null ? "" : req
				.getParameter("username");
		String name = req.getParameter("name") == null ? "" : req
				.getParameter("name");
		String lastName = req.getParameter("lastName") == null ? "" : req
				.getParameter("lastName");
		String email = req.getParameter("email") == null ? "" : req
				.getParameter("email");
		Map queryParams = null;
		if (!username.equals("") || !name.equals("") || !lastName.equals("")
				|| !email.equals("")) {
			queryParams = new HashMap();
			queryParams.put("username", URLDecoder.decode(username, "UTF-8"));

			queryParams.put("name", URLDecoder.decode(name, "UTF-8"));

			queryParams.put("lastName", URLDecoder.decode(lastName, "UTF-8"));

			queryParams.put("email", URLDecoder.decode(email, "UTF-8"));
		}

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
		Collection<User> users = null;
		int total_count = 0;
		if (queryParams == null) {
			users = service.getAllUsers(dir, sort, limit_param, start_param);
			total_count = service.getUsersCount();

		} else {
			users = service.getUsersByQuery(queryParams, dir, sort,
					limit_param, start_param);
			total_count = service.getUsersCount(queryParams);

		}
		HttpSession session = req.getSession();
		if(users!=null && session!=null){
			session.setAttribute("users", users);
		}

		// Object[][] data=LiveGridDataProxy.data;
		// if (sorted_data.containsKey(dir+sort))
		// data = (Object[][]) sorted_data.get(dir+sort);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List items = new ArrayList();
		for (User user : users) {
			JSONObject item = new JSONObject();
			item.put("username", user.getId());
			item.put("name", user.getName());
			item.put("lastName", user.getLastName());
			item.put("email", user.getEmail());
			item.put("phoneNumber", user.getPhoneNumber() == null ? "" : user
					.getPhoneNumber());
			item
					.put("mobile", user.getMobile() == null ? "" : user
							.getMobile());
			item.put("lastEditDate", user.getLastEditDate() == null ? ""
					: format.format(user.getLastEditDate()));
			item.put("lastEditUser", user.getLastEditUser() == null ? "Sistema"
					: user.getLastEditUser());
			item.put("lastLogin", user.getLastLogin() == null ? "" : format
					.format(user.getLastLogin()));
			item.put("enabled", user.isEnabled() == true ? "Si" : "No");
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

	protected void exportUsers(HttpServletRequest req, HttpServletResponse resp)
			throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		Collection<User> users = null;
		HttpSession session = req.getSession();
		if(session!=null){
		  users = (Collection<User>)session.getAttribute("users");
		}
		if (users != null) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(
						"dd/MM/yyyy HH:mm:ss");

				ArrayList headers = new ArrayList();
				headers.add("Nombre de Usuario");
				headers.add("Nombre");
				headers.add("Apellido");
				headers.add("Fecha de Nacimiento");
				headers.add("Email");
				headers.add("Telefóno");
				headers.add("Móvil");
				headers.add("Dirección");
				headers.add("Fecha Última Modificación");
				headers.add("Usuario Última Modificación");
				headers.add("Último Login");
				headers.add("Activo?");

				ArrayList values = new ArrayList();
				for (User user : users) {
					Map userMap = new TreeMap();
					userMap.put(1, user.getId());
					userMap.put(2, user.getName());
					userMap.put(3, user.getLastName());
					userMap.put(4, user.getBirthDate() == null ? "" : format
							.format(user.getBirthDate()));
					userMap.put(5, user.getEmail());
					userMap.put(6, user.getPhoneNumber() == null ? "" : user
							.getPhoneNumber());
					userMap.put(7, user.getMobile() == null ? "" : user
							.getMobile());
					userMap.put(8, user.getAddress() == null ? "" : user
							.getAddress());
					userMap.put(9, user.getLastEditDate() == null ? "" : format
							.format(user.getLastEditDate()));
					userMap.put(10, user.getLastEditUser() == null ? "Sistema"
							: user.getLastEditUser());
					userMap.put(11, user.getLastLogin() == null ? "" : format
							.format(user.getLastLogin()));
					userMap.put(12, user.isEnabled() == true ? "Si" : "No");
					values.add(userMap);
				}

				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition",
						"attachment; filename=\"usuarios.xls\"");
				out = resp.getOutputStream();
				ExcelExporter.export(values, headers, "Lista de Usuarios")
						.write(out);
			} catch (Exception e) {
				LOG.error("Error in exportUsers = " + e);
			} finally {
				if (out != null)
					out.close();
			}
		}

	}

	protected void getRoles(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		final String username = req.getParameter("username") == null ? ""
				: URLDecoder.decode(req.getParameter("username"), "UTF-8");
		final User user = service.getUserByUsername(username,false);
		if(user!=null){
		 genericService.fill(user, "roles");
		}
		final Collection<Role> availableRoles = service.getAvailableRoles(user);
		final String userRolesParam = req.getParameter("userRoles");
		List items = null;
		
		if (user != null) {
			
			final Collection<Role> userRoles  = user.getRoles();

			if (userRolesParam != null) {

				items = new ArrayList();

				for (Role role : userRoles) {
					JSONObject item = new JSONObject();
					item.put("id", role.getAuth());
					item.put("desc", role.getDescription());
					items.add(item);
				}

			} else {
	
				items = new ArrayList();
				//availableRoles = service.getRoles();
				for (Role role : availableRoles) {
					JSONObject item = new JSONObject();
					item.put("id", role.getAuth());
					item.put("desc", role.getDescription());
					items.add(item);
			}
			
		}
	

		} else {
			items = new ArrayList();
			//availableRoles = service.getRoles();
			for (Role role : availableRoles) {
				JSONObject item = new JSONObject();
				item.put("id", role.getAuth());
				item.put("desc", role.getDescription());
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
	
	
	
	protected void listEncountersForUser(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {



		Map queryParams = new HashMap();
		
		User currentUser = service.getCurrentUser();
		genericService.fill(currentUser, "roles");
		boolean isProvider = false;
		boolean isAdmin = false;
		Collection<Role> roles = currentUser.getRoles();
		for(Role role : roles){
			if(role.getAuth().equals("ROLE_DOCT")){
				isProvider = true;
			}
			if(role.getAuth().equals("ROLE_ADMIN")){
				isAdmin = true;
			}
		}
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat birthFormat = new SimpleDateFormat("dd/MM/yyyy");
		String today = req.getParameter("today");

		if(isProvider){
			queryParams.put("providerEncounter", currentUser.getUsername());
		}
		queryParams.put("current", "true");

		if(today==null){

			queryParams.put("daySearch", "true");

			queryParams.put("encounterDate", birthFormat.format(new Date()));

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
		
		int total_count = 0;
		
		Collection<Encounter> currentEncounters = null;

		currentEncounters = encounterService.listEncountersByQuery(queryParams, dir, sort,
				limit_param, start_param);

		total_count = encounterService.getEncounterCount(queryParams);
 
		HttpSession session = req.getSession();
		if(session!=null && currentEncounters!=null){
			session.setAttribute("currentEncounters", currentEncounters);
		}
				


		List items = new ArrayList();
		for (Encounter encounter : currentEncounters) {
			JSONObject item = new JSONObject();
			Patient patient = patientService.getPatient(encounter.getPatientId(),false);
			if(patient!=null){
			item.put("id", encounter.getId());
			item.put("patientId", encounter.getPatientId());
			item.put("patientName", patient.getName() + " " + patient.getLastName());
			User provider = null;
			if(encounter.getEncounterProvider()!=null){
			  provider = service.getUserByUsername(encounter.getEncounterProvider(),false);
			}
			User patientProvider = patient.getProvider();
			String patientProviderId = "";
			if(patientProvider!=null){
				patientProviderId = patientProvider.getUsername();
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
	
	
	

	protected void deleteUser(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		ArrayList<String> errors = new ArrayList<String>();
		String username = req.getParameter("username") == null ? ""
				: URLDecoder.decode(req.getParameter("username"), "UTF-8");
		User currentUser = service.getCurrentUser();

		try {
			User user = service.getUserByUsername(username,false);
			if (user != null) {

				if (user.getId().equals("admin")) {
					errors
							.add("El usuario administrador no puede ser eliminado");
				}else if(user.getId().equals(currentUser.getId())){
					errors.add("Por favor cierre la sesi&oacute;n actual antes de eliminar esta cuenta de usuario");
				}else {
					service.deleteUser(user);

					URL resource = this.getClass().getResource("/user/images/");
					synchronized(resource){
						if(resource != null){
							File imageFolder = new File(resource.getFile());
							if(imageFolder!=null){
								File[] images = imageFolder.listFiles();
								if (images != null) {
									for (File file : images) {
										String fileName = file.getName().substring(0,
												file.getName().lastIndexOf("."));
										if (fileName.equals(username)) {
											LOG.info("Deleting file = " + file.getName()
													+ " for user = " + username);
											file.delete();
										}
									}
								}
							}
						}
					}
				}

			} else if (user == null) {
				errors.add("Usuario no existe");
			}

			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();

			if (errors != null && errors.size() > 0) {
				out
						.println("Ocurrier&oacute;n errores al eliminar este usuario: ");
				for (String error : errors) {
					out.println(error);
				}

			} else {
				out.println("Registro eliminado ex&iacute;tosamente");
			}
		} catch (Exception e) {
			LOG.error("Error in deleteUser = " + e);

		}

	}
	
	
	protected void getSessionTimeout(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		try {
			boolean isRememberMe = false;
			Cookie[] cookies = req.getCookies();
			
		  for(Cookie cookie : cookies){
			  if(cookie.getName().equals("SPRING_SECURITY_REMEMBER_ME_COOKIE")){
				  isRememberMe = true;
			  }
		  }
			

			int maxInactive = req.getSession().getMaxInactiveInterval();
			int sessionTimeOut = (maxInactive/60)<=5?(maxInactive/60)/2:(maxInactive/60)-5;


	
			List dataItems = new ArrayList();
			JSONObject data = new JSONObject();
			data.put("rememberMe", isRememberMe);
			data.put("sessionTimeOut", sessionTimeOut);
			dataItems.add(data);
			JSONObject feeds = new JSONObject();
			feeds.put("data", new JSONArray(dataItems));
			feeds.put("success", true);
			resp.setContentType("application/json; charset=utf-8");
			Writer w = new OutputStreamWriter(resp.getOutputStream(),"utf-8");
			w.write(feeds.toString());
			w.close();
			resp.setStatus(HttpServletResponse.SC_OK);

		} catch (Exception e) {
			LOG.error("Error in getSessionTimeout = " + e);

		}

	}
	
	
	protected void exportEncounters(HttpServletRequest req, HttpServletResponse resp)
	throws JSONException, IOException, ServletException {
		OutputStream out = null;
		
		String current = req.getParameter("current") == null ? ""
				: URLDecoder.decode(req.getParameter("current"), "UTF-8");

		String searchList = req.getParameter("searchList");
		
		HttpSession session = req.getSession();
		Collection<Encounter> currentEncounters = null;
		if(session!=null ){
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
							  provider = service.getUserByUsername(encounter.getEncounterProvider(),false);
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
		
	}
	
	
	
	protected void dumpModel(HttpServletRequest req,
			HttpServletResponse resp) throws JSONException, IOException,
			ServletException {

		try {

			resp.setContentType("text/xml; charset=utf-8");
			PrintWriter out = resp.getWriter();
			ModelContainer mc = genericService.getModelContainer();
			mc.dumpModel(out,"RDF/XML-ABBREV");

		} catch (Exception e) {
			LOG.error("Error in dumpModel = " + e);

		}

	}

}
