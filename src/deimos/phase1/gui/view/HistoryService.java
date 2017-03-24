package deimos.phase1.gui.view;

import org.sqlite.SQLiteException;

import deimos.common.DeimosConfig;
import deimos.phase1.ExportHistory;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Usage Service
 * @author Siddhesh Karekar
 *
 */
public class HistoryService extends Service<Void> {

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
            @Override
            public Void call() throws SQLiteException
            {
            	ExportHistory.retreiveHistoryAsFile(DeimosConfig.FILE_OUTPUT_HISTORY);
            	return null;
            }
        };
	}
	
}