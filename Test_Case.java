package package_IA;

import java.util.Map;
import java.util.Scanner;


public class Test_Case {

	private int testcaseID;
	private int questionID;
	private Map<String, Object> parameters;
	private String expectedValue;
	private String expectedValueDataType;

	public Test_Case(int testcaseID, int questionID, Map<String, Object> parameters, String expectedValue,
			String expectedValueDataType) {
		super();
		this.testcaseID = testcaseID;
		this.questionID = questionID;
		this.parameters = parameters;
		this.expectedValue = expectedValue;
		this.expectedValueDataType = expectedValueDataType;
	}

	protected int getTestcaseID() {
		return testcaseID;
	}

	protected void setTestcaseID(int testcaseID) {
		this.testcaseID = testcaseID;
	}

	protected int getQuestionID() {
		return questionID;
	}

	protected void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	protected Map<String, Object> getParameters() {
		return parameters;
	}

	protected void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	protected String getExpectedValue() {
		return expectedValue;
	}

	protected void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	protected String getExpectedValueDataType() {
		return expectedValueDataType;
	}

	protected void setExpectedValueDataType(String expectedValueDataType) {
		this.expectedValueDataType = expectedValueDataType;
	}

}
