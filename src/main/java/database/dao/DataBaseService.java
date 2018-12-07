package database.dao;

import database.HibernateUtil;
import database.entity.Message;
import database.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Если программа должна выполнить какую-то бизнес-логику - она делает
 * это через сервисы. Сервис содержит внутри себя UserDao, и в своих
 * методах вызывает методы DAO. Это может показаться дублированием функций
 * (почему бы просто не вызывать методы из dao-объекта), но при большом
 * количестве объектов и сложной логике разбиение приложения на слои дает
 * огромные преимущества (это good practice").
 *
 */
public class DataBaseService {

    private UserDAO usersDao;
    private MessageDAO messageDao;

    public DataBaseService(User myUser) {
        HibernateUtil.setUserName(myUser.getAccount_name());
        usersDao = new UserDAO();
        messageDao = new MessageDAO();
    }

    public void insertUser(User user) {
        usersDao.insert(user);
    }

    public void deleteUser(User user) {
        usersDao.delete(user);
    }

    public void updateUser(User user) {
        usersDao.update(user);
    }

    public User getUser(long id) {
        return usersDao.get(id);
    }

    public User getUserByName(String userName) {
        return usersDao.get(userName);
    }

    public List<User> getAllUsers() {
        return usersDao.get();
    }

    public List<String> getAllUserNames() {
        List<String> names = new ArrayList<>();
        for (User user : usersDao.get()) {
            names.add(user.getAccount_name());
        }
        return names;
    }

    public List<Long> getAllUserId() {
        List<Long> ides = new ArrayList<>();
        for (User user : usersDao.get()) {
            ides.add(user.getUid());
        }
        return ides;
    }

    public void addMessage(long receiverId, long senderID, Message message) {
        messageDao.addSentMessage(senderID, message);
        messageDao.addReceivedMessage(receiverId, message);
    }

    public List<Message> getChat(User user1, User user2){
        return messageDao.get(user1, user2);
    }

    public Message findMessageById(long id) {
        return messageDao.findMessageById(id);
    }

    public void close(){
        usersDao.close();
    }
}