package deimos.gui.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import deimos.common.DeimosConfig;
import deimos.gui.DeimosApp;
import deimos.phase2.similarity.SimilarityMapper;
import deimos.phase2.user.UserIDF;
import deimos.phase2.user.UserURLsTF;
import deimos.phase2.user.UserWeightCalculation;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class DeimosAppOverviewController {

	private DeimosApp mainApp;
	@FXML
	private TextField userIDTextField;
	int userId;
	@FXML
	private TextField outputFileTextField;
	@FXML
	private CheckBox truncateCheckBox;
	@FXML
	private ProgressBar progressURLsTFBar;
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

	private String truncateText;
	private String filePath;
	private URLsTFService serviceURLsTF;
	private IDFService serviceIDf;
	private WeightService serviceWeights;
	private SimilarityService serviceSimilarity;
	private SelectDefaultHistoryFileService defaultFileService;
	private Desktop desktop = Desktop.getDesktop();
	private File file;
	public DeimosAppOverviewController() {

		System.out.println("Started Deimos Application GUI.");
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		initializeURLsTF();
		initializeTruncate();
		initializeIDF();
		initializeWeights();
		initializeSimilarity();
		defaultFileService = new SelectDefaultHistoryFileService();
	}

	private void initializeURLsTF() {
		serviceURLsTF = new URLsTFService();

		serviceURLsTF.setOnSucceeded(e1 -> {
			progressURLsTFBar.setProgress(1);
			progressIDFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			statusLabel.setText("IDF insertion");
			startAgain(serviceIDf);
		});
		serviceURLsTF.setOnFailed(e1 -> {
			progressURLsTFBar.setProgress(0);
		});
		serviceURLsTF.setOnCancelled(e1 -> {
			progressURLsTFBar.setProgress(0);
		});
	}
	
	private void initializeIDF() {
		serviceIDf = new IDFService();

		serviceIDf.setOnSucceeded(e1 -> {
			progressIDFBar.setProgress(1);
			progressWeightBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			statusLabel.setText("Weights insertion");
			startAgain(serviceWeights);
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
			startAgain(serviceSimilarity);
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

	/**
	 * Is called by the main application to give a reference back to itself.
	 * @param mainApp
	 */
	public void setMainApp(DeimosApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * An alternative to the factory restart() method.
	 * Avoids the cancellation of the service if it never started.
	 * Functionally near-equivalent to restart().
	 * @param s The service to start again
	 */
	private void startAgain(Service<?> s) {
		if(s.isRunning())
			s.restart();
		else {
			s.reset();
			s.start();
		}
	}

	private void generateAlerts(String s) {
		Alert alertTosAgree = new Alert(AlertType.ERROR);
		alertTosAgree.initOwner(mainApp.getPrimaryStage());
		alertTosAgree.setContentText(s);
		alertTosAgree.setTitle("Something is not right!!!");
		alertTosAgree.showAndWait();
	}
	
	private void initializeTruncate() {

		truncateLabel.setOnMouseClicked(e -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Truncate User tables");
			//alert.setHeaderText("Deimos Helper Terms of Service");

			Label label = new Label("Please read the following..");

			truncateText = "To provide a total control of GUI to the user, "
					+ "this checkbox is provided using which you can truncate"
					+ "the user table for the given user-ID if it already exists";

			TextArea textArea = new TextArea(truncateText);
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
		truncateLabel.setTooltip(new Tooltip("To truncate tables, check the checkbox."));
	}

	@FXML
	private void handleBrowseButton() 
	{
		try
		{
			browseButton.setOnAction(new EventHandler<ActionEvent>(){
	            @Override
	           public void handle(ActionEvent event) {
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
		               openFile(file);
	               }
	               else
	               {
	            	   Alert alert = new Alert(AlertType.CONFIRMATION);
	            	   alert.setTitle("File not found");
	            	   alert.setHeaderText("Selected file should be of chrome history only");
	            	   alert.setContentText("Choose the default file? click on 'Cancel' if you don't wish to.");
	            	   alert.showAndWait().ifPresent(response -> {
	            		   if (response == ButtonType.OK) {
	            			   System.out.println("Tried to select default history file");
	            			   defaultFileService.start();
	            		   }
	            	   });
	               }	               
	           }
	       });
			
		}
		catch(NullPointerException e)
		{
			
		}
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
					generateAlerts(message);
				}
				else
				{
					if(outputFileTextField.getText().isEmpty())
					{
						message = "You must select the output file";
						generateAlerts(message);
					}
					else
					{
						userIDTextField.setDisable(true);
						startButton.setDisable(true);

						progressURLsTFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
						statusLabel.setText("user_url and user_tf insertion");
						startAgain(serviceURLsTF);

						/*progressIDFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
						statusLabel.setText("IDF insertion");
	        			startAgain(serviceIDf);

	        			progressWeightBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
	        			statusLabel.setText("Weights insertion");
	        			startAgain(serviceWeights);

	        			progressSimilarityBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
	        			statusLabel.setText("Similarity insertion");
	        			startAgain(serviceSimilarity);*/
					}
					
				}
			}
			catch(NumberFormatException e) 
			{
				message = "Invalid User-ID, please enter a number!";
				generateAlerts(message);

			}
		}
		else {
			message = "User-ID field can't be empty!";
			generateAlerts(message);
		}
	}

	private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            
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
	
	public class SelectDefaultHistoryFileService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {

				@Override
				public Void call(){
					
					filePath = DeimosConfig.FILE_OUTPUT_HISTORY;
					outputFileTextField.setText(filePath);
					return null;
				}
			};
		}
	}
}
