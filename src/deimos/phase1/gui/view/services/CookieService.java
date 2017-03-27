package deimos.phase1.gui.view.services;

import org.sqlite.SQLiteException;

import deimos.common.DeimosConfig;
import deimos.phase1.ExportCookies;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Usage service
 * @author Siddhesh Karekar
 *
 */
public class CookieService extends Service<Void> {

	@Override
	protected Task<Void> createTask() {
		
		return new Task<Void>() {
            @Override
            public Void call() throws SQLiteException
            {
            	ExportCookies.retreiveCookiesAsFile(DeimosConfig.FILE_OUTPUT_COOKIES);
               	return null;
            }
        };
	}
}