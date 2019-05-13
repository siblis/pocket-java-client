package ru.geekbrains.pocket.messenger.database.dao;

import org.hibernate.Session;
import ru.geekbrains.pocket.messenger.database.HibernateUtil;
import ru.geekbrains.pocket.messenger.database.entity.Group;

public class GroupDAO {

    void insert(Group group) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();

        session.persist(group);

        session.getTransaction().commit();
    }
}
