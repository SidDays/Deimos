package deimos.gui.view.services;

import deimos.phase2.user.UserWeightCalculation;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class WeightService extends Service<Void> {
	private int userId = -1;

	public void setUserId(int id)
	{
		this.userId = id;
	}

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call(){

				if(userId == -1)
					System.err.println("Warning: userId = -1.");

				UserWeightCalculation.updateWeights(userId);
				return null;
			}
		};
	}
}