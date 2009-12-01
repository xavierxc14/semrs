package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.semrs.model.Module;
import com.googlecode.semrs.model.Role;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.dao.RoleDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;

public class RoleDAOJenaImpl extends GenericDAOJenaImpl implements RoleDAO {

	private static final Logger LOG = Logger.getLogger(RoleDAOJenaImpl.class);
	
	
	@Override
	public void deleteRole(final Role role) throws DeleteException {
		super.delete(role);
		
	}

	@Override
	public Collection<Module> getModules() {
		return (Collection<Module>) super.load(Module.class);
	}

	@Override
	public Role getRole(final String id, final boolean deep) {
		final Role role = super.load(Role.class, id, deep);
		return role;
	}

	@Override
	public int getRoleCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Role>" +
			"SELECT (count (distinct *) As ?roleCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Role> ." +
			"} ";

		return super.count(queryString,"roleCount");
	}

	@Override
	public int getRoleCount(final Map params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<Role> listRoles() {
		return (Collection<Role>) super.load(Role.class);
	}

	@Override
	public Collection<Role> listRoles(final String order, final String orderBy,
			final String limit, final String offset) {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Role>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Role> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


		//Collection<Role> result = Sparql.exec(getModel(), Role.class, queryString);
		final Collection<Role> result = super.execQuery(Role.class, queryString, "");
		return result;

	}

	@Override
	public Collection<Role> listRolesByQuery(Map params, String order,
			String orderBy, String limit, String offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role save(final Role role) throws SaveOrUpdateException {
		super.saveDeep(role);
		return role;
	}

	@Override
	public Module getModule(final String id) {
		final Module module = super.load(Module.class, id, true);
		return module;
	}

	
	@Override
	public Collection<Module> getAvailableModules(final Role role) {
		Collection<Module> result = null; 
		if(role != null){  
			Collection<Module> modules = role.getModules();
			final StringBuffer query = new StringBuffer();
			  if(modules!=null && modules.size()>0){
					String moduleIds = "";
					int counter = 0;
					for(Module mod : modules){
						/*
						if(counter==modules.size()-1){
							moduleIds += mod.getId();
						}else{
							moduleIds += mod.getId() + "|";	
						}
						counter++;
						*/
						if(counter==modules.size()-1){
							moduleIds += "?id != " + "\""+mod.getId()+"\"";
						}else{
							moduleIds += "?id != " + "\""+mod.getId()+"\""+ " && ";	
						}
						counter++;
					}
					//query.append(" FILTER (!regex(?id, \""+moduleIds+"\", \"i\")) .");
					query.append(" FILTER ("+moduleIds+") .");
					
				}
				
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/Module> ." +
		             "    ?s bean:id ?id ." +
		                  query.toString() +
		             "} ";;
			     
			     
			    result = super.execQuery(Module.class, queryString, "");
		
		}else{
			 result  = super.load(Module.class);
		}
		
		return result;
	}

}
