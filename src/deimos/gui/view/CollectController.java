package deimos.gui.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import deimos.common.DeimosConfig;
import deimos.common.DeimosImages;
import deimos.common.GUIUtils;
import deimos.common.Mailer;
import deimos.gui.DeimosApp;
import deimos.phase1.ExportAll;
import deimos.phase1.ExportUserInfo;
import deimos.phase1.Zipper;
import deimos.phase1.gui.view.services.BrowserCheckService;
import deimos.phase1.gui.view.services.HistoryService;
import deimos.phase1.gui.view.services.KillChromeService;
import deimos.phase1.gui.view.services.PublicIPService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.fxml.FXML;

public class CollectController {

	private static final String FILE_LICENSE_GUI = "resources/license/helperlicense.txt";

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
	private ProgressBar progressHistoryBar;
	@FXML
	private ProgressBar progressPublicIPBar;
	@FXML
	private ProgressBar progressMailBar;
	@FXML
	private Label collectionStatus;
	@FXML
	private Button collectButton;

	private HistoryService serviceHistory;
	private PublicIPService servicePublicIP;
	private BrowserCheckService serviceBrowserCheck;
	private KillChromeService serviceKillChrome;
	private UserInfoService serviceUserInfo;
	private MailerService serviceMailer;

	private String licenseText;
	private DeimosApp mainApp;

	/**
	 * Is called by the main application to give a reference back to itself.
	 * @param mainApp
	 */
	public void setMainApp(DeimosApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * In order.
	 */
	private static interface ServiceConstants {
		int HISTORY = 0;
		int PUBLICIP = 1;
		int USERINFO = 2;
		int NUM_FLAGS = 3;
	}

	private boolean[] flags = new boolean[ServiceConstants.NUM_FLAGS];

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
		initializeHistoryExport();
		initializePublicIPExport();
		initializeGenderChoiceBox();
		initializeUserInfoExport();

		initializeMailer();
	}

	/**
	 * Once we know that a browser is available to collect data,
	 * we can enable the controls used to start the process.
	 * @param disable
	 */
	private void setUsageControlsDisabled(boolean disable) {

		browserIcon.setDisable(disable);
		browserLabel.setDisable(disable);

		progressHistoryBar.setDisable(disable);
		progressPublicIPBar.setDisable(disable);
		collectButton.setDisable(disable);
	}

	private void setMailControlsDisabled(boolean disable) {
		progressMailBar.setDisable(disable);
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

	/**
	 * Checks if all sub-tasks are complete (except mailing)
	 * @return true if all of the 5 required processes are done.
	 */
	private boolean isAllFlagsEnabled() {

		boolean result = true;
		for(boolean b : flags)
			result = result&b;
		return result;
	}

	private void setInputControlsStartEnabledZIPAndMailIfComplete() {
		if(isAllFlagsEnabled()) {

			// TODO This is being done on the main thread because I'm a lazy fuck.
			Zipper.zipOutputFiles();

			// LEAVE NO TRACES! bwahaha
			if(DeimosConfig.OPTION_DELETE_P1_OUTPUT)
				ExportAll.deleteOutputFiles();

			browserLabel.setText("Exported output. Mailing...");
			browserIcon.setImage(DeimosImages.IMG_STATE_EXPORTED);

			// Begin mailing
			progressMailBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			setMailControlsDisabled(false);
			GUIUtils.startAgain(serviceMailer);
		}
	}
	private void setInputControlsStartDisabled(boolean disable) {
		collectButton.setDisable(disable);
		setInputControlsDisabled(disable);
	}

	private void failure() {

		if(serviceHistory.isRunning()) {
			serviceHistory.cancel();
		}

		setInputControlsStartDisabled(false);
		browserIcon.setImage(DeimosImages.IMG_STATE_FAILED);
		browserLabel.setText("Fix the issues reported, then click on 'Start'.");

	}

	// All the initialization methods

	/**
	 * Loads the externally stored License text file
	 * into the String licenseText.
	 * @param file Path to the license file.
	 * @throws IOException
	 */
	private void initalizeLicense(String file) throws IOException {

		// wtf is this shit

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
				initalizeLicense(FILE_LICENSE_GUI);
			} catch (IOException e1) {

				e1.printStackTrace();
				licenseText = "Error loading license file";
			}

			TextArea textArea = new TextArea(licenseText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(380);
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

	/**
	 * Makes sure Google Chrome is available.
	 * If it is, also asks the user if Chrome should be closed
	 * automatically (otherwise, the user must close it manually -
	 * having it open may interfere with data collection.)
	 */
	private void initializeBrowserCheck() {

		browserIcon.setImage(DeimosImages.IMG_UNKNOWN);

		serviceBrowserCheck = new BrowserCheckService();

		serviceBrowserCheck.setOnRunning(e -> {
			browserLabel.setText("Checking for installed browsers...");
		});
		serviceBrowserCheck.setOnSucceeded(e -> {
			browserIcon.setImage(DeimosImages.IMG_CHROME);
			browserLabel.setText("Google Chrome found.");
			//mainApp.getPrimaryStage().setTitle(mainApp.title + " - " + "Google Chrome found");

			// TODO Kill Chrome
			serviceKillChrome = new KillChromeService();
			serviceKillChrome.setOnRunning(e1 -> {
				browserIcon.setImage(DeimosImages.IMG_STATE_RUNNING);
				browserLabel.setText("Closing Google Chrome...");
			});
			serviceKillChrome.setOnSucceeded(e1 -> {
				browserIcon.setImage(DeimosImages.IMG_CHROME);
				browserLabel.setText("Google Chrome found.");
				System.out.println("Successfully killed Chrome on user's request.");
			});
			serviceKillChrome.setOnFailed(e1 -> {
				browserIcon.setImage(DeimosImages.IMG_CHROME);
				browserLabel.setText("Google Chrome found.");
				System.out.println("Failed to kill Chrome on user's request. Please close it manually.");
			});

			if(ExportAll.isChromeRunning()) {

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("End Chrome if Running");
				alert.setHeaderText("Google Chrome must not be running");
				alert.setContentText("Close it automatically? If you wish to close it yourself, click on 'Cancel'.");
				alert.showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						// System.out.println("Tried to kill Chrome.");
						serviceKillChrome.start();
					}
				});
			}

			setUsageControlsDisabled(false);
			setInputControlsDisabled(false);
		});
		serviceBrowserCheck.setOnCancelled(e -> {
			System.err.println("No compatible browsers available!");
			browserLabel.setText("No compatible browsers available.");

		});
		serviceBrowserCheck.setOnFailed(e -> {
			serviceBrowserCheck.getException().printStackTrace();
		});

		serviceBrowserCheck.start();
	}
	
	private void initializeHistoryExport() {
		serviceHistory = new HistoryService();

		serviceHistory.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				progressHistoryBar.setProgress(1);
				collectionStatus.setText("Exporting Public IP");
				progressPublicIPBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				flags[ServiceConstants.HISTORY] = true;
				GUIUtils.startAgain(servicePublicIP);
				setInputControlsStartEnabledZIPAndMailIfComplete();
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

				failure();
			}
		});
	}
	private void initializePublicIPExport() {

		servicePublicIP = new PublicIPService();

		servicePublicIP.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				progressPublicIPBar.setProgress(1);
				collectionStatus.setText("Auto mailing to us");
				//progressMailBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				flags[ServiceConstants.PUBLICIP] = true;
				GUIUtils.startAgain(serviceUserInfo);
				setInputControlsStartDisabled(true);

				setMailControlsDisabled(true);
				progressMailBar.setProgress(0);
				setInputControlsStartEnabledZIPAndMailIfComplete();
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

				failure();
			}
		});
	}

	private void initializeUserInfoExport() {

		serviceUserInfo = new UserInfoService();

		serviceUserInfo.setOnSucceeded(e -> {
			flags[ServiceConstants.USERINFO] = true;

			setInputControlsStartEnabledZIPAndMailIfComplete();
		});

		// I'm lazy af

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

	private void initializeMailer() {

		serviceMailer = new MailerService();

		serviceMailer.setOnSucceeded(e -> {

			setInputControlsStartDisabled(false);
			progressMailBar.setProgress(1);

			browserLabel.setText("Thank you! You can move on to next tab.");
			collectionStatus.setText("Auto-mailed");
			collectButton.setDisable(true);
			/*collectButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					Platform.exit();
				}
			});*/

			browserIcon.setImage(DeimosImages.IMG_STATE_MAILED);
		});
		serviceMailer.setOnFailed(eh -> {

			setInputControlsStartDisabled(false);
			progressMailBar.setProgress(0);

			System.out.println("Mailing failed: "+serviceMailer.getException());
			browserLabel.setText("Exported, but not mailed. Click 'Re-mail', or mail\n'export-all.zip' to deimoskjsce@gmail.com");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error while mailing");
			alert.setHeaderText("There was an error while automatically mailing us the file.");    		
			alert.getDialogPane().setContentText("Click on 'Re-mail' to try again. If problem persists, "
					+ "please mail the generated 'export-all.zip' in the program's folder "
					+ "manually to deimoskjsce@gmail.com. (Check the manual for instructions.)");
			alert.showAndWait();

			// Set change the start button to a re-mail button
			collectButton.setText("Re-mail");
			collectButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					handleRemailbutton();
				}
			});

		});
	}
	
	private void handleRemailbutton() {

		// Check if the export-all.zip exists
		FileInputStream in;
		File exportAllZIP = new File(DeimosConfig.FILE_OUTPUT_ALL_ZIP);
		try {
			in = new FileInputStream(exportAllZIP);
			in.close();

			// by this point, it exists. re-email the file
			browserLabel.setText("Trying to mail us again...");
			browserIcon.setImage(DeimosImages.IMG_STATE_EXPORTED);

			// Begin mailing
			progressMailBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			setMailControlsDisabled(false);
			GUIUtils.startAgain(serviceMailer);

		}
		catch (FileNotFoundException e) {
			// it doesn't exist. re-enable the data collection

			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setContentText("Click on Retry to collect data and try again.");
			alert.setTitle("Export file missing");
			alert.showAndWait();

			collectButton.setText("Retry");
			collectButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					handleCollectButton();
				}
			});

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}


		// if not, change it to retry
	}
	
	private void generateAlerts(String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error in User Input");
		alert.setHeaderText("Input Validation Error");

		Label label = new Label("Please fix the following errors:");

		TextArea textArea = new TextArea(error);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(320);
		textArea.setMaxHeight(240);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane content = new GridPane();
		content.setMaxWidth(Double.MAX_VALUE);
		content.add(label, 0, 0);
		content.add(textArea, 0, 1);

		alert.getDialogPane().setContent(content);
		alert.showAndWait();
	}

	/**
	 * Validates the 'About You' input section, and returns
	 * the corresponding error message (if any) which can be displayed in the alert.
	 * @return String empty if no error; non-empty string containing errors,
	 * if errors found in input validation. The String tells the user how to fix
	 * the error in input validation.
	 */
	private String getInputValidationError() {

		StringBuilder errors = new StringBuilder();

		/*if(firstNameTextField.getText().trim().isEmpty()) {
    		errors = errors + ("First Name cannot be empty.\n");
    	}

    	if(lastNameTextField.getText().trim().isEmpty()) {
    		errors = errors + ("Last Name cannot be empty.\n");
    	}*/


		if(genderChoiceBox.getSelectionModel().isSelected(0)) {
			errors.append("Gender must be Male or Female.\n");
		}
		if(yearOfBirthTextField.getText().trim().isEmpty()) {
			errors.append("Year of Birth cannot be empty.\n");
		} else {

			try {
				int year = Integer.parseInt(yearOfBirthTextField.getText().trim());

				if(year < 1900 || year > 2015) {
					errors.append("Year of Birth must be a valid number.\n");
				}

			} catch (NumberFormatException e) {

				errors.append("Year of Birth must be numeric.\n");
			}
		}

		// check if "i agree"
		if(!tosAgreeCheckBox.isSelected())
		{
			errors.append("You must agree to the Deimos Helper Terms of Service.\n");
		}

		return errors.toString();
	}

	@FXML
	private void handleCollectButton() {

		String validationError = getInputValidationError();
		if(validationError.isEmpty()) {
			browserLabel.setText("Processing... Please wait.");
			browserIcon.setImage(DeimosImages.IMG_STATE_RUNNING);

			progressHistoryBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			collectionStatus.setText("Exporting browser history");
			GUIUtils.startAgain(serviceHistory);
		}

		else {
			generateAlerts(validationError);
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
							DeimosConfig.FILE_OUTPUT_USERINFO);
					return null;
				}
			};
		}
	}

	/**
	 * Not a Usage Service
	 */
	private class MailerService extends Service<Void> {

		/*private String title, body;

    	public void setTitleAndBody(String date, String firstName, String lastName,
    			String gender, String yearOfBirth)
    	{
    		title = "Training Data: "+date;
    		body = "dateOfCollection=" + date + "\n"
        			+ "firstName=" + firstName + "\n"
        			+ "lastName=" + lastName + "\n"
        			+ "gender="+ gender + "\n"
        			+ "yearOfBirth=" + yearOfBirth + "\n";
    	}*/

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {

				public Void call(){

					// TODO
					String date = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date());

					String body = "dateOfCollection=" + date + "\n"
							+ "firstName=" + firstNameTextField.getText() + "\n"
							+ "lastName=" + lastNameTextField.getText() + "\n"
							+ "gender="+ genderChoiceBox.getSelectionModel().getSelectedItem() + "\n"
							+ "yearOfBirth=" + yearOfBirthTextField.getText() + "\n";

					Mailer.mailToDeimosTeam("Training Data: "+date,
							body,
							DeimosConfig.FILE_OUTPUT_ALL_ZIP);

					return null;
				}
			};
		}
	}
}
