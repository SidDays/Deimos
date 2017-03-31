package deimos.gui.view;

import deimos.common.DeimosImages;
import deimos.common.GUIUtils;
import deimos.gui.view.services.NeuralTrainingService;
import deimos.gui.view.services.PredictListService;
import deimos.gui.view.services.PredictService;
import deimos.phase3.Neural;
import deimos.phase3.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class TrainPredictController {

	private User currentUser = null;
	
	public static final String PROCESSING = "...";
	
	// Training mode
	@FXML
	private ImageView wordCloudImage;
	@FXML
	private TextField errorTextField;
	@FXML
	private Button trainButton;

	// prediction user
	@FXML
	private Button resyncUsersButton;
	@FXML
	private Spinner<User> userPredictSpinner;

	private ObservableList<User> usersPrediction;

	private SpinnerValueFactory<User> userPredictSpinnerValueFactory = null;

	/*@FXML
	private Label userPredictName;
	@FXML
	private Label userPredictPublicIP;*/

	private PredictListService servicePredictList;
	
	private NeuralTrainingService serviceNeuralTraining;

	// Predict
	@FXML
	private Label predictedInterestsLabel;
	@FXML
	private Label predictedGenderLabel;
	@FXML
	private Label predictedAgeGroupLabel;
	@FXML
	private Label predictedLocationLabel;

	// Bottom bar
	@FXML
	private Label tpStatus;
	@FXML
	private Button predictButton;

	private PredictService servicePrediction;

	@FXML
	private void initialize() {
		initializePrediction();

		initializePredictListSpinner();
		initializeNeuralTraining();
	}
	
	private void initializeNeuralTraining()
	{
		serviceNeuralTraining = new NeuralTrainingService();
		
		serviceNeuralTraining.setOnFailed(e-> {
			// System.err.println("Something went wrong.");
			serviceNeuralTraining.getException().printStackTrace();
			errorTextField.setDisable(false);
		});
		
		serviceNeuralTraining.setOnSucceeded(e-> {
			predictButton.setDisable(false);
			errorTextField.setDisable(false);
		});
		
		serviceNeuralTraining.setOnCancelled(e-> {
			predictButton.setDisable(false);
			errorTextField.setDisable(false);
		});
		
		serviceNeuralTraining.setOnRunning(e-> {
			predictButton.setDisable(true);
			errorTextField.setDisable(true);
		});
	}

	private void initializePredictListSpinner()
	{
		servicePredictList = new PredictListService();
		servicePredictList.setOnRunning(e-> {
			System.out.println("Populating the spinner...");
		});
		servicePredictList.setOnFailed(e-> {
			System.err.println("Something went wrong when resyncing.");
		});
		servicePredictList.setOnSucceeded(e -> {
			// Populate the spinner
			usersPrediction = FXCollections.observableArrayList(servicePredictList.getPopulater());

			userPredictSpinnerValueFactory = 
					new SpinnerValueFactory.ListSpinnerValueFactory<>(usersPrediction);
			userPredictSpinner.setValueFactory(userPredictSpinnerValueFactory);

			/*// When spinner change value.
			userPredictSpinner.valueProperty().addListener(new ChangeListener<String>() {

	            @Override
	            public void changed(ObservableValue<? extends String> observable,//
	            		String oldValue, String newValue) {
	            }
	        });*/
			
			trainButton.setDisable(false);
			predictButton.setDisable(false);

			System.out.println("Populated the spinner.");

		});
	}

	private void initializePrediction() {
		wordCloudImage.setImage(DeimosImages.IMG_WORDCLOUD_PLACEHOLDER);

		servicePrediction = new PredictService();

		servicePrediction.setOnSucceeded(e -> {
			wordCloudImage.setImage(servicePrediction.getBi());
			System.out.println("Image successfully set.");
			resetWordCloud();
			
			predictedLocationLabel.setText(servicePrediction.getLocation());
		});
		servicePrediction.setOnRunning(e -> {

			predictButton.setDisable(true);
			predictButton.setText("Generating...");
			
			
			predictedAgeGroupLabel.setText(PROCESSING);
			predictedGenderLabel.setText(PROCESSING);
			predictedInterestsLabel.setText(PROCESSING);
			predictedLocationLabel.setText(PROCESSING);
			
			wordCloudImage.setImage(DeimosImages.IMG_WORDCLOUD_INPROGRESS);
		});
		servicePrediction.setOnCancelled(e -> {
			resetWordCloud();
		});
		servicePrediction.setOnFailed(e -> {
			resetWordCloud();
		});

	}		

	@FXML
	private void handleResyncUsersButton() {
		
		trainButton.setDisable(true);
		GUIUtils.startAgain(servicePredictList);
	}
	
	@FXML
	private void handleTrainButton()
	{
		try
		{
			if(errorTextField.getText().isEmpty())
			{
				serviceNeuralTraining.setError(Neural.ERROR_ALLOWED_DEFAULT);
			}
			else {
				double error = Double.parseDouble(errorTextField.getText());
				serviceNeuralTraining.setError(error);
			}
			GUIUtils.startAgain(serviceNeuralTraining);

		} catch (NumberFormatException e)
		{
			// System.out.println(e);
			GUIUtils.generateErrorAlert("Invalid value for training error", null);
		}
	}

	@FXML
	private void handleGenerateButton() {

		try {

			// TODO
			// userId = Integer.parseInt(analyzeController.userIDTextField.getText());

			// Get userId from spinner
			if(userPredictSpinnerValueFactory != null)
			{
				currentUser = userPredictSpinner.getValue();

				servicePrediction.setUser(currentUser);
				GUIUtils.startAgain(servicePrediction); // TODO more error handling

			}
			else {
				System.err.println("Re-sync required.");
				GUIUtils.generateErrorAlert("Re-sync required.", null);
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void resetWordCloud() {
		predictButton.setText("Generate");
		predictButton.setDisable(false);
	}
}
