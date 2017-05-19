package deimos.gui.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import deimos.common.DeimosConfig;
import deimos.common.GUIUtils;
import deimos.gui.view.services.GEWService;
import deimos.gui.view.services.IDFService;
import deimos.gui.view.services.SimilarityService;
import deimos.gui.view.services.TrainingValuesService;
import deimos.gui.view.services.URLsTFService;
import deimos.gui.view.services.UserInfoPublicIPService;
import deimos.gui.view.services.WeightService;
import deimos.phase2.similarity.SimilarityMapper;
import deimos.phase2.user.UserIDF;
import deimos.phase2.user.UserURLsTF;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AnalyzeController {

	@FXML
	private TextField userIDTextField;

	private int userId;
	@FXML
	private VBox analyzeVBox;

	@FXML
	private TextField outputFileTextField;
	@FXML
	private CheckBox truncateCheckBox;
	@FXML
	private Label urlsTFStatusLabel;
	@FXML
	private Label idfStatusLabel;
	@FXML
	private Label similarityStatusLabel;
	@FXML
	private Label weightStatusLabel;
	@FXML
	private Label gewStatusLabel;
	@FXML
	private Label trainingValuesStatusLabel;
	@FXML
	private ProgressBar progressURLsTFBar;

	private Timeline urlsTFTimeline;
	private Timeline similarityTimeline;
	private Timeline idfTimeline;
	@FXML
	private ProgressBar progressIDFBar;
	@FXML
	private ProgressBar progressWeightBar;
	@FXML
	private ProgressBar progressSimilarityBar;
	@FXML
	private ProgressBar progressGEWBar;
	@FXML
	private ProgressBar progressTrainingValuesBar;
	@FXML
	private Label statusLabel;
	@FXML
	private Label truncateLabel;
	@FXML
	private Button browseButton;
	@FXML
	private Button analyzeButton;

	private String truncateText;

	private String filePathHistory;
	private String filePathUserInfo;
	private String filePathPublicIP;

	private URLsTFService serviceURLsTF;
	private UserInfoPublicIPService serviceUserInfoPublicIP;
	private IDFService serviceIDF;
	private WeightService serviceWeights;
	private SimilarityService serviceSimilarity;
	private GEWService serviceGEW;
	private TrainingValuesService serviceTrainingValues;
	private File file;

	/**
	 * Estimates the name of the public IP file given
	 * a pattern of the input history file. For example,
	 * 'export-history-Anushka.csv' -> 'export-publicIP-Anushka.txt'
	 * @param historyFileName
	 * @return
	 */
	private static String getPublicIPFileName(String historyFileName)
	{
		String historyFileNameWoExt = historyFileName.substring(0, historyFileName.length()-4);
		String FILE_OUTPUT_HISTORY_WO_EX = DeimosConfig.FILE_OUTPUT_HISTORY.substring(0,
				DeimosConfig.FILE_OUTPUT_HISTORY.length()-4);
		String FILE_OUTPUT_PUBLICIP_WO_EX = DeimosConfig.FILE_OUTPUT_PUBLICIP.substring(0,
				DeimosConfig.FILE_OUTPUT_PUBLICIP.length()-4);

		return historyFileNameWoExt.replace(FILE_OUTPUT_HISTORY_WO_EX, FILE_OUTPUT_PUBLICIP_WO_EX)+".txt";
	}

	/**
	 * Estimates the name of the user Info file given
	 * a pattern of the input history file. For example,
	 * 'export-history-Anushka.csv' -> 'export-userInfo-Anushka.txt'
	 * @param historyFileName
	 * @return
	 */
	private static String getUserInfoFileName(String historyFileName)
	{
		String historyFileNameWoExt = historyFileName.substring(0, historyFileName.length()-4);
		String FILE_OUTPUT_HISTORY_WO_EX = DeimosConfig.FILE_OUTPUT_HISTORY.substring(0,
				DeimosConfig.FILE_OUTPUT_HISTORY.length()-4);
		String FILE_OUTPUT_USERINFO_WO_EX = DeimosConfig.FILE_OUTPUT_USERINFO.substring(0,
				DeimosConfig.FILE_OUTPUT_USERINFO.length()-4);

		return historyFileNameWoExt.replace(FILE_OUTPUT_HISTORY_WO_EX, FILE_OUTPUT_USERINFO_WO_EX)+".txt";
	}

	/**
	 * Sets the 3 parameters filePath to their default values.
	 */
	private void setDefaultInputFileNames() {
		System.out.println("Selecting default history, public IP and user Info files.");
		filePathHistory = DeimosConfig.FILE_OUTPUT_HISTORY;
		filePathPublicIP = DeimosConfig.FILE_OUTPUT_PUBLICIP;
		filePathUserInfo = DeimosConfig.FILE_OUTPUT_USERINFO;

		// outputFileTextField.setText(filePathHistory);
		outputFileTextField.setText(null);
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		userId = -1;

		initializeURLsTFandUserInfoPublicIP();
		initializeTruncateHint();
		initializeInputHistoryHint();
		initializeIDF();
		initializeWeights();
		initializeSimilarity();
		initializeGEW();
		initializeTrainingValues();

		browseButton.setTooltip(new Tooltip("Select the output file of Phase 1 data collection."));
	}

	private void initializeTrainingValues() {
		serviceTrainingValues = new TrainingValuesService();

		serviceTrainingValues.setOnSucceeded(e1 -> {
			progressTrainingValuesBar.setProgress(1);
			trainingValuesStatusLabel.setText("Finished!");
			statusLabel.setText("Analyze phase finished!");
			analyzeVBox.setDisable(false);
			analyzeButton.setDisable(false);
		});
		serviceTrainingValues.setOnFailed(e1 -> {
			trainingValuesStatusLabel.setText("");
			progressTrainingValuesBar.setProgress(0);

		});
		serviceTrainingValues.setOnCancelled(e1 -> {
			trainingValuesStatusLabel.setText("");
			progressTrainingValuesBar.setProgress(0);
		});
	}

	private void initializeGEW() {
		serviceGEW = new GEWService();

		serviceGEW.setOnSucceeded(e1 -> {
			progressGEWBar.setProgress(1);

			progressTrainingValuesBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			statusLabel.setText("Obtaining training values");
			GUIUtils.startAgain(serviceTrainingValues);
			gewStatusLabel.setText("Finished!");
		});
		serviceGEW.setOnFailed(e1 -> {
			gewStatusLabel.setText("");
			progressGEWBar.setProgress(0);

		});
		serviceGEW.setOnCancelled(e1 -> {
			gewStatusLabel.setText("");
			progressGEWBar.setProgress(0);

		});
	}

	private void cancelEverything() {

		// Cancel everything
		System.out.println("Attempting to cancel everything...");
		
		if(serviceUserInfoPublicIP.cancel())
			serviceUserInfoPublicIP.reset();

		if(serviceURLsTF.cancel())
			serviceURLsTF.reset();
		if(serviceIDF.cancel())
			serviceIDF.reset();
		if(serviceWeights.cancel())
			serviceWeights.reset();
		if(serviceSimilarity.cancel())
			serviceSimilarity.reset();
		if(serviceGEW.cancel())
			serviceGEW.reset();
		if(serviceTrainingValues.cancel())
			serviceTrainingValues.reset();

		analyzeVBox.setDisable(false);
		analyzeButton.setDisable(false);

		// Reset the button
		analyzeButton.setText("Analyze!");
		analyzeButton.setOnAction(e -> {
			handleAnalyzeButton();
		});
	}

	private void initializeURLsTFandUserInfoPublicIP() {

		serviceUserInfoPublicIP = new UserInfoPublicIPService();
		serviceUserInfoPublicIP.setOnFailed(e -> {

			cancelEverything();

			Throwable ex = serviceUserInfoPublicIP.getException();

			if(ex instanceof FileNotFoundException) {
				GUIUtils.generateErrorAlert("One or more files are missing!\n\n"
						+ "The User Info and Public IP files must also be included along with the history file.", null);
				statusLabel.setText("Files missing...");
			}
			else {
				GUIUtils.generateErrorAlert("Unknown error occurred!", null);
				statusLabel.setText("Analysis phase failed...");
			}
		});


		urlsTFTimeline = new Timeline(
				new KeyFrame(Duration.seconds(0),
						new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent actionEvent) {

						urlsTFStatusLabel.setText(UserURLsTF.getStatus());
						progressURLsTFBar.setProgress(UserURLsTF.getProgress());
					}
				}),
				new KeyFrame(Duration.seconds(1))
				);

		serviceURLsTF = new URLsTFService();


		serviceURLsTF.setOnRunning(e1 -> {
			bindToStatus(urlsTFTimeline);
			//bindURLsTFToStatus();
		});
		serviceURLsTF.setOnSucceeded(e1 -> {
			progressURLsTFBar.setProgress(1);
			progressIDFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			statusLabel.setText("IDF insertion");
			GUIUtils.startAgain(serviceIDF);
		});

		// WorkerStateEvent eventURLsTFFailure = new WorkerStateEvent(new EventHandler());

		serviceURLsTF.setOnFailed(e1 -> {
			progressURLsTFBar.setProgress(0);
			urlsTFStatusLabel.setText("");
			clearBindings(urlsTFTimeline, progressURLsTFBar);
			//clearURLsTFBinding();
		});
		serviceURLsTF.setOnCancelled(e1 -> {
			progressURLsTFBar.setProgress(0);
			urlsTFStatusLabel.setText("");
			clearBindings(urlsTFTimeline, progressURLsTFBar);
			//clearURLsTFBinding();
		});
	}

	/**
	 * Generalized function for binding 
	 * the progress bars
	 */
	private void bindToStatus(Timeline timeLine)
	{
		timeLine.setCycleCount(Animation.INDEFINITE);
		timeLine.play();
	}

	/**
	 * Generalized function for 
	 * clearing the bindings of 
	 * the progress bars
	 */
	private void clearBindings(Timeline timeLine, ProgressBar progressBar)
	{
		timeLine.stop();
		progressBar.setProgress(0);
	}

	private void initializeIDF() {

		idfTimeline = new Timeline(
				new KeyFrame(Duration.seconds(0),
						new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent actionEvent) {

						idfStatusLabel.setText(UserIDF.getStatus());
						progressIDFBar.setProgress(UserIDF.getProgress());
					}
				}),
				new KeyFrame(Duration.seconds(1))
				);
		serviceIDF = new IDFService();

		serviceIDF.setOnRunning(e1 -> {
			bindToStatus(idfTimeline);
		});
		serviceIDF.setOnSucceeded(e1 -> {

			progressIDFBar.setProgress(1);
			//idfStatusLabel.setText("Finished!");
			progressWeightBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			statusLabel.setText("Weights insertion");
			GUIUtils.startAgain(serviceWeights);

		});
		serviceIDF.setOnFailed(e1 -> {
			idfStatusLabel.setText("");
			clearBindings(idfTimeline, progressIDFBar);
			progressIDFBar.setProgress(0);
		});
		serviceIDF.setOnCancelled(e1 -> {
			idfStatusLabel.setText("");
			clearBindings(idfTimeline, progressIDFBar);
			progressIDFBar.setProgress(0);
		});
	}

	private void initializeWeights() {
		serviceWeights = new WeightService();

		serviceWeights.setOnSucceeded(e1 -> {
			progressWeightBar.setProgress(1);

			progressSimilarityBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			statusLabel.setText("Similarity insertion");
			GUIUtils.startAgain(serviceSimilarity);
			weightStatusLabel.setText("Finished!");
		});
		serviceWeights.setOnFailed(e1 -> {
			weightStatusLabel.setText("");
			progressWeightBar.setProgress(0);
		});
		serviceWeights.setOnCancelled(e1 -> {
			weightStatusLabel.setText("");
			progressWeightBar.setProgress(0);
		});
	}

	private void initializeSimilarity()
	{
		similarityTimeline = new Timeline(
				new KeyFrame(Duration.seconds(0),
						new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent actionEvent) {
						similarityStatusLabel.setText(SimilarityMapper.getStatus());
						progressSimilarityBar.setProgress(SimilarityMapper.getProgress());
					}
				}),
				new KeyFrame(Duration.seconds(1))
				);
		serviceSimilarity = new SimilarityService();

		serviceSimilarity.setOnSucceeded(e1 -> {
			progressSimilarityBar.setProgress(1);
			progressGEWBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			//similarityStatusLabel.setText("Finished!");
			GUIUtils.startAgain(serviceGEW);
		});
		serviceSimilarity.setOnRunning(e1 -> {
			bindToStatus(similarityTimeline);
			//bindSimilarityToStatus();
		});
		serviceSimilarity.setOnFailed(e1 -> {

			progressSimilarityBar.setProgress(0);
			similarityStatusLabel.setText("");
			clearBindings(similarityTimeline, progressSimilarityBar);
			//clearSimilarityBinding();
		});
		serviceSimilarity.setOnCancelled(e1 -> {

			progressSimilarityBar.setProgress(0);
			similarityStatusLabel.setText("");
			clearBindings(similarityTimeline, progressSimilarityBar);
			//clearSimilarityBinding();
		});
	}

	private void initializeTruncateHint() {

		truncateLabel.setOnMouseClicked(e -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("Truncate User Tables for this ID");
			alert.setTitle("Hint");

			truncateText = "If this box is checked, the system removes any already existing records for this user ID. "
					+ "(You must check this box if you wish to reuse an existing user ID.)"
					+ "\n\nIf you're unsure, check this box.";

			alert.setContentText(truncateText);

			alert.showAndWait();
		});
		truncateLabel.setTooltip(new Tooltip("Truncate existing user tables for this ID."));
	}

	private void initializeInputHistoryHint() {

		truncateLabel.setOnMouseClicked(e -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("Import Information from Collect Phase");
			alert.setTitle("Hint");

			truncateText = "Select the history file exported during the data collection phase.\n\n"
					+ "It will be named in the format '"+DeimosConfig.FILE_OUTPUT_HISTORY+"'.\n"
					+ "The same naming format will be used to locate the '"+DeimosConfig.FILE_OUTPUT_USERINFO+"' and "
					+ "the '"+DeimosConfig.FILE_OUTPUT_PUBLICIP+"' files.";

			alert.setContentText(truncateText);

			alert.showAndWait();
		});
		truncateLabel.setTooltip(new Tooltip("The history file among the file(s) exported by the Collect tab."));
	}

	/**
	 * Many services require parameters.
	 * @param id Sends the specified user Id to them for use.
	 * @param truncate Boolean value for truncate checkbox is used for URLsTFService.
	 * @param filepathHistory filePaths are used for URLsTFService.
	 * @param filepathUserInfo filePaths are used for URLsTFService.
	 * @param filepathPublicIP filePaths are used for URLsTFService.
	 */
	private void setParamsForServices(int id, boolean truncate, String filepathHistory,
			String filepathUserInfo, String filepathPublicIP)
	{
		serviceURLsTF.setUserId(id);
		serviceURLsTF.setTruncate(truncate);
		serviceURLsTF.setFilePath(filepathHistory);

		serviceUserInfoPublicIP.setUserId(id);
		serviceUserInfoPublicIP.setTruncate(truncate);
		serviceUserInfoPublicIP.setFilePathUserInfo(filepathUserInfo);
		serviceUserInfoPublicIP.setFilePathPublicIP(filepathPublicIP);

		serviceIDF.setUserId(id);
		serviceWeights.setUserId(id);
		serviceSimilarity.setUserId(id);
		serviceGEW.setUserId(id);
		serviceTrainingValues.setUserId(id);
		serviceTrainingValues.setTruncate(truncate);
	}

	/**
	 * Sends default parameters from the UI to the services for use.
	 * Among these are userId, the boolean value of truncate checkbox,
	 * and the filePath specified in text box.
	 * 
	 * @param id
	 */
	private void setParamsForServices()
	{
		setParamsForServices(userId, truncateCheckBox.isSelected(), filePathHistory, filePathUserInfo, filePathPublicIP);
	}

	@FXML
	private void handleBrowseButton() 
	{

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open History File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Comma-separated values", "*.csv"),
				new ExtensionFilter("Text Files", "*.txt"));
		file = fileChooser.showOpenDialog(new Stage());

		filePathHistory = "";

		if(file != null)
		{
			filePathHistory = file.toString();
			outputFileTextField.setText(filePathHistory);

			// Also derive other files
			filePathPublicIP = getPublicIPFileName(filePathHistory);
			filePathUserInfo = getUserInfoFileName(filePathHistory);
		}
		else
		{
			// TODO handle invalid files vagaira

			/*Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("File not found");
			alert.setHeaderText("Selected file should be of Chrome history only");
			alert.setContentText("Reverting to the default file.");
			alert.showAndWait();*/

			System.out.println("Invalid file operation in browse button. Reverting to default files.");

			setDefaultInputFileNames();
		}

	}

	@FXML
	private void handleAnalyzeButton()
	{

		if(!(userIDTextField.getText().trim().isEmpty()))
		{
			try 
			{
				userId = Integer.parseInt(userIDTextField.getText());

				if(!truncateCheckBox.isSelected() && UserURLsTF.doesUserIdExist(userId))
				{
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Something's not right.");
					alert.setHeaderText("User-ID already exists!");
					alert.setContentText("Clear any existing rows with this "
							+ "user ID in the table and continue anyway?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK)
					{
						// ... user chose OK TODO insert user/IP anyway
						truncateCheckBox.setSelected(true);
						setParamsForServices();
						GUIUtils.startAgain(serviceUserInfoPublicIP);

					}
					else {
						// ... user chose CANCEL or closed the dialog
					}					
				}
				else
				{
					if(outputFileTextField.getText().isEmpty())
					{
						setDefaultInputFileNames();
					}

					urlsTFStatusLabel.setText("Initializing...");
					progressURLsTFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

					setParamsForServices();

					analyzeVBox.setDisable(true);
					// analyzeButton.setDisable(true);
					statusLabel.setText("user_url and user_tf insertion");

					GUIUtils.startAgain(serviceURLsTF); // The others cascade from this one.
					GUIUtils.startAgain(serviceUserInfoPublicIP);

					// Reset the button
					analyzeButton.setText("Cancel!");
					analyzeButton.setOnAction(e -> {
						cancelEverything();
					});
				}
			}
			catch(NumberFormatException e) 
			{
				String message = "Invalid User-ID, please enter a number!";
				GUIUtils.generateErrorAlert(message, null);

			}
		}
		else {
			String message = "User-ID field can't be empty!";
			GUIUtils.generateErrorAlert(message, null);
		}
	}

}
