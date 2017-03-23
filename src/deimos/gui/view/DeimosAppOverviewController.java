package deimos.gui.view;

import deimos.common.DeimosConfig;
import deimos.gui.DeimosApp;
import deimos.phase1.ExportBookmarks;
import deimos.phase2.similarity.SimilarityMapper;
import deimos.phase2.user.UserIDF;
import deimos.phase2.user.UserURLsTF;
import deimos.phase2.user.UserWeightCalculation;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class DeimosAppOverviewController {
	
	private DeimosApp mainApp;
	@FXML
    private TextField userIDTextField;
	int userId;
    @FXML
    private TextField outputFileTextField;
    @FXML
    private ProgressBar progressURLsBar;
    @FXML
    private ProgressBar progressTFBar;
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
    
    private URLsTFService serviceURLsTF;
    private IDFService serviceIDf;
    private WeightService serviceWeights;
    private SimilarityService serviceSimilarity;
    
    
    public DeimosAppOverviewController() {

    	System.out.println("Started Deimos Application GUI.");
    }
    
    /**
     * Is called by the main application to give a reference back to itself.
     * @param mainApp
     */
    public void setMainApp(DeimosApp mainApp) {
        this.mainApp = mainApp;
    }
    
    @FXML
    private void handleStartButton() {
    	
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
