package database.dao;

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

    public DataBaseService() {
        usersDao = new UserDAO();
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

    public User getUser(int id) {
        return usersDao.get(id);
    }

    public List<User> getAllUsers() {
        return usersDao.get();
    }

    public List<String> getAllUserNames() {
        List<String> names = new ArrayList<>();
        for (User user : usersDao.get()) {
            names.add(user.getName());
        }
        return names;
    }

    public List<Long> getAllUserId() {
        List<Long> ides = new ArrayList<>();
        for (User user : usersDao.get()) {
            ides.add(user.getId());
        }
        return ides;
    }

    public void addSentMessage(long userId, Message message) {
        usersDao.addSentMessage(userId, message);
    }

    public void addReceivedMessage(long userId, Message message) {
        usersDao.addReceivedMessage(userId, message);
    }

    public Message findMessageById(long id) {
        return usersDao.findMessageById(id);
    }

    public void close(){
        usersDao.close();
    }
}