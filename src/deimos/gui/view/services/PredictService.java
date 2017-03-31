package deimos.gui.view.services;

import deimos.gui.WordCloudGenerator;
import deimos.phase3.LocationEstimator;
import deimos.phase3.Neural;
import deimos.phase3.ServerLocation;
import deimos.phase3.User;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class PredictService extends Service<Void> {
	
	private Image bi;
	private User u;
	// private String[] interests;
	
	public Image getBi() {
		return bi;
	}
	
	private String publicIP;
	
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
				
				location = LocationEstimator.estimateLocation(publicIP);
				
				WordCloudGenerator.outputWordCloud(u.getUserId());
				bi = SwingFXUtils.toFXImage(WordCloudGenerator.getWordCloudImage(), null);
				
				Neural.predict(u);
				
				return null;
			}
		};
	}

}