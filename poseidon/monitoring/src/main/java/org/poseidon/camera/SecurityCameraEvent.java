package org.poseidon.camera;

import java.awt.image.BufferedImage;

import org.poseidon.EventDetails;

public class SecurityCameraEvent extends EventDetails {

	private BufferedImage currentImage;
	private BufferedImage previousImage;

	public SecurityCameraEvent(BufferedImage buffImage, BufferedImage buffImage2) {
		this.previousImage=buffImage;
		this.currentImage=buffImage2;
	}

	public BufferedImage getCurrentImage() {
		return currentImage;
	}

	public BufferedImage getPreviousImage() {
		return previousImage;
	}

}
