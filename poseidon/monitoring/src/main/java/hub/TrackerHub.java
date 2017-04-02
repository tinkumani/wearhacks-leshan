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
	Map<Integer,IOControl> inputOutputControls=new HashMap<Integer,IOControl>(){{put(Camera.RESOURCE_ID,new Camera());}};
	Map<Integer,OutputControl> outputControls=new HashMap<Integer,OutputControl>(){{put(LedControl.RESOURCE_ID,new LedControl());}};
	Map<Integer,InputControl> inputControls=new HashMap<Integer,InputControl>(){};
	private TrackerListener trackerListener;
	public TrackerHub() {

	}
	public void startTracking() {
		for (Map.Entry<Integer, IOControl> entry : inputOutputControls.entrySet()) {
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
		for (Map.Entry<Integer, IOControl> entry : inputOutputControls.entrySet()) {
			return entry.getValue().execute(resourceid,params);
			}
		for (Map.Entry<Integer, OutputControl> entry : outputControls.entrySet()) {
			return entry.getValue().execute(resourceid,params);
			}
		for (Map.Entry<Integer, InputControl> entry : inputControls.entrySet()) {
			return entry.getValue().execute(resourceid,params);
			}
		return null;
	}
	public void reset(int resourceid) {
		
		for (Map.Entry<Integer, IOControl> entry : inputOutputControls.entrySet()) {
			entry.getValue().reset(resourceid);
			}
		for (Map.Entry<Integer, OutputControl> entry : outputControls.entrySet()) {
			entry.getValue().reset(resourceid);
			}
		for (Map.Entry<Integer, InputControl> entry : inputControls.entrySet()) {
			entry.getValue().reset(resourceid);
			}
	
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
		for (Map.Entry<Integer, IOControl> entry : inputOutputControls.entrySet()) {
			entry.getValue().eventReceived(resourceId,eventDetails);
			}
		for (Map.Entry<Integer, OutputControl> entry : outputControls.entrySet()) {
			entry.getValue().eventReceived(resourceId,eventDetails);
			}
		for (Map.Entry<Integer, InputControl> entry : inputControls.entrySet()) {
			entry.getValue().eventReceived(resourceId,eventDetails);
			}
		trackerListener.eventReceived(resourceId,eventDetails);
		
	}
}