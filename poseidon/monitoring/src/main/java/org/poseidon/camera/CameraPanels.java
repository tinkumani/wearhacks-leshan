package org.poseidon.camera;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class CameraPanels extends JPanel {

	private BufferedImage image;
	private String name;
    private JSlider slider;
	private int xpos;
	private int ypos;
	private int width_;
	private int height_;
	public CameraPanels(String name) {
		setLayout(new BorderLayout());
		this.name=name;
        slider = new JSlider();
        slider.setMinimum(0);
        slider.setMaximum(100);
        slider.setMinorTickSpacing(2);
        slider.setMajorTickSpacing(10);
        slider.setValue(0);
        Hashtable labels =
                new Hashtable();
        labels.put(0, new JLabel("Sensitivity"));
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
        slider.setEnabled(false);
        add(slider, BorderLayout.SOUTH);
	}

	public void setImage(BufferedImage bufferedImage)
	{
		this.image=bufferedImage;
		repaint();
		
	}
	
	public void setSensitivity(Integer sensitivity)
	{
		slider.setValue(sensitivity);
		repaint();
	}
	public Integer getSensitivity()
	{
    return slider.getValue();
	}
	public void drawRect(int x,int y,int width,int height)
	{
		this.xpos=x;
		this.ypos=y;
		this.width_=width;
		this.height_=height;
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(image!=null)
			g.drawImage(image, 10,10,image.getWidth(),image.getHeight(),this);
		    g.setColor(new Color(0, 255, 0));
		    g.drawRect(xpos, ypos, width_, height_);
	}

}
