package ru.geekbrains.pocket.messenger.database.dao;

import org.junit.*;

import ru.geekbrains.pocket.messenger.database.HibernateUtil;
import ru.geekbrains.pocket.messenger.database.entity.*;

import java.sql.Timestamp;
import java.util.List;

public class DataBaseServiceTest {

    private DataBaseService dbs = new DataBaseService();
    private User user;
    private String Id;

    @Before
    public void setUp() throws Exception {
        dbs.setUserDB("t"); // создается тестовая БД с именем "t.db", т.к. имя пользователя не может состоять из 1 символа.
        Integer id = 111;
        Id = id.toString();
        User w = dbs.getUserById(Id);
        while (!(w==null)) {
            Id = (++id).toString();
            w = dbs.getUserById(Id);
        }
        user = new User("u@u.uu", new UserProfile(Id, "U", "U", new Timestamp(0l)));
    }

    @After
    public void tearDown() throws Exception {
            HibernateUtil.deleteDBFile();
    }

    @Test
//    @Ignore
    public void insertUser() {
        Assert.assertNull( dbs.getUserById(Id));
        dbs.insertUser(user);
        Assert.assertEquals(user, dbs.getUserById(Id));
        dbs.deleteUser(user);
    }

    @Test
//    @Ignore
    public void deleteUser() {
        dbs.insertUser(user);
        Assert.assertEquals(user, dbs.getUserById(Id));
        dbs.deleteUser(user);
        Assert.assertNull(dbs.getUserById(Id));
    }

    @Test
    public void addMessage() {
        dbs.insertUser(user);
        User userComm = new User("q@q.qq", new UserProfile("222", "His", "His", new Timestamp(01)));
        dbs.insertUser(userComm);


        Assert.assertTrue(okMessageInDB(user, userComm, "Тест", "1"));
        Assert.assertTrue(okMessageInDB(user, userComm, "Ок!", "2"));
        Assert.assertTrue(okMessageInDB(user, userComm, "", "3"));
        Assert.assertTrue(okMessageInDB(user, userComm, "wefrkncjerEf egtrg wgRW trhg vrbgrwbth rgt...", "4"));
        Assert.assertTrue(okMessageInDB(user, userComm, "Привет, друг! Как дела? ты где сейчас?", "5"));
        Assert.assertTrue(okMessageInDB(userComm, user,"И тебе привет! Всё отлично!!! Я тут,  а ты где?", "6"));
        Assert.assertTrue(okMessageInDB(user, userComm,"И я тут... ", "7"));


    }

    private Boolean okMessageInDB(User user1, User user2, String mess, String id) {
        Message message = new Message();
        message.setSender(user2);
        message.setReceiver(user1);
        message.setText(mess);
        message.setId(id);
        message.setTime(new Timestamp(01));

        dbs.addMessage(message);

        List<Message> spMess = dbs.getChat(message.getSender(), message.getReceiver());
        Message tmpMess = null;
        Boolean found = false;
        for (int i = 0; i<spMess.size(); i++) {
            tmpMess = spMess.get(i);
            if ((message.getId().equals(tmpMess.getId())) & (message.getText().equals(tmpMess.getText()))) found = true;
        }
        return found;
    }
}