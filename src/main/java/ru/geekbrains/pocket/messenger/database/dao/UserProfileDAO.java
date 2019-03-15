package ru.geekbrains.pocket.messenger.database.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.geekbrains.pocket.messenger.database.HibernateUtil;
import ru.geekbrains.pocket.messenger.database.entity.User;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

import java.util.List;

/**
 * DAO (data access object) - один из наиболее распространенных паттернов
 * проектирования, "Доступ к данным". Его смысл прост - создать в
 * приложении слой, который отвечает только за доступ к данным, и больше
 * ни за что. Достать данные из БД, обновить данные, удалить данные - и все.
 * Однако, мы не будем создавать DAO напрямую и вызывать его методы в нашем
 * приложении. Вся логика будет помещена в класс DataBaseService.
 */
class UserProfileDAO {

    void insert(UserProfile userProfile) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.persist(userProfile);

        session.getTransaction().commit();
    }

    void update(UserProfile userProfile) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.saveOrUpdate(userProfile);

        session.getTransaction().commit();
    }

    void delete(UserProfile userProfile) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.delete(userProfile);

        session.getTransaction().commit();
    }

    UserProfile get(long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        UserProfile userProfile = session.get(UserProfile.class, id);

        session.getTransaction().commit();

        return userProfile;
    }

    UserProfile getByUsername(String username) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        Query<UserProfile> query = session.createQuery("from UserProfile u where u.username = :userNameParam");
        query.setParameter("userNameParam", username);
        UserProfile userProfile = query.getSingleResult();

        session.getTransaction().commit();

        return userProfile;
    }

    List<UserProfile> get() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        List<UserProfile> list = (List<UserProfile>) session.createQuery("FROM UserProfile").list();

        session.getTransaction().commit();

        return list;
    }

    List<Long> getColumnOfData(String fieldName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        List<Long> list = (List<Long>) session.createQuery("select t." + fieldName + " FROM UserProfile as t").list();

        session.getTransaction().commit();

        return list;
    }

    void close(){
        HibernateUtil.shutdown();
    }
}
