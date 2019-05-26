package ru.geekbrains.pocket.messenger.database;

import org.junit.Before;
import org.junit.Test;
import java.io.File;



public class HibernateUtilTest {

    private static final String WORK_DIR = System.getProperty("user.dir");
    private static final String DATABASE_DIR = "database";
    private static String username = "defaultUser";
    private File TestFile = new File(WORK_DIR + File.separator + DATABASE_DIR + File.separator + username + ".db");




    @Test
    public void getSessionFactoryTest() {
        if(TestFile.exists()){
            HibernateUtil.deleteDBFile();
        }
        assert(!TestFile.exists());
        HibernateUtil.getSessionFactory();
        assert(TestFile.exists());
    }


    @Test
    public void deleteDBFileTest() {
        if(!TestFile.exists()){
            HibernateUtil.getSessionFactory();
        }
        assert(TestFile.exists());
        HibernateUtil.deleteDBFile();
        assert(!TestFile.exists());
    }
}