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
import javax.swing.JPasswordField;
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
	    int row = table.getSelectedRow();
	    if (row < 0) {
	        JOptionPane.showMessageDialog(panel, "Select a student first.");
	        return;
	    }
	    int studentId = Integer.parseInt(model.getValueAt(row, 0).toString());

	    // Check if student already has a linked user
	    Integer existingUserId = null;
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM students WHERE id = ?")) {
	        ps.setInt(1, studentId);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                int uid = rs.getInt("user_id");
	                if (!rs.wasNull()) existingUserId = uid;
	            }
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(panel, "Error checking existing access.");
	        return;
	    }

	    if (existingUserId != null) {
	        int choice = JOptionPane.showConfirmDialog(
	                panel,
	                "This student already has access (user_id=" + existingUserId + ").\nDo you want to replace it?",
	                "Access Already Exists",
	                JOptionPane.YES_NO_OPTION
	        );
	        if (choice != JOptionPane.YES_OPTION) return;
	    }

	
	    JPanel accessPanel = new JPanel(new GridLayout(3, 2, 10, 10));
	    JTextField userField = new JTextField();
	    JPasswordField passField = new JPasswordField();
	    accessPanel.add(new JLabel("Username:"));
	    accessPanel.add(userField);
	    accessPanel.add(new JLabel("Password:"));
	    accessPanel.add(passField);

	    int result = JOptionPane.showConfirmDialog(
	            panel, accessPanel, "Create Login for Student",
	            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
	    );
	    if (result != JOptionPane.OK_OPTION) return;

	    String usernameIn = userField.getText().trim();
	    String passwordIn = new String(passField.getPassword());

	    if (usernameIn.isEmpty() || passwordIn.isEmpty()) {
	        JOptionPane.showMessageDialog(panel, "Username and password are required.");
	        return;
	    }

	    try (Connection conn = DBConnection.getConnection()) {
	        conn.setAutoCommit(false);
	        try {
	           
	            try (PreparedStatement chk = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
	                chk.setString(1, usernameIn);
	                try (ResultSet rs = chk.executeQuery()) {
	                    if (rs.next()) {
	                        throw new SQLException("Username already exists. Pick another.");
	                    }
	                }
	            }

	        
	            int newUserId;
	            try (PreparedStatement ins = conn.prepareStatement(
	                    "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
	                    Statement.RETURN_GENERATED_KEYS)) {
	                ins.setString(1, usernameIn);
	                ins.setString(2, passwordIn); 
	                ins.setString(3, "Student");
	                ins.executeUpdate();
	                try (ResultSet rs = ins.getGeneratedKeys()) {
	                    if (!rs.next()) throw new SQLException("Failed to create user (no key).");
	                    newUserId = rs.getInt(1);
	                }
	            }

	         
	            try (PreparedStatement upd = conn.prepareStatement(
	                    "UPDATE students SET user_id = ? WHERE id = ?")) {
	                upd.setInt(1, newUserId);
	                upd.setInt(2, studentId);
	                upd.executeUpdate();
	            }

	            conn.commit();
	            JOptionPane.showMessageDialog(panel, "Access created and linked (user_id=" + newUserId + ").");
	        } catch (SQLException ex) {
	            conn.rollback();
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(panel, "Error creating or linking user: " + ex.getMessage());
	        } finally {
	            conn.setAutoCommit(true);
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(panel, "Database error: " + ex.getMessage());
	    }
	});
	////////////
	
	formPanel.add(buttonPanel);
	panel.add(scrollPane, BorderLayout.CENTER);
	panel.add(formPanel, BorderLayout.SOUTH);
	
	return panel;
 }
}