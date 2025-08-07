import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DepartmentPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public DepartmentPanel() {
        setLayout(new BorderLayout());

        String[] columns = {"Dept ID", "Name"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JTextField deptNameField = new JTextField();

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.add(new JLabel("Dept Name:"));
        formPanel.add(deptNameField);
        formPanel.add(addBtn);
        formPanel.add(updateBtn);
        formPanel.add(deleteBtn);

        addBtn.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO departments (dept_name) VALUES (?)",
                         Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, deptNameField.getText());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int deptId = rs.getInt(1);
                    model.addRow(new Object[]{deptId, deptNameField.getText()});
                }
                deptNameField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding department.");
            }
        });
        
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int deptId = Integer.parseInt(model.getValueAt(row, 0).toString());
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("UPDATE departments SET dept_name=? WHERE dept_id=?")) {
                    ps.setString(1, deptNameField.getText());
                    ps.setInt(2, deptId);
                    ps.executeUpdate();
                    model.setValueAt(deptNameField.getText(), row, 1);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error updating department.");
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int deptId = Integer.parseInt(model.getValueAt(row, 0).toString());
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM departments WHERE dept_id=?")) {
                    ps.setInt(1, deptId);
                    ps.executeUpdate();
                    model.removeRow(row);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting department.");
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                deptNameField.setText(model.getValueAt(row, 1).toString());
            }
        });

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dept_id, dept_name FROM departments")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("dept_id"),
                        rs.getString("dept_name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading department data");
        }

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }
}