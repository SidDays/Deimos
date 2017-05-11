package deimos.phase1.gui.view.services;

import java.net.UnknownHostException;

import deimos.common.DeimosConfig;
import deimos.common.StringUtils;
import deimos.phase1.ExportIP;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Usage service
 * @author Siddhesh Karekar
 */
public class PublicIPService extends Service<Void> {

	private String nameTag = null;

	public void setNameTag(String nt)
	{
		this.nameTag = nt;
	}

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call() throws UnknownHostException {
				ExportIP.retrievePublicIPAsFile(StringUtils.addTagToFileName(DeimosConfig.FILE_OUTPUT_PUBLICIP, nameTag));

				return null;
			}
		};
	}

}