package com.googlecode.semrs.server.dao.jenaimpl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import thewebsemantic.NotFoundException;
import thewebsemantic.Sparql;

import com.googlecode.semrs.model.Disease;
import com.googlecode.semrs.model.Group;
import com.googlecode.semrs.model.Procedure;
import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.dao.GenericDAO;
import com.googlecode.semrs.server.dao.GroupDAO;
import com.googlecode.semrs.server.exception.DeleteException;
import com.googlecode.semrs.server.exception.SaveOrUpdateException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

public class GroupDAOJenaImpl extends GenericDAOJenaImpl  implements GroupDAO{
	
	private static final Logger LOG = Logger.getLogger(GroupDAOJenaImpl.class);	

	@Override
	public void deleteGroup(final Group group) throws DeleteException {
		super.delete(group);

	}

	@Override
	public Group getGroup(final String id, final boolean deep) {
		final Group group = super.load(Group.class, id, deep);
		return group;
	}

	@Override
	public int getGroupCount() {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Group>" +
			"SELECT (count (distinct *) As ?groupCount) " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Group> ." +
			"} ";

	
		return super.count(queryString, "groupCount");
	}

	@Override
	public int getGroupCount(final Map params) {
		
		final String groupId = params.get("groupId").toString();
		final String name =  params.get("name").toString();
		final Collection<String> users = (Collection<String>)params.get("users");
		
		final StringBuffer queryBuffer = new StringBuffer();
		
	    if(name!=null && !name.equals("")){
	    	queryBuffer.append("    ?s bean:name ?name .");
	    	queryBuffer.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(groupId!=null && !groupId.equals("")){
			queryBuffer.append("    ?s bean:groupId ?groupId .");
			queryBuffer.append(" FILTER regex(?groupId, \""+groupId+"\", \"i\") .");
		}
	    if(users!=null && users.size()>0){
			queryBuffer.append("    ?s bean:users ?users .");
			queryBuffer.append("    ?users bean:userName ?userName .");
			String userNames = "";
			int counter = 0;
			for(String user : users){
				if(counter==users.size()-1){
					userNames += user;
				}else{
					userNames += user + "|";	
				}
				counter++;
			}
			queryBuffer.append(" FILTER regex(?userName, \""+userNames+"\", \"i\") .");
		}
		
	   
	    final String queryString = 
             "PREFIX bean: <http://semrs.googlecode.com/>" +
             "PREFIX user: <http://semrs.googlecode.com/Group>" +
             "SELECT (count (distinct *) As ?groupCount) " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Group> ." +
                   queryBuffer.toString() +
             "} ";

	
		 return super.count(queryString, "groupCount");
	}

	@Override
	public Collection<Group> listGroups() {
		return (Collection<Group>) super.load(Group.class);
	}

	@Override
	public Collection<Group> listGroups(final String order, final String orderBy,
			final String limit, final String offset) {
		final String queryString = 
			"PREFIX bean: <http://semrs.googlecode.com/>" +
			"PREFIX user: <http://semrs.googlecode.com/Group>" +
			"SELECT ?s " +
			"WHERE { " +
			"    ?s a  <http://semrs.googlecode.com/Group> ." +
			" OPTIONAL { " +
			"    ?s bean:"+orderBy+" ?"+orderBy+" ." +
			"} ."+
			"} "+
			"ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;


		
		final Collection<Group> result = super.execQuery(Group.class, queryString, "");
		return result;
	}

	@Override
	public Collection<Group> listGroupsByQuery(final Map params, final String order,
			final String orderBy, final String limit, final String offset) {

		
		final String groupId = params.get("groupId").toString();
		final String name =  params.get("name").toString();
		final Collection<String> users = (Collection<String>)params.get("users");
		
		final StringBuffer query = new StringBuffer();
		if(!orderBy.equals("")){
			query.append(" OPTIONAL { ");
			query.append("    ?s bean:"+orderBy+" ?"+orderBy+" .");
			query.append( "} .");
			
		}
		
	    if(name!=null && !name.equals("")){
			query.append("    ?s bean:name ?name .");
			query.append(" FILTER regex(?name, \""+name+"\", \"i\") .");
		}
	    if(groupId!=null && !groupId.equals("")){
			query.append("    ?s bean:groupId ?groupId .");
			query.append(" FILTER regex(?groupId, \""+groupId+"\", \"i\") .");
		}
	    if(users!=null && users.size()>0){
			query.append("    ?s bean:users ?users .");
			query.append("    ?users bean:userName ?userName .");
			String userNames = "";
			int counter = 0;
			for(String user : users){
				if(counter==users.size()-1){
					userNames += user;
				}else{
					userNames += user + "|";	
				}
				counter++;
			}
			query.append(" FILTER regex(?userName, \""+userNames+"\", \"i\") .");
		}
		
	   
	    final String queryString = 
             "PREFIX bean: <http://semrs.googlecode.com/>" +
             "PREFIX user: <http://semrs.googlecode.com/Group>" +
             "SELECT DISTINCT ?s " +
             "WHERE { " +
             "    ?s a  <http://semrs.googlecode.com/Group> ." +
                  query.toString() +
             "} "+
             "ORDER BY "+order+"(?"+orderBy+") LIMIT "+limit+" OFFSET "+offset;
	     
	     
	   
	    final Collection<Group> result = super.execQuery(Group.class, queryString, "");
		return result;
	}

	@Override
	public Group save(final Group group) throws SaveOrUpdateException {
		synchronized(group){
			final String queryString = 
				"PREFIX bean: <http://semrs.googlecode.com/>" +
				"PREFIX user: <http://semrs.googlecode.com/Group>" +
				"SELECT ?lastGroupId " +
				"WHERE { " +
				"    ?s a <http://semrs.googlecode.com/Group> ." +
				"    ?s bean:groupId ?lastGroupId ." +
				"} "+
				"ORDER BY desc(?lastGroupId) LIMIT 1 ";

			
			int lastGroupId = 0;
			try{
				
				lastGroupId = super.count(queryString, "lastGroupId");
			}catch(Exception e){}
			
			

			group.setGroupId(String.valueOf(lastGroupId+1));

			super.saveDeep(group);
		}
		return group;
		
	}

	
	@Override
	public Collection<User> getAvailableUsers(final Group group) {
		Collection<User> result = null; 
		if(group != null){  
			Collection<User> users = group.getUsers();
			super.fill(group, "users");
			final StringBuffer query = new StringBuffer();
			  if(users!=null && users.size()>0){
					String userIds = "";
					int counter = 0;
			
					for(User user : users){
					if(counter==users.size()-1){
						userIds += "?id != " + "\""+user.getId()+"\"";
					}else{
						userIds += "?id != " + "\""+user.getId()+"\""+ " && ";	
					}
					counter++;
				}
			       
				query.append(" FILTER ("+userIds+") .");
				}
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/User> ." +
		             "    ?s bean:username ?id ." +
		                  query.toString() +
		             "} ";
			     
			     
			    result = super.execQuery(User.class, queryString, "");
		
		}else{
			
			  final String queryString = 
		             "PREFIX bean: <http://semrs.googlecode.com/>" +
		             "SELECT DISTINCT ?s " +
		             "WHERE { " +
		             "    ?s a  <http://semrs.googlecode.com/User> ." +
		             "    ?s bean:enabled ?enabled ." +
		             "    FILTER (?enabled = true) ." +
		             "} ";
			     
			     
			    result = super.execQuery(User.class, queryString, "");
			
		}
		
		return result;
	}

}
