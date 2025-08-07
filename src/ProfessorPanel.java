import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

//Developed by Pooja

public class ProfessorPanel {
    public static JPanel createProfessorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = { "ID", "Name", "Email", "Course ID" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField courseIdField = new JTextField();

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton accessBtn = new JButton("Give Access");
        accessBtn.setVisible(false);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Course ID:"));
        formPanel.add(courseIdField);
        formPanel.add(addBtn);
        formPanel.add(updateBtn);
        formPanel.add(deleteBtn);
        formPanel.add(accessBtn);

        // Add Button Logic
        addBtn.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO professors (name, email, course_id) VALUES (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nameField.getText());
                ps.setString(2, emailField.getText());
                ps.setInt(3, Integer.parseInt(courseIdField.getText()));
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    model.addRow(
                            new Object[] { id, nameField.getText(), emailField.getText(), courseIdField.getText() });
                }
                nameField.setText("");
                emailField.setText("");
                courseIdField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error adding professor.");
            }
        });
        
        // Update button Logic
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                try (Connection conn = DBConnection.getConnection();
                        PreparedStatement ps = conn
                                .prepareStatement("UPDATE professors SET name=?, email=?, course_id=? WHERE id=?")) {
                    ps.setString(1, nameField.getText());
                    ps.setString(2, emailField.getText());
                    ps.setInt(3, Integer.parseInt(courseIdField.getText()));
                    ps.setInt(4, id);
                    ps.executeUpdate();
                    model.setValueAt(nameField.getText(), row, 1);
                    model.setValueAt(emailField.getText(), row, 2);
                    model.setValueAt(courseIdField.getText(), row, 3);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error updating professor.");
                }
            }
        });
        
        // Delete Button logic
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                try (Connection conn = DBConnection.getConnection();
                        PreparedStatement ps = conn.prepareStatement("DELETE FROM professors WHERE id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    model.removeRow(row);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error deleting professor.");
                }
            }
        });
         // add access
        accessBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a professor first.");
                return;
            }

            String professorName = model.getValueAt(selectedRow, 1).toString();
            int professorId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            JPasswordField confirmPasswordField = new JPasswordField();

            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            inputPanel.add(new JLabel("Professor:"));
            inputPanel.add(new JLabel(professorName)); 
            inputPanel.add(new JLabel("Username:"));
            inputPanel.add(usernameField);
            inputPanel.add(new JLabel("New Password:"));
            inputPanel.add(passwordField);
            inputPanel.add(new JLabel("Confirm Password:"));
            inputPanel.add(confirmPasswordField);

            int result = JOptionPane.showConfirmDialog(
                panel,
                inputPanel,
                "Give Access to Professor Dashboard",
                JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

                // Basic validation
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Please fill in all fields.");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(panel, "Passwords does not match.");
                    return;
                }

                // Save to database
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
                    
                    ps.setString(1, username);
                    ps.setString(2, password); 
                    ps.setString(3, "Professor");
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(panel, "Access granted successfully.");
                    accessBtn.setVisible(false); 

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error saving user to Database.");
                }
            }
        });
      
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                nameField.setText(model.getValueAt(row, 1).toString());
                emailField.setText(model.getValueAt(row, 2).toString());
                courseIdField.setText(model.getValueAt(row, 3).toString());
                accessBtn.setVisible(true);
            }
        });

        // Load data
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM professors")) {
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("course_id")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error loading professor data.");
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }
}
