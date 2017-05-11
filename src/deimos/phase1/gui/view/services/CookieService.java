package deimos.phase1.gui.view.services;

import org.sqlite.SQLiteException;

import deimos.common.DeimosConfig;
import deimos.common.StringUtils;
import deimos.phase1.ExportCookies;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Usage service
 * @author Siddhesh Karekar
 *
 */
public class CookieService extends Service<Void> {
	
	private String nameTag = null;
	
	public void setNameTag(String nt)
	{
		this.nameTag = nt;
	}

	@Override
	protected Task<Void> createTask() {
		
		return new Task<Void>() {
            @Override
            public Void call() throws SQLiteException
            {
            	ExportCookies.retreiveCookiesAsFile(StringUtils.addTagToFileName(DeimosConfig.FILE_OUTPUT_COOKIES, nameTag));
               	return null;
            }
        };
	}
}