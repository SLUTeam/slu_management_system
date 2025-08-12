
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Developed by Abishek Arivudainambi 

public class ProfessorDashboard extends JFrame {

    private JTabbedPane tabbedPane;
    private String username;
    private String email;
    private String role;

    public ProfessorDashboard(String username, String email, String role) {
    	
        this.username = username;
        this.email = email;
        this.role = role;

        setTitle("Professor Dashboard - SLU Management System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(33, 150, 243));
        leftPanel.setPreferredSize(new Dimension(200, 700));
        leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel title = new JLabel("PROFESSOR", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton homeBtn = new JButton("Home");
        JButton studentsBtn = new JButton("My Students");
        JButton assignmentsBtn = new JButton("Assignments");
        JButton logoutBtn = new JButton("Logout");

        Dimension buttonSize = new Dimension(150, 35);
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        JButton[] buttons = {homeBtn, studentsBtn, assignmentsBtn, logoutBtn};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
        }

        leftPanel.add(title);
        leftPanel.add(homeBtn);
        leftPanel.add(studentsBtn);
        leftPanel.add(assignmentsBtn);
        leftPanel.add(logoutBtn);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Home", createHomePanel());
        Integer professorId = getProfessorIdByUsername(username);
        if (professorId != null) {
            tabbedPane.addTab("My Students", ProfessorStudentsPanel.createProfessorStudentsPanel(professorId));
        } else {
            tabbedPane.addTab("My Students", new JPanel());
            JOptionPane.showMessageDialog(this, "Professor not found for email: " + email);
        }
        tabbedPane.addTab("Assignments", ProfessorAssignmentPanel.createProfessorAssignmentPanel(professorId));

        homeBtn.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        studentsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        assignmentsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        logoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "You have logged out!");
            System.exit(0);
        });

        add(leftPanel, BorderLayout.WEST);
        add(tabbedPane, BorderLayout.CENTER);
    }

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

        panel.add(welcomeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(emailLabel);
        panel.add(roleLabel);

        return panel;
    }
    
    private static Integer getProfessorIdByUsername(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT id FROM professors WHERE user_id = (SELECT id FROM users WHERE username = ? LIMIT 1) LIMIT 1")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfessorDashboard("professor", "professor@slu.edu", "Professor").setVisible(true));
    }
}

