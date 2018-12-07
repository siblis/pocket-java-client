package database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static String userName = "defaultUser";

    public static void setUserName(String Name) {
        userName = Name;
    }

    static private void makeDirectoryForDB() {
        File theDir = new File("database");

        if (!theDir.exists()) {
            System.out.println("Cоздаем директории: " + theDir.getName());
            if (theDir.mkdir()) {
                System.out.println("Директория " + theDir.getName() + " создана");
            } else
                System.out.println("Ошибка при создании директории: " + theDir.getName());
        }
    }

    public static SessionFactory getSessionFactory() {
        makeDirectoryForDB();

        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration
                    .setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
                    .setProperty("hibernate.current_session_context_class", "thread")
                    .setProperty("hibernate.connection.url", "jdbc:sqlite:DataBase\\" + userName + ".db")
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
        if (sessionFactory != null)
            sessionFactory.close();
    }
}
