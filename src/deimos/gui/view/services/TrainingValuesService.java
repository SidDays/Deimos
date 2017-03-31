package deimos.gui.view.services;

import deimos.phase2.user.UserTrainingInput;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TrainingValuesService extends Service<Void> {

	private int userId = -1;
	private boolean truncate = false;

	public void setUserId(int id)
	{
		this.userId = id;
	}
	
	public void setTruncate(boolean truncate)
	{
		this.truncate = truncate;
	}
	
	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call() {
				UserTrainingInput.calculateTrainingInputs(userId, truncate);
				return null;
			}
		};
	}
}
