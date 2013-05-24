package com.example.rovercontrol.mission;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

public class VisionTestState implements State<Robot> {

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
		
		
		Mat frame = robot.vision.grabFrame();
		
		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(frame, frame, new Size(7,7), 1.5, 1.5);
		
		Mat circles = new Mat();
		
		Imgproc.HoughCircles(frame, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 
				frame.rows()/8, 120,30,10,70);
		if(circles.cols() > 0) {
			for(int i = 0; i < circles.cols(); i++) {
				double circle[] = circles.get(0,i);
				if(circle == null) break;
				Point pt = new Point(Math.round(circle[0]), Math.round(circle[1]));
				int radius = (int)Math.round(circle[2]);
				Core.circle(frame, pt, radius, new Scalar(0,255,0));
				Core.circle(frame, pt, 3, new Scalar(0,0,255));
			}
		}
		
		robot.vision.publishFrame(frame);
		
	}

	@Override
	public String getName() {
		return "Vision Test";
	}

}
