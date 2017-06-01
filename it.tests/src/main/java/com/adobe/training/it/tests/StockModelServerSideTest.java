/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.training.it.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.junit.annotations.SlingAnnotationsTestRunner;
import org.apache.sling.junit.annotations.TestReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.adobe.training.core.models.StockModel;


/** 
 *  Test case which uses OSGi services injection
 *  to get hold of the StockModelServerSideTest which 
 *  it wants to test server-side. 
 *  
 *  Based on: https://github.com/Adobe-Marketing-Cloud/aem-project-archetype/blob/master/src/main/archetype/it.tests/src/main/java/it/tests/HelloWorldModelServerSideTest.java
 *
 *  Example URI: http://localhost:4502/system/sling/junit/com.adobe.training.html
 *
 *  To correctly use this testing class:
 *  -put this file under training.it.tests/src/main/java
 *  -Delete HelloWorldModelServerSideTest.java
 *  -On training.it.tests run: mvn clean install
 *  -On training.it.launcher run: mvn install -P integrationTests
 */
@RunWith(SlingAnnotationsTestRunner.class)
public class StockModelServerSideTest {

    @TestReference
    private ResourceResolverFactory rrf;
    
    @Before
    public void prepareData() throws Exception {
        new AdminResolverCallable() {
            @Override
            protected void call0(ResourceResolver rr) throws Exception {
            	Map<String, Object> properties = new HashMap<String, Object>();
            	properties.put("lastTrade", 109);
            	properties.put("requestDate", "11/13/2016");
            	properties.put("requestTime", "5:00pm");
            	properties.put("upDown", .13);
            	properties.put("openPrice", 105);
            	properties.put("rangeHigh", 110);
            	properties.put("rangeLow", 104);
            	properties.put("volume", 2131988);
            	Resource parent = rr.create(rr.getResource("/tmp"), "TEST", null);
                rr.create(parent, "lastTrade", properties);
            }
        }.call();
    }
    
    @After
    public void cleanupData() throws Exception {
        new AdminResolverCallable() {
            @Override
            protected void call0(ResourceResolver rr) throws Exception {
                Resource testResource = rr.getResource("/tmp/TEST");
                if ( testResource != null ) {
                    rr.delete(testResource);
                }
            }
        }.call();
    }
    
    @Test
    public void testStockModelServerSide() throws Exception {
        
        assertNotNull("Expecting the ResourceResolverFactory to be injected by Sling test runner", rrf);
        
        new AdminResolverCallable() {
            @Override
            protected void call0(ResourceResolver rr) throws Exception {
                Resource testResource = rr.getResource("/tmp/TEST");
                
                StockModel stock = testResource.adaptTo(StockModel.class);
                
                assertNotNull("Expecting LastTradeModel to be adapted from Resource", stock);
                assertNotNull("Expecting LastTradeModel to have the last Trade", stock.getLastTrade());
                
                assertTrue("lastTrade property incorrect",stock.getLastTrade() == 109);
                assertTrue("requestDate property incorrect",stock.getRequestDate().equals("11/13/2016"));
                assertTrue("requestTime property incorrect",stock.getRequestTime().equals("5:00pm"));
                assertTrue("timestamp property incorrect",stock.getTimestamp().equals(stock.getRequestDate() +" "+stock.getRequestTime()));
                assertTrue("upDown property incorrect",stock.getUpDown() == .13);
                assertTrue("openPrice property incorrect",stock.getOpenPrice() == 105);
                assertTrue("rangeHigh property incorrect",stock.getRangeHigh() == 110);
                assertTrue("rangeLow property incorrect",stock.getRangeLow() == 104);
                assertTrue("volume property incorrect",stock.getVolume() == 2131988);
            }
        }.call();        
    }
    
    private abstract class AdminResolverCallable implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            
            if ( rrf == null ) {
                throw new IllegalStateException("ResourceResolverFactory not injected");
            }
            
            @SuppressWarnings("deprecation") // fine for testing
            ResourceResolver rr = rrf.getAdministrativeResourceResolver(null);
            try {
                call0(rr);
                rr.commit();
            } finally {
                if ( rr != null ) {
                    rr.close();
                }
            }               
            return null;
        }
        
        protected abstract void call0(ResourceResolver rr) throws Exception;
        
    }    
}
