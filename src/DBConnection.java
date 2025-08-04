import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://bgph5k76jy8ha8h56zpo-mysql.services.clever-cloud.com:3306/bgph5k76jy8ha8h56zpo";
    private static final String USER = "u8yvmphbwryhewq6";
    private static final String PASSWORD = "Rkx2R1USygRznoILpTqI";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}