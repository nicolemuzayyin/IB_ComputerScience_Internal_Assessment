package package_IA;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Color;

public class Teacher_ViewQuestionParametersAndTestCases extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable parametersTable;
	private JTable testCasesTable;
	private Teacher currentTeacher;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);
	private Question question;
	private ArrayList<Question_Parameters> questionParameters;
	private ArrayList<Test_Case> testCases;

	public Teacher_ViewQuestionParametersAndTestCases(Teacher teacher, Question question) {
		this.currentTeacher = teacher;
		this.question = question;
		this.questionParameters = question.questionParameters;
		this.testCases = question.question_testCases;
		initialize();
	}

	private void initialize() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 700);
		setTitle("Question Parameters and Test Cases");

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblTitle = new JLabel("Question Parameters and Test Cases");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Arial", Font.PLAIN, 24));
		lblTitle.setForeground(lightPurple);
		lblTitle.setBounds(0, 20, 800, 30);
		contentPane.add(lblTitle);

		JLabel lblQuestionInfo = new JLabel(
				"Question ID: " + question.getQuestionID() + " - " + question.getQuestionText());
		lblQuestionInfo.setFont(new Font("Arial", Font.PLAIN, 14));
		lblQuestionInfo.setForeground(lightPurple);
		lblQuestionInfo.setBounds(50, 60, 700, 25);
		contentPane.add(lblQuestionInfo);

		JLabel lblParameters = new JLabel("Parameters:");
		lblParameters.setFont(new Font("Arial", Font.PLAIN, 14));
		lblParameters.setForeground(lightPurple);
		lblParameters.setBounds(50, 95, 700, 25);
		contentPane.add(lblParameters);

		String[] paramColumnNames = { "Parameter Variable", "Data Type" };
		DefaultTableModel paramModel = new DefaultTableModel(paramColumnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		for (Question_Parameters param : questionParameters) {
			paramModel.addRow(new Object[] { param.getParameterVariable(), param.getDataType() });
		}

		parametersTable = new JTable(paramModel);
		parametersTable.setFont(new Font("Arial", Font.PLAIN, 12));
		parametersTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 12));
		parametersTable.setBackground(Color.WHITE);
		parametersTable.setForeground(lightPurple);
		parametersTable.setGridColor(lightPurple);
		parametersTable.getTableHeader().setBackground(lightPurple);
		parametersTable.getTableHeader().setForeground(Color.WHITE);

		JScrollPane paramScrollPane = new JScrollPane(parametersTable);
		paramScrollPane.setBounds(50, 130, 700, 200);
		contentPane.add(paramScrollPane);

		JLabel lblTestCases = new JLabel("Test Cases:");
		lblTestCases.setFont(new Font("Arial", Font.PLAIN, 14));
		lblTestCases.setForeground(lightPurple);
		lblTestCases.setBounds(50, 340, 700, 25);
		contentPane.add(lblTestCases);

		String[] testCaseColumnNames = { "Parameter Variable", "Parameter Value", "Parameter Data Type(s)",
				"Expected Value" };
		DefaultTableModel testCaseModel = new DefaultTableModel(testCaseColumnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		for (Test_Case testCase : testCases) {
		    Map<String, Object> parameterValues = testCase.getParameters();

		    // Combine parameter names
		    StringBuilder paramNames = new StringBuilder();
		    StringBuilder paramValues = new StringBuilder();
		    StringBuilder paramTypes = new StringBuilder();

		    boolean first = true;
		    for (Map.Entry<String, Object> entry : parameterValues.entrySet()) {
		        if (!first) {
		            paramNames.append(", ");
		            paramValues.append(", ");
		            paramTypes.append(", ");
		        }
		        
		        String paramName = entry.getKey();
		        Object paramValue = entry.getValue();
		        
		        // Find the corresponding parameter definition to get its data type
		        String dataType = "";
		        for (Question_Parameters param : questionParameters) {
		            if (param.getParameterVariable().equals(paramName)) {
		                dataType = param.getDataType();
		                break;
		            }
		        }

		        paramNames.append(paramName);
		        paramValues.append(paramValue.toString());
		        paramTypes.append(dataType);
		        first = false;
		    }

		    testCaseModel.addRow(new Object[] { paramNames.toString(), paramValues.toString(), paramTypes.toString(), testCase.getExpectedValue() 
		    });
		}

		testCasesTable = new JTable(testCaseModel);
		testCasesTable.setFont(new Font("Arial", Font.PLAIN, 12));
		testCasesTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 12));
		testCasesTable.setBackground(Color.WHITE);
		testCasesTable.setForeground(lightPurple);
		testCasesTable.setGridColor(lightPurple);
		testCasesTable.getTableHeader().setBackground(lightPurple);
		testCasesTable.getTableHeader().setForeground(Color.WHITE);

		JScrollPane testCaseScrollPane = new JScrollPane(testCasesTable);
		testCaseScrollPane.setBounds(50, 375, 700, 200);
		contentPane.add(testCaseScrollPane);

		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
		btnBack.setForeground(Color.WHITE);
		btnBack.setBackground(lightRed);
		btnBack.setFocusPainted(false);
		btnBack.setBorderPainted(true);
		btnBack.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		btnBack.setBounds(350, 600, 100, 30);
		btnBack.setOpaque(true);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Teacher_QuestionInfo(currentTeacher).setVisible(true);
			}
		});
		contentPane.add(btnBack);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}
