package tictactoe;

import javafx.scene.control.Button;

/**
 * This class use {@link Game#checkGameOver(String, String, Button[][])} method and is responsible for game rules.
 *
 */
public class Game {
	
	/**
	 * This method is responsible for checking if the game is over (win / lose, draw).
	 * The individual possible winning combinations are checked. True is returned on victory, false otherwise.
	 * @param OIcon button with O icon.
	 * @param XIcon button with X icon.
	 * @param buttons table of buttons that make up the game board.
	 * @return true if a player win, false otherwise (draw).
	 */

	   public static boolean checkGameOver(String OIcon, String XIcon, Button[][] buttons) {
       for (int i = 0; i < buttons.length; i++) {
            // test for each row to O
            if (
                    buttons[i][0].getStyleClass().contains(OIcon) &&
                    buttons[i][1].getStyleClass().contains(OIcon) &&
                    buttons[i][2].getStyleClass().contains(OIcon)
                    )
                return true;
            // test for each column to O
            else if (
                    buttons[0][i].getStyleClass().contains(OIcon) &&
                    buttons[1][i].getStyleClass().contains(OIcon) &&
                    buttons[2][i].getStyleClass().contains(OIcon)
                    )
                return true;
        }
        for (int i = 0; i < buttons.length; i++) {
            // test for each row to X
            if (
                    buttons[i][0].getStyleClass().contains(XIcon) &&
                    buttons[i][1].getStyleClass().contains(XIcon) &&
                    buttons[i][2].getStyleClass().contains(XIcon)
                    )
                return true;
            // test for each column to X
            else if (
                    buttons[0][i].getStyleClass().contains(XIcon) &&
                    buttons[1][i].getStyleClass().contains(XIcon) &&
                    buttons[2][i].getStyleClass().contains(XIcon)
                    )
                return true;
        }

        // check the first diagonal
        if (
                buttons[0][0].getStyleClass().contains(OIcon) &&
                buttons[1][1].getStyleClass().contains(OIcon) &&
                buttons[2][2].getStyleClass().contains(OIcon)
                )
            return true;
        if (
                buttons[0][0].getStyleClass().contains(XIcon) &&
                buttons[1][1].getStyleClass().contains(XIcon) &&
                buttons[2][2].getStyleClass().contains(XIcon)
                )
            return true;

        // check the second diagonal
        if (
                buttons[0][2].getStyleClass().contains(OIcon) &&
                buttons[1][1].getStyleClass().contains(OIcon) &&
                buttons[2][0].getStyleClass().contains(OIcon)
                )
            return true;

        if (
                buttons[0][2].getStyleClass().contains(XIcon) &&
                buttons[1][1].getStyleClass().contains(XIcon) &&
                buttons[2][0].getStyleClass().contains(XIcon)
                )
            return true;

        return false;
    }	
	
}
