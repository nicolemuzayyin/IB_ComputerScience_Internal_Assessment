package package_IA;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class Teacher_QuestionInfo extends JFrame {

	private static final long serialVersionUID = 1L;
	private Teacher currentTeacher;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	private JTable questionTable;
	private DefaultTableModel model;

	public Teacher_QuestionInfo(Teacher teacher) {
		this.currentTeacher = teacher;

		setTitle("CyberDuck - Manage Questions");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 400);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblClassInfo = new JLabel("Questions Info");
		lblClassInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblClassInfo.setFont(new Font("Arial", Font.PLAIN, 24));
		lblClassInfo.setForeground(lightPurple);
		lblClassInfo.setBounds(0, 20, 600, 30);
		contentPane.add(lblClassInfo);

		String[] columnNames = { "Question Name", "Difficulty", "Language", "IB Level", "View Parameters" };
		model = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 4;
			}
		};

		questionTable = new JTable(model);
		questionTable.setBackground(Color.WHITE);
		questionTable.setForeground(lightPurple);
		questionTable.setGridColor(lightPurple);
		questionTable.getTableHeader().setBackground(lightPurple);
		questionTable.getTableHeader().setForeground(Color.WHITE);

		questionTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		questionTable.getColumnModel().getColumn(1).setPreferredWidth(70);
		questionTable.getColumnModel().getColumn(2).setPreferredWidth(70);
		questionTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		questionTable.getColumnModel().getColumn(4).setPreferredWidth(110);

		TableColumn viewParametersColumn = questionTable.getColumnModel().getColumn(4);
		viewParametersColumn.setCellRenderer(new ButtonRenderer());

		ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox());
		buttonEditor.setActionListener(e -> {
			int selectedRow = questionTable.getSelectedRow();
			if (selectedRow != -1) {
				viewQuestionParametersAndTestCases(selectedRow);
			}
		});
		viewParametersColumn.setCellEditor(buttonEditor);

		JScrollPane scrollPane = new JScrollPane(questionTable);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(50, 60, 500, 250);
		contentPane.add(scrollPane);

		questionTable.setFillsViewportHeight(true);

		JButton addButton = new JButton("Add");
		addButton.setBounds(50, 320, 100, 30);
		addButton.setForeground(lightPurple);
		addButton.setBackground(Color.WHITE);
		addButton.setFocusPainted(false);
		addButton.setBorderPainted(true);
		addButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));

		JButton deleteButton = new JButton("Delete");
		deleteButton.setBounds(160, 320, 100, 30);
		deleteButton.setForeground(lightPurple);
		deleteButton.setBackground(Color.WHITE);
		deleteButton.setFocusPainted(false);
		deleteButton.setBorderPainted(true);
		deleteButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));

		JButton editButton = new JButton("Edit");
		editButton.setBounds(270, 320, 100, 30);
		editButton.setForeground(lightPurple);
		editButton.setBackground(Color.WHITE);
		editButton.setFocusPainted(false);
		editButton.setBorderPainted(true);
		editButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));

		JButton backButton = new JButton("Back");
		backButton.setBounds(450, 320, 100, 30);
		backButton.setForeground(Color.WHITE);
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);

		contentPane.add(addButton);
		contentPane.add(deleteButton);
		contentPane.add(editButton);
		contentPane.add(backButton);

		backButton.addActionListener(e -> {
			dispose();
			new Teacher_MainMenu(currentTeacher).setVisible(true);
		});

		addButton.addActionListener(e -> {
			new Teacher_AddQuestion(currentTeacher).setVisible(true);
			dispose();
		});

		deleteButton.addActionListener(e -> {
			new Teacher_DeleteQuestions(currentTeacher).setVisible(true);
			dispose();
		});

		editButton.addActionListener(e -> {
			new Teacher_EditQuestions(currentTeacher).setVisible(true);
			dispose();
		});

		loadQuestionData(teacher);

		setVisible(true);
	}

	private void loadQuestionData(Teacher teacher) {

		model.setRowCount(0);

		for (Question question : Run.AllQuestions) {
			String questionTitle = question.getQuestionTitle();
			String questionLanguage = question.getLanguage();
			String questionDifficulty = question.getDifficulty();
			String questionIBLevel = question.getIBLevel();

			model.addRow(new Object[] { questionTitle, questionDifficulty, questionLanguage, questionIBLevel,
					"View Parameters" });
		}
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public ButtonRenderer() {
			setOpaque(true);
			setForeground(lightPurple);
			setBackground(Color.WHITE);
			setFocusPainted(false);
			setBorderPainted(true);
			setBorder(BorderFactory.createLineBorder(lightPurple, 1));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText((value == null) ? "" : value.toString());
			setForeground(lightPurple);
			setBackground(Color.WHITE);
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		protected JButton button;
		private String label;
		private boolean isPushed;
		private ActionListener actionListener;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(e -> fireEditingStopped());
		}

		public void setActionListener(ActionListener actionListener) {
			this.actionListener = actionListener;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(lightPurple);
				button.setBackground(Color.WHITE);
			}
			label = (value == null) ? "" : value.toString();
			button.setText(label);
			isPushed = true;
			return button;
		}

		@Override
		public Object getCellEditorValue() {
			if (isPushed && actionListener != null) {
				actionListener.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, label));
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

	private void viewQuestionParametersAndTestCases(int row) {
		String questionTitle = (String) model.getValueAt(row, 0);

		Question selectedQuestion = null;
		ArrayList<Question_Parameters> selectedParameters = new ArrayList<>();

		for (Question q : Run.AllQuestions) {
			if (q.getQuestionTitle().equalsIgnoreCase(questionTitle)) {
				selectedQuestion = q;
				selectedParameters.addAll(q.questionParameters);
				break;
			}
		}

		if (selectedQuestion != null) {
			Teacher_ViewQuestionParametersAndTestCases viewParametersFrame = new Teacher_ViewQuestionParametersAndTestCases(
					currentTeacher, selectedQuestion);
			viewParametersFrame.setVisible(true);
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Question not found.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
