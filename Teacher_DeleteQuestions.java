package package_IA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class Teacher_DeleteQuestions extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JList<CheckboxListItem> questionList;
	private DefaultListModel<CheckboxListItem> listModel;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);
	private Teacher currentTeacher;

	public Teacher_DeleteQuestions(Teacher teacher) {
		this.currentTeacher = teacher;

		setTitle("CyberDuck - Delete Questions");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDeleteQuestions = new JLabel("Delete Questions");
		lblDeleteQuestions.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeleteQuestions.setFont(new Font("Arial", Font.PLAIN, 24));
		lblDeleteQuestions.setForeground(lightPurple);
		lblDeleteQuestions.setBounds(0, 20, 400, 30);
		contentPane.add(lblDeleteQuestions);

		JLabel lblInstructions = new JLabel("Select questions to delete:");
		lblInstructions.setBounds(50, 60, 300, 25);
		lblInstructions.setForeground(lightPurple);
		contentPane.add(lblInstructions);

		listModel = new DefaultListModel<>();
		questionList = new JList<>(listModel);
		questionList.setCellRenderer(new CheckboxListRenderer());
		questionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		questionList.setBackground(Color.WHITE);
		questionList.setForeground(lightPurple);

		// MouseListener Source (See Crit C Source 22)
		questionList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int index = questionList.locationToIndex(evt.getPoint());
				if (index != -1) {
					CheckboxListItem item = listModel.getElementAt(index);
					item.setSelected(!item.isSelected());
					questionList.repaint();
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(questionList);
		scrollPane.setBounds(50, 90, 300, 200);
		contentPane.add(scrollPane);

		JButton deleteButton = new JButton("Delete Selected Questions");
		deleteButton.setBounds(50, 300, 190, 30);
		deleteButton.setForeground(Color.WHITE);
		deleteButton.setBackground(lightGreen);
		deleteButton.setFocusPainted(false);
		deleteButton.setBorderPainted(true);
		deleteButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		deleteButton.setOpaque(true);
		contentPane.add(deleteButton);

		JButton backButton = new JButton("Back");
		backButton.setBounds(252, 300, 98, 30);
		backButton.setForeground(Color.WHITE);
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);
		contentPane.add(backButton);

		deleteButton.addActionListener(e -> {
			deleteSelectedQuestions();
			dispose();
			new Teacher_MainMenu(currentTeacher).setVisible(true);
		});

		backButton.addActionListener(e -> {
			dispose();
			new Teacher_MainMenu(currentTeacher).setVisible(true);
		});

		loadQuestions();
		setVisible(true);
	}

	private void loadQuestions() {
		// Add all questions to the list model with checkboxes
		for (Question question : Run.AllQuestions) {
			listModel.addElement(new CheckboxListItem(question));
		}
	}

	private void deleteSelectedQuestions() {
		// Collect all selected questions
		ArrayList<Question> selectedQuestions = new ArrayList<>();
		for (int i = 0; i < listModel.size(); i++) {
			CheckboxListItem item = listModel.getElementAt(i);
			if (item.isSelected()) {
				selectedQuestions.add(item.getQuestion());
			}
		}

		// Show error if no questions selected
		if (selectedQuestions.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please select at least one question to delete.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Ask for confirmation before deletion
		int confirm = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete " + selectedQuestions.size() + " question(s)?", "Confirm Deletion",
				JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			// Track questions that fail to delete
			ArrayList<Question> failedDeletions = new ArrayList<>();

			// Try to delete each selected question
			for (Question question : selectedQuestions) {
				try {
					if (!question.deleteQuestion()) {
						failedDeletions.add(question);
					}
				} catch (SQLException e) {
					failedDeletions.add(question);
					e.printStackTrace();
				}
			}

			// Remove successfully deleted questions from UI and memory
			for (Question question : selectedQuestions) {
				if (!failedDeletions.contains(question)) {
					listModel.removeElement(new CheckboxListItem(question));
					Run.AllQuestions.remove(question);
				}
			}

			// Show appropriate success or failure message
			if (failedDeletions.isEmpty()) {
				JOptionPane.showMessageDialog(this, "All selected questions were deleted successfully.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this,
						"Failed to delete " + failedDeletions.size()
								+ " question(s). Please try again or contact support.",
						"Partial Failure", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	// CheckboxList Source (See Crit C Source 2)
	private class CheckboxListItem {
		private Question question;
		private boolean isSelected = false;

		// Constructor to create new checkbox item
		public CheckboxListItem(Question question) {
			this.question = question;
		}

		// Get checkbox selection state
		public boolean isSelected() {
			return isSelected;
		}

		// Set checkbox selection state
		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		// Get the question object
		public Question getQuestion() {
			return question;
		}

		// Display question title in the list
		@Override
		public String toString() {
			return question.getQuestionTitle();
		}

		// Compare checkbox items based on their questions
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CheckboxListItem) {
				return this.question.equals(((CheckboxListItem) obj).question);
			}
			return false;
		}
	}

	// Custom renderer to display checkboxes in the list
	private class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<CheckboxListItem> {
		private static final long serialVersionUID = 1L;

		// Configure how each item appears in the list
		@Override
		public Component getListCellRendererComponent(JList<? extends CheckboxListItem> list, CheckboxListItem value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setSelected(value.isSelected());
			setText(value.toString());
			setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
			setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
			return this;
		}
	}
}
