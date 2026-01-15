package package_IA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Student_QuestionBank extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField searchField;
	private JTable questionTable;
	private JButton searchButton, randomButton, backButton;
	private Student currentStudent;
	private String language;
	private ArrayList<Question> relevantQuestions;
	private DefaultTableModel table;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);

	public Student_QuestionBank(Student student, String language) {
		this.currentStudent = student;
		this.language = language;
		this.relevantQuestions = filterQuestionsByLanguage();

		setTitle("CyberDuck");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
		mainPanel.setBackground(lightPurple);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

		JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(Color.WHITE);

		JLabel titleLabel = new JLabel(language + " Question Bank");
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
		backButton.setPreferredSize(new Dimension(100, 35));
		titlePanel.add(backButton, BorderLayout.EAST);

		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
		searchPanel.setBackground(Color.WHITE);

		JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		searchInputPanel.setBackground(Color.WHITE);

		JLabel searchLabel = new JLabel("Enter Question Name:");
		searchLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		searchLabel.setForeground(lightPurple);
		searchInputPanel.add(searchLabel);

		searchField = new JTextField(20);
		searchField.setPreferredSize(new Dimension(250, 35));
		searchField.setFont(new Font("Arial", Font.PLAIN, 14));
		searchInputPanel.add(searchField);

		searchButton = new JButton("Search");
		searchButton.setPreferredSize(new Dimension(120, 35));
		searchButton.setFont(new Font("Arial", Font.BOLD, 14));
		searchButton.setForeground(Color.WHITE);
		searchButton.setBackground(lightPurple);
		searchButton.setFocusPainted(false);
		searchButton.setBorderPainted(false);
		searchButton.setOpaque(true);
		searchInputPanel.add(searchButton);

		randomButton = new JButton("Random");
		randomButton.setPreferredSize(new Dimension(120, 35));
		randomButton.setFont(new Font("Arial", Font.BOLD, 14));
		randomButton.setForeground(Color.WHITE);
		randomButton.setBackground(lightPurple);
		randomButton.setFocusPainted(false);
		randomButton.setBorderPainted(false);
		randomButton.setOpaque(true);
		searchInputPanel.add(randomButton);

		JButton recommendationsButton = new JButton("Recommendations");
		recommendationsButton.setPreferredSize(new Dimension(175, 35));
		recommendationsButton.setFont(new Font("Arial", Font.BOLD, 14));
		recommendationsButton.setForeground(Color.WHITE);
		recommendationsButton.setBackground(lightPurple);
		recommendationsButton.setFocusPainted(false);
		recommendationsButton.setBorderPainted(false);
		recommendationsButton.setOpaque(true);
		searchInputPanel.add(recommendationsButton);

		searchPanel.add(searchInputPanel);
		// Define column structure for the question table
		String[] columnNames = { "Question Title", "Difficulty", "IB Level", "" };

		// Create custom table model with specific editing permissions
		table = new DefaultTableModel(columnNames, 0) { // 0 indicates initial row count
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// Only allow editing of the last column (index 3) which contains action buttons
				return column == 3;
			}
		};

		// Initialize JTable with custom model
		questionTable = new JTable(table);

		questionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		questionTable.setRowHeight(30);
		questionTable.setFont(new Font("Arial", Font.PLAIN, 14));

		TableColumn buttonColumn = questionTable.getColumnModel().getColumn(3);
		buttonColumn.setCellRenderer(new ButtonRenderer());
		buttonColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

		JScrollPane scrollPane = new JScrollPane(questionTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createLineBorder(lightPurple));
		scrollPane.setPreferredSize(new Dimension(500, 300));

		JPanel centeringPanel = new JPanel(new GridBagLayout());
		centeringPanel.setBackground(Color.WHITE);
		centeringPanel.add(scrollPane);

		contentPanel.add(titlePanel, BorderLayout.NORTH);
		contentPanel.add(searchPanel, BorderLayout.CENTER);
		contentPanel.add(centeringPanel, BorderLayout.SOUTH);

		mainPanel.add(contentPanel, BorderLayout.CENTER);

		add(mainPanel);

		searchButton.addActionListener(e -> filterQuestions());
		randomButton.addActionListener(e -> getRandomQuestion());
		recommendationsButton.addActionListener(e -> showRecommendations());
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Student_MainMenu(currentStudent).setVisible(true);
			}
		});

		displayAllQuestions();

		setVisible(true);
	}

	private ArrayList<Question> filterQuestionsByLanguage() {
		// Create a new list to store filtered questions
		ArrayList<Question> filtered = new ArrayList<>();

		// Iterate through all questions in the system
		for (Question q : Run.AllQuestions) {
			// Add question to filtered list if its language matches (case-insensitive)
			if (q.getLanguage().equalsIgnoreCase(language)) {
				filtered.add(q);
			}
		}

		return filtered;
	}

	private void displayAllQuestions() {
		// Clear all existing rows from the table
		table.setRowCount(0);

		// Iterate through all relevant questions and add them to the table
		for (Question question : relevantQuestions) {
			table.addRow(new Object[] { question.getQuestionTitle(), question.getDifficulty(), question.getIBLevel(),
					"Select" });
		}
	}

	private void filterQuestions() {
		// Get the search term and convert to lowercase for case-insensitive comparison
		String searchTerm = searchField.getText().toLowerCase().trim();

		// Clear the existing table content
		table.setRowCount(0);

		// If search field is empty, show all questions
		if (searchTerm.isEmpty()) {
			displayAllQuestions();
			return;
		}

		// Iterate through questions and add only those matching the search term
		for (Question question : relevantQuestions) {
			if (question.getQuestionTitle().toLowerCase().contains(searchTerm)) {
				table.addRow(new Object[] { question.getQuestionTitle(), question.getDifficulty(),
						question.getIBLevel(), "Select" });
			}
		}

		// If no matching questions found, display a message in the table
		if (table.getRowCount() == 0) {
			table.addRow(new Object[] { "No questions found matching: " + searchTerm, "", "", "" });
		}
	}

	private void getRandomQuestion() {
		if (relevantQuestions.isEmpty()) {
			table.setRowCount(0);
			table.addRow(new Object[] { "No questions available.", "", "", "" });
			return;
		}
		int randomIndex = (int) (Math.random() * relevantQuestions.size());
		Question randomQuestion = relevantQuestions.get(randomIndex);
		table.setRowCount(0);
		table.addRow(new Object[] { randomQuestion.getQuestionTitle(), randomQuestion.getDifficulty(),
				randomQuestion.getIBLevel(), "Select" });
	}

	private void answerGUI(Question question) {
		dispose();
		new Student_SubmitAnswer(currentStudent, question).setVisible(true);
	}

	// ButtonRenderer and ButtonEditor Class Source (See Crit C Source 27)
	/**
	 * Custom ButtonRenderer class that defines how the select buttons appear in
	 * table cells Extends JButton to inherit button properties and implements
	 * TableCellRenderer to customize how the cell is displayed in the table
	 */
	class ButtonRenderer extends JButton implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		// visual properties of buttons
		public ButtonRenderer() {
			setOpaque(true);
			setForeground(Color.WHITE);
			setBackground(lightPurple);
			setFocusPainted(false);
			setBorderPainted(true);
			setBorder(BorderFactory.createLineBorder(lightPurple, 1));
			setFont(new Font("Arial", Font.PLAIN, 12));
		}

		/**
		 * Required method from TableCellRenderer interface Called for each cell in the
		 * button column to determine how it should be displayed
		 * 
		 * table = The JTable containing the button value = The value to be rendered
		 * (button text) isSelected = Whether the cell is selected hasFocus = Whether
		 * the cell has focus row = The row index of the cell column = The column index
		 * of the cell return the configured button component
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
	    private static final long serialVersionUID = 1L;
	    protected JButton button;
	    private String label;
	    private boolean isPushed;

	    public ButtonEditor(JCheckBox checkBox) {
	        super(checkBox);
	        button = new JButton();
	        button.setOpaque(true);
	        button.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                fireEditingStopped();
	            }
	        });
	    }

	    @Override
	    public Component getTableCellEditorComponent(JTable table, Object value,
	            boolean isSelected, int row, int column) {
	        label = (value == null) ? "" : value.toString();
	        button.setText(label);
	        isPushed = true;
	        return button;
	    }

	    @Override
	    public Object getCellEditorValue() {
	        if (isPushed) {
	            int selectedRow = questionTable.getSelectedRow();
	            if (selectedRow != -1 && selectedRow < relevantQuestions.size()) {
	                String selectedTitle = (String) questionTable.getValueAt(selectedRow, 0);
	                // Find the question with matching title
	                for (Question q : relevantQuestions) {
	                    if (q.getQuestionTitle().equals(selectedTitle)) {
	                        answerGUI(q);
	                        break;
	                    }
	                }
	            }
	        }
	        isPushed = false;
	        return label;
	    }

	    @Override
	    public boolean stopCellEditing() {
	        isPushed = false;
	        return super.stopCellEditing();
	    }
	}


	private void showRecommendations() {
		// Run getRecommendedQuestions method for the questions of the chosen coding
		ArrayList<Question> recommendedQuestions = currentStudent.getRecommendedQuestions(relevantQuestions);

		table.setRowCount(0);

		if (recommendedQuestions.isEmpty()) {
			table.addRow(new Object[] { "No recommendations available at this time.", "-", "-", "" });
		} else {
			// update DefaultModelTable with the recommended questions
			for (Question question : recommendedQuestions) {
				table.addRow(new Object[] { question.getQuestionTitle(), question.getDifficulty(),
						question.getIBLevel(), "Select" });
			}
		}
	}

}
