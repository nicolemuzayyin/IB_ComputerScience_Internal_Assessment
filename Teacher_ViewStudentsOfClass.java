package package_IA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Teacher_ViewStudentsOfClass extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable studentTable;
	private DefaultTableModel model;
	private Class currentClass;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	
	public Teacher_ViewStudentsOfClass(Teacher teacher, Class currentClass) {
		this.currentClass = currentClass;

		setTitle("CyberDuck - View Students of " + currentClass.getClassName());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400); 

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblStudentInfo = new JLabel("Students in " + currentClass.getClassName());
		lblStudentInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblStudentInfo.setFont(new Font("Arial", Font.PLAIN, 24));
		lblStudentInfo.setForeground(lightPurple);
		lblStudentInfo.setBounds(0, 20, 800, 30);
		contentPane.add(lblStudentInfo);

		String[] columnNames = { "Student Name", "Grade Level", "IB Level", "Correct Attempts",
				"Incorrect Attempts" };
		model = new DefaultTableModel(columnNames, 0);

		studentTable = new JTable(model);
		studentTable.setBackground(Color.WHITE);
		studentTable.setForeground(lightPurple);
		studentTable.setGridColor(lightPurple);
		studentTable.getTableHeader().setBackground(lightPurple);
		studentTable.getTableHeader().setForeground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(studentTable);
		scrollPane.setBounds(50, 60, 700, 250);
		contentPane.add(scrollPane);

		JButton backButton = new JButton("Back");
		backButton.setBounds(350, 320, 100, 30);
		backButton.setForeground(Color.WHITE);
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);
		contentPane.add(backButton);

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Teacher_ClassInfo classInfoFrame = new Teacher_ClassInfo(teacher);
				classInfoFrame.setVisible(true);
				dispose();
			}
		});

		loadStudentData();

		setVisible(true);
	}

	public void refreshData() {
		loadStudentData();
	}

	private void loadStudentData() {
	    model.setRowCount(0);

	    ArrayList<Student> studentList = currentClass.getStudentList();

	    for (Student student : studentList) {
	        model.addRow(new Object[]{
	            student.getFullName(),
	            student.getGradeLevel(),
	            student.getIBLevel(),
	            student.getCorrectAttempts(),
	            student.getIncorrectAttempts()
	        });
	    }
	}


}
