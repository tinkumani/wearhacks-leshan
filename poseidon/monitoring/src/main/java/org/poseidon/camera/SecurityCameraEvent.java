package org.poseidon.camera;

import java.awt.image.BufferedImage;

import org.poseidon.EventDetails;

public class SecurityCameraEvent implements EventDetails {

	public enum Event{
		PERSON_MISSING,PERSON_FOUND,VIDEO_CLIP
	}
	private BufferedImage currentImage;
	private BufferedImage previousImage;
	public Event event;
	private String filename;

	public SecurityCameraEvent(BufferedImage buffImage, BufferedImage buffImage2,Event event) {
		this.previousImage=buffImage;
		this.currentImage=buffImage2;
		this.event=event;
	}
	public SecurityCameraEvent(String filename,Event event) {
		this.filename=filename;
		this.event=event;
	}

	public BufferedImage getCurrentImage() {
		return currentImage;
	}

	public BufferedImage getPreviousImage() {
		return previousImage;
	}

	@Override
	public String getType() {
		return SecurityCameraEvent.class.getSimpleName();
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	

}
