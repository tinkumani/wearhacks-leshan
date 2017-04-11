package org.poseidon.chat;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.poseidon.EventDetails;
import org.poseidon.IOControl;
import org.poseidon.IOListener;
import org.poseidon.camera.Camera;
import org.poseidon.camera.SecurityCameraEvent;

public class FacebookChatControl implements IOControl {
	public static int RESOURCE_ID=68;

	@Override
	public ReadResponse readValue(int resourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eventReceived(int resourceId, EventDetails eventDetails) {
		switch (resourceId) {
		case 0:break;
		case Camera.RESOURCE_ID:
			SecurityCameraEvent cameraEvent = (SecurityCameraEvent) eventDetails;
			if (cameraEvent.getEvent() == SecurityCameraEvent.Event.VIDEO_CLIP) {
				//uploadFile(cameraEvent.getFilename());
			} else if (cameraEvent.getEvent() == SecurityCameraEvent.Event.PERSON_MISSING)
			{
			//uploadFile(((SecurityCameraEvent) eventDetails).getPreviousImage());
			//uploadFile(((SecurityCameraEvent) eventDetails).getCurrentImage());
			}
			break;
		
		}

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

	@Override
	public WriteResponse writeValue(int rId,LwM2mResource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addIOListerner(IOListener iolistener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startTracking() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
