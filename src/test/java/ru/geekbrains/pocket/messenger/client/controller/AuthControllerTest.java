package ru.geekbrains.pocket.messenger.client.controller;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.AfterClass;
import org.junit.Test;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequestTest;
import static org.junit.Assert.*;
import ru.geekbrains.pocket.messenger.database.HibernateUtil;

public class AuthControllerTest {
    
    static final Logger controllerLogger = LogManager.getLogger(AuthControllerTest.class);
    static final ClientController cc = ClientController.getInstance();

    @AfterClass
    public static void tearDownClass() {
        HibernateUtil.deleteDBFile();
        cc.disconnect();
        HTTPSRequestTest.tearDownClass();
    }
    
    /**
     * Test of registration method, of class AuthController (over ClientController).
     */
    @Test
    public void registrationTest() {
        HTTPSRequestTest.testModeInit(400, "{ message: ValidationErrorCollection,"
                + "data: {"
                    + "email: not valid,"
                    + "password: too short,"
                    + "name: too short"
                + "}"
            + "}");
        assertFalse(cc.proceedRegister("T", "Fail", "Test"));
        
        HTTPSRequestTest.testModeInit(409, "AlreadyRegistered");
        assertFalse(cc.proceedRegister("Test", "123456", "a@mail.ru"));
        
        HTTPSRequestTest.testModeInit(429, "Too Many Requests");
        assertFalse(cc.proceedRegister("Test", "123456", "a@mail.ru"));
        
        HTTPSRequestTest.testModeInit(201, "{  token : 9GJ1zmNgBbFGMvD4xSM_Ilg1KqJOnEWNT2QvccSEy48,"
                + "user : {"
                    + "id : 5cb70937c4504500019c5312,"
                    + "email : b@mail.ru,"
                    + "profile : {"
                        + "id : 5cb70937c4504500019c5312,"
                        + "username : RegTest,"
                        + "fullname : null,"
                        + "last_seen : null"
                    + "}"
                + "}"
            + "}");
        assertTrue(cc.proceedRegister("RegTest", "123456", "regTest@mail.ru"));
    }
}
