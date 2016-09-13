package sample;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by fenji on 9/8/2016.
 */
public class ToDoDatabaseTest {

    static ToDoDatabase todoDatabase;

    @Before
    public void setUp() throws Exception {
        if(todoDatabase == null) {
            todoDatabase = new ToDoDatabase();
            todoDatabase.init();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void init() throws Exception {

    }

    @Test
    public void testInit() throws Exception {
        // test to make sure we can access the new database
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        PreparedStatement todoQuery = conn.prepareStatement("SELECT * FROM todos");
        ResultSet results = todoQuery.executeQuery();
        assertNotNull(results);

    }

    @Test
    public void testInsertUser() throws Exception{
        int id = -1;
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String userEmail = "testEmail@fake.com";
        String firstName = "johny";
        String lastName = "test";
        todoDatabase.insertUser(conn, userEmail, firstName, lastName);

        // make sure we can retrieve the todo we just created
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users where email = ?");
        stmt.setString(1, userEmail);
        ResultSet results = stmt.executeQuery();
        assertNotNull(results);
        // count the records in results to make sure we get what we expected
        int numResults = 0;
        while (results.next()) {
            id = results.getInt("id");
            numResults++;
        }

        assertEquals(1, numResults);

        todoDatabase.deleteUser(conn, id);

        // make sure there are no more records for our test todo
        results = stmt.executeQuery();
        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);
    }

    @Test
    public void testInsertToDo() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String todoText = "UnitTest-ToDo";
        int id = -1;

        todoDatabase.insertUser(conn, "testemail@fake.com", "johnny", "test");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users where email = ?");
        stmt.setString(1, "testemail@fake.com");
        ResultSet results = stmt.executeQuery();

        assertNotNull(results);

        while (results.next()){
            id = results.getInt("id");
        }
        results = null;

        todoDatabase.insertToDo(conn, id, todoText);

        // make sure we can retrieve the todo we just created
        PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM todos where text = ?");
        stmt2.setString(1, todoText);
        results = stmt2.executeQuery();
        assertNotNull(results);
        // count the records in results to make sure we get what we expected
        int numResults = 0;
        while (results.next()) {
            numResults++;
        }

        assertEquals(1, numResults);

        todoDatabase.deleteToDo(conn, id, todoText);
        todoDatabase.deleteUser(conn, id);

        // make sure there are no more records for our test todo
        results = stmt.executeQuery();
        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);
    }

    @Test
    public void testSelectAllToDos() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String firstToDoText = "UnitTest-ToDo1";
        String secondToDoText = "UnitTest-ToDo2";
        int id = -1;

        todoDatabase.insertUser(conn, "testemail@fake.com", "johnny", "test");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users where email = ?");
        stmt.setString(1, "testemail@fake.com");
        ResultSet results = stmt.executeQuery();

        while (results.next()){
            id = results.getInt("id");
        }

        todoDatabase.insertToDo(conn, id, firstToDoText);
        todoDatabase.insertToDo(conn, id, secondToDoText);

        ArrayList<ToDoItem> todos = todoDatabase.selectToDos(conn, "testemail@fake.com");

        assertTrue("There should be at least 2 todos in the database (there are " +
                todos.size() + ")", todos.size() > 1);

        todoDatabase.deleteToDo(conn, id, firstToDoText);
        todoDatabase.deleteToDo(conn, id, secondToDoText);
        todoDatabase.deleteUser(conn, id);
    }

    @Test
    public void testToggleToDo() throws Exception {
        //(id IDENTITY, text VARCHAR, is_done BOOLEAN)
        int id = 0;
        int id2 = 0;
        int userId = 0;
        int userId2 = 0;
        boolean isDone = false;
        boolean isDone2 = false;
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String firstToDoText = "UnitTest-ToDo1";
        String duplicateToDoText = "UnitTest-ToDo1";


        todoDatabase.insertUser(conn, "testemail@fake.com", "johnny", "test");
        todoDatabase.insertUser(conn, "testemail@fake2.com", "johnny2", "test2");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        //stmt.setString(1, "testemail@fake.com");
        ResultSet results = stmt.executeQuery();

        int count = 0;
        while (results.next()){
            if (count == 0){
                userId = results.getInt("id");
            }else{
                userId2 = results.getInt("id");
            }
            count++;
        }

        todoDatabase.insertToDo(conn, userId, firstToDoText);
        todoDatabase.insertToDo(conn, userId2, duplicateToDoText);
        stmt = conn.prepareStatement("SELECT * FROM todos WHERE text = 'UnitTest-ToDo1'");
        results = stmt.executeQuery();
        count = 0;
        while (results.next()) {
            if (count == 0){
            id = results.getInt("id");
            todoDatabase.toggleToDo(conn, userId, id);
            } else {
                id2 = results.getInt("id");
            }
            count++;
        }
        results = stmt.executeQuery();
        count = 0;
        while (results.next()){
            if (count == 0){
                isDone = results.getBoolean("is_done");
            } else {
                isDone2 = results.getBoolean("is_done");
            }
            count++;
        }

        assertTrue(isDone);
        assertFalse(isDone2);
        todoDatabase.deleteToDo(conn, userId, firstToDoText);
        todoDatabase.deleteUser(conn, userId);
        todoDatabase.deleteToDo(conn, userId2, firstToDoText);
        todoDatabase.deleteUser(conn, userId2);
    }
}