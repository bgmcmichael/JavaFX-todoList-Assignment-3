package sample;

import javafx.collections.ObservableList;
import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;

public class ToDoDatabase {
    public final static String DB_URL = "jdbc:h2:./main";
    public static int userID = -1;
    ArrayList<ToDoItem> items = null;
    ObservableList<ToDoItem> todoItems = null;


    public void init() throws SQLException {
        // we'll add some implementation code here once we have a unit test method for it
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, text VARCHAR, is_done BOOLEAN, user_id INT);");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id Identity, email VARCHAR, first_name VARCHAR, last_name VARCHAR);");
    }

    public void insertUser(Connection conn, String userEmail, String firstName, String lastName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?, ?)");
        stmt.setString(1, userEmail);
        stmt.setString(2, firstName);
        stmt.setString(3, lastName);
        stmt.execute();
    }

    public void insertToDo(Connection conn, int userId, String text) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todos VALUES (NULL, ?, false, ?)");
        stmt.setString(1, text);
        stmt.setInt(2, userId);
        stmt.execute();
    }

    public void deleteUser(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users where id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public void deleteToDo(Connection conn, int userId, String text) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM todos where text = ? AND user_id = ?");
        stmt.setString(1, text);
        stmt.setInt(2, userId);
        stmt.execute();
    }

    public static void toggleToDo(Connection conn, int userId, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE todos SET is_done = NOT is_done WHERE id = ? AND user_id = ?");
        stmt.setInt(1, id);
        stmt.setInt(2, userId);
        stmt.execute();
    }

    public static User selectAUser(Connection conn, String email) throws SQLException{
        Statement stmt = conn.createStatement();
        ResultSet user = stmt.executeQuery("SELECT * FROM users WHERE email = '" + email + "'");

        if (user.next()){
            int id = user.getInt("id");
            String userEmail = user.getString("email");
            String firstName = user.getString("first_name");
            String lastName = user.getString("last_name");
            User currentUser = new User(userEmail, firstName, lastName, id);
            userID = id;

            return currentUser;
        }
        return null;
    }

    public static ArrayList<ToDoItem> selectToDos(Connection conn, String email) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet user = stmt.executeQuery("SELECT * FROM users WHERE email = '" + email + "'");
        while (user.next()) {
            userID = user.getInt("id");
        }
        ResultSet results = stmt.executeQuery("SELECT * FROM todos WHERE user_id = " + userID);
        while (results.next()) {
            int id = results.getInt("id");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            items.add(new ToDoItem(id, text, isDone, userID));
        }
        return items;
    }
}
