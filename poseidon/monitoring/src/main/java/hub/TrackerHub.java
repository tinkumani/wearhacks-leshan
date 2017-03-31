package hub;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.poseidon.InputControl;
import org.poseidon.OutputControl;
import org.poseidon.camera.Camera;
import org.poseidon.led.LedControl;



public class TrackerHub {
	Map<String,InputControl> inputControls=new HashMap<String,InputControl>(){{put("abc",new Camera());}};
	OutputControl[] outputControls=new OutputControl[]{new LedControl()};
	public TrackerHub() {

	}
	public void startTracking() {
		// TODO Auto-generated method stub
		
	}
	public void addTrackerListener(TrackerListener mySecurityHub) {
		// TODO Auto-generated method stub
		
	}
	public ReadResponse read(int resourceid) {
		// TODO Auto-generated method stub
		return null;
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


}
