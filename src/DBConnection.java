import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://root:ISmoyTEYiZCgjGVNkLShvJwOOERMYmVg@tramway.proxy.rlwy.net:43921/slu_management";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        return DriverManager.getConnection(URL);
    }
}