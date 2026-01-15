package package_IA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Teacher_AddStudentToClass extends JFrame {
	private static final long serialVersionUID = 1L;
	private Teacher currentTeacher;
	private Class currentClass;
	private JPanel contentPane;
	private JComboBox<Student> existingStudentComboBox;
	private JTextField newStudentNameField;
	private JComboBox<String> newStudentIBLevelComboBox;
	private JTextField newStudentEmailField;
	private JPasswordField newStudentPasswordField;
	private JTextField newStudentGradeLevelField;
	private JRadioButton existingStudentRadio;
	private JRadioButton newStudentRadio;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);

	public Teacher_AddStudentToClass(Teacher teacher, Class currentClass) {
		this.currentTeacher = teacher;
		this.currentClass = currentClass;

		setTitle("CyberDuck - Add Student to Class");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 550);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblAddStudent = new JLabel("Add Student");
		lblAddStudent.setHorizontalAlignment(SwingConstants.CENTER);
		lblAddStudent.setFont(new Font("Arial", Font.PLAIN, 24));
		lblAddStudent.setForeground(lightPurple);
		lblAddStudent.setBounds(0, 20, 450, 30);
		contentPane.add(lblAddStudent);

		ButtonGroup buttonGroup = new ButtonGroup();

		existingStudentRadio = new JRadioButton("Existing Student");
		existingStudentRadio.setBounds(50, 70, 150, 30);
		existingStudentRadio.setBackground(Color.WHITE);
		existingStudentRadio.setForeground(lightPurple);
		buttonGroup.add(existingStudentRadio);
		contentPane.add(existingStudentRadio);

		existingStudentComboBox = new JComboBox<>();
		existingStudentComboBox.setBounds(50, 110, 350, 30);
		existingStudentComboBox.setBackground(Color.WHITE);
		existingStudentComboBox.setForeground(lightPurple);
		existingStudentComboBox.setBorder(BorderFactory.createLineBorder(lightPurple));
		contentPane.add(existingStudentComboBox);

		newStudentRadio = new JRadioButton("New Student");
		newStudentRadio.setBounds(50, 150, 150, 30);
		newStudentRadio.setBackground(Color.WHITE);
		newStudentRadio.setForeground(lightPurple);
		buttonGroup.add(newStudentRadio);
		contentPane.add(newStudentRadio);

		newStudentNameField = new JTextField("Enter full name");
		newStudentNameField.setBounds(50, 190, 350, 30);
		newStudentNameField.setBorder(BorderFactory.createLineBorder(lightPurple));
		newStudentNameField.setForeground(lightPurple);
		contentPane.add(newStudentNameField);

		newStudentIBLevelComboBox = new JComboBox<>(new String[] { "SL", "HL" });
		newStudentIBLevelComboBox.setBounds(50, 230, 350, 30);
		newStudentIBLevelComboBox.setBackground(Color.WHITE);
		newStudentIBLevelComboBox.setForeground(Color.BLACK);
		newStudentIBLevelComboBox.setBorder(BorderFactory.createLineBorder(lightPurple));
		contentPane.add(newStudentIBLevelComboBox);

		newStudentEmailField = new JTextField("Enter email address");
		newStudentEmailField.setBounds(50, 270, 350, 30);
		newStudentEmailField.setBorder(BorderFactory.createLineBorder(lightPurple));
		newStudentEmailField.setForeground(lightPurple);
		contentPane.add(newStudentEmailField);

		newStudentPasswordField = new JPasswordField("Enter password");
		newStudentPasswordField.setBounds(50, 310, 350, 30);
		newStudentPasswordField.setBorder(BorderFactory.createLineBorder(lightPurple));
		newStudentPasswordField.setForeground(lightPurple);
		newStudentPasswordField.setEchoChar((char) 0);
		contentPane.add(newStudentPasswordField);

		newStudentGradeLevelField = new JTextField("Enter Grade Level (e.g., 11, 12)");
		newStudentGradeLevelField.setBounds(50, 350, 350, 30);
		newStudentGradeLevelField.setBorder(BorderFactory.createLineBorder(lightPurple));
		newStudentGradeLevelField.setForeground(lightPurple);
		contentPane.add(newStudentGradeLevelField);

		JButton addCreateButton = new JButton("Add/Create");
		addCreateButton.setBounds(180, 450, 110, 30);
		addCreateButton.setForeground(Color.WHITE);
		addCreateButton.setBackground(lightGreen);
		addCreateButton.setFocusPainted(false);
		addCreateButton.setBorderPainted(true);
		addCreateButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		addCreateButton.setOpaque(true);
		contentPane.add(addCreateButton);

		JButton backButton = new JButton("Back");
		backButton.setBounds(300, 450, 100, 30);
		backButton.setForeground(Color.WHITE);
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);
		contentPane.add(backButton);

		loadExistingStudents();

		existingStudentRadio.addActionListener(e -> updateFieldsState());
		newStudentRadio.addActionListener(e -> updateFieldsState());

		addCreateButton.addActionListener(e -> addStudent());

		backButton.addActionListener(e -> {
			dispose();
			new Teacher_EditClass(currentTeacher).setVisible(true);
		});

		updateFieldsState();
		setVisible(true);
	}

	private void loadExistingStudents() {
		// Clear existing items first
	    existingStudentComboBox.removeAllItems();
		// Custom renderer to format how students are displayed in the combo box
		// DefaultListCellRenderer Source (See Crit C Source 6)
		existingStudentComboBox.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Student) {
					Student student = (Student) value;
					// Format: "Full Name - IB Level - Grade Level"
					setText(String.format("%s - %s - Grade %d", student.getFullName(), student.getIBLevel(),
							student.getGradeLevel()));
					// Change colors based on selection state
					setForeground(isSelected ? Color.WHITE : lightPurple);
					setBackground(isSelected ? lightPurple : Color.WHITE);
				}
				return this;
			}
		});

		// Add only unassigned students (classID = 0) to the combo box
		for (Student s : Run.AllStudents) {
			if (s.getClassID() == 0) {
				existingStudentComboBox.addItem(s);
			}
		}
	}

	private void updateFieldsState() {
		boolean isExistingStudent = existingStudentRadio.isSelected();
		// Enable/disable fields based on whether adding existing or new student
		existingStudentComboBox.setEnabled(isExistingStudent);
		newStudentNameField.setEnabled(!isExistingStudent);
		newStudentIBLevelComboBox.setEnabled(!isExistingStudent);
		newStudentEmailField.setEnabled(!isExistingStudent);
		newStudentPasswordField.setEnabled(!isExistingStudent);
		newStudentGradeLevelField.setEnabled(!isExistingStudent);
	}

	private void addStudent() {
		if (existingStudentRadio.isSelected()) {
			addExistingStudent();
		} else if (newStudentRadio.isSelected()) {
			addNewStudent();
		} else {
			JOptionPane.showMessageDialog(this, "Please select either Existing Student or New Student.", "Input Error",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void addExistingStudent() {
		Student selectedStudent = (Student) existingStudentComboBox.getSelectedItem();
		if (selectedStudent == null) {
			JOptionPane.showMessageDialog(this, "Please select a student.", "Input Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Add student to class and show success message
		currentClass.addExistingStudentToClass(selectedStudent);
		JOptionPane.showMessageDialog(this, "Student added to class successfully!", "Success",
				JOptionPane.INFORMATION_MESSAGE);
		dispose();
		new Teacher_EditClass(currentTeacher).setVisible(true);
	}

	private void addNewStudent() {
		// Get all input field values
		String fullName = newStudentNameField.getText().trim();
		String ibLevel = (String) newStudentIBLevelComboBox.getSelectedItem();
		String gradeLevelString = newStudentGradeLevelField.getText().trim();
		String email = newStudentEmailField.getText().trim();
		String password = new String(newStudentPasswordField.getPassword());

		// Validate that all fields are filled
		if (fullName.equals("Enter full name") || email.equals("Enter email address")
				|| password.equals("Enter password") || gradeLevelString.equals("Enter Grade Level (e.g., 11, 12)")) {
			JOptionPane.showMessageDialog(this, "Please fill in all fields for the new student.", "Input Error",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Parse and validate grade level
		int gradeLevel;
		try {
			gradeLevel = Integer.parseInt(gradeLevelString);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Please enter a valid grade level (e.g., 11, 12).", "Input Error",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Attempt to add new student to class
		try {
			currentClass.addNewStudentToClass(fullName, email, password, gradeLevel, ibLevel);
			JOptionPane.showMessageDialog(this, "New student added to class successfully!", "Success",
					JOptionPane.INFORMATION_MESSAGE);
			dispose();
			new Teacher_EditClass(currentTeacher).setVisible(true);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error adding new student to class: " + e.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
