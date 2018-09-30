package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MainApp {
    public static void main(String[] args) {
        createTable();

//        User student = new User("Stormcoder", 44);
//        UserRepository.insert(student);
//
//        User student1 = UserRepository.get(3);
//        student1.setName("Sergey");
//        student1.setAge(27);
//        UserRepository.update(student1);
//
//        List<User> students = UserRepository.get();
//        System.out.println(Arrays.toString(students.toArray()));
    }

    private static void createTable() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:client.db");
             Statement st = conn.createStatement()){
            st.execute("CREATE TABLE IF NOT EXISTS USERS(" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "email TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS MESSAGES(" +
                    "id INTEGER PRIMARY KEY," +
                    "text TEXT NOT NULL," +
                    "sender INTEGER FOREIGN KEY," +
                    "receiver INTEGER FOREIGN KEY)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}