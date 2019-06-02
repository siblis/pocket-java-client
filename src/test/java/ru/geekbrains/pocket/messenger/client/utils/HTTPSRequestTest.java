package ru.geekbrains.pocket.messenger.client.utils;

import java.lang.reflect.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class HTTPSRequestTest {
    
    static final Logger logger = LogManager.getLogger(HTTPSRequestTest.class);

    /**
     * Switch {@link HTTPSRequest} to testMode, by which 
     * {@link HTTPSRequest#sendRequest(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * will return {@code forTestRequestCode} and 
     * {@link HTTPSRequest#getResponse()} will return {@code forTestRequestString}.
     *
     * @param forTestResponseCode wished responseCode from server
     * @param forTestResponseString wished responseString from server
     */
    public static void testModeInit(int forTestResponseCode, String forTestResponseString) {
        HTTPSRequest.testModeInit(forTestResponseCode, forTestResponseString);
    }

    @AfterClass
    public static void tearDownClass() {
        HTTPSRequest.testModeEnd();
    }
    
    private Object getFieldValue(String testFieldName) throws SecurityException, IllegalAccessException, 
            NoSuchFieldException, IllegalArgumentException {
        HTTPSRequest testClazz = new HTTPSRequest();
        Field temp = testClazz.getClass().getDeclaredField(testFieldName);
        if (!temp.isAccessible()) temp.setAccessible(true);
        return temp.get(testClazz);
    }

    /**
     * Test of testModeInit method, of class HTTPSRequest.
     */
    @Test
    public void testModeInitTest() {
        int testRequestCode = 200;
        String testRequestString = "{ message: messageFromServer }";
        logger.info("testModeInit: start");

        try {
            if (!((boolean) getFieldValue("isTestMode"))) {
                HTTPSRequest.testModeInit(0, null);
                HTTPSRequest testClazz = new HTTPSRequest();
                Field temp = testClazz.getClass().getDeclaredField("isTestMode");
                if (!temp.isAccessible()) temp.setAccessible(true);
                temp.set(testClazz, false);
            }
            
            assertFalse((boolean) getFieldValue("isTestMode"));
            assertEquals(0, (int) getFieldValue("testRequestCode"));
            assertNull(getFieldValue("testRequestString"));
            
            HTTPSRequest.testModeInit(testRequestCode, testRequestString);
            
            assertTrue((boolean) getFieldValue("isTestMode"));
            assertEquals(testRequestCode, (int) getFieldValue("testRequestCode"));
            assertEquals(testRequestString, (String) getFieldValue("testRequestString"));
            
            logger.info("testModeInit: end");
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
            logger.error(ex);
        }
    }

    /**
     * Test of testModeEnd method, of class HTTPSRequest.
     */
    @Test
    public void testModeEndTest() {
        logger.info("testTestModeEnd: start");
        try {
            if (!((boolean) getFieldValue("isTestMode")))
                 HTTPSRequest.testModeInit(1, "prepare");
            
            assertTrue((boolean) getFieldValue("isTestMode"));
            
            HTTPSRequest.testModeEnd();
            
            assertFalse((boolean) getFieldValue("isTestMode"));
            
            logger.info("testTestModeEnd: end");
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
            logger.error(ex);
        }
    }
}