package ru.geekbrains.pocket.messenger.database;

import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.lang.reflect.Field;
import org.apache.logging.log4j.LogManager;

public class HibernateUtilTest {

    private static String WORK_DIR;
    private static String DATABASE_DIR;
    private static String username;
    private File testFile;

    @Before
    public void setFieldsOverReflection() {
        try {
            HibernateUtil hibUtil = new HibernateUtil();
            Field temp = hibUtil.getClass().getDeclaredField("WORK_DIR");
            temp.setAccessible(true);
            WORK_DIR = (String) temp.get(hibUtil);
            temp = hibUtil.getClass().getDeclaredField("DATABASE_DIR");
            temp.setAccessible(true);
            DATABASE_DIR = (String) temp.get(hibUtil);
            temp = hibUtil.getClass().getDeclaredField("username");
            temp.setAccessible(true);
            username = "t";
            temp.set(hibUtil, username);
            testFile = new File(WORK_DIR + File.separator + DATABASE_DIR + File.separator + username + ".db");
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
            LogManager.getLogger(getClass().getName()).error(ex);
        }
    }

    @Test
    public void getSessionFactoryTest() {
        if(testFile.exists()){
            HibernateUtil.deleteDBFile();
        }
        assert(!testFile.exists());
        HibernateUtil.getSessionFactory();
        assert(testFile.exists());
    }

    @Test
    public void deleteDBFileTest() {
        if(!testFile.exists()){
            HibernateUtil.getSessionFactory();
        }
        assert(testFile.exists());
        HibernateUtil.deleteDBFile();
        assert(!testFile.exists());
    }
}