package package_IA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class Teacher_EditStudent extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<Student> studentComboBox;
    private JComboBox<Class> classComboBox;
    private JTextField gradeLevelField;
    private JComboBox<String> ibLevelComboBox;
    private Teacher currentTeacher;
    static Color lightPurple = new Color(173, 153, 255);
    static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);

    public Teacher_EditStudent(Teacher teacher) {
        this.currentTeacher = teacher;

        setTitle("CyberDuck - Edit Student");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 400);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblEditStudent = new JLabel("Edit Student");
        lblEditStudent.setHorizontalAlignment(SwingConstants.CENTER);
        lblEditStudent.setFont(new Font("Arial", Font.PLAIN, 24));
        lblEditStudent.setForeground(lightPurple);
        lblEditStudent.setBounds(0, 20, 450, 30);
        contentPane.add(lblEditStudent);

        JLabel lblSelectStudent = new JLabel("Select Student:");
        lblSelectStudent.setBounds(50, 70, 150, 25);
        lblSelectStudent.setForeground(lightPurple);
        contentPane.add(lblSelectStudent);

        studentComboBox = new JComboBox<>();
        studentComboBox.setBounds(50, 100, 350, 30);
        studentComboBox.setBackground(Color.WHITE);
        studentComboBox.setForeground(Color.BLACK);
        contentPane.add(studentComboBox);

        JLabel lblClass = new JLabel("Class:");
        lblClass.setBounds(50, 140, 150, 25);
        lblClass.setForeground(lightPurple);
        contentPane.add(lblClass);

        classComboBox = new JComboBox<>();
        classComboBox.setBounds(50, 170, 350, 30);
        classComboBox.setBackground(Color.WHITE);
        classComboBox.setForeground(Color.BLACK);
        contentPane.add(classComboBox);

        JLabel lblGradeLevel = new JLabel("Grade Level:");
        lblGradeLevel.setBounds(50, 210, 150, 25);
        lblGradeLevel.setForeground(lightPurple);
        contentPane.add(lblGradeLevel);

        gradeLevelField = new JTextField();
        gradeLevelField.setBounds(50, 240, 350, 30);
        gradeLevelField.setForeground(Color.BLACK);
        contentPane.add(gradeLevelField);

        JLabel lblIBLevel = new JLabel("IB Level:");
        lblIBLevel.setBounds(50, 261, 150, 25);
        lblIBLevel.setForeground(lightPurple);
        contentPane.add(lblIBLevel);

        ibLevelComboBox = new JComboBox<>(new String[]{"SL", "HL"});
        ibLevelComboBox.setBounds(50, 286, 350, 30);
        ibLevelComboBox.setBackground(Color.WHITE);
        ibLevelComboBox.setForeground(Color.BLACK);
        contentPane.add(ibLevelComboBox);

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBounds(50, 325, 150, 30);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(lightGreen);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(true);
        saveButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
        saveButton.setOpaque(true);
        contentPane.add(saveButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(250, 325, 150, 30);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(lightRed);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(true);
        backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
        backButton.setOpaque(true);
        contentPane.add(backButton);

        loadStudents();
        loadClasses();

        studentComboBox.addActionListener(e -> updateFields());
        saveButton.addActionListener(e -> saveChanges());
        backButton.addActionListener(e -> {
            dispose();
            new Teacher_StudentInfo(currentTeacher).setVisible(true);
        });

        setVisible(true);
    }

    private void loadStudents() {
        for (Student student : Run.AllStudents) {
            studentComboBox.addItem(student);
        }
        studentComboBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    Student student = (Student) value;
                    setText(student.getFullName());
                }
                return this;
            }
        });
    }

    private void loadClasses() {
        for (Class c : currentTeacher.getAllClasses()) {
            classComboBox.addItem(c);
        }
        classComboBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Class) {
                    Class c = (Class) value;
                    setText(c.getClassName());
                }
                return this;
            }
        });
    }

    private void updateFields() {
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        if (selectedStudent != null) {
            for (int i = 0; i < classComboBox.getItemCount(); i++) {
                Class c = classComboBox.getItemAt(i);
                if (c.getClassID() == selectedStudent.getClassID()) {
                    classComboBox.setSelectedIndex(i);
                    break;
                }
            }
            gradeLevelField.setText(String.valueOf(selectedStudent.getGradeLevel()));
            ibLevelComboBox.setSelectedItem(selectedStudent.getIBLevel());
        }
    }

    private void saveChanges() {
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        Class selectedClass = (Class) classComboBox.getSelectedItem();
        String gradeLevel = gradeLevelField.getText();
        String ibLevel = (String) ibLevelComboBox.getSelectedItem();

        if (selectedStudent == null || selectedClass == null || gradeLevel.isEmpty() || ibLevel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int newGradeLevel = Integer.parseInt(gradeLevel);
            int newClassID = selectedClass.getClassID();

            boolean success = selectedStudent.updateStudentInfo(newClassID, newGradeLevel, ibLevel, currentTeacher);

            if (success) {
                JOptionPane.showMessageDialog(this, "Student information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new Teacher_StudentInfo(currentTeacher).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update student information.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid grade level.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
