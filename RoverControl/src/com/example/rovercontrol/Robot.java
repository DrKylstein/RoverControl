package com.example.rovercontrol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

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
	private final String tag = "Robot";
	
	private volatile boolean _logEnabled = false;
	private final String _logPathFormat = "%s/RoverLog/%s/robot.txt";
	private SimpleDateFormat _stampFormat;
	private File _logFile;
	private BufferedWriter _logWriter;
	
	private final String _logFormat = 
			"[%s] \n" +
			"    State: %s\n"+
			"    Gyro: %.4f\n"+
			"    Compass: %.2f\n"+
			"    PID Target: %.4f\n"+
			"    PID Input: %.4f\n" +
			"    PID Correction %.4f\n";
	
	private long _lastLogTime = 0;
	
	private final long SEC = 1000000000;
	
	
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
		if(_logEnabled && currentTime - _lastLogTime > SEC/10) {
			try {
				_logWriter.write(String.format(_logFormat, 
						_stampFormat.format(new Date()), 
						stateMachine.getStateName(), 
						orientation.getOrientation()[2], 
						orientation.getCompass(), 
						motion.getTargetRotation(), 
						motion.getActualRotation(),
						motion.getLastPID()));
				_logWriter.newLine();
				_lastLogTime = System.nanoTime();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
		motion.setSpeed(0);
		motion.setRotationSpeed(0);
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
	
	/**
	 * Begins logging data to ExternalStorage:/RoverLog/yyyy.MM.dd.hh.mm.ss/robot.txt
	 */
	public void startLogging() {
		_logEnabled = false;
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss", Locale.US);
		_stampFormat = new SimpleDateFormat("hh.mm.ss.SSS", Locale.US);
		String folderName = formatter.format(today);
		_logFile = new File(String.format(_logPathFormat, Environment.getExternalStorageDirectory(), folderName));
		try {
			_logFile.getParentFile().mkdirs();
			_logFile.createNewFile();
			_logWriter = new BufferedWriter(new FileWriter(_logFile));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		assert _logWriter != null : "_logWriter not initialized!"; 
		_logEnabled = true;
		Log.v(tag, "Data logging started.");
		vision.startLogging();
	}

	/**
	 * Stops logging data.
	 */
	public void stopLogging() {
		if(_logEnabled) {
			_logEnabled = false;
			try {
				_logWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v(tag, "Data logging stopped.");
			vision.stopLogging();
		}
	}
}
