package com.googlecode.semrs.server.service.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import thewebsemantic.NotFoundException;

import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.dao.UserDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.googlecode.semrs.server.service.UserService;

public class UserServiceJenaImpl implements UserService{
	
    private static final Logger LOG = Logger
    .getLogger(UserServiceJenaImpl.class);
    
	
	private UserDAO userDAO;
	
    public UserServiceJenaImpl() {
        super();
    }

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public User getUserByUsername(final String userName, final boolean deep) {
	    return userDAO.getUserByUsername(userName,deep);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<User> find(final String query) {
		Collection<User> results = userDAO.find(query);
		return results;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<User> getAllUsers() {
       return userDAO.getAllUsers();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<User> getAllUsers(final String order, final String orderBy, final String limit,
			final String offset) {
		return userDAO.getAllUsers(order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<User> getUsersByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {
		return userDAO.getUsersByQuery(params, order, orderBy, limit, offset);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getUsersCount() {
		return userDAO.getUsersCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public int getUsersCount(final Map params) {
	    return userDAO.getUsersCount(params);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public User save(final User user) throws SaveOrUpdateException {
		userDAO.save(user);
		return user;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public User saveDeep(final User user) throws SaveOrUpdateException {
		userDAO.saveDeep(user);
		return user;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Role getRoleById(final String id) throws NotFoundException {
		return userDAO.getRoleById(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Role> getRoles() {
		return userDAO.getAllRoles();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = false, propagation=Propagation.REQUIRED )
	public void deleteUser(final User userToDelete) throws DeleteException {
		userDAO.delete(userToDelete);
		
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional( readOnly = true, propagation=Propagation.SUPPORTS )
	public Collection<Role> getAvailableRoles(User user) {
		return userDAO.getAvailableRoles(user);
	}

	@Override
	public User getCurrentUser() {
		User currentUser = null;
		try {
			Object obj = SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			if (obj instanceof UserDetails) {
			 currentUser = getUserByUsername(((UserDetails) obj).getUsername(),false);
			}
		} catch (Exception e) {
			LOG.error("Error in getCurrentUser = " + e);
		}
		return currentUser;
	}

}
