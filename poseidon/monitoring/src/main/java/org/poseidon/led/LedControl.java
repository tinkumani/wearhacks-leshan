package org.poseidon.led;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.poseidon.EventDetails;
import org.poseidon.IOControl;
import org.poseidon.IOListener;
import org.poseidon.OutputControl;
import org.poseidon.camera.Camera;
import org.poseidon.camera.SecurityCameraEvent;

public class LedControl extends JPanel implements IOControl{
	public static int RESOURCE_ID=78;
	private static final int CANCEL_ALARM = 21;


	private JFrame jframe;
	private JPanel jpanel;
	private Timer panicTimer=null;
	private AtomicBoolean panic=new AtomicBoolean(false);
	private Timer blinkTimer=new Timer();
	private long alertDelay;
	public LedControl() {
		jframe = new JFrame("Led Light");
		jpanel = new JPanel();
		jframe.setSize(200,350);
		lightState=3;
		blinkTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(lightState==0){
					lightState=tempLightState;}
				else
				{
					tempLightState=lightState;
					lightState=0;
				}
				repaint();
				
			}
		}, 500l,500l);

	}
	
	public synchronized void startPanic(boolean panic)
	{
	  
	  if(panic==false)
	  {
		  if(this.panic.get()==true)
		  {
			  panicTimer.cancel();
			  lightState=3;
		  }
	  }
	  else
	  {
		  lightState=2;
		panicTimer=new Timer();
		panicTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				lightState=1;
				ioListener.eventOccured(0, new EventDetails() {
					
					@Override
					public String getType() {
						return "ALARM";
					}
				});
				
			}
		}, getDelay());
	  }
	  this.panic.set(panic);
	}

	private long getDelay() {
		return alertDelay;
	}
	private void setDelay(long delay)
	{
		alertDelay=delay;
	}

	public boolean launch()
	{
		if(jframe.isVisible())return false;
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Dimension frameSize = jframe.getSize();
				jframe.setLocation(((screenSize.width - frameSize.width) / 2),
									((screenSize.height - frameSize.height) / 2));
				jframe.getContentPane().add(jpanel, BorderLayout.SOUTH);
				jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jframe.setVisible(true);
				return true;
			}

		/** Variable to store the current state of the traffic light.
		 * @ lightState = 1 (Red)
		 * @ lightState = 2 (Yellow)
		 * @ lightState = 3 (Green)
		 */
		private int lightState = 1;
		
		private int tempLightState = 0;
		private IOListener ioListener;

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
		switch(resourceId)
		{
		case Camera.RESOURCE_ID:
			SecurityCameraEvent cameraEvent=(SecurityCameraEvent) eventDetails;
			if(cameraEvent.getEvent()==SecurityCameraEvent.Event.PERSON_MISSING)
			{
			startPanic(true);
			}
			else if(cameraEvent.getEvent()==SecurityCameraEvent.Event.PERSON_FOUND)
			{
				if(panic.get()==true)
				{
					startPanic(false);
				}
			}
		}

	}

	@Override
	public WriteResponse writeValue(int resourceid,LwM2mResource resource) {
		switch(resourceid)
		{

		case CANCEL_ALARM:startPanic(!new Boolean(resource.getValue().toString()).booleanValue());break;

		}
		return WriteResponse.success();
	}

	@Override
	public ExecuteResponse execute(int resourceid, String params) {
		switch(resourceid)
		{

		case CANCEL_ALARM:startPanic(!new Boolean(params).booleanValue());break;

		}
		return ExecuteResponse.success();
	}

	@Override
	public void reset(int resourceid) {
		// TODO Auto-generated method stub

	}

	@Override
	public ReadResponse readValue(int resourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addIOListerner(IOListener iolistener) {
		this.ioListener=iolistener;
		
	}

	@Override
	public void startTracking() throws Exception {
		// TODO Auto-generated method stub
		
	}


}
