package package_IA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.python.google.common.reflect.TypeToken;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Teacher_EditQuestions extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Question selectedQuestion;
	private Teacher currentTeacher;
	private JTable parametersTable;
	private JTable testCasesTable;
	private DefaultTableModel parametersModel;
	private DefaultTableModel testCasesModel;
	private JTextArea questionTextArea;
	private JTextArea solutionHintsArea;
	private JTextField titleField;
	private JComboBox<String> languageCombo;
	private JComboBox<String> ibLevelCombo;
	private JComboBox<String> difficultyCombo;
	private JTextField returnTypeField;
	private JComboBox<Question> questionComboBox;

	static Color lightPurple = new Color(147, 112, 219);
	static Color backgroundColor = new Color(240, 240, 250);
	static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);

	public Teacher_EditQuestions(Teacher teacher) {
		this.currentTeacher = teacher;
		initialize();
	}

	private void initialize() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 900);
		setTitle("Edit Question");

		contentPane = new JPanel();
		contentPane.setBackground(backgroundColor);
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JLabel titleLabel = new JLabel("Edit Questions");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
		titleLabel.setForeground(lightPurple);
		titleLabel.setBounds(300, 6, 200, 20);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(titleLabel);

		JLabel lblTitle = new JLabel("Title:");
		lblTitle.setBounds(50, 60, 100, 25);
		lblTitle.setForeground(lightPurple);
		contentPane.add(lblTitle);

		titleField = new JTextField();
		titleField.setBounds(150, 60, 600, 25);
		contentPane.add(titleField);
		JLabel lblLanguage = new JLabel("Language:");
		lblLanguage.setBounds(50, 95, 100, 25);
		lblLanguage.setForeground(lightPurple);
		contentPane.add(lblLanguage);

		languageCombo = new JComboBox<>(new String[] { "Java", "Python" });
		languageCombo.setBounds(150, 95, 200, 25);
		contentPane.add(languageCombo);

		JLabel lblIBLevel = new JLabel("IB Level:");
		lblIBLevel.setBounds(360, 95, 100, 25);
		lblIBLevel.setForeground(lightPurple);
		contentPane.add(lblIBLevel);

		ibLevelCombo = new JComboBox<>(new String[] { "SL", "HL" });
		ibLevelCombo.setBounds(460, 95, 100, 25);
		contentPane.add(ibLevelCombo);

		JLabel lblDifficulty = new JLabel("Difficulty:");
		lblDifficulty.setBounds(570, 95, 80, 25);
		lblDifficulty.setForeground(lightPurple);
		contentPane.add(lblDifficulty);

		difficultyCombo = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
		difficultyCombo.setBounds(650, 95, 100, 25);
		contentPane.add(difficultyCombo);

		JLabel lblReturnType = new JLabel("Return Type:");
		lblReturnType.setBounds(50, 130, 100, 25);
		lblReturnType.setForeground(lightPurple);
		contentPane.add(lblReturnType);

		returnTypeField = new JTextField();
		returnTypeField.setBounds(150, 130, 200, 25);
		contentPane.add(returnTypeField);

		JLabel lblQuestionText = new JLabel("Question Text:");
		lblQuestionText.setBounds(50, 165, 100, 25);
		lblQuestionText.setForeground(lightPurple);
		contentPane.add(lblQuestionText);

		questionTextArea = new JTextArea();
		questionTextArea.setLineWrap(true);
		questionTextArea.setWrapStyleWord(true);
		JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
		questionScrollPane.setBounds(50, 190, 700, 150);
		contentPane.add(questionScrollPane);

		JLabel lblSolutionHints = new JLabel("Solution Hints:");
		lblSolutionHints.setBounds(50, 350, 100, 25);
		lblSolutionHints.setForeground(lightPurple);
		contentPane.add(lblSolutionHints);

		solutionHintsArea = new JTextArea();
		solutionHintsArea.setLineWrap(true);
		solutionHintsArea.setWrapStyleWord(true);
		JScrollPane hintsScrollPane = new JScrollPane(solutionHintsArea);
		hintsScrollPane.setBounds(50, 375, 700, 100);
		contentPane.add(hintsScrollPane);

		JLabel lblParameters = new JLabel("Parameters:");
		lblParameters.setBounds(50, 485, 100, 25);
		lblParameters.setForeground(lightPurple);
		contentPane.add(lblParameters);

		String[] paramColumns = { "ID", "Parameter Variable", "Data Type" };
		parametersModel = new DefaultTableModel(paramColumns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		parametersTable = new JTable(parametersModel);
		parametersTable.getTableHeader().setBackground(lightPurple);
		parametersTable.getTableHeader().setForeground(Color.WHITE);

		JScrollPane parametersScrollPane = new JScrollPane(parametersTable);
		parametersScrollPane.setBounds(50, 510, 700, 100);
		contentPane.add(parametersScrollPane);

		JButton editParametersButton = new JButton("Edit Parameters");
		editParametersButton.setBounds(50, 620, 150, 25);
		editParametersButton.setBackground(Color.WHITE);
		editParametersButton.setForeground(lightPurple);
		editParametersButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		editParametersButton.setFocusPainted(false);
		editParametersButton.addActionListener(e -> openParametersEditor());
		contentPane.add(editParametersButton);

		JLabel lblTestCases = new JLabel("Test Cases:");
		lblTestCases.setBounds(50, 655, 100, 25);
		lblTestCases.setForeground(lightPurple);
		contentPane.add(lblTestCases);

		String[] testColumns = { "ID", "Parameters", "Expected Value", "Value Data Type" };
		testCasesModel = new DefaultTableModel(testColumns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		testCasesTable = new JTable(testCasesModel);
		testCasesTable.getTableHeader().setBackground(lightPurple);
		testCasesTable.getTableHeader().setForeground(Color.WHITE);

		JScrollPane testCasesScrollPane = new JScrollPane(testCasesTable);
		testCasesScrollPane.setBounds(50, 680, 700, 100);
		contentPane.add(testCasesScrollPane);

		JButton editTestCasesButton = new JButton("Edit Test Cases");
		editTestCasesButton.setBounds(50, 790, 150, 25);
		editTestCasesButton.setBackground(Color.WHITE);
		editTestCasesButton.setForeground(lightPurple);
		editTestCasesButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		editTestCasesButton.setFocusPainted(false);

		editTestCasesButton.addActionListener(e -> openTestCasesEditor());
		contentPane.add(editTestCasesButton);

		JButton saveButton = new JButton("Save Changes");
		saveButton.setBounds(438, 790, 150, 25);
		saveButton.setBackground(lightGreen);
		saveButton.setForeground(Color.WHITE);
		saveButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		saveButton.setOpaque(true);
		saveButton.setFocusPainted(false);
		saveButton.addActionListener(e -> saveChanges());
		contentPane.add(saveButton);

		JButton backButton = new JButton("Back");
		backButton.setBounds(600, 790, 150, 25);
		backButton.setBackground(lightRed);
		backButton.setForeground(Color.WHITE);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);
		backButton.setFocusPainted(false);
		backButton.addActionListener(e -> {
			new Teacher_QuestionInfo(currentTeacher).setVisible(true);
			dispose();
		});
		contentPane.add(backButton);

		JLabel lblQuestion = new JLabel("Select Question:");
		lblQuestion.setBounds(50, 35, 134, 25);
		lblQuestion.setForeground(lightPurple);
		contentPane.add(lblQuestion);

		questionComboBox = new JComboBox<>();
		questionComboBox.setBounds(155, 35, 554, 25);
		contentPane.add(questionComboBox);

		questionComboBox.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Question) {
					Question q = (Question) value;
					setText(q.getQuestionTitle());
				} else if (value != null) {
					setText(value.toString());
				}
				return this;
			}
		});

		ArrayList<Question> questions = Run.AllQuestions;
		DefaultComboBoxModel<Question> comboModel = new DefaultComboBoxModel<>();
		for (Question question : questions) {
			comboModel.addElement(question);
		}
		questionComboBox.setModel(comboModel);

		questionComboBox.addActionListener(e -> {
			if (questionComboBox.getSelectedItem() != null) {
				selectedQuestion = (Question) questionComboBox.getSelectedItem();
				if (selectedQuestion != null) {
					loadQuestionData();
					setFieldsEnabled(true);
				}
			}
		});

		if (questionComboBox.getItemCount() > 0) {
			questionComboBox.setSelectedIndex(0);
		}

		setFieldsEnabled(false);
	}

	private void setFieldsEnabled(boolean enabled) {
		titleField.setEnabled(enabled);
		languageCombo.setEnabled(enabled);
		ibLevelCombo.setEnabled(enabled);
		difficultyCombo.setEnabled(enabled);
		returnTypeField.setEnabled(enabled);
		questionTextArea.setEnabled(enabled);
		solutionHintsArea.setEnabled(enabled);
		parametersTable.setEnabled(enabled);
		testCasesTable.setEnabled(enabled);
	}

	private void loadQuestionData() {
		titleField.setText(selectedQuestion.getQuestionTitle());
		languageCombo.setSelectedItem(selectedQuestion.getLanguage());
		ibLevelCombo.setSelectedItem(selectedQuestion.getIBLevel());
		difficultyCombo.setSelectedItem(selectedQuestion.getDifficulty());
		returnTypeField.setText(selectedQuestion.getReturnType());
		questionTextArea.setText(selectedQuestion.getQuestionText());
		solutionHintsArea.setText(selectedQuestion.getSolutionHints());

		updateParametersTable();
		updateTestCasesTable();
	}

	private void updateParametersTable() {
		parametersModel.setRowCount(0);
		for (Question_Parameters param : selectedQuestion.questionParameters) {
			parametersModel
					.addRow(new Object[] { param.getParameterID(), param.getParameterVariable(), param.getDataType() });
		}
	}

	private void updateTestCasesTable() {
		testCasesModel.setRowCount(0);
		for (Test_Case testCase : selectedQuestion.question_testCases) {
			Map<String, Object> params = testCase.getParameters();
			StringBuilder paramStr = new StringBuilder("{");
			boolean first = true;

			// Sort the parameter entries to maintain consistent order
			ArrayList<Map.Entry<String, Object>> sortedEntries = new ArrayList<>(params.entrySet());
			sortedEntries.sort(Map.Entry.comparingByKey());

			for (Map.Entry<String, Object> entry : sortedEntries) {
				if (!first) {
					paramStr.append(", ");
				}
				String value = entry.getValue().toString();
				// If the value is a string, wrap it in quotes
				if (entry.getValue() instanceof String) {
					value = "\"" + value + "\"";
				}
				paramStr.append("\"").append(entry.getKey()).append("\": ").append(value);
				first = false;
			}
			paramStr.append("}");

			testCasesModel.addRow(new Object[] { testCase.getTestcaseID(), paramStr.toString(), // Now it's properly
																								// formatted as a JSON
																								// string
					testCase.getExpectedValue(), testCase.getExpectedValueDataType() });
		}
	}

	private void openParametersEditor() {
		if (selectedQuestion == null)
			return;

		JDialog dialog = new JDialog(this, "Edit Parameters", true);
		dialog.setSize(500, 400);
		dialog.setLocationRelativeTo(this);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().setBackground(Color.WHITE);

		String[] columns = { "Parameter ID", "Variable Name", "Data Type" };
		DefaultTableModel model = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setBackground(lightPurple);
		table.getTableHeader().setForeground(Color.WHITE);

		JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
		inputPanel.setBackground(Color.WHITE);

		JTextField varNameField = new JTextField();
		JComboBox<String> dataTypeCombo = new JComboBox<>(
				new String[] { "int", "long", "double", "String", "ArrayList", "int[]", "double[]", "String[]" });

		inputPanel.add(new JLabel("Variable Name:"));
		inputPanel.add(varNameField);
		inputPanel.add(new JLabel("Data Type:"));
		inputPanel.add(dataTypeCombo);

		JButton addButton = new JButton("Add New");
		addButton.setBackground(Color.WHITE);
		addButton.setForeground(lightPurple);
		addButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		addButton.setFocusPainted(false);

		JButton editButton = new JButton("Edit Selected");
		editButton.setBackground(Color.WHITE);
		editButton.setForeground(lightPurple);
		editButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		editButton.setFocusPainted(false);

		JButton deleteButton = new JButton("Delete Selected");
		deleteButton.setBackground(Color.WHITE);
		deleteButton.setForeground(lightPurple);
		deleteButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		deleteButton.setFocusPainted(false);

		JButton saveButton = new JButton("Save Changes");
		saveButton.setBackground(Color.WHITE);
		saveButton.setForeground(lightPurple);
		saveButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		saveButton.setFocusPainted(false);

		JButton closeButton = new JButton("Close");
		closeButton.setBackground(Color.WHITE);
		closeButton.setForeground(lightPurple);
		closeButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		closeButton.setFocusPainted(false);

		editButton.setEnabled(false);
		deleteButton.setEnabled(false);
		saveButton.setVisible(false);

		for (Question_Parameters param : selectedQuestion.questionParameters) {
			model.addRow(new Object[] { param.getParameterID(), param.getParameterVariable(), param.getDataType() });
		}

		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = table.getSelectedRow();
				boolean hasSelection = selectedRow != -1;
				editButton.setEnabled(hasSelection);
				deleteButton.setEnabled(hasSelection);

				if (hasSelection) {
					varNameField.setText((String) model.getValueAt(selectedRow, 1));
					dataTypeCombo.setSelectedItem(model.getValueAt(selectedRow, 2));
				}
			}
		});

		addButton.addActionListener(e -> {
			String varName = varNameField.getText().trim();
			String dataType = (String) dataTypeCombo.getSelectedItem();

			if (varName.isEmpty()) {
				JOptionPane.showMessageDialog(dialog, "Variable name cannot be empty!");
				return;
			}

			try {
				selectedQuestion.addParameter(selectedQuestion, varName, dataType);
				updateParametersTable();
				varNameField.setText("");

				model.setRowCount(0);
				for (Question_Parameters param : selectedQuestion.questionParameters) {
					model.addRow(
							new Object[] { param.getParameterID(), param.getParameterVariable(), param.getDataType() });
				}
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(dialog, "Error adding parameter: " + ex.getMessage());
			}
		});

		editButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				varNameField.setText((String) model.getValueAt(selectedRow, 1));
				dataTypeCombo.setSelectedItem(model.getValueAt(selectedRow, 2));
				saveButton.setVisible(true);
				addButton.setEnabled(false);
				deleteButton.setEnabled(false);
			}
		});

		saveButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				int paramId = (int) model.getValueAt(selectedRow, 0);
				String oldVarName = (String) model.getValueAt(selectedRow, 1);
				String newVarName = varNameField.getText().trim();
				String newDataType = (String) dataTypeCombo.getSelectedItem();

				if (newVarName.isEmpty()) {
					JOptionPane.showMessageDialog(dialog, "Variable name cannot be empty!");
					return;
				}

				try {

					selectedQuestion.updateParameter(paramId, newVarName, newDataType);

					// Update parameter table immediately
					model.setValueAt(newVarName, selectedRow, 1);
					model.setValueAt(newDataType, selectedRow, 2);

					// Update test cases for the parameter change
					for (Test_Case testCase : selectedQuestion.question_testCases) {
						Map<String, Object> parameters = testCase.getParameters();
						ArrayList<String> updatedValues = new ArrayList<>();
						boolean needsUpdate = false;

						// If this parameter was renamed, update the JSON structure
						if (!oldVarName.equals(newVarName) && parameters.containsKey(oldVarName)) {
							// Get the value associated with the old parameter name
							Object value = parameters.get(oldVarName);
							// Remove the old parameter entry
							parameters.remove(oldVarName);
							// Add the value with the new parameter name
							parameters.put(newVarName, value);
							needsUpdate = true;
						}

						// Create a list of parameter values in the correct order
						for (Question_Parameters param : selectedQuestion.questionParameters) {
							String paramValue = "";
							String paramName = param.getParameterVariable();

							if (parameters.containsKey(paramName)) {
								paramValue = parameters.get(paramName).toString();
							}

							// If this is the parameter being updated, validate its value
							if (param.getParameterID() == paramId && !paramValue.isEmpty()) {
								try {
									switch (newDataType.toLowerCase()) {
									case "int":
										long longVal = Long.parseLong(paramValue);
										if (longVal >= Integer.MIN_VALUE && longVal <= Integer.MAX_VALUE) {
											paramValue = String.valueOf((int) longVal);
										}
										break;
									case "double":
										Double.parseDouble(paramValue);
										break;
									case "long":
										Long.parseLong(paramValue);
										break;
									case "string":
										if (paramValue.startsWith("\"") && paramValue.endsWith("\"")) {
											paramValue = paramValue.substring(1, paramValue.length() - 1);
										}
										break;
									case "boolean":
										if (paramValue.equalsIgnoreCase("true")
												|| paramValue.equalsIgnoreCase("false")) {
											paramValue = paramValue.toLowerCase();
										}
										break;
									}
								} catch (NumberFormatException ex) {
									paramValue = "";
								}
								needsUpdate = true;
							}

							updatedValues.add(paramValue);
						}

						if (needsUpdate) {
							try {
								selectedQuestion.updateTestCase(testCase.getTestcaseID(), updatedValues,
										testCase.getExpectedValue());
							} catch (SQLException ex) {
								System.err.println("Error updating test case " + testCase.getTestcaseID() + ": "
										+ ex.getMessage());
							}
						}
					}

					// Update UI elements
					SwingUtilities.invokeLater(() -> {
						updateParametersTable();
						updateTestCasesTable();
					});

					// Reset UI state
					saveButton.setVisible(false);
					addButton.setEnabled(true);
					deleteButton.setEnabled(true);

					// Clear input fields
					varNameField.setText("");
					dataTypeCombo.setSelectedIndex(0);
					table.clearSelection();

				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(dialog, "Error updating parameter: " + ex.getMessage());
				}
			}
		});

		deleteButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				int paramId = (int) model.getValueAt(selectedRow, 0);
				int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete this parameter?",
						"Confirm Delete", JOptionPane.YES_NO_OPTION);

				if (confirm == JOptionPane.YES_OPTION) {
					try {
						selectedQuestion.deleteParameter(paramId);
						updateParametersTable();

						model.setRowCount(0);
						for (Question_Parameters param : selectedQuestion.questionParameters) {
							model.addRow(new Object[] { param.getParameterID(), param.getParameterVariable(),
									param.getDataType() });
						}
					} catch (SQLException ex) {
						JOptionPane.showMessageDialog(dialog, "Error deleting parameter: " + ex.getMessage());
					}
				}
			}
		});

		closeButton.addActionListener(e -> dialog.dispose());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(closeButton);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		topPanel.add(inputPanel, BorderLayout.CENTER);

		dialog.getContentPane().add(topPanel, BorderLayout.NORTH);
		dialog.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		dialog.setVisible(true);
	}

	private void openTestCasesEditor() {
		if (selectedQuestion == null)
			return;

		JDialog dialog = new JDialog(this, "Edit Test Cases", true);
		dialog.setSize(600, 400);
		dialog.setLocationRelativeTo(this);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().setBackground(Color.WHITE);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setBackground(Color.WHITE);

		JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
		inputPanel.setBackground(Color.WHITE);

		Map<String, JTextField> parameterFields = new HashMap<>();
		for (Question_Parameters param : selectedQuestion.questionParameters) {
			inputPanel.add(new JLabel(param.getParameterVariable() + " (" + param.getDataType() + "):"));
			JTextField field = new JTextField();
			parameterFields.put(param.getParameterVariable(), field);
			inputPanel.add(field);
		}

		inputPanel.add(new JLabel("Expected Value:"));
		JTextField expectedValueField = new JTextField();
		inputPanel.add(expectedValueField);

		inputPanel.add(new JLabel("Expected Value Type:"));
		JTextField expectedValueTypeField = new JTextField();
		inputPanel.add(expectedValueTypeField);

		JButton addButton = new JButton("Add Test Case");
		addButton.setBackground(Color.WHITE);
		addButton.setForeground(lightPurple);
		addButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		addButton.setFocusPainted(false);

		JButton editButton = new JButton("Edit");
		editButton.setBackground(Color.WHITE);
		editButton.setForeground(lightPurple);
		editButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		editButton.setFocusPainted(false);
		editButton.setEnabled(false);

		JButton saveButton = new JButton("Save");
		saveButton.setBackground(Color.WHITE);
		saveButton.setForeground(lightPurple);
		saveButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		saveButton.setFocusPainted(false);
		saveButton.setVisible(false);

		JButton deleteButton = new JButton("Delete Selected");
		deleteButton.setBackground(Color.WHITE);
		deleteButton.setForeground(lightPurple);
		deleteButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		deleteButton.setFocusPainted(false);
		deleteButton.setEnabled(false);

		JButton closeButton = new JButton("Close");
		closeButton.setBackground(Color.WHITE);
		closeButton.setForeground(lightPurple);
		closeButton.setBorder(BorderFactory.createLineBorder(lightPurple, 2));
		closeButton.setFocusPainted(false);

		String[] columns = { "Test Case ID", "Parameters", "Expected Value", "Value Type" };
		DefaultTableModel model = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable table = new JTable(model);
		table.getTableHeader().setBackground(lightPurple);
		table.getTableHeader().setForeground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(table);

		for (Test_Case testCase : selectedQuestion.question_testCases) {
			model.addRow(new Object[] { testCase.getTestcaseID(), testCase.getParameters(), testCase.getExpectedValue(),
					testCase.getExpectedValueDataType() });
		}

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(Color.WHITE);

		buttonPanel.add(editButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(closeButton);

		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = table.getSelectedRow();
				boolean rowSelected = selectedRow != -1;
				editButton.setEnabled(rowSelected);
				deleteButton.setEnabled(rowSelected);

				if (rowSelected) {
					String paramsJson = model.getValueAt(selectedRow, 1).toString().trim();

					try {
						// Clear all fields first
						for (JTextField field : parameterFields.values()) {
							field.setText("");
						}

						// Remove curly braces
						paramsJson = paramsJson.substring(1, paramsJson.length() - 1);

						Map<String, String> parameters = new HashMap<>();
						String[] pairs = paramsJson.split(",");

						for (String pair : pairs) {
							String[] keyValue = pair.split(": "); // Split on ": " instead of just ":"
							if (keyValue.length == 2) {
								String key = keyValue[0].trim().replace("\"", "");
								String value = keyValue[1].trim().replace("\"", "");
								parameters.put(key, value);
							}
						}

						// Set values for all parameters
						for (Map.Entry<String, String> entry : parameters.entrySet()) {
							JTextField field = parameterFields.get(entry.getKey());
							if (field != null) {
								field.setText(entry.getValue());
							}
						}

						expectedValueField.setText(model.getValueAt(selectedRow, 2).toString());
						expectedValueTypeField.setText(model.getValueAt(selectedRow, 3).toString());
					} catch (Exception ex) {
						System.err.println("Error parsing parameters: " + ex.getMessage());
						ex.printStackTrace();
					}
				}
			}
		});

		addButton.addActionListener(e -> {
			ArrayList<String> parameterValues = new ArrayList<>();
			for (Map.Entry<String, JTextField> entry : parameterFields.entrySet()) {
				parameterValues.add(entry.getValue().getText().trim());
			}

			String expectedValue = expectedValueField.getText().trim();

			if (expectedValue.isEmpty()) {
				JOptionPane.showMessageDialog(dialog, "Expected value cannot be empty!");
				return;
			}

			try {
				selectedQuestion.addTestCase(parameterValues, expectedValue);
				updateTestCasesTable();

				for (JTextField field : parameterFields.values()) {
					field.setText("");
				}
				expectedValueField.setText("");
				expectedValueTypeField.setText("");
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(dialog, "Error adding test case: " + ex.getMessage());
			}
		});

		editButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				editButton.setVisible(false);
				saveButton.setVisible(true);
				addButton.setEnabled(false);
				deleteButton.setEnabled(false);
			}
		});

		saveButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				int testCaseId = (int) model.getValueAt(selectedRow, 0);

				ArrayList<String> parameterValues = new ArrayList<>();
				for (Question_Parameters param : selectedQuestion.questionParameters) {
					JTextField field = parameterFields.get(param.getParameterVariable());
					String value = field.getText().trim();
					parameterValues.add(value);
				}

				String expectedValue = expectedValueField.getText().trim();

				if (expectedValue.isEmpty()) {
					JOptionPane.showMessageDialog(dialog, "Expected value cannot be empty!");
					return;
				}

				try {
					selectedQuestion.updateTestCase(testCaseId, parameterValues, expectedValue);
					updateTestCasesTable();

					saveButton.setVisible(false);
					editButton.setVisible(true);
					addButton.setEnabled(true);
					deleteButton.setEnabled(true);

					for (JTextField field : parameterFields.values()) {
						field.setText("");
					}
					expectedValueField.setText("");
					expectedValueTypeField.setText("");
					table.clearSelection();
				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(dialog, "Error updating test case: " + ex.getMessage());
				}
			}
		});

		deleteButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0) {
				int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete this test case?",
						"Confirm Delete", JOptionPane.YES_NO_OPTION);

				if (confirm == JOptionPane.YES_OPTION) {
					int testCaseId = (int) model.getValueAt(selectedRow, 0);
					try {
						selectedQuestion.deleteTestCase(testCaseId);
						updateTestCasesTable();

						for (JTextField field : parameterFields.values()) {
							field.setText("");
						}
						expectedValueField.setText("");
						expectedValueTypeField.setText("");
						editButton.setEnabled(false);
						deleteButton.setEnabled(false);
					} catch (SQLException ex) {
						JOptionPane.showMessageDialog(dialog, "Error deleting test case: " + ex.getMessage());
					}
				}
			}
		});

		closeButton.addActionListener(e -> {
			updateTestCasesTable();
			dialog.dispose();
		});

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		topPanel.add(inputPanel, BorderLayout.CENTER);
		topPanel.add(addButton, BorderLayout.SOUTH);

		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		dialog.getContentPane().add(mainPanel);
		dialog.setVisible(true);
	}

	private void saveChanges() {
		if (selectedQuestion == null)
			return;

		try {
			selectedQuestion.updateQuestionDetails(titleField.getText(), (String) languageCombo.getSelectedItem(),
					(String) ibLevelCombo.getSelectedItem(), (String) difficultyCombo.getSelectedItem(),
					questionTextArea.getText(), solutionHintsArea.getText(), returnTypeField.getText());

			JOptionPane.showMessageDialog(this, "Question updated successfully!", "Success",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error updating question: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

}
