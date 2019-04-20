package tests;


import org.junit.*;
import ru.geekbrains.pocket.messenger.database.dao.DataBaseService;
import ru.geekbrains.pocket.messenger.database.entity.User;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

import java.sql.Timestamp;

public class testUserDB {

    private DataBaseService dbs = new DataBaseService();
    private UserProfile up;
    private User user;
    String Id;

    @Before
    public void start() {
        Integer id = 111;
        Id = id.toString();
        User w = dbs.getUserById(Id);
        while (!(w==null)) {
            Id = (++id).toString();
            w = dbs.getUserById(Id);
        }
        up = new UserProfile(Id, "User", "Userov", new Timestamp(0l));
        user = new User("u@u.uu", up);
    }

    @Test
    public void testAddUser() {
        dbs.insertUser(user);
        Assert.assertEquals(user, dbs.getUserById(Id));
        dbs.deleteUser(user);
    }

    @Test
    public void testDelUser() {
        dbs.insertUser(user);
        dbs.deleteUser(user);
        Assert.assertNotEquals(user, dbs.getUserById(Id));
    }

    @After
    public void finish() {
        if (!(dbs.getUserById(Id)==null))
            dbs.deleteUser(user);
    }

}
