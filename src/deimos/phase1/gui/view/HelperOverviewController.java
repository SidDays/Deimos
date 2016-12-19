package deimos.phase1.gui.view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;

import org.sqlite.SQLiteException;

import deimos.common.BrowserCheck;
import deimos.phase1.ExportBookmarks;
import deimos.phase1.ExportCookies;
import deimos.phase1.ExportHistory;
import deimos.phase1.ExportIP;
import deimos.phase1.ExportUserInfo;
import deimos.phase1.gui.HelperApp;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
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
	
	// In order;
	
	private static interface ServiceConstants {
		int BOOKMARKS = 0;
		int COOKIES = 1;
		int HISTORY = 2;
		int PUBLICIP = 3;
		int USERINFO = 4;
		int NUM_FLAGS = 5;
	}
	
	private boolean[] flags = new boolean[ServiceConstants.NUM_FLAGS];
	
	private BookmarkService serviceBookmarks;
	private CookieService serviceCookies;
	private HistoryService serviceHistory;
	private PublicIPService servicePublicIP;
	private UserInfoService serviceUserInfo;
	
	private BrowserCheckService serviceBrowserCheck;
    
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
    
    private boolean isAllFlagsEnabled() {
    	
    	boolean result = true;
    	for(boolean b : flags)
    		result = result&b;
    	return result;
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
    	
    	serviceBrowserCheck = new BrowserCheckService();
    	
    	serviceBrowserCheck.setOnRunning(e -> {
    		browserLabel.setText("Checking for installed browsers...");
    	});
    	serviceBrowserCheck.setOnSucceeded(e -> {
    		browserIcon.setImage(new Image("./deimos/phase1/gui/view/icon_Chrome.png"));
    		browserLabel.setText("Google Chrome loaded.");
    		mainApp.getPrimaryStage().setTitle(mainApp.title + " - " + "Google Chrome loaded");
    		setUsageControlsDisabled(false);
    		setInputControlsDisabled(false);
    	});
    	serviceBrowserCheck.setOnCancelled(e -> {
    		System.err.println("No compatible browsers available!");
    		
    	});
    	serviceBrowserCheck.setOnFailed(e -> {
    		serviceBrowserCheck.getException().printStackTrace();
    	});

		serviceBrowserCheck.start();
    }
    
    private void initializeCookiesExport() {
    	
    	serviceCookies = new CookieService();
    	
    	serviceCookies.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressCookiesBar.setProgress(1);
    	    	
        		flags[ServiceConstants.COOKIES] = true;
        		
        		if(isAllFlagsEnabled())
        			startButton.setDisable(false);
    	    }
    	});
    	serviceCookies.setOnCancelled(e -> { 
    		System.out.println("Cookie export cancelled.");
    		progressCookiesBar.setProgress(0);
    	});
    	serviceCookies.setOnFailed(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	
    	    	System.out.println("Cookie export failed: "+serviceCookies.getException());
    	    	progressCookiesBar.setProgress(0);
    	    	
    	    	Alert alertChromeOpen = new Alert(AlertType.ERROR);
    	    	alertChromeOpen.initOwner(mainApp.getPrimaryStage());
    	    	alertChromeOpen.setContentText("Please make sure Google Chrome is not running, then click on Start again.");
    	    	alertChromeOpen.setTitle("Cookie Export Failed");
    	    	alertChromeOpen.showAndWait();
    	    	
    	    	if(serviceHistory.isRunning()) {
    	    		serviceHistory.cancel();
    	    	}
    	    	
    	    	startButton.setDisable(false);
    	    }
    	});
    }
    private void initializeBookmarksExport() {
    	
    	serviceBookmarks = new BookmarkService();
    	
    	serviceBookmarks.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressBookmarksBar.setProgress(1);

    	    	flags[ServiceConstants.BOOKMARKS] = true;

    	    	if(isAllFlagsEnabled())
    	    		startButton.setDisable(false);
    	    }
    	});
    }
    private void initializeHistoryExport() {
    	serviceHistory = new HistoryService();
    	
    	serviceHistory.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressHistoryBar.setProgress(1);
    	    	
    	    	flags[ServiceConstants.HISTORY] = true;

    	    	if(isAllFlagsEnabled())
    	    		startButton.setDisable(false);
    	    }
    	});
    	serviceHistory.setOnCancelled(e -> { 
    		System.out.println("History export cancelled.");
    		progressHistoryBar.setProgress(0);
    		
    	});
    	serviceHistory.setOnFailed(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	
    	    	System.out.println("History export failed: "+serviceHistory.getException());
    	    	progressHistoryBar.setProgress(0);
    	    	
    	    	Alert alertChromeOpen = new Alert(AlertType.ERROR);
    	    	alertChromeOpen.initOwner(mainApp.getPrimaryStage());
    	    	alertChromeOpen.setContentText("Please make sure Google Chrome is not running, then click on Start again.");
    	    	alertChromeOpen.setTitle("History Export Failed");
    	    	alertChromeOpen.showAndWait();
    	    	
    	    	if(serviceCookies.isRunning()) {
    	    		serviceCookies.cancel();
    	    	}
    	    	
    	    	startButton.setDisable(false);
    	    }
    	});
    }
    private void initializePublicIPExport() {
    	
    	servicePublicIP = new PublicIPService();
    	
    	servicePublicIP.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	progressPublicIPBar.setProgress(1);
    	    	
    	    	flags[ServiceConstants.PUBLICIP] = true;

    	    	if(isAllFlagsEnabled())
    	    		startButton.setDisable(false);
    	    }
    	});
    	
    	servicePublicIP.setOnFailed(new EventHandler<WorkerStateEvent>() {
    	    @Override
    	    public void handle(WorkerStateEvent event) {
    	    	
    	    	System.out.println("Public IP export failed: "+servicePublicIP.getException());
    	    	progressPublicIPBar.setProgress(0);
    	    	
    	    	Alert alert = new Alert(AlertType.ERROR);
    	    	alert.initOwner(mainApp.getPrimaryStage());
    	    	alert.setContentText("Please make sure your internet connection is working, and click Start again.");
    	    	alert.setTitle("Public IP Export Failed");
    	    	alert.showAndWait();
    	    	
    	    	startButton.setDisable(false);
    	    }
    	});
    }
    
    private void initializeUserInfoExport() {
    	
    	serviceUserInfo = new UserInfoService();
    	
    	serviceUserInfo.setOnSucceeded(e -> {
    		flags[ServiceConstants.USERINFO] = true;

	    	if(isAllFlagsEnabled())
	    		startButton.setDisable(false);
    	});
    	
    	// Lazy
    	/*firstNameTextField.setText("John");
    	lastNameTextField.setText("Doe");
    	genderChoiceBox.getSelectionModel().select(1);
    	yearOfBirthTextField.setText("1995");*/
    }
    
    private void initializeGenderChoiceBox() {
    	genderChoiceBox.setItems(FXCollections.observableArrayList(
        	    "Gender", "Male", "Female"));
        	genderChoiceBox.getSelectionModel().selectFirst();
    }
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    	for(int i = 0; i<flags.length; i++)
    		flags[i] = false;
    	
    	initializeBrowserCheck();
    	initializeTosAgree();
    	initializeCookiesExport();
    	initializeBookmarksExport();
    	initializeHistoryExport();
    	initializePublicIPExport();
    	initializeGenderChoiceBox();
    	initializeUserInfoExport();
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
    
    private void startAgain(Service<?> s) {
    	if(s.isRunning())
    		s.restart();
    	else {
    		s.reset();
    		s.start();
    	}
    }
    
    @FXML
    private void handleStartButton() {
    	
    	// check if all user details are filled
    	String validationError = getInputValidationError();
    	if(validationError.isEmpty()) {

    		// check if "i agree"
    		if(tosAgreeCheckBox.isSelected())
    		{
    			
    			System.out.println("\nBeginning export...");
    			
    			// clear all usage flags
    			for(int i = 0; i<flags.length; i++)
    	    		flags[i] = false;

    			progressCookiesBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    			startAgain(serviceCookies);

    			progressBookmarksBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    			startAgain(serviceBookmarks);
    			
    			progressHistoryBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    			startAgain(serviceHistory);

    			progressPublicIPBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    			startAgain(servicePublicIP);
    			
    			startAgain(serviceUserInfo);

    			startButton.setDisable(true);
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
    
    private class CookieService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {
			
			return new Task<Void>() {
	            @Override
	            public Void call() throws SQLiteException
	            {
	            	ExportCookies.retreiveCookiesAsFile("export-cookies.txt");
	               	return null;
	            }
	        };
		}

    }

    private class BookmarkService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {
	            @Override
	            public Void call(){
	            
	            	ExportBookmarks.retreiveBookmarksAsFile("export-bookmarks.txt");
	               	return null;
	            }
			};
		}
    	
    }
    
    private class HistoryService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {
	            @Override
	            public Void call() throws SQLiteException
	            {
	            	ExportHistory.retreiveHistoryAsFile("export-history.txt");
	            	return null;
	            }
	        };
		}
    	
    }
    
    private class PublicIPService extends Service<Void> {

    	@Override
    	protected Task<Void> createTask() {

    		return new Task<Void>() {
    			@Override
    			public Void call() throws UnknownHostException {
    				ExportIP.retrievePublicIPAsFile("export-publicIP.txt");

    				return null;
    			}
    		};
    	}

    }
    
    private class UserInfoService extends Service<Void> {
    	
    	@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {
				
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
		}
    }
    
    /**
     * uses BrowserCheck to check if a browser is available,
     * if it is, controls should be enabled on its success.
     * Not a Usage Service
     */
    private class BrowserCheckService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {
	        	
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
    }
}