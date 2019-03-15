package ru.geekbrains.pocket.messenger.database;

import ru.geekbrains.pocket.messenger.database.dao.DataBaseService;
import ru.geekbrains.pocket.messenger.database.entity.User;

import java.util.List;

/**
 * Пример использования БД
 */
public class ExampleUsage2 {
    public static void main(String[] args) {
        /*DataBaseService dataBaseService = new DataBaseService(new User(1, "testName", "testEmail"));

//        clearBase(dataBaseService);

        User user1 = dataBaseService.getUser(674832);
        User user2 = dataBaseService.getUser(567364);
        Message message1 = new Message("Я делаю сервер", new Timestamp(System.currentTimeMillis()), user1, user2);

        dataBaseService.addMessage(user1.getId(), user2.getId(), message1);

        Message message2 = new Message("Отлично, когда потестим", new Timestamp(System.currentTimeMillis()), user2, user1);

        dataBaseService.addMessage(user2.getId(), user1.getId(), message2);

        HibernateUtil.shutdown();*/
    }

    private static void clearBase(DataBaseService dataBaseService) {
        List<User> users = dataBaseService.getAllUsers();
        for (User user :
                users) {
            dataBaseService.deleteUser(user);
        }
    }
}