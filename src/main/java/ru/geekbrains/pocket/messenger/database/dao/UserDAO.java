package ru.geekbrains.pocket.messenger.database.dao;

import ru.geekbrains.pocket.messenger.database.HibernateUtil;
import ru.geekbrains.pocket.messenger.database.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
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

    User get(long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        User user = session.get(User.class, id);

        session.getTransaction().commit();

        return user;
    }

    User getByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        User user = null;
        Query<User> query = session.createQuery("from User u where u.email = :email");
        query.setParameter("email", email);
        try {
            user = query.getSingleResult();
        } catch (NoResultException ex) {
            System.out.println(ex.getMessage());
        }

        session.getTransaction().commit();

        return user;
    }

    User getByUid(String uid) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        User user = null;
        Query<User> query = session.createQuery("from User u where u.uid = :uid");
        query.setParameter("uid", uid);
        try {
            user = query.getSingleResult();
        } catch (NoResultException ex) {
            System.out.println(ex.getMessage());
        }

        session.getTransaction().commit();

        return user;
    }

    User getByUsername(String userName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        User user = null;
        Query<User> query = session.createQuery("from User u where u.profile.username = :userNameParam");
        query.setParameter("userNameParam", userName);
        try {
            user = query.getSingleResult();
        } catch (NoResultException ex) {
            System.out.println(ex.getMessage());
        }

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

    List<Long> getColumnOfData(String fieldName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        List<Long> list = (List<Long>) session.createQuery("select t." + fieldName + " FROM User as t").list();

        session.getTransaction().commit();

        return list;
    }

    List<String> getColumnOfDataAsStringList(String fieldName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        List<String> list = (List<String>) session.createQuery("select t." + fieldName + " FROM User as t").list();

        session.getTransaction().commit();

        return list;
    }

    void close(){
        HibernateUtil.shutdown();
    }
}