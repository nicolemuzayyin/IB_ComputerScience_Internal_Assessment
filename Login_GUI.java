package package_IA;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Login_GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginButton;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightGreen = new Color(171, 238, 157);

	public Login_GUI() {
		//JFrame Source (See Crit C Source 10)
		setTitle("CyberDuck");
		setSize(400, 300);
		//Terminates program when window is closed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
		mainPanel.setBackground(new Color(240, 240, 240));

		JLabel titleLabel = new JLabel("LOGIN");
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setForeground(lightPurple);
		mainPanel.add(titleLabel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		JLabel usernameLabel = new JLabel("Enter Username: ");
		usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		usernameLabel.setForeground(lightPurple);
		mainPanel.add(usernameLabel);
		usernameField = new JTextField(20);
		usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		usernameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		mainPanel.add(usernameField);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		JLabel passwordLabel = new JLabel("Enter Password: ");
		passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		passwordLabel.setForeground(lightPurple);
		mainPanel.add(passwordLabel);
		passwordField = new JPasswordField(20);
		passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
		passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		mainPanel.add(passwordField);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		loginButton = new JButton("Login Now");
		loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		loginButton.setBackground(lightGreen); 
		loginButton.setForeground(Color.WHITE);
		loginButton.setOpaque(true);
		loginButton.setFocusPainted(false);
		loginButton.setBorder(BorderFactory.createLineBorder(lightPurple, 3)); ;
		loginButton.addActionListener(new LoginButtonListener());
		mainPanel.add(loginButton);

		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	private static void initializeData() throws SQLException {
		//Calls the Run Class methods that create Objects from Database information 
		Run.loadStudents();
		Run.loadTeachers();
		Run.loadQuestions();
	}

	// Login Button Action Listener method
	private class LoginButtonListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Get user input from the text fields to retrieve username/email and password
	        String email = usernameField.getText();
	        String password = new String(passwordField.getPassword());

	        // Attempt to login and get user object --> calling login method from User class
	        User loggedInUser = User.login(email, password);

	        // Check if login was successful
	        if (loggedInUser != null) {
	            // Show success message
	        	//JOptionPane Source (See Crit C Source 11)
	            JOptionPane.showMessageDialog(Login_GUI.this, "Login successful!");
	            
	            // Route to appropriate menu based on user role
	            if ("student".equals(loggedInUser.getRole())) {
	                // Create and show student menu
	                Student_MainMenu studentMenu = new Student_MainMenu(loggedInUser);
	                studentMenu.setVisible(true);
	                dispose(); // Close login window
	            } else if ("teacher".equals(loggedInUser.getRole())) {
	                // Create and show teacher menu
	                Teacher_MainMenu teacherMenu = new Teacher_MainMenu(loggedInUser);
	                teacherMenu.setVisible(true);
	                dispose(); // Close login window
	            }
	        } else {
	            // Show error message if login failed
	            JOptionPane.showMessageDialog(Login_GUI.this, 
	                "Login failed. Please check your email and password.", 
	                "Login Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	//Main method --> starts the program
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				//Open login GUI upon program start-up
				Login_GUI loginGUI = new Login_GUI();
				loginGUI.setVisible(true);
				try {
					initializeData();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
