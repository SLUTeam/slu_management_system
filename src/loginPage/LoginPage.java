package loginPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {

    // Components used in the login form
    private JTextField userField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeBox;
    private JButton submitButton;
    private JButton closeButton;

    public LoginPage() {
        // Set basic window properties
        setTitle("LOGIN PAGE");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create left panel (blue side)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(33, 150, 243)); // blue color
        leftPanel.setPreferredSize(new Dimension(250, 400));
        leftPanel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome User!");
        welcomeLabel.setForeground(Color.white);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setBounds(50, 100, 200, 30);
        
        JLabel developerLabel = new JLabel("Developed by");
        developerLabel.setBounds(50, 245, 150, 30);
        developerLabel.setForeground(Color.WHITE);
        developerLabel.setHorizontalAlignment(SwingConstants.CENTER); // CENTER IT

        JLabel groupLabel = new JLabel("Group 1");
        groupLabel.setBounds(50, 265, 150, 30);
        groupLabel.setForeground(Color.WHITE);
        groupLabel.setHorizontalAlignment(SwingConstants.CENTER); // CENTER IT

        leftPanel.add(developerLabel);
        leftPanel.add(groupLabel);

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/Avatar.jpg"));
        Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setBounds(85, 150, 80, 80);
        leftPanel.add(imageLabel);

        leftPanel.add(welcomeLabel);
       
        // Create right panel (white side)
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

        // Close the application when close button is clicked
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close window
            }
        });

        // Just print input data when submit button is clicked
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = userField.getText();
                String pass = new String(passwordField.getPassword());
                String type = (String) userTypeBox.getSelectedItem();

                JOptionPane.showMessageDialog(null,
                        "Username: " + user + "\nPassword: " + pass + "\nType: " + type);
            }
        });

        // Add components to right panel
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

        // Add panels to frame
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Center the window
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginPage(); // Run the login page
    }
}
