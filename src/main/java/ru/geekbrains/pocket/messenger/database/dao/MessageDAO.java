package ru.geekbrains.pocket.messenger.database.dao;

import ru.geekbrains.pocket.messenger.database.HibernateUtil;
import ru.geekbrains.pocket.messenger.database.entity.Message;
import ru.geekbrains.pocket.messenger.database.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class MessageDAO {

    public void insert(Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.persist(message);

        session.getTransaction().commit();
    }

    public void update(Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.saveOrUpdate(message);

        session.getTransaction().commit();
    }

    public void delete(String id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        Message message = (Message) session.get(Message.class, id);
        session.delete(message);

        session.getTransaction().commit();
    }

    public List<Message> get() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        List<Message> list = (List<Message>) session.createQuery("FROM Message").list();

        session.getTransaction().commit();

        return list;
    }

    List<Message> get(User agent1id, User agent2id){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        Query<Message> query = session.createQuery("FROM Message m where " +
                "(m.sender = :id1Param and m.receiver = :id2Param) " +
                "or " +
                "(m.receiver = :id1Param and m.sender = :id2Param)");
        query.setParameter("id1Param", agent1id);
        query.setParameter("id2Param", agent2id);
        List<Message> messages = query.list();

        session.getTransaction().commit();

        return messages;
    }

    void delete(User agent1id, User agent2id){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        Query<Message> query = session.createQuery("DELETE Message m where " +
                "(m.sender = :id1Param and m.receiver = :id2Param) " +
                "or " +
                "(m.receiver = :id1Param and m.sender = :id2Param)");
        query.setParameter("id1Param", agent1id);
        query.setParameter("id2Param", agent2id);
        query.executeUpdate();

        session.getTransaction().commit();
    }

    Message findMessageById(String id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        Message message = session.get(Message.class, id);

        session.getTransaction().commit();
        return message;
    }

    void addSentMessage(String senderID, Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        User user = session.get(User.class, senderID);
        user.addSentMessage(message);
        session.saveOrUpdate(user);

        session.getTransaction().commit();
    }

    void addReceivedMessage(String receiverId, Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        User user = session.get(User.class, receiverId);
        user.addReceivedMessage(message);
        session.saveOrUpdate(user);

        session.getTransaction().commit();
    }
}
