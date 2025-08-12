import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	  private static final String URL = "jdbc:mysql://tramway.proxy.rlwy.net:43921/slu_management";
	  private static final String USER = "root";
	  private static final String PASSWORD = "ISmoyTEYiZCgjGVNkLShvJwOOERMYmVg";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}