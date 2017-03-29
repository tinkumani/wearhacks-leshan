package org.poseidon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.omg.CORBA.OMGVMCID;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Tracker {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	@Parameter(description = "localtion of the video file or ipcamaddress")
	private String video;

	@Parameter(description = "max number of results.")
	private int maxResults = 10;
	private JPanel panelCamera = new JPanel();
	private JFrame frameCamera = createFrame("Camera", panelCamera);
	private JFrame frameThreshold;
	//max number of objects to be detected in frame
		private final int MAX_NUM_OBJECTS = 50;
		
		//minimum and maximum object area
		private final int MIN_OBJECT_AREA = 40 * 40;
       //Ignore the image border
		private double MIN_X_BORDER=10;
		private double MIN_Y_BORDER=10;

		private SecurityCameraListener securityCameraListener;
	public void startTracking() throws Exception {

		Mat image = new Mat();
		Mat previousImage = new Mat();
		Mat avgImage = new Mat();
		Mat grayImage = new Mat();
		Mat absDiffImage = new Mat();
		VideoCapture capture = null;
		if (video == null) {
			capture = new VideoCapture(0);
		} else {
			capture = new VideoCapture(video);

		}
		if (capture == null){
			throw new Exception("Could not conect to camera.");
		}
		// Captures one image, for starting the process.
				try{
					capture.read(image);
				} catch (Exception e){
					throw new Exception("Could not read from camera. Maybe the URL is not correct.");
				}
				 
				setFramesSizes(image);
				
				if (capture.isOpened()) {
					
					while (true) {
						
						capture.read(image);
						
						if (!image.empty()) {
							
							//pre-process
							
							Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
							Imgproc.GaussianBlur(grayImage,grayImage,new Size(21, 21), 0);							
							if(avgImage.empty())
							{
								grayImage.copyTo(avgImage);
								continue;
							}
							Core.absdiff(grayImage, avgImage, absDiffImage);
							Imgproc.accumulateWeighted(grayImage,avgImage, 0.5);						
							
							//process
							List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
							Mat temp = new Mat();
							Mat hierarchy = new Mat();
							grayImage.copyTo(temp);
							Imgproc.findContours(absDiffImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
							if (contours.size() > 0) {
								int numObjects = contours.size();
						
								//large number of objects, we have a noisy filter
								if (numObjects < MAX_NUM_OBJECTS) {
									int currenTrackedObjects=0;
									int previousTrackedObjects=0;
									for (int i=0; i< contours.size(); i++){
										Moments moment = Imgproc.moments(contours.get(i));
										double area = moment.get_m00();
						
										//small objects,just noise										
										if (area > MIN_OBJECT_AREA) {
											Point centroid = new Point();
											centroid.x = moment.get_m10() / moment.get_m00();
											centroid.y = moment.get_m01() / moment.get_m00();
											if(centroid.x>MIN_X_BORDER && centroid.x<temp.size().width-MIN_X_BORDER &&
													centroid.y>MIN_Y_BORDER && centroid.y<temp.size().height-MIN_Y_BORDER	)
											{
												currenTrackedObjects++;
											}
											else//some is coming or leaving to the tracked area
											{
												
											}

										}
										if(previousTrackedObjects>currenTrackedObjects)
										{
											securityCameraListener.fireSecurityAlert(image,previousImage);
										}
										previousTrackedObjects=currenTrackedObjects;
										currenTrackedObjects=0;
									}
								}
							}
										
						}
						else{
					}
				}

	}
	}

	private JFrame createFrame(String frameName, JPanel panel) {
		JFrame frame = new JFrame(frameName); 	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setBounds(0, 0, frame.getWidth(), frame.getHeight());		
		frame.setContentPane(panel);
		frame.setVisible(true);
		return frame;
	}

	private void setFramesSizes(Mat image) {
		frameCamera.setSize(image.width() + 20, image.height() + 60);
		
		
		
	}

	public static void main(String[] args) throws Exception {
		Tracker tracker = new Tracker();
		new JCommander(tracker, args);
		tracker.startTracking();
	}

	public void addSecurityCameraListener(SecurityCameraListener mySecurityCamera) {
		this.securityCameraListener=securityCameraListener;
		
	}
}
