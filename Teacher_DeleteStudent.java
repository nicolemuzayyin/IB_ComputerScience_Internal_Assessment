package package_IA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class Teacher_DeleteStudent extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JList<CheckboxListItem> studentList;
	private DefaultListModel<CheckboxListItem> listModel;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);
	private Teacher teacher;

	public Teacher_DeleteStudent(Teacher currentTeacher) {
		this.teacher = currentTeacher;
		setTitle("CyberDuck - Delete Students");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 400);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDeleteStudents = new JLabel("Delete Students");
		lblDeleteStudents.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeleteStudents.setFont(new Font("Arial", Font.PLAIN, 24));
		lblDeleteStudents.setForeground(lightPurple);
		lblDeleteStudents.setBounds(100, 35, 400, 30);
		contentPane.add(lblDeleteStudents);

		JLabel lblInstructions = new JLabel("Select students to delete:");
		lblInstructions.setHorizontalAlignment(SwingConstants.CENTER);
		lblInstructions.setBounds(150, 60, 300, 25);
		lblInstructions.setForeground(lightPurple);

		listModel = new DefaultListModel<>();
		studentList = new JList<>(listModel);
		studentList.setCellRenderer(new CheckboxListRenderer());
		studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		studentList.setBackground(Color.WHITE);
		studentList.setForeground(lightPurple);

		// MouseListener Source (See Crit C Source 22)
		studentList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int index = studentList.locationToIndex(evt.getPoint());
				CheckboxListItem item = listModel.getElementAt(index);
				item.setSelected(!item.isSelected());
				studentList.repaint();
			}
		});

		JScrollPane scrollPane = new JScrollPane(studentList);
		scrollPane.setBounds(50, 90, 500, 200);
		contentPane.add(scrollPane);

		JButton deleteButton = new JButton("Delete Selected Students");
		deleteButton.setBounds(50, 300, 168, 30);
		deleteButton.setForeground(Color.WHITE);
		deleteButton.setBackground(lightGreen);
		deleteButton.setFocusPainted(false);
		deleteButton.setBorderPainted(true);
		deleteButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		deleteButton.setOpaque(true);
		contentPane.add(deleteButton);

		JButton backButton = new JButton("Back");
		backButton.setBounds(430, 300, 120, 30);
		backButton.setForeground(Color.WHITE);
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);
		contentPane.add(backButton);

		deleteButton.addActionListener(e -> {
			deleteSelectedStudents();
			dispose();
			new Teacher_StudentInfo(currentTeacher).setVisible(true);
		});

		backButton.addActionListener(e -> {
			dispose();
			new Teacher_StudentInfo(currentTeacher).setVisible(true);
		});

		loadStudents();
	}

	private void loadStudents() {
		// Add all students to the list model with checkboxes
		for (Student student : Run.AllStudents) {
			listModel.addElement(new CheckboxListItem(student));
		}
	}

	private void deleteSelectedStudents() {
		// Collect all selected students
		ArrayList<Student> selectedStudents = new ArrayList<>();
		for (int i = 0; i < listModel.size(); i++) {
			CheckboxListItem item = listModel.getElementAt(i);
			if (item.isSelected()) {
				selectedStudents.add(item.getStudent());
			}
		}

		// Show error if no students selected
		if (selectedStudents.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please select at least one student to delete.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Ask for confirmation before deletion
		int confirm = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete " + selectedStudents.size() + " student(s)?", "Confirm Deletion",
				JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			// Track students that fail to delete
			ArrayList<Student> failedDeletions = new ArrayList<>();

			// Try to delete each selected student
			for (Student student : selectedStudents) {
				if (!student.deleteStudent()) {
					failedDeletions.add(student);
				}
			}

			// Remove successfully deleted students from UI, memory, and their classes
			for (Student student : selectedStudents) {
				if (!failedDeletions.contains(student)) {
					listModel.removeElement(new CheckboxListItem(student));
					Run.AllStudents.remove(student);
					// Remove student from their class list
					for (Class cl : teacher.AllClasses) {
						if (student.getClassID() == cl.getClassID()) {
							cl.studentList.remove(student);
						}
					}
				}
			}

			// Show appropriate success or failure message
			if (failedDeletions.isEmpty()) {
				JOptionPane.showMessageDialog(this, "All selected students were deleted successfully.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this,
						"Failed to delete " + failedDeletions.size()
								+ " student(s). Please try again or contact support.",
						"Partial Failure", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	// CheckboxList Source (See Crit C Source 2)
	private class CheckboxListItem {
		private Student student;
		private boolean isSelected = false;

		public CheckboxListItem(Student student) {
			this.student = student;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public Student getStudent() {
			return student;
		}

		@Override
		public String toString() {
			return student.getFullName();
		}
	}

	private class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<CheckboxListItem> {
		private static final long serialVersionUID = -1645452926355596533L;


		@Override
		public Component getListCellRendererComponent(JList<? extends CheckboxListItem> list, CheckboxListItem value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setSelected(value.isSelected());
			setText(value.toString());
			setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
			setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
			return this;
		}
	}
}
