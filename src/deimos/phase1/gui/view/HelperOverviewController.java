package deimos.phase1.gui.view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.sqlite.SQLiteException;

import deimos.common.BrowserCheck;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * View-controller for HelperOverview.
 * 
 * @author Siddhesh Karekar
 *
 */

public class HelperOverviewController {
	
	// TODO unclutter code

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
    ImageView browserIcon;
    @FXML
    private Label browserLabel;
    @FXML
    private Label progressCookiesLabel;
    @FXML
    private ProgressBar progressCookiesBar;
    @FXML
    private Label progressHistoryLabel;
    @FXML
    private ProgressBar progressHistoryBar;
    @FXML
    private Label progressBookmarksLabel;
    @FXML
    private ProgressBar progressBookmarksBar;
    @FXML
    private Label progressIPLabel;
    @FXML
    private ProgressBar progressPublicIPBar;
    @FXML
    private Button startButton;

    // Reference to the main application.
	private HelperApp mainApp;
	
	private String licenseText;
	
	
    
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public HelperOverviewController() {
    	
    	System.out.println("Started Deimos Helper GUI.");
    	
    }
    
    /**
     * Sets the saturation of the image used as the browser icon.
     * Possible application could be greying out the browser image
     * if something stops working.
     * @param value A double between -1.0 and +1.0
     */
    public void saturateBrowserIcon(double value) {
    	ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);
        browserIcon.setEffect(monochrome);
    }
    
    /**
     * Once we know that a browser is available to collect data,
     * we can enable the controls used to start the process.
     * @return
     */
    private void setControlsDisabled(boolean disable) {

    	browserIcon.setDisable(disable);
    	browserLabel.setDisable(disable);
    	progressCookiesLabel.setDisable(disable);
    	progressCookiesBar.setDisable(disable);
    	progressHistoryLabel.setDisable(disable);
    	progressHistoryBar.setDisable(disable);
    	progressBookmarksLabel.setDisable(disable);
    	progressBookmarksBar.setDisable(disable);
    	progressIPLabel.setDisable(disable);
    	progressPublicIPBar.setDisable(disable);
    	startButton.setDisable(disable);

    }
    
    // All the initialization methods
    
    private void initalizeLicense(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            licenseText = stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
    
    private void initializeTosAgree() {
    	
    	tosAgreeLabel.setOnMouseClicked(e -> {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Terms of Service");
    		alert.setHeaderText("Deimos Helper Terms of Service");

    		Label label = new Label("Please read the following..");
    		
    		try {
				initalizeLicense("src/deimos/phase1/gui/view/helperlicense.txt");
			} catch (IOException e1) {
				
				e1.printStackTrace();
				licenseText = "Error loading license file";
			}
    		
    		TextArea textArea = new TextArea(licenseText);
    		textArea.setEditable(false);
    		textArea.setWrapText(true);

    		textArea.setMaxWidth(Double.MAX_VALUE);
    		textArea.setMaxHeight(Double.MAX_VALUE);
    		GridPane.setVgrow(textArea, Priority.ALWAYS);
    		GridPane.setHgrow(textArea, Priority.ALWAYS);

    		GridPane content = new GridPane();
    		content.setMaxWidth(Double.MAX_VALUE);
    		content.add(label, 0, 0);
    		content.add(textArea, 0, 1);

    		// Set expandable Exception into the dialog pane.
    		alert.getDialogPane().setContent(content);

    		alert.showAndWait();
    	});
    	tosAgreeLabel.setTooltip(new Tooltip("You must agree to the Deimos Helper Terms of Service."));
	}
    
    private void initializeBrowserCheck() {
    	taskBrowserCheck.setOnRunning(e -> {
    		browserLabel.setText("Checking for installed browsers...");
    	});
    	taskBrowserCheck.setOnSucceeded(e -> {
    		browserIcon.setImage(new Image("./deimos/phase1/gui/view/icon_Chrome.png"));
    		browserLabel.setText("Google Chrome loaded.");
    		mainApp.getPrimaryStage().setTitle(mainApp.title + " - " + "Google Chrome loaded");
    		setControlsDisabled(false);
    	});
    	taskBrowserCheck.setOnCancelled(e -> {
    		System.err.println("No compatible browsers available!");
    		
    	});
    	taskBrowserCheck.setOnFailed(e -> {
    		taskBrowserCheck.getException().printStackTrace();
    	});
    	Thread t = new Thread(taskBrowserCheck);
		t.setDaemon(true); // thread will not prevent application shutdown
		t.start();
    }
    
    private void initializeCookiesExport() {
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
    	    	
    	    	Alert alertChromeOpen = new Alert(AlertType.ERROR);
    	    	alertChromeOpen.initOwner(mainApp.getPrimaryStage());
    	    	alertChromeOpen.setContentText("Please make sure Google Chrome is not running, then click on Start again.");
    	    	alertChromeOpen.setTitle("Cookie Export Failed");
    	    	alertChromeOpen.showAndWait();
    	    	
    	    	if(taskHistory.isRunning()) {
    	    		taskHistory.cancel();
    	    	}
    	    	
    	    	startButton.setDisable(false);
    	    }
    	});
    }
    private void initializeBookmarksExport() {
    	taskBookmarks.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressBookmarksBar.setProgress(1);
    	    }
    	});
    }
    private void initializeHistoryExport() {
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
    	    	
    	    	startButton.setDisable(false);
    	    }
    	});
    }
    private void initializePublicIPExport() {
    	taskPublicIP.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressPublicIPBar.setProgress(1);
    	    }
    	});
    }
    
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    	initializeBrowserCheck();
    	initializeTosAgree();
    	initializeCookiesExport();
    	initializeBookmarksExport();
    	initializeHistoryExport();
    	initializePublicIPExport();
    }


	/**
     * Is called by the main application to give a reference back to itself.
     * @param mainApp
     */
    public void setMainApp(HelperApp mainApp) {
        this.mainApp = mainApp;
    }

    public void handleStartButton() {
    	
    	// check if "i agree"
    	if(tosAgreeCheckBox.isSelected())
    	{

    		progressCookiesBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    		tC = new Thread(taskCookies);
    		tC.setDaemon(true); // thread will not prevent application shutdown
    		tC.start();

    		progressBookmarksBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    		tB = new Thread(taskBookmarks);
    		tB.setDaemon(true); // thread will not prevent application shutdown
    		tB.start();

    		progressHistoryBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    		tH = new Thread(taskHistory);
    		tH.setDaemon(true); // thread will not prevent application shutdown
    		tH.start();

    		progressPublicIPBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    		tP = new Thread(taskPublicIP);
    		tP.setDaemon(true); // thread will not prevent application shutdown
    		tP.start();
    		
    		startButton.setDisable(true);
    		
    		tCompletionWait = new Thread(new Runnable() {
    	    	@Override
    	    	public void run() {
    	    		try {
    					tC.join();
    					tB.join();
    					tH.join();
    					tP.join();
    					
    					startButton.setDisable(false);
    					
    				} catch (InterruptedException e) {
    					
    					e.printStackTrace();
    				}
    	    		
    	    		System.out.println("All threads completed!");
    	    	}
    	    });
    		tCompletionWait.start();
    	}
    	else 
    	{
    		Alert alertTosAgree = new Alert(AlertType.WARNING);
    		alertTosAgree.initOwner(mainApp.getPrimaryStage());
    		alertTosAgree.setContentText("Click on the checkbox next to 'I Agree', then click on Start again.");
    		alertTosAgree.setTitle("You must agree to the Deimos Helper ToS");
    		alertTosAgree.showAndWait();
    	}

    }

    // The Tasks
    /**
     * Threads used to start tasks for each export activity.
     */
    private Thread tC, tB, tH, tP;
    /**
     * Thread that enables start button upon completion.
     */
    private Thread tCompletionWait;
    
    private final Task<Void> taskCookies = new Task<Void>() {
        @Override
        public Void call() throws SQLiteException
        {
        	ExportCookies.retreiveCookiesAsFile("export-cookies.txt");
           	return null;
        }
    };
    
    private final Task<Void> taskBookmarks = new Task<Void>() {
        @Override
        public Void call(){
        	ExportBookmarks.retreiveBookmarksAsFile("export-bookmarks.txt");
           	return null;
        }
    };
    
    private final Task<Void> taskHistory = new Task<Void>() {
        @Override
        public Void call() throws SQLiteException
        {
        	ExportHistory.retreiveHistoryAsFile("export-history.txt");
        	return null;
        }
    };
    
    private final Task<Void> taskPublicIP = new Task<Void>() {
        @Override
        public Void call(){
        	ExportIP.retrievePublicIPAsFile("export-publicIP.txt");
           	return null;
        }
    };
    
    
    /**
     * uses BrowserCheck to check if a browser is available,
     * if it is, controls should be enabled on this task's success.
     */
    private final Task<Void> taskBrowserCheck = new Task<Void>() {
    	
    	// Check if Google Chrome can be used
        public Void call(){
        	
        	// TODO Remove this later! Used to simulate a delay
        	try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
        	if(BrowserCheck.isChromeAvailable()) {
        		
        		System.out.println("Google Chrome is available.");
        		
        	}
        	else {
        		
        		this.cancel();
        	}
           	return null;
        }
    };
    

}