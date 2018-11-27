package database;

import database.dao.DataBaseService;
import database.entity.Message;
import database.entity.User;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

/**
 *  Пример использования БД
 */
public class ExampleUsage {
    public static void main(String[] args) {
        DataBaseService dataBaseService = new DataBaseService(new User(1, "test2Name", "test2Email"));

        clearBase(dataBaseService);

        User user1 = new User(567364, "Stormcoder", "st_mail");
        dataBaseService.insertUser(user1);
        User user2 = new User(674832, "OzzyFrost", "of_mail");
        dataBaseService.insertUser(user2);

        Message message1 = new Message("Привет, как дела?", new Timestamp(System.currentTimeMillis()));
        user2.addSentMessage(message1);
        user1.addReceivedMessage(message1);

        Message message2 = new Message("Здорова, отлично", new Timestamp(System.currentTimeMillis()));
        user1.addSentMessage(message2);
        user2.addReceivedMessage(message2);

        dataBaseService.updateUser(user2);
        dataBaseService.updateUser(user1);

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