package org.poseidon.camera;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;
import org.poseidon.EventDetails;
import org.poseidon.IOControl;
import org.poseidon.IOListener;

public class Camera implements IOControl{
	private static final int ENABLE_CAMERA = 11;
	private static final int ENABLE_AVG_CAMERA = 12;
	private static final int TRACKED_OBJECTS_CAMERA = 13;
	private static final int SENSITIVITY = 14;
	public static final int RESOURCE_ID=67;
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private String video;
	public Camera(){
	}
	Camera(String video){
		this.video=video;
	}
	private enum Status{MOTION_SENSOR,DROWNING_SENSOR}
	private Status status=Status.DROWNING_SENSOR;
	//RawImage
	private CameraPanels panelCamera = new CameraPanels("Camera");
	private JFrame frameCamera = createFrame("Camera", panelCamera);
	//Avg Image
	private CameraPanels panelAvgCamera = new CameraPanels("Average Camera");
	private JFrame frameAvgCamera = createFrame("AverageImage", panelAvgCamera);
	//Tracked Objects
	private CameraPanels trackedObjectsCamera = new CameraPanels("Tracked Camera");
	private JFrame frameTrackedObjects = createFrame("Tracked Objects", trackedObjectsCamera);


	//max number of objects to be detected in frame
		private final int MAX_NUM_OBJECTS = 50;

		//minimum and maximum object area
		private final int MIN_OBJECT_AREA = 5 * 5;
       //Ignore the image border
		private double MIN_X_BORDER=10;
		private double MIN_Y_BORDER=10;

		private int sensitivity=100;

		private IOListener ioListener;
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
			throw new Exception("Could not connect to camera.");
		}
		// Captures one image, for starting the process.
				try{
					capture.read(image);

				} catch (Exception e){
					throw new Exception("Could not read from camera. Maybe the URL is not correct.");
				}

				setFramesSizes(image);

				if (capture.isOpened()) {
					int currenTrackedObjects=0;
					int previousTrackedObjects=0;

					while (true) {

						capture.read(image);
						writeImage(panelCamera,image);
						if (!image.empty()) {

							//pre-process

							Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
							Imgproc.GaussianBlur(grayImage,grayImage,new Size(21, 21), 0);

							Mat grayImageFloating=new Mat();
							grayImage.convertTo(grayImageFloating,CvType.CV_32F);
							if (absDiffImage.empty())
								absDiffImage = Mat.zeros(image.size(), CvType.CV_32F);
							if (avgImage.empty())
								avgImage = Mat.zeros(grayImage.size(), CvType.CV_32F);

							Core.absdiff(grayImageFloating, avgImage, absDiffImage);

							Mat inputFloating=new Mat();
							grayImage.convertTo(inputFloating, CvType.CV_32F);
							Imgproc.accumulateWeighted(inputFloating,avgImage, 0.001);
							writeImage(panelAvgCamera,avgImage);
							//process
							List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
							Mat temp = new Mat();
							Mat hierarchy = new Mat();
							grayImage.copyTo(temp);
							Mat canny=new Mat();
							Mat absInteger=new Mat();
							absDiffImage.convertTo(absInteger, CvType.CV_8UC1);
							Imgproc.Canny(absInteger, canny, 100, 200);


							Imgproc.findContours(canny, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
							if (contours.size() > 0) {
								int numObjects = contours.size();

								//large number of objects, we have a noisy filter
								if (numObjects < MAX_NUM_OBJECTS) {
									for (int i=0; i< contours.size(); i++){
										processDisplay(contours.get(i),image);
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
											if(ioListener!=null)
											ioListener.eventOccured(RESOURCE_ID, new SecurityCameraEvent(toBuffImage(image),toBuffImage(previousImage)));
										}
										previousTrackedObjects=currenTrackedObjects;
										currenTrackedObjects=0;
									}
								}
							}
							else
							{
								if(previousTrackedObjects>0)
								{
									if(ioListener!=null)
									ioListener.eventOccured(RESOURCE_ID, new SecurityCameraEvent(toBuffImage(image),toBuffImage(previousImage)));
								}
							}

						}

				}

	}
	}

	private void processDisplay(MatOfPoint matOfPoint, Mat image) {
		if(trackedObjectsCamera.isShowing())
		{
		Rect rect=Imgproc.boundingRect(matOfPoint);
		Imgproc.rectangle(image, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.height),new Scalar(0,255,0), 2);
		try {
			trackedObjectsCamera.setImage(toBuffImage(image));
		} catch (IOException e) {
			e.printStackTrace();
		}
		}

	}
	private void writeImage(CameraPanels panelCamera, Mat image) {
		if(true)
		{
			try {
				panelCamera.setImage(toBuffImage(image));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	private BufferedImage toBuffImage(Mat image) throws IOException {
		if(image.empty())return null;
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		return ImageIO.read(new ByteArrayInputStream(bytes));
	}
	private JFrame createFrame(String frameName, JPanel panel) {
		JFrame frame = new JFrame(frameName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		frame.setContentPane(panel);
		//frame.setVisible(true);
		return frame;
	}

	private void setFramesSizes(Mat image) {
		frameCamera.setSize(image.width() + 20, image.height() + 60);
		frameAvgCamera.setSize(image.width() + 20, image.height() + 60);
		frameTrackedObjects.setSize(image.width() + 20, image.height() + 60);




	}

	public static void main(String[] args) throws Exception {
		Camera tracker = new Camera();
		tracker.startTracking();
	}


	public String getStatus() {
		return status.name();
	}
	@Override
	public ReadResponse readValue(int id) {
		switch(id)
		{
		case ENABLE_CAMERA:return ReadResponse.success(RESOURCE_ID+id, frameCamera.isVisible());

		case ENABLE_AVG_CAMERA:return ReadResponse.success(RESOURCE_ID+id, frameAvgCamera.isVisible());

		case TRACKED_OBJECTS_CAMERA:return ReadResponse.success(RESOURCE_ID+id, frameTrackedObjects.isVisible());

		case SENSITIVITY:return ReadResponse.success(RESOURCE_ID+id, getSensitivity());
		}
		return null;
	}
	@Override
	public WriteResponse writeValue(int id,LwM2mResource value) {
		switch(id)
		{
		case ENABLE_CAMERA:frameCamera.setVisible(new Boolean(value.getValue().toString()));break;

		case ENABLE_AVG_CAMERA:frameAvgCamera.setVisible(new Boolean(value.getValue().toString()));break;

		case TRACKED_OBJECTS_CAMERA:frameTrackedObjects.setVisible(new Boolean(value.getValue().toString()));break;

		case SENSITIVITY:setSensitivity(new Integer(value.getValue().toString()));
		}
		return WriteResponse.success();
	}
	private void setSensitivity(int value) {
	sensitivity=value;
	}
	private int getSensitivity()
	{
		return sensitivity;
	}
	@Override
	public void eventReceived(int resourceId, EventDetails eventDetails) {
		// Not Interested in other events

	}
	@Override
	public void addIOListerner(IOListener iolistener) {
		this.ioListener=iolistener;

	}
	@Override
	public ExecuteResponse execute(int resourceid, String params) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void reset(int resourceid) {
		// TODO Auto-generated method stub

	}
}
