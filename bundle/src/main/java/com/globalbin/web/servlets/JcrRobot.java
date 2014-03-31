package com.globalbin.web.servlets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.RowIterator;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.jcr.JsonJcrNode;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(Servlet.class)
@Properties({ 
	@Property(name="service.description", value="JcrRobot Servlet"),
    @Property(name="service.vendor", value="The GlobalBin"),
    @Property(name="sling.servlet.extensions", value="json"),
    @Property(name="sling.servlet.paths", value="/bin/bot")
})

public class JcrRobot extends SlingAllMethodsServlet {
	
	private static final long serialVersionUID = 2398798435L;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Reference
	private SlingRepository repository;
	protected void bindRepository(SlingRepository repository) {
		this.repository = repository;
	}
	
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) 
    		throws ServletException, IOException {
    	
    	
    	// String sqlQuery = getQueryString(request.getParameter("q"));
    	
    	JSONObject result = new JSONObject();
		try {
			Session session = this.repository.loginAdministrative(null);
			List<Node> nodes = queryNodes(session, "/content/geometrixx/en/events");
			result.put("result", nodesToJsonArray(nodes));
			session.logout();
			
		} catch (RepositoryException e) {
		} catch (JSONException jse) {
		}
    	response.setContentType("application/json");
    	Writer w = response.getWriter();
        w.write(result.toString());
    }
    
    public JSONArray nodesToJsonArray(List<Node> list) {
    	if (list == null) return null;
    	
    	JSONArray jsonArray = new JSONArray();
    	Iterator<Node> it  = list.iterator();    	
    	while(it.hasNext()){
    		Node n = (Node) it.next();
    		try {
				JsonJcrNode jjn = new JsonJcrNode(n);
	    		jsonArray.put(jjn);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
    	}
    	return jsonArray;
    }
    
    public String getQueryString(String path) {
    	
    	String sqlQuery = "SELECT * FROM [cq:Page] AS parent " +
    			"INNER JOIN [nt:base] AS child " + 
    			"ON ISCHILDNODE(child,parent) " +
    			"WHERE ISDESCENDANTNODE(parent, '"+ path +"')";
    	
    	return sqlQuery;
    }
    
    public List<Node> queryNodes(Session session, String path) {
    	List<Node> nodeList = null;
    	String queryString = getQueryString(path);
    	
		try {
			
	    	QueryManager queryManager = session.getWorkspace().getQueryManager();
	    	Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);
	    	RowIterator rowIterator = query.execute().getRows();
	    	if (rowIterator!=null) {
		    	nodeList = rowIteratorToArrayList(rowIterator, "parent");	    		
	    	}
	    	
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return nodeList;    	
    }
    
	public List<Node> rowIteratorToArrayList(RowIterator ni, String nodeSelector) {		
		if (ni == null) return null;
		List<Node> list = new ArrayList<Node>();
    	while(ni.hasNext()) {
			try {
				Node n = ni.nextRow().getNode(nodeSelector);
	    		list.add(n);
			} catch (RepositoryException e) {
			}
    	}
    	return list;
    }
	
}
