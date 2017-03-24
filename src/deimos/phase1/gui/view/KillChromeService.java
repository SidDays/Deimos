package deimos.phase1.gui.view;

import deimos.phase1.ExportAll;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Not a Usage service
 * @author Siddhesh Karekar
 *
 */
public class KillChromeService extends Service<Void> {

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {

			@Override
			public Void call(){
				
				ExportAll.killChrome();
				
				return null;
			}
		};
	}
}
