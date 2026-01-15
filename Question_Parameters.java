package package_IA;


public class Question_Parameters {

	private int parameterID;
	private int questionID;
	private String parameterVariable;
	private String dataType;

	public Question_Parameters(int parameterID, int questionID, String parameterVariable, String dataType) {
		super();
		this.parameterID = parameterID;
		this.questionID = questionID;
		this.parameterVariable = parameterVariable;
		this.dataType = dataType;
	}
	
	//Parse String to Data Type Source (See Crit C Source 1)
	protected Object convertToType(String value) {
	    try {
	        // Convert data type to lowercase for case-insensitive comparison
	        String lowercaseDataType = dataType.toLowerCase();
	        
	        // Handle integer conversion, including decimal strings
	        if ("int".equals(lowercaseDataType)) {
	            if (value.contains(".")) {
	                return (int) Double.parseDouble(value);    // Convert decimal to int
	            }
	            return Integer.parseInt(value);
	        } 
	        // Handle long conversion, including decimal strings
	        else if ("long".equals(lowercaseDataType)) {
	            if (value.contains(".")) {
	                return (long) Double.parseDouble(value);   // Convert decimal to long
	            }
	            return Long.parseLong(value);
	        } 
	        // Handle floating-point numbers (both double and float)
	        else if ("double".equals(lowercaseDataType) || "float".equals(lowercaseDataType)) {
	            return Double.parseDouble(value);
	        } 
	        // Handle boolean conversion
	        else if ("boolean".equals(lowercaseDataType)) {
	            return Boolean.parseBoolean(value);
	        } 
	        // Handle string (no conversion needed)
	        else if ("string".equals(lowercaseDataType)) {
	            return value;
	        } 
	        // Throw exception for unsupported data types
	        else {
	            throw new IllegalArgumentException("Unsupported data type: " + dataType);
	        }
	    } catch (NumberFormatException e) {
	        // Throw exception if number conversion fails
	        throw new IllegalArgumentException("Cannot convert '" + value + "' to " + dataType);
	    }
	}


	protected String getDataType() {
        return dataType;
    }


	protected int getParameterID() {
		return parameterID;
	}


	protected void setDataType(String dataType) {
		this.dataType = dataType;
	}

	protected void setParameterID(int parameterID) {
		this.parameterID = parameterID;
	}

	protected int getQuestionID() {
		return questionID;
	}

	protected void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	protected String getParameterVariable() {
		return parameterVariable;
	}

	protected void setParameterVariable(String parameterVariable) {
	    this.parameterVariable = parameterVariable;
	}


}
