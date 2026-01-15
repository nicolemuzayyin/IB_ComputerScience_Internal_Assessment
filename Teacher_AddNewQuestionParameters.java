package package_IA;

import java.awt.*;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class Teacher_AddNewQuestionParameters extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable parametersTable;
	private Question newQuestion;
	private int numberOfParameters;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightGreen = new Color(171, 238, 157);

	public Teacher_AddNewQuestionParameters(int numberOfParameters, Question question, Teacher currentTeacher) {
		this.newQuestion = question;
		this.numberOfParameters = numberOfParameters;
		setBounds(100, 100, 800, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Add New Question");

		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JLabel lblTitle = new JLabel("Add New Question");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Arial", Font.PLAIN, 24));
		lblTitle.setForeground(lightPurple);
		lblTitle.setBounds(0, 20, 800, 30);
		contentPane.add(lblTitle);

		JLabel lblParameters = new JLabel("Enter Parameter Varriables and their Data Types:");
		lblParameters.setFont(new Font("Arial", Font.PLAIN, 16));
		lblParameters.setForeground(lightPurple);
		lblParameters.setBounds(50, 70, 411, 25);
		contentPane.add(lblParameters);

		String[] columnNames = { "Parameter Variable", "Data Type" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		parametersTable = new JTable(model);
		parametersTable.setFont(new Font("Arial", Font.PLAIN, 14));
		parametersTable.setRowHeight(30);
		parametersTable.setGridColor(lightPurple);
		parametersTable.setForeground(lightPurple);

		for (int i = 1; i <= numberOfParameters; i++) {
			String defaultType = (i == 1) ? "String" : "int";
			model.addRow(new Object[] { "Parameter_" + i, defaultType });
		}

		String[] dataTypes = { "String", "int", "double", "boolean" };
		TableColumn dataTypeColumn = parametersTable.getColumnModel().getColumn(1);
		JComboBox<String> comboBox = new JComboBox<>(dataTypes);
		comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
		comboBox.setForeground(lightPurple);
		dataTypeColumn.setCellEditor(new DefaultCellEditor(comboBox));

		JScrollPane scrollPane = new JScrollPane(parametersTable);
		scrollPane.setBounds(50, 110, 700, 350);
		scrollPane.setBorder(BorderFactory.createLineBorder(lightPurple));
		contentPane.add(scrollPane);

		JButton btnContinue = new JButton("Continue");
		btnContinue.setFont(new Font("Arial", Font.PLAIN, 14));
		btnContinue.setForeground(Color.WHITE);
		btnContinue.setBackground(lightGreen);
		btnContinue.setFocusPainted(false);
		btnContinue.setBorderPainted(true);
		btnContinue.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		btnContinue.setOpaque(true);
		btnContinue.setBounds(341, 472, 120, 35);
		contentPane.add(btnContinue);

		btnContinue.addActionListener(e -> {
			saveParameters();
			new Teacher_AddNewQuestionTestCases(currentTeacher, newQuestion);
			dispose();

		});
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void saveParameters() {
		// Get the table model containing parameter data
		DefaultTableModel model = (DefaultTableModel) parametersTable.getModel();

		// Iterate through each row in the parameters table
		for (int i = 0; i < numberOfParameters; i++) {
			// Get parameter name and type from the table cells
			String paramName = (String) model.getValueAt(i, 0);
			String paramType = (String) model.getValueAt(i, 1);

			// Only save if parameter name is not empty
			if (paramName != null && !paramName.trim().isEmpty()) {
				try {
					// Add parameter to the question in database
					newQuestion.addParameter(newQuestion, paramName, paramType);
				} catch (SQLException e) {
					// Show error message if saving fails
					JOptionPane.showMessageDialog(this, "Error saving parameter: " + paramName + "\n" + e.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}
	}

}
