package org.poseidon;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class Tracker
{
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
    public void startTracking()
    {
    	    	
    			Mat image = new Mat();
    			Mat thresholdedImage = new Mat();
    			Mat hsvImage = new Mat();
    			VideoCapture capture = null;
    			


    }
    public static void main(String[] args) {
		Tracker tracker=new Tracker();
		new JCommander(tracker, args);
		tracker.run();
	}
}
