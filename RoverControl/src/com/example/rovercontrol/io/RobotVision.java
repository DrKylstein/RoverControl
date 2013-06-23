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
	private File _path;
	private final String _imagesDir = "%s/RoverLog/%s/Forward/";
	private SimpleDateFormat _imageStamp;
	
	private String _simFolder = null;
	private String _simPath;
	private Mat[] _simulation = null;
	private int _currentSimFrame = 0;
	
	private void _init() {
	    Date today = Calendar.getInstance().getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss", Locale.US);
	    _imageStamp = new SimpleDateFormat("hh.mm.ss.SSS", Locale.US);
	    String folderName = formatter.format(today);
		
		_path = new File(String.format(_imagesDir, Environment.getExternalStorageDirectory(), folderName));
		_path.mkdirs();
		_openCVGood = true;
		if(_simFolder == null) {
			_videoCapture = new VideoCapture(0);
		} else {
			File path = new File(String.format(_imagesDir, Environment.getExternalStorageDirectory(), _simFolder));
			_simPath = path.toString();
			String[] fileList = path.list();
			Arrays.sort(fileList);
			_simulation = new Mat[fileList.length];
			for(int i = 0; i < fileList.length; i++) {
				_simulation[i] = Highgui.imread(_simPath+"/"+fileList[i]);
			}
		}
		_cameraFrame = new Mat(HEIGHT, WIDTH, CvType.CV_8UC4);
		_rawMat = new Mat(HEIGHT, WIDTH, CvType.CV_8UC4);
		_publishedFrame = new Mat(HEIGHT, WIDTH, CvType.CV_8UC4);
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
                	_init();
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
	 * Initialize OpenCV.
	 * @param context
	 * @param folder  where to grab simulated images from
	 */
	public void load(Context context, String folder) {
		_simFolder  = folder;
		
		load(context);
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
		if(_simulation != null) return true;
		if(_videoCapture == null) return false;
		return _videoCapture.isOpened();
	}
	/**
	 * Manually grab frame from camera
	 * @return OpenCv Matrix containing copy of the image
	 */
	public void grabFrame(Mat dest) {
		if(_simulation != null) {
			if(_simulation[_currentSimFrame] != null) {
				synchronized(_cameraFrame) {
					_simulation[_currentSimFrame].copyTo(_cameraFrame);
				}
				_currentSimFrame = (_currentSimFrame + 1) % _simulation.length;
				_cameraFrame.copyTo(dest);
			}
			return;
		}
		if(_videoCapture.grab()) {
			_videoCapture.retrieve(_rawMat);
			synchronized(_cameraFrame) {
				Core.flip(_rawMat.t(), _cameraFrame, 1);
				_cameraFrame.copyTo(dest);
			}
			_saveImage(_cameraFrame);
		}
	}
	/**
	 * make a processed frame available to viewers
	 * @param newMat the frame, which is taken as reference. Do not attempt to access it again directly.
	 */
	public void publishFrame(Mat newMat) {
		//synchronized(_previewMat) { //switched to volatile, I don't think null supports locks!
			newMat.copyTo(_publishedFrame);
			_framePublished = true;
		//}
	}
	/**
	 * Get a copy of the last published frame
	 * @return
	 */
	public void getPublishedFrame(Mat dest) {
		if(_publishedFrame != null) {
			synchronized(_publishedFrame) {
				_publishedFrame.copyTo(dest);
			}
		}
		_framePublished = false;
		return;
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
		Highgui.imwrite(new File(_path, String.format("%s.png",_imageStamp.format(new Date()))).toString(), mat);
	}
}
