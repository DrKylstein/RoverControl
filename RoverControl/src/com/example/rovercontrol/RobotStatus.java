package com.example.rovercontrol;

import org.opencv.core.*;

public class RobotStatus {
	
	// Gyro
	private double rotationX;
	private double rotationY;
	private double rotationZ;
	
	// Puck Retrieval
	private double IRdistance;
	private String retrievalState;
	
	private double compassHeading;
	
	private double headingCorrection;
	private double speed;
	
	private Mat currentPicture;
	private byte[] command;
	
	// State in State Machine
	private String state;
	
	

}
