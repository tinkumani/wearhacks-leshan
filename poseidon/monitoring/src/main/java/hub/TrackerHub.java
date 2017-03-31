package hub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.poseidon.InputControl;
import org.poseidon.OutputControl;
import org.poseidon.camera.Camera;
import org.poseidon.led.LedControl;



public class TrackerHub {
	Map<String,InputControl> inputControls=new HashMap<String,InputControl>(){{put("67",new Camera());}};
	Map<String,OutputControl> outputControls=new HashMap<String,OutputControl>(){{put("77",new LedControl());}};
	private TrackerListener trackerListener;
	public TrackerHub() {

	}
	public void startTracking() {
		// TODO Auto-generated method stub
		
	}
	public void addTrackerListener(TrackerListener trackerListener) {
		this.trackerListener=trackerListener;
		
	}
	public ReadResponse read(int resourceid) {
		inputControls.get(getInputId(resourceid)).readValue(getResourceId(resourceid));
	}
	
	public WriteResponse write(int resourceid, LwM2mResource value) {
		// TODO Auto-generated method stub
		return null;
	}
	public ExecuteResponse execute(int resourceid, String params) {
		// TODO Auto-generated method stub
		return null;
	}
	public void reset(int resourceid) {
		// TODO Auto-generated method stub
		
	

	}
	private Object getInputId(int resourceid) {
		// TODO Auto-generated method stub
		return null;
	}
	private Object getResourceId(int resourceid) {
		// TODO Auto-generated method stub
		return null;
	}
}
