package com.googlecode.semrs.server.service;

import java.util.Collection;
import java.util.Map;

import com.googlecode.semrs.model.Group;
import com.googlecode.semrs.model.Module;
import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface RoleService {
	
    public void save(Role role) throws SaveOrUpdateException;
	
	public Collection<Role> listRoles();
	
	public Role getRole(String id,boolean deep);
	
	public Collection<Role> listRoles(String order, String orderBy, String limit, String offset);
	
	public int getRoleCount();
	
	public Collection<Role> listRolesByQuery(Map params, String order, String orderBy, String limit, String offset);
	
	public int getRoleCount(Map params);
	
	public void deleteRole(Role role) throws DeleteException;
	
	public Collection<Module> getModules();
	
	public Module getModule(String id);
	
	public Collection<Module> getAvailableModules(Role role);

}
