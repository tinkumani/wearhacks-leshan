package org.poseidon.camera;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.poseidon.EventDetails;
import org.poseidon.IOControl;
import org.poseidon.IOListener;
import org.poseidon.camera.SecurityCameraEvent.Event;

public class Camera implements IOControl, ActionListener, ChangeListener {
	private static final int ENABLE_CAMERA = 11;
	private static final int ACCUMULATED_IMAGE_CAM = 12;
	private static final int TRACKED_OBJECTS_CAMERA = 13;
	private static final int GRAY_CAM = 14;
	private static final int RECORD_VIDEO = 15;
	private static final int CLIP_DURATION = 16;
	private static final int BLUR_CAM = 17;
	private static final int DIFF_CAM = 18;
	private static final int EDGE_CAM = 19;
	private static final int CONTOUR_CAM = 20;
	private static final int SETTINGS = 21;
	private static final int TRACK_CAM = 22;

	public static final int RESOURCE_ID = 67;
	private AtomicBoolean isRecording = new AtomicBoolean(false);
	private Timer recordingTimer = null;
	private static VideoWriter videoWriter;
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		ImageIO.setUseCache(false);
	}

	private String video;

	public Camera() {

	}

	Camera(String video) {
		this.video = video;
	}

	private enum Status {
		MOTION_SENSOR, DROWNING_SENSOR
	}

	public enum Cameras {
		LIVE_CAM, GRAY_CAM, BLUR_CAM, DIFF_CAM, ACCUMULATED_IMAGE_CAM, EDGE_CAM, CONTOUR_CAM, SETTINGS, TRACK_CAM
	}

	private Status status = Status.DROWNING_SENSOR;
	// Cameras
	private CameraPanels panelCamera = new CameraPanels("Camera's",Cameras.values(), this, this);
	private JFrame frameCamera = createFrame("Camera", panelCamera);
	private IOListener ioListener;
	private int clipDuration = 10 * 1000;
	private AtomicReference<File> currentRecording = new AtomicReference<File>(null);
	private boolean previouslytracked;
	private int KsizeA = 21;
	private int KsizeB = 21;
	private int SigmaX = 0;
	private int Alpha = 1;
	private int Threshold1 = 100;
	private int Threshold2 = 200;
	private int MaxObjectsToTrack = 50;
	private int MinObjectArea = 15;
	private int MinX = 10;
	private int MinY = 10;

	private String currentCamera = "LIVE_CAM";

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
		if (capture == null) {
			throw new Exception("Could not connect to camera.");
		}
		// Captures one image, for starting the process.
		try {
			capture.read(image);

		} catch (Exception e) {
			throw new Exception("Could not read from camera. Maybe the URL is not correct.");
		}

		setFramesSizes(image);

		if (capture.isOpened()) {
			int currenTrackedObjects = 0;
			int previousTrackedObjects = 0;

			while (true) {

				capture.read(image);
				if (isRecording.get()) {
					VideoWriter videoWriter = getCurrentVideoWriter(
							new Size((int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH),
									(int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT)),
							capture.get(Videoio.CAP_PROP_FPS));
					videoWriter.write(image);
				}
				writeImage(Cameras.LIVE_CAM, image);
				if (!image.empty()) {

					// Pre-process Phase

					// Step 1. Convert to Gray Scale
					Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
					writeImage(Cameras.GRAY_CAM, grayImage);
					// Step 2.Blur
					Imgproc.GaussianBlur(grayImage, grayImage, new Size(Math.round((KsizeA-1)/2)*2+1, Math.round((KsizeB-1)/2)*2+1), SigmaX);
					writeImage(Cameras.BLUR_CAM, grayImage);
					
					//
					Mat thresholdImage= new Mat();
					Imgproc.threshold(grayImage, thresholdImage, 127, 255, 0);

					// Converting Formats
					Mat grayImageFloating = new Mat();
					thresholdImage.convertTo(grayImageFloating, CvType.CV_32F);
					if (absDiffImage.empty())
						absDiffImage = Mat.zeros(image.size(), CvType.CV_32F);
					if (avgImage.empty())
						avgImage = Mat.zeros(grayImage.size(), CvType.CV_32F);

					// Step 3. Diff from Avg Image
					Core.absdiff(grayImageFloating, avgImage, absDiffImage);
					//absDiffImage=grayImageFloating;
					writeImage(Cameras.DIFF_CAM, absDiffImage);

					// Converting Formats
					Mat inputFloating = new Mat();
					grayImage.convertTo(inputFloating, CvType.CV_32F);

					// Step 4. Add the current image to Avg Image
					Imgproc.accumulateWeighted(inputFloating, avgImage, Alpha / 100d);
					writeImage(Cameras.ACCUMULATED_IMAGE_CAM, avgImage);

					// Process Phase
					List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
					Mat temp = new Mat();
					Mat hierarchy = new Mat();
					grayImage.copyTo(temp);
					Mat canny = new Mat();
					Mat absInteger = new Mat();
					absDiffImage.convertTo(absInteger, CvType.CV_8UC1);
					// Step 5 Find Edge
					Imgproc.Canny(absInteger, canny, Threshold1, Threshold2,3,false);
					writeImage(Cameras.EDGE_CAM, canny);
					// Step 6 Find Contour
					Imgproc.findContours(canny, contours, hierarchy, Imgproc.RETR_EXTERNAL,
							Imgproc.CHAIN_APPROX_SIMPLE);
					writeImage(Cameras.CONTOUR_CAM, canny,hierarchy,contours);
					if (contours.size() > 0) {
						int numObjects = contours.size();

						// large number of objects, we have a noisy filter
						if (numObjects < MaxObjectsToTrack) {
							for (int i = 0; i < contours.size(); i++) {
								writeImage(Cameras.TRACK_CAM, processDisplay(contours.get(i), image));
								Moments moment = Imgproc.moments(contours.get(i));
								// Step 7. Calculate Area of each Contour
								double area = moment.get_m00();

								// small objects,just noise
								if (area > MinObjectArea) {
									Point centroid = new Point();
									centroid.x = moment.get_m10() / moment.get_m00();
									centroid.y = moment.get_m01() / moment.get_m00();
									if (centroid.x > MinX && centroid.x < temp.size().width - MinX && centroid.y > MinY
											&& centroid.y < temp.size().height - MinY) {
										currenTrackedObjects++;

									} else/*
											 * Assume that its someone leaving
											 * the area TODO Need to enhance to
											 * calculate the vector of the
											 * images and track
											 */
									{

									}

								}
								if (previousTrackedObjects > currenTrackedObjects) {
									if (ioListener != null)
										ioListener.eventOccured(RESOURCE_ID, new SecurityCameraEvent(toBuffImage(image),
												toBuffImage(previousImage), Event.PERSON_MISSING));
								}
								previousTrackedObjects = currenTrackedObjects;
								currenTrackedObjects = 0;
							}
						}
					} else {
						// System.out.println("missing
						// "+previousTrackedObjects);
						if (previousTrackedObjects > 0) {

							if (ioListener != null)
								ioListener.eventOccured(RESOURCE_ID, new SecurityCameraEvent(toBuffImage(image),
										toBuffImage(previousImage), Event.PERSON_MISSING));
						}
					}

				}

			}

		}
	}

	private void writeImage(Cameras cam, Mat image,Mat hierarchy, List<MatOfPoint> contours) {
		if (image != null && cam != null) {

			if (currentCamera == null || !currentCamera.equals(cam.name())) {
				return;
			}
		if(cam.equals(Cameras.CONTOUR_CAM))
		{
			Mat drawing = Mat.zeros( image.size(), CvType.CV_8UC3 );
			System.out.println("Contours-->"+contours.size());
			  for( int i = 0; i< contours.size(); i++ )
			     {
			      Random random=new Random(); 
				  Scalar color = new Scalar(random.nextInt()%255, random.nextInt()%255, random.nextInt()%255 );
			       Imgproc.drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, new Point() );
			     }
			  try {
				panelCamera.setImage(toBuffImage(drawing));
			} catch (IOException e) {
			}
		}
		}
		
	}
	private void writeImage(Cameras cam, Mat image) {

		if (image != null && cam != null) {

			if (currentCamera == null || !currentCamera.equals(cam.name())) {
				return;
			}
			
			try {
				panelCamera.setImage(toBuffImage(image));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private synchronized VideoWriter getCurrentVideoWriter(Size frameSize, double fps) throws IOException {
		String prefix = "recording";
		String suffix = ".avi";
		if (currentRecording.get() == null && isRecording.get()) {
			File outputFile = File.createTempFile(prefix, suffix);
			outputFile.deleteOnExit();
			currentRecording = new AtomicReference<File>(outputFile);
			videoWriter = new VideoWriter(currentRecording.get().getAbsolutePath(),
					VideoWriter.fourcc('D', 'I', 'V', 'X'), fps, frameSize, true);
		}

		return videoWriter;
	}

	private Mat processDisplay(MatOfPoint matOfPoint, Mat image) {
		Rect rect = Imgproc.boundingRect(matOfPoint);
		Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.height),
				new Scalar(0, 255, 0), 2);
		return image;

	}

	

	private BufferedImage toBuffImage(Mat image) throws IOException {
		if (image.empty())
			return null;
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		return ImageIO.read(new ByteArrayInputStream(bytes));
	}

	private JFrame createFrame(String frameName, JPanel panel) {
		final JFrame  frame = new JFrame(frameName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		frame.setContentPane(panel);
		
		
		 SwingUtilities.invokeLater(new Runnable() {

	            @Override
	            public void run() {
	            	frame.setVisible(true);
	            }
	        });
		 return frame;
		 
		
	}

	private void setFramesSizes(Mat image) {
		frameCamera.setSize(image.width() + 20, image.height() + 60);
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
		switch (id) {
		case ENABLE_CAMERA:
			isCurrentCamera(Cameras.LIVE_CAM.name());
			break;

		case ACCUMULATED_IMAGE_CAM:
			return ReadResponse.success(RESOURCE_ID + id,isCurrentCamera(Cameras.ACCUMULATED_IMAGE_CAM.name()));
		case TRACKED_OBJECTS_CAMERA:
			return ReadResponse.success(RESOURCE_ID + id,isCurrentCamera(Cameras.TRACK_CAM.name()));
		case GRAY_CAM:
			return ReadResponse.success(RESOURCE_ID + id,isCurrentCamera(Cameras.GRAY_CAM.name()));
		case BLUR_CAM:
			return ReadResponse.success(RESOURCE_ID + id,isCurrentCamera(Cameras.BLUR_CAM.name()));
		case DIFF_CAM:
			return ReadResponse.success(RESOURCE_ID + id,isCurrentCamera(Cameras.DIFF_CAM.name()));
		case EDGE_CAM:
			return ReadResponse.success(RESOURCE_ID + id,isCurrentCamera(Cameras.EDGE_CAM.name()));
		case CONTOUR_CAM:
			return ReadResponse.success(RESOURCE_ID + id,isCurrentCamera(Cameras.CONTOUR_CAM.name()));
		case SETTINGS:
			return ReadResponse.success(RESOURCE_ID + id ,isCurrentCamera(Cameras.SETTINGS.name()));
		case TRACK_CAM:
			return ReadResponse.success(RESOURCE_ID + id,isCurrentCamera(Cameras.TRACK_CAM.name()));
		case RECORD_VIDEO:
			return ReadResponse.success(RESOURCE_ID + id, isRecording.get());

		case CLIP_DURATION:
			return ReadResponse.success(RESOURCE_ID + id, this.clipDuration);
		}
		return null;
	}

	@Override
	public WriteResponse writeValue(int id, LwM2mResource value) {
		switch (id) {
		case ENABLE_CAMERA:
			setCurrentCamera(Cameras.LIVE_CAM.name());
			break;

		case ACCUMULATED_IMAGE_CAM:
			setCurrentCamera(Cameras.ACCUMULATED_IMAGE_CAM.name());
			break;

		case TRACKED_OBJECTS_CAMERA:
			setCurrentCamera(Cameras.TRACK_CAM.name());
			break;

		case GRAY_CAM:
			setCurrentCamera(Cameras.GRAY_CAM.name());
			break;
			
		case BLUR_CAM:
			setCurrentCamera(Cameras.BLUR_CAM.name());
			break;
		case DIFF_CAM:
			setCurrentCamera(Cameras.DIFF_CAM.name());
			break;
		case EDGE_CAM:
			setCurrentCamera(Cameras.EDGE_CAM.name());
			break;
		case CONTOUR_CAM:
			setCurrentCamera(Cameras.CONTOUR_CAM.name());
			break;
		case SETTINGS:
			setCurrentCamera(Cameras.SETTINGS.name());
			break;
		case TRACK_CAM:
			setCurrentCamera(Cameras.TRACK_CAM.name());
			break;	
		case RECORD_VIDEO:
			recordClip(new Boolean(value.getValue().toString()));
			break;

		case CLIP_DURATION:
			setClipDuration(new Integer(value.getValue().toString()));
		}
		return WriteResponse.success();
	}

	@Override
	public ExecuteResponse execute(int id, String value) {
		switch (id) {

		case ENABLE_CAMERA:
			setCurrentCamera(Cameras.LIVE_CAM.name());
			break;

		case ACCUMULATED_IMAGE_CAM:
			setCurrentCamera(Cameras.ACCUMULATED_IMAGE_CAM.name());
			break;

		case TRACKED_OBJECTS_CAMERA:
			setCurrentCamera(Cameras.TRACK_CAM.name());
			break;

		case GRAY_CAM:
			setCurrentCamera(Cameras.GRAY_CAM.name());
			break;

		case BLUR_CAM:
			setCurrentCamera(Cameras.BLUR_CAM.name());
			break;
		case DIFF_CAM:
			setCurrentCamera(Cameras.DIFF_CAM.name());
			break;
		case EDGE_CAM:
			setCurrentCamera(Cameras.EDGE_CAM.name());
			break;
		case CONTOUR_CAM:
			setCurrentCamera(Cameras.CONTOUR_CAM.name());
			break;
		case SETTINGS:
			setCurrentCamera(Cameras.SETTINGS.name());
			break;
		case TRACK_CAM:
			setCurrentCamera(Cameras.TRACK_CAM.name());
			break;

		case RECORD_VIDEO:
			recordClip(new Boolean(value));
			break;

		case CLIP_DURATION:
			setClipDuration(new Integer(value));
		}
		return ExecuteResponse.success();
	}

	private void setCurrentCamera(String name) {
		currentCamera = name;
		panelCamera.getCameraList().setSelectedItem(name);

	}
	private boolean isCurrentCamera(String name) {
		return (currentCamera == name);		

	}

	private void setClipDuration(Integer duration) {

		if (duration > 30)
			this.clipDuration = duration * 1000;
	}

	private synchronized void recordClip(boolean start) {
		if (start) {
			if (!isRecording.get()) {
				isRecording.set(true);
				recordingTimer = new Timer();
				recordingTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						recordClip(false);

					}
				}, clipDuration);

			}
		} else {
			ioListener.eventOccured(RESOURCE_ID,
					new SecurityCameraEvent(getCurrentRecording(), SecurityCameraEvent.Event.VIDEO_CLIP));
			isRecording.set(false);
			currentRecording.set(null);
		}

	}

	private File getCurrentRecording() {
		return currentRecording.get();
	}

	@Override
	public void eventReceived(int resourceId, EventDetails eventDetails) {
		// Not Interested in other events

	}

	@Override
	public void addIOListerner(IOListener iolistener) {
		this.ioListener = iolistener;

	}

	@Override
	public void reset(int resourceid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox combo = (JComboBox) e.getSource();
		String currentPanel = ((Cameras) combo.getSelectedItem()).toString();
		Map<String, Map.Entry<Integer, Integer>> sliderlist = new HashMap<String, Map.Entry<Integer, Integer>>();
		currentCamera = currentPanel;
		switch (currentPanel) {
		case "BLUR_CAM":
			sliderlist.put("KsizeA", new AbstractMap.SimpleEntry<Integer,Integer>(100,KsizeA));
			sliderlist.put("KsizeB", new AbstractMap.SimpleEntry<Integer,Integer>(100,KsizeB));
			sliderlist.put("SigmaX", new AbstractMap.SimpleEntry<Integer,Integer>(100,SigmaX));
			break;
		case "ACCUMULATED_IMAGE_CAM":
			sliderlist.put("Alpha", new AbstractMap.SimpleEntry<Integer,Integer>(1,Alpha));
			break;
		case "EDGE_CAM":
			sliderlist.put("Threshold1", new AbstractMap.SimpleEntry<Integer,Integer>(200,Threshold1));
			sliderlist.put("Threshold2", new AbstractMap.SimpleEntry<Integer,Integer>(400,Threshold2));
			break;
		case "SETTINGS":
			sliderlist.put("MaxObjectsToTrack", new AbstractMap.SimpleEntry<Integer,Integer>(50,MaxObjectsToTrack));
			sliderlist.put("MinObjectArea", new AbstractMap.SimpleEntry<Integer,Integer>(15,MinObjectArea));
			sliderlist.put("MinX", new AbstractMap.SimpleEntry<Integer,Integer>(15,MinX));
			sliderlist.put("MinY", new AbstractMap.SimpleEntry<Integer,Integer>(15,MinY));
			break;
		default:
			break;
		}
		panelCamera.setSliders(sliderlist.entrySet());

	}

	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		String name = source.getName();
		int val = source.getValue();
		switch (name) {
		case "KsizeA":
			KsizeA = val;
			break;
		case "KsizeB":
			KsizeB = val;
			break;
		case "SigmaX":
			SigmaX = val;
			break;
		case "Alpha":
			Alpha = val;
			break;
		case "Threshold1":
			Threshold1 = val;
			break;
		case "Threshold2":
			Threshold2 = val;
			break;
		case "MaxObjectsToTrack":
			MaxObjectsToTrack = val;
			break;
		case "MinObjectArea":
			MinObjectArea = val;
			break;
		case "MinX":
			MinX = val;
			break;
		case "MinY":
			MinY = val;
			break;

		default:
			break;
		}

	}
}
