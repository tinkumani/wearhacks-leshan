package org.poseidon;

import org.opencv.core.Core;

public class App
{
    public void startTracking()
    {
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    }
}
