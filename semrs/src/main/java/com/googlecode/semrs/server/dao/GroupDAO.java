package com.googlecode.semrs.server.dao;

import java.util.Collection;
import java.util.Map;

import com.googlecode.semrs.model.Group;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface GroupDAO {
	
	public Group save(Group group) throws SaveOrUpdateException;
	
	public Collection<Group> listGroups();
	
	public Group getGroup(String id, boolean deep);
	
	public Collection<Group> listGroups(String order, String orderBy, String limit, String offset);
	
	public int getGroupCount();
	
	public Collection<Group> listGroupsByQuery(Map params, String order, String orderBy, String limit, String offset);
	
	public int getGroupCount(Map params);
	
	public void deleteGroup(Group group) throws DeleteException;

	public Collection<User> getAvailableUsers(Group group);

}
