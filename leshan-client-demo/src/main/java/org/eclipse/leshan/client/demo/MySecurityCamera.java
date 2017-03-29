/*******************************************************************************
 * Copyright (c) 2017 Sierra Wireless and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.client.demo;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.resource.ResourceChangedListener;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.opencv.core.Mat;
import org.poseidon.SecurityCameraListener;
import org.poseidon.Tracker;

public class MySecurityCamera extends BaseInstanceEnabler implements SecurityCameraListener{
	 Tracker tracker=null;
	private static final int STATUS=6701;
	private static final int MODE=6700;
    MySecurityCamera() {
        tracker = new Tracker();
        try {
			tracker.startTracking();
			tracker.addSecurityCameraListener(this);
		} catch (Exception e) {			
		}
    }

	@Override
	public synchronized ReadResponse read(int resourceid) {
		switch (resourceid) {
		case MODE:
			return ReadResponse.success(resourceid,tracker.getSecurityMode());			
		case STATUS:
			return ReadResponse.success(resourceid, tracker.getStatus());

		default:
			
		}
		return null;
	}

	@Override
	public synchronized WriteResponse write(int resourceid, LwM2mResource value) {
		// TODO Auto-generated method stub
		return super.write(resourceid, value);
	}

	@Override
	public synchronized ExecuteResponse execute(int resourceid, String params) {
		// TODO Auto-generated method stub
		return super.execute(resourceid, params);
	}

	@Override
	public synchronized void reset(int resourceid) {
		// TODO Auto-generated method stub
		super.reset(resourceid);
	}

	@Override
	public void fireSecurityAlert(Mat image, Mat previousImage) {
		// TODO Auto-generated method stub
		
	}

}
