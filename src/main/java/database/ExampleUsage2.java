package database;

import database.dao.UserService;
import database.entity.Message;
import database.entity.User;

import java.util.List;

/**
 * Пример использования БД
 */
public class ExampleUsage2 {
    public static void main(String[] args) {
        UserService userService = new UserService();

//        clearBase(userService);

        User user1 = userService.getUser(674832); //OzzyFrost
        User user2 = userService.getUser(567364); //Stormcoder
//        user1.clearMessages();
//        user2.clearMessages();

        Message message1 = new Message(1453675, "Я делаю сервер", "03.10.18 19:41:42", user1, user2);
        userService.addSentMessage(user1, message1);
        userService.addReceivedMessage(user2, message1);

        Message message2 = new Message(1453676, "Отлично, когда потестим", "03.10.18 19:45:27", user2, user1);
        userService.addSentMessage(user1, message2);
        userService.addReceivedMessage(user2, message2);

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