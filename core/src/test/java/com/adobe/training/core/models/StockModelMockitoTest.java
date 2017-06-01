package com.adobe.training.core.models;
 
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the StockModel using the Mockito testing framework
 * Note that the testable class is under /src/main/java:
 * com.adobe.training.core.models.StockModel.java
 * 
 *  To correctly use this testing class:
 *  -put this file under training.core/src/test/java in the package com.adobe.training.core.models
 * 
 */
public class StockModelMockitoTest {
 
	private StockModel stock;
	
    @Before
    public void setup() throws Exception {
    	
    	//Adapt the Resource if needed
    	Resource RESOURCE_MOCK = mock(Resource.class);
    	StockModel STOCKMODEL_MOCK = mock(StockModel.class);
    	when(RESOURCE_MOCK.adaptTo(StockModel.class)).thenReturn(STOCKMODEL_MOCK);
    	
    	stock = STOCKMODEL_MOCK;
    	
    	//Setup lastTrade Property
    	Random rand = new Random();
    	double n = Math.round(100*(rand.nextInt(150) + 100)+rand.nextDouble())/100; //random value between 100.00 and 150.00
    	when(STOCKMODEL_MOCK.getLastTrade()).thenReturn(n);
    	
    	
    	//Setup requestDate Property
     	int numberOfDays = randBetween(1,365);
    	Date date = new SimpleDateFormat("D").parse(numberOfDays + " " + Calendar.getInstance().get(Calendar.YEAR));
    	String tradeDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
    	when(STOCKMODEL_MOCK.getRequestDate()).thenReturn(tradeDate);
    	
    	//Setup requestTime Property
    	int hours = randBetween(1, 12);
    	int minutes = randBetween(1, 60);
    	String time = hours+":"+minutes+"pm";
    	when(STOCKMODEL_MOCK.getRequestTime()).thenReturn(time);
    }
    
    private static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
	
	@Test
	public void testGetLadeTradeValue() throws Exception{
		assertNotNull("lastTradeModel is null", stock);
		assertTrue("lastTrade value is inccorrect", stock.getLastTrade() > 100);
		assertFalse("requestDate value is inccorrect", stock.getRequestDate().isEmpty());
		assertFalse("requestTime value is inccorrect", stock.getRequestTime().isEmpty());
	}

}