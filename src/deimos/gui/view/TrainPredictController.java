package deimos.gui.view;

import deimos.common.DeimosImages;
import deimos.common.GUIUtils;
import deimos.gui.view.services.WordCloudService;
import javafx.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.fxml.FXML;

public class TrainPredictController {
	
	private int userId = -1;
	
	@FXML
	private ImageView wordCloudImage;
	
	@FXML
	private Button generateWordCloudButton;
	
	private WordCloudService serviceWordCloud;
	
	@FXML
	private void initialize() {
		initializeWordCloud();
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
	@FXML
	private void handleWordCloudGenerateButton() {

		try {
			
			// TODO
			// userId = Integer.parseInt(analyzeController.userIDTextField.getText());
			userId = 2;
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

}
