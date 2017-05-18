package deimos.gui.view.services;

import java.util.List;

import deimos.common.TimeUtils;
import deimos.phase3.LocationEstimator;
import deimos.phase3.Neural;
import deimos.phase3.NeuralConstants;
import deimos.phase3.ServerLocation;
import deimos.phase3.User;
import deimos.phase3.WordCloudGenerator;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class PredictService extends Service<Void> {
	
	private Image bi;
	private User u;

	private String predictedGroup;
	private String predictedAge;
	private String predictedGender;
	private List<String> predictedInterests;
	
	public List<String> getPredictedInterests() {
		return predictedInterests;
	}

	public String getPredictedAge() {
		return predictedAge;
	}


	public String getPredictedGender() {
		return predictedGender;
	}

	public Image getBi() {
		return bi;
	}
	
	private ServerLocation location;
	
	public String getLocation() {
		return location.getCountryName();
	}
	
	public void setUser(User u)
	{
		this.u = u;
	}

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call(){
				
				long startTime = System.currentTimeMillis();
				location = LocationEstimator.estimateLocation(u.getPublicIP());
				long stopTime = System.currentTimeMillis();
				System.out.format("IP-based estimation finished in %s.\n",
						TimeUtils.formatHmss(stopTime-startTime));
				
				startTime = System.currentTimeMillis();
				WordCloudGenerator.outputWordCloud(u.getUserId());
				bi = SwingFXUtils.toFXImage(WordCloudGenerator.getWordCloudImage(), null);
				predictedInterests = WordCloudGenerator.getInterests();
				stopTime = System.currentTimeMillis();
				System.out.format("Wordcloud finished in %s.\n",
						TimeUtils.formatHmss(stopTime-startTime));
				
				double predictedRow[] = Neural.predict(u);
				predictedGroup = NeuralConstants.getClosestGroup(predictedRow);
				System.out.println("Predicted groups.");
				
				startTime = System.currentTimeMillis();
				predictedAge = predictedGroup.substring(0, predictedGroup.indexOf(" ")).trim();
				predictedGender = predictedGroup.substring(predictedGroup.indexOf(" ")).trim();
				stopTime = System.currentTimeMillis();
				System.out.format("Prediction finished in %s.\n",
						TimeUtils.formatHmss(stopTime-startTime));
				
				return null;
			}
		};
	}

}