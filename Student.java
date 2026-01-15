package package_IA;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Student extends User {
    private int studentID;
    private int classID;
    private int gradeLevel;
    private String iBLevel;
    private int correctAttempts;
    private int incorrectAttempts;
    protected ArrayList<Attempt> allAttempts = new ArrayList<>();

    private String url = "jdbc:sqlite:data/CyberDuck.db";
    
    public Student(int userID, String fullName, String email, String password, String role) {
        super(userID, fullName, email, password, role);
    }

    public Student(int userID, String fullName, String email, String password, String role, int studentID, int classID,
            int gradeLevel, String iBLevel, int correctAttempts, int incorrectAttempts) {
        super(userID, fullName, email, password, role);
        this.studentID = studentID;
        this.classID = classID;
        this.gradeLevel = gradeLevel;
        this.iBLevel = iBLevel;
        this.correctAttempts = correctAttempts;
        this.incorrectAttempts = incorrectAttempts;
    }

    public Student(int userID, String fullName, String email, String password, String role, int studentID,
            int gradeLevel, String iBLevel, int correctAttempts, int incorrectAttempts) {
        super(userID, fullName, email, password, role);
        this.studentID = studentID;
        this.classID = 0;
        this.gradeLevel = gradeLevel;
        this.iBLevel = iBLevel;
        this.correctAttempts = correctAttempts;
        this.incorrectAttempts = incorrectAttempts;
    }

    protected void loadStudentAttempts() throws SQLException {
        // Establish database connection
        Connection conn = DriverManager.getConnection(url);
        // Prepare SQL statement to get all attempts for this student
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Attempts WHERE studentID = ?");
        pstmt.setInt(1, this.getStudentID());
        ResultSet attemptData = pstmt.executeQuery();

        // Process each attempt record
        while (attemptData.next()) {
            // Parse the date string from database to LocalDateTime
            String dateString = attemptData.getString("dateCompleted").trim();
            LocalDateTime attemptDate = LocalDateTime.parse(dateString); 
            // Create new Attempt object and add to student's attempt history
            Attempt att = new Attempt(attemptData.getInt("attemptID"), 
                                    attemptData.getInt("studentID"),
                                    attemptData.getInt("questionID"), 
                                    attemptData.getBoolean("is_Correct"), 
                                    attemptDate);
            this.allAttempts.add(att);
        }
        conn.close();
    }

    protected boolean addAttempt(Question question, boolean isCorrect) {
    	//LocalDateTime Source (See Crit C Source 18)
    	//Retrieve current time stamp
        LocalDateTime currentTime = LocalDateTime.now();
        //DateTimeFormatter Source (See Crit C Source 5)
        //Create formatter for time stamp
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        try (Connection conn = DriverManager.getConnection(url)) {
            // Begin transaction
            conn.setAutoCommit(false);

            try {
                // Insert new attempt record
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO Attempts (studentID, questionID, is_Correct, dateCompleted) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

                    // Set parameters for the insert
                    pstmt.setInt(1, this.getStudentID());
                    pstmt.setInt(2, question.getQuestionID());
                    pstmt.setBoolean(3, isCorrect);
                    pstmt.setString(4, currentTime.format(formatter));
                    pstmt.executeUpdate();

                    // Prepare update query based on attempt correctness
                    String updateQuery;
                    if (isCorrect) {
                        updateQuery = "UPDATE Students SET correctAttempts = correctAttempts + 1 WHERE studentID = ?";
                        this.correctAttempts++; 
                    } else {
                        updateQuery = "UPDATE Students SET incorrectAttempts = incorrectAttempts + 1 WHERE studentID = ?";
                        this.incorrectAttempts++; 
                    }

                    // Update student's attempt counts
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, this.studentID);
                        updateStmt.executeUpdate();
                    }

                    // Get the generated attempt ID and create local attempt record
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int attemptID = generatedKeys.getInt(1);
                            Attempt newAttempt = new Attempt(attemptID, this.getStudentID(), 
                                question.getQuestionID(), isCorrect, currentTime);
                            this.allAttempts.add(newAttempt);
                        }
                    }

                    // Verify and sync attempt counts with database
                    try (PreparedStatement verifyStmt = conn.prepareStatement(
                            "SELECT correctAttempts, incorrectAttempts FROM Students WHERE studentID = ?")) {
                        verifyStmt.setInt(1, this.studentID);
                        ResultSet rs = verifyStmt.executeQuery();
                        if (rs.next()) {
                            this.correctAttempts = rs.getInt("correctAttempts");
                            this.incorrectAttempts = rs.getInt("incorrectAttempts");
                        }
                    }

                    // Commit transaction if everything succeeded
                    conn.commit();
                    return true;
                }
            } catch (SQLException e) {
                // Rollback transaction on error
                conn.rollback();
                System.err.println("Error recording attempt: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            // Handle connection errors
            System.err.println("Error recording attempt: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    protected String viewProgress() {
        // Check if student has made any attempts
        if (allAttempts.isEmpty()) {
            return "No attempts recorded yet.";
        }

        // Calculate statistics
        int totalAttempts = allAttempts.size();
        int correctAttempts = 0;
        // Count correct attempts
        for (Attempt attempt : allAttempts) {
            if (attempt.getIs_Correct()) {
                correctAttempts++;
            }
        }
        // Calculate derived statistics
        int incorrectAttempts = totalAttempts - correctAttempts;
        double successRate = (double) correctAttempts / totalAttempts * 100;

        // Build formatted progress report
        StringBuilder progress = new StringBuilder();
        progress.append(String.format("Total attempts: %d\n", totalAttempts));
        progress.append(String.format("Correct attempts: %d\n", correctAttempts));
        progress.append(String.format("Incorrect attempts: %d\n", incorrectAttempts));
        progress.append(String.format("Success rate: %.2f%%\n", successRate));

        return progress.toString();
    }

    protected String viewQuestionProgress(int questionID) {
        // Initialize tracking variables
        int totalAttempts = 0;
        int correctAttempts = 0;
        LocalDateTime mostRecent = null;

        // Analyze attempts for the specified question
        for (Attempt attempt : allAttempts) {
            if (attempt.getQuestionID() == questionID) {
                totalAttempts++;
                if (attempt.getIs_Correct()) {
                    correctAttempts++;
                }
                // Track most recent attempt
                if (mostRecent == null || attempt.getDateCompleted().isAfter(mostRecent)) {
                    mostRecent = attempt.getDateCompleted();
                }
            }
        }

        // Return early if no attempts found for this question
        if (totalAttempts == 0) {
            return String.format("No attempts recorded for question ID: ", questionID);
        }

        // Calculate statistics
        int incorrectAttempts = totalAttempts - correctAttempts;
        double successRate = (double) correctAttempts / totalAttempts * 100;

        // Build detailed progress report
        StringBuilder progress = new StringBuilder();
        progress.append(String.format("Progress for Question ID %d:\n", questionID));
        progress.append(String.format("Total attempts: %d\n", totalAttempts));
        progress.append(String.format("Correct attempts: %d\n", correctAttempts));
        progress.append(String.format("Incorrect attempts: %d\n", incorrectAttempts));
        progress.append(String.format("Success rate: %.2f%%\n", successRate));

        // Add timestamp of most recent attempt if available
        if (mostRecent != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            progress.append(String.format("Most recent attempt: %s\n", mostRecent.format(formatter)));
        }

        return progress.toString();
    }
    
    protected boolean deleteStudent() {
        try (Connection conn = DriverManager.getConnection(url)) {
            // Start transaction
            conn.setAutoCommit(false);

            try {
                // First delete from Students table due to foreign key relationship
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Students WHERE studentID = ?")) {
                    pstmt.setInt(1, this.getStudentID());
                    pstmt.executeUpdate();
                }

                // Then delete the associated user account
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Users WHERE userID = ?")) {
                    pstmt.setInt(1, this.getUserID());
                    pstmt.executeUpdate();
                }

                // Commit transaction if both operations succeed
                conn.commit();
                return true;
            } catch (SQLException e) {
                // Rollback transaction if any operation fails
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            // Handle connection errors
            e.printStackTrace();
            return false;
        }
    }

    protected boolean updateStudentInfo(int newClassID, int newGradeLevel, String newIBLevel, Teacher currentTeacher) {
        // Store original class ID for comparison
        int oldClassID = this.classID;

        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE Students SET ClassID = ?, GradeLevel = ?, IBLevel = ? WHERE StudentID = ?")) {
            
            // Set update parameters
            pstmt.setInt(1, newClassID);
            pstmt.setInt(2, newGradeLevel);
            pstmt.setString(3, newIBLevel);
            pstmt.setInt(4, this.studentID);
            int affectedRows = pstmt.executeUpdate();

            // If database update successful, update local data structures
            if (affectedRows > 0) {
                // Update instance variables
                this.classID = newClassID;
                this.gradeLevel = newGradeLevel;
                this.iBLevel = newIBLevel;

                // Update student in global student list
                int index = Run.AllStudents.indexOf(this);
                if (index != -1) {
                    Run.AllStudents.set(index, this);
                }

                // Update class rosters if student changed classes
                if (oldClassID != newClassID) {
                    for (Class c : currentTeacher.AllClasses) {
                        // Remove from old class
                        if (c.getClassID() == oldClassID) {
                            c.studentList.remove(this);
                        }
                        // Add to new class
                        if (c.getClassID() == newClassID) {
                            c.studentList.add(this);
                        }
                    }
                }

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    protected ArrayList<Question> getRecommendedQuestions(ArrayList<Question> allQuestions) {
        // For new users with no attempts, return all easy questions
        if (allAttempts.isEmpty()) {
            ArrayList<Question> easyQuestions = new ArrayList<>();
            for (Question q : allQuestions) {
                if (q.getDifficulty().equals("Easy")) {
                    easyQuestions.add(q);
                }
            }
            return easyQuestions;
        }

        // Initialize tracking maps for attempts and failures at each difficulty level
        Map<String, Integer> attemptsPerDifficulty = new HashMap<>();
        Map<String, Integer> failsPerDifficulty = new HashMap<>();
        
        // Set initial counts to zero for all difficulty levels
        attemptsPerDifficulty.put("Easy", 0);
        attemptsPerDifficulty.put("Medium", 0);
        attemptsPerDifficulty.put("Hard", 0);
        failsPerDifficulty.put("Easy", 0);
        failsPerDifficulty.put("Medium", 0);
        failsPerDifficulty.put("Hard", 0);

        // Create a map for quick question lookup by ID
        Map<Integer, Question> questionMap = new HashMap<>();
        for (Question q : allQuestions) {
            questionMap.put(q.getQuestionID(), q);
        }

        // Calculate attempt and failure counts for each difficulty level
        for (Attempt attempt : allAttempts) {
            Question question = questionMap.get(attempt.getQuestionID());
            if (question != null) {
                String difficulty = question.getDifficulty();
                attemptsPerDifficulty.put(difficulty, attemptsPerDifficulty.get(difficulty) + 1);
                if (!attempt.getIs_Correct()) {
                    failsPerDifficulty.put(difficulty, failsPerDifficulty.get(difficulty) + 1);
                }
            }
        }

        // Calculate failure rates for each difficulty level
        // If no attempts made at a difficulty level, failure rate is set to 0
        double easyFailRate = attemptsPerDifficulty.get("Easy") == 0 ? 0 : 
            (double) failsPerDifficulty.get("Easy") / attemptsPerDifficulty.get("Easy");
        double mediumFailRate = attemptsPerDifficulty.get("Medium") == 0 ? 0 : 
            (double) failsPerDifficulty.get("Medium") / attemptsPerDifficulty.get("Medium");
        double hardFailRate = attemptsPerDifficulty.get("Hard") == 0 ? 0 : 
            (double) failsPerDifficulty.get("Hard") / attemptsPerDifficulty.get("Hard");

        // Determine recommended difficulty based on performance thresholds
        // Rules: Step down if failure rate > 60% with >= 3 attempts, Step up if failure rate < 30% with >= 5 attempts
        String recommendedDifficulty;
        if (hardFailRate > 0.6 && attemptsPerDifficulty.get("Hard") >= 3) {
            recommendedDifficulty = "Medium";  // Step down from Hard if struggling
        } else if (mediumFailRate > 0.6 && attemptsPerDifficulty.get("Medium") >= 3) {
            recommendedDifficulty = "Easy";    // Step down from Medium if struggling
        } else if (easyFailRate < 0.3 && attemptsPerDifficulty.get("Easy") >= 5) {
            recommendedDifficulty = "Medium";  // Step up from Easy if performing well
        } else if (mediumFailRate < 0.3 && attemptsPerDifficulty.get("Medium") >= 5) {
            recommendedDifficulty = "Hard";    // Step up from Medium if performing well
        } else {
            recommendedDifficulty = "Easy";    // Default to Easy if no conditions are met
        }

        // Filter questions matching the recommended difficulty that haven't been completed
        ArrayList<Question> recommendations = new ArrayList<>();
        for (Question question : allQuestions) {
            if (question.getDifficulty().equals(recommendedDifficulty) && 
                !hasCompletedSuccessfully(question.getQuestionID())) {
                recommendations.add(question);
            }
        }

        // If no questions available at recommended difficulty,find questions from an alternative difficulty level
        if (recommendations.isEmpty()) {
            String additionalDifficulty;
            if (recommendedDifficulty.equals("Hard")) {
                additionalDifficulty = "Medium";  // Step down from Hard
            } else if (recommendedDifficulty.equals("Medium")) {
                additionalDifficulty = "Easy";    // Step down from Medium
            } else {
                additionalDifficulty = "Medium";  // Step up from Easy
            }

            // Add questions from alternative difficulty that haven't been completed
            for (Question question : allQuestions) {
                if (question.getDifficulty().equals(additionalDifficulty) && 
                    !hasCompletedSuccessfully(question.getQuestionID()) &&
                    !recommendations.contains(question)) {
                    recommendations.add(question);
                }
            }
        }

        return recommendations;
    }
    /**
     * Checks if a question has been successfully completed by the user
     * questionId = The ID of the question to check
     * returns true if the question has been completed successfully, false otherwise
     */
    private boolean hasCompletedSuccessfully(int questionId) {
        for (Attempt attempt : getAllAttempts()) {
            if (attempt.getQuestionID() == questionId && attempt.getIs_Correct()) {
                return true;
            }
        }
        return false;
    }
    protected int getStudentID() {
        return studentID;
    }

    protected void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    protected int getClassID() {
        return classID;
    }

    protected void setClassID(int classID) {
        this.classID = classID;
    }

    protected int getGradeLevel() {
        return gradeLevel;
    }

    protected void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    protected String getIBLevel() {
        return iBLevel;
    }

    protected void setIBLevel(String iBLevel) {
    	this.iBLevel = iBLevel;
    }

    protected int getCorrectAttempts() {
        return correctAttempts;
    }

    protected void setCorrectAttempts(int correctAttempts) {
        this.correctAttempts = correctAttempts;
    }

    protected int getIncorrectAttempts() {
        return incorrectAttempts;
    }

    protected void setIncorrectAttempts(int incorrectAttempts) {
        this.incorrectAttempts = incorrectAttempts;
    }

    protected ArrayList<Attempt> getAllAttempts() {
        return allAttempts;
    }

    protected void setAllAttempts(ArrayList<Attempt> allAttempts) {
        this.allAttempts = allAttempts;
    }
}
