package LeapMotion;

import javax.swing.*;
import java.awt.*;


public class LeapMotionGUI {	
	
	JFrame frame = new JFrame("Comand Controll");
	
	public static JButton option1 = new JButton("Option 1");
	public static JButton option2 = new JButton("Option 2");
	public static JButton option3 = new JButton("Option 3");
	public static JButton option4 = new JButton("Option 4");
	public static JButton option5 = new JButton("Option 5");
	public static JButton option6 = new JButton("Option 6");
	public static JButton option7 = new JButton("Option 7");
	
	JPanel pane1 = new JPanel();
	
	JButton[] alleButtons = new JButton[7];
	
//	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//	
//	double x = dim.getWidth();
//	double y = dim.getHeight();
	
	
	
	public LeapMotionGUI(){	
		
		frame.setSize(800, 600);	
		frame.setLocationRelativeTo(null);
		
		pane1.setLayout(new GridBagLayout());
		frame.getContentPane().add(pane1);
		
		GridBagConstraints c;
		Insets set = new Insets(5, 5, 5, 5);
		c = new GridBagConstraints();
		c.insets = set;
		c.gridy = 0;
		c.ipadx = 100;
		c.ipady = 10;
		
		c.anchor = GridBagConstraints.LINE_START;
		pane1.add(option1, c);
		c.gridy = 1;
		pane1.add(option2, c);
		c.gridy = 2;
		pane1.add(option3, c);
		c.gridy = 3;
		pane1.add(option4, c);
		c.gridy = 4;
		pane1.add(option5, c);
		c.gridy = 5;
		pane1.add(option6, c);
		c.gridy = 6;
		pane1.add(option7, c);
		
		c.fill = GridBagConstraints.VERTICAL;
		
						
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);		

		
		alleButtons[0] = option1;
		alleButtons[1] = option2;
		alleButtons[2] = option3;
		alleButtons[3] = option4;
		alleButtons[4] = option5;
		alleButtons[5] = option6;
		alleButtons[6] = option7;
		
	}
	
	public JButton[] getButtons(){
		return alleButtons;
	}
	
	public void setAuswahlDown(int index){
		//frame.getRootPane().setDefaultButton(alleButtons[index]);
		alleButtons[index].setFocusable(true);
		alleButtons[index-1].setFocusable(false);
		//alleButtons[index].setFocusPainted(true);
		//alleButtons[index-1].setFocusPainted(false);

	}
	
	public void setEnter(int index){
		alleButtons[index].setForeground(Color.red);
		alleButtons[index].setSelected(true);
		try {
		    Thread.sleep(200);
		    alleButtons[index].setForeground(Color.black);
		    alleButtons[index].setSelected(false);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}

		
	}
	
	public void setAuswahlUp(int index){
		alleButtons[index].setFocusable(true);
		alleButtons[index+1].setFocusable(false);
	}
	
	public void buttonInit(){
		for(int i = 0; i < alleButtons.length; i++){
			alleButtons[i].setFocusable(true);
			alleButtons[i].setFocusable(false);
		}
		alleButtons[0].setFocusable(true);
	}
}

