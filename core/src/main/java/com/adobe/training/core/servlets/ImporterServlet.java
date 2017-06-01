package com.adobe.training.core.servlets;

import java.io.IOException;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.Node;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;

/**
 * Serlvet that allows a text component to be added to pages
 * 
 * To use this serlvet, a new node must be created:
 * /etc/trainingproject/importer (nt:unstructured)
 *    +sling:resourceType=trainingproject/tools/importer
 *    
 * To Test:
 * http://localhost:4502/etc/trainingproject/importer.html
 * 
 * @author Kevin Nennig (nennig@adobe.com)
 */
@SlingServlet(resourceTypes="trainingproject/tools/importer")
public class ImporterServlet extends SlingSafeMethodsServlet {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
		throws ServletException, IOException {
		response.setHeader("Content-Type", "text/html");
		
		Session session = request.getResourceResolver().adaptTo(Session.class);
		
		try{
		String q = "/jcr:root/content/trainingproject/es//*" +
				"[@sling:resourceType='wcm/foundation/components/parsys']";
		Query query = session.getWorkspace().getQueryManager()
					.createQuery(q, "xpath");
		NodeIterator result = query.execute().getNodes();
		while (result.hasNext()) {
			Node n = result.nextNode();
		    Node newTextNode = JcrUtil.createUniqueNode(n, "newtext", 
					"nt:unstructured", session);
		    newTextNode.setProperty("sling:resourceType", 
					"foundation/components/text");
		    newTextNode.setProperty("text", "<h3>A Text Component from a Servlet using the JCR API</h3>");
		    newTextNode.setProperty("textIsRich", "true");
		    response.getWriter().print("Added node: " + n.getPath() + "<br />");
		}
		    session.save();
		}
		catch (RepositoryException e){
			response.getWriter().print("Could not create nodes");
			logger.error(e.getMessage() + e);
		}
		response.getWriter().close();
	}
	
	
	
	
}
