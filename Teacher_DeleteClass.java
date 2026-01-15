package package_IA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Teacher_DeleteClass extends JFrame {
	private static final long serialVersionUID = 1L;
	private Teacher currentTeacher;
	private JPanel contentPane;
	private JTable classTable;
	private DefaultTableModel model;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);

	public Teacher_DeleteClass(Teacher teacher) {
		this.currentTeacher = teacher;

		setTitle("CyberDuck - Delete Class");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDeleteClass = new JLabel("Delete Class");
		lblDeleteClass.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeleteClass.setFont(new Font("Arial", Font.PLAIN, 24));
		lblDeleteClass.setForeground(lightPurple);
		lblDeleteClass.setBounds(0, 20, 600, 30);
		contentPane.add(lblDeleteClass);

		String[] columnNames = { "Class Name", "Number of Students", "Delete?" };
		model = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 2;
			}

		};

		classTable = new JTable(model);
		classTable.setBackground(Color.WHITE);
		classTable.setForeground(lightPurple);
		classTable.setGridColor(lightPurple);
		classTable.getTableHeader().setBackground(lightPurple);
		classTable.getTableHeader().setForeground(Color.WHITE);

		TableColumn deleteColumn = classTable.getColumnModel().getColumn(2);
		deleteColumn.setCellRenderer(classTable.getDefaultRenderer(Boolean.class));
		deleteColumn.setCellEditor(classTable.getDefaultEditor(Boolean.class));

		JScrollPane scrollPane = new JScrollPane(classTable);
		scrollPane.setBounds(50, 60, 500, 250);
		contentPane.add(scrollPane);

		JButton backButton = new JButton("Back");
		backButton.setBounds(450, 322, 100, 30);
		backButton.setForeground(Color.WHITE);
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);

		JButton saveButton = new JButton("Save");
		saveButton.setBounds(50, 322, 100, 30);
		saveButton.setForeground(Color.WHITE);
		saveButton.setBackground(lightGreen);
		saveButton.setFocusPainted(false);
		saveButton.setBorderPainted(true);
		saveButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		saveButton.setOpaque(true);

		contentPane.add(backButton);
		contentPane.add(saveButton);

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Teacher_ClassInfo(currentTeacher).setVisible(true);
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					deleteSelectedClasses();
				} catch (SQLException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(Teacher_DeleteClass.this,
							"Error deleting classes: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		loadClassData();
		setVisible(true);
	}

	private void deleteSelectedClasses() throws SQLException {
		// Create a list to store names of classes to be deleted
		ArrayList<String> classNamesToDelete = new ArrayList<>();

		// Loop through table rows backwards to check which classes are selected
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			Boolean isChecked = (Boolean) model.getValueAt(i, 2);
			if (isChecked) {
				String className = (String) model.getValueAt(i, 0);
				classNamesToDelete.add(className);
			}
		}

		// If any classes are selected for deletion
		if (!classNamesToDelete.isEmpty()) {
			try {
				// Delete the selected classes from database
				currentTeacher.deleteClasses(classNamesToDelete);
				// Show success message
				JOptionPane.showMessageDialog(this, "Selected classes have been deleted.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (SQLException ex) {
				// Handle database errors
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error deleting classes: " + ex.getMessage(), "Database Error",
						JOptionPane.ERROR_MESSAGE);
			}
			// Refresh the table data
			loadClassData();
		} else {
			// Show message if no classes were selected
			JOptionPane.showMessageDialog(this, "No classes selected for deletion.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void loadClassData() {
		// Clear existing table data
		model.setRowCount(0);

		// Add each class to the table with its details
		for (Class c : currentTeacher.getAllClasses()) {
			String className = c.getClassName();
			int studentCount = c.getStudentList().size();
			model.addRow(new Object[] { className, studentCount, false });
		}
	}

}
