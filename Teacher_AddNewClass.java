package package_IA;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Teacher_AddNewClass extends JFrame {

	private static final long serialVersionUID = 1L;
	static DefaultTableModel model;
	private JTextField classNameField;
	private JTable studentTable;
	private JButton addButton;
	private Teacher currentTeacher;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);

	public Teacher_AddNewClass(Teacher teacher) {
		this.currentTeacher = teacher;

		setTitle("CyberDuck - Manage Classes");
		setBounds(100, 100, 600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(10, 10));
		getContentPane().setBackground(Color.WHITE);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		JLabel titleLabel = new JLabel("Add New Class", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setForeground(lightPurple);
		topPanel.add(titleLabel, BorderLayout.CENTER);

		getContentPane().add(topPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
		centerPanel.setBackground(Color.WHITE);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		classNameField = new JTextField("Enter Class Name");
		classNameField.setForeground(lightPurple);
		classNameField.setFont(new Font("Arial", Font.PLAIN, 16));
		classNameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(lightPurple),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		centerPanel.add(classNameField, BorderLayout.NORTH);

		String[] columnNames = { "Student Name", "Grade Level", "Select" };
		model = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 2;
			}
		};

		studentTable = new JTable(model);
		studentTable.setBackground(Color.WHITE);
		studentTable.setForeground(Color.BLACK);
		studentTable.setGridColor(lightPurple);
		studentTable.setRowHeight(25);

		// TableColumn Source (See Crit C Source 26)
		// Configure the checkbox column (column index 2) in the student table
		TableColumn selectColumn = studentTable.getColumnModel().getColumn(2);

		// Set up custom renderer for the checkbox column to control its appearance
		selectColumn.setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			// Create a reusable checkbox component for rendering
			private final JCheckBox checkbox = new JCheckBox();

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				// Cast and set the checkbox state if the cell value is boolean
				if (value instanceof Boolean) {
					checkbox.setSelected((Boolean) value);
				}

				// Changes the checkbox's background color to match the table's selection color
				// when selected, or the table's default background color when not selected,
				// ensuring visual consistency
				checkbox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

				// Center-align the checkbox in the cell
				checkbox.setHorizontalAlignment(JLabel.CENTER);

				return checkbox;
			}
		});

		// Set up the editor component to handle user interactions with the checkbox -->
		// enables user to click the checkbox to toggle its state
		selectColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));

		// JScrollPane Source (See Crit C Source 12)
		JScrollPane scrollPane = new JScrollPane(studentTable);
		scrollPane.setBorder(BorderFactory.createLineBorder(lightPurple));

		centerPanel.add(scrollPane, BorderLayout.CENTER);

		getContentPane().add(centerPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		bottomPanel.setBackground(Color.WHITE);

		addButton = new JButton("Add");
		addButton.setForeground(Color.WHITE);
		addButton.setPreferredSize(new Dimension(120, 40));
		addButton.setBackground(lightGreen);
		addButton.setFocusPainted(false);
		addButton.setBorderPainted(true);
		addButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		addButton.setOpaque(true);
		bottomPanel.add(addButton);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Get and validate the class name from the input field
				String className = classNameField.getText().trim();
				if (className.isEmpty()) {
					JOptionPane.showMessageDialog(Teacher_AddNewClass.this, "Please enter a class name.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Create a list to store students that are selected in the table
				ArrayList<Student> selectedStudents = new ArrayList<Student>();
				// Iterate through each row in the table model
				for (int i = 0; i < model.getRowCount(); i++) {
					// Check if the checkbox in column 2 is selected
					Boolean isSelected = (Boolean) model.getValueAt(i, 2);
					if (isSelected) {
						// Get the student's full name from column 0
						String fullName = (String) model.getValueAt(i, 0);
						// Find the corresponding Student object from the global students list
						for (Student student : Run.AllStudents) {
							if (student.getFullName().equals(fullName)) {
								selectedStudents.add(student);
								break; // Exit inner loop once student is found
							}
						}
					}
				}

				try {
					// Add the new class to the database and update object relationships
					currentTeacher.addClassToTeacher(className, selectedStudents);

					// Show success message
					JOptionPane.showMessageDialog(Teacher_AddNewClass.this, "Class created successfully!", "Success",
							JOptionPane.INFORMATION_MESSAGE);

					// Close current window and open the class info view
					dispose();
					new Teacher_ClassInfo(currentTeacher).setVisible(true);

				} catch (SQLException ex) {
					// Handle database errors
					ex.printStackTrace();
					JOptionPane.showMessageDialog(Teacher_AddNewClass.this, "Error creating class: " + ex.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}

				// Refresh the student data display
				loadStudentData();
			}
		});

		JButton backButton = new JButton("Back");
		backButton.setForeground(Color.WHITE);
		backButton.setPreferredSize(new Dimension(120, 40));
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);
		bottomPanel.add(backButton);

		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Teacher_ClassInfo(currentTeacher).setVisible(true);
			}

		});

		getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		loadStudentData();

	}

	// Method to refresh Student Table data
	private void loadStudentData() {
		model.setRowCount(0);
		for (Student student : Run.AllStudents) {
			if (student.getClassID() == 0) {
				String studentName = student.getFullName();
				int gradeLevel = student.getGradeLevel();
				model.addRow(new Object[] { studentName, gradeLevel, Boolean.FALSE });
			}
		}
	}

}
