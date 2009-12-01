package com.googlecode.semrs.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.semrs.model.Module;
import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.util.Util;

public class StartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(StartupServlet.class);

	private static final String CLASS_NAME = "StartupServlet";


	public void init(ServletConfig config) throws ServletException {

		WebApplicationContext wac = WebApplicationContextUtils
		.getRequiredWebApplicationContext(config.getServletContext());
		GenericService genericService = (GenericService) wac.getBean("genericService");
		LOG.info("Starting init() in " + CLASS_NAME);
		
		Collection<Role> roles = new ArrayList<Role>();
		Collection<Role> roles2 = new ArrayList<Role>();
		Collection<Role> roles3 = new ArrayList<Role>();
		Collection<Module> adminModules = new ArrayList<Module>();
		Collection<Module> nurseModules = new ArrayList<Module>();
		Collection<Module> doctModules = new ArrayList<Module>();


		if (!genericService.exists(Module.class, "admin-category")) {
			LOG.info("Creating screens initial data...");
			try{
				Module screen = new Module();
				screen.setId("admin-category");
				screen.setCategory(null);
				screen.setTitle("Administraci&oacute;n");
				screen.setIconPath("misc-category-icon");
				screen.setOrder(0);
				genericService.save(screen);
				adminModules.add(screen);
			}catch(Exception e){
				LOG.error(e);
			}
		}



		if (!genericService.exists(Module.class, "patient-category")) {
			LOG.info("Creating screens initial data...");
			try{
				Module screen = new Module();
				screen.setId("patient-category");
				screen.setCategory(null);
				screen.setTitle("Registro y Control de Pacientes");
				screen.setIconPath("combination-category-icon");
				screen.setOrder(1);
				genericService.save(screen);
				adminModules.add(screen);
				nurseModules.add(screen);
				doctModules.add(screen);
			}catch(Exception e){
				LOG.error(e);
			}
		}



		if (!genericService.exists(Module.class, "dict-category")) {
			LOG.info("Creating screens initial data...");
			try{
				Module screen = new Module();
				screen.setId("dict-category");
				screen.setCategory(null);
				screen.setTitle("Diccionario de Conceptos");
				screen.setIconPath("tree-category-icon");
				screen.setOrder(2);
				genericService.save(screen);
				adminModules.add(screen);
				doctModules.add(screen);
			}catch(Exception e){
				LOG.error(e);
			}
		}


		if (!genericService.exists(Module.class, "med-category")) {
			LOG.info("Creating screens initial data...");
			try{
				Module screen = new Module();
				screen.setId("med-category");
				screen.setCategory(null);
				screen.setTitle("Ex&aacute;menes y Medicamentos");
				screen.setIconPath("med-icon");
				screen.setOrder(3);
				genericService.save(screen);
				adminModules.add(screen);
				doctModules.add(screen);
			}catch(Exception e){
				LOG.error(e);
			}
		}



		if (!genericService.exists(Role.class, "ROLE_ADMIN")) {
			Role admin = new Role();
			try{
				admin.setName("ROLE_ADMIN");
				admin.setDescription("Rol Administrador");
				admin.setModules(adminModules);
				admin.setLastEditDate(new Date());
				genericService.save(admin);
				roles.add(admin);
			}catch(Exception e){
				LOG.error(e);
			}
		}

		if (!genericService.exists(Role.class, "ROLE_NURSE")) {
			Role nurse = new Role();
			try{
				nurse.setName("ROLE_NURSE");
				nurse.setDescription("Rol Enfermera");
				nurse.setModules(nurseModules);
				nurse.setLastEditDate(new Date());
				genericService.save(nurse);
				roles2.add(nurse);
			}catch(Exception e){
				LOG.error(e);
			}
		}

		if (!genericService.exists(Role.class, "ROLE_DOCT")) {
			Role doctor = new Role();
			try{
				doctor.setName("ROLE_DOCT");
				doctor.setDescription("Rol Médico");
				doctor.setModules(doctModules);
				doctor.setLastEditDate(new Date());
				genericService.save(doctor);
				roles3.add(doctor);
			}catch(Exception e){
				LOG.error(e);
			}

		}



		if (!genericService.exists(User.class, "admin")) {
			LOG.info("Creating admin user and initial data...");
			try{
				User admin = new User();
				admin.setId("admin");
				admin.setEmail("rsmaniak@gmail.com");
				admin.setPassword(Util.hashString("admin"));
				admin.setRoles(roles);
				admin.setAccountNonExpired(true);
				admin.setAccountNonLocked(true);
				admin.setCredentialsNonExpired(true);
				admin.setName("Admin");
				admin.setLastName("admin");
				admin.setEnabled(true);
				admin.setLastEditDate(new Date());
				admin.setLastEditUser("Sistema");
				genericService.save(admin);
			}catch(Exception e){
				LOG.error(e);
			}
		}

		if (!genericService.exists(User.class, "enfermera1")) {
			LOG.info("Creating nurse user and initial data...");
			try{
				User admin = new User();
				admin.setId("enfermera1");
				admin.setEmail("prueba@prueba.com");
				admin.setPassword(Util.hashString("enfermera1"));
				admin.setRoles(roles2);
				admin.setAccountNonExpired(true);
				admin.setAccountNonLocked(true);
				admin.setCredentialsNonExpired(true);
				admin.setName("Enfermera");
				admin.setLastName("Enfermera");
				admin.setEnabled(false);
				admin.setLastEditDate(new Date());
				admin.setLastEditUser("Sistema");
				genericService.save(admin);
			}catch(Exception e){
				LOG.error(e);
			}
		}


		if (!genericService.exists(User.class, "anigarc")) {
			LOG.info("Creating doctor user and initial data...");
			try{
				User admin = new User();
				admin.setId("anigarc");
				admin.setEmail("prueba@prueba.com");
				admin.setPassword(Util.hashString("anigarc"));
				admin.setRoles(roles3);
				admin.setAccountNonExpired(true);
				admin.setAccountNonLocked(true);
				admin.setCredentialsNonExpired(true);
				admin.setName("Aniuskar");
				admin.setLastName("Garcia");
				admin.setEnabled(true);
				admin.setLastEditDate(new Date());
				admin.setLastEditUser("Sistema");
				genericService.save(admin);
			}catch(Exception e){
				LOG.error(e);
			}
		}


	}


	public void service(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

	}

	public void destroy() {
		LOG.info("Starting destroy() in " + CLASS_NAME);
	}


	public java.lang.String getServletInfo() {
		return "";
	}

}
