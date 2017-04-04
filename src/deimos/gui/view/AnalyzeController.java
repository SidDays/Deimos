package deimos.gui.view;

import java.io.File;

import deimos.common.DeimosConfig;
import deimos.common.GUIUtils;
import deimos.gui.view.services.GEWService;
import deimos.gui.view.services.IDFService;
import deimos.gui.view.services.SimilarityService;
import deimos.gui.view.services.TrainingValuesService;
import deimos.gui.view.services.URLsTFService;
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
	private String filePath;
	private URLsTFService serviceURLsTF;
	private IDFService serviceIDF;
	private WeightService serviceWeights;
	private SimilarityService serviceSimilarity;
	private GEWService serviceGEW;
	private TrainingValuesService serviceTrainingValues;
	private File file;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		userId = -1;

		initializeURLsTF();
		initializeTruncateHint();
		initializeIDF();
		initializeWeights();
		initializeSimilarity();
		initializeGEW();
		initializeTrainingValues();

		browseButton.setTooltip(new Tooltip("Select the output file of Phase 1 data collection."));
	}
	
	/**
	 * TODO
	 * TrainingValuesService is not complete, 
	 * add a function call to UserTrainingInput file
	 */
	
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
			progressTrainingValuesBar.setProgress(0);

		});
		serviceTrainingValues.setOnCancelled(e1 -> {
			progressTrainingValuesBar.setProgress(0);

		});
	}

	/**
	 * TODO
	 * GEWService is not complete, 
	 * add a function call to GEW class 
	 */
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
			progressGEWBar.setProgress(0);

		});
		serviceGEW.setOnCancelled(e1 -> {
			progressGEWBar.setProgress(0);

		});
	}
	
	private void initializeURLsTF() {

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
		serviceURLsTF.setOnFailed(e1 -> {
			progressURLsTFBar.setProgress(0);
			clearBindings(urlsTFTimeline, progressURLsTFBar);
			//clearURLsTFBinding();
		});
		serviceURLsTF.setOnCancelled(e1 -> {
			progressURLsTFBar.setProgress(0);
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
			progressIDFBar.setProgress(0);
			clearBindings(idfTimeline, progressIDFBar);
			progressIDFBar.setProgress(0);
		});
		serviceIDF.setOnCancelled(e1 -> {
			progressIDFBar.setProgress(0);
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
			progressWeightBar.setProgress(0);
		});
		serviceWeights.setOnCancelled(e1 -> {
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
			clearBindings(similarityTimeline, progressSimilarityBar);
			//clearSimilarityBinding();
		});
		serviceSimilarity.setOnCancelled(e1 -> {
			
			progressSimilarityBar.setProgress(0);
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

	/**
	 * Many services require parameters.
	 * @param id Sends the specified user Id to them for use.
	 * @param truncate Boolean value for truncate checkbox is used for URLsTFService.
	 * @param filepath FilePath is used for URLsTFService.
	 */
	private void setParamsForServices(int id, boolean truncate, String filepath)
	{
		serviceURLsTF.setUserId(id);
		serviceURLsTF.setTruncate(truncate);
		serviceURLsTF.setFilePath(filepath);
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
		setParamsForServices(userId, truncateCheckBox.isSelected(), filePath);
	}

	@FXML
	private void handleBrowseButton() 
	{

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Coma-separated values", "*.csv"),
				new ExtensionFilter("Text Files", "*.txt"));
		file = fileChooser.showOpenDialog(new Stage());

		filePath = "";

		if(file != null)
		{
			filePath = file.toString();
			outputFileTextField.setText(filePath);
		}
		else
		{
			// TODO handle invalid files vagaira

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("File not found");
			alert.setHeaderText("Selected file should be of chrome history only");
			alert.setContentText("Choose the default file? click on 'Cancel' if you don't wish to.");
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					System.out.println("Selecting default history file.");
					filePath = DeimosConfig.FILE_OUTPUT_HISTORY;
					outputFileTextField.setText(filePath);
				}
			});
		}

	}

	@FXML
	private void handleAnalyzeButton() {
		String message = "";

		if(!(userIDTextField.getText().trim().isEmpty())) {
			try 
			{
				userId = Integer.parseInt(userIDTextField.getText());

				if(!truncateCheckBox.isSelected() && UserURLsTF.doesUserIdExist(userId)) {

					message = "User-ID already exists!";
					GUIUtils.generateErrorAlert(message, null);
				}
				else
				{
					if(outputFileTextField.getText().isEmpty())
					{
						System.out.println("Selecting default history file.");
						filePath = DeimosConfig.FILE_OUTPUT_HISTORY;
						outputFileTextField.setText(filePath);
					}
					analyzeVBox.setDisable(true);
					/*userIDTextField.setDisable(true);
					outputFileTextField.setDisable(true);*/
					analyzeButton.setDisable(true);

					urlsTFStatusLabel.setText("Initializing...");
					progressURLsTFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

					setParamsForServices();

					statusLabel.setText("user_url and user_tf insertion");
					GUIUtils.startAgain(serviceURLsTF); // The others cascade from this one.				
				}
			}
			catch(NumberFormatException e) 
			{
				message = "Invalid User-ID, please enter a number!";
				GUIUtils.generateErrorAlert(message, null); // TODO get stage

			}
		}
		else {
			message = "User-ID field can't be empty!";
			GUIUtils.generateErrorAlert(message, null);
		}
	}

}
