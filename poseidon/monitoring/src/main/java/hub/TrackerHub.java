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
import org.poseidon.GenericControl;
import org.poseidon.IOControl;
import org.poseidon.IOListener;
import org.poseidon.InputControl;
import org.poseidon.OutputControl;
import org.poseidon.camera.Camera;
import org.poseidon.chat.FacebookChatControl;
import org.poseidon.dropbox.DropBoxControl;
import org.poseidon.led.LedControl;



public class TrackerHub implements IOListener{
	Map<Integer,IOControl> inputOutputControls=new HashMap<Integer,IOControl>(){{put(Camera.RESOURCE_ID,new Camera());put(FacebookChatControl.RESOURCE_ID,new FacebookChatControl());put(LedControl.RESOURCE_ID,new LedControl());}};
	Map<Integer,OutputControl> outputControls=new HashMap<Integer,OutputControl>(){{put(DropBoxControl.RESOURCE_ID,new DropBoxControl());}};
	Map<Integer,InputControl> inputControls=new HashMap<Integer,InputControl>(){};
	private TrackerListener trackerListener;
	public TrackerHub() {

	}
	public void startTracking() {
		for (Map.Entry<Integer, IOControl> entry : inputOutputControls.entrySet()) {
		entry.getValue().addIOListerner(this);
		try {
			entry.getValue().startTracking();
		} catch (Exception e) {
			e.printStackTrace();
		}
		}

	}
	public void addTrackerListener(TrackerListener trackerListener) {
		this.trackerListener=trackerListener;

	}
	public ReadResponse read(int resourceid) {
		 InputControl control=inputOutputControls.get(getIId(resourceid));
		 if(control==null){control=inputControls.get(getIId(resourceid));}
		 return control.readValue(getRId(resourceid));
	}

	public WriteResponse write(int resourceid, LwM2mResource value) {
		OutputControl control=inputOutputControls.get(getIId(resourceid));
		 if(control==null){control=outputControls.get(getIId(resourceid));}
		return control.writeValue(getRId(resourceid),value);
	}
	public ExecuteResponse execute(int resourceid, String params) {
		GenericControl control=inputOutputControls.get(getIId(resourceid));
		 if(control==null){control=outputControls.get(getIId(resourceid));}
		 if(control==null){control=inputControls.get(getIId(resourceid));}
		return control.execute(getRId(resourceid),params);
	}
	public void reset(int resourceid) {

		GenericControl control=inputOutputControls.get(getIId(resourceid));
		 if(control==null){control=outputControls.get(getIId(resourceid));}
		 if(control==null){control=inputControls.get(getIId(resourceid));}
		control.reset(resourceid);

	}
	private int getIId(int resourceid) {
		return resourceid/100;
	}
	private int getRId(int resourceid) {
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
