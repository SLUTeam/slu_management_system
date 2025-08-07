import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CoursePanel {

	private static class DeptItem {
		final int id;
		final String name;

		DeptItem(int id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static class ProfItem {
		final int id;
		final String name;

		ProfItem(int id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static JPanel createCoursePanel() {
		JPanel panel = new JPanel(new BorderLayout());

		String[] columns = { "ID", "Course Name", "Professor", "Department" };
		DefaultTableModel model = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		JTable table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);

		JTextField courseName = new JTextField();
		JComboBox<ProfItem> profCombo = new JComboBox<>();
		JComboBox<DeptItem> deptCombo = new JComboBox<>();

		// Load professors
		try (Connection conn = DBConnection.getConnection();
				Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery("SELECT id, name FROM professors ORDER BY name")) {
			while (rs.next()) {
				profCombo.addItem(new ProfItem(rs.getInt("id"), rs.getString("name")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(panel, "Error loading professors.");
		}

		// Load departments
		try (Connection conn = DBConnection.getConnection();
				Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery("SELECT dept_id, dept_name FROM departments ORDER BY dept_name")) {
			while (rs.next()) {
				deptCombo.addItem(new DeptItem(rs.getInt("dept_id"), rs.getString("dept_name")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(panel, "Error loading departments.");
		}

		JButton addBtn = new JButton("Add");
		JButton updateBtn = new JButton("Update");
		JButton deleteBtn = new JButton("Delete");

		JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
		form.add(new JLabel("Course Name:"));
		form.add(courseName);
		form.add(new JLabel("Professor:"));
		form.add(profCombo);
		form.add(new JLabel("Department:"));
		form.add(deptCombo);

		JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 10));
		buttons.add(addBtn);
		buttons.add(updateBtn);
		buttons.add(deleteBtn);

		// ADD
		addBtn.addActionListener(e -> {
			ProfItem p = (ProfItem) profCombo.getSelectedItem();
			DeptItem d = (DeptItem) deptCombo.getSelectedItem();
			if (p == null || d == null) {
				JOptionPane.showMessageDialog(panel, "Select professor and department.");
				return;
			}

			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement(
							"INSERT INTO courses (course_name, professor_id, dept_id) VALUES (?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, courseName.getText().trim());
				ps.setInt(2, p.id);
				ps.setInt(3, d.id);
				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						int id = rs.getInt(1);
						model.addRow(new Object[] { id, courseName.getText().trim(), p.name, d.name });
					}
				}
				courseName.setText("");
				if (profCombo.getItemCount() > 0)
					profCombo.setSelectedIndex(0);
				if (deptCombo.getItemCount() > 0)
					deptCombo.setSelectedIndex(0);
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Error adding course.");
			}
		});

		// UPDATE
		updateBtn.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row < 0) {
				JOptionPane.showMessageDialog(panel, "Select a row to update.");
				return;
			}
			ProfItem p = (ProfItem) profCombo.getSelectedItem();
			DeptItem d = (DeptItem) deptCombo.getSelectedItem();
			if (p == null || d == null) {
				JOptionPane.showMessageDialog(panel, "Select professor and department.");
				return;
			}

			int courseId = (int) model.getValueAt(row, 0);
			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement(
							"UPDATE courses SET course_name=?, professor_id=?, dept_id=? WHERE course_id=?")) {
				ps.setString(1, courseName.getText().trim());
				ps.setInt(2, p.id);
				ps.setInt(3, d.id);
				ps.setInt(4, courseId);
				ps.executeUpdate();

				model.setValueAt(courseName.getText().trim(), row, 1);
				model.setValueAt(p.name, row, 2);
				model.setValueAt(d.name, row, 3);
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Error updating course.");
			}
		});

		// DELETE
		deleteBtn.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row < 0) {
				JOptionPane.showMessageDialog(panel, "Select a row to delete.");
				return;
			}
			int courseId = (int) model.getValueAt(row, 0);
			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement("DELETE FROM courses WHERE course_id=?")) {
				ps.setInt(1, courseId);
				ps.executeUpdate();
				model.removeRow(row);
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Error deleting course.");
			}
		});

		// TABLE -> FORM (simple: requery IDs for this row)
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row < 0)
					return;
				courseName.setText(String.valueOf(model.getValueAt(row, 1)));
				int courseId = (int) model.getValueAt(row, 0);

				try (Connection conn = DBConnection.getConnection();
						PreparedStatement ps = conn
								.prepareStatement("SELECT professor_id, dept_id FROM courses WHERE course_id=?")) {
					ps.setInt(1, courseId);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							int pid = rs.getInt("professor_id");
							int did = rs.getInt("dept_id");
							selectComboById(profCombo, pid);
							selectComboById(deptCombo, did);
						}
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		// INITIAL LOAD (names via JOIN)
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT c.course_id, c.course_name, p.name AS professor_name, d.dept_name AS department_name "
								+ "FROM courses c " + "JOIN professors p ON p.id = c.professor_id "
								+ "JOIN departments d ON d.dept_id = c.dept_id " + "ORDER BY c.course_id")) {
			while (rs.next()) {
				model.addRow(new Object[] { rs.getInt("course_id"), rs.getString("course_name"),
						rs.getString("professor_name"), rs.getString("department_name") });
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(panel, "Error loading courses.");
		}

		JPanel south = new JPanel(new BorderLayout());
		south.add(form, BorderLayout.CENTER);
		south.add(buttons, BorderLayout.SOUTH);

		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(south, BorderLayout.SOUTH);
		return panel;
	}

	private static void selectComboById(JComboBox combo, int id) {
		for (int i = 0; i < combo.getItemCount(); i++) {
			Object item = combo.getItemAt(i);

			if (item instanceof ProfItem) {
				ProfItem p = (ProfItem) item;
				if (p.id == id) {
					combo.setSelectedIndex(i);
					return;
				}
			}

			if (item instanceof DeptItem) {
				DeptItem d = (DeptItem) item;
				if (d.id == id) {
					combo.setSelectedIndex(i);
					return;
				}
			}
		}
	}
}