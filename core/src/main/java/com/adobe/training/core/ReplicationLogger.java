package com.adobe.training.core;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;

import org.apache.sling.event.jobs.consumer.JobConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import java.util.HashMap;
import java.util.Map;

//https://sling.apache.org/documentation/bundles/apache-sling-eventing-and-job-handling.html

/*
 * In general, the eventing mechanism (OSGi EventAdmin) has no knowledge about the contents of an event. Therefore, it can't decide if an event is important and should be processed by someone. As the event mechanism is a "fire event and forget about it" algorithm, there is no way for an event admin to tell if someone has really processed the event. Processing of an event could fail, the server or bundle could be stopped etc.

	On the other hand, there are use cases where the guarantee of processing is a must and usually this comes with the requirement of processing exactly once. Typical examples are sending notification emails (or sms), post processing of content (like thumbnail generation of images or documents), workflow steps etc.

	The Sling Event Support adds the notion of a job. A job is a special event that has to be processed exactly once. To be precise, the processing guarantee is at least once. However, the time window for a single job where exactly once can't be guaranteed is very small. It happens if the instance which processes a job crashes after the job processing is finished but before this state is persisted. Therefore a job consumer should be prepared to process a job more than once. Of course, if there is no job consumer for a job, the job is never processed. However this is considered a deployment error.
 * 
 */

@Component(immediate = true)
@Service(value={JobConsumer.class})
@Property(name=JobConsumer.PROPERTY_TOPICS, value = "com/adobe/training/core/replicationjob")

public class ReplicationLogger implements JobConsumer {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
    
	@Override
	public JobResult process(final Job job) {
		
		final String pagePath = job.getProperty("PAGE_PATH").toString();
		
		ResourceResolver resourceResolver = null;
		try { 
	      Map<String, Object> serviceParams = new HashMap<String, Object>();
		  serviceParams.put(ResourceResolverFactory.SUBSERVICE, "training");
		  resourceResolver = resourceResolverFactory.getServiceResourceResolver(serviceParams);
		} catch (LoginException e) {
			e.printStackTrace();
		}

		// Create a Page object to log its title
		final PageManager pm = resourceResolver.adaptTo(PageManager.class);
		final Page page = pm.getContainingPage(pagePath);
		if(page != null) {
			logger.info("+++++++++++++ ACTIVATION OF PAGE : {}", page.getTitle());
		}	
		return JobConsumer.JobResult.OK;
	}
}