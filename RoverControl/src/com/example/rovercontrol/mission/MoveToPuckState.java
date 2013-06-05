package com.example.rovercontrol.mission;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

public class MoveToPuckState implements State<Robot> {

	@Override
	public void onEnter(Robot owner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExit(Robot owner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(long dtNanos, Robot robot) {
		if(!robot.vision.servicesAvailable()) return;
		if(!robot.vision.cameraAvailable()) return;
		
		Mat frame = robot.vision.grabFrame();
		Mat original = frame.clone();
		
		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(frame, frame, new Size(11,11), 5, 5);
		Imgproc.threshold(frame, frame, 16, 255, Imgproc.THRESH_BINARY_INV);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		
		Imgproc.findContours(frame, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		Moments mu[] = new Moments[contours.size()];
		Point mc[] = new Point[contours.size()];
		double radii[] = new double[contours.size()];
		for(int i = 0; i < contours.size(); i++) {
			mu[i] = Imgproc.moments(contours.get(i), false );
            mc[i] = new Point(mu[i].get_m10()/mu[i].get_m00(), mu[i].get_m01()/mu[i].get_m00());
            radii[i] = Math.sqrt(Imgproc.contourArea(contours.get(i))/Math.PI);//mu[i].get_mu20();
		}

		for( int i = 0; i < contours.size(); i++ )
        {
			if(radii[i] < 30) continue;
            Imgproc.drawContours(original, contours, i, new Scalar(255,0,0), 2);
            Core.circle(original, mc[i], 3, new Scalar(0,255,0));
            Core.circle(original, mc[i], (int) Math.round(radii[i]), new Scalar(0,0,255));
        }
		robot.vision.publishFrame(original);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Moving towards Puck";
	}

}
