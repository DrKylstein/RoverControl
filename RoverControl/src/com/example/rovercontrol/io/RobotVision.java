package com.example.rovercontrol.io;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import android.content.Context;

public class RobotVision {
	
	private boolean _openCVGood;
	private VideoCapture _videoCapture;
	private Mat _rawMat; //holds initial results of video capture
	private Mat _cameraFrame; //holds video capture after pre-processing
	private volatile Mat _publishedFrame; //holds image result from ai
	
	
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
                	_cameraFrame = new Mat(640, 360, CvType.CV_8UC4);
                	_rawMat = new Mat(_cameraFrame.width(), _cameraFrame.height(), _cameraFrame.type());
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
	 * Must check before grabbing frames.
	 * Remember to check servicesAvailable first!
	 * @return Whether the camera was successfully opened.
	 */
	public boolean cameraAvailable() {
		//if(!_openCVGood) return false;
		if(_videoCapture == null) return false;
		return _videoCapture.isOpened();
	}
	/**
	 * Manually grab frame from camera
	 * @return OpenCv Matrix containing copy of the image
	 */
	public Mat grabFrame() {
		if(_videoCapture.grab()) {
			_videoCapture.retrieve(_rawMat);
			synchronized(_cameraFrame) {
				Core.flip(_rawMat.t(), _cameraFrame, 1);
				return _cameraFrame.clone();
			}
		}
		return null;
		//return currentFrame;
	}
	/**
	 * make a processed frame available to viewers
	 * @param newMat the frame, which is taken as reference. Do not attempt to access it again directly.
	 */
	public void publishFrame(Mat newMat) {
		//synchronized(_previewMat) { //switched to volatile, I don't think null supports locks!
			_publishedFrame = newMat;
		//}
	}
	/**
	 * Get a copy of the last published frame
	 * @return
	 */
	public Mat getPublishedFrame() {
		if(_publishedFrame != null) {
			synchronized(_publishedFrame) {
				return _publishedFrame.clone();
			}
		}
		return null;
	}	
	/**
	 * Call when service exits or the camera may be left on
	 */
	public void unload() {
		if(_openCVGood) {
			_videoCapture.release();
		}
	}
}
