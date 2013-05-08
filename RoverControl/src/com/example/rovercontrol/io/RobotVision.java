package com.example.rovercontrol.io;

import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.core.Core;
import org.opencv.core.CvType;

import android.content.Context;

public class RobotVision {
	
	private final int DEFAULT_FPS = 5;
	private boolean _openCVGood;
	private VideoCapture _videoCapture;
	Mat _cameraMat;
	Mat _rawMat;
	Timer _timer;
	
	public RobotVision() {
		_timer = new Timer();
	}
	
	//receives callback when OpenCV is either loaded or failed to load
	private class _LoaderCallback extends BaseLoaderCallback {
        public _LoaderCallback(Context AppContext) {
			super(AppContext);
			// TODO Auto-generated constructor stub
		}

		@Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                	_openCVGood = true;
                	_videoCapture = new VideoCapture(0);
                	_cameraMat = new Mat(640, 360, CvType.CV_8UC4);
                	_rawMat = new Mat(_cameraMat.width(), _cameraMat.height(), _cameraMat.type());
                	break;
                default:
                	//System.out.printf("", status);
                    assert(false);
                    break;
            }
        }
    }
	private _LoaderCallback _loaderCallback;
    
	/**
	 * Initialize OpenCV.
	 * @param context
	 */
	
	public void load(Context context) {
		_loaderCallback = new _LoaderCallback(context);
		_openCVGood = false;
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, context, _loaderCallback);
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
	 * Must check before manually grabbing frames.
	 * Recommended to check before reading automatically grabbed frames.
	 * @return Whether the camera was successfully opened.
	 */
	public boolean cameraAvailable() {
		if(!_openCVGood) return false;
		if(_videoCapture == null) return false;
		return _videoCapture.isOpened();
	}
	/**
	 * Manually grab frame from camera
	 * @return OpenCv Matrix containing the image
	 */
	public Mat grabFrame() {
		if(_videoCapture.grab()) {
			_videoCapture.retrieve(_rawMat);
			
			Core.flip(_rawMat.t(), _cameraMat, 1);
		}
		return _cameraMat;
	}
	/**
	 * Get the last frame fetched either by grabFrame() or startCapture()
	 * @return OpenCv Matrix containing the image
	 */
	public Mat getLastFrame() {
		return _cameraMat;
	}
	
	private TimerTask _captureTask = new TimerTask() {
		@Override
		public void run() {
			if(servicesAvailable() && cameraAvailable()) {
				grabFrame();
			}
		}
	};
	/**
	 * Start automatically grabbing frames at the default rate
	 */
	public void startCapture() {
		startCapture(DEFAULT_FPS);
	}
	/**
	 * Start automatically grabbing frames at the specified rate.
	 * @param fps frames per second (not guaranteed)
	 */
	public void startCapture(long fps) {
		_timer.scheduleAtFixedRate(_captureTask, 0, 1000/fps);
	}
	/**
	 * Stop automatically capturing frames
	 */
	public void stopCapture() {
		_timer.cancel();
	}
}
