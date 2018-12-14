package database;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.GenericJDBCException;

import java.io.File;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static String userName = "defaultUser";
    private static final String WORK_DIR = System.getProperty("user.dir");
    //или WORK_DIR = Paths.get(".").toAbsolutePath().normalize().toString();
    private static final String DATABASE_DIR = "database";

    public static void setUserName(String Name) {
        userName = Name;
    }

    static private void makeDirectoryForDB() {
        File theDir = new File(DATABASE_DIR);

        if (!theDir.exists()) {
            System.out.println("Cоздаем директорию: " + theDir.getName());
            if (theDir.mkdir()) {
                System.out.println("Директория " + theDir.getAbsolutePath() + " создана");
            } else
                System.err.println("Ошибка при создании директории " + theDir.getName());
        }
    }

    public static SessionFactory getSessionFactory() {
        makeDirectoryForDB();

        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration
                        .setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
                        .setProperty("hibernate.current_session_context_class", "thread")
                        .setProperty("hibernate.connection.url", "jdbc:sqlite:" + DATABASE_DIR + File.separator + userName + ".db")
                        .setProperty("hibernate.show_sql", "true")
                        .setProperty("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect")
                        .setProperty("hibernate.hbm2ddl.auto", "update")
                        .addAnnotatedClass(database.entity.User.class)
                        .addAnnotatedClass(database.entity.Message.class);
                sessionFactory = configuration.buildSessionFactory();
            }
            //https://docs.jboss.org/hibernate/orm/3.2/api/org/hibernate/class-use/HibernateException.html
            catch (HibernateException e) {
                System.err.println("HibernateException: " + e.toString());
                Throwable t = e.getCause();
                System.err.println("HibernateException: " + t.toString());
                if (t instanceof org.hibernate.JDBCException) {
                    System.err.println("JDBCException");
                    if (t instanceof org.hibernate.exception.GenericJDBCException) {
                        GenericJDBCException ge = (GenericJDBCException) t;
                        if (ge.getSQLException() != null) {
                            String sqlEx = ge.getSQLException().getMessage();
                            System.err.println("ErrorCode: " + ge.getErrorCode());
                            System.err.println("SQLException: " + sqlEx);
                            System.err.println("SQLState: " + ge.getSQLState());
                            System.err.println("SQL: " + ge.getSQL());
                            if (sqlEx.contains(DATABASE_DIR + "' does not exist")) {
                                System.err.println("Отсутствует каталог " + WORK_DIR + File.separator + DATABASE_DIR);
                            } else if (sqlEx.contains("opening db:")) {
                                System.err.println("Отсутствует доступ к БД " + WORK_DIR + File.separator + DATABASE_DIR + userName + ".db");
                            }
                        }
                    } else if (t instanceof org.hibernate.exception.JDBCConnectionException)
                        System.err.println("JDBCConnectionException");
                } else if (t instanceof org.hibernate.SessionException)
                    System.err.println("SessionException");
                else if (t instanceof org.hibernate.service.spi.ServiceException)
                    System.err.println("ServiceException");

                t.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null)
            sessionFactory.close();
    }
}
