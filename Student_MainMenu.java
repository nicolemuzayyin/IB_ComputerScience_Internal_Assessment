package package_IA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.EmptyBorder;

public class Student_MainMenu extends JFrame {
	private static final long serialVersionUID = 1L;
	private Student currentStudent;
	private JPanel contentPane;

	public Student_MainMenu(User user) {

		for (Student s : Run.AllStudents) {
			if (user.getUserID() == s.getUserID()) {
				currentStudent = s;
			}
		}
		
		Color lightRed = new Color(252, 157, 142);
		Color lightPurple = new Color(173, 153, 255);

		setTitle("CyberDuck - Student");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null); 

		JLabel lblWelcome = new JLabel("Welcome " + currentStudent.getFullName() + " !");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setFont(new Font("Arial", Font.PLAIN, 36));
		lblWelcome.setForeground(lightPurple); 
		lblWelcome.setBounds(0, 30, 600, 40);
		contentPane.add(lblWelcome);

		JButton logoutButton = new JButton("Logout");
		logoutButton.setForeground(Color.WHITE); 
		logoutButton.setFont(new Font("Arial", Font.PLAIN, 18));
		logoutButton.setFocusPainted(false);
		logoutButton.setBorderPainted(true);
		logoutButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4)); 
		logoutButton.setBackground(lightRed);
		logoutButton.setOpaque(true);
		logoutButton.setBounds(327, 154, 230, 80);
		contentPane.add(logoutButton);

		logoutButton.addActionListener((ActionListener) new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentStudent.logout();
				//JOptionPane Code Source (See Crit C Source 11)
				JOptionPane.showMessageDialog(Student_MainMenu.this, "You have been logged out successfully.", "Logout",
						JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
		});

		JButton practiceCodingButton = new JButton("Practice Coding");
		practiceCodingButton.setForeground(lightPurple); 
		practiceCodingButton.setFont(new Font("Arial", Font.PLAIN, 18));
		practiceCodingButton.setFocusPainted(false);
		practiceCodingButton.setBorderPainted(true);
		practiceCodingButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1)); 
		practiceCodingButton.setBackground(Color.WHITE);
		practiceCodingButton.setBounds(51, 154, 230, 80);
		contentPane.add(practiceCodingButton);

		practiceCodingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Student_ChooseQuestionLanguage(currentStudent).setVisible(true);
			}
		});
	}
}
