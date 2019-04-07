package ru.geekbrains.pocket.messenger.database.dao;

import org.hibernate.Session;
import ru.geekbrains.pocket.messenger.database.HibernateUtil;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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

    UserProfile get(String id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        UserProfile userProfile = session.get(UserProfile.class, id);

        session.getTransaction().commit();

        return userProfile;
    }

    UserProfile getByData(UserProfile.UPFields fieldName, String searchData) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<UserProfile> criteria = builder.createQuery(UserProfile.class);
        Root<UserProfile> from = criteria.from(UserProfile.class);
        criteria.select(from);
        criteria.where(builder.equal(from.get(fieldName.toString()), searchData));
        TypedQuery<UserProfile> typed = session.createQuery(criteria);
        UserProfile profile;
        try {
            profile = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        session.getTransaction().commit();

        return profile;
    }

    List<UserProfile> get() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        List<UserProfile> list = (List<UserProfile>) session.createQuery("FROM "
                + UserProfile.class.getSimpleName() + "").list();

        session.getTransaction().commit();

        return list;
    }

    List<String> getColumnOfData(UserProfile.UPFields fieldName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        List<String> list = (List<String>) session.createQuery("select t." + fieldName
                + " FROM " + UserProfile.class.getSimpleName() + " as t").list();

        session.getTransaction().commit();

        return list;
    }
}
