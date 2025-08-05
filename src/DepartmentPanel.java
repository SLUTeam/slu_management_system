import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

//Developed by Takudzwa Mutata
   
   public class DepartmentPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public DepartmentPanel() {
		setLayout(new BorderLayout());	

		String[] columns = {"Dept ID", "Name"};
		DefaultTableModel model = new DefaultTableModel(columns, 0);
		JTable table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);

		JButton addBtn = new JButton("Add");
		JButton updateBtn = new JButton("Update");
		JButton deleteBtn = new JButton("Delete");

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		Dimension buttonSize = new Dimension(100, 30); 
		addBtn.setPreferredSize(buttonSize);
		updateBtn.setPreferredSize(buttonSize);
		deleteBtn.setPreferredSize(buttonSize);

		JTextField deptIdField = new JTextField();
		JTextField deptNameField = new JTextField();

		buttonPanel.add(addBtn);
		buttonPanel.add(updateBtn);
		buttonPanel.add(deleteBtn);

		JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
		formPanel.add(new JLabel("Dept ID:"));
		formPanel.add(deptIdField);
		formPanel.add(new JLabel("Dept Name:"));
		formPanel.add(deptNameField);
		formPanel.add(buttonPanel);
		formPanel.add(new JLabel()); // empty
		formPanel.add(buttonPanel);
		formPanel.add(new JLabel()); // empty

		// Add
		addBtn.addActionListener(e -> {
			try (Connection conn = DBConnection.getConnection();
				 PreparedStatement ps = conn.prepareStatement("INSERT INTO departments (dept_id, dept_name) VALUES (?, ?)")) {
				ps.setInt(1, Integer.parseInt(deptIdField.getText()));
				ps.setString(2, deptNameField.getText());
				ps.executeUpdate();
				model.addRow(new Object[]{deptIdField.getText(), deptNameField.getText()});
				deptIdField.setText(""); 
				deptNameField.setText("");
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error adding department.");
			}
		});

		// Update
		updateBtn.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row >= 0) {
		      try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement("UPDATE departments SET dept_name=? WHERE dept_id=?")) {
			    ps.setString(1, deptNameField.getText());
			    ps.setInt(2, Integer.parseInt(deptIdField.getText()));
				ps.executeUpdate();
				model.setValueAt(deptNameField.getText(), row, 1);
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error updating department.");
			}
		}
	});

	    // Delete
		deleteBtn.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row >= 0) {
			  try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement("DELETE FROM departments WHERE dept_id=?")) {
				ps.setInt(1, Integer.parseInt(model.getValueAt(row, 0).toString()));
				ps.executeUpdate();
				model.removeRow(row);
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error deleting department.");
			}
		}
	});

		// Table row click listener
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			 int row = table.getSelectedRow();
			 deptIdField.setText(model.getValueAt(row, 0).toString());
			 deptNameField.setText(model.getValueAt(row, 1).toString());
		}
	});

		// Load departments from DB
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

