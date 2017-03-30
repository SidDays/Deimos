package deimos.gui.view.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TrainingValuesService extends Service<Void> {
	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call() {
				return null;
			}
		};
	}
}
