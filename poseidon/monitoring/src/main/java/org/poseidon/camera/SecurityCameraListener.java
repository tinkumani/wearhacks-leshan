package org.poseidon.camera;

import org.opencv.core.Mat;

public interface SecurityCameraListener {

	void fireSecurityAlert(Mat image, Mat previousImage);

}
