package org.poseidon.chat;

import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.poseidon.EventDetails;
import org.poseidon.IOControl;
import org.poseidon.IOListener;

public class FacebookChatControl implements IOControl {

	@Override
	public ReadResponse readValue(int resourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eventReceived(int resourceId, EventDetails eventDetails) {
		// TODO Auto-generated method stub

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
	public WriteResponse writeValue(int rId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addIOListerner(IOListener iolistener) {
		// TODO Auto-generated method stub

	}

}