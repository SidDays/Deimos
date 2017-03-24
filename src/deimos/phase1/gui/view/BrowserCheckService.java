package deimos.phase1.gui.view;

import deimos.common.BrowserCheck;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * uses BrowserCheck to check if a browser is available,
 * if it is, controls should be enabled on its success.
 * Not a Usage Service
 * 
 * @author Siddhesh Karekar
 */
public class BrowserCheckService extends Service<Void> {
	
	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
        	
        	// Check if Google Chrome can be used
            public Void call(){
            	
            	// TODO Remove this later! Used to simulate a delay
            	try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				
    				e.printStackTrace();
    			}
            	if(BrowserCheck.isChromeAvailable()) {
            		
            		System.out.println("Google Chrome is available.");
            	}
            	else {
            		this.cancel();
            	}
               	return null;
            }
        };
	}

}
