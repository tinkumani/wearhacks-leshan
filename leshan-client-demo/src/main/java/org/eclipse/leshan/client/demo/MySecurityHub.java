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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.resource.ResourceChangedListener;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.opencv.core.Mat;
import org.poseidon.EventDetails;
import org.poseidon.camera.SecurityCameraListener;

import hub.TrackerHub;
import hub.TrackerListener;

public class MySecurityHub extends BaseInstanceEnabler implements TrackerListener{
	private final ScheduledExecutorService scheduler;
	final TrackerHub tracker= new TrackerHub();
	MySecurityHub() {		
		tracker.addTrackerListener(this);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                  try {
          			tracker.startTracking();
          			
          		} catch (Exception e) {			
          		}
            }
        }, 2, 2, TimeUnit.SECONDS);
		
    }

	@Override
	public synchronized ReadResponse read(int resourceid) {
		return tracker.read(resourceid);
	}

	@Override
	public synchronized WriteResponse write(int resourceid, LwM2mResource value) {
		
		return tracker.write(resourceid,value);
	}

	@Override
	public synchronized ExecuteResponse execute(int resourceid, String params) {
		return tracker.execute(resourceid, params);
	}

	@Override
	public synchronized void reset(int resourceid) {
		tracker.reset(resourceid);
	}

	@Override
	public void eventReceived(int resourceId, EventDetails eventDetails) {
		// TODO Auto-generated method stub
		
	}

	

}
