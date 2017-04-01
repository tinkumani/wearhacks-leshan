package hub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.poseidon.EventDetails;
import org.poseidon.IOControl;
import org.poseidon.IOListener;
import org.poseidon.InputControl;
import org.poseidon.OutputControl;
import org.poseidon.camera.Camera;
import org.poseidon.led.LedControl;



public class TrackerHub implements IOListener{
	Map<String,IOControl> inputOutputControls=new HashMap<String,IOControl>(){{put("67",new Camera());}};
	Map<String,OutputControl> outputControls=new HashMap<String,OutputControl>(){{put("77",new LedControl());}};
	Map<String,InputControl> inputControls=new HashMap<String,InputControl>(){};
	private TrackerListener trackerListener;
	public TrackerHub() {

	}
	public void startTracking() {
		for (Map.Entry<String, IOControl> entry : inputOutputControls.entrySet()) {
		entry.getValue().addIOListerner(this);
		}
		
	}
	public void addTrackerListener(TrackerListener trackerListener) {
		this.trackerListener=trackerListener;
		
	}
	public ReadResponse read(int resourceid) {
		return inputOutputControls.get(getIId(resourceid)).readValue(getRId(resourceid));
	}
	
	public WriteResponse write(int resourceid, LwM2mResource value) {
		return outputControls.get(getIId(resourceid)).writeValue(getRId(resourceid));
	}
	public ExecuteResponse execute(int resourceid, String params) {
		// TODO Auto-generated method stub
		return null;
	}
	public void reset(int resourceid) {
		// TODO Auto-generated method stub
		
	

	}
	private int getIId(int resourceid) {
		// TODO Auto-generated method stub
		return resourceid/100;
	}
	private int getRId(int resourceid) {
		// TODO Auto-generated method stub
		return resourceid%100;
	}
	@Override
	public void eventOccured(int resourceId, EventDetails eventDetails) {
		//Announce the event
		for (Map.Entry<String, IOControl> entry : inputOutputControls.entrySet()) {
			entry.getValue().eventReceived(resourceId,eventDetails);
			}
		for (Map.Entry<String, OutputControl> entry : outputControls.entrySet()) {
			entry.getValue().eventReceived(resourceId,eventDetails);
			}
		for (Map.Entry<String, InputControl> entry : inputControls.entrySet()) {
			entry.getValue().eventReceived(resourceId,eventDetails);
			}
		trackerListener.eventReceived(resourceId,eventDetails);
		
	}
}
