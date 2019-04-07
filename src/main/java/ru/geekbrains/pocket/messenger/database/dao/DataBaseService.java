package ru.geekbrains.pocket.messenger.database.dao;

import ru.geekbrains.pocket.messenger.database.HibernateUtil;
import ru.geekbrains.pocket.messenger.database.entity.Message;
import ru.geekbrains.pocket.messenger.database.entity.User;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

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
    private UserProfileDAO usersProfileDao;
    private MessageDAO messageDao;

    public DataBaseService() {
        usersDao = new UserDAO();
        usersProfileDao = new UserProfileDAO();
        messageDao = new MessageDAO();
    }

    public void setUserDB(String username) {
        HibernateUtil.setUsername(username);
    }

    public void setUserDB(User user) {
        HibernateUtil.setUsername(user.getUserName());
    }

    public void insertUser(User user) {
//        UserProfile userProfile = new UserProfile(user.getProfile());
//        usersProfileDao.insert(userProfile);
//        userProfile = usersProfileDao.getByData(UserProfile.Fields.userName, 
//                userProfile.getUserName());
//        user.setProfile(userProfile);
        usersDao.insert(user);
    }

    public void deleteUser(User user) {
        usersDao.delete(user);
    }

    public void updateUser(User user) {
        usersDao.update(user);
    }

    public User getUserById(String id) {
        return usersDao.get(id);
    }

    public User getUserByEmail(String email) {
        //todo: часть email закрыта звёздочками, обдумать...
        return usersDao.getByData(User.UFields.email, email); 
    }

    public User getUserByName(String userName) {
        UserProfile prof = usersProfileDao.getByData(UserProfile.UPFields.userName, userName);
        return prof == null ? null : usersDao.getByData(User.UFields.id, prof.getId());
    }

    public List<User> getAllUsers() {
        return usersDao.get();
    }

    public List<String> getAllUserNames() {
        return usersProfileDao.getColumnOfData(UserProfile.UPFields.userName);
    }

    public List<String> getAllUserId() {
        return usersDao.getColumnOfData(User.UFields.id);
    }

    public void addMessage(String receiverId, String senderId, Message message) {
        messageDao.addSentMessage(senderId, message);
        messageDao.addReceivedMessage(receiverId, message);
        System.out.println("BD addMessage "+ receiverId+" "+senderId+" "+message);
    }


    public void addMessage(Message message) {
        //todo: проверить работоспособность
        if (message.getSender() == null || message.getReceiver() == null) return;
        messageDao.insert(message);
    }

    public List<Message> getChat(User user1, User user2){
        return messageDao.get(user1, user2);
    }

    public void deleteChat(User user1, User user2){
        messageDao.delete(user1, user2);
    }

    public Message findMessageById(String id) {
        return messageDao.findMessageById(id);
    }

    public void close(){
        usersDao = null;
        usersProfileDao = null;
        messageDao = null;
        HibernateUtil.shutdown();
    }
}