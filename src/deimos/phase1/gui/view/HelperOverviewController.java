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
import deimos.phase1.ExportUserInfo;
import deimos.phase1.gui.HelperApp;
import javafx.collections.FXCollections;
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
    private TextField yearOfBirthTextField;
    @FXML
    private CheckBox tosAgreeCheckBox;
    @FXML
    private Label tosAgreeLabel;
    @FXML
    private ImageView browserIcon;
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
     * Not used currently.
     * @param value A double between -1.0 and +1.0
     */
    @SuppressWarnings("unused")
	private void saturateBrowserIcon(double value) {
    	ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);
        browserIcon.setEffect(monochrome);
    }
    
    
    /**
     * Once we know that a browser is available to collect data,
     * we can enable the controls used to start the process.
     * @param disable
     */
    private void setUsageControlsDisabled(boolean disable) {

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
    
    /**
     * Controls the disable status of 'About You'.
     * May be enabled when the browser is loaded.
     * @param disable
     */
    private void setInputControlsDisabled(boolean disable) {
    	
    	firstNameTextField.setDisable(disable);
    	lastNameTextField.setDisable(disable);
    	genderChoiceBox.setDisable(disable);
    	yearOfBirthTextField.setDisable(disable);
    	tosAgreeCheckBox.setDisable(disable);
    	tosAgreeLabel.setDisable(disable);
    }
  
    // All the initialization methods
    
    /**
     * Loads the externally stored License text file
     * into the String licenseText.
     * @param file Path to the license file.
     * @throws IOException
     */
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
    		setUsageControlsDisabled(false);
    		setInputControlsDisabled(false);
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
    private void initializeGenderChoiceBox() {
    	genderChoiceBox.setItems(FXCollections.observableArrayList(
        	    "Gender", "Male", "Female"));
        	genderChoiceBox.getSelectionModel().selectFirst();
    }
    /**
     * For the lazy programmer: fills the input fields
     * with sample input to help speed up testing.
     * DO NOT KEEP IN FINAL!
     */
    @SuppressWarnings("unused")
	private void initializeInputDefaults() {
    	
    	firstNameTextField.setText("John");
    	lastNameTextField.setText("Doe");
    	genderChoiceBox.getSelectionModel().select(1);
    	yearOfBirthTextField.setText("1995");
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
    	initializeGenderChoiceBox();
    	// initializeInputDefaults();
    }


	/**
     * Is called by the main application to give a reference back to itself.
     * @param mainApp
     */
    public void setMainApp(HelperApp mainApp) {
        this.mainApp = mainApp;
    }
    
    /**
     * Validates the 'About You' input section, and returns
     * the corresponding error message (if any) which can be displayed in the alert.
     * @return String empty if no error; non-empty string containing errors,
     * if errors found in input validation. The String tells the user how to fix
     * the error in input validation.
     */
    private String getInputValidationError() {
    	
    	String errors = "";
    	
    	if(firstNameTextField.getText().trim().isEmpty()) {
    		errors = errors + ("First Name cannot be empty.\n");
    	}
    	if(lastNameTextField.getText().trim().isEmpty()) {
    		errors = errors + ("Last Name cannot be empty.\n");
    	}
    	if(genderChoiceBox.getSelectionModel().isSelected(0)) {
    		errors = errors + ("Gender must be Male or Female.\n");
    	}
    	if(yearOfBirthTextField.getText().trim().isEmpty()) {
    		errors = errors + ("Year of Birth cannot be empty.\n");
    	} else {
    		
    		try {
				int year = Integer.parseInt(yearOfBirthTextField.getText().trim());
				
				if(year < 1900 || year > 2015) {
					errors = errors + ("Year of Birth must be a valid number.\n");
				}
				
			} catch (NumberFormatException e) {

				errors = errors + ("Year of Birth must be numeric.\n");
			}
    	}

    	return errors;
    }
    
    @FXML
    private void handleStartButton() {
    	// check if all user details are filled
    	String validationError = getInputValidationError();
    	if(validationError.isEmpty()) {

    		// check if "i agree"
    		if(tosAgreeCheckBox.isSelected())
    		{

    			progressCookiesBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    			threadCookies = new Thread(taskCookies);
    			threadCookies.setDaemon(true); // thread will not prevent application shutdown
    			threadCookies.start();

    			progressBookmarksBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    			threadBookmarks = new Thread(taskBookmarks);
    			threadBookmarks.setDaemon(true); // thread will not prevent application shutdown
    			threadBookmarks.start();

    			progressHistoryBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    			threadHistory = new Thread(taskHistory);
    			threadHistory.setDaemon(true); // thread will not prevent application shutdown
    			threadHistory.start();

    			progressPublicIPBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    			threadPublicIP = new Thread(taskPublicIP);
    			threadPublicIP.setDaemon(true); // thread will not prevent application shutdown
    			threadPublicIP.start();
    			
    			threadUserInfo = new Thread(taskUserInfo);
    			threadUserInfo.setDaemon(true); // thread will not prevent application shutdown
    			threadUserInfo.start();

    			startButton.setDisable(true);

    			threadCompletionWait = new Thread(new Runnable() {
    				@Override
    				public void run() {
    					try {
    						threadCookies.join();
    						threadBookmarks.join();
    						threadHistory.join();
    						threadPublicIP.join();
    						threadUserInfo.join();

    						startButton.setDisable(false);

    					} catch (InterruptedException e) {

    						e.printStackTrace();
    					}

    					System.out.println("All threads completed!");
    				}
    			});
    			threadCompletionWait.start();
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
    	else {

			Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error in User Input");
    		alert.setHeaderText("Input Validation Error");

    		Label label = new Label("Please fix the following errors:");
    		
    		TextArea textArea = new TextArea(validationError);
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
    		
    		alert.getDialogPane().setContent(content);
    		alert.showAndWait();
    	}
    }

    // The Tasks TODO Change them to services
    /**
     * Threads used to start tasks for each export activity.
     */
    private Thread threadCookies, threadBookmarks, threadHistory, threadPublicIP, threadUserInfo;
    /**
     * Thread that enables start button upon completion.
     */
    private Thread threadCompletionWait;
    
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
    private final Task<Void> taskUserInfo = new Task<Void>() {
        @Override
        public Void call(){
        	ExportUserInfo.retrieveUserInfoAsFile(firstNameTextField.getText(),
        			lastNameTextField.getText(),
        			genderChoiceBox.getSelectionModel().getSelectedItem(),
        			Integer.parseInt(yearOfBirthTextField.getText()),
        			"export-userInfo.txt");
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
				Thread.sleep(1000);
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