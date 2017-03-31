package deimos.gui.view.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.util.List;

import deimos.phase3.Neural;
import deimos.phase3.User;

public class PredictListService extends Service<Void>{
	
	private List<User> populater = null;
	
	public List<User> getPopulater()
	{
		return populater;
	}
	
	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call() {
				
				populater = Neural.separateUsers();
				
				return null;
			}
		};
	}
}
