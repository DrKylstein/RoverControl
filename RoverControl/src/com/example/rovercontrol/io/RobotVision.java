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
    
	public void load(Context context) {
		_loaderCallback = new _LoaderCallback(context);
		_openCVGood = false;
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, context, _loaderCallback);
	}
	
	public boolean servicesAvailable() {
		return _openCVGood;
	}
	
	public boolean cameraAvailable() {
		if(!_openCVGood) return false;
		if(_videoCapture == null) return false;
		return _videoCapture.isOpened();
	}
	
	public Mat grabFrame() {
		if(_videoCapture.grab()) {
			_videoCapture.retrieve(_rawMat);
			
			Core.flip(_rawMat.t(), _cameraMat, 1);
		}
		return _cameraMat;
	}
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
	
	public void startCapture() {
		startCapture(DEFAULT_FPS);
	}
	public void startCapture(long fps) {
		_timer.scheduleAtFixedRate(_captureTask, 0, 1000/fps);
	}
	public void stopCapture() {
		_timer.cancel();
	}
}
