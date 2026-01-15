package package_IA;

import java.sql.Connection;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class Run {
	protected static Scanner StringKeyboard = new Scanner(System.in);
	protected static Scanner IntKeyboard = new Scanner(System.in);
	protected static ArrayList<Student> AllStudents = new ArrayList<>();
	protected static ArrayList<Teacher> AllTeachers = new ArrayList<>();
	protected static ArrayList<User> AllUsers = new ArrayList<>();
	protected static ArrayList<Question> AllQuestions = new ArrayList<>();
	private static String url = "jdbc:sqlite:data/CyberDuck.db";
	public static void main(String[] args) throws SQLException {

		loadStudents();
		loadTeachers();
		loadQuestions();
	}

	protected static void loadStudents() throws SQLException {
	    try (Connection conn = DriverManager.getConnection(url);
	            PreparedStatement pstmt1 = conn.prepareStatement("SELECT * FROM Users WHERE role = ?");
	            PreparedStatement pstmt2 = conn.prepareStatement("SELECT * FROM Students WHERE userID = ?")) {
	        
	        // Query to get all users with role 'student'
	        pstmt1.setString(1, "student");
	        ResultSet s_userData = pstmt1.executeQuery();
	        
	        while (s_userData.next()) {
	            // Extract basic user information
	            int userID = s_userData.getInt("userID");
	            String fullName = s_userData.getString("fullName");
	            String email = s_userData.getString("email");
	            String password = s_userData.getString("password");
	            
	            // Query to get student-specific information
	            pstmt2.setInt(1, userID);
	            ResultSet studentData = pstmt2.executeQuery();
	            
	            if (studentData.next()) {
	                // Handle null classID by defaulting to 0
	                int classID = studentData.getInt("classID");
	                if (studentData.wasNull()) {
	                    classID = 0;
	                }
	                
	                
	                // Create new Student object with all information
	                Student tempStudent = new Student(userID, fullName, email, password, "student",
	                        studentData.getInt("studentID"), classID, studentData.getInt("gradeLevel"),
	                        studentData.getString("IBLevel"), studentData.getInt("correctAttempts"),
	                        studentData.getInt("incorrectAttempts"));
	                
	                // Load student's attempt history
	                tempStudent.loadStudentAttempts();
	                
	                // Add to both student and user collections
	                AllStudents.add(tempStudent);
	                AllUsers.add(tempStudent);
	            }
	        }
	    }
	}

	protected static void loadTeachers() throws SQLException {
	    try (Connection conn = DriverManager.getConnection(url);
	            PreparedStatement pstmt1 = conn.prepareStatement("SELECT * FROM Users WHERE role = ?");
	            PreparedStatement pstmt2 = conn.prepareStatement("SELECT * FROM Teachers WHERE userID = ?")) {
	        
	        // Query to get all users with role 'teacher'
	        pstmt1.setString(1, "teacher");
	        ResultSet t_userData = pstmt1.executeQuery();
	        
	        while (t_userData.next()) {
	            // Extract basic user information
	            int userID = t_userData.getInt("userID");
	            String fullName = t_userData.getString("fullName");
	            String email = t_userData.getString("email");
	            String password = t_userData.getString("password");
	            
	            // Query to get teacher-specific information
	            pstmt2.setInt(1, userID);
	            ResultSet teacherData = pstmt2.executeQuery();
	            
	            if (teacherData.next()) {
	                // Create new Teacher object with all information
	                Teacher tempTeacher = new Teacher(userID, fullName, email, password,
	                        teacherData.getInt("teacherID"), "teacher");
	                
	                // Load teacher's associated classes
	                tempTeacher.loadClassestoTeacher();
	                
	                // Add to both teacher and user collections
	                AllTeachers.add(tempTeacher);
	                AllUsers.add(tempTeacher);
	            }
	        }
	    }
	}

	protected static void loadQuestions() throws SQLException {
	    try (Connection conn = DriverManager.getConnection(url);
	            PreparedStatement pstmt1 = conn.prepareStatement("SELECT * FROM Question")) {
	        
	        // Query to get all questions
	        ResultSet questionData = pstmt1.executeQuery();
	        
	        while (questionData.next()) {
	            // Create new Question object with all information
	            Question tempQuestion = new Question(questionData.getInt("questionID"),
	                    questionData.getString("questionTitle"), questionData.getString("language"),
	                    questionData.getString("IBLevel"), questionData.getString("difficulty"),
	                    questionData.getString("questionText"), questionData.getString("solutionHint"),
	                    questionData.getString("returnType"));
	            
	            // Load associated parameters and test cases
	            tempQuestion.loadParametersAndTestCases();
	            
	            // Add to questions collection
	            AllQuestions.add(tempQuestion);
	        }
	    }
	}
	

}