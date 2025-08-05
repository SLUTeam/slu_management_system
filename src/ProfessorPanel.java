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

        // Button Logic
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

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                nameField.setText(model.getValueAt(row, 1).toString());
                emailField.setText(model.getValueAt(row, 2).toString());
                courseIdField.setText(model.getValueAt(row, 3).toString());
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
