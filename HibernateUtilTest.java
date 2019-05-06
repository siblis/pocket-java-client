package ru.geekbrains.pocket.messenger.database;

import org.junit.Test;
import java.io.File;



public class HibernateUtilTest {

    private static final String WORK_DIR = System.getProperty("user.dir");
    private static final String DATABASE_DIR = "database";
    private static String username = "defaultUser";


    @Test
    public void makeDirectoryForDB() {
        File theDir = new File(DATABASE_DIR);
        if (!theDir.exists()) {
            System.out.println("Cоздаем директорию: " + theDir.getName());
            if (theDir.mkdir()) {
                System.out.println("Директория " + theDir.getAbsolutePath() + " создана");
            } else
                System.err.println("Ошибка при создании директории " + theDir.getName());
        }
        else{
            HibernateUtil.deleteDBFile();
            theDir.delete();
            theDir.mkdir();
            System.out.println("Директория " + theDir.getAbsolutePath() + " создана");
        }
        assert(theDir.exists());
        }

    @Test
    public void getSessionFactory() {
        HibernateUtil.getSessionFactory();
        File TestFile = new File(WORK_DIR + File.separator + DATABASE_DIR + File.separator + username + ".db");
        assert(TestFile.exists());

    }

    @Test
    public void deleteDBFile() {
        HibernateUtil.deleteDBFile();
        File TestFile = new File(WORK_DIR + File.separator + DATABASE_DIR + File.separator + username + ".db");
        assert(!TestFile.exists());
    }
}