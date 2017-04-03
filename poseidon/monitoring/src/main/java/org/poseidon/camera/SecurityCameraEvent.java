package org.poseidon.camera;

import java.awt.image.BufferedImage;

import org.poseidon.EventDetails;

public class SecurityCameraEvent extends EventDetails {

	private BufferedImage currentImage;
	private BufferedImage previosImage;

	public SecurityCameraEvent(BufferedImage buffImage, BufferedImage buffImage2) {
		this.previosImage=buffImage;
		this.currentImage=buffImage2;
	}

}
