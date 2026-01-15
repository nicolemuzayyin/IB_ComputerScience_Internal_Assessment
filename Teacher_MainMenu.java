package package_IA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.EmptyBorder;

public class Teacher_MainMenu extends JFrame {
    private static final long serialVersionUID = 1L;
    private Teacher currentTeacher;
    private JPanel contentPane;

    public Teacher_MainMenu(User user)  {
        for (Teacher t : Run.AllTeachers) {
        	if (user.getUserID() == t.getUserID()) {
        		currentTeacher = t;
        	}
        }

        Color lightPurple = new Color(173, 153, 255);
        Color lightRed = new Color(252, 157, 142);

        setTitle("CyberDuck - Teacher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);
        contentPane.setLayout(null); 

        JLabel lblWelcome = new JLabel("Welcome " + currentTeacher.getFullName() + " !");
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.PLAIN, 36));
        lblWelcome.setForeground(lightPurple);
        lblWelcome.setBounds(0, 20, 600, 40);
        contentPane.add(lblWelcome);

        JButton manageStudentsButton = new JButton("Manage Students");
        manageStudentsButton.setForeground(lightPurple);
        manageStudentsButton.setFont(new Font("Arial", Font.PLAIN, 18));
        manageStudentsButton.setFocusPainted(false);
        manageStudentsButton.setBorderPainted(true);
        manageStudentsButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));
        manageStudentsButton.setBackground(Color.WHITE);
        manageStudentsButton.setBounds(50, 110, 230, 80);
        contentPane.add(manageStudentsButton);
        
        manageStudentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Teacher_StudentInfo Teacher_StudentInfoFrame = new Teacher_StudentInfo(currentTeacher);
                Teacher_StudentInfoFrame.setVisible(true);
                dispose(); 
            }
        });
        
        
        JButton manageClassesButton = new JButton("Manage Classes");
        manageClassesButton.setForeground(lightPurple);
        manageClassesButton.setFont(new Font("Arial", Font.PLAIN, 18));
        manageClassesButton.setFocusPainted(false);
        manageClassesButton.setBorderPainted(true);
        manageClassesButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));
        manageClassesButton.setBackground(Color.WHITE);
        manageClassesButton.setBounds(320, 110, 230, 80);
        contentPane.add(manageClassesButton);
        
        manageClassesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Teacher_ClassInfo Teacher_ClassInfoFrame = new Teacher_ClassInfo(currentTeacher);
                Teacher_ClassInfoFrame.setVisible(true);
                dispose(); 
            }
        });
        
        JButton manageQuestionsButton = new JButton("Manage Questions");
        manageQuestionsButton.setForeground(lightPurple);
        manageQuestionsButton.setFont(new Font("Arial", Font.PLAIN, 18));
        manageQuestionsButton.setFocusPainted(false);
        manageQuestionsButton.setBorderPainted(true);
        manageQuestionsButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));
        manageQuestionsButton.setBackground(Color.WHITE);
        manageQuestionsButton.setBounds(50, 219, 230, 80);
        contentPane.add(manageQuestionsButton);
        manageQuestionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Teacher_QuestionInfo Teacher_QuestionInfoFrame = new Teacher_QuestionInfo(currentTeacher);
            	Teacher_QuestionInfoFrame.setVisible(true);            	
            	dispose();
            }
        });
        
        JButton LogoutButton = new JButton("Logout");
        LogoutButton.setForeground(Color.WHITE);
        LogoutButton.setFont(new Font("Arial", Font.PLAIN, 18));
        LogoutButton.setFocusPainted(false);
        LogoutButton.setBorderPainted(true);
        LogoutButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
        LogoutButton.setBackground(lightRed);
        LogoutButton.setOpaque(true);
        LogoutButton.setBounds(320, 219, 230, 80);
        contentPane.add(LogoutButton);
        
        LogoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentTeacher.logout();
                JOptionPane.showMessageDialog(Teacher_MainMenu.this, "You have been logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        });
        
        

        setVisible(true);
    }
}
