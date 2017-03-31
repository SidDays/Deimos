package deimos.gui.view.services;

import deimos.phase3.Neural;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class NeuralTrainingService extends Service<Void> {
	
	private double error;

	public NeuralTrainingService() {
		error = Neural.ERROR_ALLOWED_DEFAULT;
	}
	
	public void setError(double error)
	{
		this.error = error;
	}

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call()
			{
				Neural.train(error);
				
				return null;
			}
		};
	}

}