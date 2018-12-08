package database;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.GenericJDBCException;

import java.io.File;
import java.nio.channels.SelectableChannel;
import java.sql.SQLException;
import java.util.Arrays;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static String userName = "defaultUser";
    private static final String WORK_DIR = System.getProperty("user.dir");
    //или WORK_DIR = Paths.get(".").toAbsolutePath().normalize().toString();
    private static final String DATABASE_DIR = "database";
    private static final long REQUIRED_DISK_SPACE = 16000;

    public static void setUserName(String Name) {
        userName = Name;
    }

    public static SessionFactory getSessionFactory() {
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
                //System.err.println("HibernateException: " + e.getMessage());
                Throwable t = e.getCause();
                System.err.println("HibernateException: " + t.toString());
                //System.err.println("HibernateException: " + t.getMessage());
                if (t instanceof org.hibernate.JDBCException)
                    System.err.println("JDBCException");
                if (t instanceof org.hibernate.exception.GenericJDBCException) {
                    GenericJDBCException ge = (GenericJDBCException) t;
                    System.err.println("GenericJDBCException: " + ge.getErrorCode());
                    System.err.println("GenericJDBCException: " + ge.getSQLException());
                    System.err.println("GenericJDBCException: " + ge.getSQLState());
                    System.err.println("GenericJDBCException: " + ge.getSQL());
                    if (ge.getSQLException() != null) {
                        String sqlEx = ge.getSQLException().getMessage();
                        if (sqlEx.contains(DATABASE_DIR + "' does not exist")) {
                            System.err.println("Отсутствует каталог");
                        }
                    }
                } else if (t instanceof org.hibernate.exception.JDBCConnectionException)
                    System.err.println("JDBCConnectionException");
                else if (t instanceof org.hibernate.SessionException)
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

    //проверка наличия файла базы данных для конкретного пользователя
    static private boolean isExistsDB() {
        String fileDB = WORK_DIR + File.separator + DATABASE_DIR + File.separator + userName + ".db";
        File file = new File(fileDB);
        if (file.exists()) {
            System.out.println("Файл БД для пользователя '" + userName + "' уже создан.");
            return true;
        } else {
            System.out.println("Файл БД '" + fileDB + "' не существует.");
            return false;
        }
    }

    //проверка свободного места для создания файла БД
    private static boolean isEnoughFreeSpaceForCreateDB() {
        File file = new File(WORK_DIR);

        long totalSpace = file.getTotalSpace();
        //получить свободное место раздела жесткого диска
        //long freeSpace = file.getFreeSpace();
        //или
        //количество свободно места, доступного для использования виртуальной машине Java (JVM)
        long usableSpace = file.getUsableSpace();
        if (usableSpace > REQUIRED_DISK_SPACE) {
            return true;
        } else {
            System.out.println("Ошибка: недостаточно свободного места для создания БД.");
            System.out.println("Имеется: " + totalSpace / 1024 / 1024 + "Мбайт. Нужно: " + usableSpace / 1024 / 1024 + "Мбайт.");
            return false;
        }
    }

    //проверка наличия файла базы данных для конкретного пользователя
    static private boolean isCanAccessRightsCreateDB() {
        //File file = new File(WORK_DIR + File.separator + DATABASE_DIR);
        File file = new File(WORK_DIR);
        if (file.canWrite()) {
            return true;
        } else {
            System.out.println("Ошибка: отсутствуют права на запись.");
            return false;
        }
    }

    static private boolean isExistsDirectoryForDB() {
        File theDir = new File("database");

        if (theDir.exists()) {
            return true;
        } else {
            System.out.println("Cоздаем директорию: " + theDir.getName());
            if (theDir.mkdir()) {
                System.out.println("Директория " + WORK_DIR + File.separator + theDir.getName() + " создана");
                return true;
            } else {
                System.out.println("Ошибка при создании директории: " + theDir.getName());
            }
        }
        return false;
    }
}
