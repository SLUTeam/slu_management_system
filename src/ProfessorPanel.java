import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

//Developed by Pooja

public class ProfessorPanel {

	
	private static class CourseItem {
		final int id;
		final String name;

		CourseItem(int id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static JPanel createProfessorPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		String[] columns = { "ID", "Name", "Email", "Course" };
		DefaultTableModel model = new DefaultTableModel(columns, 0);
		JTable table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);

		JTextField nameField = new JTextField();
		JTextField emailField = new JTextField();
		JComboBox<CourseItem> courseCombo = new JComboBox<>();

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

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(addBtn);
		buttonPanel.add(updateBtn);
		buttonPanel.add(deleteBtn);
		buttonPanel.add(accessBtn);
		formPanel.add(buttonPanel);

//        formPanel.add(addBtn);
//        formPanel.add(updateBtn);
//        formPanel.add(deleteBtn);
//        formPanel.add(accessBtn);

	
		addBtn.addActionListener(e -> {
			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement("INSERT INTO professors (name, email) VALUES (?, ?)",
							Statement.RETURN_GENERATED_KEYS)) {

				ps.setString(1, nameField.getText());
				ps.setString(2, emailField.getText());

				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					model.addRow(new Object[] { id, nameField.getText(), emailField.getText(), "" });
				}
				nameField.setText("");
				emailField.setText("");

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
								.prepareStatement("UPDATE professors SET name=?, email=? WHERE id=?")) {
					ps.setString(1, nameField.getText());
					ps.setString(2, emailField.getText());

					ps.setInt(3, id);
					ps.executeUpdate();
					model.setValueAt(nameField.getText(), row, 1);
					model.setValueAt(emailField.getText(), row, 2);
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
				if (row < 0)
					return;
				nameField.setText(model.getValueAt(row, 1).toString());
				emailField.setText(model.getValueAt(row, 2).toString());
				accessBtn.setVisible(true);
			}
		});
		
		accessBtn.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row < 0) {
				JOptionPane.showMessageDialog(panel, "Select a professor first.");
				return;
			}

			int professorId = Integer.parseInt(model.getValueAt(row, 0).toString());
			String professorName = model.getValueAt(row, 1).toString();

			
			Integer existingUserId = null;
			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM professors WHERE id = ?")) {
				ps.setInt(1, professorId);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						int uid = rs.getInt("user_id");
						if (!rs.wasNull())
							existingUserId = uid;
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Error checking existing access.");
				return;
			}

			if (existingUserId != null) {
				int choice = JOptionPane
						.showConfirmDialog(panel,
								"This professor already has access (user_id=" + existingUserId
										+ ").\nDo you want to replace it?",
								"Access Already Exists", JOptionPane.YES_NO_OPTION);
				if (choice != JOptionPane.YES_OPTION)
					return;
			}

		
			JPanel accessPanel = new JPanel(new GridLayout(3, 2, 10, 10));
			accessPanel.add(new JLabel("Professor:"));
			accessPanel.add(new JLabel(professorName));
			JTextField userField = new JTextField();
			JPasswordField passField = new JPasswordField();
			accessPanel.add(new JLabel("Username:"));
			accessPanel.add(userField);
			accessPanel.add(new JLabel("Password:"));
			accessPanel.add(passField);

			int result = JOptionPane.showConfirmDialog(panel, accessPanel, "Create Login for Professor",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result != JOptionPane.OK_OPTION)
				return;

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
							if (rs.next())
								throw new SQLException("Username already exists. Pick another.");
						}
					}

					
					int newUserId;
					try (PreparedStatement ins = conn.prepareStatement(
							"INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS)) {
						ins.setString(1, usernameIn);
						ins.setString(2, passwordIn); 
						ins.setString(3, "Professor");
						ins.executeUpdate();
						try (ResultSet rs = ins.getGeneratedKeys()) {
							if (!rs.next())
								throw new SQLException("Failed to create user (no key).");
							newUserId = rs.getInt(1);
						}
					}

					
					try (PreparedStatement upd = conn
							.prepareStatement("UPDATE professors SET user_id = ? WHERE id = ?")) {
						upd.setInt(1, newUserId);
						upd.setInt(2, professorId);
						upd.executeUpdate();
					}

					conn.commit();
					JOptionPane.showMessageDialog(panel, "Access created and linked (user_id=" + newUserId + ").");
					accessBtn.setVisible(false);
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

	
		try (Connection conn = DBConnection.getConnection();
				Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery("SELECT course_id, course_name FROM courses ORDER BY course_name")) {
			while (rs.next()) {
				courseCombo.addItem(new CourseItem(rs.getInt("course_id"), rs.getString("course_name")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(panel, "Error loading courses.");
		}

	
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT p.id, p.name, p.email, c.course_name " + "FROM professors p "
						+ "LEFT JOIN courses c ON c.course_id = p.course_id " + "ORDER BY p.id")) {
			while (rs.next()) {
				model.addRow(new Object[] { rs.getInt("id"), rs.getString("name"), rs.getString("email"),
						rs.getString("course_name") });
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