package com.example.rovercontrol.io;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class RobotVision {

	public final int WIDTH = 180;
	public final int HEIGHT = 320;

	private boolean _openCVGood;
	private VideoCapture _videoCapture;
	private Mat _rawMat; //holds initial results of video capture
	private Mat _cameraFrame; //holds video capture after pre-processing
	private volatile Mat _publishedFrame; //holds image result from ai
	private volatile boolean _framePublished;

	private volatile boolean _logEnabled;
	private File _logPath;
	private final String _logPathFormat = "%s/RoverLog/%s/Forward/";
	private SimpleDateFormat _imageStamp;

	private volatile boolean _simEnabled;
	private Mat[] _simFrames;
	private int _simPos;

	private final String tag = "RobotVision";

	/**
	 * Begins logging captured images to ExternalStorage:/RoverLog/yyyy.MM.dd.hh.mm.ss/Forward/hh.mm.ss.SSS.png
	 */
	public void startLogging() {
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss", Locale.US);
		_imageStamp = new SimpleDateFormat("hh.mm.ss.SSS", Locale.US);
		String folderName = formatter.format(today);
		_logPath = new File(String.format(_logPathFormat, Environment.getExternalStorageDirectory(), folderName));
		_logPath.mkdirs();
		_logEnabled = true;
		Log.v(tag, "Image logging started.");
	}

	/**
	 * Stops logging images.
	 */
	public void stopLogging() {
		_logEnabled = false;
		Log.v(tag, "Image logging stopped.");
	}

	/**
	 * Begins taking images from the specified path instead of the camera.
	 * @param path
	 */
	public synchronized void startSimulation(String path) {
		_simEnabled = false;
		File dir = new File(path);
		if(!dir.exists()) return;
		String[] fileList = dir.list();
		Arrays.sort(fileList);
		
		_simFrames = new Mat[fileList.length];
		for(int i = 0; i < fileList.length; i++) {
			_simFrames[i] = Highgui.imread(path+"/"+fileList[i]);
			if(_simFrames[i].width() == 0 || _simFrames[i].height() == 0) {
				Log.e(tag, "Bad simulation image: "+path+"/"+fileList[i]);
				return;
			}
		}
		_simPos = 0;
		_simEnabled = true;
		Log.v(tag, "Simulated image stream started.");
	}

	/**
	 * Resumes taking images from the live camera.
	 */
	public void stopSimulation() {
		_simEnabled = false;
		Log.v(tag, "Simulated image stream stopped.");
	}

	/**
	 * Because so many things depend on OpenCV being loaded, we do initializations here.
	 */
	private void _init() {
		_videoCapture = new VideoCapture(0);
		_cameraFrame = new Mat(HEIGHT, WIDTH, CvType.CV_8UC4);
		_rawMat = new Mat(HEIGHT, WIDTH, CvType.CV_8UC4);
		_publishedFrame = new Mat(HEIGHT, WIDTH, CvType.CV_8UC4);
		_openCVGood = true;
	}

	/**
	 * Bind to OpenCV library separately installed on the device.
	 * @param context
	 */
	public void load(Context context) {
		_openCVGood = false;
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, context, new BaseLoaderCallback(context) {
			@Override
			public void onManagerConnected(int status) {
				if(status == LoaderCallbackInterface.SUCCESS) {
					_init();
				}
			}
		});
	}
	/**
	 * Used to determine if OpenCV is available. 
	 * Always check before using any OpenCV classes.
	 * @return Whether OpenCV is loaded or not.
	 */
	public boolean servicesAvailable() {
		return _openCVGood;
	}
	/**
	 * Used to determine if the camera is available.
	 * Must check before grabbing frames.
	 * Remember to check servicesAvailable first!
	 * @return Whether the camera was successfully opened.
	 */
	public boolean cameraAvailable() {
		if(!_openCVGood) return false;
		if(_simFrames != null) return true;
		if(_videoCapture == null) return false;
		return _videoCapture.isOpened();
	}
	/**
	 * Manually grab frame from camera
	 * @return OpenCv Matrix containing copy of the image
	 */
	public void grabFrame(Mat dest) {
		if(_videoCapture.grab()) {
			_videoCapture.retrieve(_rawMat);
			synchronized(_cameraFrame) {
				Core.flip(_rawMat.t(), _cameraFrame, 1);
				if(!_simEnabled) {
					_cameraFrame.copyTo(dest);
				} else { //going through the motions to be as close to real as possible
					// actually it's because the program locked up when I tried taking shortcuts
					_simFrames[_simPos].copyTo(dest);
					_simPos = (_simPos + 1) % _simFrames.length;
				}
			}
			if(_logEnabled) {
				_saveImage(_cameraFrame);
			}
		}
	}
	
	/**
	 * 
	 */
	public void grabToLog() {
		if(_logEnabled && _videoCapture.grab()) {
			_videoCapture.retrieve(_rawMat);
			synchronized(_cameraFrame) {
				Core.flip(_rawMat.t(), _cameraFrame, 1);
			}
			_saveImage(_cameraFrame);
		}
	}
	
	/**
	 * make a processed frame available to viewers
	 * @param newMat the frame to copy the image from
	 */
	public void publishFrame(Mat newMat) {
		newMat.copyTo(_publishedFrame);
		_framePublished = true;
	}
	/**
	 * Get a copy of the last published frame
	 * @param dest A suitable matrix for copying the image to
	 */
	public void getPublishedFrame(Mat dest) {
		if(_publishedFrame != null) {
			synchronized(_publishedFrame) {
				_publishedFrame.copyTo(dest);
			}
		}
		_framePublished = false;
	}
	public boolean framePublished() {
		return _framePublished;
	}
	/**
	 * Call when service exits or the camera may be left on
	 */
	public void unload() {
		if(_openCVGood) {
			_videoCapture.release();
		}
	}

	private void _saveImage (Mat mat) {
		Highgui.imwrite(new File(_logPath, String.format("%s.png",_imageStamp.format(new Date()))).toString(), mat);
	}
}
