package deimos.gui.view;

import deimos.gui.DeimosApp;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * References:
 * stackoverflow.com/questions/13227809/displaying-changing-values-in-javafx-label
 * 
 * @author Bhushan Pathak
 * @author Siddhesh Karekar
 */
public class DeimosAppOverviewController {

	@SuppressWarnings("unused") // TODO find a use for this
	private DeimosApp mainApp;
	
	@FXML
	private VBox vboxAnalyze;
	@FXML
	private AnalyzeController vboxAnalyzeController;
	
	@FXML
	private VBox vboxTraining;
	@FXML
	private TrainPredictController vboxTrainingController;
	
	
	public DeimosAppOverviewController() {
		System.out.println("Started Deimos Application GUI.");
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

	}
	
	
	/**
	 * Is called by the main application to give a reference back to itself.
	 * @param mainApp
	 */
	public void setMainApp(DeimosApp mainApp) {
		this.mainApp = mainApp;
	}
}
