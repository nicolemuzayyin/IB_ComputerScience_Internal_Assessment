package package_IA;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Teacher_AddQuestion extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);

	public Teacher_AddQuestion(Teacher currentTeacher) {

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		setTitle("Add New Question");

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblTitle = new JLabel("Add New Question");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Arial", Font.PLAIN, 24));
		lblTitle.setForeground(lightPurple);
		lblTitle.setBounds(0, 20, 800, 30);
		contentPane.add(lblTitle);

		JTextField txtQuestionName = new JTextField();
		txtQuestionName.setBounds(200, 80, 400, 35);
		txtQuestionName.setFont(new Font("Arial", Font.PLAIN, 14));
		txtQuestionName.setBorder(BorderFactory.createLineBorder(lightPurple));
		contentPane.add(txtQuestionName);
		setPlaceholder(txtQuestionName, "Enter Question Name");

		JTextField txtDifficulty = new JTextField();
		txtDifficulty.setBounds(200, 130, 400, 35);
		txtDifficulty.setFont(new Font("Arial", Font.PLAIN, 14));
		txtDifficulty.setBorder(BorderFactory.createLineBorder(lightPurple));
		contentPane.add(txtDifficulty);
		setPlaceholder(txtDifficulty, "Enter Difficulty");

		String[] levels = { "SL", "HL" };
		JComboBox<String> comboLevel = new JComboBox<>(levels);
		comboLevel.setBounds(200, 180, 400, 35);
		comboLevel.setFont(new Font("Arial", Font.PLAIN, 14));
		comboLevel.setBackground(Color.WHITE);
		comboLevel.setForeground(lightPurple);
		comboLevel.setBorder(BorderFactory.createLineBorder(lightPurple));
		contentPane.add(comboLevel);

		JTextField txtLanguage = new JTextField();
		txtLanguage.setBounds(200, 230, 400, 35);
		txtLanguage.setFont(new Font("Arial", Font.PLAIN, 14));
		txtLanguage.setBorder(BorderFactory.createLineBorder(lightPurple));
		contentPane.add(txtLanguage);
		setPlaceholder(txtLanguage, "Enter Language");

		JTextField txtHint = new JTextField();
		txtHint.setBounds(200, 280, 400, 35);
		txtHint.setFont(new Font("Arial", Font.PLAIN, 14));
		txtHint.setBorder(BorderFactory.createLineBorder(lightPurple));
		contentPane.add(txtHint);
		setPlaceholder(txtHint, "Enter Hint for the Solution");

		JTextField txtReturnType = new JTextField();
		txtReturnType.setBounds(200, 330, 400, 35);
		txtReturnType.setFont(new Font("Arial", Font.PLAIN, 14));
		txtReturnType.setBorder(BorderFactory.createLineBorder(lightPurple));
		contentPane.add(txtReturnType);
		setPlaceholder(txtReturnType, "Enter Return Type (int, String, etc.)");

		JLabel lblParameters = new JLabel("Enter the Number of Parameters:");
		lblParameters.setFont(new Font("Arial", Font.PLAIN, 14));
		lblParameters.setForeground(lightPurple);
		lblParameters.setBounds(200, 380, 250, 35);
		contentPane.add(lblParameters);

		// SpinnerNumberModel Source (See Crit C Source 24)
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
		// JSpinner Source (See Crit C Source 16)
		JSpinner spinnerParams = new JSpinner(spinnerModel);
		spinnerParams.setBounds(450, 380, 150, 35);
		spinnerParams.setFont(new Font("Arial", Font.PLAIN, 14));
		((JSpinner.DefaultEditor) spinnerParams.getEditor()).getTextField().setBackground(Color.WHITE);
		contentPane.add(spinnerParams);

		JButton btnContinue = new JButton("Continue");
		btnContinue.setFont(new Font("Arial", Font.PLAIN, 14));
		btnContinue.setForeground(Color.WHITE);
		btnContinue.setBackground(lightGreen);
		btnContinue.setFocusPainted(false);
		btnContinue.setBorderPainted(true);
		btnContinue.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		btnContinue.setOpaque(true);
		btnContinue.setBounds(350, 450, 100, 35);
		contentPane.add(btnContinue);
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String questionTitle = txtQuestionName.getText();
				String difficulty = txtDifficulty.getText();
				String level = (String) comboLevel.getSelectedItem();
				String language = txtLanguage.getText();
				String solutionHints = txtHint.getText();
				String returnType = txtReturnType.getText();
				int numParameters = (Integer) spinnerParams.getValue();

				if (questionTitle.equals("Enter Question Name") || questionTitle.trim().isEmpty()) {
					javax.swing.JOptionPane.showMessageDialog(null, "Please enter a question name", "Input Error",
							javax.swing.JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (difficulty.equals("Enter Difficulty") || difficulty.trim().isEmpty()) {
					javax.swing.JOptionPane.showMessageDialog(null, "Please enter a difficulty level", "Input Error",
							javax.swing.JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (language.equals("Enter Language") || language.trim().isEmpty()) {
					javax.swing.JOptionPane.showMessageDialog(null, "Please enter a programming language",
							"Input Error", javax.swing.JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (solutionHints.equals("Enter Hint for the Solution") || solutionHints.trim().isEmpty()) {
					javax.swing.JOptionPane.showMessageDialog(null, "Please enter a hint", "Input Error",
							javax.swing.JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (returnType.equals("Enter Return Type (int, String, void, etc.)") || returnType.trim().isEmpty()) {
					javax.swing.JOptionPane.showMessageDialog(null, "Please enter a return type", "Input Error",
							javax.swing.JOptionPane.ERROR_MESSAGE);
					return;
				}

				new Teacher_AddQuestionText(questionTitle, difficulty, level, language, solutionHints, returnType,
						numParameters, currentTeacher);
				dispose();
			}
		});

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
		btnBack.setForeground(Color.WHITE);
		btnBack.setBackground(lightRed);
		btnBack.setFocusPainted(false);
		btnBack.setBorderPainted(true);
		btnBack.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		btnBack.setOpaque(true);
		btnBack.setBounds(650, 20, 100, 35);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Teacher_QuestionInfo(currentTeacher);
			}
		});
		contentPane.add(btnBack);

		setLocationRelativeTo(null);
		setVisible(true);
	}

	// Helper method to set up placeholder text behavior for text fields
	private void setPlaceholder(JTextField textField, String placeholder) {
		textField.setText(placeholder);
		textField.setForeground(Color.GRAY);

		textField.addFocusListener(new java.awt.event.FocusAdapter() {
			// Clear placeholder text when field gains focus
			public void focusGained(java.awt.event.FocusEvent evt) {
				if (textField.getText().equals(placeholder)) {
					textField.setText("");
					textField.setForeground(lightPurple);
				}
			}

			// Restore placeholder text if field is empty when losing focus
			public void focusLost(java.awt.event.FocusEvent evt) {
				if (textField.getText().isEmpty()) {
					textField.setText(placeholder);
					textField.setForeground(Color.GRAY);
				}
			}
		});
	}
}
