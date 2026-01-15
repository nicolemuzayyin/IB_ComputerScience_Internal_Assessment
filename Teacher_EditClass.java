package package_IA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Teacher_EditClass extends JFrame {
	private static final long serialVersionUID = 1L;
	private Teacher currentTeacher;
	private JPanel contentPane;
	private JTable studentTable;
	private DefaultTableModel model;
	private JComboBox<Class> classSelector;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);

	public Teacher_EditClass(Teacher teacher) {
		this.currentTeacher = teacher;

		setTitle("CyberDuck - Edit Class");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 450);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblEditClass = new JLabel("Edit Classes");
		lblEditClass.setHorizontalAlignment(SwingConstants.CENTER);
		lblEditClass.setFont(new Font("Arial", Font.PLAIN, 24));
		lblEditClass.setForeground(lightPurple);
		lblEditClass.setBounds(0, 20, 600, 30);
		contentPane.add(lblEditClass);

		JLabel lblSelectClass = new JLabel("Select Class:");
		lblSelectClass.setFont(new Font("Arial", Font.PLAIN, 16));
		lblSelectClass.setForeground(lightPurple);
		lblSelectClass.setBounds(50, 60, 100, 30);
		contentPane.add(lblSelectClass);

		classSelector = new JComboBox<>();
		classSelector.setBounds(160, 60, 300, 30);
		classSelector.setBackground(Color.WHITE);
		classSelector.setForeground(lightPurple);
		classSelector.setFont(new Font("Arial", Font.PLAIN, 14));
		classSelector.setBorder(BorderFactory.createLineBorder(lightPurple));
		classSelector.setRenderer(new ClassRenderer());
		contentPane.add(classSelector);

		JButton addStudentButton = new JButton("Add Student to Class");
		addStudentButton.setBounds(200, 100, 200, 30);
		addStudentButton.setForeground(lightPurple);
		addStudentButton.setBackground(Color.WHITE);
		addStudentButton.setFocusPainted(false);
		addStudentButton.setBorderPainted(true);
		addStudentButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		contentPane.add(addStudentButton);

		String[] columnNames = { "Current Students", "Delete" };
		model = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public java.lang.Class<?> getColumnClass(int columnIndex) {
				return columnIndex == 1 ? Boolean.class : String.class;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};

		studentTable = new JTable(model);
		studentTable.setBackground(Color.WHITE);
		studentTable.setForeground(Color.BLACK);
		studentTable.setGridColor(lightPurple);
		studentTable.getTableHeader().setBackground(lightPurple);
		studentTable.getTableHeader().setForeground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(studentTable);
		scrollPane.setBounds(50, 140, 500, 180);
		contentPane.add(scrollPane);

		JButton saveButton = new JButton("Save Changes");
		saveButton.setBounds(200, 330, 200, 30);
		saveButton.setForeground(Color.WHITE);
		saveButton.setBackground(lightGreen);
		saveButton.setFocusPainted(false);
		saveButton.setBorderPainted(true);
		saveButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		saveButton.setOpaque(true);
		contentPane.add(saveButton);

		JButton backButton = new JButton("Back");
		backButton.setBounds(450, 380, 100, 30);
		backButton.setForeground(Color.WHITE);
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);
		contentPane.add(backButton);

		loadClasses();

		classSelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Class selectedClass = (Class) classSelector.getSelectedItem();
				if (selectedClass != null) {
					loadStudents(selectedClass.getClassName());
				}
			}
		});

		addStudentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Class selectedClass = (Class) classSelector.getSelectedItem();
				if (selectedClass != null) {
					dispose();
					new Teacher_AddStudentToClass(currentTeacher, selectedClass).setVisible(true);
				} else {
					JOptionPane.showMessageDialog(Teacher_EditClass.this, "Please select a class first.",
							"No Class Selected", JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveChanges();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Teacher_ClassInfo(currentTeacher).setVisible(true);
			}
		});

		setVisible(true);
	}

	private void loadClasses() {
		for (Class classObj : currentTeacher.getAllClasses()) {
			classSelector.addItem(classObj);
		}
	}

	private void loadStudents(String className) {
		model.setRowCount(0);

		Class chosenClass = null;
		for (Class c : currentTeacher.AllClasses) {
			if (c.getClassName().equalsIgnoreCase(className)) {
				chosenClass = c;
			}
		}
		if (chosenClass != null) {
			for (Student student : chosenClass.getStudentList()) {
				model.addRow(new Object[] { student.getFullName(), false });
			}
		}
	}

	private void saveChanges() throws SQLException {
		// Get the selected class from dropdown
		Class selectedClass = (Class) classSelector.getSelectedItem();
		if (selectedClass == null) {
			JOptionPane.showMessageDialog(this, "Please select a class first.", "No Class Selected",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Collect names of students marked for removal
		ArrayList<String> studentsToRemove = new ArrayList<>();
		for (int i = 0; i < model.getRowCount(); i++) {
			String studentName = (String) model.getValueAt(i, 0);
			Boolean isChecked = (Boolean) model.getValueAt(i, 1);
			if (isChecked) {
				studentsToRemove.add(studentName);
			}
		}

		// Try to save changes and update display
		try {
			currentTeacher.editClasses(selectedClass, studentsToRemove);
			JOptionPane.showMessageDialog(this, "Changes saved successfully!", "Success",
					JOptionPane.INFORMATION_MESSAGE);
			loadStudents(selectedClass.getClassName());
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// DefaultListCellRRenderer + ClassRenderer Source (See Crit C Source 6)
	class ClassRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof Class) {
				setText(((Class) value).getClassName());
			}
			return this;
		}
	}
}
