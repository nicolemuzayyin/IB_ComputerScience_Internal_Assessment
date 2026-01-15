package package_IA;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Teacher_AddQuestionText extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    static Color lightPurple = new Color(173, 153, 255);
    static Color lightRed = new Color(252, 157, 142);
	static Color lightGreen = new Color(171, 238, 157);

    public Teacher_AddQuestionText(String questionTitle, String difficulty, String ibLevel, 
                                 String language, String solutionHints, String returnType, 
                                 int numberOfParameters, Teacher currentTeacher) {

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

        JTextArea txtQuestionText = new JTextArea();
        txtQuestionText.setFont(new Font("Arial", Font.PLAIN, 14));
        txtQuestionText.setForeground(lightPurple);
        txtQuestionText.setBorder(BorderFactory.createLineBorder(lightPurple));
        txtQuestionText.setBounds(50, 80, 700, 350);
        txtQuestionText.setLineWrap(true);
        txtQuestionText.setWrapStyleWord(true);
        
        txtQuestionText.setText("Enter Question Text");
        txtQuestionText.setForeground(Color.GRAY);
        txtQuestionText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtQuestionText.getText().equals("Enter Question Text")) {
                    txtQuestionText.setText("");
                    txtQuestionText.setForeground(lightPurple);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtQuestionText.getText().isEmpty()) {
                    txtQuestionText.setText("Enter Question Text");
                    txtQuestionText.setForeground(Color.GRAY);
                }
            }
        });
        contentPane.add(txtQuestionText);

        JButton btnContinue = new JButton("Continue");
        btnContinue.setFont(new Font("Arial", Font.PLAIN, 14));
        btnContinue.setForeground(Color.WHITE);
        btnContinue.setBackground(lightGreen);
        btnContinue.setFocusPainted(false);
        btnContinue.setBorderPainted(true);
        btnContinue.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
        btnContinue.setOpaque(true);
        btnContinue.setBounds(250, 470, 100, 35);
        btnContinue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String questionText = txtQuestionText.getText();
                if (questionText.equals("Enter Question Text") || questionText.trim().isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(null, 
                        "Please enter the question text", 
                        "Input Error", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Question newQuestion = Question.createNewQuestion(questionTitle, difficulty, ibLevel, language, solutionHints, returnType, questionText);
                new Teacher_AddNewQuestionParameters(numberOfParameters, newQuestion, currentTeacher);
                dispose();
            }
        });
        contentPane.add(btnContinue);

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(lightRed);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(true);
        btnBack.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
        btnBack.setOpaque(true);
        btnBack.setBounds(450, 470, 100, 35);
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(btnBack);

        setLocationRelativeTo(null);
        setVisible(true);
    }

}
