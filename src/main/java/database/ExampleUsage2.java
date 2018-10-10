package database;

import database.dao.DataBaseService;
import database.entity.Message;
import database.entity.User;

import java.util.List;

/**
 * Пример использования БД
 */
public class ExampleUsage2 {
    public static void main(String[] args) {
        DataBaseService dataBaseService = new DataBaseService();

//        clearBase(dataBaseService);

        User user1 = dataBaseService.getUser(674832);
        User user2 = dataBaseService.getUser(567364);
        Message message1 = new Message(1453675, "Я делаю сервер", "03.10.18 19:41:42", user1, user2);

        dataBaseService.addSentMessage(user1.getId(), message1);
        dataBaseService.addReceivedMessage(user2.getId(), message1);

        Message message2 = new Message(1453676, "Отлично, когда потестим", "03.10.18 19:45:27", user2, user1);

        dataBaseService.addSentMessage(user1.getId(), message2);
        dataBaseService.addReceivedMessage(user2.getId(), message2);

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