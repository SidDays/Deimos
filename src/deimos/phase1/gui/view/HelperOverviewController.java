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
    @SuppressWarnings("unused")
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
        public Void call() {
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
        public Void call() {
        	try {
				ExportHistory.retreiveHistoryAsFile("export-history.txt");
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
    	
    	taskCookies.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressCookiesBar.setProgress(1);
    	    }
    	});
    	
    	taskBookmarks.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressBookmarksBar.setProgress(1);
    	    }
    	});
    	
    	taskHistory.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressHistoryBar.setProgress(1);
    	    }
    	});
    	
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