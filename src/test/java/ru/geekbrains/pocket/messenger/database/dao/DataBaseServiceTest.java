package ru.geekbrains.pocket.messenger.database.dao;

import org.junit.*;

import ru.geekbrains.pocket.messenger.database.HibernateUtil;
import ru.geekbrains.pocket.messenger.database.entity.User;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

import java.sql.Timestamp;

public class DataBaseServiceTest {

    private DataBaseService dbs = new DataBaseService();
    private UserProfile up;
    private User user;
    private String Id;

    @Before
    public void setUp() throws Exception {
        Integer id = 111;
        Id = id.toString();
        dbs.setUserDB("t"); // создается тестовая БД с именем "t.db", т.к. имя пользователя не может состоять из 1 символа.
        User w = dbs.getUserById(Id);
        while (!(w==null)) {
            Id = (++id).toString();
            w = dbs.getUserById(Id);
        }
        up = new UserProfile(Id, "U", "U", new Timestamp(0l));
        user = new User("u@u.uu", up);
    }

    @After
    public void tearDown() throws Exception {
            HibernateUtil.deleteDBFile();
    }

    @Test
    public void insertUser() {
        Assert.assertNull( dbs.getUserById(Id));
        dbs.insertUser(user);
        Assert.assertEquals(user, dbs.getUserById(Id));
        dbs.deleteUser(user);
    }

    @Test
    public void deleteUser() {
        dbs.insertUser(user);
        Assert.assertEquals(user, dbs.getUserById(Id));
        dbs.deleteUser(user);
        Assert.assertNull(dbs.getUserById(Id));
    }
}