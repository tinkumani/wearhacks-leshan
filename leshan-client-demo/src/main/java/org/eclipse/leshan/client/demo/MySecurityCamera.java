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
import org.poseidon.SecurityCameraListener;
import org.poseidon.Tracker;

public class MySecurityCamera extends BaseInstanceEnabler implements SecurityCameraListener{
    MySecurityCamera() {
        Tracker tracker = new Tracker();
        try {
			tracker.startTracking();
			tracker.addSecurityCameraListener(this);
		} catch (Exception e) {			
		}
    }

	@Override
	public void addResourceChangedListener(ResourceChangedListener listener) {
		// TODO Auto-generated method stub
		super.addResourceChangedListener(listener);
	}

	@Override
	public void removeResourceChangedListener(ResourceChangedListener listener) {
		// TODO Auto-generated method stub
		super.removeResourceChangedListener(listener);
	}

	@Override
	public void fireResourcesChange(int... resourceIds) {
		// TODO Auto-generated method stub
		super.fireResourcesChange(resourceIds);
	}

	@Override
	public ReadResponse read(int resourceid) {
		// TODO Auto-generated method stub
		return super.read(resourceid);
	}

	@Override
	public WriteResponse write(int resourceid, LwM2mResource value) {
		// TODO Auto-generated method stub
		return super.write(resourceid, value);
	}

	@Override
	public ExecuteResponse execute(int resourceid, String params) {
		// TODO Auto-generated method stub
		return super.execute(resourceid, params);
	}

	@Override
	public void reset(int resourceid) {
		// TODO Auto-generated method stub
		super.reset(resourceid);
	}

}
