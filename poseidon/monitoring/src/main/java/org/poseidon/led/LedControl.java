package org.poseidon.led;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.poseidon.EventDetails;
import org.poseidon.OutputControl;

public class LedControl extends JPanel implements OutputControl{
	public static int RESOURCE_ID=77;
	
	
	private JFrame jframe;
	private JPanel jpanel;
	public LedControl() {
		jframe = new JFrame("Traffic Light");
		jpanel = new JPanel();
		jframe.setSize(200,350);

	}

	public void launch()
	{
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Dimension frameSize = jframe.getSize();
				jframe.setLocation(((screenSize.width - frameSize.width) / 2),
									((screenSize.height - frameSize.height) / 2));		
				jframe.getContentPane().add(jpanel, BorderLayout.SOUTH);
				jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jframe.setVisible(true);
			}
	
		/** Variable to store the current state of the traffic light.
		 * @ lightState = 1 (Red)
		 * @ lightState = 2 (Yellow)
		 * @ lightState = 3 (Green)
		 */
		private int lightState = 1;	 

		/**
		 * This method repaints the light status
		 */
		public void changeColor() {		
			lightState++;

			if (lightState > 3) {
				lightState = 1;
			}	
			repaint();	
		}

		/**
		 * This method draws the traffic light on the screen
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);		

			// Draws the traffic light
			// Draw out white frame
			g.setColor(new Color(255,255,255));
			g.fillRoundRect(35,15,120,225,30,30);

			// Draw inner black frame
			g.setColor(new Color(0,0,0));
			g.fillRoundRect(50,30,90,195,30,30);
			g.drawRoundRect(35,15,120,225,30,30);

			// RED bulb dim		
			g.setColor(new Color(100,0,0));
			g.fillOval(70,40,50,50);		

			// YELLOW bulb dim
			g.setColor(new Color(100,100,0));
			g.fillOval(70,100,50,50);

			// GREEN bulb dim
			g.setColor(new Color(0,100,0));
			g.fillOval(70,160,50,50);

			// Draw traffic light stand
			g.setColor(new Color(50,50,50));
			g.fillRect(80,240,30,30);

			switch(lightState) {
			case 1:
				// RED bulb glows
				g.setColor(new Color(255,0,0));
				g.fillOval(70,40,50,50);				
				break;

			case 2:
				// YELLOW bulb glows
				g.setColor(new Color(255,255,0));
				g.fillOval(70,100,50,50);		
				break;

			case 3:
				// GREEN bulb glows
				g.setColor(new Color(0,255,0));
				g.fillOval(70,160,50,50);		
				break;		
			}
		}
	@Override
	public void eventReceived(int resourceId, EventDetails eventDetails) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WriteResponse writeValue(int rId,LwM2mResource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExecuteResponse execute(int resourceid, String params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset(int resourceid) {
		// TODO Auto-generated method stub
		
	}
	

}
