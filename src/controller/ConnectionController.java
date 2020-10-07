package controller;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tictactoe.FieldGuide;

import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is responsible for establishing the connection between the
 * players.
 * 
 */
public class ConnectionController {

	//constants
	private final double screenWidth = 657;
	private final double screenHeight = 520;
	private final long timeout = 300000;

	//fields
	private Socket connectionSocket;
	private FieldGuide fieldGuide = new FieldGuide();
	// Scene objects
	@FXML
	private TextField nicknameTextField;
	@FXML
	private TextField hostPortTextField;
	@FXML
	private TextField hostIPTextField;
	@FXML
	private TextField connectIPTextField;
	@FXML
	private TextField connectPortTextField;
	@FXML
	private RadioButton ORadio;
	@FXML
	private RadioButton XRadio;
	@FXML
	private ToggleGroup selectRadio;
	@FXML
	private ToggleGroup iconRadio;
	@FXML
	private RadioButton hostRadio;
	@FXML
	private RadioButton connectRadio;
	@FXML
	private Button hostButton;
	@FXML
	private Button connectionButton;
	@FXML
	private ProgressIndicator hostIndicator;

	/**
	 * This method is responsible for setting disable host area when
	 * {@link ConnectionController#connectRadio} is clicked.
	 */
	@FXML 
	private void disableHostArea() {
		connectIPTextField.setDisable(false);
		connectPortTextField.setDisable(false);
		connectionButton.setDisable(false);
		hostPortTextField.setDisable(true);
		hostButton.setDisable(true);
		hostIPTextField.setDisable(true);
	}

	/**
	 * This method is responsible for setting disable connect area when
	 * {@link ConnectionController#hostRadio} is clicked.
	 */
	@FXML
	private void disableConnectArea() {
		connectIPTextField.setDisable(true);
		connectPortTextField.setDisable(true);
		connectionButton.setDisable(true);
		hostPortTextField.setDisable(false);
		hostButton.setDisable(false);
		hostIPTextField.setDisable(false);
	}

	/**
	 * This method initialize default settings, select {@link ConnectionController}.
	 */
	@FXML
	public void initialize() {
		selectRadio.selectToggle(hostRadio);
		connectIPTextField.setDisable(true);
		connectPortTextField.setDisable(true);
		connectionButton.setDisable(true);
	}

	/**
	 * Method run when HostButton was clicked
	 * {@link ConnectionController#hostButton} The hostButtonAction method tries
	 * to parse the port number from the
	 * {@link ConnectionController#hostPortTextField} text field. If the text box
	 * contains anything other than the port number, show the dialog box ("Invalid
	 * port"). Then method tries to open the server socket on the port provided. If
	 * it was not possible, show the dialog box ("Could not open port"). Most
	 * reasons for failure are that the port is in use. The method then creates an
	 * timer thread to close the socket when it times out
	 * {@link ConnectionController#timeout}.
	 * @param event
	 * HostIndicator
	 *            shows the indicator that the server is waiting for a connection. A
	 *            thread is created that waits for another player to connect. If it
	 *            takes too long, a dialog box will appear ("Connection time
	 *            expired"). If any other exception occurs, a dialog box
	 *            ("Connection Failure") will appear and you will return to the
	 *            previous scene.
	 * port
	 *            the port number chosen by the player.
	 * serverSocket
	 *            variable of the ServerSocket class.
	 */
	@FXML
	private void hostButtonAction(Event event) {
		
		int port;
		final ServerSocket serverSocket;

		try {
			port = fieldGuide.checkPortNumber(hostPortTextField.getText());
			
		} catch (NumberFormatException e) {
			showAlert("Niepoprawny port", "Niepoprawny numer portu: " + hostPortTextField.getText(),
					Alert.AlertType.WARNING, ButtonType.OK);
			return;
		}
		try {
			// server socked opening
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			showAlert("Nie mo¿na otworzyæ portu", "Nie mo¿na otworzyæ portu: " + port, Alert.AlertType.ERROR,
					ButtonType.OK);
			return;
		}

		// timer thread to close the socket when time is out
		Timer timer = new Timer();
		Thread timerThread = new Thread(() -> {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						serverSocket.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, timeout);
		});
		
		timerThread.setDaemon(true);
		timerThread.start();

		// connection indicator
		hostIndicator.setVisible(true);
		Stage stage = ((Stage) ((Button) event.getSource()).getScene().getWindow());
		// thread to wait until someone connects
		Thread connectionWaitingThread = new Thread(() -> {
			try {
				// waiting for connection. 
				connectionSocket = serverSocket.accept();
			} catch (SocketException e) {

				Platform.runLater(() -> showAlert("Czas po³¹czenia min¹³",
						"Czas po³¹czenia min¹³, sprawdz dane i kliknij ponownie przycisk host",
						Alert.AlertType.INFORMATION, ButtonType.OK));
				return;
			} catch (IOException e) {

				Platform.runLater(
						() -> showAlert("B³¹d po³¹czenia", "B³¹d po³¹czenia", Alert.AlertType.ERROR, ButtonType.OK));
				return;

			} finally {
				timer.cancel();
				timerThread.interrupt();
			}

			// change to the game scene - succesfull connection

			Platform.runLater(() -> showGameScene(stage, connectionSocket, true));
		});
		connectionWaitingThread.setDaemon(true);
		connectionWaitingThread.start();

	}

	/**
	 *  This method creates a dialog boxes using the Alert class.
	 * @param title alert title
	 * @param message description
	 * @param type alert type
	 * @param buttonType button type
	 */
	private void showAlert(String title, String message, Alert.AlertType type, ButtonType buttonType) {
		Alert alert = new Alert(type, message, buttonType);
		alert.getDialogPane().getStylesheets().add("/res/styles.css");
		alert.setTitle(title);
		alert.showAndWait();
	}

	/**
	 * Method run when ConnectionButton was clicked
	 * This method is responsible to connect second player to a host (first player).
	 * second player should set the right parameters in
	 * {@link ConnectionController#connectIPTextField} and
	 * {@link ConnectionController#connectPortTextField}
	 * @param event event
	 */
	@FXML 
	private void connectButtonAction(Event event) {
		String ip;
		int port;

		try {
			port = fieldGuide.checkPortNumber(connectPortTextField.getText());
		} catch (NumberFormatException e) {
			// if port number is wrong
			showAlert("Niprawid³owy port", "Nieprawid³owy numer portu: " + connectPortTextField.getText(),
					Alert.AlertType.WARNING, ButtonType.OK);
			return;
		}

		ip = connectIPTextField.getText();
		try {
			// connection
			connectionSocket = new Socket(ip, port);
		} catch (UnknownHostException e) {
			showAlert("Nieznany host", "B³¹d po³¹czenia z hostem " + connectIPTextField.getText(),
					Alert.AlertType.ERROR, ButtonType.OK);
			return;
		} catch (IOException e) {
			showAlert("Nieznany host", "B³¹d po³¹czenia z hostem " + connectIPTextField.getText(),
					Alert.AlertType.ERROR, ButtonType.OK);
			return;
		}

		Stage stage = ((Stage) ((Button) event.getSource()).getScene().getWindow());

		showGameScene(stage, false);
	}

	private void showGameScene(Stage stage, boolean turn) {
		showGameScene(stage, connectionSocket, turn);
	}
	
	/**
	 * This method is responsible for change the game scene to the game once a
	 * connection was made.
	 *
	 * @param stage {@link Stage class parameter}
	 * @param socket {@link Socket class parameter}
	 * @param move boolean parameter that determines if any player has won.
	 */
	private void showGameScene(Stage stage, Socket socket, boolean move) {

		GameController gameController;

		// checking nickname
		FieldGuide checkNickName = new FieldGuide();
		checkNickName.setNick(nicknameTextField.getText());

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GameScene.fxml"));
		Parent parent;
		try {		
			parent = loader.load();
		} catch (IOException e) {
		
			e.printStackTrace();
			return;
		}
		stage.setScene(new Scene(parent));
		stage.setTitle("Kó³ko i krzy¿yk");
		stage.setHeight(screenHeight);
		stage.setWidth(screenWidth);
		stage.show();
		gameController = loader.getController();
		gameController.initializeConnection(socket);
		gameController.setMove(move);
		gameController.setIcon(((RadioButton) iconRadio.getSelectedToggle()).getText());
		gameController.setNickName(checkNickName.getNick());
		gameController.printInitialText();

	}
}
