package ru.geekbrains.pocket.messenger.client.controller;

import org.hibernate.HibernateException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.AuthFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.UserFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.UserProfileFromServer;
import ru.geekbrains.pocket.messenger.database.HibernateUtil;
import ru.geekbrains.pocket.messenger.database.entity.User;

import java.io.File;

public class AuthControllerTest  {
    private File FileTestDb;
    private ClientController clientController;

    @Before
    public void init() {
        FileTestDb = new File(System.getProperty("user.dir") + File.separator + "database", "t.db");
        clientController = new ClientController();
        if (FileTestDb.exists())
            FileTestDb.delete();
    }

    @After
    public void tearDown() throws HibernateException {
        HibernateUtil.deleteDBFile();
    }

    @Test
    public void registrationTest() throws HibernateException {
        // тестовые данные
        String email = "mail@mail.ru";
        String name = "testUser";
        String id = "111";
        String token = "aaa";
        UserProfileFromServer userProfileFromServer = new UserProfileFromServer(id, name, null, null);
        UserFromServer userFromServer = new UserFromServer(id, email, userProfileFromServer);
        AuthFromServer responseFromServer = new AuthFromServer(token, userFromServer);

        // registration
        if (responseFromServer != null) {
            if (responseFromServer.getUser() != null) {
                User regUser = responseFromServer.getUser().toUser();
                clientController.dbService.setUserDB("t"); // тестовая БД
                clientController.dbService.insertUser(regUser);
                Assert.assertEquals(regUser, clientController.dbService.getUserById(id));
            }
        }
    }

}
