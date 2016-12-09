package deimos.phase1.gui;

import java.io.IOException;

import deimos.phase1.gui.model.Person;
import deimos.phase1.gui.view.HelperOverviewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelperApp extends Application {

    private Stage primaryStage;
    private VBox rootLayout;
    private Person currentPerson;
    
    public HelperApp() {

    	currentPerson = new Person();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Deimos Helper");

        initRootLayout();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HelperApp.class.getResource("view/HelperOverview.fxml"));
            rootLayout = (VBox) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setResizable(false);
            
            HelperOverviewController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}