package deimos.phase1.gui.view;

import org.sqlite.SQLiteException;

import deimos.phase1.ExportBookmarks;
import deimos.phase1.ExportCookies;
import deimos.phase1.ExportHistory;
import deimos.phase1.ExportIP;
import deimos.phase1.gui.HelperApp;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

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
    private ChoiceBox<String> genderChoiceBox;
    @FXML
    private ChoiceBox<Integer> yearOfBirthChoiceBox;
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
    
    // The Tasks
    
    final Task<Void> taskCookies = new Task<Void>() {
        @Override
        public Void call() throws SQLiteException
        {
        	ExportCookies.retreiveCookiesAsFile("export-cookies.txt");
           	return null;
        }
    };
    
    final Task<Void> taskBookmarks = new Task<Void>() {
        @Override
        public Void call(){
        	ExportBookmarks.retreiveBookmarksAsFile("export-bookmarks.txt");
           	return null;
        }
    };
    
    final Task<Void> taskHistory = new Task<Void>() {
        @Override
        public Void call() throws SQLiteException
        {
        	ExportHistory.retreiveHistoryAsFile("export-history.txt");
        	return null;
        }
    };
    
    final Task<Void> taskPublicIP = new Task<Void>() {
        @Override
        public Void call(){
        	ExportIP.retrievePublicIPAsFile("export-publicIP.txt");
           	return null;
        }
    };
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    	System.out.println("Started Deimos Helper GUI.");
    	
    	// Cookies
    	taskCookies.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressCookiesBar.setProgress(1);
    	    }
    	});
    	taskCookies.setOnCancelled(e -> { 
    		System.out.println("Cookie export cancelled.");
    		progressCookiesBar.setProgress(0);
    	});
    	taskCookies.setOnFailed(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	
    	    	System.out.println("Cookie export failed: "+taskCookies.getException());
    	    	progressCookiesBar.setProgress(0);
    	    	
    	    	taskHistory.cancel();
    	    	
    	    	Alert alertChromeOpen = new Alert(AlertType.ERROR);
    	    	alertChromeOpen.initOwner(mainApp.getPrimaryStage());
    	    	alertChromeOpen.setContentText("Please make sure Google Chrome is not running, then click on Start again.");
    	    	alertChromeOpen.setTitle("Cookie Export Failed");
    	    	alertChromeOpen.showAndWait();
    	    }
    	});
    	
    	// Bookmarks
    	taskBookmarks.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressBookmarksBar.setProgress(1);
    	    }
    	});
    	
    	
    	// History
    	taskHistory.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressHistoryBar.setProgress(1);
    	    }
    	});
    	taskHistory.setOnCancelled(e -> { 
    		System.out.println("History export cancelled.");
    		progressHistoryBar.setProgress(0);
    	});
    	taskHistory.setOnFailed(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	
    	    	System.out.println("History export failed: "+taskHistory.getException());
    	    	progressHistoryBar.setProgress(0);
    	    	
    	    	Alert alertChromeOpen = new Alert(AlertType.ERROR);
    	    	alertChromeOpen.initOwner(mainApp.getPrimaryStage());
    	    	alertChromeOpen.setContentText("Please make sure Google Chrome is not running, then click on Start again.");
    	    	alertChromeOpen.setTitle("History Export Failed");
    	    	alertChromeOpen.showAndWait();
    	    	
    	    	if(taskCookies.isRunning()) {
    	    		taskCookies.cancel();
    	    	}
    	    }
    	});
    	
    	// Public IP
    	taskPublicIP.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressPublicIPBar.setProgress(1);
    	    }
    	});
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(HelperApp mainApp) {
        this.mainApp = mainApp;

    }

    public void handleStartButton() {
    	
		progressCookiesBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		Thread tC = new Thread(taskCookies);
		tC.setDaemon(true); // thread will not prevent application shutdown
		tC.start();
		
		progressBookmarksBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		Thread tB = new Thread(taskBookmarks);
		tB.setDaemon(true); // thread will not prevent application shutdown
		tB.start();
		
		progressHistoryBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		Thread tH = new Thread(taskBookmarks);
		tH.setDaemon(true); // thread will not prevent application shutdown
		tH.start();
		
		progressPublicIPBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		Thread tP = new Thread(taskPublicIP);
		tP.setDaemon(true); // thread will not prevent application shutdown
		tP.start();

    }
    

}