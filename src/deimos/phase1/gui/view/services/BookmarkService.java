package deimos.phase1.gui.view.services;

import deimos.common.DeimosConfig;
import deimos.common.StringUtils;
import deimos.phase1.ExportBookmarks;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Usage Service
 * @author Siddhesh Karekar
 *
 */
public class BookmarkService extends Service<Void> {
	
	private String nameTag = null;

	public void setNameTag(String nt)
	{
		this.nameTag = nt;
	}

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
            @Override
            public Void call(){
            
            	ExportBookmarks.retreiveBookmarksAsFile(StringUtils.addTagToFileName(DeimosConfig.FILE_OUTPUT_BOOKMARKS, nameTag));
               	return null;
            }
		};
	}
	
}
