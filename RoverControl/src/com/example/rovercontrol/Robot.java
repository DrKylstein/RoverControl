package com.example.rovercontrol;

import com.example.rovercontrol.control.StateMachine;
import com.example.rovercontrol.io.GrabberPiston;
import com.example.rovercontrol.io.IRSensor;
import com.example.rovercontrol.io.MotorDriver;
import com.example.rovercontrol.io.RobotGPS;
import com.example.rovercontrol.io.RobotMotion;
import com.example.rovercontrol.io.RobotOrientation;
import com.example.rovercontrol.io.RobotVision;
import com.example.rovercontrol.io.UDPClient;

public class Robot {
	public RobotMotion motion;
	public IRSensor irSensor;
	public GrabberPiston grabber;
	public StateMachine<Robot> stateMachine;
	public RobotOrientation orientation;
	public UDPClient udpClient;
	public RobotVision vision;
	public RobotGPS gps;
	
	private long _lastNanoTime;
	private final int PISTON_PIN = 12;
	private final int IR_PIN = 40;	
	private final int _TX_PIN = 14;
	private final int UDP_PORT = 4444;
	private final String HOST_NAME = "192.168.43.190";

	
	public Robot() {
		udpClient = new UDPClient(UDP_PORT, HOST_NAME);
		irSensor = new IRSensor(IR_PIN);
		grabber = new GrabberPiston(PISTON_PIN);
		orientation = new RobotOrientation();
		motion = new RobotMotion(new MotorDriver(_TX_PIN), orientation);
		stateMachine = new StateMachine<Robot>(this);
		_looper = new _RobotLooper();
		vision = new RobotVision();
	}
	
	public void update() {
		long currentTime = System.nanoTime();
		stateMachine.update(currentTime - _lastNanoTime);
		_lastNanoTime = currentTime;
	}
	
	public void start() {
		new Thread(_looper).start();
	}
	public void stop() {
		_looper.stop = true;
		while(!_looper.stopped) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class _RobotLooper implements Runnable {
		volatile boolean stop;
		volatile boolean stopped;
		@Override
		public void run() {
			stop = false;
			stopped = false;
			while(!stop) {
				update();
			}
			stopped = true;
		}
	}
	_RobotLooper _looper;
}
