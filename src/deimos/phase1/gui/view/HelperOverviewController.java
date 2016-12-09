package deimos.phase1.gui.view;

import org.sqlite.SQLiteException;

import deimos.phase1.ExportBookmarks;
import deimos.phase1.ExportCookies;
import deimos.phase1.ExportHistory;
import deimos.phase1.ExportIP;
import deimos.phase1.gui.HelperApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;

/**
 * View-controller for HelperOverview.
 * 
 * @author Siddhesh Karekar
 *
 */

public class HelperOverviewController {
	

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private ChoiceBox genderChoiceBox;
    @FXML
    private ChoiceBox yearOfBirthChoiceBox;
    @FXML
    private CheckBox tosAgreeCheckBox;
    @FXML
    private Label tosAgreeLabel;
    @FXML
    private Label browserLabel;
    @FXML
    private ProgressBar progressCookiesBar;
    @FXML
    private ProgressBar progressHistoryBar;
    @FXML
    private ProgressBar progressBookmarksBar;
    @FXML
    private ProgressBar progressPublicIPBar;
    @FXML
    private Button startButton;

    // Reference to the main application.
    private HelperApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public HelperOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        // firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        // lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(HelperApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        // personTable.setItems(mainApp.getPersonData());
    }
    
    public void exportCookies() {
    	
    	progressCookiesBar.setProgress(0);
    	progressCookiesBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		ExportCookies.retreiveCookiesAsFile("export-cookies.txt");
		progressCookiesBar.setProgress(1);

    }

    public void exportBookmarks() {
    	
    	progressBookmarksBar.setProgress(0);
    	progressBookmarksBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    	ExportBookmarks.retreiveBookmarksAsFile("export-bookmarks.txt");
    	progressBookmarksBar.setProgress(1);

    }

    public void exportHistory() throws SQLiteException {
    	
    	progressHistoryBar.setProgress(0);
    	progressHistoryBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    	ExportHistory.retreiveHistoryAsFile("export-history.txt");
    	progressHistoryBar.setProgress(1);
    }

    public void getPublicIP() {
    	
    	progressPublicIPBar.setProgress(0);
    	progressPublicIPBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		ExportIP.retrievePublicIPAsFile("export-publicIP.txt");
		progressPublicIPBar.setProgress(1);
    }
    
    public void handleStartButton() {
    	
    	exportCookies();
    	exportBookmarks();
    	
    	try {
    		exportHistory();
    	}
    	catch (SQLiteException sle) {

    		System.out.println("Database locked? "+sle);
    		progressHistoryBar.setProgress(0);

    		Alert alert = new Alert(AlertType.ERROR);
    		alert.initOwner(mainApp.getPrimaryStage());
    		alert.setTitle("Error");
    		alert.setHeaderText("Database Locked");
    		alert.setContentText("Please make sure Google Chrome is not running, then click on Start again.");
    		alert.showAndWait();
    	}
    	
    	getPublicIP();
    	
    }
}