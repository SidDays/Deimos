package deimos.gui.view.services;

import deimos.gui.WordCloudTest;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class WordCloudService extends Service<Void> {
	
	private int userId;
	public Image bi;
	
	public WordCloudService() {
		userId = -1;
	}
	
	public void setUserId(int id)
	{
		this.userId = id;
	}

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call(){
				
				WordCloudTest.outputWordCloud(userId);
				bi = SwingFXUtils.toFXImage(WordCloudTest.wordCloudImage, null);
				
				return null;
			}
		};
	}

}