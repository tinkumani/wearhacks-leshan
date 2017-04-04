package org.poseidon.camera;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class CameraPanels extends JPanel {

	private BufferedImage image;
	private String name;

	public CameraPanels(String name) {
		this.name=name;
	}

	public void setImage(BufferedImage bufferedImage)
	{
		this.image=bufferedImage;
		repaint();
		System.out.println("Repainted..."+name);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(image!=null)
			g.drawImage(image, 10,10,image.getWidth(),image.getHeight(),this);
	}

}
