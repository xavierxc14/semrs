package com.googlecode.semrs.server.dao;

import java.util.Collection;
import java.util.Map;

import thewebsemantic.NotFoundException;

import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;



public interface UserDAO{

    void delete(User user) throws DeleteException;

    Collection<User> getAllUsers();

    User getUserByUsername(String username, boolean deep);

    User getUserForId(long id);

    User save(User user) throws SaveOrUpdateException;

    long getUserCount();

    User getUserByUsernameFetchAll(String username);

    User getUserByNicknameFetchAll(String nickname);
    
    Collection<User> find(String query);
    
    Collection<User> getAllUsers(String order, String orderBy, String limit, String offset);
    
    Collection<User> getUsersByQuery(Map params, String order, String orderBy, String limit, String offset);
    
    int getUsersCount();
    
    int getUsersCount(Map params);
    
    public Role getRoleById(String id) throws NotFoundException;
    
    Collection<Role> getAllRoles();

	User saveDeep(User user) throws SaveOrUpdateException;

	public Collection<Role> getAvailableRoles(User user);


}
