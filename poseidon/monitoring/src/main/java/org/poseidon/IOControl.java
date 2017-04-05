package org.poseidon;

public interface IOControl extends InputControl, OutputControl {

	public void addIOListerner(IOListener iolistener);

	public void startTracking() throws Exception;

}
