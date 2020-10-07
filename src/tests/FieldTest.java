package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import tictactoe.FieldGuide;

/**
 * Testing class
 * 
 */

public class FieldTest {

	private FieldGuide checkNickName = new FieldGuide();
	/**
	 * This method check the correctness of the nickname assignment
	 */
	@Test
	public void checkFillNickName() {
		
		checkNickName.setNick("Adam");
		assertEquals("Adam", checkNickName.getNick());
	}
	/**
	 * This method check the correctness of assignment of "Anonymous" nick, when field is empty
	 */
	@Test 
	public void checkEmptyNickName() {
		checkNickName.setNick("");
		assertEquals("Anonymous", checkNickName.getNick());
	}
	
	/**
	 * This method check the correctness of assignment of port number 
	 */
	@Test
	public void checkCorrectnessOfPortNumber() {
		int a = checkNickName.checkPortNumber("12344");
		assertEquals(12344, a);
		assertThrows(
				NumberFormatException.class,
				()->{
					checkNickName.checkPortNumber("-5");
				}
				);
		assertThrows(
				NumberFormatException.class,
				()->{
					checkNickName.checkPortNumber("1000000");
				}
				);
		}
	}

