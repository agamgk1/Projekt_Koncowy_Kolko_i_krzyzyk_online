package tictactoe;

/**
 * Class responsible for checking data entered by the user
 * 
 * 
 */

public class FieldGuide {

	private String nick;

	/**
	 * @return nick - player nickname
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Method responsible for setting and checking nickname correctness
	 * 
	 * @param nick  - the nickname of which will be validated. If player leave empty
	 *            nickname field, nickname will be default - "Anonymous"         
	 */
	public void setNick(String nick) {
		if (nick.equals(""))
			this.nick = "Anonymous";
		else
			this.nick = nick;
	}

	/**
	 * Method responsible for checking correctness of port number. Throws
	 * NumberFormatException if port number is wrong
	 * Condition: the text must be a positive number and less than 65536.
	 * 
	 * @param port - given portNumer as a string          
	 * @return portNumber, if correct
	 */
	public int checkPortNumber(String port) {
		int portNumber = Integer.parseInt(port);
		if (portNumber < 0 || portNumber > 65535)
			throw new NumberFormatException();
		return portNumber;
	}

}
