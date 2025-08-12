import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorAssignmentPanel {

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

	public static JPanel createProfessorAssignmentPanel(int professorId) {
		JPanel panel = new JPanel(new BorderLayout());

		
		String[] cols = { "ID", "Title", "Course", "Created At" };
		DefaultTableModel model = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		JTable table = new JTable(model);
		JScrollPane scroll = new JScrollPane(table);

	
		JTextField titleField = new JTextField();
		JTextArea descArea = new JTextArea(5, 40);
		descArea.setLineWrap(true);
		descArea.setWrapStyleWord(true);
		JComboBox<CourseItem> courseCombo = new JComboBox<>();

		JButton createBtn = new JButton("Create Assignment");
		JButton refreshBtn = new JButton("Refresh");

		
		JPanel form = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(6, 6, 6, 6);
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
		gc.gridy = 0;
		form.add(new JLabel("Assignment Title:"), gc);
		gc.gridx = 1;
		gc.weightx = 1;
		form.add(titleField, gc);

		gc.gridx = 0;
		gc.gridy = 1;
		gc.weightx = 0;
		form.add(new JLabel("Description:"), gc);
		gc.gridx = 1;
		gc.weightx = 1;
		form.add(new JScrollPane(descArea), gc);

		gc.gridx = 0;
		gc.gridy = 2;
		gc.weightx = 0;
		form.add(new JLabel("Course:"), gc);
		gc.gridx = 1;
		gc.weightx = 1;
		form.add(courseCombo, gc);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		actions.add(refreshBtn);
		actions.add(createBtn);
		gc.gridx = 0;
		gc.gridy = 3;
		gc.gridwidth = 2;
		gc.weightx = 1;
		form.add(actions, gc);

		Runnable loadCourses = () -> {
			courseCombo.removeAllItems();
			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement(
							"SELECT course_id, course_name FROM courses WHERE professor_id = ? ORDER BY course_name")) {
				ps.setInt(1, professorId);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						courseCombo.addItem(new CourseItem(rs.getInt(1), rs.getString(2)));
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Error loading courses.");
			}
		};

		Runnable loadAssignments = () -> {
			model.setRowCount(0);
			String sql = """
					    SELECT a.assignment_id, a.title, c.course_name, a.created_at
					      FROM assignments a
					      JOIN courses c ON c.course_id = a.course_id
					     WHERE a.professor_id = ?
					     ORDER BY a.created_at DESC, a.assignment_id DESC
					""";
			try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, professorId);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						model.addRow(new Object[] { rs.getInt("assignment_id"), rs.getString("title"),
								rs.getString("course_name"), rs.getTimestamp("created_at") });
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Error loading assignments.");
			}
		};

		loadCourses.run();
		loadAssignments.run();

		createBtn.addActionListener(e -> {
			String title = titleField.getText().trim();
			String desc = descArea.getText().trim();
			CourseItem sel = (CourseItem) courseCombo.getSelectedItem();

			if (title.isEmpty()) {
				JOptionPane.showMessageDialog(panel, "Title is required.");
				return;
			}
			if (sel == null) {
				JOptionPane.showMessageDialog(panel, "Select a course.");
				return;
			}

			int newId = -1;
			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement(
							"INSERT INTO assignments (professor_id, course_id, title, description) VALUES (?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS)) {
				ps.setInt(1, professorId);
				ps.setInt(2, sel.id);
				ps.setString(3, title);
				ps.setString(4, desc);
				ps.executeUpdate();
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						newId = rs.getInt(1);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Error creating assignment.");
				return;
			}

		
			List<String> emails = new ArrayList<>();
			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement(
							"SELECT s.email FROM registrations r JOIN students s ON s.id = r.student_id WHERE r.course_id = ?")) {
				ps.setInt(1, sel.id);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next())
						emails.add(rs.getString(1));
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Assignment created, but failed to fetch student emails.");
			}

			if (!emails.isEmpty()) {
				String subject = "New Assignment: " + title;
				String body = "Hello " + sel.name + " students,\n\n"
						+ "A new assignment has been posted for your course.\n\n" + "Title: " + title + "\n"
						+ (desc.isEmpty() ? "" : ("Description: " + desc + "\n"))
						+ "\nPlease check the SLU Management System for details.\n";
				try {
					EmailUtil.sendEmailBulk(emails, subject, body);
					JOptionPane.showMessageDialog(panel,
							"Assignment created (ID: " + newId + "). Emails sent to " + emails.size() + " students.");
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(panel,
							"Assignment created (ID: " + newId + "). Email send failed: " + ex.getMessage());
				}
			} else {
				JOptionPane.showMessageDialog(panel,
						"Assignment created (ID: " + newId + "). No registered students to notify.");
			}

			titleField.setText("");
			descArea.setText("");
			loadAssignments.run();
		});

		refreshBtn.addActionListener(e -> {
			loadCourses.run();
			loadAssignments.run();
		});

		panel.add(form, BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);
		return panel;
	}
}