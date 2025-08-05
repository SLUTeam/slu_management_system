import java.sql.*;

public class DBConnection {
	
	// Currently this DB connection is based on the person who is working at the moment Soon we will buy one common DB server
    private static final String URL = "jdbc:mysql://localhost:3306/slu_management";
    private static final String USER = "root";
    private static final String PASSWORD = "Abishek@333";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}