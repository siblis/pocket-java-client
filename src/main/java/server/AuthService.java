package server;

import java.sql.*;

public class AuthService {
    private Connection connection;
    private Statement statement;

    public void connect() throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");

        connection = DriverManager.getConnection("jdbc:sqlite:JavaFX_chat.db");
        statement = connection.createStatement();
    }

    public String getNickByLoginAndPass(String login, String pass){
        try {
            ResultSet rs = statement.executeQuery("SELECT nick FROM users where login='"+ login +"' and password='"+ pass +"';");
            while (rs.next()){
                return rs.getString("nick");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
