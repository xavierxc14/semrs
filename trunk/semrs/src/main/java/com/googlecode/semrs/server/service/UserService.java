package com.googlecode.semrs.server.service;

import java.util.Collection;
import java.util.Map;

import thewebsemantic.NotFoundException;

import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public interface UserService {
	
	public User getUserByUsername(String userName,boolean deep);
	
	public Collection<User> find(String query);
	
	public Collection<User> getAllUsers();
	
	public Collection<User> getAllUsers(String order, String orderBy, String limit, String offset);
	
	public Collection<User> getUsersByQuery(Map params, String order, String orderBy, String limit, String offset);
	
    public int getUsersCount();
    
    public int getUsersCount(Map params);
    
    public User save(User user) throws SaveOrUpdateException;
    
    public Role getRoleById(String id) throws NotFoundException;
    
    public Collection<Role> getRoles();
    
    public void deleteUser(User userToDelete) throws DeleteException;

	public User saveDeep(User user) throws SaveOrUpdateException;
	
	public Collection<Role> getAvailableRoles(User user);
	
	public User getCurrentUser();
}
