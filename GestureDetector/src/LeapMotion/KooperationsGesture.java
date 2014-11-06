package LeapMotion;

//import LeapMotion.GesteUPorDown;
import java.awt.AWTException;
import java.io.IOException;
import java.lang.Math;
import javax.swing.JButton;

import org.omg.CORBA.PUBLIC_MEMBER;

import LeapMotion.LeapMotionGUI.*;
import com.leapmotion.leap.*;
//import com.leapmotion.leap.Gesture.State;

class KooperationsGesture01 extends Listener {
	
	public LeapMotionGUI gui;
	
	public KooperationsGesture01(LeapMotionGUI c) {
		gui = c;
	}
	Controller controller;
	
	//GesteUPorDown gesteupordown;
	
	//LeapMotionGUI gui = new LeapMotionGUI();
	JButton[] auswahl = new JButton[7];	
	int index = 0;
	JButton button;
	
	private final static int DEFAULT = 0;
	
	private final static int MENUESELECTION = 10;
	private final static int MENUEDEFAULT = 11;
	private final static int TIPDOWN = 12;
	private final static int ENTERMIDDLE = 13;
	private final static int TIPUP = 14;
	
	//private final static int ENTER = 20;
	
	private int state = DEFAULT;
	private int stateMenue = MENUEDEFAULT;
	
	
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }
    
    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }
    
    public void onFrame(Controller controller) {

 
        Frame frame = controller.frame();
        Frame framePast = controller.frame(25);

        if (!frame.hands().isEmpty()) {
            // Get the first hand
            Hand hand = frame.hands().get(0);
            Hand handPast = framePast.hands().get(0);            
            
            FingerList fingers = hand.fingers();
        	Finger finger = hand.fingers().leftmost();
        	int fingerID = finger.id();
        	
//        	float handVelo = hand.palmVelocity().getY();
        	float fingerVelo = finger.tipVelocity().getX();
        	
        	Vector fingerDirV = hand.finger(fingerID).direction();
        	Vector fingerDirPastV = handPast.finger(fingerID).direction();
//        	float fingerDir = hand.finger(fingerID).direction().getX();
//        	float fingerDirPast = handPast.finger(fingerID).direction().getX();
        	
        	float handDir = hand.direction().pitch();   
        	
//        	float handPos = hand.palmPosition().getY();
//        	float handPosPast = handPast.palmPosition().getY();
        	
        	
            switch (state){
            case DEFAULT:
            	if (fingers.count() == 1){
            		setState(MENUESELECTION);
            		System.out.println("MENUESELECTION..");
            		break;
            	}
            	else{
            		setState(DEFAULT);
            		System.out.println("DEFAULT..");
            		break;
            	}
//            	if(enter(handPos, handPosPast, handVelo)){
//            		setState(ENTER);
//            		break;
//            	}
                
            case MENUESELECTION:
            	if(handDir < -0.09){
        			if(fingerVelo > 250)
        				if(fingerDirPastV.angleTo(fingerDirV) > 0.17F){
        					setStateM(TIPDOWN);
        					break;
        				}
            	}else if(handDir > 0.09){
            		if(fingerVelo > 250)
            			if(fingerDirPastV.angleTo(fingerDirV) > 0.17F){
            				setStateM(TIPUP);
            				break;
            			}
            	}else if(fingerVelo > 250){
            		if(fingerDirPastV.angleTo(fingerDirV) > 0.17F){
            			setStateM(ENTERMIDDLE);
            			break;
            		}            		
            	}else if(fingers.count() > 3){
            		setState(DEFAULT);
            		System.out.println("DEFAULT");
            		break; 
            		
            	}else if(frame.hands().isEmpty()){
            		setState(DEFAULT);
            		System.out.println("DEFAULT");
            		break;
            	}
            		           		            	            	
            	switch(stateMenue){
            	case MENUEDEFAULT:
                	//System.out.println(checkMenueState(handDir));
            		break;
            	case TIPDOWN:
            		setState(MENUESELECTION);
            		setStateM(MENUEDEFAULT);
            		auswahl = gui.getButtons();
            		index += 1;
            		if(index == auswahl.length){
            			System.out.print("ENDE erreicht");
            			break;
            		}
            		else{
            			gui.setAuswahlDown(index);
            			System.out.println("Tip Down..");
            			break;
            		}            		
            		            		
            	case ENTERMIDDLE:
            		setState(MENUESELECTION);
            		setStateM(MENUEDEFAULT);
            		gui.setEnter(index);
            		System.out.println("Enter..");            		
            		break;
            		
            	case TIPUP:
            		setState(MENUESELECTION);
            		setStateM(MENUEDEFAULT);
            		index = index - 1;
            		if(index < 0){
            			System.out.print("Anfang erreicht");
            			break;
            		}
            		else{
            			gui.setAuswahlUp(index);
            			System.out.println("Tip Up..");
            			break;
            		}            		
            	default:
            		setState(MENUESELECTION);
            		setStateM(MENUEDEFAULT);
            		break;
            	}
            }
        }
    }
    
    private void setStateM(int s){
    	stateMenue = s;
    }
    
    private void setState(int s){
		state = s;
    }       	
	
//	public int checkMenueState(float handDir){
//		if(handDir < -0.09)
//			return TIPDOWN;
//		else if(handDir > 0.09)
//			return TIPUP;
//		else
//			return ENTERMIDDLE;
//	}
	
}

class KooperationsGesture {
    public static void main(String[] args) {
        // Create a sample listener and controller
        
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        
       
        LeapMotionGUI c = new LeapMotionGUI();
        c.buttonInit();
        KooperationsGesture01 listener01 = new KooperationsGesture01(c);
        controller.addListener(listener01);
        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener01);
    }
}
