package ru.geekbrains.pocket.messenger.database.tests;


import ru.geekbrains.pocket.messenger.database.dao.DataBaseService;
import org.junit.*;
import ru.geekbrains.pocket.messenger.database.entity.*;

import java.sql.Timestamp;

public class testUserDB {

    private DataBaseService dbs = new DataBaseService();
    private UserProfile up;
    private User user;

    @Before
    public void start() {
        up = new UserProfile("111", "User", "Userov", new Timestamp(0l));
        user = new User("u@u.uu", up);
    }

    @Test
    public void testAddUser() {
        dbs.insertUser(user);
        Assert.assertEquals(user, dbs.getUserById("111"));
    }

    @Test
    public void testDelUser() {
        dbs.deleteUser(user);
        Assert.assertNotEquals(user, dbs.getUserById("111"));
}

}
