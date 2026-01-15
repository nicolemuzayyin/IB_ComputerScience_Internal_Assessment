package package_IA;

import java.time.LocalDateTime;

public class Attempt {

	private int attemptID;
	private int studentID;
	private int questionID;
	private boolean is_Correct;
	private LocalDateTime dateCompleted;

	public Attempt(int attemptID, int studentID, int questionID, boolean is_Correct, LocalDateTime dateCompleted) {
		super();
		this.attemptID = attemptID;
		this.studentID = studentID;
		this.questionID = questionID;
		this.is_Correct = is_Correct;
		this.dateCompleted = dateCompleted;
	}

	protected int getAttemptID() {
		return attemptID;
	}

	protected void setAttemptID(int attemptID) {
		this.attemptID = attemptID;
	}

	protected int getStudentID() {
		return studentID;
	}

	protected void setStudentID(int studentID) {
		this.studentID = studentID;
	}

	protected int getQuestionID() {
		return questionID;
	}

	protected void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	protected boolean getIs_Correct() {
		return is_Correct;
	}

	protected void setIs_Correct(boolean is_Correct) {
		this.is_Correct = is_Correct;
	}

	protected LocalDateTime getDateCompleted() {
		return dateCompleted;
	}

	protected void setDateCompleted(LocalDateTime dateCompleted) {
		this.dateCompleted = dateCompleted;
	}
	

}
