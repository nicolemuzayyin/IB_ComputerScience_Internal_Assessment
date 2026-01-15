package package_IA;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Teacher extends User {

	private int teacherID;
	private String url = "jdbc:sqlite:data/CyberDuck.db";
	protected ArrayList<Class> AllClasses = new ArrayList<>();

	public Teacher(int userID, String fullName, String email, String password, int teacherID, String role) {
		super(userID, fullName, email, password, role);
		this.teacherID = teacherID;

	}

	protected void loadClassestoTeacher() throws SQLException {		
		Connection conn = DriverManager.getConnection(url);
		PreparedStatement pstmt1 = conn.prepareStatement("SELECT * FROM Classes WHERE teacherID = ?");
		pstmt1.setInt(1, this.getTeacherID());

		ResultSet classesData = pstmt1.executeQuery();
		while (classesData.next()) {
			ArrayList<Student> classStudents = new ArrayList<>();
			PreparedStatement pstmt2 = conn.prepareStatement("SELECT * FROM Students WHERE classID = ?");
			int classID = classesData.getInt("classID");
			pstmt2.setInt(1, classID);
			ResultSet class_studentData = pstmt2.executeQuery();
			while (class_studentData.next()) {
				int studentID = class_studentData.getInt("studentID");
				for (Student student : Run.AllStudents) {
					if (student.getStudentID() == studentID) {
						classStudents.add(student);
						break;
					}
				}
			}

			Class cl = new Class(classID, classesData.getInt("teacherID"), classesData.getString("className"),
					classStudents);
			this.AllClasses.add(cl);

		}
		conn.close();
	}

	protected void addClassToTeacher(String className, ArrayList<Student> selectedStudents) throws SQLException {

		Connection conn = DriverManager.getConnection(url);
		conn.setAutoCommit(false);

		PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Classes (className, teacherID) VALUES (?, ?)",
				PreparedStatement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, className);
		pstmt.setInt(2, this.getTeacherID());

		int affectedRows = pstmt.executeUpdate();

		if (affectedRows > 0) {
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				int classID = generatedKeys.getInt(1);

				Class newClass = new Class(classID, this.getTeacherID(), className, new ArrayList<>());
				this.AllClasses.add(newClass);

				PreparedStatement updateStmt = conn
						.prepareStatement("UPDATE Students SET classID = ? WHERE studentID = ?");
				for (Student student : selectedStudents) {
					updateStmt.setInt(1, classID);
					updateStmt.setInt(2, student.getStudentID());
					updateStmt.executeUpdate();

					student.setClassID(classID);
					newClass.studentList.add(student);
				}

				conn.commit();
				conn.close();
			}
		}

	}

	protected void deleteClasses(ArrayList<String> classNamesToDelete) throws SQLException {

		Connection conn = DriverManager.getConnection(url);
		conn.setAutoCommit(false);
		String deleteClassSQL = "DELETE FROM Classes WHERE className = ? AND teacherID = ?";
		String deleteStudentsSQL = "UPDATE Students SET classID = NULL WHERE classID IN (SELECT classID FROM Classes WHERE className = ? AND teacherID = ?)";

		PreparedStatement deleteClassStmt = conn.prepareStatement(deleteClassSQL);
		PreparedStatement deleteStudentsStmt = conn.prepareStatement(deleteStudentsSQL);

		for (String className : classNamesToDelete) {
			Class classToDelete = null;
			for (Class c : this.AllClasses) {
				if (c.getClassName().equalsIgnoreCase(className)) {
					classToDelete = c;
					break;
				}
			}

			if (classToDelete != null) {
				for (Student student : classToDelete.studentList) {
					student.setClassID(0);
				}
			}

			deleteStudentsStmt.setString(1, className);
			deleteStudentsStmt.setInt(2, this.getTeacherID());
			deleteStudentsStmt.executeUpdate();

			deleteClassStmt.setString(1, className);
			deleteClassStmt.setInt(2, this.getTeacherID());
			deleteClassStmt.executeUpdate();

			AllClasses.removeIf(c -> c.getClassName().equalsIgnoreCase(className));

			for (Teacher t : Run.AllTeachers) {
				if (t.getTeacherID() == this.getTeacherID()) {
					t.AllClasses.removeIf(c -> c.getClassName().equalsIgnoreCase(className));
					break;
				}
			}
		}

		conn.commit();
		conn.close();

	}

	protected void editClasses(Class selectedClass, ArrayList<String> studentsToRemove) throws SQLException {
		Connection conn = DriverManager.getConnection(url);
		PreparedStatement pstmt = conn.prepareStatement(
				"UPDATE Students SET classID = NULL WHERE userID = (SELECT userID FROM Users WHERE fullName = ?)");
		conn.setAutoCommit(false);
		if (selectedClass == null) {
			throw new IllegalArgumentException("Selected class cannot be null");
		}
		for (String studentName : studentsToRemove) {
			Student student = findStudentByName(studentName);
			if (student != null) {
				student.setClassID(0);
				selectedClass.studentList.remove(student);
			}
		}

		for (String studentName : studentsToRemove) {
			pstmt.setString(1, studentName);
			pstmt.addBatch();

			Student student = findStudentByName(studentName);
			if (student != null) {
				student.setClassID(0);
				selectedClass.studentList.remove(student);
			}
		}

		pstmt.executeBatch();
		conn.commit(); 

	}

	private Student findStudentByName(String studentName) {
		for (Student student : Run.AllStudents) {
			if (student.getFullName().equals(studentName)) {
				return student;
			}
		}
		return null;
	}

	protected void addQuestion(Question question) throws SQLException {
		Connection conn = DriverManager.getConnection(url);
		PreparedStatement pstmt = conn.prepareStatement(
				"INSERT INTO Questions (questionTitle, language, IBLevel, difficulty, questionText, solutionHint, returnType) VALUES (?, ?, ?, ?, ?, ?, ?)");

		pstmt.setString(1, question.getQuestionTitle());
		pstmt.setString(2, question.getLanguage());
		pstmt.setString(3, question.getIBLevel());
		pstmt.setString(4, question.getDifficulty());
		pstmt.setString(5, question.getQuestionText());
		pstmt.setString(6, question.getSolutionHints());
		pstmt.setString(7, question.getReturnType());

		int affectedRows = pstmt.executeUpdate();

		if (affectedRows > 0) {
			System.out.println("Question added successfully.");
		} else {
			System.out.println("Failed to add question.");
		}

	}

	protected void modifyQuestion(int questionID, Question updatedQuestion) throws SQLException {
		Connection conn = DriverManager.getConnection(url);
		PreparedStatement pstmt = conn.prepareStatement(
				"UPDATE Questions SET questionTitle = ?, language = ?, IBLevel = ?, difficulty = ?, questionText = ?, solutionHint = ?, returnType = ? WHERE questionID = ?");

		for (Question q : Run.AllQuestions) {
			if (q.getQuestionID() == questionID) {
				q.setQuestionTitle(updatedQuestion.getQuestionTitle());
				q.setLanguage(updatedQuestion.getLanguage());
				q.setIBLevel(updatedQuestion.getIBLevel());
				q.setDifficulty(updatedQuestion.getDifficulty());
				q.setQuestionText(updatedQuestion.getQuestionText());
				q.setSolutionHints(updatedQuestion.getSolutionHints());
				q.setReturnType(updatedQuestion.getReturnType());
			}
		}
		pstmt.setString(1, updatedQuestion.getQuestionTitle());
		pstmt.setString(2, updatedQuestion.getLanguage());
		pstmt.setString(3, updatedQuestion.getIBLevel());
		pstmt.setString(4, updatedQuestion.getDifficulty());
		pstmt.setString(5, updatedQuestion.getQuestionText());
		pstmt.setString(6, updatedQuestion.getSolutionHints());
		pstmt.setString(7, updatedQuestion.getReturnType());
		pstmt.setInt(8, questionID);

		pstmt.executeUpdate();

	}

	protected int getTeacherID() {
		return teacherID;
	}

	protected void setTeacherID(int teacherID) {
		this.teacherID = teacherID;
	}

	protected ArrayList<Class> getAllClasses() {
		return AllClasses;
	}

	protected void setAllClasses(ArrayList<Class> allClasses) {
		AllClasses = allClasses;
	}

}
