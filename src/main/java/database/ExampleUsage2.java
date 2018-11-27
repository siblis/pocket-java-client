package database;

import database.dao.DataBaseService;
import database.entity.Message;
import database.entity.User;

import java.sql.Timestamp;
import java.util.List;

/**
 * Пример использования БД
 */
public class ExampleUsage2 {
    public static void main(String[] args) {
        DataBaseService dataBaseService = new DataBaseService(new User(1, "testName", "testEmail"));

//        clearBase(dataBaseService);

        User user1 = dataBaseService.getUser(674832);
        User user2 = dataBaseService.getUser(567364);
        Message message1 = new Message("Я делаю сервер", new Timestamp(System.currentTimeMillis()), user1, user2);

        dataBaseService.addMessage(user1.getUid(), user2.getUid(), message1);

        Message message2 = new Message("Отлично, когда потестим", new Timestamp(System.currentTimeMillis()), user2, user1);

        dataBaseService.addMessage(user2.getUid(), user1.getUid(), message2);

        HibernateUtil.shutdown();
    }

    private static void clearBase(DataBaseService dataBaseService) {
        List<User> users = dataBaseService.getAllUsers();
        for (User user :
                users) {
            dataBaseService.deleteUser(user);
        }
    }
}