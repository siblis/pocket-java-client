package database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    private static String userName = "defaultUser";

    public static void setUserName(String Name) {
        userName = Name;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration
                    .setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
                    .setProperty("hibernate.current_session_context_class", "thread")
                    .setProperty("hibernate.connection.url", "jdbc:sqlite:" + userName + ".db")
                    .setProperty("hibernate.show_sql", "true")
                    .setProperty("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect")
                    .setProperty("hibernate.hbm2ddl.auto", "update")
                    .addAnnotatedClass(database.entity.User.class)
                    .addAnnotatedClass(database.entity.Message.class);
            sessionFactory = configuration.buildSessionFactory();
        }

        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null)
            StandardServiceRegistryBuilder.destroy(registry);
    }
}
