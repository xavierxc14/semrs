package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Module;
import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.server.dao.RoleDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.RoleService;

public class RoleServiceJenaImpl implements RoleService{

    private static final Logger LOG = Logger.getLogger(RoleServiceJenaImpl.class);
	
	private RoleDAO roleDAO;
	
	public RoleServiceJenaImpl(){
		  super();
	}
	
	public void setRoleDAO(RoleDAO roleDAO) {
		this.roleDAO = roleDAO;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public void deleteRole(final Role role) throws DeleteException {
		roleDAO.deleteRole(role);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Module> getModules() {
		return roleDAO.getModules();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Role getRole(final String id, final boolean deep) {
		return roleDAO.getRole(id,deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getRoleCount() {
		return roleDAO.getRoleCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getRoleCount(final Map params) {
		return roleDAO.getRoleCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Role> listRoles() {
		return roleDAO.listRoles();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Role> listRoles(final String order, final String orderBy,
			final String limit, final String offset) {
		return roleDAO.listRoles(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Role> listRolesByQuery(final Map params,final String order,
			final String orderBy, final String limit, final String offset) {
		return roleDAO.listRolesByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public void save(final Role role) throws SaveOrUpdateException {
		roleDAO.save(role);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Module getModule(final String id) {
		return roleDAO.getModule(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Module> getAvailableModules(Role role) {
		return roleDAO.getAvailableModules(role);
	}

}
