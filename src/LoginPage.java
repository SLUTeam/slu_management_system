import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

//Developed by Takudzwa Mtata

public class LoginPage extends JFrame {

    private static final long serialVersionUID = 1L;
	private JTextField userField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeBox;
    private JButton submitButton;
    private JButton closeButton;

    public LoginPage() {
        setTitle("LOGIN PAGE");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left Panel for welcome page
        
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(33, 150, 243));
        leftPanel.setPreferredSize(new Dimension(250, 400));
        leftPanel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome User!");
        welcomeLabel.setForeground(Color.white);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setBounds(50, 100, 200, 30);

        JLabel developerLabel = new JLabel("Developed by");
        developerLabel.setBounds(50, 245, 150, 30);
        developerLabel.setForeground(Color.WHITE);

        JLabel groupLabel = new JLabel("SLU Team");
        groupLabel.setBounds(50, 265, 150, 30);
        groupLabel.setForeground(Color.WHITE);

        leftPanel.add(developerLabel);
        leftPanel.add(groupLabel);
        leftPanel.add(welcomeLabel);

        // Right Panel of login page
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);

        JLabel titleLabel = new JLabel("School Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(30, 20, 300, 30);

        JLabel loginLabel = new JLabel("Login Page");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loginLabel.setBounds(110, 60, 150, 25);

        JLabel userLabel = new JLabel("User name:");
        userLabel.setBounds(40, 100, 100, 25);
        userField = new JTextField();
        userField.setBounds(130, 100, 150, 25);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(40, 140, 100, 25);
        passwordField = new JPasswordField();
        passwordField.setBounds(130, 140, 150, 25);

        JLabel typeLabel = new JLabel("User Type:");
        typeLabel.setBounds(40, 180, 100, 25);
        String[] types = {"Admin", "Professor", "Student"};
        userTypeBox = new JComboBox<>(types);
        userTypeBox.setBounds(130, 180, 150, 25);

        submitButton = new JButton("SUBMIT");
        submitButton.setBounds(60, 230, 100, 30);
        closeButton = new JButton("CLOSE");
        closeButton.setBounds(180, 230, 100, 30);

        closeButton.addActionListener(e -> dispose());

        submitButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passwordField.getPassword());
            String type = (String) userTypeBox.getSelectedItem();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT * FROM users WHERE username=? AND password=? AND role=?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, user);
                ps.setString(2, pass);
                ps.setString(3, type);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String email = rs.getString("email");
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    String subject = "Login Notification";
                    String messgae = "Hello " + user + ",\n\nYou have successfully logged in to the SLU Management System.";
                    EmailUtil.sendEmail(email, user ,messgae,subject);

                    if (type.equals("Admin")) {
                        new AdminDashboard(user, email, type).setVisible(true);
                    } else if (type.equals("Professor")) {
                        new ProfessorDashboard(user, email, type).setVisible(true);
                    } else if (type.equals("Student")) {
                        new StudentDashboard(user, email, type).setVisible(true);
                    }

                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username, password, or role!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            }
        });

        rightPanel.add(titleLabel);
        rightPanel.add(loginLabel);
        rightPanel.add(userLabel);
        rightPanel.add(userField);
        rightPanel.add(passLabel);
        rightPanel.add(passwordField);
        rightPanel.add(typeLabel);
        rightPanel.add(userTypeBox);
        rightPanel.add(submitButton);
        rightPanel.add(closeButton);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
