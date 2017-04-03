package org.poseidon.camera;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class CameraPanels extends JPanel {

	private BufferedImage image;

	public void setImage(BufferedImage bufferedImage)
	{
		this.image=bufferedImage;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(image!=null)
			g.drawImage(image, 10,10,image.getWidth(),image.getHeight(),this);
	}

}
