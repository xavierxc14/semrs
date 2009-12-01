package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.semrs.model.Group;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.dao.GroupDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.GroupService;

public class GroupServiceJenaImpl implements GroupService{
	
    private static final Logger LOG = Logger.getLogger(GroupServiceJenaImpl.class);
	
	private GroupDAO groupDAO;
	
	public GroupServiceJenaImpl(){
		  super();
	}
	
	public void setGroupDAO(GroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}


	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public void deleteGroup(final Group group) throws DeleteException {
		groupDAO.deleteGroup(group);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Group getGroup(final String id, final boolean deep) {
		return groupDAO.getGroup(id,deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getGroupCount() {
		return groupDAO.getGroupCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getGroupCount(final Map params) {
		return groupDAO.getGroupCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Group> listGroups() {
		return groupDAO.listGroups();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Group> listGroups(final String order, final String orderBy,
			final String limit, final String offset) {
		return groupDAO.listGroups(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Group> listGroupsByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		return groupDAO.listGroupsByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public void save(final Group group) throws SaveOrUpdateException {
		groupDAO.save(group);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<User> getAvailableUsers(Group group) {
		return groupDAO.getAvailableUsers(group);
	}


}
