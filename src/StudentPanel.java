import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;



public class StudentPanel {

	public  static JPanel createStudentPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		String[] columns = { "ID", "Name", "Email", "Department" };
		DefaultTableModel model = new DefaultTableModel(columns, 0);
		JTable table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);

		JTextField nameField = new JTextField();
		JTextField emailField = new JTextField();
		JTextField deptIdField = new JTextField();
		//JTextField accessField = new JTextField();
		
		JButton addBtn = new JButton("Add");
		JButton updateBtn = new JButton("Update");
		JButton deleteBtn = new JButton("Delete");
		JButton accessBtn = new JButton("Access");
		accessBtn.setEnabled(false);  // Initially disabled

		JPanel formPanel = new JPanel(new GridLayout(4, 4, 10, 10));
		formPanel.add(new JLabel("Name:"));
		formPanel.add(nameField);
		formPanel.add(new JLabel("Email:"));
		formPanel.add(emailField);
		formPanel.add(new JLabel("Dept ID:"));
		formPanel.add(deptIdField);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(addBtn);
		buttonPanel.add(updateBtn);
		buttonPanel.add(deleteBtn);
		buttonPanel.add(accessBtn);
		formPanel.add(buttonPanel);
		
		
		/*formPanel.add(addBtn);
		formPanel.add(updateBtn);
		formPanel.add(deleteBtn);
		formPanel.add(accessBtn);
	*/

		addBtn.addActionListener(e -> {
			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement(
							"INSERT INTO students (name, email, dept_id) VALUES (?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, nameField.getText());
				ps.setString(2, emailField.getText());
				ps.setInt(3, Integer.parseInt(deptIdField.getText()));
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					model.addRow(new Object[] { id, nameField.getText(), emailField.getText(), deptIdField.getText() });
				}
				nameField.setText("");
				emailField.setText("");
				deptIdField.setText("");
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Error adding student.");
			}
		});

		updateBtn.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row >= 0) {
				int id = Integer.parseInt(model.getValueAt(row, 0).toString());
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement ps = conn
								.prepareStatement("UPDATE students SET name=?, email=?, dept_id=? WHERE id=?")) {
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
					JOptionPane.showMessageDialog(panel, "Error updating student.");
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
					JOptionPane.showMessageDialog(panel, "Error deleting student.");
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
	             JOptionPane.showMessageDialog(panel, "Error loading student data");
	         }

		
	

	
	table.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
	        int row = table.getSelectedRow();
	        if (row >= 0) {
	            nameField.setText(model.getValueAt(row, 1).toString());
	            emailField.setText(model.getValueAt(row, 2).toString());
	            deptIdField.setText(model.getValueAt(row, 3).toString());

	            //Enable Access button when a row is selected
	            accessBtn.setEnabled(true);
	        }
	    }
	});
	////////////
	accessBtn.addActionListener(e -> {
        JPanel accessPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JTextField passField = new JTextField();

        JButton submitBtn = new JButton("Submit");
        JButton cancelBtn = new JButton("Cancel");

        accessPanel.add(userLabel);
        accessPanel.add(userField);
        accessPanel.add(passLabel);
        accessPanel.add(passField);
        accessPanel.add(submitBtn);
        accessPanel.add(cancelBtn);

        javax.swing.JDialog dialog = new javax.swing.JDialog();
        dialog.setTitle("Access Credentials");
        dialog.setModal(true);
        dialog.getContentPane().add(accessPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        submitBtn.addActionListener(submitEvent -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.equals("admin") && password.equals("1234")) {
                JOptionPane.showMessageDialog(dialog, "Login Successful!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Login Failed. Try Again.");
            }
        });

        cancelBtn.addActionListener(cancelEvent -> dialog.dispose());

        dialog.setVisible(true);
    });
	////////////
	
	formPanel.add(buttonPanel);
	panel.add(scrollPane, BorderLayout.CENTER);
	panel.add(formPanel, BorderLayout.SOUTH);
	
	return panel;
}
}