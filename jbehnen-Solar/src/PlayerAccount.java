
/**
 * Represents a game with an ID, title, description, price, genre ID, gameplay type ID,
 * and publisher ID.
 * @author Julia Behnen
 * @version December 2015
 *
 */
public class PlayerAccount {
	private String username;
	private String email;
	private String password;
	
	/**
	 * Initialize the game parameters.
	 * @param username
	 * @param email
	 * @param password
	 * @param isPasswordEncrypted True if password is already encrypted, false otherwise.
	 * @throws IllegalArgumentException if username, email, and/or password are null or empty.
	 */
	public PlayerAccount(String username, String email, String password, boolean isPasswordEncrypted) {
		setUsername(username);
		setEmail(email);
		setPassword(password, isPasswordEncrypted);
	}

	@Override
	public String toString() {
		return "PlayerAccount [username=" + username + ", email=" + email + ", password=" + password + "]";
	}

	/**
	 * Returns the username of the account.
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Modifies the username of the account.
	 * @param username
//	 * @throws IllegalArgumentException if username is null or empty.
	 */
	public final void setUsername(String username) {
		if (username == null || username.length() == 0) {
			throw new IllegalArgumentException("Please supply a valid username.");
		}
		this.username = username;
	}

	/**
	 * Returns the email of the account.
	 * @return email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Modifies the email of the account.
	 * @param email
	 * @throws IllegalArgumentException if email is null or empty.
	 */
	public final void setEmail(String email) {
		if (email == null || email.length() == 0) {
			throw new IllegalArgumentException("Please supply a valid email.");
		}
		this.email = email;
	}

	/**
	 * Returns the encrypted password of the account.
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Modifies the password of the account.
	 * @param password
	 * @param isEncrypted True if password is already encrypted, false otherwise.
	 * @throws IllegalArgumentException if password is null or empty.
	 */
	public final void setPassword(String password, boolean isEncrypted) {
		if (password == null || password.length() == 0) {
			throw new IllegalArgumentException("Please supply a valid password.");
		}
		if (!isEncrypted) {
			password = Helper.encryptString(password);
		}
		this.password = password;
	}
	
}
