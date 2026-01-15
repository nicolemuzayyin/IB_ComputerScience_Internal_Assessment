package package_IA;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;

public class Student_SubmitAnswer extends JFrame {
	private static final long serialVersionUID = 1L;
	private JLabel filePathLabel;
	private JTable resultsTable;
	private DefaultTableModel tableModel;
	private JButton backButton, browseButton, submitButton;
	private Student currentStudent;
	private Question currentQuestion;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);
	private JProgressBar progressBar;
	private JTextArea hintBox;
	private int attemptCount = 0;
	private JLabel attemptsLabel;

	public Student_SubmitAnswer(Student student, Question question) {
		this.currentStudent = student;
		this.currentQuestion = question;
		initialize();
	}

	private void initialize() {
		setTitle("CyberDuck - " + currentQuestion.getLanguage() + " Question");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
		mainPanel.setBackground(lightPurple);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

		JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(Color.WHITE);
		JLabel titleLabel = new JLabel(currentQuestion.getQuestionTitle());
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
		titleLabel.setForeground(lightPurple);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add(titleLabel, BorderLayout.CENTER);

		backButton = new JButton("Back");
		backButton.setFont(new Font("Arial", Font.BOLD, 14));
		backButton.setForeground(Color.WHITE);
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);
		backButton.setPreferredSize(new Dimension(120, 35));
		backButton.addActionListener(e -> {
			dispose();
			new Student_MainMenu(currentStudent).setVisible(true);
		});
		titlePanel.add(backButton, BorderLayout.EAST);

		JPanel uploadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		uploadPanel.setBackground(Color.WHITE);
		JLabel uploadLabel = new JLabel("Selected File:");
		uploadLabel.setFont(new Font("Arial", Font.BOLD, 16));
		uploadLabel.setForeground(lightPurple);
		uploadPanel.add(uploadLabel);

		filePathLabel = new JLabel("No file selected");
		filePathLabel.setForeground(new Color(173, 154, 255));
		filePathLabel.setPreferredSize(new Dimension(250, 35));
		filePathLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		filePathLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		filePathLabel.setOpaque(true);
		filePathLabel.setBackground(Color.WHITE);
		uploadPanel.add(filePathLabel);

		browseButton = new JButton("Browse");
		browseButton.setFont(new Font("Arial", Font.BOLD, 14));
		browseButton.setForeground(Color.WHITE);
		browseButton.setBackground(lightPurple);
		browseButton.setFocusPainted(false);
		browseButton.setBorderPainted(false);
		browseButton.setOpaque(true);
		browseButton.setPreferredSize(new Dimension(100, 35));
		browseButton.addActionListener(e -> browseFile());
		uploadPanel.add(browseButton);

		submitButton = new JButton("Submit");
		submitButton.setFont(new Font("Arial", Font.BOLD, 14));
		submitButton.setForeground(Color.WHITE);
		submitButton.setBackground(lightGreen);
		submitButton.setFocusPainted(false);
		submitButton.setBorderPainted(true);
		submitButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		submitButton.setOpaque(true);
		submitButton.setPreferredSize(new Dimension(100, 35));
		submitButton.addActionListener(e -> submitFile());
		uploadPanel.add(submitButton);

		JPanel questionInfoPanel = new JPanel(new BorderLayout());
		questionInfoPanel.setBackground(Color.WHITE);

		JTextArea questionTextArea = new JTextArea();
		questionTextArea.setEditable(false);
		questionTextArea.setWrapStyleWord(true);
		questionTextArea.setLineWrap(true);
		questionTextArea.setOpaque(false);
		questionTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
		questionTextArea.setForeground(lightPurple);

		//StringBuilder Code Source (See Crit C Source 25)
		StringBuilder questionInfo = new StringBuilder();
		questionInfo.append("This question contains the following parameters: ");
		boolean isFirst = true;
		for (Question_Parameters param : currentQuestion.questionParameters) {
			if (!isFirst) {
				questionInfo.append(", ");
			}
			questionInfo.append("(").append(param.getDataType()).append(") ").append(param.getParameterVariable());
			isFirst = false;
		}
		questionInfo.append("\n\nQuestion:\n");
		questionInfo.append(currentQuestion.getQuestionText());

		questionTextArea.setText(questionInfo.toString());

		questionInfoPanel.add(questionTextArea);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(Color.WHITE);
		centerPanel.add(uploadPanel, BorderLayout.NORTH);
		centerPanel.add(questionInfoPanel, BorderLayout.CENTER);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(Color.WHITE);
		//DefaultTableModel Source (See Crit C Source 28)
		String[] columnNames = { "Parameter", "Data Type", "Value", "Expected Output", "Actual Output", "Correct?" };
		tableModel = new DefaultTableModel(columnNames, 0);
		resultsTable = new JTable(tableModel);
		resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
		resultsTable.setRowHeight(30);
		//JScrollPane Source (See Crit C Source 12)
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		scrollPane.setPreferredSize(new Dimension(750, 200));
		scrollPane.setBorder(BorderFactory.createLineBorder(lightPurple));
		tablePanel.add(scrollPane, BorderLayout.CENTER);
		//JSplitPaner Source (See Crit C Source 17)
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerPanel, tablePanel);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(250);
		splitPane.setBackground(Color.WHITE);

		JPanel mainContentPanel = new JPanel(new BorderLayout());
		mainContentPanel.setBackground(Color.WHITE);
		mainContentPanel.add(titlePanel, BorderLayout.NORTH);
		mainContentPanel.add(splitPane, BorderLayout.CENTER);

		contentPanel.add(mainContentPanel, BorderLayout.CENTER);
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		getContentPane().add(mainPanel);

		JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		progressPanel.setBackground(Color.WHITE);

		JLabel progressLabel = new JLabel("Progress:");
		progressLabel.setFont(new Font("Arial", Font.BOLD, 14));
		progressLabel.setForeground(lightPurple);
		progressPanel.add(progressLabel);

		//JProgressBar Source (See Crit C Source 15)
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(200, 20));
		progressBar.setForeground(lightPurple);
		progressBar.setString("0%");
		progressBar.setFont(new Font("Arial", Font.BOLD, 12));
		progressPanel.add(progressBar);

		bottomPanel.add(progressPanel, BorderLayout.SOUTH);

		hintBox = new JTextArea("Hints will appear here after 3 unsuccessful attempts.");
		hintBox.setEditable(false);
		hintBox.setLineWrap(true);
		hintBox.setWrapStyleWord(true);
		hintBox.setPreferredSize(new Dimension(300, 60));
		hintBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(lightPurple), "Hint",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12),
				lightPurple));
		hintBox.setBackground(Color.WHITE);
		hintBox.setForeground(lightPurple);
		hintBox.setFont(new Font("Arial", Font.PLAIN, 12));
		bottomPanel.add(hintBox, BorderLayout.EAST);

		attemptsLabel = new JLabel("Attempts: " + attemptCount);
		attemptsLabel.setFont(new Font("Arial", Font.BOLD, 14));
		attemptsLabel.setForeground(lightPurple);
		bottomPanel.add(attemptsLabel, BorderLayout.NORTH);

		contentPanel.add(bottomPanel, BorderLayout.SOUTH);

		setVisible(true);
	}

	private void browseFile() {
	    // Create a file chooser dialog window
	    //JFileChooser Source (See Crit C Source 14)
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Select your answer file");
	    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

	    // Determine required file extension based on question language
	    String questionLanguage = currentQuestion.getLanguage();
	    String requiredExtension;
	    if (questionLanguage.equalsIgnoreCase("Java")) {
	        requiredExtension = "java";
	    } else if (questionLanguage.equalsIgnoreCase("Python")) {
	        requiredExtension = "py";
	    } else {
	        // Show error for unsupported languages
	        JOptionPane.showMessageDialog(this, "Unsupported language type: " + questionLanguage, "Error",
	                JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    // Set up file type filter
	    //FileNameExtensionFilter Source (See Crit C Source 7)
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            questionLanguage + " files (*." + requiredExtension + ")", requiredExtension);
	    fileChooser.setFileFilter(filter);
	    fileChooser.setAcceptAllFileFilterUsed(false);

	    // Show file chooser dialog and handle user selection
	    int result = fileChooser.showOpenDialog(this);
	    if (result == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        String fileName = selectedFile.getName().toLowerCase();

	        // Verify file has correct extension
	        if (fileName.endsWith("." + requiredExtension)) {
	            filePathLabel.setText(selectedFile.getAbsolutePath());
	        } else {
	            // Show warning if wrong file type is selected
	            JOptionPane.showMessageDialog(this,
	                    "Please select a ." + requiredExtension + " file for " + questionLanguage + " questions.",
	                    "Invalid File Type", JOptionPane.WARNING_MESSAGE);
	        }
	    }
	}

	private void submitFile() {
	    // Check if a file has been selected
	    if (filePathLabel.getText().equals("No file selected")) {
	        JOptionPane.showMessageDialog(this, "Please select a file first.", "No File Selected",
	                JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    try {
	        // Create File object from the selected file path
	        File selectedFile = new File(filePathLabel.getText());
	        
	        // Read entire file content into a string with UTF-8 encoding
	        String fileContent = new String(Files.readAllBytes(selectedFile.toPath()));
	        
	        // Reset progress bar to initial state
	        progressBar.setValue(0);
	        progressBar.setString("0%");
	        
	        // Process the file content with the file name
	        processFileContent(fileContent, selectedFile.getName());
	    } catch (IOException e) {
	        // Display error message if file reading fails
	        JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(), "File Read Error",
	                JOptionPane.ERROR_MESSAGE);
	    }
	}

	private void processFileContent(String fileContent, String fileName) {
	    // Clear existing rows from the table
	    while (tableModel.getRowCount() > 0) {
	        tableModel.removeRow(0);
	    }

	    // Initialize test case counters
	    int totalTestCases = currentQuestion.question_testCases.size();
	    int correctTestCases = 0;

	    // Process each test case
	    for (Test_Case testCase : currentQuestion.question_testCases) {
	        Map<String, Object> testParameters = testCase.getParameters();

	        // Initialize string builders for parameter display
	        StringBuilder paramNames = new StringBuilder();
	        StringBuilder dataTypes = new StringBuilder();
	        StringBuilder values = new StringBuilder();

	        // Build comma-separated strings for parameter details
	        boolean isFirst = true;
	        for (Question_Parameters param : currentQuestion.questionParameters) {
	            if (!isFirst) {
	                paramNames.append(", ");
	                dataTypes.append(", ");
	                values.append(", ");
	            }
	            paramNames.append(param.getParameterVariable());
	            dataTypes.append(param.getDataType());
	            values.append(testParameters.get(param.getParameterVariable()));
	            isFirst = false;
	        }

	        // Create parameters map with correct data types
	        Map<String, Object> parameters = new HashMap<>();
	        for (Question_Parameters param : currentQuestion.questionParameters) {
	            String paramVariable = param.getParameterVariable();
	            Object paramValue = testParameters.get(paramVariable);
	            if (paramValue != null) {
	                parameters.put(paramVariable, param.convertToType(paramValue.toString()));
	            }
	        }

	        // Execute the code with current test case
	        String result = currentQuestion.runCode(fileContent, parameters, fileName);

	        // Check if result matches expected value and update table
	        boolean isCorrect = result.equals(testCase.getExpectedValue());
	        Object[] row = { paramNames.toString(), dataTypes.toString(), values.toString(),
	                testCase.getExpectedValue(), result, isCorrect ? "✓" : "✗" };
	        tableModel.addRow(row);

	        // Update correct test cases counter
	        if (isCorrect) {
	            correctTestCases++;
	        }
	    }

	    // Calculate and update progress bar
	    int progress = (correctTestCases * 100) / totalTestCases;
	    progressBar.setValue(progress);
	    progressBar.setString(progress + "%");
	    progressBar.setForeground(lightPurple);

	    // Check if all test cases passed
	    boolean allCorrect = (progress == 100);
	    attemptCount++;
	    attemptsLabel.setText("Attempts: " + attemptCount);
	    
	    // Record the attempt in student's history
	    boolean attemptRecorded = currentStudent.addAttempt(currentQuestion, allCorrect);
	    
	    // Show appropriate message based on results
	    if (allCorrect) {
	        JOptionPane.showMessageDialog(this, 
	            "Congratulations! All test cases passed!", 
	            "Success", 
	            JOptionPane.INFORMATION_MESSAGE);
	    } else {
	        // Show hint after 3 attempts and display partial success message
	        if (attemptCount >= 3) {
	            showHint();
	        }
	        JOptionPane.showMessageDialog(this, 
	            "Some test cases failed. Keep trying!", 
	            "Partial Success", 
	            JOptionPane.WARNING_MESSAGE);
	    }

	    // Warn if attempt recording failed
	    if (!attemptRecorded) {
	        JOptionPane.showMessageDialog(this,
	            "Note: There was an issue recording your attempt, but you can continue practicing.",
	            "Warning",
	            JOptionPane.WARNING_MESSAGE);
	    }
	}


	private void showHint() {
		String hint = currentQuestion.getSolutionHints();
		if (hint != null && !hint.isEmpty()) {
			//Change hint box to display Solution Hint
			hintBox.setText(hint);
		} else {
			hintBox.setText("No hint available for this question.");
		}
		hintBox.setForeground(lightPurple);
	}

}
