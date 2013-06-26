package com.example.rovercontrol.mission;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

public class VisionTestState implements State<Robot> {

	private Mat frame;
	private Mat circles;
	private boolean _init = false;
	
	@Override
	public void onEnter(Robot robot) {
		//robot.vision.startCapture();
	}

	@Override
	public void onExit(Robot robot) {
		//robot.vision.stopCapture();
	}

	@Override
	public void update(long dtNanos, Robot robot) {
		if(!robot.vision.servicesAvailable()) return;
		if(!robot.vision.cameraAvailable()) return;
		
		if(!_init) {
			frame = new Mat(robot.vision.HEIGHT, robot.vision.WIDTH, CvType.CV_8UC4);
			circles = new Mat();
		}
		robot.vision.grabFrame(frame);
		
		assert frame != null : "VisionTestState: null frame!";
		
		/*Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(frame, frame, new Size(11,11), 5, 5);
		//Imgproc.threshold(frame, frame, 128, 255, Imgproc.THRESH_BINARY);
		
		
		
		Imgproc.HoughCircles(frame, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 
				frame.rows()/8, 200,28,10,150); //18 => 28
		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_GRAY2BGR);
		if(circles.cols() > 0) {
			for(int i = 0; i < circles.cols(); i++) {
				double circle[] = circles.get(0,i);
				if(circle == null) break;
				Point pt = new Point(Math.round(circle[0]), Math.round(circle[1]));
				int radius = (int)Math.round(circle[2]);
				Core.circle(frame, pt, radius, new Scalar(0,255,0));
				Core.circle(frame, pt, 3, new Scalar(0,0,255));
			}
		}*/
		
		robot.vision.publishFrame(frame);
		
	}

	@Override
	public String getName() {
		return "Seeing spots! (Vision Test)";
	}

}
