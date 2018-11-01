package database.dao;

import database.HibernateUtil;
import database.entity.Message;
import database.entity.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;

/**
 * DAO (data access object) - один из наиболее распространенных паттернов
 * проектирования, "Доступ к данным". Его смысл прост - создать в
 * приложении слой, который отвечает только за доступ к данным, и больше
 * ни за что. Достать данные из БД, обновить данные, удалить данные - и все.
 * Однако, мы не будем создавать DAO напрямую и вызывать его методы в нашем
 * приложении. Вся логика будет помещена в класс DataBaseService.
 */
class UserDAO {

    void insert(User user) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.persist(user);

        session.getTransaction().commit();
    }

    void update(User user) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.saveOrUpdate(user);

        session.getTransaction().commit();
    }

    void delete(User user) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.delete(user);

        session.getTransaction().commit();
    }

    User get(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        User user = session.get(User.class, id);

        session.getTransaction().commit();

        return user;
    }

    List<User> get() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        List<User> list = (List<User>) session.createQuery("FROM User").list();

        session.getTransaction().commit();

        return list;
    }

    Message findMessageById(long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        Message message = session.get(Message.class, id);

        session.getTransaction().commit();
        return message;
    }

    void addSentMessage(long senderID, Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        User user = session.get(User.class, senderID);
        user.addSentMessage(message);
        session.saveOrUpdate(user);

        session.getTransaction().commit();
    }

    void addReceivedMessage(long receiverId, Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        User user = session.get(User.class, receiverId);
        user.addReceivedMessage(message);
        session.saveOrUpdate(user);

        session.getTransaction().commit();
    }

    void close(){
        HibernateUtil.shutdown();
    }
}
