package deimos.gui.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import deimos.common.DeimosConfig;
import deimos.common.DeimosImages;
import deimos.common.GUIUtils;
import deimos.common.StringUtils;
import deimos.gui.view.services.ExportUserService;
import deimos.gui.view.services.ImportUserService;
import deimos.gui.view.services.NeuralTrainingService;
import deimos.gui.view.services.PredictListService;
import deimos.gui.view.services.PredictService;
import deimos.phase2.user.UserURLsTF;
import deimos.phase3.Neural;
import deimos.phase3.User;
import deimos.phase3.WordCloudGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class TrainPredictController {

	private User currentUser = null;

	public static final String PROCESSING = "...";
	public static final String NONE = "N/A";

	// Export User
	@FXML
	private Button exportButton;

	private ExportUserService serviceExportUser;

	// Import user
	@FXML
	private TextField userIDTextField;
	@FXML
	private VBox importUserVBox;
	@FXML
	private TextField outputFileTextField;
	@FXML
	private CheckBox truncateCheckBox;
	@FXML
	private Button browseButton;
	@FXML
	private Button importButton;

	private String filePathValues, filePathPublicIP, filePathUserInfo;

	private ImportUserService serviceImportUser;

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

	/**
	 * Estimates the name of the user Info file given
	 * a pattern of the input training values file. For example,
	 * 'export-trainVal-Anushka.csv' -> 'export-userInfo-Anushka.txt'
	 */
	private static String getUserInfoFileName(String valuesFileName)
	{
		String valuesFileNameWoExt = StringUtils.removeExtension(valuesFileName);
		String FILE_OUTPUT_TRAINVAL_WO_EX = StringUtils.removeExtension(DeimosConfig.FILE_OUTPUT_TRAINVAL);
		String FILE_OUTPUT_USERINFO_WO_EX = StringUtils.removeExtension(DeimosConfig.FILE_OUTPUT_USERINFO);

		return valuesFileNameWoExt.replace(FILE_OUTPUT_TRAINVAL_WO_EX, FILE_OUTPUT_USERINFO_WO_EX)+".txt";
	}

	/**
	 * Estimates the name of the public IP file given
	 * a pattern of the input training values file. For example,
	 * 'export-trainVal-Anushka.csv' -> 'export-publicIP-Anushka.txt'
	 */
	private static String getPublicIPFileName(String valuesFileName)
	{
		String valuesFileNameWoExt = StringUtils.removeExtension(valuesFileName);
		String FILE_OUTPUT_PUBLICIP_WO_EX = StringUtils.removeExtension(DeimosConfig.FILE_OUTPUT_PUBLICIP);
		String FILE_OUTPUT_TRAINVAL_WO_EX = StringUtils.removeExtension(DeimosConfig.FILE_OUTPUT_TRAINVAL);

		return valuesFileNameWoExt.replace(FILE_OUTPUT_TRAINVAL_WO_EX, FILE_OUTPUT_PUBLICIP_WO_EX)+".txt";
	}

	@FXML
	private void initialize()
	{
		initializePredictListSpinner();
		initializeExportUser();
		initializeImportUser();
		initializeNeuralTraining();
		initializePrediction();

		handleResyncUsersButton();
		// handleTrainButton(); - BUGGY

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

	private void initializeExportUser()
	{
		serviceExportUser = new ExportUserService();

		serviceExportUser.setOnSucceeded(e -> {
			tpStatus.setText("Exported user!");
		});
	}

	private void initializeImportUser()
	{
		serviceImportUser = new ImportUserService();
		serviceImportUser.setOnRunning(e-> {
			importUserVBox.setDisable(true);
		});
		serviceImportUser.setOnCancelled(e-> {
			importUserVBox.setDisable(false);
		});
		serviceImportUser.setOnFailed(e-> {
			importUserVBox.setDisable(false);
			
			Throwable ex = serviceImportUser.getException();
			
			if(ex instanceof FileNotFoundException)
				GUIUtils.generateErrorAlert("One or more files are missing!\n\n"
						+ "The User Info and Public IP files must also be included along with the training values.", null);
			else if(ex instanceof NumberFormatException)
				GUIUtils.generateErrorAlert("Invalid training input file format!\n\n"
						+ "The training values file is a CSV file containing category names and decimal values. Please confirm the correct file is being"
						+ " imported and try again.", null);
			else
				GUIUtils.generateErrorAlert("Unknown error occurred!", null);
			
			tpStatus.setText("Imported user failed...");
			// serviceImportUser.getException().printStackTrace();
		});
		serviceImportUser.setOnSucceeded(e -> {
			importUserVBox.setDisable(false);
			tpStatus.setText("Imported user!");
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

	@FXML
	private void magnifyImage()
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Word Cloud");
		alert.setHeaderText("Categories of interest");

		// Set expandable Exception into the dialog pane. */
		ImageView content = new ImageView();
		content.setFitHeight(WordCloudGenerator.DIM_SIDE/2);
		content.setFitWidth(WordCloudGenerator.DIM_SIDE/2);
		content.setImage(wordCloudImage.getImage());
		alert.getDialogPane().setContent(content);

		alert.showAndWait();
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
			/*predictedInterestsLabel.setText(topics.toString());
			predictedInterestsLabel.setTooltip(new Tooltip(topics.toString()));*/
			predictedLocationLabel.setText(servicePrediction.getLocation());

			tpStatus.setText("Prediction complete.");
		});
		servicePrediction.setOnRunning(e -> {

			predictButton.setDisable(true);
			tpStatus.setText("Predicting...");

			predictedAgeGroupLabel.setText(PROCESSING);
			predictedGenderLabel.setText(PROCESSING);
			// predictedInterestsLabel.setText(PROCESSING);
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
	private void handleResyncUsersButton()
	{

		trainButton.setDisable(true);
		GUIUtils.startAgain(servicePredictList);
	}

	@FXML
	private void handleExportButton()
	{
		currentUser = userPredictSpinner.getValue();
		serviceExportUser.setUserId(currentUser.getUserId());
		GUIUtils.startAgain(serviceExportUser);
	}

	/**
	 * Returns 1 higher than the largest userId in the list.
	 * @return
	 */
	private int autoGenerateUserId()
	{
		if(usersPrediction.size() == 0)
			return 1;
		else {
			int userId = usersPrediction.get(usersPrediction.size()-1).getUserId()+1;
			userIDTextField.setText(String.valueOf(userId));
			return userId;
		}
	}

	@FXML
	private void handleImportButton()
	{
		// TODO
		System.out.println("Importing started.");

		int userId = -1;
		boolean userIdCheck = false;

		if((userIDTextField.getText().isEmpty()))
		{
			System.out.println("Auto-generating user Id.");
			userId = autoGenerateUserId();
			userIdCheck = true;
		}
		else
		{
			try
			{
				userId = Integer.parseInt(userIDTextField.getText());
				userIdCheck = true;
			}
			catch (NumberFormatException e)
			{
				String message = "Invalid User-ID, please enter a number!";
				GUIUtils.generateErrorAlert(message, null);

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Something's not right.");
				alert.setHeaderText("Invalid User-ID");
				alert.setContentText("Select OK to auto-generate a userId by default, or select Cancel to change it manually.");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK)
				{
					// ... user chose OK
					userId = autoGenerateUserId();
					userIdCheck = true;
				}
				else {
					// ... user chose CANCEL or closed the dialog
				}
			}
		}

		if(userIdCheck)
		{
			System.out.println("Passed userId check, userId chosen = "+userId);
			
			boolean truncate = truncateCheckBox.isSelected();
			boolean alreadyExists = UserURLsTF.doesUserIdExist(userId);		
			boolean proceed = false;

			if(alreadyExists && !truncate)
			{
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Something's not right.");
				alert.setHeaderText("User-ID already exists!");
				alert.setContentText("Clear any existing rows with this "
						+ "user ID in the table and continue anyway?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK)
				{
					// ... user chose OK
					truncateCheckBox.setSelected(true);
					proceed = true;
				}
				else {
					// ... user chose CANCEL or closed the dialog
				}	
			}
			else
				proceed = true;

			if(proceed)
			{					
				// Set parameter Values
				serviceImportUser.setUserId(userId);
				serviceImportUser.setTruncate(truncateCheckBox.isSelected());
				serviceImportUser.setFilePathValues(filePathValues);
				serviceImportUser.setFilePathPublicIP(filePathPublicIP);
				serviceImportUser.setFilePathUserInfo(filePathUserInfo);

				System.out.println("Importing...");
				GUIUtils.startAgain(serviceImportUser);
			}
		}
		else
			System.out.println("Failed userId check. Won't continue.");

	}

	@FXML
	private void handleTrainButton()
	{
		System.out.println("\nTraining started.");
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
	private void handleBrowseButton()
	{
		// TODO
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Training Input Values File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Comma-separated values", "*.csv"));

		File fileVal = fileChooser.showOpenDialog(new Stage());

		filePathValues = DeimosConfig.FILE_OUTPUT_TRAINVAL;
		filePathPublicIP = DeimosConfig.FILE_OUTPUT_PUBLICIP;
		filePathUserInfo = DeimosConfig.FILE_OUTPUT_USERINFO;

		if(fileVal != null)
		{
			filePathValues = fileVal.toString();
			outputFileTextField.setText(filePathValues);

			// Also derive other files
			filePathPublicIP = getPublicIPFileName(filePathValues);
			filePathUserInfo = getUserInfoFileName(filePathValues);
		}
		else
		{
			System.out.println("Invalid file operation in browse button. Reverting to default files.");

			setDefaultInputFileNames();
		}
	}

	/**
	 * Sets the 3 parameters filePath to their default values.
	 */
	private void setDefaultInputFileNames()
	{
		filePathValues = DeimosConfig.FILE_OUTPUT_TRAINVAL;
		filePathPublicIP = DeimosConfig.FILE_OUTPUT_PUBLICIP;
		filePathUserInfo = DeimosConfig.FILE_OUTPUT_USERINFO;
		outputFileTextField.setText(DeimosConfig.FILE_OUTPUT_TRAINVAL);
	}

	@FXML
	private void handlePredictButton()
	{

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
		// predictedInterestsLabel.setText(NONE);
		predictedLocationLabel.setText(NONE);

		predictButton.setDisable(false);

		wordCloudImage.setImage(DeimosImages.IMG_WORDCLOUD_PLACEHOLDER);
	}
}
