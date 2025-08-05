import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

//Developed by Abishek Arivudainambi


public class AdminDashboard extends JFrame {

    private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
    private String username;
    private String email;
    private String role;

    public AdminDashboard(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;

        setTitle("Admin Dashboard - SLU Management System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        //we created this left panel to maintain the tabs 

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(33, 150, 243));
        leftPanel.setPreferredSize(new Dimension(200, 700));
        leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel title = new JLabel("ADMIN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton homeBtn = new JButton("Home");
        JButton studentBtn = new JButton("Students");
        JButton professorBtn = new JButton("Professors");
        JButton courseBtn = new JButton("Courses");
        JButton deptBtn = new JButton("Departments");
        JButton logoutBtn = new JButton("Logout");

        Dimension buttonSize = new Dimension(150, 35);
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        JButton[] buttons = {homeBtn, studentBtn, professorBtn, courseBtn, deptBtn, logoutBtn};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
        }

        leftPanel.add(title);
        leftPanel.add(homeBtn);
        leftPanel.add(studentBtn);
        leftPanel.add(professorBtn);
        leftPanel.add(courseBtn);
        leftPanel.add(deptBtn);
        leftPanel.add(logoutBtn);
        
        //this JTabbedPane is new to me I found this in Youtube and I used.

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Home", createHomePanel());
        tabbedPane.addTab("Students", StudentPanel.createStudentPanel());
        tabbedPane.addTab("Professors", ProfessorPanel.createProfessorPanel());
        tabbedPane.addTab("Courses", CoursePanel.createCoursePanel());
        tabbedPane.addTab("Departments", new DepartmentPanel());

        homeBtn.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        studentBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        professorBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        courseBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        deptBtn.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        logoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "You have logged out!");
            System.exit(0);
        });

        add(leftPanel, BorderLayout.WEST);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    //We Created separate separate panels for all tabs 

    private JPanel createHomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel emailLabel = new JLabel("Email: " + email);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel roleLabel = new JLabel("Role: " + role);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton manageStudentsBtn = new JButton("Manage Students");
        JButton manageProfessorsBtn = new JButton("Manage Professors");
        JButton manageCoursesBtn = new JButton("Manage Courses");
        JButton manageDeptBtn = new JButton("Manage Departments");

        Dimension btnSize = new Dimension(200, 40);
        for (JButton btn : new JButton[]{manageStudentsBtn, manageProfessorsBtn, manageCoursesBtn, manageDeptBtn}) {
            btn.setPreferredSize(btnSize);
        }
        
        //This panel contain logged in user details and all tabs managing buttons
        
       // setSelectedIndex used to manage to know which tab is active at the time

        manageStudentsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        manageProfessorsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        manageCoursesBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        manageDeptBtn.addActionListener(e -> tabbedPane.setSelectedIndex(4));

        buttonPanel.add(manageStudentsBtn);
        buttonPanel.add(manageProfessorsBtn);
        buttonPanel.add(manageCoursesBtn);
        buttonPanel.add(manageDeptBtn);

        panel.add(welcomeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(emailLabel);
        panel.add(roleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(buttonPanel);

        return panel;
    }

    
    //Main method to start

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard("AdminUser", "admin@slu.edu", "Admin").setVisible(true));
    }
}
