package database;

import database.dao.UserService;
import database.entity.Message;
import database.entity.User;

import java.util.List;

/**
 *  Пример использования БД
 */
public class ExampleUsage {
    public static void main(String[] args) {
        UserService userService = new UserService();

        clearBase(userService);

        User user1 = new User(567364, "Stormcoder", "st_mail");
        userService.insertUser(user1);
        User user2 = new User(674832, "OzzyFrost", "of_mail");
        userService.insertUser(user2);

        Message message1 = new Message(1453672, "Привет, как дела?", "03.10.18 19:37:32");
        message1.setSender(user2);
        message1.setReceiver(user1);
        user2.addSentMessage(message1);
        user1.addReceivedMessage(message1);

        Message message2 = new Message(1453673, "Здорова, отлично", "03.10.18 19:39:17");
        message2.setSender(user1);
        message2.setReceiver(user2);
        user1.addSentMessage(message2);
        user2.addReceivedMessage(message2);

        userService.updateUser(user2);
        userService.updateUser(user1);
    }

    private static void clearBase(UserService userService) {
        List<User> users = userService.getAllUsers();
        for (User user :
                users) {
            userService.deleteUser(user);
        }
    }
}