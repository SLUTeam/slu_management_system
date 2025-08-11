import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.*;
// developed by Jyoti Priya
public class StudentCoursePanel {

    private static class CourseItem {
        final int id;
        final String name;
        final String profName;
        CourseItem(int id, String name, String profName) {
            this.id = id;
            this.name = name;
            this.profName = profName;
        }
        @Override
        public String toString() {
            return name;
        }
    }

    public static JPanel createStudentsCoursePanel(int studentId) {
        JPanel panel = new JPanel(new BorderLayout());

        
        String[] cols = {"reg_id(HIDDEN)", "S.No", "Course", "Professor"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int c) {
                return (c == 0 || c == 1) ? Integer.class : String.class;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        hideFirstColumn(table);

       
        JComboBox<CourseItem> courseCombo = new JComboBox<>();
        JTextField professorField = new JTextField();
        professorField.setEditable(false);

        JButton registerBtn = new JButton("Register");
        JButton deleteBtn = new JButton("Delete");

        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Course:"));
        form.add(courseCombo);
        form.add(new JLabel("Professor:"));
        form.add(professorField);
        form.add(registerBtn);
        form.add(deleteBtn);

        
        loadCourses(courseCombo, professorField);

        
        Runnable loadRegs = () -> loadRegistrations(model, studentId);

        
        courseCombo.addItemListener(ev -> {
            if (ev.getStateChange() == ItemEvent.SELECTED) {
                CourseItem ci = (CourseItem) courseCombo.getSelectedItem();
                professorField.setText(ci == null ? "" : ci.profName);
            }
        });

        
        registerBtn.addActionListener(e -> {
            CourseItem ci = (CourseItem) courseCombo.getSelectedItem();
            if (ci == null) {
                JOptionPane.showMessageDialog(panel, "Select a course.");
                return;
            }
            String dup = "SELECT 1 FROM registrations WHERE student_id=? AND course_id=?";
            String ins = "INSERT INTO registrations (student_id, course_id) VALUES (?, ?)";
            try (Connection conn = DBConnection.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement(dup)) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, ci.id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(panel, "Already registered.");
                            return;
                        }
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(ins)) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, ci.id);
                    ps.executeUpdate();
                }
                loadRegs.run();
                JOptionPane.showMessageDialog(panel, "Registered.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error registering.");
            }
        });

        
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Select a registration to delete.");
                return;
            }
            int regId = (int) model.getValueAt(row, 0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM registrations WHERE reg_id=?")) {
                ps.setInt(1, regId);
                ps.executeUpdate();
                loadRegs.run();
                JOptionPane.showMessageDialog(panel, "Deleted.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error deleting.");
            }
        });

        
        loadRegs.run();

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    private static void hideFirstColumn(JTable table) {
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    private static void loadCourses(JComboBox<CourseItem> courseCombo, JTextField professorField) {
        courseCombo.removeAllItems();
        String sql = "SELECT c.course_id, c.course_name, COALESCE(p.name,'') AS professor_name " +
                     "FROM courses c LEFT JOIN professors p ON p.id = c.professor_id " +
                     "ORDER BY c.course_name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                courseCombo.addItem(new CourseItem(
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getString("professor_name")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (courseCombo.getItemCount() > 0) {
            CourseItem ci = (CourseItem) courseCombo.getSelectedItem();
            professorField.setText(ci == null ? "" : ci.profName);
        }
    }

    private static void loadRegistrations(DefaultTableModel model, int studentId) {
        model.setRowCount(0);
        String sql = "SELECT r.reg_id, c.course_name, COALESCE(p.name,'') AS professor_name " +
                     "FROM registrations r " +
                     "JOIN courses c ON c.course_id = r.course_id " +
                     "LEFT JOIN professors p ON p.id = c.professor_id " +
                     "WHERE r.student_id = ? " +
                     "ORDER BY c.course_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                int sno = 1;
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("reg_id"),
                        sno++,
                        rs.getString("course_name"),
                        rs.getString("professor_name")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}