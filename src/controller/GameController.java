package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tictactoe.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;

/**
*This class is responsible for manage game board and chat area.
*/
public class GameController {
	
	/**
	 * auxiliary constant.
	 */
	static final String initialMsg = "Letsplay";
	
	/**
	 * a constant used to specify the X icon style.
	 */
	static final String XIcon = "buttonX";
	/**
	 * a constant used to specify the O icon.
	 */
	static final String OIcon = "buttonO";

	/**
	 * Game buttons
	 */
	private Button[][] gameButtons;
	/**
	 * The connection socket received from the previous scene
	 */
	private Socket connectionSocket;

	/**
	 * Grid Pane object.
	 */
	@FXML private GridPane gridPane;
	/**
	 * Area for chat.
	 */
	@FXML private TextArea chatTextArea;
	/**
	 * Textfield for chat input.
	 */
	@FXML private TextField chatInputTextField;
	/**
	 * The field where information about the turn is displayed.
	 */
	@FXML private TextField turnTextField;
	@FXML private ImageView sendImage;
	
		/**
		 * boolean parameter that determines whether another click is possible.
		 */
		private boolean canPlay;
		/**
		 * turn this parameter determines if the player can now make a move.
		 */
		private boolean turn;
		/**
		 *boolean parameter that determines if any player has won.
		 */
		private boolean endGame = false;
		/**
		 * string parameter that determines which icon (O or X) the player chose.
		 */
		private String myIcon;
		/**
		 * string parameter that determines which icon (O or X) the opponent chose.
		 */
		private String opponentIcon;
		/**
		 * string parameter that determines a nickname of player.
		 */
		private String nickName;

		
		/**
		*This method is responsible for creating a game board on which 
		*the player will be able to place a symbol of his choice (in the previous scene). 
		*/
	@FXML
	public void initialize() {
		
		int width, height;
		width = gridPane.getColumnConstraints().size();
		height = gridPane.getRowConstraints().size();

		gameButtons = new Button[width][height];

		// generates the buttons
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Button button = new Button();
				button.getStyleClass().add("gameButton");
				button.setMaxWidth(Double.MAX_VALUE);
				button.setMaxHeight(Double.MAX_VALUE);
				button.setAlignment(Pos.CENTER);
				GridPane.setMargin(button, new Insets(5d, 5d, 5d, 5d));

				gridPane.add(button, j, i);
				gameButtons[i][j] = button;
				button.setOnAction(new gameButtonHandler());
			}
		}
	}
	/**
	*This method is responsible for setting icon (X or O) from 
	*RadioButton (ORadio or XRadio) from ConnectionController.
	*Host player starts the game
	*@param icon Each player can only choose one icon (X or O).
	*/
	// Gets the style (icon) to use from previous scene
	public void setIcon(String icon) {
		
		if (icon.startsWith("X")) {
			this.myIcon = XIcon;
			this.opponentIcon = OIcon;
			
		} else {
			this.myIcon = OIcon;
			this.opponentIcon = XIcon;		
		}
	}

	/**
	*This method is responsible for getting the nickname from {@link ConnectionController#nicknameTextField};
	* @param nickName nickname of player.
	*/
	public void setNickName(String nickName) {
			this.nickName = nickName;
	}

	/**
	*This method is responsible for displaying the welcome message and inform the player whose turn it is now;
	*/
	public void printInitialText() {
		chatTextArea.appendText("[Gra]" + " Witaj Graczu! " + nickName + "\n");

		if (turn) {
			turnTextField.setText("Twój ruch!");
		} else {
			turnTextField.setText("Ruch przeciwnika!");	
		}
	}
	/**
	 * This inner class is responsible for handle the button presses on game board.
	 */
	class gameButtonHandler implements EventHandler<ActionEvent> {

		/**
		 * This method is responsible for playing the game. Tells the player in turnTextField who should make the move now. 
		 * The method uses {@link Game#checkGameOver(String, String, Button[][])} to check if the game has finished. 
		 * If so, a win or lose message is displayed in {@link GameController#turnTextField}. 
		 * Then the {@link GameController#checkNewGame(boolean)} method is used to play the game again.
		 */
	
		@Override
		public void handle(ActionEvent event) {
			
			if (!canPlay)
				return;
			PrintWriter writer;
			try {
				writer = new PrintWriter(connectionSocket.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			canPlay = !canPlay;
			Button button = ((Button) event.getSource());
			button.setDisable(true);
			// style the button to show an X or a O, based on turn
			button.getStyleClass().add(getPlayerIcon(turn));
			if(!turn) {
			turnTextField.setText("Twój ruch!");
		} else {
			turnTextField.setText("Ruch przeciwnika!");
		}
			// checks endGame
			if (Game.checkGameOver(OIcon, XIcon, gameButtons)) {

				for (Button[] button1 : gameButtons)
					for (Button button2 : button1)
						button2.setDisable(true);
				if (turn) {
					turnTextField.setText("Wygra³eœ!");
				} else {
					turnTextField.setText("Przegra³eœ!");
				}
				endGame = true;
			} else if (checkFullBoard()) {
				chatTextArea.appendText("[Gra] Remis!\n");
				turnTextField.setText("Remis!");
				endGame = true;
			}
			if (!turn) {
				// set the turn before leaving
				canPlay = !canPlay;
				turn = true;
				if (endGame)
					checkNewGame(false);
				return;
			}
			turn = !turn;
			// send a chat command to the other user
			writer.println(initialMsg);

			int x, y = 0;
			// finds out which button was clicked
			Click: for (x = 0; x < gameButtons.length; x++) {
				for (y = 0; y < gameButtons.length; y++) {
					if (gameButtons[x][y].equals(button))
						break Click;
				}
			}
			// sends a x and y value
			writer.println(x);
			writer.println(y);
			if (endGame)
				checkNewGame(true);
		}
	}

	/**
	 * This method is responsible for the development of the game after its completion. 
	 * If the player wishes to continue playing, the board will be prepared for the new game. 
	 * Otherwise, the player will be taken to the start screen.
	 * @param newGame This parameter inform the player if he won or lost (tied).
	 */
	private void checkNewGame(boolean newGame) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy chcesz rozpocz¹æ now¹ grê?", ButtonType.YES,
					ButtonType.NO);
			alert.getDialogPane().getStylesheets().add("/res/styles.css");
			alert.setTitle("Nowa gra");
			Optional<ButtonType> result = alert.showAndWait();
			endGame = false;
			if (result.get() == ButtonType.YES) {

				CleanTheBoard();
				turn = !newGame;
				turnTextField.setText("Twój ruch!");

				if (!turn) {
					turnTextField.setText("Ruch przeciwnika!");
				}
			} else {
				Stage stage = ((Stage) chatTextArea.getScene().getWindow());
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ConnectionScene.fxml"));
				Parent root = null;
				try {
					root = loader.load();
				} catch (IOException e) {
					Platform.exit();
				}
				stage.setTitle("Kó³ko i krzy¿yk Menu");
				stage.setScene(new Scene(root));
				stage.setWidth(460);
				stage.setHeight(445);
				stage.show();
			}
		});
	}
	
	/**
	 * This method is responsible for send message by mouse click (using the {@link GameController#sendMessage()} method).
	 */
	@FXML 
	private void sendMessageByMouseCliked() {
		sendMessage();
	}
	
	/**
	 * This method is responsible for send message by click enter button on keyboard (using the {@link GameController#sendMessage()} method).
	 */
	@FXML 
	// Method called when enter is pressed on the chatInput field
	private void sendMessageByEnter() {
		sendMessage();
	}
	/**
	 * Method is responsible for handle communication. Operate both game buttons and chat message connection 
	 * Method is called from ConnectionController scene to establish connection socket
	 * Thread handleCommunication provide the communication on the background and brake connection when can not read data
	 * @param socket - socket responsible for connection
	 */

	public void initializeConnection(Socket socket) {

		this.connectionSocket = socket;
		Scanner scanner;
		try {

			scanner = new Scanner(connectionSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		// provide communication on the background
		Thread handleCommunication = new Thread(() -> {
			while (true) {
				String message;
				try {
					// tries to read the input
					message = scanner.next();
					System.out.println(message);
				} catch (Exception e) {
					
					break;
				}
				// check if the incoming message is a game command
				if (message.startsWith(initialMsg) && !canPlay) {
					// reads which button to press
					int i = scanner.nextInt();
					int j = scanner.nextInt();
			
					if (!(gameButtons[i][j].isDisable()))
						canPlay = !canPlay;
					gameButtons[i][j].fire();
				} else {
					chatTextArea.appendText(message + "\n");
					turnTextField.appendText("");
					
				}
			}
			scanner.close();
		});
		handleCommunication.setDaemon(true);
		handleCommunication.start();
	}
	
	/**
	 * This method allows players to chat through the chat window. 
	 * Print messages on player and opponent's chats
	 */
	private void sendMessage() {
		PrintWriter writer;
		try {
			writer = new PrintWriter(connectionSocket.getOutputStream(), true);
		} catch (IOException e) {
			return;
		}
		if (chatInputTextField.getText().equals(""))
			return;
		writer.println(nickName + ": " + chatInputTextField.getText());
		chatTextArea.appendText("Ty: " + chatInputTextField.getText() + "\n");
		chatInputTextField.setText("");
	}

	/**
	 * Method to initialize the turn and canPlay variables.
	 * @param move boolean parameter that determines which player has turn.
	 */
	public void setMove(boolean move) {
		canPlay = move;
		this.turn = move;
	}
	/**
	 * Method which return the style (icon), which is to use for next turn.
	 * @param turn this parameter determines if the player can now make a move.
	 * @return return the style (icon), which is to use for next turn.
	 */

		private String getPlayerIcon(boolean turn) {
			return turn ? myIcon : opponentIcon;
		}
	/**
	 * This method is responsible for cleaning game board 
	 * (preparing game board for new game).
	 */
	private void CleanTheBoard() {
		for (Button[] button : gameButtons) {
			for (Button b : button) {
				b.setDisable(false);
				b.getStyleClass().remove(OIcon);
				b.getStyleClass().remove(XIcon);
			}
		}
	}
	/**
	 * This method is responsible for checking if the game board is full. 
	 * @return true if game board is full.
	 */
	private boolean checkFullBoard() {
		for (Button[] button : gameButtons) {
			for (Button b : button)
				if (!b.isDisable())
					return false;
		}
		return true;
	}

}