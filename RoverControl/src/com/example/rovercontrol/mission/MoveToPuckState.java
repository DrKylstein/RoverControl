package com.example.rovercontrol.mission;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.util.Log;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

public class MoveToPuckState implements State<Robot> {

	private Mat puckThresh;
	private Mat original;
	private Mat tapeThresh;
	private Mat tapeHSV;
	private Mat dockThresh;
	private List<MatOfPoint> contours;
	private Mat hierarchy;
	private List<Moments> mu;
	private List<Point> mc;
	private final double deadZoneHigh = 0.1;
    private final double deadZoneLow = -0.1;
	private double error;
	private boolean _init = false;
	private MatOfPoint _puckContour;
	
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
	
		if(!_init) {
			contours = new ArrayList<MatOfPoint>();
			hierarchy = new Mat();
			mu = new ArrayList<Moments>();
			mc = new ArrayList<Point>();
			_init = true;
			puckThresh = new Mat(robot.vision.HEIGHT, robot.vision.WIDTH, CvType.CV_8UC4);
			tapeHSV = new Mat(robot.vision.HEIGHT, robot.vision.WIDTH, CvType.CV_8UC4);
			tapeThresh = new Mat(robot.vision.HEIGHT, robot.vision.WIDTH, CvType.CV_8U);
			original = new Mat(robot.vision.HEIGHT, robot.vision.WIDTH, CvType.CV_8UC4);
			_puckContour = new MatOfPoint();
			dockThresh = new Mat(robot.vision.HEIGHT, robot.vision.WIDTH, CvType.CV_8UC4);
		}
		
		robot.vision.grabFrame(original);
		//original.copyTo(puckThresh);
		//original.copyTo(tapeThresh);

		/*Imgproc.cvtColor(original, dockThresh, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(dockThresh, dockThresh, new Size(11,11), 5, 5);
		Imgproc.threshold(dockThresh, dockThresh, 127, 255, Imgproc.THRESH_BINARY);
*/
		
		Imgproc.cvtColor(original, puckThresh, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(puckThresh, puckThresh, new Size(11,11), 5, 5);
		Imgproc.threshold(puckThresh, puckThresh, 8, 255, Imgproc.THRESH_BINARY_INV);

		Imgproc.cvtColor(original, tapeHSV, Imgproc.COLOR_BGR2HSV);
		//Imgproc.GaussianBlur(tapeHSV, tapeHSV, new Size(11,11), 5, 5);
		//tape HSV estimate: 340deg, 88%, 88%
		Core.inRange(tapeHSV, new Scalar(170, 64, 64), new Scalar(179, 255, 255), tapeThresh);
		
		contours.clear();
		mu.clear();
		mc.clear();
		
		Imgproc.findContours(puckThresh, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		for(int i = 0; i < contours.size(); i++) {
			mu.add(Imgproc.moments(contours.get(i), false ));
            mc.add(new Point(mu.get(i).get_m10()/mu.get(i).get_m00(), mu.get(i).get_m01()/mu.get(i).get_m00()));
		}

		for(int i = 0; i < contours.size(); i++ )
        {
			if(Imgproc.contourArea(contours.get(i)) < 10) continue;
            Imgproc.drawContours(original, contours, i, new Scalar(255,0,0), 2);
            Core.circle(original, mc.get(i), 3, new Scalar(0,255,0));
            //targetIndex = i;
            break;
        }
		
		/*contours.clear();
		mu.clear();
		mc.clear();
		
		Imgproc.findContours(dockThresh, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		for(int i = 0; i < contours.size(); i++) {
			mu.add(Imgproc.moments(contours.get(i), false ));
            mc.add(new Point(mu.get(i).get_m10()/mu.get(i).get_m00(), mu.get(i).get_m01()/mu.get(i).get_m00()));
		}

		for(int i = 0; i < contours.size(); i++ )
        {
			if(Imgproc.contourArea(contours.get(i)) < 10) continue;
            Imgproc.drawContours(original, contours, i, new Scalar(0,0,255), 2);
            Core.circle(original, mc.get(i), 3, new Scalar(0,255,255));
            //targetIndex = i;
            break;
        }*/
		
		contours.clear();
		mu.clear();
		mc.clear();
		
		Imgproc.findContours(tapeThresh, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		for(int i = 0; i < contours.size(); i++) {
			mu.add(Imgproc.moments(contours.get(i), false ));
            mc.add(new Point(mu.get(i).get_m10()/mu.get(i).get_m00(), mu.get(i).get_m01()/mu.get(i).get_m00()));
		}

		for(int i = 0; i < contours.size(); i++ )
        {
			if(Imgproc.contourArea(contours.get(i)) < 10) continue;
            Imgproc.drawContours(original, contours, i, new Scalar(0,255,0), 2);
            //Core.circle(original, mc.get(i), 3, new Scalar(255, 255, 0));
        }
		
		robot.vision.publishFrame(original);
		
		/*if(targetIndex  < 0) {
			Log.i("MoveToPuck", "Can't see puck!");
			robot.motion.setRotationSpeed(0.0);
        	robot.motion.setSpeed(0.0);
			return;
		}
		
		error = (mc.get(targetIndex).x - (puckThresh.cols()/2)) / (puckThresh.cols()/2);
        
        if(error > deadZoneLow && error < deadZoneHigh) {
        	//roughly on target
        	robot.motion.setRotationSpeed(0.0);
        	robot.motion.setSpeed(0.0);//robot.motion.setSpeed(0.5);
        	return;
        }
        // turn towards target
        robot.motion.setRotationSpeed((error/Math.abs(error)) * 0.15);
        robot.motion.setSpeed(0.0);*/
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Moving towards Puck";
	}

}
