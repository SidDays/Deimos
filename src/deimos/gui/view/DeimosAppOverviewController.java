package deimos.gui.view;

import java.io.File;

import deimos.common.DeimosConfig;
import deimos.common.DeimosImages;
import deimos.common.GUIUtils;
import deimos.gui.DeimosApp;
import deimos.gui.view.services.WordCloudService;
import deimos.phase2.similarity.SimilarityMapper;
import deimos.phase2.user.UserIDF;
import deimos.phase2.user.UserURLsTF;
import deimos.phase2.user.UserWeightCalculation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * References:
 * stackoverflow.com/questions/13227809/displaying-changing-values-in-javafx-label
 * 
 * @author Bhushan Pathak
 * @author Siddhesh Karekar
 */
public class DeimosAppOverviewController {

	private DeimosApp mainApp;
	@FXML
	private TextField userIDTextField;
	
	private int userId;
	
	@FXML
	private TextField outputFileTextField;
	@FXML
	private CheckBox truncateCheckBox;
	@FXML
	private Label urlsTFStatusLabel;
	@FXML
	private ProgressBar progressURLsTFBar;
	
	private Timeline urlsTFTimeline;
	
	@FXML
	private ProgressBar progressIDFBar;
	@FXML
	private ProgressBar progressWeightBar;
	@FXML
	private ProgressBar progressSimilarityBar;
	@FXML
	private Label statusLabel;
	@FXML
	private Label truncateLabel;
	@FXML
	private Button browseButton;
	@FXML
	private Button startButton;
	@FXML
	private Button generateWordCloudButton;

	private String truncateText;
	private String filePath;
	private URLsTFService serviceURLsTF;
	private IDFService serviceIDf;
	private WeightService serviceWeights;
	private SimilarityService serviceSimilarity;
	private WordCloudService serviceWordCloud;
	private File file;
	
	@FXML
	ImageView wordCloudImage;
	
	public DeimosAppOverviewController() {

		System.out.println("Started Deimos Application GUI.");
		
		userId = -1;
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		initializeURLsTF();
		initializeTruncateHint();
		initializeIDF();
		initializeWeights();
		initializeSimilarity();
		
		browseButton.setTooltip(new Tooltip("Select the output file of Phase 1 data collection."));
		
		initializeWordCloud();
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
			bindURLsTFLabelToStatus();
		});
		serviceURLsTF.setOnSucceeded(e1 -> {
			progressURLsTFBar.setProgress(1);
			progressIDFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			statusLabel.setText("IDF insertion");
			GUIUtils.startAgain(serviceIDf);
		});
		serviceURLsTF.setOnFailed(e1 -> {
			progressURLsTFBar.setProgress(0);
			
			clearURLsTFBinding();
		});
		serviceURLsTF.setOnCancelled(e1 -> {
			progressURLsTFBar.setProgress(0);
			
			clearURLsTFBinding();
		});
	}
	/**
	 * Bind the URL status to the text above the progress bar.
	 */
	private void bindURLsTFLabelToStatus() {

		
		urlsTFTimeline.setCycleCount(Animation.INDEFINITE);
		urlsTFTimeline.play();
	}
	
	/**
	 * If URLs TF population fails, stop the status and progress bar updation.
	 */
	private void clearURLsTFBinding() {
		// if(urlsTFTimeline != null)
			urlsTFTimeline.stop();
		
		progressURLsTFBar.setProgress(0);
	}

	private void initializeIDF() {
		serviceIDf = new IDFService();

		serviceIDf.setOnSucceeded(e1 -> {
			progressIDFBar.setProgress(1);
			progressWeightBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			statusLabel.setText("Weights insertion");
			GUIUtils.startAgain(serviceWeights);
		});
		serviceIDf.setOnFailed(e1 -> {
			progressIDFBar.setProgress(0);
		});
		serviceIDf.setOnCancelled(e1 -> {
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
		});
		serviceWeights.setOnFailed(e1 -> {
			progressWeightBar.setProgress(0);
		});
		serviceWeights.setOnCancelled(e1 -> {
			progressWeightBar.setProgress(0);
		});
	}
	
	private void initializeSimilarity() {
		serviceSimilarity = new SimilarityService();

		serviceSimilarity.setOnSucceeded(e1 -> {
			progressSimilarityBar.setProgress(1);
		});
		serviceSimilarity.setOnFailed(e1 -> {
			progressSimilarityBar.setProgress(0);
		});
		serviceSimilarity.setOnCancelled(e1 -> {
			progressSimilarityBar.setProgress(0);
		});
	}
	
	private void initializeWordCloud() {
		wordCloudImage.setImage(DeimosImages.IMG_WORDCLOUD_PLACEHOLDER);
		
		serviceWordCloud = new WordCloudService();
		
		serviceWordCloud.setOnSucceeded(e -> {
			wordCloudImage.setImage(serviceWordCloud.bi);
			System.out.print("Image successfully set.");
			resetWordCloud();
		});
		serviceWordCloud.setOnRunning(e -> {
			
			generateWordCloudButton.setDisable(true);
			generateWordCloudButton.setText("Generating...");
			wordCloudImage.setImage(DeimosImages.IMG_WORDCLOUD_INPROGRESS);

		});
		serviceWordCloud.setOnCancelled(e -> {
			resetWordCloud();
		});
		serviceWordCloud.setOnFailed(e -> {
			resetWordCloud();
		});

	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * @param mainApp
	 */
	public void setMainApp(DeimosApp mainApp) {
		this.mainApp = mainApp;
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

	@FXML
	private void handleBrowseButton() 
	{

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
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
	private void handleWordCloudGenerateButton() {
		
		try {
			userId = Integer.parseInt(userIDTextField.getText());
			serviceWordCloud.setUserId(userId);
			GUIUtils.startAgain(serviceWordCloud); // TODO more error handling
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		
	}
	
	private void resetWordCloud() {
		generateWordCloudButton.setText("Generate");
		generateWordCloudButton.setDisable(false);
	}
	
	@FXML
	private void handleStartButton() {
		String message = "";

		if(!(userIDTextField.getText().trim().isEmpty())) {
			try 
			{
				userId = Integer.parseInt(userIDTextField.getText());

				if(!truncateCheckBox.isSelected() && UserURLsTF.doesUserIdExist(userId)) {

					message = "User-ID already exists!";
					GUIUtils.generateErrorAlert(message, mainApp.getPrimaryStage());
				}
				else
				{
					if(outputFileTextField.getText().isEmpty())
					{
						System.out.println("Selecting default history file.");
						filePath = DeimosConfig.FILE_OUTPUT_HISTORY;
						outputFileTextField.setText(filePath);
					}

					userIDTextField.setDisable(true);
					startButton.setDisable(true);
					
					urlsTFStatusLabel.setText("Initializing...");
					progressURLsTFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
					
					statusLabel.setText("user_url and user_tf insertion");
					GUIUtils.startAgain(serviceURLsTF); // The others cascade from this one.				
				}
			}
			catch(NumberFormatException e) 
			{
				message = "Invalid User-ID, please enter a number!";
				GUIUtils.generateErrorAlert(message, mainApp.getPrimaryStage());

			}
		}
		else {
			message = "User-ID field can't be empty!";
			GUIUtils.generateErrorAlert(message, mainApp.getPrimaryStage());
		}
	}
	
	private class URLsTFService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {
				@Override
				public Void call(){

					UserURLsTF.userURLAndTFTableInsertion(userId, truncateCheckBox.isSelected(), filePath);
					return null;
				}
			};
		}

	}

	private class IDFService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {
				@Override
				public Void call(){

					UserIDF.computeUserIDF(userId);
					return null;
				}
			};
		}

	}

	private class WeightService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {
				@Override
				public Void call(){

					UserWeightCalculation.updateWeights(userId);
					return null;
				}
			};
		}
	}

	private class SimilarityService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {
				@Override
				public Void call(){

					SimilarityMapper.computeSimilarity(userId);
					return null;
				}
			};
		}

	}
}
