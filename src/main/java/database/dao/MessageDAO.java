package database.dao;

import database.HibernateUtil;
import database.entity.Message;
import org.hibernate.Session;

import java.util.List;

public class MessageDAO {

    public static void insert(Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.persist(message);

        session.getTransaction().commit();
    }

    public static void update(Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.saveOrUpdate(message);

        session.getTransaction().commit();
    }

    public static void delete(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        Message message = (Message) session.get(Message.class, id);
        session.delete(message);

        session.getTransaction().commit();
    }

    public static Message get(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        Message message = session.get(Message.class, id);

        session.getTransaction().commit();

        return message;
    }

    public static List<Message> get() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        List<Message> list = (List<Message>) session.createQuery("FROM Message").list();

        session.getTransaction().commit();

        return list;
    }
}
