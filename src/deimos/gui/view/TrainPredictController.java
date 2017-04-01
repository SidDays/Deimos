package deimos.gui.view;

import java.util.List;

import deimos.common.DeimosImages;
import deimos.common.GUIUtils;
import deimos.gui.view.services.NeuralTrainingService;
import deimos.gui.view.services.PredictListService;
import deimos.gui.view.services.PredictService;
import deimos.phase3.Neural;
import deimos.phase3.User;
import deimos.phase3.WordCloudGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

public class TrainPredictController {

	private User currentUser = null;

	public static final String PROCESSING = "...";
	public static final String NONE = "N/A";

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
			
			tpStatus.setText("Training complete!");
			
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

			System.out.println("Populated the spinner.");
			tpStatus.setText("Users synced!");

		});
	}

	private void initializePrediction() {
		wordCloudImage.setImage(DeimosImages.IMG_WORDCLOUD_PLACEHOLDER);

		servicePrediction = new PredictService();

		servicePrediction.setOnSucceeded(e -> {
			
			predictButton.setDisable(false);
			
			wordCloudImage.setImage(servicePrediction.getBi());
			System.out.println("Image successfully set.");
			
			StringBuilder topics = new StringBuilder();
			List<String> x = WordCloudGenerator.getInterests();
			int length = Math.min(x.size(), 5);
			for(int i = 0; i <  length; i++) {
				topics.append(x.get(i));
				if(i < length-1)
					topics.append(", ");
			}
			
			predictedAgeGroupLabel.setText(servicePrediction.getPredictedAge());
			predictedGenderLabel.setText(servicePrediction.getPredictedGender());
			predictedInterestsLabel.setText(topics.toString());
			predictedInterestsLabel.setTooltip(new Tooltip(topics.toString()));
			predictedLocationLabel.setText(servicePrediction.getLocation());
			
			tpStatus.setText("Prediction complete.");
		});
		servicePrediction.setOnRunning(e -> {

			predictButton.setDisable(true);
			tpStatus.setText("Predicting...");

			predictedAgeGroupLabel.setText(PROCESSING);
			predictedGenderLabel.setText(PROCESSING);
			predictedInterestsLabel.setText(PROCESSING);
			predictedLocationLabel.setText(PROCESSING);

			wordCloudImage.setImage(DeimosImages.IMG_WORDCLOUD_INPROGRESS);
		});
		servicePrediction.setOnCancelled(e -> {
			resetPrediction();
		});
		servicePrediction.setOnFailed(e -> {
			resetPrediction();
			System.out.println("Problem in prediction.");			
			
			servicePrediction.getException().printStackTrace();
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
	private void handleGenerateButton()
	{
		// TODO
		// userId = Integer.parseInt(analyzeController.userIDTextField.getText());

		// Get userId from spinner
		if(userPredictSpinnerValueFactory != null)
		{
			currentUser = userPredictSpinner.getValue();
			System.out.println("Selected "+currentUser);

			servicePrediction.setUser(currentUser);
			GUIUtils.startAgain(servicePrediction); // TODO more error handling

		}
		else {
			System.err.println("Re-sync required.");
			GUIUtils.generateErrorAlert("Re-sync required.", null);
		}


	}

	private void resetPrediction()
	{
		predictedAgeGroupLabel.setText(NONE);
		predictedGenderLabel.setText(NONE);
		predictedInterestsLabel.setText(NONE);
		predictedLocationLabel.setText(NONE);
		
		predictButton.setDisable(false);
		
		wordCloudImage.setImage(DeimosImages.IMG_WORDCLOUD_PLACEHOLDER);
	}
}
