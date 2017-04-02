package org.poseidon.led;

import org.eclipse.leshan.core.response.WriteResponse;
import org.poseidon.EventDetails;
import org.poseidon.OutputControl;

public class LedControl implements OutputControl{
	public static int RESOURCE_ID=77;

	@Override
	public void eventReceived(int resourceId, EventDetails eventDetails) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WriteResponse writeValue(int rId) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
