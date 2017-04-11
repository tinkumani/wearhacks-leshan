package org.poseidon.camera;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JComboBox;
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
	private Map<String, JSlider> sliders = null;

	public CameraPanels(String name, List<String> cameras, ActionListener actionListener) {
		setLayout(new BorderLayout());
		this.name = name;
		JComboBox cameraList = new JComboBox(cameras.toArray(new String[cameras.size()]));
		cameraList.addActionListener(actionListener);
		add(cameraList, BorderLayout.SOUTH);
	}

	public void setSliders(Set<Map.Entry<String, Integer>> sliderlist) {
		sliders = new HashMap<String, JSlider>();
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		for (Iterator iterator = sliderlist.iterator(); iterator.hasNext();) {
			Entry<String, Integer> entry = (Entry<String, Integer>) iterator.next();
			int maxTick = entry.getValue();
			JSlider slider = new JSlider();
			slider.setMinimum(0);
			slider.setMaximum(maxTick);
			slider.setMinorTickSpacing(maxTick > 50 ? 2 : 1);
			slider.setMajorTickSpacing(maxTick > 50 ? 10 : 5);
			Hashtable labels = new Hashtable();
			labels.put(0, new JLabel(entry.getKey()));
			slider.setLabelTable(labels);
			slider.setPaintLabels(true);
			slider.setEnabled(false);
			sliderPanel.add(slider);
			sliders.put(entry.getKey(), slider);

		}
	}

	public JSlider getSlider(String value) {
		return sliders.get(value);
	}

	public void setImage(BufferedImage bufferedImage) {
		this.image = bufferedImage;
		repaint();

	}

	public void setSensitivity(Integer sensitivity) {
		slider.setValue(sensitivity);
		repaint();
	}

	public Integer getSensitivity() {
		return slider.getValue();
	}

	public void drawRect(int x, int y, int width, int height) {
		this.xpos = x;
		this.ypos = y;
		this.width_ = width;
		this.height_ = height;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null)
			g.drawImage(image, 10, 10, image.getWidth(), image.getHeight(), this);
		g.setColor(new Color(0, 255, 0));
		g.drawRect(xpos, ypos, width_, height_);
	}

}
