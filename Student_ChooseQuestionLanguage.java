package package_IA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Student_ChooseQuestionLanguage extends JFrame {

    private static final long serialVersionUID = 1L;
	private Student currentStudent;
    private JPanel contentPane;

    public Student_ChooseQuestionLanguage(Student student) {
        this.currentStudent = student;
        initialize();
    }

    private void initialize() {
        Color lightPurple = new Color(173, 153, 255);
        Color lightRed = new Color(252, 157, 142);
    	
    	setTitle("CyberDuck - Student");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);

        //JFrame Code Source (See Crit C Source 11)
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);
        contentPane.setLayout(null); 
		
        JLabel choiceLabel = new JLabel("Choose a Coding Language:");
        choiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        choiceLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        choiceLabel.setForeground(lightPurple);
        choiceLabel.setBounds(0, 50, 600, 40); 
        contentPane.add(choiceLabel);

        JButton pythonButton = new JButton("Python");
        pythonButton.setForeground(lightPurple);
        pythonButton.setFont(new Font("Arial", Font.PLAIN, 18));
        pythonButton.setFocusPainted(false);
        pythonButton.setBorderPainted(true);
        pythonButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));
        pythonButton.setBackground(Color.WHITE);
        pythonButton.setBounds(50, 130, 230, 80); 
        contentPane.add(pythonButton);

        JButton javaButton = new JButton("Java");
        javaButton.setForeground(lightPurple);
        javaButton.setFont(new Font("Arial", Font.PLAIN, 18));
        javaButton.setFocusPainted(false);
        javaButton.setBorderPainted(true);
        javaButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));
        javaButton.setBackground(Color.WHITE);
        javaButton.setBounds(331, 130, 230, 80); 
        contentPane.add(javaButton);

        JButton backButton = new JButton("Back");
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(true);
        backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
        backButton.setBackground(lightRed);
        backButton.setOpaque(true);
        backButton.setBounds(225, 270, 150, 50); 
        contentPane.add(backButton);

        javaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            	new Student_QuestionBank(currentStudent, "Java").setVisible(true);
            }
        });

        pythonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	dispose();
            	 new Student_QuestionBank(currentStudent, "Python").setVisible(true);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Student_MainMenu(currentStudent).setVisible(true);
            }
        });
    }

}
