package com.googlecode.semrs.server.dao.jenaimpl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import thewebsemantic.NotFoundException;

import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.dao.UserDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;


public class UserDAOJenaImpl extends GenericDAOJenaImpl implements UserDAO{

	private static final Logger LOG = Logger.getLogger(UserDAOJenaImpl.class);

	@Override
	public void delete(final User user) throws DeleteException {
		super.delete(user);
	}


	@Override
	public Collection<User> getAllUsers() {
			return (Collection<User>) super.load(User.class);
	}

	@Override
	public User getUserByNicknameFetchAll(String nickname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserByUsername(final String username, final boolean deep) {
			final User user = super.load(User.class, username, deep);
			return user;
	}

	@Override
	public User getUserByUsernameFetchAll(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getUserCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public User getUserForId(final long id) {
			final User user = super.load(User.class, String.valueOf(id), true);
			return user;
	}

	@Override
	public User save(final User user) throws SaveOrUpdateException {
		super.save(user);
			return user;
		
	}

	@Override
	public User saveDeep(final User user) throws SaveOrUpdateException {
		super.saveDeep(user);
			return user;
	}


	@Override
	public Collection<User> find(final String query) {
			final Collection<User> results = (Collection<User>) super.find(User.class, query);
			return results;
		
	}


	@Override
	public Collection<User> getAllUsers(final String order, final String orderBy, final String limit,
			final String offset) {
			final String queryString = 
				"PREFIX bean: <http://semrs.googlecode.com/>" +
				"PREFIX user: <http://semrs.googlecode.com/User>" +
				"SELECT ?s " +
				"WHERE { " +
				"    ?s a  <http://semrs.googlecode.com/User> ." +
				" OPTIONAL { " +
				"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
				"} ."+
				"} "+
				"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


		
			final Collection<User> result = super.execQuery(User.class, queryString,"");
			return result;
	}


	@Override
	public Collection<User> getUsersByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {

			final String username =  params.get("username") == null? "" : params.get("username").toString();
			final String name =  params.get("name") == null? "" : params.get("name").toString();
			final String lastName = params.get("lastName") == null? "" : params.get("lastName").toString();
			final String email =  params.get("email") == null? "" : params.get("email").toString();
			final Collection<String> roles = params.get("roles")==null? new ArrayList<String>() : (Collection<String>)params.get("roles");

			StringBuffer query = new StringBuffer();
			if(!orderBy.equals("")){
				query.append(" OPTIONAL { ");
				query.append("    ?s bean:"+orderBy+" ?"+orderBy+" .");
				query.append( "} .");
			
			}

			if(username!=null && !username.equals("")){
				query.append("    ?s bean:username ?username .");
				query.append(" FILTER regex(?username, \""+username+"\", \"i\") .");
			}
			if(name!=null && !name.equals("")){
				query.append("    ?s bean:name ?name .");
				query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
			}
			if(lastName!=null && !lastName.equals("")){
				query.append("    ?s bean:lastName ?lastName .");
				query.append(" FILTER regex(?lastName, \""+lastName+"\", \"i\") .");
			}
			if(email!=null && !email.equals("")){
				query.append("    ?s bean:email ?email .");
				query.append(" FILTER regex(?email, \""+email+"\", \"i\") .");
			}
			if(roles!=null && roles.size()>0){
				query.append("    ?s bean:roles ?roles .");
				query.append("    ?roles bean:auth ?roleName .");
				String roleNames = "";
				int counter = 0;
				for(String role : roles){
					if(counter==roles.size()-1){
						roleNames += role;
					}else{
						roleNames += role + "|";	
					}
					counter++;
				}
				query.append(" FILTER regex(?roleName, \""+roleNames+"\", \"i\") .");
			}


			final String queryString = 
				"PREFIX bean: <http://semrs.googlecode.com/>" +
				"PREFIX user: <http://semrs.googlecode.com/User>" +
				"SELECT DISTINCT ?s " +
				"WHERE { " +
				"    ?s a  <http://semrs.googlecode.com/User> ." +
				query.toString() +
				"} "+
				"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


			//Collection<User> result = Sparql.exec(getModel(), User.class, queryString);
			final Collection<User> result = super.execQuery(User.class, queryString,"");
			return result;

	}


	@Override
	public int getUsersCount() {
			final String queryString = 
				"PREFIX bean: <http://semrs.googlecode.com/>" +
				"PREFIX user: <http://semrs.googlecode.com/User>" +
				"SELECT (count (distinct *) As ?userCount) " +
				"WHERE { " +
				"    ?s a  <http://semrs.googlecode.com/User> ." +
				"} ";

			return super.count(queryString, "userCount");
	}


	@Override
	public int getUsersCount(final Map params) {
			final String username =  params.get("username") == null? "" : params.get("username").toString();
			final String name =  params.get("name") == null? "" : params.get("name").toString();
			final String lastName = params.get("lastName") == null? "" : params.get("lastName").toString();
			final String email =  params.get("email") == null? "" : params.get("email").toString();
			final Collection<String> roles = params.get("roles")==null? new ArrayList<String>() : (Collection<String>)params.get("roles");

			final StringBuffer queryBuffer = new StringBuffer();

			if(username!=null && !username.equals("")){
				queryBuffer.append("    ?s bean:username ?username .");
				queryBuffer.append(" FILTER regex(?username, \""+username+"\", \"i\") .");
			}
			if(name!=null && !name.equals("")){
				queryBuffer.append("    ?s bean:name ?name .");
				queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
			}
			if(lastName!=null && !lastName.equals("")){
				queryBuffer.append("    ?s bean:lastName ?lastName .");
				queryBuffer.append(" FILTER regex(?lastName, \""+lastName+"\", \"i\") .");
			}
			if(email!=null && !email.equals("")){
				queryBuffer.append("    ?s bean:email ?email .");
				queryBuffer.append(" FILTER regex(?email, \""+email+"\", \"i\") .");
			}
			if(roles!=null && roles.size()>0){
				queryBuffer.append("    ?s bean:roles ?roles .");
				queryBuffer.append("    ?roles bean:auth ?roleName .");
				String roleNames = "";
				int counter = 0;
				for(String role : roles){
					if(counter==roles.size()-1){
						roleNames += role;
					}else{
						roleNames += role + "|";	
					}
					counter++;
				}
				queryBuffer.append(" FILTER regex(?roleName, \""+roleNames+"\", \"i\") .");
			}

			final String queryString = 
				"PREFIX bean: <http://semrs.googlecode.com/>" +
				"PREFIX user: <http://semrs.googlecode.com/User>" +
				"SELECT (count (distinct *) As ?userCount) " +
				"WHERE { " +
				"    ?s a  <http://semrs.googlecode.com/User> ." +
				queryBuffer.toString() + 
				"} ";


			return super.count(queryString, "userCount");
	}


	@Override
	public Role getRoleById(final String id) throws NotFoundException {
			final Role role = super.load(Role.class, id, true);
			return role;
	}


	@Override
	public Collection<Role> getAllRoles() {
			return super.load(Role.class);
	}

	
	@Override
	public Collection<Role> getAvailableRoles(final User user) {
		Collection<Role> result = null; 
		if(user != null){  
			Collection<Role> roles = user.getRoles();
			final StringBuffer query = new StringBuffer();
			  if(roles!=null && roles.size()>0){
					String roleIds = "";
					int counter = 0;
					for(Role role : roles){
			
						if(counter==roles.size()-1){
							roleIds += "?id != " + "\""+role.getId()+"\"";
						}else{
							roleIds += "?id != " + "\""+role.getId()+"\""+ " && ";	
						}
						counter++;
					}
				
					query.append(" FILTER ("+roleIds+") .");
				}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/Role> ." +
		             "    ?s bean:auth ?id ." +
		                  query.toString() +
		             "} ";
			     
			     
			    result = super.execQuery(Role.class, queryString, "");
		
		}else{
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/Role> ." +
		             "} ";
			     
			     
			    result = super.execQuery(Role.class, queryString, "");
		}
		
		return result;
	}

}
