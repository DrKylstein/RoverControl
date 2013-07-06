/**
 * 
 */
package com.example.rovercontrol.io;

import java.util.Timer;
import java.util.TimerTask;

import com.example.rovercontrol.control.PID;

/**
 * @author kyle
 *
 */
public class RobotMotion{
	
	public MotorDriver driver;
	private PID _pid;
	private Timer _pidTimer;
	
	private double _actualRotationSpeed;
	private double _speed;
	private double _targetRotation;
	private double _lastPIDResult;
	private double _adjustedRotation;
	
	private RobotOrientation _orientation;
		
	private double _radps;
	
	private final double _INTERVAL = 0.01;
	
	private class PidTask_ extends TimerTask {
		@Override
		public void run() {
			_actualRotationSpeed = _orientation.getOrientation()[2] * -1;
			_lastPIDResult = _pid.update(_actualRotationSpeed, _INTERVAL);
			_adjustedRotation += _lastPIDResult;
			driver.setRotationSpeed(_adjustedRotation*_radps);
			driver.setSpeed(_speed);
		}

	}
	
	public void startPID() {
		_pidTimer = new Timer();
		_pidTimer.scheduleAtFixedRate(new PidTask_(), 0, (long) (_INTERVAL*1000));
	}
	
	public void stopPID() {
		if(_pidTimer != null) {
			_pidTimer.cancel();
		}
	}
	
	/**
	 * 
	 * @param driver_ MotorDriver that provides output
	 * @param orientation RobotOrientation that provides input for rotation control
	 * @param radps speed of rotation in radians/second when 1.0 is sent to MotorDriver
	 *
	 */
	public RobotMotion(MotorDriver driver_, RobotOrientation orientation, double radps) {
		driver = driver_;
		_radps = radps;
		
		_pid = new PID();
		_orientation = orientation;
	}

	/**
	 * Set PID constants and start/restart PID loop
	 * @param p Proportional gain
	 * @param i Integral gain
	 * @param d Derivative gain
	 */
	public void configurePID(double p, double i, double d) {
		stopPID();
		_pid.configure(p, i, d);
		_adjustedRotation = _targetRotation;
		_pid.reset();
		startPID();
	}
	
	/**
	 * 
	 * @param speed in proportion of maximum
	 */
	public void setSpeed(double speed) {
		_speed = speed; //driver_.setSpeed(mps);//driver_.setSpeed((mps / WHEEL_CIRC) * ONE_RPS); //mps speed in approximate meters/second
	}
	
	/**
	 * Will try to maintain this rotation speed with closed loop control.
	 * If the speed passed exceeds the physical limits, will turn at maximum speed instead.
	 * @param radps rotation speed in radians/second
	 */
	public void setRotationSpeed(double radps) {
		
		radps = Math.signum(radps) * Math.min(Math.abs(_radps), Math.abs(radps));
		
		_adjustedRotation = _targetRotation = radps;
		_pid.setTarget(_targetRotation);
	}
	
	/**
	 * Use this to check if desired rotation speed exceeds capability, if that would alter AI calculations.
	 * @return maximum absolute value of rotation speed in radians/second
	 */
	public double getMaxRotation() {
		return Math.abs(_radps);
	}
	
	/*
	 * Reporting functions
	 */
	public double getSpeed() {
		return _speed;
	}
	public double getTargetRotation() {
		return _targetRotation;
	}	
	public double getActualRotation() {
		return _actualRotationSpeed;
	}
	public double getLastPID() {
		return _lastPIDResult;
	}
}
