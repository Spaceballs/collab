package Listeners;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;

import javax.swing.JButton;



import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

public class GestureClass extends Listener {
	

	JButton[] auswahl = new JButton[7];	
	int index = 0;
	JButton button;
	
	
    private Robot robot;
    private int mouseX, mouseY;
    private int screenSizeWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int screenSizeHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
    
    // private Hand hand = null;
    
    class InvalidHandOrFingerException extends Exception {
        private static final long serialVersionUID = 1L;
        
    }
    

    /**
     * Finger Detection States
     */
    private final int STANDBY = 0;
    private final int ONEFINGER = 1;
    private final int TWOFINGERS = 2;
    private final int THREEFINGERS = 3;
    
    /**
     * Finger Movement States
     */
    private final int OPENED = 1;
    private final int CLOSEING = 2;
    private final int CLOSED = 3;
    private final int OPENING = 4;
    private final int SUCCEDED = 5;
    		
    /**
     * Abortion States
     */
    // private final int SWIPEING = 10;

    /**
     * 
     */
    private int movementState;
    private int numberOfFingerState;
    
    
    /**
	 * Called once, when this Listener object is newly added to a Controller.
	 * 
	 * @param controller
	 */
	public void onInit(Controller controller) {
		System.out.println("Init Done");
        mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX();
        mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY();
        // state = STANDBY;
        numberOfFingerState = STANDBY;
        movementState = STANDBY;
	}
	

	/**
	 * Called when the Controller object connects to the Leap Motion software and hardware, 
	 * or when this Listener object is added to a Controller that is already connected.
	 * 
	 * @param controller
	 */
	public void onConnect(Controller controller) {
		System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        robot.mouseMove(mouseX, mouseY);
	}
	
	
	/**
	 * Called when the Controller object disconnects from the Leap Motion software. 
	 * The controller can disconnect when the Leap Motion device is unplugged, 
	 * the user shuts the Leap Motion software down, or the Leap Motion software encounters an unrecoverable error. 
	 * 
	 * Note: When you launch a Leap-enabled application in a debugger, 
	 * the Leap Motion library does not disconnect from the application. 
	 * This is to allow you to step through code without losing the connection because of time outs.
	 * 
	 * @param controller
	 */
	public void onDisconnect(Controller controller) {
		System.out.println("Disconnected");
	}
	
	
	
	
	/**
	 * Called when this Listener object is removed from the Controller or the Controller instance is destroyed.
	 * 
	 * @param controller
	 */
	public void onExit(Controller controller) {
        System.out.println("Exited");
    }
	
	
	
	
	/**
	 * Called when a new frame of hand and finger tracking data is available. 
	 * Access the new frame data using the Controller.frame() function.
	 * 
	 * Note, the Controller skips any pending on_frame events while your on_frame handler executes. 
	 * If your implementation takes too long to return, one or more frames can be skipped. 
	 * The Controller still inserts the skipped frames into the frame history. 
	 * You can access recent frames by setting the history parameter when calling the Controller.frame() function. 
	 * You can determine if any pending on_frame events were skipped by comparing the ID of the most recent frame with the ID of the last received frame.
	 * 
	 * @param controller
	 */
	public void onFrame (Controller controller) {
		// Get the Fingers of the first Hand into FingerList
        FingerList fingerList = controller.frame().hands().get(0).fingers();
        Finger fingerOne = fingerList.leftmost();
        Finger fingerTwo = fingerList.rightmost();
        // Get the gestures recognized or continuing in this frame.
        GestureList gestureList = controller.frame().gestures();
        
		switch (numberOfFingerState) {
			case STANDBY:
				try {
			        	setFingerState(fingerNumberInPastFrames(controller, controller.frame().hands().get(0).id(), 30));
			       	} catch (InvalidHandOrFingerException ex) {
			        }
				swipeGesture(controller, gestureList);
				break;
			case ONEFINGER:
				pointerToolControl(controller, gestureList);
				sissorGesture(controller, fingerOne, fingerTwo);
				break;
			case TWOFINGERS:
				devilGesture(controller, fingerList);
				phoneGesture(controller, fingerList);
				sissorGesture(controller, fingerOne, fingerTwo);
				break;
			case THREEFINGERS:	
				americanThree(controller, fingerList);
				swipeGesture(controller, gestureList);
				setFingerState(STANDBY);
				// System.out.println("THREEFINGERS");
				break;
			default:
				swipeGesture(controller, gestureList);
				setFingerState(STANDBY);
				break;
		}
		
    }
	

	/**
	 * Devil gesture detection
	 * 
	 * @param controller
	 * @param fingerList
	 */
	private void devilGesture (Controller controller, FingerList fingerList){		
		Finger fingerOne = fingerList.get(0);
		Finger fingerTwo = fingerList.get(1);		
	    if(calculateFingerDistance(fingerOne, fingerTwo) > 70F && fingerOne.direction().angleTo(fingerTwo.direction()) < 0.45F && calculateFingerDistance(fingerOne, fingerTwo) < 95F && fingerOne.timeVisible()> 1F && fingerTwo.timeVisible() > 1F){
	    	System.out.println("Devil Gesture Found");
	    }
	    resetStates();
	}
	
	
	/**
	 * American Three gesture detection
	 * 
	 * @param controller
	 * @param fingerList
	 */
	private void americanThree(Controller controller, FingerList fingerList) {
		Finger fingerOne = fingerList.get(0);
		Finger fingerTwo = fingerList.get(1);
		Finger fingerThree = fingerList.get(2);
		if (
				((calculateFingerDistance(fingerOne, fingerTwo) < 45) && (calculateFingerDistance(fingerTwo, fingerThree) < 45) && (calculateFingerDistance(fingerThree, fingerOne) > 45))
				||
				((calculateFingerDistance(fingerTwo, fingerThree) < 45) && (calculateFingerDistance(fingerOne, fingerThree) < 45) && (calculateFingerDistance(fingerTwo, fingerOne) > 45))
				||
				((calculateFingerDistance(fingerTwo, fingerOne) < 45) && (calculateFingerDistance(fingerOne, fingerThree) < 45) && (calculateFingerDistance(fingerTwo, fingerThree) > 45))
				) {
			//gui.setAuswahlDown(1);
		}
		resetStates();
	}
	
	/**
	 * Phone gesture detection
	 * 
	 * @param controller
	 * @param fingerList
	 */
	private void phoneGesture(Controller controller, FingerList fingerList) {
		Finger fingerOne = fingerList.get(0);
		Finger fingerTwo = fingerList.get(1);
		if ( fingerOne.direction().angleTo(fingerTwo.direction()) > 0.45F && calculateFingerDistance(fingerOne, fingerTwo) > 100F) {
			System.out.println("Phone gesture" );
			//gui.setAuswahlDown(2);
		}
		resetStates();
	}
	
	
	/**
	 * Siccors gesture
	 * 
	 * @param controller
	 * @param fingerList
	 */
	private void sissorGesture (Controller controller, Finger fingerOne, Finger fingerTwo ){
		switch (movementState) {
		case STANDBY:
			if(calculateFingerDistance(fingerOne, fingerTwo) > 24F && calculateFingerDistance(fingerOne, fingerTwo) < 60F){
				setMovementState(OPENED);
				System.out.println("   OPENED   ");
			}
			break;
		case OPENED:
			setMovementState(checkFingerMovementInPastFrames(controller, 58, CLOSEING, movementState));
			break;
		case CLOSEING:
			if (calculateFingerDistance(fingerOne, fingerTwo) <= 5F || controller.frame().hand(0).fingers().count() == 1) {
				setMovementState(CLOSED);
				System.out.println("   CLOSED   ");
			}
			break;
		case CLOSED:
			setMovementState(checkFingerMovementInPastFrames(controller, 58, OPENING, movementState));
			break;
		case OPENING:
			if(calculateFingerDistance(fingerOne, fingerTwo) >24F && calculateFingerDistance(fingerOne, fingerTwo) < 60F){
				setMovementState(SUCCEDED);
				System.out.println("   SUCCEDED   ");
			}
			break;
		case SUCCEDED:
			System.out.println("Siccors detected");
			resetStates();
			break;
		default:
			break;
		}	
	}
	
	
	/**
	 * Swipe gesture detection
	 * 
	 * @param controller
	 * @param gestures
	 */
	private boolean swipeGesture(Controller controller, GestureList gestures) {
	    for (int i = 0; i < gestures.count(); i++) {
	    	Gesture gesture = gestures.get(i);
	    	
	        if (gesture.type() == Gesture.Type.TYPE_SWIPE) {
	        	System.out.println("Abort gesture detected");
	        	//resetStates();
	        	gesture.delete();
	        	//gui.setAuswahlDown(-1);
	        	return true;
	        }
	        
	    }
		return false;
	}


	/**
	 * Control method for the pointer and click tools
	 * 
	 * @param controller
	 * @param gestures
	 */
	private void pointerToolControl(Controller controller, GestureList gestures) {
		if (movementState != CLOSED) {
			pointerTool(controller);
	        for (int i = 0; i < gestures.count(); i++) {
	            Gesture gesture = gestures.get(i);
				cicleGesture(controller, gesture);
				keyTap(controller, gesture);
				screenTap(controller, gesture);
	        }
		}
	}

	
	/**
	 * Pointer Finger Detection
	 * 
	 * @param controller
	 * @param fingerList
	 */
	private void pointerTool(Controller controller) {
        Finger finger = controller.frame().hand(0).fingers().frontmost();
        Vector dir = finger.direction();
        mouseX = Math.max(
                0,
                Math.min(screenSizeWidth, mouseX
                        + (int) (dir.getX() * 5)));
        mouseY = Math.max(
                0,
                Math.min(screenSizeHeight, mouseY
                        - (int) (dir.getY() * 5)));
        robot.mouseMove(mouseX, mouseY);
	}
	

	/**
	 * Gesture usage for Pointer Finger
	 * 
	 * @param controller
	 * @param gestures
	 */
	private void cicleGesture(Controller controller, Gesture gesture) {
        if (gesture.type() == Gesture.Type.TYPE_CIRCLE) {
            CircleGesture circle = new CircleGesture(gesture);
            int progress = Math.round(circle.progress() % 1) ;
            if(progress == 1){
                if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4) {
                    System.out.println("found right circle gesture ...");
                    resetStates();
                } else {
                    System.out.println("found left circle gesture ...");
                    resetStates();
                }
            } 
        } 
	}
	
	
	/**
	 * Screen tap detection
	 * 
	 * @param controller
	 * @param gesture
	 */
	private void screenTap(Controller controller, Gesture gesture){
        if (gesture.type() == Gesture.Type.TYPE_SCREEN_TAP) {
            System.out.println("Choose selection");
            resetStates();
        }
	}
	
	/**
	 * Key tap detection
	 * 
	 * @param controller
	 * @param gesture
	 */
	private void keyTap(Controller controller, Gesture gesture){
        if (gesture.type() == Gesture.Type.TYPE_KEY_TAP) {
        	System.out.println("Choose selection");
        	resetStates();
        }
	}
	
	
	/**
	 * Setting the fingercountvariable
	 * 
	 * @param s
	 */
	private void setFingerState(int s){numberOfFingerState = s;}
	/**
	 * Setting the fingermovementvariable
	 * 
	 * @param i
	 */
	private void setMovementState(int i) {movementState = i;}
	/**
	 * Restetting states
	 */
	private void resetStates() {movementState = STANDBY; numberOfFingerState = STANDBY;}
	
	
	/**
	 * 
	 * @param fingers
	 * @return
	 *
    private Vector calculateAverageFingerDirection (FingerList fingers) {
        // Calculate the hand's average finger tip position
        Vector avgPos = Vector.zero();
        for (Finger finger : fingers) {
            avgPos = avgPos.plus(finger.direction());
        }
        avgPos = avgPos.divide(fingers.count());
        return avgPos;
    }
    */

	
    /**
     * 
     * @param fingers
     * @return avgVel
     *
    private Vector calculateAverageFingerTipVelocity (FingerList fingers) {
        // Calculate the hand's average finger tip position
        Vector avgVel = Vector.zero();
        for (Finger finger : fingers) {
            avgVel = avgVel.plus(finger.tipVelocity());
        }
        avgVel = avgVel.divide(fingers.count());
        return avgVel;
    }
    */

	
    /**
     * 
     * @param controller
     * @param handId
     * @param frames
     * @return fingers
     * @throws InvalidHandOrFingerException
     */
    private int fingerNumberInPastFrames(Controller controller, int handId, int frames)
            throws InvalidHandOrFingerException {
        Hand hand = controller.frame(0).hand(handId);
        int fingers = hand.fingers().count();
        for (int i = 0; i < frames; i++) {
            hand = controller.frame(i).hand(handId);
            if (!hand.isValid() || hand.fingers().count() != fingers) {
                throw new InvalidHandOrFingerException();
            }
        }
        return fingers;
    }
	
    
    /**
     * 
     * 
     * @param controller
     * @param frames
     * @param toCheckMovementState
     * @param previousState
     * @return
     */
    private int checkFingerMovementInPastFrames (Controller controller, Integer frames, Integer toCheckMovementState, Integer previousState) {
    	if (toCheckMovementState == CLOSEING){
        	int counterCloseing = 0;
    		for(int i=0; i<frames; i++){
				Finger fingerOneX = controller.frame(i).hands().get(0).fingers().leftmost();
				Finger fingerTwoX = controller.frame(i).hands().get(0).fingers().rightmost();
				Finger fingerOneY = controller.frame(i+1).hands().get(0).fingers().leftmost();
				Finger fingerTwoY = controller.frame(i+1).hands().get(0).fingers().rightmost();
				
				if(calculateFingerDistance(fingerOneX, fingerTwoX) < calculateFingerDistance(fingerOneY, fingerTwoY) ){
					counterCloseing++;
					
				} 
				System.out.println(counterCloseing);
			}
			if (counterCloseing >= (frames/3)) {
				System.out.println("   CLOSEING 1  ");
				return CLOSEING;
			}
    	}
    	
    	if (toCheckMovementState == OPENING){
        	int counterOpening = 0;
			for(int i=0; i<frames; i++){
				Finger fingerOneX = controller.frame(i).hands().get(0).fingers().leftmost();
				Finger fingerTwoX = controller.frame(i).hands().get(0).fingers().rightmost();
				Finger fingerOneY = controller.frame(i+1).hands().get(0).fingers().leftmost();
				Finger fingerTwoY = controller.frame(i+1).hands().get(0).fingers().rightmost();
				
				if(calculateFingerDistance(fingerOneX, fingerTwoX) > calculateFingerDistance(fingerOneY, fingerTwoY)){
					counterOpening++;
				}
			}
			if (counterOpening >= (frames/4)) {
				System.out.println("   OPENING 1  ");
				return OPENING;
			}
    	}
		return previousState; 	
	}
    
    
	private float calculateFingerDistance(Finger fingerOne, Finger fingerTwo){
		return fingerOne.tipPosition().minus(fingerTwo.tipPosition()).magnitude();
	}
	
	
	
	/**
	private int getNumberOfFingers(Controller controller) {
        hand = controller.frame().hands().get(0);
        if (hand.timeVisible()<1) {
            return 0;
        }
        try {
            // only change number of fingers when consistently seen for 20
            // frames
            return fingerNumberInPastFrames(controller, hand.id(), 20);
        } catch (InvalidHandOrFingerException ex) {
        }
	}
	*/
	
	
	/**
	 * Some Tests
	 * 
	 * @param controller
	 *
	private void fingerTest(Controller controller) {
        Frame tmpFrame = null;
        int totalNumberOfFingers = 0;
        for(int i=0; i<59;i++){
        	tmpFrame = controller.frame(i);
        	if(tmpFrame.hands().count()>0){
        		totalNumberOfFingers += tmpFrame.hands().leftmost().fingers().count();
        	}
        }    
        System.out.println("Fingertest:" + totalNumberOfFingers/59);
	}
	*/
}
