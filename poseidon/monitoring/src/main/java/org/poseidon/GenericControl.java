package org.poseidon;

import org.eclipse.leshan.core.response.ExecuteResponse;

public interface GenericControl {
	public void eventReceived(int resourceId, EventDetails eventDetails);
	public ExecuteResponse execute(int resourceid, String params);
	public  void reset(int resourceid);
}