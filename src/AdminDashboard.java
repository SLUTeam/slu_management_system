import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

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

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Home", createHomePanel());
        tabbedPane.addTab("Students", createStudentPanel());
        tabbedPane.addTab("Professors", new JPanel());
        tabbedPane.addTab("Courses", new JPanel());
        tabbedPane.addTab("Departments", new DepartmentManagementPanel());

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

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Name", "Email", "Department"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField deptIdField = new JTextField();

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Dept ID:"));
        formPanel.add(deptIdField);
        formPanel.add(addBtn);
        formPanel.add(updateBtn);
        formPanel.add(deleteBtn);

        addBtn.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO students (name, email, dept_id) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nameField.getText());
                ps.setString(2, emailField.getText());
                ps.setInt(3, Integer.parseInt(deptIdField.getText()));
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    model.addRow(new Object[]{id, nameField.getText(), emailField.getText(), deptIdField.getText()});
                }
                nameField.setText(""); emailField.setText(""); deptIdField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding student.");
            }
        });

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("UPDATE students SET name=?, email=?, dept_id=? WHERE id=?")) {
                    ps.setString(1, nameField.getText());
                    ps.setString(2, emailField.getText());
                    ps.setInt(3, Integer.parseInt(deptIdField.getText()));
                    ps.setInt(4, id);
                    ps.executeUpdate();
                    model.setValueAt(nameField.getText(), row, 1);
                    model.setValueAt(emailField.getText(), row, 2);
                    model.setValueAt(deptIdField.getText(), row, 3);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error updating student.");
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    model.removeRow(row);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting student.");
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                nameField.setText(model.getValueAt(row, 1).toString());
                emailField.setText(model.getValueAt(row, 2).toString());
                deptIdField.setText(model.getValueAt(row, 3).toString());
            }
        });

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT s.id, s.name, s.email, d.dept_id FROM students s JOIN departments d ON s.dept_id = d.dept_id")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("dept_id")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student data");
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard("AdminUser", "admin@slu.edu", "Admin").setVisible(true));
    }
}
