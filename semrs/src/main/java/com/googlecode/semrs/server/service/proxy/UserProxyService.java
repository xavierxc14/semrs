package com.googlecode.semrs.server.service.proxy;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import com.googlecode.semrs.client.screens.admin.AdminUsersScreen;
import com.googlecode.semrs.model.Module;
import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.server.service.UserService;
import com.totsp.gwt.beans.server.BeanMapping;



public class UserProxyService implements UserDetailsService {
	
    private static final Logger LOG = Logger
    .getLogger(UserProxyService.class);
	
	 private UserService userService;
	 private GenericService genericService; 
	
	 private Properties mappingProperties = new Properties();
	 private Properties clientMappingProperties = new Properties();
	 
	 static final Comparator<Module> menuOrder = new Comparator<Module>() {
		 public int compare(Module module1, Module module2) {
			 return module1.getOrder().compareTo(module2.getOrder());
		 }
	 };
	
    public UserProxyService() {
        super();
        mappingProperties.setProperty(
                "com.googlecode.semrs.model.*",
                "com.googlecode.semrs.model.proxy.*");
        clientMappingProperties.setProperty(
        		"com.googlecode.semrs.model.*",
                "com.googlecode.semrs.client.model.*");
    }

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		UserDetails user = null;
		try {
			User serverUser = userService.getUserByUsername(userName,false);
			genericService.fill(serverUser, "roles");
			user = (com.googlecode.semrs.model.proxy.User)BeanMapping.convert(mappingProperties,serverUser);
		    
		} catch (Exception e) {
			LOG.error(e);
		}
		return user;
	}
	

	public com.googlecode.semrs.client.model.User getCurrentUser() {
		com.googlecode.semrs.client.model.User user = null; 
		try{

		User currentUser =  userService.getCurrentUser();
		genericService.fill(currentUser, "roles");
        
		user = (com.googlecode.semrs.client.model.User) 
		                   BeanMapping.convert(clientMappingProperties,currentUser);
        }catch(Exception e){
        	LOG.info("Error getting user: "+ user +" caused by: " + e);
        }
		return user;

	}
	

	public void setGenericService(GenericService genericService) {
		this.genericService = genericService;
	}
	
	
	public String[][] getUserMenu(){

		final String[][] menuScreens = new String[19][];
		final List<Module> allowedModules = new ArrayList<Module>();
		User user = userService.getCurrentUser();
		genericService.fill(user, "roles");
		final Collection<Role> userRoles = user.getRoles();		
		for(Role role: userRoles){
			genericService.fill(role, "modules");
			for(Module module: role.getModules()){
				if(!allowedModules.contains(module)){
					allowedModules.add(module);	
				}
			}
		}


		Collections.sort(allowedModules, menuOrder);

		synchronized(menuScreens){
			int counter = 0;
			for(Module allowedModule :  allowedModules){

				if(allowedModule.getId().equals("admin-category")){
					menuScreens[counter] = new String[]{"admin-category", null, allowedModule.getTitle(), "misc-category-icon", null, null, null};
					menuScreens[counter+1] = new String[]{"admUser", "admin-category", "Administrar Usuarios", "user-icon", null, null, "AdminUsersScreen"};
					menuScreens[counter+2] = new String[]{"addEditUser", "admin-category", "Agregar/Editar Usuario", "user-add-icon", null, null, "AddEditUserScreen"};
					menuScreens[counter+3] = new String[]{"admGroups", "admin-category", "Administrar Grupos", "groups-icon", null, null, "AdminGroupsScreen"};
					menuScreens[counter+4] = new String[]{"admRole", "admin-category", "Administrar Roles", "roles-icon", null, null, "AdminRolesScreen"};
					//menuScreens[counter+3] = new String[]{"admPerm", "admin-category", "Administrar Permisos", null, null, null, "AdminPermScreen"};
					counter = counter+5;
				}

				if(allowedModule.getId().equals("patient-category")){
					menuScreens[counter] = new String[]{"patient-category", null, "Registro y Control de Pacientes", "combination-category-icon", null, null, null};
					menuScreens[counter+1] = new String[]{"admPatient", "patient-category", "Administrar Pacientes", "patients-icon", null, null, "ListPatientsScreen"};
					menuScreens[counter+2] = new String[]{"addPatient", "patient-category", "Nuevo Paciente", "patient-icon", null, null, "AddPatientScreen"};
					menuScreens[counter+3] = new String[]{"listEncounters", "patient-category", "Administrar Consultas", "encounter-icon", null, null, "ListEncountersScreen"};
					counter = counter+4;

				}

				if(allowedModule.getId().equals("dict-category")){
					menuScreens[counter] = new String[]{"dict-category", null, "Diccionario de Conceptos", "tree-category-icon", null, null, null};
					menuScreens[counter+1] = new String[]{"admSynt", "dict-category", "S&iacute;ntomas", "symptoms-icon", null, null, "AdminSymptomsScreen"};
					menuScreens[counter+2] = new String[]{"admEnf", "dict-category", "Enfermedades", "disease-icon", null, null, "AdminDiseaseScreen"};
					menuScreens[counter+3] = new String[]{"admProc", "dict-category", "Procedimientos", "procedure-icon", null, null, "AdminProceduresScreen"};
					menuScreens[counter+4] = new String[]{"admComp", "dict-category", "Complicaciones", "complication-icon", null, null, "AdminComplicationsScreen"};
					//menuScreens[counter+3] = new String[]{"admDrug", "dict-category", "Medicinas", "drugs-icon", null, null, "AdminDrugsScreen"};
					//menuScreens[counter+4] = new String[]{"admTest", "dict-category", "Examenes de Laboratorio", "labtests-icon", null, null, "AdminLabTestsScreen"};
					counter = counter+5;

				}

				if(allowedModule.getId().equals("med-category")){
					menuScreens[counter] = new String[]{"med-category", null, "Ex&aacute;menes y Medicamentos", "med-icon", null, null, null};
					menuScreens[counter+1] = new String[]{"admDrug", "med-category", "Medicamentos", "drugs-icon", null, null, "AdminDrugsScreen"};
					menuScreens[counter+2] = new String[]{"admTest", "med-category", "Ex&aacute;menes de Laboratorio", "labtests-icon", null, null, "AdminLabTestsScreen"};
					counter = counter+3;

				}


			}
		}

		return menuScreens;
	}


}
