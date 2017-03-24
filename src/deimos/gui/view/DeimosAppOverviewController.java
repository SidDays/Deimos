package deimos.gui.view;

import java.io.File;

import deimos.common.DeimosConfig;
import deimos.gui.DeimosApp;
import deimos.phase1.ExportBookmarks;
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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

public class DeimosAppOverviewController {
	
	private DeimosApp mainApp;
	@FXML
    private TextField userIDTextField;
	int userId;
    @FXML
    private TextField outputFileTextField;
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
    private Button browseButton;
    @FXML
    private Button startButton;
    
    private String regex = "\\d+";
    
    private URLsTFService serviceURLsTF;
    private IDFService serviceIDf;
    private WeightService serviceWeights;
    private SimilarityService serviceSimilarity;
    
    private String validationError = "";
    
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
    	
    }
    
    private void initializeURLsTF() {
    	serviceURLsTF = new URLsTFService();
    	
    	serviceURLsTF.setOnSucceeded(e1 -> {
    		progressURLsTFBar.setProgress(1);
    	});
    	serviceURLsTF.setOnFailed(e1 -> {
    		progressURLsTFBar.setProgress(0);
    	});
    	serviceURLsTF.setOnCancelled(e1 -> {
    		progressURLsTFBar.setProgress(0);
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
    	Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error in User Input");
		alert.setHeaderText("Input Error");

		Label label = new Label("Please fix the following errors:");
		
		validationError = s;
		TextArea textArea = new TextArea(validationError);
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
    
    @FXML
    private void handleBrowseButton() {
    	
    }
    
    @FXML
    private void handleStartButton() {
    	String message = "";
    	
    	if(!(userIDTextField.getText().trim().isEmpty())) {
    		if(userIDTextField.getText().matches(regex)) {
    			userId = Integer.parseInt(userIDTextField.getText());

    			userIDTextField.setDisable(true);
    			startButton.setDisable(true);
    			
    			if(! UserURLsTF.doesUserIdExist(userId)) {
    				progressURLsTFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    				statusLabel.setText("user_url and user_tf insertion");
        			startAgain(serviceURLsTF);

        			/*progressIDFBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        			startAgain(serviceIDf);

        			progressWeightBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        			startAgain(serviceWeights);

        			progressSimilarityBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        			startAgain(serviceSimilarity);*/
    			}
    			else {
    				message = "User-ID already exists!";
    				generateAlerts(message);
    			}
    			
    		}
    		else {
    			message = "Invalid User-ID, please enter a number!";
    			//generateAlerts(message);
    			
    			Alert alertTosAgree = new Alert(AlertType.ERROR);
    			alertTosAgree.initOwner(mainApp.getPrimaryStage());
    			alertTosAgree.setContentText("Click on the checkbox next to 'I Agree', then click on Start again.");
    			alertTosAgree.setTitle("You must agree to the Deimos Helper ToS");
    			alertTosAgree.showAndWait();
    		}
    	}
    	else {
    		message = "User-ID field can't be empty!";
			generateAlerts(message);
    	}
    }
    
    private class URLsTFService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {

			return new Task<Void>() {
	            @Override
	            public Void call(){
	            
	            	UserURLsTF.userURLAndTFTableInsertion(userId);
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
