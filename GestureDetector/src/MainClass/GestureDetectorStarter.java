/**
 * 
 */
package MainClass;

/**
 * 
 */
import java.io.IOException;

import Listeners.GestureClass;

import com.leapmotion.leap.Controller;


/**
 * @author Sebastian
 *
 */
public class GestureDetectorStarter {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        // Create a sample listener and controller
		GestureClass listener = new GestureClass();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Remove the sample listener when done
        controller.removeListener(listener);
	}

}
