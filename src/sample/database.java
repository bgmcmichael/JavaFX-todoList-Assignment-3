package sample;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by fenji on 9/8/2016.
 */
public class database {
    public static void main(String[] args) throws Exception {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, text VARCHAR, is_done BOOLEAN)");
    }
}
