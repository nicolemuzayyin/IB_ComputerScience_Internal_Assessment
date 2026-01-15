package package_IA;


import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class User {

	private int userID;
	private String fullName;
	private String email;
	private String password;
	private boolean isLoggedIn;
	private String role;

	public User(int userID, String fullName, String email, String hashedPassword, String role) {
		this.userID = userID;
		this.fullName = fullName;
		this.email = email;
		this.password = hashedPassword;
		this.role = role;
		this.isLoggedIn = false;

	}
	//argon2 Code Source (See Crit C Source 8)
	protected static User login(String email, String password) {
	    // Search through all users
	    for (User user : Run.AllUsers) {
	        // Check if email matches (case insensitive)
	        if (user.email.equalsIgnoreCase(email)) {
	            // Create Argon2 hasher
	            Argon2 argon2 = Argon2Factory.create();
	            
	            // Verify password matches stored hash
	            if (argon2.verify(user.password, password.toCharArray())) {
	                user.isLoggedIn = true;
	                argon2.wipeArray(password.toCharArray()); // Clean up sensitive data
	                return user; // Return user on successful login
	            }
	            
	            argon2.wipeArray(password.toCharArray()); // Clean up sensitive data
	            return null; // Return null if password incorrect
	        }
	    }
	    return null; // Return null if email not found
	}
	
	protected void logout() {
		this.isLoggedIn = false;
	}

	protected int getUserID() {
		return userID;
	}

	protected void setUserID(int userID) {
		this.userID = userID;
	}

	protected String getFullName() {
		return fullName;
	}

	protected void setFullName(String fullName) {
		this.fullName = fullName;
	}

	protected String getEmail() {
		return email;
	}

	protected void setEmail(String email) {
		this.email = email;
	}

	protected String getPassword() {
		return password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	protected boolean isLoggedIn() {
		return isLoggedIn;
	}

	protected void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	protected String getRole() {
		return role;
	}

	protected void setRole(String role) {
		this.role = role;
	}

}
