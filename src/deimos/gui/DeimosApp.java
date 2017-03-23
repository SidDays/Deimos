package deimos.gui;

import java.io.IOException;

import deimos.gui.view.DeimosAppOverviewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeimosApp extends Application {

	private Stage primaryStage;
	private VBox rootLayout;
	final public String title = "Deimos Application";

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		this.primaryStage = primaryStage;

		try {

			// Load root layout from fxml file.        	
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(DeimosApp.class.getResource("view/DeimosAppOverview.fxml"));
			rootLayout = (VBox) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);

			primaryStage.show();

			DeimosAppOverviewController controller = loader.getController();
			controller.setMainApp(this);


		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Can be used to allow other classes to
     * make changes to the main Stage of the application.
     * @return A reference to the main Stage used
     */
	public Stage getPrimaryStage() {
		
		return primaryStage;
	}
}
