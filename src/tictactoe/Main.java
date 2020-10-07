package tictactoe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class responsible for running application
 * 
 */
public class Main extends Application {

	/**
	 * Method load the first scene with connection options fields
	 */
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ConnectionScene.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Kó³ko i krzy¿yk menu");
        primaryStage.setScene(new Scene(root));
        primaryStage.setHeight(460);
        primaryStage.setWidth(445);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> Platform.exit());
    }

/**
 * Launches java application
 * @param args default argument 
 */
    
    public static void main(String[] args) {
        launch(args);
    }
}
