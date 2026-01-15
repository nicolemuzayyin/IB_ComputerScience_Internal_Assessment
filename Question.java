package package_IA;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import org.python.util.PythonInterpreter;
import org.python.core.PyObject;
import org.python.core.PyException;
import com.google.gson.JsonElement;

public class Question {

	private int questionID;
	private String questionTitle;
	private String language;
	private String iBLevel;
	private String difficulty;
	private String questionText;
	private String solutionHints;
	private String returnType;
	private static String url = "jdbc:sqlite:data/CyberDuck.db";
	ArrayList<Question_Parameters> questionParameters = new ArrayList<>();
	ArrayList<Test_Case> question_testCases = new ArrayList<>();

	static Scanner StringKeyboard = new Scanner(System.in);
	static Scanner IntKeyboard = new Scanner(System.in);

	public Question(int questionID, String questionTitle, String language, String IBLevel, String difficulty,
			String questionText, String solutionHints, String returnType) {
		super();
		this.questionID = questionID;
		this.questionTitle = questionTitle;
		this.language = language;
		this.iBLevel = IBLevel;
		this.difficulty = difficulty;
		this.questionText = questionText;
		this.solutionHints = solutionHints;
		this.returnType = returnType;
	}

	// Loads question parameters and test cases from the database for Question
	// object
	protected void loadParametersAndTestCases() throws SQLException {
		try (Connection conn = DriverManager.getConnection(url)) { // Establish database connection

			// Clear existing data before loading new data
			questionParameters.clear();
			question_testCases.clear();

			// Retrieve all Question Parameters for specific Question Object
			try (PreparedStatement getParameterTable = conn
					.prepareStatement("SELECT * FROM 'Question Parameters' WHERE questionID = ?")) {
				getParameterTable.setInt(1, this.getQuestionID());

				// Execute query and retrieve resultset
				try (ResultSet question_pData = getParameterTable.executeQuery()) {
					while (question_pData.next()) {
						// Create parameter object for every node in result set
						Question_Parameters tempQuestionP = new Question_Parameters(
								question_pData.getInt("parameterID"), question_pData.getInt("questionID"),
								question_pData.getString("parameterVariable"), question_pData.getString("dataType"));
						// Add to this Question's Question Parameter ArrayList
						this.questionParameters.add(tempQuestionP);
					}
				}
			}

			// Load test cases from database
			try (PreparedStatement getTestCases = conn
					.prepareStatement("SELECT * FROM 'Test Cases' WHERE questionID = ?")) {
				getTestCases.setInt(1, this.getQuestionID());

				// Execute query and retrieve results
				try (ResultSet testCaseData = getTestCases.executeQuery()) {
					while (testCaseData.next()) {
						// Parse parameters string into a Map
						// Parse String to other Data Types Source (See Crit C Source 1)
						// Map Data Type Source (See Crit C Source 20)
						String paramsStr = testCaseData.getString("questionParameters");
						Map<String, Object> parameters = parseParametersString(paramsStr);

						// Create test case object and add to list
						Test_Case tempTestCase = new Test_Case(testCaseData.getInt("testcaseID"),
								testCaseData.getInt("questionID"), parameters, testCaseData.getString("expectedValue"),
								testCaseData.getString("expectedValueDataType"));

						this.question_testCases.add(tempTestCase);
					}
				}
			}
		}
		// Connection is automatically closed because of try-with-resources
	}

	protected Map<String, Object> parseParametersString(String paramsStr) {
		// Create new HashMap to store the parsed parameters
		Map<String, Object> parameters = new HashMap<>();
		// Java String Class Source (See Crit C Source 13)
		// Remove leading/trailing whitespace
		paramsStr = paramsStr.trim();
		// Remove surrounding curly braces if present
		if (paramsStr.startsWith("{")) {
			paramsStr = paramsStr.substring(1, paramsStr.length() - 1);
		}

		// Split the string into key-value pairs, handling arrays properly
		String[] pairs = paramsStr.split(",(?=[^\\]]*(?:\\[|$))");

		// Process each key-value pair
		for (String pair : pairs) {
			String[] keyValue = pair.split(":");
			if (keyValue.length == 2) {
				// Clean up the key by removing quotes and whitespace
				String key = keyValue[0].trim().replace("\"", "");
				String valueStr = keyValue[1].trim();

				Object value;
				// Check if the value is an array
				if (valueStr.startsWith("[")) {
					value = parseArray(valueStr);
				} else {
					// Try to parse as integer, if fails, keep as string
					try {
						value = Integer.parseInt(valueStr);
					} catch (NumberFormatException e) {
						value = valueStr;
					}
				}

				parameters.put(key, value);
			}
		}
		// Returns the parameters in a Map<String, Object> data type to GUI
		return parameters;
	}

	protected Object parseArray(String arrayStr) {
		// Parse String to other data types Source (See Crit C Source 1)
		// Remove square brackets
		arrayStr = arrayStr.substring(1, arrayStr.length() - 1);
		// Split into individual elements
		String[] elements = arrayStr.split(",");
		ArrayList<Object> array = new ArrayList<>();

		// Process each element, attempting to parse to other data types
		for (String element : elements) {
			String trimmedElement = element.trim();

			// Try parsing as Integer
			try {
				array.add(Integer.parseInt(trimmedElement));
			}
			// If not Integer, try Long
			catch (NumberFormatException e1) {
				try {
					array.add(Long.parseLong(trimmedElement));
				}
				// If not Long, try Double
				catch (NumberFormatException e2) {
					try {
						array.add(Double.parseDouble(trimmedElement));
					}
					// If not Double, check for Boolean or handle as String
					catch (NumberFormatException e3) {
						if (trimmedElement.equalsIgnoreCase("true") || trimmedElement.equalsIgnoreCase("false")) {
							array.add(Boolean.parseBoolean(trimmedElement));
						} else {
							// Handle as String, removing quotes if present
							if (trimmedElement.startsWith("\"") && trimmedElement.endsWith("\"")) {
								trimmedElement = trimmedElement.substring(1, trimmedElement.length() - 1);
							}
							array.add(trimmedElement);
						}
					}
				}
			}
		}

		return array;
	}

	protected String displayQuestion(String questionTitle) throws SQLException {
		// Initialize question text as null
		String questionText = null;

		// Create connection
		try (Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn
						.prepareStatement("SELECT questionText FROM Question WHERE questionTitle = ?")) {

			// Set the question title parameter in query
			pstmt.setString(1, questionTitle);

			// Execute query and retrieve results
			try (ResultSet getQuestionText = pstmt.executeQuery()) {
				if (getQuestionText.next()) {
					questionText = getQuestionText.getString("questionText");
				}
			}
		}

		// Throw exception if no question was found
		if (questionText == null) {
			throw new SQLException("No question found with the title: " + questionTitle);
		}

		return questionText;
	}

	/**
	 * Adds a new test case to the database with parameter values and expected
	 * output parameterValues = List of parameter values for the test case
	 * expectedOutput = Expected result of the test case
	 */
	protected void addTestCase(ArrayList<String> parameterValues, String expectedOutput) throws SQLException {
		// Using prepared statement to prevent SQL injection
		Connection conn = DriverManager.getConnection(url);
		PreparedStatement pstmt = conn.prepareStatement(
				"INSERT INTO 'Test Cases' (questionID, questionParameters, expectedValue, expectedValueDataType) VALUES (?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		// Convert parameter values to JSON format
		// Validates parameters for null/empty values before adding
		JsonObject parametersJson = new JsonObject();
		for (int i = 0; i < parameterValues.size(); i++) {
			String paramValue = parameterValues.get(i);
			String paramVariable = questionParameters.get(i).getParameterVariable(); // Get the parameter variable

			if (paramValue != null && !paramValue.isEmpty()) {
				// Handle different data types appropriately
				try {
					// Try parsing as number first
					double numValue = Double.parseDouble(paramValue);
					if (paramValue.contains(".")) {
						parametersJson.addProperty(paramVariable, numValue);
					} else {
						parametersJson.addProperty(paramVariable, (int) numValue);
					}
				} catch (NumberFormatException e) {
					// If not a number, add as string
					// Remove quotes if they exist at start and end
					if (paramValue.startsWith("\"") && paramValue.endsWith("\"")) {
						paramValue = paramValue.substring(1, paramValue.length() - 1);
					}
					parametersJson.addProperty(paramVariable, paramValue);
				}
			}
		}
		// Determine data type of expected output through type checking
		// Validates and sanitizes expected output type
		String expectedValueDataType;
		try {
			Integer.parseInt(expectedOutput);
			expectedValueDataType = "int";
		} catch (NumberFormatException e) {
			try {
				Long.parseLong(expectedOutput);
				expectedValueDataType = "long";
			} catch (NumberFormatException e2) {
				try {
					Double.parseDouble(expectedOutput);
					expectedValueDataType = "double";
				} catch (NumberFormatException e3) {
					expectedValueDataType = "String";
				}
			}
		}

		// Using parameterized queries to prevent SQL injection
		pstmt.setInt(1, this.questionID);
		pstmt.setString(2, parametersJson.toString());
		pstmt.setString(3, expectedOutput);
		pstmt.setString(4, expectedValueDataType);

		// Execute update and handle the result
		int affectedRows = pstmt.executeUpdate();
		if (affectedRows > 0) {
			// Retrieve and handle the generated test case ID
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				int testcaseID = generatedKeys.getInt(1);

				// Convert JSON parameters to Map for Test_Case object
				Gson gson = new Gson();
				Type mapType = new TypeToken<Map<String, Object>>() {
				}.getType();
				Map<String, Object> parameters = gson.fromJson(parametersJson.toString(), mapType);

				// Create and add new test case to the collection
				Test_Case newTestCase = new Test_Case(testcaseID, this.questionID, parameters, expectedOutput,
						expectedValueDataType);
				question_testCases.add(newTestCase);

			}
		}

		// Properly close database resources to prevent resource leaks
		pstmt.close();
		conn.close();
	}

	/**
	 * Updates an existing test case with new parameter values and expected output
	 * parameterValues = New parameter values for the test case expectedOutput = New
	 * expected result testCaseId = test case to alter
	 */
	protected void updateTestCase(int testCaseId, ArrayList<String> parameterValues, String expectedOutput)
			throws SQLException {

		// Using prepared statement to prevent SQL injection
		Connection conn = DriverManager.getConnection(url);
		PreparedStatement pstmt = conn.prepareStatement(
				"UPDATE 'Test Cases' SET questionParameters = ?, expectedValue = ?, expectedValueDataType = ? WHERE testcaseID = ?");

		// Convert parameter values to JSON format using the correct parameter names
		JsonObject parametersJson = new JsonObject();

		// Iterate through the question parameters and their corresponding values
		for (int i = 0; i < questionParameters.size() && i < parameterValues.size(); i++) {
			String paramName = questionParameters.get(i).getParameterVariable();
			String paramValue = parameterValues.get(i);
			String paramType = questionParameters.get(i).getDataType();

			if (paramValue != null && !paramValue.isEmpty()) {
				try {
					// Handle different data types
					switch (paramType.toLowerCase()) {
					case "int":
						parametersJson.addProperty(paramName, Integer.parseInt(paramValue));
						break;
					case "double":
						parametersJson.addProperty(paramName, Double.parseDouble(paramValue));
						break;
					case "long":
						parametersJson.addProperty(paramName, Long.parseLong(paramValue));
						break;
					case "boolean":
						parametersJson.addProperty(paramName, Boolean.parseBoolean(paramValue));
						break;
					default:
						// Remove quotes if they exist at start and end
						if (paramValue.startsWith("\"") && paramValue.endsWith("\"")) {
							paramValue = paramValue.substring(1, paramValue.length() - 1);
						}
						parametersJson.addProperty(paramName, paramValue);
					}
				} catch (NumberFormatException e) {
					// If parsing fails, store as string
					parametersJson.addProperty(paramName, paramValue);
				}
			}
		}

		// Determine data type of expected output
		String expectedValueDataType;
		try {
			Integer.parseInt(expectedOutput);
			expectedValueDataType = "int";
		} catch (NumberFormatException e) {
			try {
				Long.parseLong(expectedOutput);
				expectedValueDataType = "long";
			} catch (NumberFormatException e2) {
				try {
					Double.parseDouble(expectedOutput);
					expectedValueDataType = "double";
				} catch (NumberFormatException e3) {
					expectedValueDataType = "String";
				}
			}
		}

		// Set the parameters for the SQL update
		pstmt.setString(1, parametersJson.toString());
		pstmt.setString(2, expectedOutput);
		pstmt.setString(3, expectedValueDataType);
		pstmt.setInt(4, testCaseId);

		try {
			// Execute update and handle the result
			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				// Update the in-memory test case
				for (Test_Case testCase : question_testCases) {
					if (testCase.getTestcaseID() == testCaseId) {
						// Convert JSON to Map for Test_Case object
						Gson gson = new Gson();
						Type mapType = new TypeToken<Map<String, Object>>() {
						}.getType();
						Map<String, Object> parameters = gson.fromJson(parametersJson.toString(), mapType);

						testCase.setParameters(parameters);
						testCase.setExpectedValue(expectedOutput);
						testCase.setExpectedValueDataType(expectedValueDataType);
						break;
					}
				}
				System.out.println("Test case updated successfully.");
			} else {
				System.out.println("No test case found with ID: " + testCaseId);
			}
		} finally {
			pstmt.close();
			conn.close();
		}
	}

	/**
	 * Deletes a test case from the database testcaseID = ID of the test case to
	 * delete
	 */
	protected void deleteTestCase(int testcaseID) throws SQLException {
		// Using prepared statement to prevent SQL injection
		Connection conn = DriverManager.getConnection(url);
		PreparedStatement pstmt = conn
				.prepareStatement("DELETE FROM 'Test Cases' WHERE testcaseID = ? AND questionID = ?");

		// Ensures deletion only occurs for valid testcase and question combination
		pstmt.setInt(1, testcaseID);
		pstmt.setInt(2, this.questionID);

		// Execute delete and handle the result
		int affectedRows = pstmt.executeUpdate();
		if (affectedRows > 0) {
			// Remove the test case from the in-memory collection
			question_testCases.removeIf(testCase -> testCase.getTestcaseID() == testcaseID);
			System.out.println("Test case deleted successfully.");
		} else {
			System.out.println("No test case found with ID: " + testcaseID + " for question ID: " + this.questionID);
		}

		// Properly close database resources
		pstmt.close();
		conn.close();
	}

	/**
	 * Main method to execute code based on language selection Determines whether to
	 * run Python or Java code based on question language
	 */
	protected String runCode(String code, Map<String, Object> parameters, String fileName) {
		if (language.equalsIgnoreCase("Python")) {
			return runPythonCode(code, parameters);
		} else if (language.equalsIgnoreCase("Java")) {
			return runJavaCode(code, parameters, fileName);
		} else {
			return "Error: Unsupported language type: " + language;
		}
	}

	/**
	 * Executes Python code using Jython interpreter Key features: - Creates new
	 * Python interpreter instance - Captures output using ByteArrayOutputStream -
	 * Injects parameters into Python environment - Constructs and evaluates
	 * function call - Handles Python-specific exceptions
	 */
	protected String runPythonCode(String code, Map<String, Object> parameters) {
		// Jython Library Code Source (See Crit C Source 23)
		try (PythonInterpreter interpreter = new PythonInterpreter()) {
			// Set up output capture
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			interpreter.setOut(outputStream);

			// Execute the submitted code
			interpreter.exec(code);

			// Inject parameters into Python environment
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				interpreter.set(entry.getKey(), entry.getValue());
			}

			// Construct function name from question title
			String functionName = questionTitle.replaceAll("\\s+", "_").toLowerCase();

			// Build function call string with parameters
			StringBuilder callStr = new StringBuilder(functionName + "(");
			boolean first = true;
			for (String paramName : parameters.keySet()) {
				if (!first)
					callStr.append(", ");
				callStr.append(paramName);
				first = false;
			}
			callStr.append(")");

			// Evaluate function and return result
			PyObject result = interpreter.eval(callStr.toString());

			return result != null ? result.toString() : outputStream.toString().trim();
		} catch (PyException e) {
			return "Python Error: " + e.value;
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	/**
	 * Executes Java code with given parameters and returns the output code = The
	 * source code to be executed parameters = Map of parameter names to their
	 * values fileName = Name of the file to create (must end in .java) returns
	 * String containing program output or error message
	 */
	protected String runJavaCode(String code, Map<String, Object> parameters, String fileName) {
		// Java File and Java Path Source (See Sources 14 and 21)
		// Installing JDK-17 and Code Source(See Sources 9 and 19)

		// Defines path to JDK by getting absolute path of created file
		String projectDir = new File("").getAbsolutePath();
		String jdkPath = projectDir + "/jdk-17.jdk/Contents/Home";
		String javacPath = jdkPath + "/bin/javac";
		String javaPath = jdkPath + "/bin/java";

		File javacFile = new File(javacPath);
		File javaFile = new File(javaPath);
		if (!javacFile.exists() || !javaFile.exists()) {
			return "Error: JDK not found. Please ensure JDK is properly installed.";
		}
		// Verify JDK installation
		try {

			String methodName = this.getQuestionTitle(); // Since it's already "double_Char" in the database

			// Verify the submitted code contains the required method
			if (!code.contains(methodName)) {
				return "Error: Method '" + methodName + "' not found in submitted code";
			}

			// Create temporary directory for compilation and execution
			Path tempDir = Files.createTempDirectory("java_execution");

			// Extract class name from file name (removing .java extension)
			String className = fileName.substring(0, fileName.lastIndexOf('.'));

			StringBuilder preparedCode = new StringBuilder();
			preparedCode.append("public class ").append(className).append(" {\n");
			preparedCode.append("    ").append(code).append("\n");
			preparedCode.append("    public static void main(String[] args) {\n");
			preparedCode.append("        ").append(className).append(" solution = new ").append(className)
					.append("();\n");
			preparedCode.append("        System.out.print(solution.").append(methodName).append("(");

			// Add parameter parsing for each parameter in the method call
			int paramIndex = 0;
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				if (paramIndex > 0) {
					preparedCode.append(", ");
				}

				// Handle different parameter types (Integer, Double, String)
				if (entry.getValue() instanceof Integer) {
					preparedCode.append("Integer.parseInt(args[").append(paramIndex).append("])");
				} else if (entry.getValue() instanceof Double) {
					preparedCode.append("Double.parseDouble(args[").append(paramIndex).append("])");
				} else {
					preparedCode.append("args[").append(paramIndex).append("]");
				}

				paramIndex++;
			}

			preparedCode.append("));\n");
			preparedCode.append("    }\n}");

			// Write the complete source code to a file
			Path javaFilePath = tempDir.resolve(fileName);
			Files.write(javaFilePath, preparedCode.toString().getBytes());

			// Compile the Java file
			ProcessBuilder compileBuilder = new ProcessBuilder(javacPath, javaFilePath.toString());
			compileBuilder.redirectErrorStream(true);
			Process compileProcess = compileBuilder.start();

			// Capture compilation output/errors
			StringBuilder compileOutput = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					compileOutput.append(line).append("\n");
				}
			}

			// Check if compilation was successful
			int compileResult = compileProcess.waitFor();
			if (compileResult != 0) {
				return "Compilation Error: " + compileOutput.toString();
			}

			// Prepare command to run the compiled program
			ArrayList<String> command = new ArrayList<>();
			command.add(javaPath);
			command.add("-cp");
			command.add(tempDir.toString() + File.pathSeparator + jdkPath + "/lib/*");
			command.add(className);

			// Add parameters as command line arguments
			for (Object value : parameters.values()) {
				if (value instanceof String) {
					// Remove surrounding quotes if present
					String strValue = value.toString();
					if (strValue.startsWith("\"") && strValue.endsWith("\"")) {
						strValue = strValue.substring(1, strValue.length() - 1);
					}
					command.add(strValue);
				} else {
					command.add(value.toString());
				}
			}
			// Set up and start the process to run the program
			ProcessBuilder runBuilder = new ProcessBuilder(command);
			runBuilder.redirectErrorStream(true);
			runBuilder.directory(tempDir.toFile());
			Process runProcess = runBuilder.start();

			// Capture program output
			StringBuilder output = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line).append("\n");
				}
			}

			// Wait for completion with timeout
			boolean completed = runProcess.waitFor(10, TimeUnit.SECONDS);
			if (!completed) {
				runProcess.destroyForcibly();
				return "Error: Execution timed out";
			}

			// Clean up temporary files
			Files.walk(tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

			// Return the program output
			return output.toString().trim();

		} catch (IOException e) {
			return "Error: IO Exception - " + e.getMessage();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return "Error: Execution interrupted";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}

	}

	protected static Question createNewQuestion(String questionTitle, String difficulty, String ibLevel,
			String language, String solutionHints, String returnType, String questionText) {
		try {
			// Establish database connection
			Connection conn = DriverManager.getConnection(url);

			// Prepare SQL statement with parameter placeholders
			PreparedStatement pstmt = conn.prepareStatement(
					"INSERT INTO Question (questionTitle, language, IBlevel, difficulty, questionText, solutionHint, returnType) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			// Set values for the prepared statement
			pstmt.setString(1, questionTitle);
			pstmt.setString(2, language);
			pstmt.setString(3, ibLevel);
			pstmt.setString(4, difficulty);
			pstmt.setString(5, questionText);
			pstmt.setString(6, solutionHints);
			pstmt.setString(7, returnType);

			// Execute the insert statement
			pstmt.executeUpdate();

			// Get the auto-generated question ID from CyberDuck database
			ResultSet rs = pstmt.getGeneratedKeys();
			int questionID;
			if (rs.next()) {
				questionID = rs.getInt(1);
			} else {
				throw new SQLException("Failed to get question ID");
			}

			// Create new Question object with the generated ID
			Question newQuestion = new Question(questionID, questionTitle, language, ibLevel, difficulty, questionText,
					solutionHints, returnType);

			// Add the new question to the collection of all questions
			Run.AllQuestions.add(newQuestion);

			return newQuestion;

		} catch (SQLException e) {
			e.printStackTrace();
			javax.swing.JOptionPane.showMessageDialog(null, "Error creating question: " + e.getMessage(),
					"Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	protected void addParameter(Question question, String parameterVariable, String dataType) throws SQLException {

		try (Connection conn = DriverManager.getConnection(url);
				PreparedStatement addParameter = conn.prepareStatement(
						"INSERT INTO 'Question Parameters' (questionID, parameterVariable, dataType) VALUES (?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS)) {

			// Set values for the prepared statement
			addParameter.setInt(1, question.getQuestionID());
			addParameter.setString(2, parameterVariable);
			addParameter.setString(3, dataType);

			// Execute the insert statement and get number of affected rows
			int affectedRows = addParameter.executeUpdate();

			if (affectedRows > 0) {
				// If insert successful, get the generated parameter ID
				try (ResultSet generatedKeys = addParameter.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int newParameterID = generatedKeys.getInt(1);

						// Create new Parameter object and add to question's parameters
						Question_Parameters newParameter = new Question_Parameters(newParameterID,
								question.getQuestionID(), parameterVariable, dataType);
						question.questionParameters.add(newParameter);
					} else {
						throw new SQLException("Creating parameter failed, no ID obtained.");
					}
				}
			} else {
				throw new SQLException("Creating parameter failed, no rows affected.");
			}
		}
	}

	protected void updateParameter(int parameterID, String newVarName, String newDataType) throws SQLException {
		try (Connection conn = DriverManager.getConnection(url)) {
			conn.setAutoCommit(false);
			try {
				try (PreparedStatement checkBefore = conn.prepareStatement(
						"SELECT testcaseID, questionParameters, expectedValue FROM 'Test Cases' WHERE questionID = ?")) {
					checkBefore.setInt(1, this.questionID);
					ResultSet rsBefore = checkBefore.executeQuery();
					while (rsBefore.next()) {
						System.out.println(String.format("TestCase ID: %d, Params: %s, Expected: %s",
								rsBefore.getInt("testcaseID"), rsBefore.getString("questionParameters"),
								rsBefore.getString("expectedValue")));
					}
				}

				String oldVarName = null;
				for (Question_Parameters param : questionParameters) {
					if (param.getParameterID() == parameterID) {
						oldVarName = param.getParameterVariable();
						break;
					}
				}

				// Update parameter in database
				try (PreparedStatement pstmt = conn.prepareStatement(
						"UPDATE 'Question Parameters' SET parameterVariable = ?, dataType = ? WHERE parameterID = ?")) {
					pstmt.setString(1, newVarName);
					pstmt.setString(2, newDataType);
					pstmt.setInt(3, parameterID);
					pstmt.executeUpdate();
				}

				// Update the parameter in memory
				for (Question_Parameters param : questionParameters) {
					if (param.getParameterID() == parameterID) {
						param.setParameterVariable(newVarName);
						param.setDataType(newDataType);
						break;
					}
				}

				// Update test cases
				try (PreparedStatement getTestCases = conn
						.prepareStatement("SELECT * FROM 'Test Cases' WHERE questionID = ?")) {
					getTestCases.setInt(1, this.questionID);
					ResultSet rs = getTestCases.executeQuery();

					while (rs.next()) {
						int testCaseId = rs.getInt("testcaseID");
						String paramsJson = rs.getString("questionParameters");
						String expectedValue = rs.getString("expectedValue");
						String expectedValueType = rs.getString("expectedValueDataType");

						Gson gson = new Gson();
						JsonObject parametersJson = gson.fromJson(paramsJson, JsonObject.class);
						JsonObject updatedJson = new JsonObject();

						for (Map.Entry<String, JsonElement> entry : parametersJson.entrySet()) {
						    String key = entry.getKey();
						    JsonElement value = entry.getValue();
						    
						    // Ensure value is stored as a string
						    String stringValue;
						    if (value.isJsonPrimitive()) {
						        stringValue = value.getAsString();
						    } else {
						        stringValue = value.toString();
						    }
						    
						    if (!key.equals(oldVarName)) {
						        updatedJson.addProperty(key, stringValue);  
						    } else {
						        updatedJson.addProperty(newVarName, stringValue);
						    }
						}
						// Right before the UPDATE statement
						String jsonToSave = gson.toJson(updatedJson);
						System.out.println("JSON being saved to database: " + jsonToSave);

						// Update test case with explicit column list
						try (PreparedStatement updateTestCase = conn.prepareStatement(
								"UPDATE 'Test Cases' SET questionParameters = ?, expectedValue = ?, expectedValueDataType = ? WHERE testcaseID = ? AND questionID = ?")) {
							updateTestCase.setString(1, jsonToSave);
							updateTestCase.setString(2, expectedValue);
							updateTestCase.setString(3, expectedValueType);
							updateTestCase.setInt(4, testCaseId);
							updateTestCase.setInt(5, this.questionID);

							int updateResult = updateTestCase.executeUpdate();
							System.out.println("Test case update result: " + updateResult);
						}
						
						conn.commit();

						// Update in-memory test case
						for (Test_Case testCase : question_testCases) {
							if (testCase.getTestcaseID() == testCaseId) {
								Type mapType = new TypeToken<Map<String, Object>>() {
								}.getType();
								Map<String, Object> parameters = gson.fromJson(updatedJson, mapType);
								testCase.setParameters(parameters);
								System.out.println("Updated in-memory test case: " + parameters);
							}
						}
					}
				}

			} finally {
				conn.setAutoCommit(true);
			}
		}
	}

	protected void deleteParameter(int parameterID) throws SQLException {
		try (Connection conn = DriverManager.getConnection(url)) {
			// Disable auto-commit to enable transaction management
			conn.setAutoCommit(false);

			try {
				// Prepare and execute the delete statement
				try (PreparedStatement deleteStmt = conn
						.prepareStatement("DELETE FROM 'Question Parameters' WHERE parameterID = ?")) {

					deleteStmt.setInt(1, parameterID);

					// Execute the delete and check if any rows were affected
					int affectedRows = deleteStmt.executeUpdate();
					if (affectedRows == 0) {
						throw new SQLException("Deleting parameter failed, no rows affected.");
					}

					// Remove the parameter from the in-memory list
					questionParameters.removeIf(param -> param.getParameterID() == parameterID);

					// Commit the transaction if successful
					conn.commit();
				}
			} catch (SQLException e) {
				// Rollback the transaction if any error occurs
				conn.rollback();
				throw e;
			} finally {
				// Re-enable auto-commit
				conn.setAutoCommit(true);
			}
		}
	}

	protected boolean deleteQuestion() throws SQLException {
		Connection conn = DriverManager.getConnection(url);

		// Disable auto-commit to ensure transaction management
		conn.setAutoCommit(false);

		// Delete all question-associated test cases first
		PreparedStatement pstmt = conn.prepareStatement("DELETE FROM 'Test Cases' WHERE QuestionID = ?");
		pstmt.setInt(1, this.questionID);
		pstmt.executeUpdate();
		pstmt.close();

		// Delete all question-associated parameters
		PreparedStatement pstmt1 = conn.prepareStatement("DELETE FROM 'Question Parameters' WHERE QuestionID = ?");
		pstmt1.setInt(1, this.questionID);
		pstmt1.executeUpdate();
		pstmt1.close();

		// Delete all question-associated attempts
		PreparedStatement pstmt2 = conn.prepareStatement("DELETE FROM Attempts WHERE QuestionID = ?");
		pstmt2.setInt(1, this.questionID);
		pstmt2.executeUpdate();
		pstmt2.close();

		// Delete Question
		PreparedStatement pstmt3 = conn.prepareStatement("DELETE FROM Question WHERE QuestionID = ?");
		pstmt3.setInt(1, this.questionID);
		int rowsAffected = pstmt3.executeUpdate();
		pstmt3.close();

		// Commit the transaction
		conn.commit();

		// Return true if the question was successfully deleted
		return rowsAffected > 0;
	}

	protected void updateQuestionDetails(String title, String language, String iBLevel, String difficulty,
			String questionText, String solutionHints, String returnType) throws SQLException {

		// Use try-with-resources to ensure proper resource cleanup
		try (Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement("UPDATE Question SET " + "questionTitle = ?, "
						+ "language = ?, " + "IBLevel = ?, " + "difficulty = ?, " + "questionText = ?, "
						+ "solutionHint = ?, " + "returnType = ? " + "WHERE questionID = ?")) {

			// Set all the parameters for the prepared statement
			pstmt.setString(1, title);
			pstmt.setString(2, language);
			pstmt.setString(3, iBLevel);
			pstmt.setString(4, difficulty);
			pstmt.setString(5, questionText);
			pstmt.setString(6, solutionHints);
			pstmt.setString(7, returnType);
			pstmt.setInt(8, this.questionID);

			// Execute the update and check if any rows were affected
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Updating question failed, no rows affected.");
			}

			// Update the Question Object's instance variables if database update was
			// successful
			this.questionTitle = title;
			this.language = language;
			this.iBLevel = iBLevel;
			this.difficulty = difficulty;
			this.questionText = questionText;
			this.solutionHints = solutionHints;
			this.returnType = returnType;
		}
	}

	protected int getQuestionID() {
		return questionID;
	}

	protected void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	protected String getQuestionTitle() {
		return questionTitle;
	}

	protected void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	protected String getLanguage() {
		return language;
	}

	protected void setLanguage(String language) {
		this.language = language;
	}

	protected String getIBLevel() {
		return iBLevel;
	}

	protected void setIBLevel(String iBLevel) {
		this.iBLevel = iBLevel;
	}

	protected String getDifficulty() {
		return difficulty;
	}

	protected void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	protected String getQuestionText() {
		return questionText;
	}

	protected void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	protected String getSolutionHints() {
		return solutionHints;
	}

	protected void setSolutionHints(String solutionHints) {
		this.solutionHints = solutionHints;
	}

	protected String getReturnType() {
		return returnType;
	}

	protected void setReturnType(String returnType) {
		this.returnType = returnType;
	}

}
