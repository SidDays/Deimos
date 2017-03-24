package deimos.phase1.gui.view;

import deimos.common.DeimosConfig;
import deimos.phase1.ExportBookmarks;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Usage Service
 * @author Siddhesh Karekar
 *
 */
public class BookmarkService extends Service<Void> {

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
            @Override
            public Void call(){
            
            	ExportBookmarks.retreiveBookmarksAsFile(DeimosConfig.FILE_OUTPUT_BOOKMARKS);
               	return null;
            }
		};
	}
	
}
