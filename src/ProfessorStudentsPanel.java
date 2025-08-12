import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProfessorStudentsPanel {

	public static JPanel createProfessorStudentsPanel(int professorId) {
		JPanel panel = new JPanel(new BorderLayout());
		String[] columns = { "S.No", "Student Name", "Course Name", "_email_hidden" };
		DefaultTableModel model = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		JTable table = new JTable(model);

		table.getColumnModel().getColumn(3).setMinWidth(0);
		table.getColumnModel().getColumn(3).setMaxWidth(0);
		table.getColumnModel().getColumn(3).setPreferredWidth(0);

		JScrollPane scrollPane = new JScrollPane(table);

		JButton refreshBtn = new JButton("Refresh");
		JButton messageBtn = new JButton("Message");
		messageBtn.setEnabled(false);

		JPanel bottom = new JPanel();
		bottom.add(refreshBtn);
		bottom.add(messageBtn);

		Runnable loader = () -> {
			model.setRowCount(0);
			int sno = 1;
			String sql = """
					SELECT s.name AS student_name,
					       s.email AS student_email,
					       c.course_name
					  FROM registrations r
					  JOIN students s ON s.id = r.student_id
					  JOIN courses  c ON c.course_id = r.course_id
					 WHERE c.professor_id = ?
					 ORDER BY s.name, c.course_name
					""";
			try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, professorId);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						model.addRow(new Object[] { sno++, rs.getString("student_name"), rs.getString("course_name"),
								rs.getString("student_email") });
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Error loading students.");
			}
		};

		loader.run();

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				messageBtn.setEnabled(table.getSelectedRow() >= 0);
			}
		});

		refreshBtn.addActionListener(e -> {
			messageBtn.setEnabled(false);
			loader.run();
		});

		messageBtn.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row < 0) {
				JOptionPane.showMessageDialog(panel, "Select a student first.");
				return;
			}
			String studentName = model.getValueAt(row, 1).toString();
			String courseName = model.getValueAt(row, 2).toString();
			String recipient = model.getValueAt(row, 3).toString(); // hidden email

			JPanel msgPanel = new JPanel(new GridLayout(3, 1, 8, 8));
			msgPanel.add(new JLabel("Send message to: " + studentName + " (" + courseName + ")"));
			JTextArea messageArea = new JTextArea(6, 40);
			messageArea.setLineWrap(true);
			messageArea.setWrapStyleWord(true);
			JScrollPane msgScroll = new JScrollPane(messageArea);
			msgPanel.add(msgScroll);

			int result = JOptionPane.showConfirmDialog(panel, msgPanel, "Compose Message", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (result != JOptionPane.OK_OPTION)
				return;

			String message = messageArea.getText().trim();
			if (message.isEmpty()) {
				JOptionPane.showMessageDialog(panel, "Message cannot be empty.");
				return;
			}

			try {
				String subject = "Message From professor";
				EmailUtil.sendEmail(recipient, studentName, message, subject);
				JOptionPane.showMessageDialog(panel, "Email sent to " + studentName + ".");
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, "Failed to send email: " + ex.getMessage());
			}
		});

		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.SOUTH);
		return panel;
	}
}