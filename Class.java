package package_IA;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Class {

	private String url = "jdbc:sqlite:data/CyberDuck.db";
	private int classID;
	private int teacherID;
	private String className;
	protected ArrayList<Student> studentList = new ArrayList<Student>();

	public Class(int classID, int teacherID, String className, ArrayList<Student> studentList) {
		super();
		this.classID = classID;
		this.teacherID = teacherID;
		this.className = className;
		this.studentList = studentList;
	}

	// Method to access information to view Class Progress in GUI
	protected String viewClassProg() {
	    // Initialize StringBuilder for report
	    StringBuilder progress = new StringBuilder();
	    
	    // Add header information
	    progress.append("Class Progress Report for Class ID: ").append(classID).append("\n");
	    progress.append("Teacher ID: ").append(teacherID).append("\n\n");

	    // Initialize counters
	    int totalStudents = studentList.size();
	    int totalCorrectAttempts = 0;
	    int totalIncorrectAttempts = 0;

	    // Process each student's progress
	    for (Student student : studentList) {
	        progress.append("Student ID: ").append(student.getStudentID()).append("\n");

	        // Get student's attempt statistics
	        int studentCorrectAttempts = student.getCorrectAttempts();
	        int studentIncorrectAttempts = student.getIncorrectAttempts();
	        int studentTotalQuestions = studentCorrectAttempts + studentIncorrectAttempts;

	        // Update class totals
	        totalCorrectAttempts += studentCorrectAttempts;
	        totalIncorrectAttempts += studentIncorrectAttempts;

	        // Calculate student's success percentage
	        double studentProgress = 0;
	        if (studentTotalQuestions > 0) {
	            studentProgress = (double) studentCorrectAttempts / studentTotalQuestions * 100;
	        }

	        // Add student's progress statistics
	        progress.append("Progress: ").append(String.format("%.2f", studentProgress)).append("% (")
	                .append(studentCorrectAttempts).append("/").append(studentTotalQuestions)
	                .append(" questions correct)\n");

	        // Add detailed student progress
	        progress.append(student.viewProgress()).append("\n");
	    }

	    // Calculate overall class statistics
	    int totalAttempts = totalCorrectAttempts + totalIncorrectAttempts;
	    double overallProgress = (totalAttempts > 0) ? (double) totalCorrectAttempts / totalAttempts * 100 : 0;

	    // Add summary section
	    progress.append("\nOverall Class Progress:\n");
	    progress.append("Total Questions Attempted: ").append(totalAttempts).append("\n");
	    progress.append("Total Correct Questions: ").append(totalCorrectAttempts).append("\n");
	    progress.append("Total Incorrect Questions: ").append(totalIncorrectAttempts).append("\n");
	    progress.append("Overall Success Rate: ").append(String.format("%.2f", overallProgress)).append("%\n");
	    progress.append("Total Students: ").append(totalStudents);

	    // Return progress text to GUI
	    return progress.toString();
	}

	// Method to add an existing student to a class
	protected void addExistingStudentToClass(Student selectedStudent) {
	    // Check for null student
	    if (selectedStudent == null) {
	        throw new IllegalArgumentException("Selected student cannot be null");
	    }

	    try (Connection conn = DriverManager.getConnection(url)) {
	        // Start transaction
	        conn.setAutoCommit(false);
	        try {
	            // Get userID from Users table in CyberDuck database
	            int userID;
	            try (PreparedStatement pstmt = conn.prepareStatement("SELECT userID FROM Users WHERE fullName = ?")) {
	                pstmt.setString(1, selectedStudent.getFullName());
	                ResultSet rs = pstmt.executeQuery();
	                if (rs.next()) {
	                    userID = rs.getInt("userID");
	                    System.out.println("Found UserID: " + userID);
	                } else {
	                    throw new SQLException("User not found with name: " + selectedStudent.getFullName());
	                }
	            }

	            // Get studentID from Students table in CyberDuck database
	            int studentID;
	            try (PreparedStatement pstmt = conn
	                    .prepareStatement("SELECT studentID FROM Students WHERE userID = ?")) {
	                pstmt.setInt(1, userID);
	                ResultSet rs = pstmt.executeQuery();
	                if (rs.next()) {
	                    studentID = rs.getInt("studentID");
	                    System.out.println("Found StudentID: " + studentID);
	                } else {
	                    throw new SQLException("Student not found for UserID: " + userID);
	                }
	            }

	            // Update student's classID in database
	            try (PreparedStatement pstmt = conn
	                    .prepareStatement("UPDATE Students SET classID = ? WHERE studentID = ?")) {
	                pstmt.setInt(1, this.getClassID());
	                pstmt.setInt(2, studentID);
	                int affectedRows = pstmt.executeUpdate();
	                if (affectedRows == 0) {
	                    throw new SQLException("Updating student failed, no rows affected.");
	                }
	                System.out.println("Updated classID to " + this.getClassID() + " for StudentID: " + studentID);
	            }

	            // Get updated student info and update Student object and add to Class's student list
	            try (PreparedStatement pstmt2 = conn.prepareStatement("SELECT u.fullName, u.email, u.password, "
	                    + "s.gradeLevel, s.IBLevel, s.correctAttempts, s.incorrectAttempts "
	                    + "FROM Users u JOIN Students s ON u.userID = s.userID " + "WHERE s.studentID = ?")) {
	                pstmt2.setInt(1, studentID);
	                ResultSet rs = pstmt2.executeQuery();
	                if (rs.next()) {
	                    Student updatedStudent = new Student(userID, rs.getString("fullName"), rs.getString("email"),
	                            rs.getString("password"), "student", studentID, rs.getInt("gradeLevel"),
	                            rs.getString("IBLevel"), rs.getInt("correctAttempts"),
	                            rs.getInt("incorrectAttempts"));
	                    updatedStudent.setClassID(this.getClassID());
	                    this.studentList.add(updatedStudent);
	                    Run.AllStudents.add(selectedStudent);
	                    System.out.println("Added student to class list: " + updatedStudent.getFullName());
	                }
	            }

	            // Commit the transaction
	            conn.commit();
	            System.out.println("Transaction committed successfully");

	        } catch (SQLException e) {
	            // Rollback on error
	            conn.rollback();
	            System.err.println("Error occurred: " + e.getMessage());
	            throw e;
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException("Error adding student to class: " + e.getMessage(), e);
	    }
	}

	// Method to add a new student to the class with provided details
	protected void addNewStudentToClass(String fullName, String email, String password, int gradeLevel, String ibLevel)
	        throws SQLException {
	    
	    // Validate input parameters to hold necessary information
	    if (fullName == null || email == null || password == null || ibLevel == null) {
	        throw new IllegalArgumentException("Required fields cannot be null");
	    }

	    try (Connection conn = DriverManager.getConnection(url)) {
	        // Start transaction
	        conn.setAutoCommit(false);
	        try {
	            // Check if email already exists
	            try (PreparedStatement pstmt = conn.prepareStatement("SELECT userID FROM Users WHERE email = ?")) {
	                pstmt.setString(1, email);
	                ResultSet rs = pstmt.executeQuery();
	                if (rs.next()) {
	                    throw new SQLException("User with this email already exists");
	                }
	            }

	            // Argon2 Source (See Crit C Source 8)
	            Argon2 argon2 = Argon2Factory.create();
	            String hashedPassword = argon2.hash(10, 65536, 1, password.toCharArray());

	            // Create new user and get userID
	            int newUserID;
	            try (PreparedStatement pstmt2 = conn.prepareStatement(
	                    "INSERT INTO Users (fullName, email, password, role) VALUES (?, ?, ?, 'student')",
	                    Statement.RETURN_GENERATED_KEYS)) {
	                pstmt2.setString(1, fullName);
	                pstmt2.setString(2, email);
	                pstmt2.setString(3, hashedPassword);
	                System.out.println(fullName);
	                pstmt2.executeUpdate();
	                
	                // Get generated userID
	                try (ResultSet rs = pstmt2.getGeneratedKeys()) {
	                    if (rs.next()) {
	                        newUserID = rs.getInt(1);
	                    } else {
	                        throw new SQLException("Creating user failed, no ID obtained.");
	                    }
	                }
	            }

	            // Create student record and get studentID
	            int newStudentID;
	            try (PreparedStatement pstmt3 = conn.prepareStatement(
	                    "INSERT INTO Students (userID, classID, gradeLevel, IBLevel, correctAttempts, incorrectAttempts) "
	                            + "VALUES (?, ?, ?, ?, 0, 0)",
	                    Statement.RETURN_GENERATED_KEYS)) {
	                pstmt3.setInt(1, newUserID);
	                pstmt3.setInt(2, this.getClassID());
	                pstmt3.setInt(3, gradeLevel);
	                pstmt3.setString(4, ibLevel);

	                int studentRows = pstmt3.executeUpdate();
	                if (studentRows == 0) {
	                    throw new SQLException("Failed to create student record");
	                }

	                // Get generated studentID
	                try (ResultSet rs = pstmt3.getGeneratedKeys()) {
	                    if (rs.next()) {
	                        newStudentID = rs.getInt(1);
	                        System.out.println("Created new student with ID: " + newStudentID);
	                    } else {
	                        throw new SQLException("Creating student failed, no ID obtained.");
	                    }
	                }
	            }

	            // Create Student object and add to Class Object's list of Student Objects
	            Student newStudent = new Student(newUserID, fullName, email, hashedPassword, "student", newStudentID,this.getClassID(),
	                    gradeLevel, ibLevel, 0, 0);
	            this.studentList.add(newStudent);
	            Run.AllStudents.add(newStudent);

	            // Commit transaction
	            conn.commit();

	        } catch (SQLException e) {
	            // Rollback on error
	            conn.rollback();
	            System.err.println("Error occurred: " + e.getMessage());
	            throw e;
	        }
	    }
	}
	protected int getClassID() {
		return classID;
	}

	protected void setClassID(int classID) {
		this.classID = classID;
	}

	protected int getTeacherID() {
		return teacherID;
	}

	protected void setTeacherID(int teacherID) {
		this.teacherID = teacherID;
	}

	protected ArrayList<Student> getStudentList() {
		return studentList;
	}

	protected void setStudentList(ArrayList<Student> studentList) {
		this.studentList = studentList;
	}

	protected String getClassName() {
		return className;
	}

	protected void setClassName(String className) {
		this.className = className;
	}

}
