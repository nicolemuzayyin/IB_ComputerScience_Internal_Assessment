package package_IA;

import java.awt.*;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;

public class Teacher_AddNewQuestionTestCases extends JFrame {
	private static final long serialVersionUID = 1L;
	private Question newQuestion;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightGreen = new Color(171, 238, 157);
	private int testCaseCount = 1;
	private JPanel contentPane;
	private JPanel testCasePanel;
	private JScrollPane scrollPane;

	public Teacher_AddNewQuestionTestCases(Teacher currentTeacher, Question newQuestion) {
		this.newQuestion = newQuestion;
		setBounds(100, 100, 800, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Add New Question");

		contentPane = new JPanel();
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

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
		btnBack.setForeground(Color.WHITE);
		btnBack.setBackground(lightPurple);
		btnBack.setFocusPainted(false);
		btnBack.setBorderPainted(false);
		btnBack.setBounds(650, 20, 100, 35);
		contentPane.add(btnBack);

		testCasePanel = new JPanel();
		testCasePanel.setLayout(new BoxLayout(testCasePanel, BoxLayout.Y_AXIS));
		testCasePanel.setBackground(Color.WHITE);
		testCasePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setBackground(Color.WHITE);
		centerPanel.setBounds(0, 70, 800, 400);

		scrollPane = new JScrollPane(testCasePanel);
		scrollPane.setPreferredSize(new Dimension(700, 400));
		scrollPane.setBorder(BorderFactory.createLineBorder(lightPurple));
		centerPanel.add(scrollPane);

		contentPane.add(centerPanel);

		addTestCasePanel();

		JButton btnAddTestCase = new JButton("Add Another Test Case");
		btnAddTestCase.setFont(new Font("Arial", Font.PLAIN, 14));
		btnAddTestCase.setForeground(lightPurple);
		btnAddTestCase.setBackground(Color.WHITE);
		btnAddTestCase.setFocusPainted(false);
		btnAddTestCase.setBorderPainted(true);
		btnAddTestCase.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		btnAddTestCase.setBounds(48, 482, 200, 35);
		contentPane.add(btnAddTestCase);

		JButton btnConfirm = new JButton("Confirm");
		btnConfirm.setFont(new Font("Arial", Font.PLAIN, 14));
		btnConfirm.setForeground(Color.WHITE);
		btnConfirm.setBackground(lightGreen);
		btnConfirm.setFocusPainted(false);
		btnConfirm.setBorderPainted(true);
		btnConfirm.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		btnConfirm.setOpaque(true);
		btnConfirm.setBounds(350, 480, 100, 35);
		contentPane.add(btnConfirm);

		btnAddTestCase.addActionListener(e -> addTestCasePanel());

		btnBack.addActionListener(e -> {
			dispose();
			new Teacher_AddNewQuestionParameters(newQuestion.questionParameters.size(), newQuestion, currentTeacher);
		});

		btnConfirm.addActionListener(e -> {
			try {
				saveTestCases();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			new Teacher_MainMenu(currentTeacher);
			dispose();
		});

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void addTestCasePanel() {
		// Create main panel for test case
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);
		// Add titled border with test case number
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(lightPurple),
				"Test Case " + testCaseCount, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.PLAIN, 14), lightPurple));

		// Set panel size based on number of parameters
		int panelHeight = (newQuestion.questionParameters.size() * 35) + 100;
		panel.setPreferredSize(new Dimension(650, panelHeight));
		panel.setMaximumSize(new Dimension(650, panelHeight));

		// Add input fields for each parameter
		int yOffset = 30;
		ArrayList<JTextField> parameterFields = new ArrayList<>();
		for (int i = 0; i < newQuestion.questionParameters.size(); i++) {
			// Add label and text field for each parameter
			Question_Parameters tempParam = newQuestion.questionParameters.get(i);
			JLabel lblParam = new JLabel(
					"Enter " + tempParam.getParameterVariable() + " Value (" + tempParam.getDataType() + "):");
			lblParam.setFont(new Font("Arial", Font.PLAIN, 14));
			lblParam.setForeground(lightPurple);
			lblParam.setBounds(20, yOffset, 250, 25);
			panel.add(lblParam);

			JTextField txtParam = new JTextField();
			txtParam.setBounds(280, yOffset, 340, 25);
			txtParam.setFont(new Font("Arial", Font.PLAIN, 14));
			panel.add(txtParam);
			parameterFields.add(txtParam);

			yOffset += 35;
		}

		// Add expected output field
		JLabel lblExpected = new JLabel("Expected Output:");
		lblExpected.setFont(new Font("Arial", Font.PLAIN, 14));
		lblExpected.setForeground(lightPurple);
		lblExpected.setBounds(20, yOffset, 200, 25);
		panel.add(lblExpected);
		
		JTextField txtExpected = new JTextField();
		txtExpected.setBounds(280, yOffset, 340, 25);
		txtExpected.setFont(new Font("Arial", Font.PLAIN, 14));
		panel.add(txtExpected);

		// Add panel to container and update UI
		JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		wrapperPanel.setBackground(Color.WHITE);
		wrapperPanel.add(panel);

		testCasePanel.add(wrapperPanel);
		testCasePanel.add(Box.createRigidArea(new Dimension(0, 10)));
		testCaseCount++;

		// Refresh display
		testCasePanel.revalidate();
		testCasePanel.repaint();
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}

	private void saveTestCases() throws SQLException {
		// Component Class Source (See Crit C Source 3)
		// Get all test case panels
		Component[] wrapperPanels = testCasePanel.getComponents();
		int testCaseNumber = 1;

		// Process each test case
		for (Component wrapperComp : wrapperPanels) {
			if (!(wrapperComp instanceof JPanel))
				continue;

			JPanel wrapperPanel = (JPanel) wrapperComp;
			JPanel testCase = (JPanel) ((JPanel) wrapperPanel).getComponent(0);
			Component[] components = testCase.getComponents();

			// Collect parameter values and expected output
			ArrayList<String> parameterValues = new ArrayList<>();
			String expectedOutput = "";
			for (Component comp : components) {
				if (comp instanceof JTextField) {
					JTextField textField = (JTextField) comp;
					String value = textField.getText().trim();

					if (textField.getBounds().y == (newQuestion.questionParameters.size() * 35) + 30) {
						expectedOutput = value;
					} else {
						parameterValues.add(value);
					}
				}
			}

			// Validate all fields are filled
			if (parameterValues.stream().anyMatch(String::isEmpty) || expectedOutput.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please fill in all fields in Test Case " + testCaseNumber,
						"Incomplete Test Case", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Validate parameter types
			for (int i = 0; i < parameterValues.size(); i++) {
				String value = parameterValues.get(i);
				String dataType = newQuestion.questionParameters.get(i).getDataType();
				if (!validateParameterValue(value, dataType)) {
					JOptionPane.showMessageDialog(this,
							"Invalid value for " + newQuestion.questionParameters.get(i).getParameterVariable()
									+ " in Test Case " + testCaseNumber + ". Expected " + dataType,
							"Invalid Input", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}

			// Save test case and increment counter
			newQuestion.addTestCase(parameterValues, expectedOutput);
			testCaseNumber++;
		}

		// Check at least one test case exists
		if (testCaseNumber == 1) {
			JOptionPane.showMessageDialog(this, "Please add at least one test case", "No Test Cases",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Show success message
		JOptionPane.showMessageDialog(this, "Successfully saved " + (testCaseNumber - 1) + " test cases!", "Success",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private boolean validateParameterValue(String value, String dataType) {
		// Parse String to datatype Source (See Crit C Source 1)
		try {
			String type = dataType.toLowerCase();

			if (type.equals("integer")) {
				Integer.parseInt(value);
			} else if (type.equals("double")) {
				Double.parseDouble(value);
			} else if (type.equals("boolean")) {
				if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
					return false;
				}
			} else if (type.equals("long")) {
				Long.parseLong(value);
			}

			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
