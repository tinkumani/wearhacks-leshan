package hub;

import org.poseidon.EventDetails;

public interface TrackerListener {

	public void eventReceived(int resourceId, EventDetails eventDetails);

}
